/*
 * Copyright 2025 GWT Project Authors
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
package com.google.gwt.user.server.rpc.logging;

import javax.servlet.ServletContext;

/**
 * A {@link RpcLoggerProvider} that delegates to the servlet context's logging. Used as the fallback
 * if no other logger provider is found.
 * <br />
 * Servlet context logging does not support separate named loggers, so this reuses the same logger
 * instance. This reuse allows the servlet context to be easily set after the provider is
 * initialized, and normally the provider will be initialized before the servlet context is even
 * available.
 */
public class ServletContextLoggerProvider implements RpcLoggerProvider {

  private final ServletContextLogger logger = new ServletContextLogger();

  public ServletContextLoggerProvider() {
  }

  void setServletContext(ServletContext servletContext) {
    if (servletContext != null) {
      logger.servletContext = servletContext;
    }
  }

  @Override
  public RpcLoggerDelegate createLogger(String ignored) {
    return logger;
  }

  private static final class ServletContextLogger implements RpcLoggerDelegate {

    private volatile ServletContext servletContext;

    private ServletContextLogger() { }

    @Override
    public void info(String message) {
      if (servletContext != null) {
        servletContext.log("INFO: " + message);
      } else {
        System.out.println("INFO: " + message);
      }
    }

    @Override
    public void warn(String message) {
      if (servletContext != null) {
        servletContext.log("WARNING: " + message);
      } else {
        System.out.println("WARNING: " + message);
      }
    }

    @Override
    public void error(String message) {
      if (servletContext != null) {
        servletContext.log("ERROR: " + message);
      } else {
        System.err.println("ERROR: " + message);
      }
    }

    @Override
    public void error(String message, Throwable throwable) {
      if (servletContext != null) {
        servletContext.log("ERROR: " + message, throwable);
      } else {
        System.err.println("ERROR: " + message);
        throwable.printStackTrace();
      }
    }
  }

}
