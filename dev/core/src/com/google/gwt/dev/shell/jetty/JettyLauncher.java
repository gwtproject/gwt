/*
 * Copyright 2008 Google Inc.
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

import com.google.gwt.core.ext.ServletContainer;
import com.google.gwt.core.ext.ServletContainerLauncher;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.util.InstalledHelpInfo;
import com.google.gwt.thirdparty.guava.common.collect.Iterators;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.preventers.AppContextLeakPreventer;
import org.eclipse.jetty.util.preventers.DOMLeakPreventer;
import org.eclipse.jetty.util.preventers.GCThreadLeakPreventer;
import org.eclipse.jetty.util.preventers.SecurityProviderLeakPreventer;
import org.eclipse.jetty.webapp.ClasspathPattern;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * A {@link ServletContainerLauncher} for an embedded Jetty server.
 * <p></p>
 * @deprecated please migrate to {@link com.google.gwt.dev.shell.StaticResourceServer},
 * or provide your own implementation.
 */
@Deprecated
public class JettyLauncher extends ServletContainerLauncher {
  private static final AtomicBoolean hasLoggedDeprecationWarning = new AtomicBoolean(false);

  /**
   * Disable warning about JettyLauncher being deprecated when running tests, as this only is
   * intended to apply to DevMode - The test runner can be updated at the same time as any other
   * changes are made to JettyLauncher.
   */
  public static void suppressDeprecationWarningForTests() {
    hasLoggedDeprecationWarning.set(true);
  }

  /**
   * Warns, only once, that JettyLauncher is deprecated for removal. Call when it is clear that
   * the developer is actively deploying code to, or loading resources from, the embedded Jetty
   * instance.
   *
   * @param log existing TreeLogger to append warning to
   */
  private static void maybeLogDeprecationWarning(TreeLogger log) {
    if (hasLoggedDeprecationWarning.compareAndSet(false, true)) {
      log.log(TreeLogger.Type.WARN, "DevMode will default to -noserver in a future release, and " +
              "JettyLauncher may be removed or changed. Please consider running your own " +
              "application server and either passing -noserver to DevMode or migrating to " +
              "CodeServer. Alternatively, consider implementing your own " +
              "ServletContainerLauncher to continue running your application server from " +
              "DevMode.");
    }
  }

  /**
   * Log jetty requests/responses to TreeLogger.
   */
  public static class JettyRequestLogger extends AbstractLifeCycle implements
      RequestLog {

    private final TreeLogger logger;
    private final TreeLogger.Type normalLogLevel;

    public JettyRequestLogger(TreeLogger logger, TreeLogger.Type normalLogLevel) {
      this.logger = logger;
      assert (normalLogLevel != null);
      this.normalLogLevel = normalLogLevel;
    }

    /**
     * Log an HTTP request/response to TreeLogger.
     */
    public void log(Request request, Response response) {
      int status = response.getStatus();
      if (status < 0) {
        // Copied from NCSARequestLog
        status = 404;
      }
      if (status != 404) {
        // Ignore 404 errors, log the first other call to the server if we haven't logged yet
        maybeLogDeprecationWarning(logger);
      }
      TreeLogger.Type logStatus, logHeaders;
      if (status >= 500) {
        logStatus = TreeLogger.ERROR;
        logHeaders = TreeLogger.INFO;
      } else if (status == 404) {
        if ("/favicon.ico".equals(request.getRequestURI())
            && request.getQueryString() == null) {
          /*
           * We do not want to call the developer's attention to a 404 when
           * requesting favicon.ico. This is a very common 404.
           */
          logStatus = TreeLogger.TRACE;
          logHeaders = TreeLogger.DEBUG;
        } else {
          logStatus = TreeLogger.WARN;
          logHeaders = TreeLogger.INFO;
        }
      } else if (status >= 400) {
        logStatus = TreeLogger.WARN;
        logHeaders = TreeLogger.INFO;
      } else {
        logStatus = normalLogLevel;
        logHeaders = TreeLogger.DEBUG;
      }

      String userString = request.getRemoteUser();
      if (userString == null) {
        userString = "";
      } else {
        userString += "@";
      }
      String bytesString = "";
      if (response.getContentCount() > 0) {
        bytesString = " " + response.getContentCount() + " bytes";
      }
      if (logger.isLoggable(logStatus)) {
        TreeLogger branch = logger.branch(logStatus, String.valueOf(status)
            + " - " + request.getMethod() + ' ' + request.getRequestURI() + " ("
            + userString + request.getRemoteHost() + ')' + bytesString);
        if (branch.isLoggable(logHeaders)) {
          logHeaders(branch.branch(logHeaders, "Request headers"), logHeaders,
              request.getHttpFields());
          logHeaders(branch.branch(logHeaders, "Response headers"), logHeaders,
              response.getHttpFields());
        }
      }
    }

    private void logHeaders(TreeLogger logger, TreeLogger.Type logLevel, HttpFields fields) {
      for (int i = 0; i < fields.size(); ++i) {
        HttpField field = fields.getField(i);
        logger.log(logLevel, field.getName() + ": " + field.getValue());
      }
    }
  }

