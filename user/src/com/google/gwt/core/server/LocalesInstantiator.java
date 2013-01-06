/*
 * Copyright 2012 Google Inc.
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
package com.google.gwt.core.server;

import com.google.gwt.core.server.ServerGwtBridge.ClassInstantiator;
import com.google.gwt.core.server.ServerGwtBridge.Properties;
import com.google.gwt.i18n.shared.Locales;

/**
 * Creates an instance of the {@link Locales} interface.
 */
class LocalesInstantiator implements ClassInstantiator {
  /* 
   * TODO(jat): tie in with the output of a linker to get the actual set of
   * supportted locales and configuration property values.
   */

  @Override
  public <T> T create(Class<?> baseClass, Properties properties) {
    if (!Locales.class.equals(baseClass)) {
      return null;
    }
    @SuppressWarnings("unchecked")
    T obj = (T) new LocalesImpl(ServerGwtBridge.getLocaleFactory());
    return obj;
  }
}
