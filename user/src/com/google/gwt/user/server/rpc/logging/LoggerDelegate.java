package com.google.gwt.user.server.rpc.logging;

public interface LoggerDelegate {

    void info(String message);

    void warn(String message);

    void error(String message);

    void error(String message, Throwable throwable);

}
