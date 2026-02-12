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
 * A wrapper that creates a {@link RpcLoggerDelegate} for a given name.
 */
public final class RpcLogger {

  private final RpcLoggerDelegate delegate;

  RpcLogger(String name, RpcLoggerProvider provider) {
    delegate = provider.createLogger(name);
  }

  public void info(String message, ServletContext servletContext) {
    delegate.info(message, servletContext);
  }

  public void warn(String message, ServletContext servletContext) {
    delegate.warn(message, servletContext);
  }

  public void error(String message, ServletContext servletContext) {
    delegate.error(message, servletContext);
  }

  public void error(String message, Throwable throwable, ServletContext servletContext) {
    delegate.error(message, throwable, servletContext);
  }

}
