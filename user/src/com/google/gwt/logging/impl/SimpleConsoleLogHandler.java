package com.google.gwt.logging.impl;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.core.client.impl.ConsoleLogger;

class SimpleConsoleLogHandler extends Handler {

  @Override
  public void publish(LogRecord record) {
    ConsoleLogger console = ConsoleLogger.createIfSupported();
    if (console == null) {
      return;
    }
    if (!isLoggable(record)) {
      return;
    }

    String level = toConsoleLogLevel(record.getLevel());
    console.log(level, record.getMessage());
    if (record.getThrown() != null) {
      console.log(level, record.getThrown());
    }
  }

  private String toConsoleLogLevel(Level level) {
    int val = level.intValue();
    if (val >= Level.SEVERE.intValue()) {
      return "error";
    } else if (val >= Level.WARNING.intValue()) {
      return "warn";
    } else if (val >= Level.INFO.intValue()) {
      return "info";
    } else {
      return "log";
    }
  }

  @Override
  public void close() {
    // No action needed
  }

  @Override
  public void flush() {
    // No action needed
  }
}