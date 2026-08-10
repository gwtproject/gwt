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

/**
 * A minimal logging facade used by GWT's server-side RPC package.
 * <p>
 * Instances can be obtained from {@link RpcLogManager#getLogger(Class)}. The logging system that
 * this delegates to is selected by {@link RpcLogManager} at class-initialization.
 *
 * @see RpcLogManager
 * @see RpcLoggerProvider
 */
public interface RpcLogger {

  void info(String message);

  void warn(String message);

  void error(String message);

  void error(String message, Throwable throwable);

}
