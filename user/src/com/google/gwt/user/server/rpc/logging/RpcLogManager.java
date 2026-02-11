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
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletConfig;

/**
 * Handles creation of {@link RpcLogger}s and the initialization of the {@link RpcLoggerProvider},
 * using {@link ServiceLoader} to discover available providers.
 * <br />
 * Can be manually initialized with a specific {@link RpcLoggerProvider} using an object or a fully
 * qualified class name. If not manually initialized, it will lazily initialize, using a provider
 * chosen in this order:
 * <ol>
 *     <li>The first provider with a class name that matches a system property
 *         <code>gwt.rpc.logging</code></li>
 *     <li>The first provider for which {@link RpcLoggerProvider#isDefault()} returns
 *         <code>true</code></li>
 *     <li>The {@link ServletContextLoggerProvider}</li>
 * </ol>
 * Included providers can be found in
 * <code>META-INF/services/com.google.gwt.user.server.rpc.logging.RpcLoggerProvider</code>
 */
public class RpcLogManager {

  public static final String PROVIDER_PARAMETER_KEY = "gwt.rpc.logging";
  private static final ConcurrentHashMap<String, RpcLogger> loggers = new ConcurrentHashMap<>();
  private static final AtomicReference<RpcLoggerProvider> loggerProvider = new AtomicReference<>();
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
    RpcLoggerProvider result = loggerProvider.get();
    if (result == null) {
      initialize();
      result = loggerProvider.get();
    }
    return result;
  }

  /**
   * Initializes the {@link RpcLoggerProvider} using the value of the system property.
   */
  public static void initialize() {
    initialize(System.getProperty(PROVIDER_PARAMETER_KEY));
  }

  /**
   * Initializes the {@link RpcLoggerProvider} with the given name.
   * <br />
   * If no name is supplied, it will use the first loaded provider for which
   * {@link RpcLoggerProvider#isDefault()} returns true.
   * <br />
   * If no such provider is found, it will fall back to {@link ServletContextLoggerProvider}.
   * <br />
   * The provider will be initialized only once.
   * @param providerName the fully qualified class name of the RpcLoggerProvider to use
   */
  public static void initialize(String providerName) {
    if (loggerProvider.get() == null) {
      synchronized (initLock) {
        if (loggerProvider.get() == null) {
          RpcLoggerProvider fallback = new ServletContextLoggerProvider();
          RpcLoggerProvider provider = loadProvider(providerName, fallback);
          loggerProvider.set(provider);
        }
      }
    }
  }

  /**
   * Initializes the {@link RpcLoggerProvider} with the given provider.
   * <br />
   * The provider will be initialized only once.
   * @param provider the logger provider to use
   */
  public static void initialize(RpcLoggerProvider provider) {
    if (loggerProvider.get() == null) {
      synchronized (initLock) {
        if (loggerProvider.get() == null) {
          loggerProvider.set(provider);
        }
      }
    }
  }

  /**
   * Attempts to retrieve a fully qualified class name for a
   * {@link com.google.gwt.user.server.rpc.logging.RpcLoggerProvider}, checking system properties
   * and init parameters for <code>gwt.rpc.logging</code>.
   * @param config the servlet config
   * @return the fully qualified class name of the provider, or <code>null</code> if none was found.
   */
  public static String getProviderName(ServletConfig config) {
    String parameterName = RpcLogManager.PROVIDER_PARAMETER_KEY;
    if (System.getProperty(parameterName) != null) {
      return System.getProperty(parameterName);
    } else if (config.getInitParameter(parameterName) != null) {
      return config.getInitParameter(parameterName);
    } else {
      return config.getServletContext().getInitParameter(parameterName);
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
      if (provider.isAvailable() && provider.getClass().getName().equals(name)) {
        return provider;
      }
    }
    for (RpcLoggerProvider provider : loaderService) {
      if (provider.isAvailable() && provider.isDefault()) {
        return provider;
      }
    }
    return fallback;
  }

}
