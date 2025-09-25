package com.google.gwt.user.server.rpc.logging;

import java.lang.reflect.Method;

public class Log4jProvider extends ReflectiveLoggerProvider implements LoggerProvider {

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
