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

import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletContext;

/**
 * A wrapper that lazily creates a {@link RpcLoggerDelegate} for a given name.
 */
public class RpcLogger {

  private final AtomicReference<RpcLoggerDelegate> delegate = new AtomicReference<>();
  private final String name;

  public RpcLogger(String name) {
    this.name = name;
  }

  public void info(String message, ServletContext servletContext) {
    getDelegate().info(message, servletContext);
  }

  public void warn(String message, ServletContext servletContext) {
    getDelegate().warn(message, servletContext);
  }

  public void error(String message, ServletContext servletContext) {
    getDelegate().error(message, servletContext);
  }

  public void error(String message, Throwable throwable, ServletContext servletContext) {
    getDelegate().error(message, throwable, servletContext);
  }

  /**
   * Retrieves or creates a logger delegate.
   * @return the delegate
   */
  private RpcLoggerDelegate getDelegate() {
    RpcLoggerDelegate result = this.delegate.get();
    if (result == null) {
      RpcLoggerProvider provider = RpcLogManager.getLoggerProvider();
      RpcLoggerDelegate newDelegate = provider.createLogger(name);
      result = delegate.updateAndGet(old -> old == null ? newDelegate : old);
    }
    return result;
  }

}
