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

/**
 * Service provider interface for obtaining {@link RpcLogger} instances.
 * <p>
 * This is not intended to be used directly, but through {@link RpcLogManager#getLogger(Class)},
 * which discovers implementations with a service loader
 *
 * @see RpcLogManager
 */
public interface RpcLoggerProvider {

  /**
   * Creates or retrieves a logger with the given name.
   *
   * @param name the name of the logger to create or retrieve
   * @return the created or retrieved logger
   */
  RpcLogger createLogger(String name);

  /**
   * Indicates whether this provider should be used as the default in the absence of a provider
   * explicitly selected with the {@link RpcLogManager#PROVIDER_PROPERTY_KEY} system property.
   *
   * @return true if this provider should be used when no named provider is found
   */
  default boolean isDefault() {
    return false;
  }

}