  /**
   * An adapter for the Jetty logging system to GWT's TreeLogger.
   * @deprecated use {@link com.google.gwt.dev.shell.jetty.JettyTreeLogger} instead.
   */
  @Deprecated
  public static class JettyTreeLogger extends com.google.gwt.dev.shell.jetty.JettyTreeLogger {
    public JettyTreeLogger(TreeLogger logger) {
      super(logger);
    }
  }
  /**
   * The resulting {@link ServletContainer} this is launched.
   */
  protected static class JettyServletContainer extends ServletContainer {
    private final int actualPort;
    private final File appRootDir;
    private final TreeLogger logger;
    private final Server server;
    private final WebAppContext wac;

    public JettyServletContainer(TreeLogger logger, Server server,
        WebAppContext wac, int actualPort, File appRootDir) {
      this.logger = logger;
      this.server = server;
      this.wac = wac;
      this.actualPort = actualPort;
      this.appRootDir = appRootDir;
    }

    @Override
    public int getPort() {
      return actualPort;
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
        branch.log(TreeLogger.ERROR, "Unable to restart embedded Jetty server",
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
          "Stopping Jetty server");
      // Temporarily log Jetty on the branch.
      Log.setLog(new JettyTreeLogger(branch));
      try {
        server.stop();
        server.setStopAtShutdown(false);
        branch.log(TreeLogger.TRACE, "Stopped successfully");
      } catch (Exception e) {
        branch.log(TreeLogger.ERROR, "Unable to stop embedded Jetty server", e);
        throw new UnableToCompleteException();
      } finally {
        // Reset the top-level logger.
        Log.setLog(new JettyTreeLogger(logger));
      }
    }
  }

  /**
   * A {@link WebAppContext} tailored to GWT hosted mode. Features hot-reload
   * with a new {@link WebAppClassLoader} to pick up disk changes. The default
   * Jetty {@code WebAppContext} will create new instances of servlets, but it
   * will not create a brand new {@link ClassLoader}. By creating a new {@code
   * ClassLoader} each time, we re-read updated classes from disk.
   *
   * Also provides special class filtering to isolate the web app from the GWT
   * hosting environment.
   */
  protected static final class WebAppContextWithReload extends WebAppContext {

    /**
     * Specialized {@link WebAppClassLoader} that allows outside resources to be
     * brought in dynamically from the system path. A warning is issued when
     * this occurs.
     */
    private class WebAppClassLoaderExtension extends WebAppClassLoader {

      private static final String META_INF_SERVICES = "META-INF/services/";

      private final ClasspathPattern systemClassesFromWebappFirst = new ClasspathPattern(new String[] {
          "-javax.servlet.",
          "-javax.el.",
          "-javax.websocket.",
          "javax.",
      });
      private final ClasspathPattern allowedFromSystemClassLoader = new ClasspathPattern(new String[] {
          "org.eclipse.jetty.",
          "javax.websocket.",
          // Jasper
          "org.apache.jasper.",
          "org.apache.juli.logging.",
          "org.apache.tomcat.",
          "org.apache.el.",
          // Xerces
          "org.apache.xerces.",
          "javax.xml.", // Used by Jetty for jetty-web.xml parsing
      });

      public WebAppClassLoaderExtension() throws IOException {
        super(bootStrapOnlyClassLoader, WebAppContextWithReload.this);
      }

