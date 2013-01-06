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

import com.google.gwt.core.server.ServerGwtBridge.CachingClassInstantiatorBase;
import com.google.gwt.i18n.shared.GwtLocale;

/**
 * Instantiator that knows how to lookup locale-specific implementations of
 * CLDR-derived classes, which are <b>required</b> to be immutable and therefore
 * cacheable.
 * <p>
 * For {@code pkg.Class}, it tries {@code pkg.cldr.ClassImpl_locale} (including
 * nested classes), following the inheritance chain for the requested locale.
 */
public class CldrInstantiator extends CachingClassInstantiatorBase {

  /**
   * Create an instance of a CLDR class.
   * 
   * @param baseClass
   * @param locale
   * @return the instance for the specified class and locale or null if not present
   */
  public static <T> T createInstance(Class<T> baseClass, GwtLocale locale) {
    Class<? extends T> clazz = findImplementation(baseClass, locale);
    try {
      return clazz.newInstance();
    } catch (InstantiationException e) {
    } catch (IllegalAccessException e) {
    }
    return null;
  }

  /**
   * Find an implementation of a CLDR class.
   * 
   * @param baseClass
   * @param locale
   * @return the instance for the specified class and locale or null if not present
   */
  public static <T> Class<? extends T> findImplementation(Class<T> baseClass, GwtLocale locale) {
    String pkgName = baseClass.getPackage().getName();
    String className = pkgName + ".cldr." + baseClass.getSimpleName() + "Impl";
    for (GwtLocale search : locale.getCompleteSearchList()) {
      String suffix = search.isDefault() ? "" : "_" + search.getAsString();
      try {
        @SuppressWarnings("unchecked")
        Class<? extends T> clazz = (Class<? extends T>) Class.forName(className + suffix);
        return clazz;
      } catch (ClassNotFoundException e) {
        // keep looking
      }
    }
    return null;
  }

  @Override
  protected <T> T createForLocale(Class<T> clazz, GwtLocale locale) {
    return createInstance(clazz, locale);
  }
}