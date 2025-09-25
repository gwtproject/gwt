package com.google.gwt.user.server.rpc.logging;

import java.lang.reflect.Method;

class ReflectiveLogger implements LoggerDelegate {

    private final Object logger;
    private final Object infoLevel;
    private final Object warnLevel;
    private final Object errorLevel;
    private final Method logMessage;
    private final Method logThrowable;

    public ReflectiveLogger(Object platformLogger, Object infoLevel, Object warnLevel, Object errorLevel, Method logMessage, Method logThrowable) {
        this.logger = platformLogger;
        this.infoLevel = infoLevel;
        this.warnLevel = warnLevel;
        this.errorLevel = errorLevel;
        this.logMessage = logMessage;
        this.logThrowable = logThrowable;
    }

    @Override
    public void info(String message) {
        try {
            logMessage.invoke(logger, infoLevel, message);
        } catch (Exception e) {
            System.err.println("Failed to log message:" + message);
            e.printStackTrace();
        }
    }

    @Override
    public void warn(String message) {
        try {
            logMessage.invoke(logger, warnLevel, message);
        } catch (Exception e) {
            System.err.println("Failed to log message:" + message);
            e.printStackTrace();
        }
    }

    @Override
    public void error(String message) {
        try {
            logMessage.invoke(logger, errorLevel, message);
        } catch (Exception e) {
            System.err.println("Failed to log message:" + message);
            e.printStackTrace();
        }
    }

    @Override
    public void error(String message, Throwable throwable) {
        try {
            logThrowable.invoke(logger, errorLevel, message, throwable);
        } catch (Exception e) {
            System.err.println("Failed to log message:" + message);
            e.printStackTrace();
            System.err.println("Original exception:");
            throwable.printStackTrace();
        }
    }

}
