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
    if (isLoggingDisabled()) {
      return new Logger(name, "");
    }
    return LogManager.getLogManager().ensureLogger(name);
  }

  private static boolean isLoggingDisabled() {
    String loggingEnabled = System.getProperty("gwt.logging.enabled", "FALSE");
    if ("TRUE".equals(loggingEnabled) || "SEVERE".equals(loggingEnabled)
        || "WARNING".equals(loggingEnabled)) {
      return false;
    }
    return true;
  }

  private static boolean isLogSetToSevere() {
    return System.getProperty("gwt.logging.enabled", "FALSE").equals("SEVERE");
  }

  private static boolean isLogSetToWarning() {
    return System.getProperty("gwt.logging.enabled", "FALSE").equals("SEVERE");
  }

  private List<Handler> handlers;
  private Level level = null;
  private String name;
  private Logger parent;  // Should never be null except in the RootLogger
  private boolean useParentHandlers;

  protected Logger(String name, @SuppressWarnings("unused") String resourceName) {
    if (isLoggingDisabled()) {
      return;
    }
    this.name = name;
    this.useParentHandlers = true;
    handlers = new ArrayList<Handler>();
  }

  public void addHandler(Handler handler) {
    if (isLoggingDisabled()) {
      return;
    }
    handlers.add(handler);
  }

  public void config(String msg) {
    if (isLoggingDisabled()) {
      return;
    }
    if (isLogSetToSevere()) {
      return;
    }
    if (isLogSetToWarning()) {
      return;
    }
    log(Level.CONFIG, msg);
  }

  public void fine(String msg) {
    if (isLoggingDisabled()) {
      return;
    }
    if (isLogSetToSevere()) {
      return;
    }
    if (isLogSetToWarning()) {
      return;
    }
    log(Level.FINE, msg);
  }

  public void finer(String msg) {
    if (isLoggingDisabled()) {
      return;
    }
    if (isLogSetToSevere()) {
      return;
    }
    if (isLogSetToWarning()) {
      return;
    }
    log(Level.FINER, msg);
  }

  public void finest(String msg) {
    if (isLoggingDisabled()) {
      return;
    }
    if (isLogSetToSevere()) {
      return;
    }
    if (isLogSetToWarning()) {
      return;
    }
    log(Level.FINEST, msg);
  }

  public Handler[] getHandlers() {
    if (isLoggingDisabled()) {
      return null;
    }
    return handlers.toArray(new Handler[handlers.size()]);
  }

  public Level getLevel() {
    if (isLoggingDisabled()) {
      return null;
    }
    return level;
  }

  public String getName() {
    if (isLoggingDisabled()) {
      return null;
    }
    return name;
  }

  public Logger getParent() {
    if (isLoggingDisabled()) {
      return null;
    }
    return parent;
  }

  public boolean getUseParentHandlers() {
    if (isLoggingDisabled()) {
      return false;
    }
    return useParentHandlers;
  }

  public void info(String msg) {
    if (isLoggingDisabled()) {
      return;
    }

    if (isLogSetToSevere()) {
      return;
    }
    if (isLogSetToWarning()) {
      return;
    }
    log(Level.INFO, msg);
  }

  public boolean isLoggable(Level messageLevel) {
    if (isLoggingDisabled()) {
      return false;
    }

    return getEffectiveLevel().intValue() <= messageLevel.intValue();
  }

  public void log(Level level, String msg) {
    if (isLoggingDisabled()) {
      return;
    }
    if (isLogSetToSevere()) {
      if (level.intValue() >= 1000) {
        actuallyLog(level, msg, null);
      }
    }

    if (isLogSetToWarning()) {
      if (level.intValue() >= Level.WARNING.intValue()) {
        actuallyLog(level, msg, null);
      }
    }
  }

  public void log(Level level, String msg, Throwable thrown) {
    if (isLoggingDisabled()) {
      return;
    }
    if (isLogSetToSevere()) {
      if (level.intValue() >= 1000) {
        actuallyLog(level, msg, thrown);
      }
    }
    if (isLogSetToWarning()) {
      if (level.intValue() >= Level.WARNING.intValue()) {
        actuallyLog(level, msg, thrown);
      }
    }
  }

  public void log(LogRecord record) {
    if (isLoggingDisabled()) {
      return;
    }
    if (isLogSetToSevere()) {
      if (level.intValue() >= 1000) {
        actuallyLog(record);
      }
    }
    if (isLogSetToWarning()) {
      if (level.intValue() >= Level.WARNING.intValue()) {
        actuallyLog(record);
      }
    }
  }

  public void removeHandler(Handler handler) {
    if (isLoggingDisabled()) {
      return;
    }
    handlers.remove(handler);
  }

  public void setLevel(Level newLevel) {
    if (isLoggingDisabled()) {
      return;
    }
    this.level = newLevel;
  }

  public void setParent(Logger newParent) {
    if (isLoggingDisabled()) {
      return;
    }
    if (newParent != null) {
      parent = newParent;
    }
  }

  public void setUseParentHandlers(boolean newUseParentHandlers) {
    if (isLoggingDisabled()) {
      return;
    }
    this.useParentHandlers = newUseParentHandlers;
  }

  public void severe(String msg) {
    if (isLoggingDisabled()) {
      return;
    }
    log(Level.SEVERE, msg);
  }

  public void warning(String msg) {
    if (isLoggingDisabled()) {
      return;
    }
    if (isLogSetToSevere()) {
      return;
    }
    log(Level.WARNING, msg);
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