/*
 * Copyright 2025 GWT Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.user.server.rpc.logging;

import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles creation of {@link RpcLogger}s and the initialization of the {@link RpcLoggerProvider},
 * using {@link ServiceLoader} to discover available providers.
 * <br />
 * The provider is initialized the first time a logger is requested. The provider is chosen in this
 * order:
 * <ol>
 *     <li>The first provider with a fully-qualified class name that matches the system property
 *         <code>gwt.rpc.logging</code></li>
 *     <li>The first provider for which {@link RpcLoggerProvider#isDefault()} returns
 *         <code>true</code></li>
 *     <li>The {@link ServletContextLoggerProvider}</li>
 * </ol>
 */
public class RpcLogManager {

  public static final String PROVIDER_PARAMETER_KEY = "gwt.rpc.logging";
  private static final ConcurrentHashMap<String, RpcLogger> loggers = new ConcurrentHashMap<>();
  private static volatile RpcLoggerProvider loggerProvider;
  private static final Object initLock = new Object();

  /**
   * Creates or retrieves a logger for the fully qualified name of the given class.
   * @param clazz the class for which to return a logger
   * @return a logger
   */
  public static RpcLogger getLogger(Class<?> clazz) {
    return loggers.computeIfAbsent(clazz.getName(),
        name -> new RpcLogger(name, getLoggerProvider()));
  }

  /**
   * Retrieves the current instance of the {@link RpcLoggerProvider}.
   * <br />
   * If the LoggerProvider is not yet initialized, initializes it in a thread-safe manner
   * and returns the resulting instance.
   * @return the current instance of the LoggerProvider
   */
  private static RpcLoggerProvider getLoggerProvider() {
    if (loggerProvider == null) {
      initialize();
    }
    return loggerProvider;
  }

  /**
   * Initializes the {@link RpcLoggerProvider}, attempting to use a name from a system property.
   * <br />
   * If no name is supplied, it will use the first loaded provider for which
   * {@link RpcLoggerProvider#isDefault()} returns true.
   * <br />
   * If no such provider is found, it will fall back to {@link ServletContextLoggerProvider}.
   * <br />
   * If the provider is already initialized, no action is taken.
   */
  private static void initialize() {
    String providerName = System.getProperty(PROVIDER_PARAMETER_KEY);
    synchronized (initLock) {
      if (loggerProvider == null) {
        RpcLoggerProvider fallback = new ServletContextLoggerProvider();
        loggerProvider = loadProvider(providerName, fallback);
      }
    }
  }

  /**
   * Loads available providers and chooses the first whose class matches the given name, or the
   * first for which {@link RpcLoggerProvider#isDefault()} returns <code>true</code>.
   * @param name the fully qualified class name of the {@link RpcLoggerProvider} to use. May be
   * <code>null</code>.
   * @param fallback the provider to use in case no named or default provider is found
   * @return the chosen provider
   */
  private static RpcLoggerProvider loadProvider(String name, RpcLoggerProvider fallback) {
    ServiceLoader<RpcLoggerProvider> loaderService = ServiceLoader.load(RpcLoggerProvider.class);
    for (RpcLoggerProvider provider : loaderService) {
      if (provider.getClass().getName().equals(name)) {
        return provider;
      }
    }
    for (RpcLoggerProvider provider : loaderService) {
      if (provider.isDefault()) {
        return provider;
      }
    }
    return fallback;
  }

}
