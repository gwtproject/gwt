/*
 * Copyright 2010 Google Inc.
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
package java.util.logging;

import java.util.ArrayList;
import java.util.List;

/**
 *  An emulation of the java.util.logging.Logger class. See
 *  <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/util/logging/Logger.html">
 *  The Java API doc for details</a>
 */
public class Logger {

  public static final String GLOBAL_LOGGER_NAME = "global";

  public static Logger getGlobal() {
    return getLogger(GLOBAL_LOGGER_NAME);
  }

  public static Logger getLogger(String name) {
    // Use shortcut if logging is disabled to avoid parent logger creations in LogManager
    if (isLoggingSetToFalse()) {
      return new Logger(name, "");
    }
    return LogManager.getLogManager().ensureLogger(name);
  }

  private static boolean isLoggingSetToFalse() {
    assertLoggingValues();
    return System.getProperty("gwt.logging.enabled", "FALSE").equals("FALSE");
  }

  private static boolean isLoggingSetToSevere() {
    assertLoggingValues();
    return System.getProperty("gwt.logging.enabled", "FALSE").equals("SEVERE");
  }

  private static boolean isLoggingSetToWarning() {
    assertLoggingValues();
    return System.getProperty("gwt.logging.enabled", "FALSE").equals("SEVERE");
  }

  private static boolean isLoggingSetToTrue() {
    assertLoggingValues();
    return System.getProperty("gwt.logging.enabled", "FALSE").equals("TRUE");
  }

  static void assertLoggingValues() {
    String value = System.getProperty("gwt.logging.enabled", "FALSE");
    if ("FALSE".equals(value) || "TRUE".equals(value) || "SEVERE".equals(value)
        || "WARNING".equals(value)) {
      return;
    }

    throw new RuntimeException("Undefined value for gwt.logging.enabled: '" + value
        + "'. Allowed values are TRUE, FALSE, SEVERE, WARNING");
  }

  private List<Handler> handlers;
  private Level level = null;
  private String name;
  private Logger parent;  // Should never be null except in the RootLogger
  private boolean useParentHandlers;

  protected Logger(String name, @SuppressWarnings("unused") String resourceName) {
    if (isLoggingSetToFalse()) {
      return;
    }

    this.name = name;
    this.useParentHandlers = true;
    handlers = new ArrayList<Handler>();
  }

  public void addHandler(Handler handler) {
    if (isLoggingSetToFalse()) {
      return;
    }
    handlers.add(handler);
  }

  public void config(String msg) {
    if (isLoggingSetToTrue()) {
      log(Level.CONFIG, msg);
    }
  }

  public void fine(String msg) {
    if (isLoggingSetToTrue()) {
      log(Level.FINE, msg);
    }
  }

  public void finer(String msg) {
    if (isLoggingSetToTrue()) {
      log(Level.FINER, msg);
    }
  }

  public void finest(String msg) {
    if (isLoggingSetToTrue()) {
      log(Level.FINEST, msg);
    }
  }

  public void info(String msg) {
    if (isLoggingSetToTrue()) {
      log(Level.INFO, msg);
    }
  }

  public void warning(String msg) {
    if (isLoggingSetToFalse() || isLoggingSetToSevere()) {
      return;
    }
    log(Level.WARNING, msg);
  }

  public void severe(String msg) {
    if (isLoggingSetToFalse()) {
      return;
    }
    log(Level.SEVERE, msg);
  }

  public Handler[] getHandlers() {
    if (isLoggingSetToFalse()) {
      return new Handler[0];
    }

    return handlers.toArray(new Handler[handlers.size()]);
  }

  public Level getLevel() {
    return isLoggingSetToFalse() ? null : level;
  }

  public String getName() {
    return isLoggingSetToFalse() ? null : name;
  }

  public Logger getParent() {
    return isLoggingSetToFalse() ? null : parent;
  }

  public boolean getUseParentHandlers() {
    return isLoggingSetToFalse() ? null : useParentHandlers;
  }

