package com.google.gwt.user.server.rpc.logging;

public interface LoggerProvider {

    LoggerDelegate createLogger(String name);

    default boolean isDefault() {
        return false;
    }

    default boolean isAvailable() {
        return true;
    }

}
