package com.google.gwt.user.server.rpc.logging;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class LogManager {

    private static volatile List<LoggerProvider> providers = null;
    private static volatile ServletContext servletContext = null;
    private static final ConcurrentHashMap<String, Logger> loggers = new ConcurrentHashMap<>();
    private static final AtomicReference<LoggerProvider> loggerProvider = new AtomicReference<>();
    private static final Object initLock = new Object();

    public static Logger getLogger(Class<?> clazz) {
        return loggers.computeIfAbsent(clazz.getName(), Logger::new);
    }

    static LoggerProvider getLoggerProvider() {
        loggerProvider.compareAndSet(null, new JulLoggerProvider());
        LoggerProvider provider = loggerProvider.get();
        if (provider == null) {
            synchronized (initLock) {
                provider = loggerProvider.get();
                if (provider == null) {
                    initialize();
                    provider = loggerProvider.get();
                }
            }
        }
        return provider;
    }

    public static void initialize() {
        initialize(null, null);
    }

    public static void initialize(String providerName) {
        initialize(providerName, null);
    }

    public static void initialize(String providerName, ServletContext servletContext) {
        synchronized (initLock) {
            if (servletContext != null) {
                LogManager.servletContext = servletContext;
            }
            LoggerProvider fallback = servletContext != null ?
                    new ServletContextLoggerProvider(LogManager.servletContext) :
                    new JulLoggerProvider();
            LoggerProvider provider = chooseProvider(providerName, fallback);
            setupProvider(provider);
        }
    }

    private static LoggerProvider chooseProvider(String name, LoggerProvider fallback) {
        if (providers == null) {
            providers = loadAllProviders();
        }
        for (LoggerProvider provider : providers) {
            if (provider.isDefault() || provider.getClass().getName().equals(name)) {
                return provider;
            }
        }
        return fallback;
    }

    private static void setupProvider(LoggerProvider provider) {
        LoggerProvider currentProvider = loggerProvider.get();
        String currentProviderName = currentProvider != null ? currentProvider.getClass().getName() : null;
        if (!provider.getClass().getName().equals(currentProviderName)) {
            loggerProvider.set(provider);
            loggers.clear();
        }
    }

    private static List<LoggerProvider> loadAllProviders() {
        List<LoggerProvider> providers = new ArrayList<>();
        Set<String> loaded = new HashSet<>();
        ClassLoader[] loaders = new ClassLoader[] {
                Thread.currentThread().getContextClassLoader(),
                LogManager.class.getClassLoader(),
                ClassLoader.getSystemClassLoader()
        };

        for (ClassLoader loader : loaders) {
            if (loader == null) {
                continue;
            }
            ServiceLoader<LoggerProvider> loaderService = ServiceLoader.load(LoggerProvider.class, loader);
            for (LoggerProvider provider : loaderService) {
                if (provider.isAvailable() && !loaded.contains(provider.getClass().getName())) {
                    loaded.add(provider.getClass().getName());
                    providers.add(provider);
                }
            }
        }

        return providers;
    }

}
