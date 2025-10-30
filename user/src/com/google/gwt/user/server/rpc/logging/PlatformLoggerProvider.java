package com.google.gwt.user.server.rpc.logging;

public class PlatformLoggerProvider extends ReflectiveLoggerProvider implements LoggerProvider {

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
