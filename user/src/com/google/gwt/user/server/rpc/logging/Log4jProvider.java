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

import java.lang.reflect.Method;

/**
 * A {@link RpcLoggerProvider} that delegates to Log4j.
 */
public class Log4jProvider extends ReflectiveLoggerProvider implements RpcLoggerProvider {

  public Log4jProvider() {
    try {
      Class<?> factory = Class.forName("org.apache.logging.log4j.LogManager");
      createLogger = factory.getMethod("getLogger", String.class);
      Class<?> levelClass = Class.forName("org.apache.logging.log4j.Level");
      Method levelFinder = levelClass.getMethod("valueOf", String.class);
      infoLevel = levelFinder.invoke(null, "INFO");
      warnLevel = levelFinder.invoke(null, "WARN");
      errorLevel = levelFinder.invoke(null, "ERROR");
      Class<?> loggerClass = Class.forName("org.apache.logging.log4j.Logger");
      logMessage = loggerClass.getMethod("log", levelClass, String.class);
      logThrowable = loggerClass.getMethod("log", levelClass, String.class, Throwable.class);
      available = true;
    } catch (Exception e) {
      available = false;
    }
  }

}
