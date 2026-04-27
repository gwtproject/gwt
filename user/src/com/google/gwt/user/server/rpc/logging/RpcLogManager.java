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

import javax.servlet.ServletContext;

/**
 * Handles creation of {@link RpcLogger}s and the initialization of the {@link RpcLoggerProvider},
 * using {@link ServiceLoader} to discover available providers.
 * <br />
 * The provider is initialized the first time a logger is requested. The provider is chosen in this
 * order:
 * <ol>
 *   <li>The first provider with a fully-qualified class name that matches the system property with
 *       the key {@link #PROVIDER_PROPERTY_KEY} (<code>gwt.rpc.logging</code>)</li>
 *   <li>The first provider for which {@link RpcLoggerProvider#isDefault()} returns
 *       <code>true</code></li>
 *   <li>The {@link ServletContextLoggerProvider}</li>
 * </ol>
 */
public class RpcLogManager {

  /**
   * System property key for selecting an {@link RpcLoggerProvider} by fully-qualified class name.
   */
  public static final String PROVIDER_PROPERTY_KEY = "gwt.rpc.logging";
  private static final ConcurrentHashMap<String, RpcLogger> loggers = new ConcurrentHashMap<>();
  private static final RpcLoggerProvider loggerProvider = loadProvider();

  /**
   * Creates or retrieves a logger for the fully-qualified name of the given class.
   * @param clazz the class for which to return a logger
   * @return a logger
   */
  public static RpcLogger getLogger(Class<?> clazz) {
    return loggers.computeIfAbsent(clazz.getName(), loggerProvider::createLogger);
  }

  /**
   * Sets the servlet context of the {@link ServletContextLoggerProvider} to use for logging. Has no
   * effect if the provider is not a {@link ServletContextLoggerProvider}.
   * @param servletContext the servlet context to use
   */
  public static void setServletContext(ServletContext servletContext) {
    if (loggerProvider instanceof ServletContextLoggerProvider) {
      ((ServletContextLoggerProvider) loggerProvider).setServletContext(servletContext);
    }
  }

  /**
   * Loads available providers and chooses the first whose class matches the name given with the
   * {@link #PROVIDER_PROPERTY_KEY} system property, or, failing that, the first for which
   * {@link RpcLoggerProvider#isDefault()} returns <code>true</code>. If none found, returns a
   * {@link ServletContextLoggerProvider} as fallback.
   * @return a logger provider
   */
  private static RpcLoggerProvider loadProvider() {
    String providerClassName = System.getProperty(PROVIDER_PROPERTY_KEY);
    ServiceLoader<RpcLoggerProvider> loaderService = ServiceLoader.load(RpcLoggerProvider.class);
    for (RpcLoggerProvider provider : loaderService) {
      if (provider.getClass().getName().equals(providerClassName)) {
        return provider;
      }
    }
    for (RpcLoggerProvider provider : loaderService) {
      if (provider.isDefault()) {
        return provider;
      }
    }
    return new ServletContextLoggerProvider();
  }

  private RpcLogManager() {
    // Not instantiable
  }

}