      @Override
      public Enumeration<URL> getResources(String name) throws IOException {
        // Logic copied from Jetty's WebAppClassLoader, modified to use the system classloader
        // instead of the parent classloader for server classes
        List<URL> fromParent = WebAppContextWithReload.this.isServerClass(name)
            ? Collections.<URL>emptyList()
            : Lists.newArrayList(Iterators.forEnumeration(systemClassLoader.getResources(name)));
        Iterator<URL> fromWebapp = WebAppContextWithReload.this.isSystemClass(name)
                && !fromParent.isEmpty()
            ? Collections.<URL>emptyIterator()
            : Iterators.forEnumeration(findResources(name));
        return Iterators.asEnumeration(Iterators.concat(fromWebapp, fromParent.iterator()));
      }

      @Override
      public URL findResource(String name) {
        // Specifically for META-INF/services/javax.xml.parsers.SAXParserFactory
        String checkName = name;
        if (checkName.startsWith(META_INF_SERVICES)) {
          checkName = checkName.substring(META_INF_SERVICES.length());
        }
        checkName = checkName.replace('/', '.');

        // For a system path, load from the outside world.
        // Note: bootstrap has already been searched, so javax. classes should be
        // tried from the webapp first (except for javax.servlet and javax.el).
        URL found;
        if (WebAppContextWithReload.this.isSystemClass(checkName)
                && !systemClassesFromWebappFirst.match(checkName)) {
          found = systemClassLoader.getResource(name);
          if (found != null) {
            return found;
          }
        }

        // Always check this ClassLoader first.
        found = super.findResource(name);
        if (found != null) {
          return found;
        }

        // See if the outside world has it.
        found = systemClassLoader.getResource(name);
        if (found == null || WebAppContextWithReload.this.isServerClass(checkName)) {
          return null;
        }

        // Special-case Jetty/Jasper/etc. resources
        if (allowedFromSystemClassLoader.match(checkName) ||
            // Jetty-plus reads jndi.properties
            "jndi.properties".equals(name)) {
          return found;
        }

        // Warn, add containing URL to our own ClassLoader, and retry the call.
        String warnMessage = "Server resource '"
            + name
            + "' could not be found in the web app, but was found on the system classpath";
        if (!addContainingClassPathEntry(warnMessage, found, name)) {
          return null;
        }
        return super.findResource(name);
      }

      @Override
      protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // For system path, always prefer the outside world.
        // Note: bootstrap has already been searched, so javax. classes should be
        // tried from the webapp first (except for javax.servlet).
        if (WebAppContextWithReload.this.isSystemClass(name)
                && !systemClassesFromWebappFirst.match(name)) {
          try {
            Class<?> loaded = systemClassLoader.loadClass(name);
            if (resolve) {
              resolveClass(loaded);
            }
            return loaded;
          } catch (ClassNotFoundException e) {
          }
        }

        try {
          return super.loadClass(name, resolve);
        } catch (ClassNotFoundException e) {
          // Don't allow server classes to be loaded from the outside.
          if (WebAppContextWithReload.this.isServerClass(name)) {
            throw e;
          }
        }

        // See if the outside world has a URL for it.
        String resourceName = name.replace('.', '/') + ".class";
        URL found = systemClassLoader.getResource(resourceName);
        if (found == null) {
          throw new ClassNotFoundException(name);
        }

        // Special-case JDBCUnloader; it should always be loaded in the webapp classloader
        if (JDBCUnloader.class.getName().equals(name)) {
          byte[] jdbcUnloader;
          try (InputStream inputStream = found.openStream()) {
            jdbcUnloader = inputStream.readAllBytes();
          } catch (IOException e) {
            throw new ClassNotFoundException("Could not read class " + name, e);
          }
          return defineClass(name, jdbcUnloader, 0, jdbcUnloader.length);
        }

        // Those classes are allowed to be loaded right from the systemClassLoader
        // Note: Jetty classes here are not "server classes", handled above.
        if (allowedFromSystemClassLoader.match(name)) {
          Class<?> loaded = systemClassLoader.loadClass(name);
          if (resolve) {
            resolveClass(loaded);
          }
          return loaded;
        }

        // Warn, add containing URL to our own ClassLoader, and retry the call.
        String warnMessage = "Server class '"
                + name
                + "' could not be found in the web app, but was found on the system classpath";
        if (!addContainingClassPathEntry(warnMessage, found, resourceName)) {
          throw new ClassNotFoundException(name);
        }
        return super.loadClass(name, resolve);
      }

