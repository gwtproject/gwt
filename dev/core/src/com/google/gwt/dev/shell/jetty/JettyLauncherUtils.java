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
package com.google.gwt.dev.shell.jetty;

import com.google.gwt.core.ext.TreeLogger;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * Shared static methods for both JettyLauncher and StaticResourceServer, useful for
 * any Jetty-based implementation of a ServletContainerLauncher.
 */
public class JettyLauncherUtils {

  private JettyLauncherUtils() {
    // non-instantiable
  }

  /**
   * Setup a connector for the bind address/port.
   *
   * @param connector
   * @param bindAddress
   * @param port
   */
  public static void setupConnector(ServerConnector connector,
                                    String bindAddress, int port) {
    if (bindAddress != null) {
      connector.setHost(bindAddress);
    }
    connector.setPort(port);

    // Allow binding to a port even if it's still in state TIME_WAIT.
    connector.setReuseAddress(true);
  }

  public static ServerConnector getConnector(Server server, SslConfiguration sslConfig, TreeLogger logger) {
    HttpConfiguration config = defaultConfig();
    if (sslConfig.isUseSsl()) {
      TreeLogger sslLogger = logger.branch(TreeLogger.INFO,
          "Listening for SSL connections");
      if (sslLogger.isLoggable(TreeLogger.TRACE)) {
        sslLogger.log(TreeLogger.TRACE, "Using keystore " + sslConfig.getKeyStore());
      }
      SslContextFactory ssl = new SslContextFactory();
      if (sslConfig.getClientAuth() != null) {
        switch (sslConfig.getClientAuth()) {
          case NONE:
            ssl.setWantClientAuth(false);
            ssl.setNeedClientAuth(false);
            break;
          case WANT:
            sslLogger.log(TreeLogger.TRACE, "Requesting client certificates");
            ssl.setWantClientAuth(true);
            ssl.setNeedClientAuth(false);
            break;
          case REQUIRE:
            sslLogger.log(TreeLogger.TRACE, "Requiring client certificates");
            ssl.setWantClientAuth(true);
            ssl.setNeedClientAuth(true);
            break;
        }
      }
      ssl.setKeyStorePath(sslConfig.getKeyStore());
      ssl.setTrustStorePath(sslConfig.getKeyStore());
      ssl.setKeyStorePassword(sslConfig.getKeyStorePassword());
      ssl.setTrustStorePassword(sslConfig.getKeyStorePassword());
      config.addCustomizer(new SecureRequestCustomizer());
      return new ServerConnector(server,
          null, null, null, 0, 2,
          new SslConnectionFactory(ssl, "http/1.1"),
          new HttpConnectionFactory(config));
    }
    return new ServerConnector(server, new HttpConnectionFactory(config));
  }

  private static HttpConfiguration defaultConfig() {
     HttpConfiguration config = new HttpConfiguration();
     config.setRequestHeaderSize(16386);
     config.setSendServerVersion(false);
     config.setSendDateHeader(true);
     return config;
  }
}
