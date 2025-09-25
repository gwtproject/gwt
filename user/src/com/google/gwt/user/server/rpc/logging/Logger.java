package com.google.gwt.user.server.rpc.logging;

import java.util.concurrent.atomic.AtomicReference;

public class Logger {

    private final AtomicReference<LoggerDelegate> delegate = new AtomicReference<>();
    private final String name;

    public Logger(String name) {
        this.name = name;
    }

    public void info(String message) {
        getDelegate().info(message);
    }

    public void warn(String message) {
        getDelegate().warn(message);
    }

    public void error(String message) {
        getDelegate().error(message);
    }

    public void error(String message, Throwable throwable) {
        getDelegate().error(message, throwable);
    }

    private LoggerDelegate getDelegate() {
        LoggerDelegate delegate = this.delegate.get();
        if (delegate == null) {
            delegate = createDelegate();
        }
        return delegate;
    }

    private LoggerDelegate createDelegate() {
        LoggerProvider provider = LogManager.getLoggerProvider();
        LoggerDelegate newDelegate = provider.createLogger(name);
        return delegate.compareAndSet(null, newDelegate) ? newDelegate : delegate.get();
    }

}