  public boolean isLoggable(Level messageLevel) {
    return isLoggingSetToFalse()
        ? false : getEffectiveLevel().intValue() <= messageLevel.intValue();
  }

  public void log(Level level, String msg) {
    log(level, msg, null);
  }

  public void log(Level level, String msg, Throwable thrown) {
    if (isLoggingSetToFalse()) {
      return;
    }
    if (isLoggingSetToSevere()) {
      if (level.intValue() >= 1000) {
        actuallyLog(level, msg, thrown);
      }
    } else if (isLoggingSetToWarning()) {
      if (level.intValue() >= Level.WARNING.intValue()) {
        actuallyLog(level, msg, thrown);
      }
    } else {
      actuallyLog(level, msg, thrown);
    }
  }

  public void log(LogRecord record) {
    if (isLoggingSetToFalse()) {
      return;
    }
    if (isLoggingSetToSevere()) {
      if (level.intValue() >= 1000) {
        actuallyLog(record);
      }
    } else if (isLoggingSetToWarning()) {
      if (level.intValue() >= Level.WARNING.intValue()) {
        actuallyLog(record);
      }
    } else {
      actuallyLog(record);
    }
  }

  public void removeHandler(Handler handler) {
    if (isLoggingSetToFalse()) {
      return;
    }
    handlers.remove(handler);
  }

  public void setLevel(Level newLevel) {
    if (isLoggingSetToFalse()) {
      return;
    }
    this.level = newLevel;
  }

  public void setParent(Logger newParent) {
    if (isLoggingSetToFalse()) {
      return;
    }
    if (newParent != null) {
      parent = newParent;
    }
  }

  public void setUseParentHandlers(boolean newUseParentHandlers) {
    if (isLoggingSetToFalse()) {
      return;
    }
    this.useParentHandlers = newUseParentHandlers;
  }

  private Level getEffectiveLevel() {
    if (level != null) {
      return level;
    }

    Logger logger = getParent();
    while (logger != null) {
      Level effectiveLevel = logger.getLevel();
      if (effectiveLevel != null) {
        return effectiveLevel;
      }
      logger = logger.getParent();
    }
    return Level.INFO;
  }

  private void actuallyLog(Level level, String msg, Throwable thrown) {
    if (isLoggable(level)) {
      LogRecord record = new LogRecord(level, msg);
      record.setThrown(thrown);
      record.setLoggerName(getName());
      actuallyLog(record);
    }
  }

  private void actuallyLog(LogRecord record) {
    if (isLoggable(record.getLevel())) {
      for (Handler handler : getHandlers()) {
        handler.publish(record);
      }
      Logger logger = getUseParentHandlers() ? getParent() : null;
      while (logger != null) {
        for (Handler handler : logger.getHandlers()) {
          handler.publish(record);
        }
        logger = logger.getUseParentHandlers() ? logger.getParent() : null;
      }
    }
  }

  /* Not Implemented */
  // public static Logger getAnonymousLogger() {
  // public static Logger getAnonymousLogger(String resourceBundleName) {}
  // public Filter getFilter() {}
  // public static Logger getLogger(String name, String resourceBundleName) {}
  // public ResourceBundle getResourceBundle() {}
  // public String getResourceBundleName() {}
  // public void setFilter(Filter newFilter) {}
  // public void entering(String sourceClass, String sourceMethod) {}
  // public void entering(String sourceClass, String sourceMethod, Object param1) {}
  // public void entering(String sourceClass, String sourceMethod, Object[] params) {}
  // public void exiting(String sourceClass, String sourceMethod, Object result) {}
  // public void exiting(String sourceClass, String sourceMethod) {}
  // public void log(Level level, String msg, Object param1) {}
  // public void log(Level level, String msg, Object[] params) {}
  // public void logp(Level level, String sourceClass, String sourceMethod, String msg) {}
  // public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {}
  // public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {}
  // public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {}
  // public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg) {}
  // public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object param1) {}
  // public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object[] params) {}
  // public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown) {}
  // public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {}
}