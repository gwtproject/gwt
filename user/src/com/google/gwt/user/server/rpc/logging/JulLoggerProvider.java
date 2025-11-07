package com.google.gwt.user.server.rpc.logging;

import java.util.logging.Level;

public class JulLoggerProvider implements LoggerProvider {

    public JulLoggerProvider() { }

    @Override
    public LoggerDelegate createLogger(String name) {
        return new JulLogger(java.util.logging.Logger.getLogger(name));
    }

    private static final class JulLogger implements LoggerDelegate {
        private final java.util.logging.Logger logger;

        public JulLogger(java.util.logging.Logger logger) {
            this.logger = logger;
        }

        @Override
        public void info(String message) {
            logger.log(Level.INFO, message);
        }

        @Override
        public void warn(String message) {
            logger.log(Level.WARNING, message);
        }

        @Override
        public void error(String message) {
            logger.log(Level.SEVERE, message);
        }

        @Override
        public void error(String message, Throwable throwable) {
            logger.log(Level.SEVERE, message, throwable);
        }

    }

}
