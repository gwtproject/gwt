package com.google.gwt.user.server.rpc.logging;

import java.lang.reflect.Method;

abstract class ReflectiveLoggerProvider implements LoggerProvider {

    protected Method createLogger;
    protected Object infoLevel;
    protected Object warnLevel;
    protected Object errorLevel;
    protected Method logMessage;
    protected Method logThrowable;
    protected boolean available;

    @Override
    public LoggerDelegate createLogger(String name) {
        try {
            Object logger = createLogger.invoke(null, name);
            return new ReflectiveLogger(logger, infoLevel, warnLevel, errorLevel, logMessage, logThrowable);
        } catch (Exception e) {
            throw new IllegalStateException(getClass() + " is not available on this platform", e);
        }
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    protected Object getLogLevel(Class<?> levelClass, String levelName) {
        for (Object level : levelClass.getEnumConstants()) {
            if (level.toString().equals(levelName)) {
                return level;
            }
        }
        return null;
    }

}