      private boolean addContainingClassPathEntry(String warnMessage,
          URL resource, String resourceName) {
        TreeLogger.Type logLevel = (System.getProperty(PROPERTY_NOWARN_WEBAPP_CLASSPATH) == null)
            ? TreeLogger.WARN : TreeLogger.DEBUG;
        TreeLogger branch = logger.branch(logLevel, warnMessage);
        String classPathURL;
        String foundStr = resource.toExternalForm();
        if (resource.getProtocol().equals("file")) {
          assert foundStr.endsWith(resourceName);
          classPathURL = foundStr.substring(0, foundStr.length()
              - resourceName.length());
        } else if (resource.getProtocol().equals("jar")) {
          assert foundStr.startsWith("jar:");
          assert foundStr.endsWith("!/" + resourceName);
          classPathURL = foundStr.substring(4, foundStr.length()
              - (2 + resourceName.length()));
        } else {
          branch.log(TreeLogger.ERROR,
              "Found resouce but unrecognized URL format: '" + foundStr + '\'');
          return false;
        }
        branch = branch.branch(logLevel, "Adding classpath entry '"
            + classPathURL + "' to the web app classpath for this session",
            null, new InstalledHelpInfo("webAppClassPath.html"));
        try {
          addClassPath(classPathURL);
          return true;
        } catch (IOException e) {
          branch.log(TreeLogger.ERROR, "Failed add container URL: '"
              + classPathURL + '\'', e);
          return false;
        }
      }
    }

    /**
     * Parent ClassLoader for the Jetty web app, which can only load JVM
     * classes. We would just use <code>null</code> for the parent ClassLoader
     * except this makes Jetty unhappy.
     */
    private final ClassLoader bootStrapOnlyClassLoader = new ClassLoader(null) {
    };

    private final TreeLogger logger;

    /**
     * In the usual case of launching {@link com.google.gwt.dev.DevMode}, this
     * will always by the system app ClassLoader.
     */
    private final ClassLoader systemClassLoader = Thread.currentThread().getContextClassLoader();

    private WebAppContextWithReload(TreeLogger logger, String webApp,
        String contextPath) {
      super(null, contextPath, null, null, null, new ErrorPageErrorHandler(),
              ServletContextHandler.SESSIONS);
      this.setWar(webApp);
      this.logger = logger;

      // Prevent file locking on Windows; pick up file changes.
      getInitParams().put(
          "org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");

      // Since the parent class loader is bootstrap-only, prefer it first.
      setParentLoaderPriority(true);
    }

    @Override
    protected void doStart() throws Exception {
      setClassLoader(new WebAppClassLoaderExtension());
      super.doStart();

      // After start, warn if a servlet/filter was configured other than those provided by Jetty
      boolean hasNonJettyFiltersOrServlets = Stream.concat(
              getServletContext().getServletRegistrations().values().stream(),
              getServletContext().getFilterRegistrations().values().stream()
      ).anyMatch(r -> !r.getClassName().startsWith("org.eclipse.jetty"));
      if (hasNonJettyFiltersOrServlets) {
        maybeLogDeprecationWarning(logger);
      }
    }

