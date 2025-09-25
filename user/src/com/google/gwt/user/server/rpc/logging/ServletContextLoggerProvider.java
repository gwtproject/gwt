package com.google.gwt.user.server.rpc.logging;

import javax.servlet.ServletContext;

public class ServletContextLoggerProvider implements LoggerProvider {

    private final ServletContext servletContext;

    public ServletContextLoggerProvider(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public LoggerDelegate createLogger(String name) {
        return new ServletContextLogger(servletContext);
    }

    private static final class ServletContextLogger implements LoggerDelegate {

        private final ServletContext servletContext;

        public ServletContextLogger(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        @Override
        public void info(String message) {
            servletContext.log(message);
        }

        @Override
        public void warn(String message) {
            servletContext.log("WARNING: " + message);
        }

        @Override public void error(String message) {
            servletContext.log("ERROR: " + message);
        }

        @Override
        public void error(String message, Throwable throwable) {
            servletContext.log(message, throwable);
        }

    }

}
