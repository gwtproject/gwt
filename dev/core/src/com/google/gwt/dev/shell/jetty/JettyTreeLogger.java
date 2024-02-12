/*
 * Copyright 2023 Google Inc.
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

import com.google.gwt.core.ext.TreeLogger;

import org.eclipse.jetty.util.log.Logger;

/**
 * An adapter for the Jetty logging system to GWT's TreeLogger. This implementation class is only
 * public to allow {@link org.eclipse.jetty.util.log.Log} to instantiate it.
 * <p>
 * The weird static data / default construction setup is a game we play with
 * {@link org.eclipse.jetty.util.log.Log}'s static initializer to prevent the initial log message
 * from going to stderr.
 */
public class JettyTreeLogger implements Logger {
  private final TreeLogger logger;

  public JettyTreeLogger(TreeLogger logger) {
    if (logger == null) {
      throw new NullPointerException();
    }
    this.logger = logger;
  }

  public void debug(String msg, long arg) {
    logger.log(TreeLogger.SPAM, format(msg, arg));
  }

  public void debug(String msg, Object... args) {
    if (logger.isLoggable(TreeLogger.SPAM)) {
      logger.log(TreeLogger.SPAM, format(msg, args));
    }
  }

  public void debug(String msg, Throwable th) {
    logger.log(TreeLogger.SPAM, msg, th);
  }

  public void debug(Throwable th) {
    logger.log(TreeLogger.SPAM, "", th);
  }

  public Logger getLogger(String name) {
    return this;
  }

  public String getName() {
    return "";
  }

  public void info(String msg, Object... args) {
    if (logger.isLoggable(TreeLogger.TRACE)) {
      logger.log(TreeLogger.TRACE, format(msg, args));
    }
  }

  public void info(String msg, Throwable th) {
    logger.log(TreeLogger.TRACE, msg, th);
  }

  public void info(Throwable th) {
    logger.log(TreeLogger.TRACE, "", th);
  }

  public boolean isDebugEnabled() {
    return logger.isLoggable(TreeLogger.SPAM);
  }

  public void setDebugEnabled(boolean enabled) {
    // ignored
  }

  public void warn(String msg, Object... args) {
    if (logger.isLoggable(TreeLogger.WARN)) {
      logger.log(TreeLogger.WARN, format(msg, args));
    }
  }

  public void warn(String msg, Throwable th) {
    logger.log(TreeLogger.WARN, msg, th);
  }

  public void warn(Throwable th) {
    logger.log(TreeLogger.WARN, "", th);
  }

  public void ignore(Throwable th) {
    logger.log(TreeLogger.SPAM, "IGNORE", th);
  }

  /**
   * Copied from org.eclipse.log.StdErrLog.
   */
  private String format(String msg, Object... args) {
    if (msg == null) {
      msg = "";
      for (int i = 0; i < args.length; i++) {
        msg += "{} ";
      }
    }
    String braces = "{}";
    int start = 0;
    StringBuilder builder = new StringBuilder();
    for (Object arg : args) {
      int bracesIndex = msg.indexOf(braces, start);
      if (bracesIndex < 0) {
        builder.append(msg.substring(start));
        builder.append(" ");
        builder.append(arg);
        start = msg.length();
      } else {
        builder.append(msg.substring(start, bracesIndex));
        builder.append(String.valueOf(arg));
        start = bracesIndex + braces.length();
      }
    }
    builder.append(msg.substring(start));
    return builder.toString();
  }
}
