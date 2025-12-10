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
 * A very simplified interface for logging RPC events.
 * <br />
 * A {@link ServletContext} is passed for compatibility with the fallback
 * {@link ServletContextLoggerProvider}. It may be ignored for any other implementation.
 */
public interface RpcLoggerDelegate {

  void info(String message, ServletContext servletContext);

  void warn(String message, ServletContext servletContext);

  void error(String message, ServletContext servletContext);

  void error(String message, Throwable throwable, ServletContext servletContext);

}
