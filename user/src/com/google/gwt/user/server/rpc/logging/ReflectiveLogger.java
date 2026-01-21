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

import javax.servlet.ServletContext;

/**
 * A logger that uses reflection to call the underlying platform logger.
 * Compatible with platform logging and Log4j; incompatible with SLF4j.
 */
class ReflectiveLogger implements RpcLoggerDelegate {

  private final Object logger;
  private final Object infoLevel;
  private final Object warnLevel;
  private final Object errorLevel;
  private final Method logMessage;
  private final Method logThrowable;

  ReflectiveLogger(Object platformLogger, Object infoLevel, Object warnLevel, Object errorLevel,
      Method logMessage, Method logThrowable) {
    this.logger = platformLogger;
    this.infoLevel = infoLevel;
    this.warnLevel = warnLevel;
    this.errorLevel = errorLevel;
    this.logMessage = logMessage;
    this.logThrowable = logThrowable;
  }

  @Override
  public void info(String message, ServletContext servletContext) {
    try {
      logMessage.invoke(logger, infoLevel, message);
    } catch (Exception e) {
      System.err.println("Failed to log message: " + message);
      e.printStackTrace();
    }
  }

  @Override
  public void warn(String message, ServletContext servletContext) {
    try {
      logMessage.invoke(logger, warnLevel, message);
    } catch (Exception e) {
      System.err.println("Failed to log message: " + message);
      e.printStackTrace();
    }
  }

  @Override
  public void error(String message, ServletContext servletContext) {
    try {
      logMessage.invoke(logger, errorLevel, message);
    } catch (Exception e) {
      System.err.println("Failed to log message: " + message);
      e.printStackTrace();
    }
  }

  @Override
  public void error(String message, Throwable throwable, ServletContext servletContext) {
    try {
      logThrowable.invoke(logger, errorLevel, message, throwable);
    } catch (Exception e) {
      System.err.println("Failed to log message: " + message);
      e.printStackTrace();
      System.err.println("Original exception:");
      throwable.printStackTrace();
    }
  }

}