    @Override
    protected void doStop() throws Exception {
      super.doStop();

      Class<?> jdbcUnloader =
          getClassLoader().loadClass("com.google.gwt.dev.shell.jetty.JDBCUnloader");
      java.lang.reflect.Method unload = jdbcUnloader.getMethod("unload");
      unload.invoke(null);

      setClassLoader(null);
    }
  }

  /**
   * System property to suppress warnings about loading web app classes from the
   * system classpath.
   */
  private static final String PROPERTY_NOWARN_WEBAPP_CLASSPATH = "gwt.nowarn.webapp.classpath";

  /**
   * Setup a connector for the bind address/port.
   *
   * @param connector
   * @param bindAddress
   * @param port
   */
  private static void setupConnector(ServerConnector connector,
      String bindAddress, int port) {
    JettyLauncherUtils.setupConnector(connector, bindAddress, port);
  }

  // default value used if setBaseLogLevel isn't called
  private TreeLogger.Type baseLogLevel = TreeLogger.INFO;

  private String bindAddress = null;

  private SslConfiguration sslConfig = new SslConfiguration(ClientAuthType.NONE, null, null, false);

  private final Object privateInstanceLock = new Object();


  @Override
  public String getName() {
    return "Jetty";
  }

  @Override
  public boolean isSecure() {
    return sslConfig.isUseSsl();
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

  /*
   * TODO: This is a hack to pass the base log level to the SCL. We'll have to
   * figure out a better way to do this for SCLs in general. Please do not
   * depend on this method, as it is subject to change.
   */
  public void setBaseRequestLogLevel(TreeLogger.Type baseLogLevel) {
    synchronized (privateInstanceLock) {
      this.baseLogLevel = baseLogLevel;
    }
  }

  @Override
  public void setBindAddress(String bindAddress) {
    this.bindAddress = bindAddress;
  }

  @Override
  public ServletContainer start(TreeLogger logger, int port, File appRootDir)
      throws Exception {
    TreeLogger branch = logger.branch(TreeLogger.TRACE,
        "Starting Jetty on port " + port, null);

    checkStartParams(branch, port, appRootDir);

    // Setup our branch logger during startup.
    Log.setLog(new JettyTreeLogger(branch));

    // Force load some JRE singletons that can pin the classloader.
    jreLeakPrevention(logger);

    // Turn off XML validation.
    System.setProperty("org.eclipse.jetty.xml.XmlParser.Validating", "false");

    Server server = new Server();

    ServerConnector connector = getConnector(server, logger);
    setupConnector(connector, bindAddress, port);
    server.addConnector(connector);
    addPreventers(server);

    Configuration.ClassList cl = Configuration.ClassList.setServerDefault(server);
    try {
      // from jetty-plus.xml
      Thread.currentThread().getContextClassLoader().loadClass("org.eclipse.jetty.plus.webapp.PlusConfiguration");
      cl.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration",
          "org.eclipse.jetty.plus.webapp.EnvConfiguration",
          "org.eclipse.jetty.plus.webapp.PlusConfiguration");
    } catch (ClassNotFoundException cnfe) {
      logger.log(TreeLogger.Type.DEBUG, "jetty-plus isn't on the classpath, JNDI won't work. This might also affect annotations scanning and JSP.");
    }
    try {
      // from jetty-annotations.xml
      Thread.currentThread().getContextClassLoader()
          .loadClass("org.eclipse.jetty.annotations.AnnotationConfiguration");
      cl.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
          "org.eclipse.jetty.annotations.AnnotationConfiguration");
    } catch (ClassNotFoundException cnfe) {
      logger.log(TreeLogger.Type.DEBUG, "jetty-annotations isn't on the classpath, annotation scanning won't work. This might also affect annotations scanning.");
    }

    // Create a new web app in the war directory.
    WebAppContext wac = createWebAppContext(logger, appRootDir);
    wac.setSecurityHandler(new ConstraintSecurityHandler());

    RequestLogHandler logHandler = new RequestLogHandler();
    logHandler.setRequestLog(new JettyRequestLogger(logger, getBaseLogLevel()));
    logHandler.setHandler(wac);
    server.setHandler(logHandler);
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
    return createServletContainer(logger, appRootDir, server, wac,
        connectorPort);
  }

  protected JettyServletContainer createServletContainer(TreeLogger logger,
      File appRootDir, Server server, WebAppContext wac, int localPort) {
    return new JettyServletContainer(logger, server, wac, localPort, appRootDir);
  }

  protected WebAppContext createWebAppContext(TreeLogger logger, File appRootDir) {
    WebAppContext context = new WebAppContextWithReload(logger, appRootDir.getAbsolutePath(), "/");
    context.setConfigurationClasses(new String[] {
        "org.eclipse.jetty.webapp.WebInfConfiguration",
        "org.eclipse.jetty.webapp.WebXmlConfiguration",
        "org.eclipse.jetty.webapp.MetaInfConfiguration",
        "org.eclipse.jetty.webapp.FragmentConfiguration",
        "org.eclipse.jetty.plus.webapp.EnvConfiguration",
        "org.eclipse.jetty.plus.webapp.PlusConfiguration",
        "org.eclipse.jetty.annotations.AnnotationConfiguration",
        "org.eclipse.jetty.webapp.JettyWebXmlConfiguration"
    });
    return context;
  }

  protected ServerConnector getConnector(Server server, TreeLogger logger) {
    return JettyLauncherUtils.getConnector(server, sslConfig, logger);
  }

  private void addPreventers(Server server) {
    // Trigger a call to sun.awt.AppContext.getAppContext(). This will
    // pin the common class loader in memory but that shouldn't be an
    // issue.
    server.addBean(new AppContextLeakPreventer());

    /*
     * Several components end up calling: sun.misc.GC.requestLatency(long)
     *
     * Those libraries / components known to trigger memory leaks due to 
     * eventual calls to requestLatency(long) are:
     * - javax.management.remote.rmi.RMIConnectorServer.start()
     */
    server.addBean(new GCThreadLeakPreventer());

    /*
     * Creating a MessageDigest during web application startup initializes the 
     * Java Cryptography Architecture. Under certain conditions this starts a 
     * Token poller thread with TCCL equal to the web application class loader.
     *
     * Instead we initialize JCA right now.
     */
    server.addBean(new SecurityProviderLeakPreventer());

    /*
     * Haven't got to the root of what is going on with this leak but if a web app is the first to
     * make the calls below the web application class loader will be pinned in memory.
     */
    server.addBean(new DOMLeakPreventer());
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
      throw new NullPointerException("app root direcotry cannot be null");
    }
  }

  /*
   * TODO: This is a hack to pass the base log level to the SCL. We'll have to
   * figure out a better way to do this for SCLs in general.
   */
  private TreeLogger.Type getBaseLogLevel() {
    synchronized (privateInstanceLock) {
      return this.baseLogLevel;
    }
  }

  /**
   * This is a modified version of JreMemoryLeakPreventionListener.java found
   * in the Apache Tomcat project at
   *
   * http://svn.apache.org/repos/asf/tomcat/trunk/java/org/apache/catalina/core/
   * JreMemoryLeakPreventionListener.java
   *
   * Relevant part of the Tomcat NOTICE, retrieved from
   * http://svn.apache.org/repos/asf/tomcat/trunk/NOTICE Apache Tomcat Copyright
   * 1999-2010 The Apache Software Foundation
   *
   * This product includes software developed by The Apache Software Foundation
   * (http://www.apache.org/).
   */
  private void jreLeakPrevention(TreeLogger logger) {
    /*
     * Calling getPolicy retains a static reference to the context class loader.
     */
    try {
      // Policy.getPolicy();
      Class<?> policyClass = Class.forName("javax.security.auth.Policy");
      Method method = policyClass.getMethod("getPolicy");
      method.invoke(null);
    } catch (ClassNotFoundException e) {
      // Ignore. The class is deprecated.
    } catch (SecurityException e) {
      // Ignore. Don't need call to getPolicy() to be successful,
      // just need to trigger static initializer.
    } catch (NoSuchMethodException e) {
      logger.log(TreeLogger.WARN, "jreLeakPrevention.authPolicyFail", e);
    } catch (IllegalArgumentException e) {
      logger.log(TreeLogger.WARN, "jreLeakPrevention.authPolicyFail", e);
    } catch (IllegalAccessException e) {
      logger.log(TreeLogger.WARN, "jreLeakPrevention.authPolicyFail", e);
    } catch (InvocationTargetException e) {
      logger.log(TreeLogger.WARN, "jreLeakPrevention.authPolicyFail", e);
    }

    /*
     * Several components end up opening JarURLConnections without first
     * disabling caching. This effectively locks the file. Whilst more
     * noticeable and harder to ignore on Windows, it affects all operating
     * systems.
     *
     * Those libraries/components known to trigger this issue include: - log4j
     * versions 1.2.15 and earlier - javax.xml.bind.JAXBContext.newInstance()
     */

    // Set the default URL caching policy to not to cache
    try {
      // Doesn't matter that this JAR doesn't exist - just as long as
      // the URL is well-formed
      URL url = new URL("jar:file://dummy.jar!/");
      URLConnection uConn = url.openConnection();
      uConn.setDefaultUseCaches(false);
    } catch (MalformedURLException e) {
      logger.log(TreeLogger.ERROR, "jreLeakPrevention.jarUrlConnCacheFail", e);
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "jreLeakPrevention.jarUrlConnCacheFail", e);
    }
  }
}
