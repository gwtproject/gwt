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
  
  /**
   * Interface for the implementation of Logger. We use a LoggerImplNull to ensure
   * that logging code compiles out when logging is disabled, and a
   * LoggerImplRegular to provide normal functionality when logging is enabled.
   */
  private interface LoggerImpl {
    void addHandler(Handler handler);
    void config(String msg);
    void fine(String msg);
    void finer(String msg);
    void finest(String msg);
    
    /**
     * Get the handlers attached to this logger.
     * @return the array of handlers, or null if there are no handlers
     */
    Handler[] getHandlers();

    Level getLevel();
    String getName();
    Logger getParent();
    boolean getUseParentHandlers();
    void info(String msg);
    boolean isLoggable(Level messageLevel);
    void log(Level level, String msg);
    void log(Level level, String msg, Throwable thrown);
    void log(LogRecord record);
    void removeHandler(Handler handler);
    void setLevel(Level newLevel);
    void setName(String newName);
    void setParent(Logger newParent);
    void setUseParentHandlers(boolean newUseParentHandlers);
    void severe(String msg);
    void warning(String msg);
  }
  
  /**
   * Null implementation for the Logger class which ensures that calls to Logger
   * compile out when logging is disabled.
   */
  private static class LoggerImplNull implements LoggerImpl {

    @Override
    public void addHandler(Handler handler) { 
      // Do nothing
    }

    @Override
    public void config(String msg) { 
      // Do nothing
    }

    @Override
    public void fine(String msg) { 
      // Do nothing
    }

    @Override
    public void finer(String msg) { 
      // Do nothing
    }

    @Override
    public void finest(String msg) { 
      // Do nothing
    }

    @Override
    public Handler[] getHandlers() {
      return null;
    }

    @Override
    public Level getLevel() {
      return null;
    }

    @Override
    public String getName() {
      return "";
    }

    @Override
    public Logger getParent() {
      return null;
    }

    @Override
    public boolean getUseParentHandlers() {
      return false;
    }

    @Override
    public void info(String msg) {
      // Do nothing
    }

    @Override
    public boolean isLoggable(Level messageLevel) {
      return false;
    }

    @Override    
    public void log(Level level, String msg) {
      // Do nothing  
    }

    @Override    
    public void log(Level level, String msg, Throwable thrown) {
      // Do nothing
    }

    @Override
    public void log(LogRecord record) {
      // Do nothing
    }

    @Override
    public void removeHandler(Handler handler) { 
      // Do nothing
    }

    @Override
    public void setLevel(Level newLevel) { 
      // Do nothing
    }

    @Override
    public void setName(String newName) { 
      // Do nothing
    }

    @Override
    public void setParent(Logger newParent) { 
      // Do nothing
    }

    @Override
    public void setUseParentHandlers(boolean newUseParentHandlers) { 
      // Do nothing
    }

    @Override
    public void severe(String msg) { 
      // Do nothing
    }

    @Override
    public void warning(String msg) { 
      // Do nothing
    }  
  }
  
  /**
   * Implementation for the Logger class when logging is enabled.
   */
  private static class LoggerImplRegular implements LoggerImpl {
    private List<Handler> handlers;
    private Level level = null;
    private String name;
    private Logger parent;  // Should never be null except in the RootLogger
    private boolean useParentHandlers;

    public LoggerImplRegular() {
      this.useParentHandlers = true;
      handlers = new ArrayList<Handler>();
    }

    @Override
    public void addHandler(Handler handler) {
      handlers.add(handler);
    }

    @Override
    public void config(String msg) {
      log(Level.CONFIG, msg);
    }

    @Override
    public void fine(String msg) {
      log(Level.FINE, msg);
    }

    @Override
    public void finer(String msg) {
      log(Level.FINER, msg);
    }

    @Override
    public void finest(String msg) {
      log(Level.FINEST, msg);
    }

    @Override
    public Handler[] getHandlers() {
      return handlers.toArray(new Handler[handlers.size()]);
    }

    @Override
    public Level getLevel() {
      return level;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public Logger getParent() {
      return parent;
    }

    @Override
    public boolean getUseParentHandlers() {
      return useParentHandlers;
    }

    @Override
    public void info(String msg) {
      log(Level.INFO, msg);
    }

    @Override
    public boolean isLoggable(Level messageLevel) {
      return getEffectiveLevel().intValue() <= messageLevel.intValue();
    }

    @Override
    public void log(Level level, String msg) {
      log(level, msg, null);
    }

    @Override
    public void log(Level level, String msg, Throwable thrown) {
      if (isLoggable(level)) {
        LogRecord record = new LogRecord(level, msg);
        record.setThrown(thrown);
        record.setLoggerName(getName());
        log(record);
      }
    }

    @Override
    public void log(LogRecord record) {
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

    @Override
    public void removeHandler(Handler handler) {
      handlers.remove(handler);
    }

    @Override
    public void setLevel(Level newLevel) {
      level = newLevel;
    }

    @Override
    public void setName(String newName) {
      name = newName;
    }

    @Override
    public void setParent(Logger newParent) {
      if (newParent != null) {
        parent = newParent;
      }
    }

    @Override
    public void setUseParentHandlers(boolean newUseParentHandlers) {
      useParentHandlers = newUseParentHandlers;
    }

    @Override
    public void severe(String msg) {
      log(Level.SEVERE, msg);
    }

    @Override
    public void warning(String msg) {
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
  }
  
  /**
   * Implementation for the Logger class when logging is enabled only at Severe and above.
   */
  private static class LoggerImplSevere extends LoggerImplRegular {
    @Override
    public void config(String msg) {
      // Do nothing
    }

    @Override
    public void fine(String msg) {
      // Do nothing
    }

    @Override
    public void finer(String msg) {
      // Do nothing
    }

    @Override
    public void finest(String msg) {
      // Do nothing
    }

    @Override
    public void info(String msg) {
      // Do nothing
    }

    @Override
    public void log(Level level, String msg) {
      if (level.intValue() >= 1000) {
        super.log(level, msg);
      }
    }

    @Override
    public void log(Level level, String msg, Throwable thrown) {
      if (level.intValue() >= 1000) {
        super.log(level, msg, thrown);
      }
    }

    @Override
    public void log(LogRecord record) {
      if (record.getLevel().intValue() >= 1000) {
        super.log(record);
      }
    }

    @Override
    public void severe(String msg) {
      super.severe(msg);
    }

    @Override
    public void warning(String msg) {
      // Do nothing
    }
  }

  /**
   * Implementation for the Logger class when logging is enabled only at Warning and above.
   */
  private static class LoggerImplWarning extends LoggerImplRegular {
    @Override
    public void config(String msg) {
      // Do nothing
    }

    @Override
    public void fine(String msg) {
      // Do nothing
    }

    @Override
    public void finer(String msg) {
      // Do nothing
    }

    @Override
    public void finest(String msg) {
      // Do nothing
    }

    @Override
    public void info(String msg) {
      // Do nothing
    }

    @Override
    public void log(Level level, String msg) {
      if (level.intValue() >= Level.WARNING.intValue()) {
        super.log(level, msg);
      }
    }

    @Override
    public void log(Level level, String msg, Throwable thrown) {
      if (level.intValue() >= Level.WARNING.intValue()) {
        super.log(level, msg, thrown);
      }
    }

    @Override
    public void log(LogRecord record) {
      if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
        super.log(record);
      }
    }

    @Override
    public void severe(String msg) {
      super.severe(msg);
    }

    @Override
    public void warning(String msg) {
      super.warning(msg);
    }
  }

  public static final String GLOBAL_LOGGER_NAME = "global";

  public static Logger getGlobal() {
    return getLogger(GLOBAL_LOGGER_NAME);
  }

  private static LoggerImpl createLoggerImpl() {
    String logginEnabled = System.getProperty("gwt.logging.enabled");
    if ("TRUE".equals(logginEnabled)) {
      return new LoggerImplRegular();
    } else if ("SEVERE".equals(logginEnabled)) {
      return new LoggerImplSevere();
    } else if ("WARNING".equals(logginEnabled)) {
      return new LoggerImplWarning();
    }
    return new LoggerImplNull();
  }
  
  public static Logger getLogger(String name) {
    // Use shortcut if logging is disabled to avoid parent logger creations in LogManager
    if (createLoggerImpl() instanceof LoggerImplNull) {
      return new Logger(name, "");
    }
    return LogManager.getLogManager().ensureLogger(name);
  }

  private LoggerImpl impl;
  
  protected Logger(String name, String resourceName) {
    impl = createLoggerImpl();
    impl.setName(name);
  }

  public void addHandler(Handler handler) {
    impl.addHandler(handler);
  }
  
  public void config(String msg) {
    impl.config(msg);
  } 
   
  public void fine(String msg) {
    impl.fine(msg);
  } 
  
  public void finer(String msg) {
    impl.finer(msg);
  }
  
  public void finest(String msg) {
    impl.finest(msg);
  }
  
  public Handler[] getHandlers() {
    return impl.getHandlers();
  }
  
  public Level getLevel() {
    return impl.getLevel();
  } 
  
  public String getName() {
    return impl.getName();
  }
  
  public Logger getParent() {
    return impl.getParent();
  }
  
  public boolean getUseParentHandlers() {
    return impl.getUseParentHandlers();
  }
  
  public void info(String msg) {
    impl.info(msg);
  } 
  
  public boolean isLoggable(Level messageLevel) {
    return impl.isLoggable(messageLevel);
  }
  
  public void log(Level level, String msg) {
    impl.log(level, msg);
  }
  
  public void log(Level level, String msg, Throwable thrown) {
    impl.log(level, msg, thrown);
  }

  public void log(LogRecord record) {
    impl.log(record);
  }
  
  public void removeHandler(Handler handler) {
    impl.removeHandler(handler);
  }
  
  public void setLevel(Level newLevel) {
    impl.setLevel(newLevel);
  }
  
  public void setParent(Logger newParent) {
    impl.setParent(newParent);
  }
  
  public void setUseParentHandlers(boolean newUseParentHandlers) {
    impl.setUseParentHandlers(newUseParentHandlers);
  }
  
  public void severe(String msg) {
    impl.severe(msg);
  }
  
  public void warning(String msg) {
    impl.warning(msg);
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