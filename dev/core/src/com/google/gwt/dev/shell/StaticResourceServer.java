/*
 * Copyright 2023 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev.shell;

import com.google.gwt.core.ext.ServletContainer;
import com.google.gwt.core.ext.ServletContainerLauncher;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.shell.jetty.ClientAuthType;
import com.google.gwt.dev.shell.jetty.JettyLauncherUtils;
import com.google.gwt.dev.shell.jetty.JettyTreeLogger;
import com.google.gwt.dev.shell.jetty.SslConfiguration;

import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.util.Optional;

/**
 * Simple webserver that only hosts static resources. Intended to be the replacement
 * for JettyLauncher.
 */
public class StaticResourceServer extends ServletContainerLauncher {

  private String bindAddress = null;

  private SslConfiguration sslConfig = new SslConfiguration(ClientAuthType.NONE, null, null, false);

  @Override
  public String getName() {
    return "StaticResourceServer";
  }

  @Override
  public void setBindAddress(String bindAddress) {
    this.bindAddress = bindAddress;
  }

  @Override
  public boolean isSecure() {
    return sslConfig.isUseSsl();
  }

  private void checkStartParams(TreeLogger logger, int port, File appRootDir) {
    if (logger == null) {
      throw new NullPointerException("logger cannot be null");
    }

    if (port < 0 || port > 65535) {
      throw new IllegalArgumentException(
              "port must be either 0 (for auto) or less than 65536");
    }

    if (appRootDir == null) {
      throw new NullPointerException("app root directory cannot be null");
    }
  }

  @Override
  public boolean processArguments(TreeLogger logger, String arguments) {
    if (arguments != null && arguments.length() > 0) {
      Optional<SslConfiguration> parsed = SslConfiguration.parseArgs(arguments.split(","), logger);
      if (parsed.isPresent()) {
        sslConfig = parsed.get();
      } else {
        return false;
      }
    }
    return true;
  }


  @Override
  public ServletContainer start(TreeLogger logger, int port, File appRootDir) throws Exception {
    TreeLogger branch = logger.branch(TreeLogger.TRACE,
            "Starting StaticResourceServer on port " + port, null);

    checkStartParams(branch, port, appRootDir);

    // During startup, use the branch logger, we'll reset this later to the root logger
    Log.setLog(new JettyTreeLogger(branch));

    Server server = new Server();

    ServerConnector connector = JettyLauncherUtils.getConnector(server, sslConfig, branch);
    JettyLauncherUtils.setupConnector(connector, bindAddress, port);
    server.addConnector(connector);

    WebAppContext webAppContext = new WebAppContext(null, "/", null, null, null,
            new ErrorPageErrorHandler(), ServletContextHandler.NO_SECURITY);
    webAppContext.setWar(appRootDir.getAbsolutePath());
    webAppContext.setSecurityHandler(new ConstraintSecurityHandler());

    server.setHandler(webAppContext);

    server.start();
    server.setStopAtShutdown(true);

    // Now that we're started, log to the top level logger.
    Log.setLog(new JettyTreeLogger(logger));

    // DevMode#doStartUpServer() fails from time to time (rarely) due
    // to an unknown error. Adding some logging to pinpoint the problem.
    int connectorPort = connector.getLocalPort();
    if (connector.getLocalPort() < 0) {
      branch.log(TreeLogger.ERROR, String.format(
              "Failed to connect to open channel with port %d (return value %d)",
              port, connectorPort));
    }

    return new StaticServerImpl(connectorPort, appRootDir, branch, server);
  }

  private static final class StaticServerImpl extends ServletContainer {
    private final int port;
    private final File appRootDir;
    private final TreeLogger logger;
    private final Server server;

    private StaticServerImpl(int port, File appRootDir, TreeLogger logger, Server server) {
      this.port = port;
      this.appRootDir = appRootDir;
      this.logger = logger;
      this.server = server;
    }

    @Override
    public int getPort() {
      return port;
    }

    @Override
    public void refresh() throws UnableToCompleteException {
      String msg = "Reloading web app to reflect changes in "
              + appRootDir.getAbsolutePath();
      TreeLogger branch = logger.branch(TreeLogger.INFO, msg);
      // Temporarily log Jetty on the branch.
      Log.setLog(new JettyTreeLogger(branch));
      try {
        server.stop();
        server.start();
        branch.log(TreeLogger.INFO, "Reload completed successfully");
      } catch (Exception e) {
        branch.log(TreeLogger.ERROR, "Unable to restart StaticResourceServer server",
                e);
        throw new UnableToCompleteException();
      } finally {
        // Reset the top-level logger.
        Log.setLog(new JettyTreeLogger(logger));
      }
    }

    @Override
    public void stop() throws UnableToCompleteException {
      TreeLogger branch = logger.branch(TreeLogger.INFO,
              "Stopping StaticResourceServer server");
      // Temporarily log Jetty on the branch.
      Log.setLog(new JettyTreeLogger(branch));
      try {
        server.stop();
        server.setStopAtShutdown(false);
        branch.log(TreeLogger.TRACE, "Stopped successfully");
      } catch (Exception e) {
        branch.log(TreeLogger.ERROR, "Unable to stop embedded StaticResourceServer server", e);
        throw new UnableToCompleteException();
      } finally {
        // Reset the top-level logger.
        Log.setLog(new JettyTreeLogger(logger));
      }
    }
  }
}
