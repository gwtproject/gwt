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
 */
public class ServletContextLoggerProvider implements RpcLoggerProvider {

  public ServletContextLoggerProvider() {
  }

  @Override
  public RpcLoggerDelegate createLogger(String name) {
    return new ServletContextLogger();
  }

  private static final class ServletContextLogger implements RpcLoggerDelegate {

    ServletContextLogger() { }

    @Override
    public void info(String message, ServletContext servletContext) {
      servletContext.log(message);
    }

    @Override
    public void warn(String message, ServletContext servletContext) {
      servletContext.log("WARNING: " + message);
    }

    @Override
    public void error(String message, ServletContext servletContext) {
      servletContext.log("ERROR: " + message);
    }

    @Override
    public void error(String message, Throwable throwable, ServletContext servletContext) {
      servletContext.log("ERROR: " + message, throwable);
    }
  }

}
