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
 * A {@link RpcLoggerProvider} that delegates to <code>System.Logger</code>
 * <br />
 * Once support for Java 8 is dropped, this class can be implemented without reflection.
 */
public class PlatformLoggerProvider extends ReflectiveLoggerProvider implements RpcLoggerProvider {

  public PlatformLoggerProvider() {
    try {
      createLogger = System.class.getMethod("getLogger", String.class);
      Class<?> levelClass = Class.forName("java.lang.System$Logger$Level");
      infoLevel = getLogLevel(levelClass, "INFO");
      warnLevel = getLogLevel(levelClass, "WARNING");
      errorLevel = getLogLevel(levelClass, "ERROR");
      Class<?> loggerClass = Class.forName("java.lang.System$Logger");
      logMessage = loggerClass.getMethod("log", levelClass, String.class);
      logThrowable = loggerClass.getMethod("log", levelClass, String.class, Throwable.class);
      available = true;
    } catch (Exception e) {
      available = false;
    }
  }

}
