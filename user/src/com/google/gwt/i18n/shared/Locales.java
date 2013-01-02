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
package com.google.gwt.i18n.shared;

/**
 * Information about the complete set of locales the application is built for,
 * along with configuration information about how to determine the selected
 * locale (such as cookies, query parameters, etc).
 * <p>
 * <b>Note:</b> currently this is backed by just a stub implementation on the
 * server.
 */
public interface Locales {

  /**
   * Returns an array of available locale names.
   * <p>
   * Note that in client code, only locales that the app has been compiled with
   * will be available.
   */
  String[] getAvailableLocaleNames();

  /**
   * Return a {@link LocaleInfo} instance for a requested locale, which must be
   * one returned from {@link #getAvailableLocaleNames()}.
   * <p>
   * <i><b>NOTE:</b> calling this method in client code will result in a
   * significant increase in the compiled application size, as large tables for
   * all compiled locales will be included.</i>
   * 
   * @param localeName
   * @return {@link LocaleInfo} instance, or null if unavailable
   */
  LocaleInfo getLocale(String localeName);

  /**
   * Returns the name of the name of the cookie holding the locale to use,
   * which is defined in the config property {@code locale.cookie}.
   * 
   * @return locale cookie name, or null if none
   */
  String getLocaleCookieName();

  /**
   * Returns the display name of the requested locale in its native locale, if
   * possible. If no native localization is available, the English name will
   * be returned, or as a last resort just the locale name will be returned.
   * If the locale name is unknown (including any user overrides) or is not a
   * valid locale name, null is returned.
   * <p>
   * Note that in client code, only locales that the app has been compiled with
   * will be available.
   * 
   * @param localeName the name of the locale to lookup.
   * @return the name of the locale in its native locale
   */
  String getLocaleNativeDisplayName(String localeName);

  /**
   * Returns the name of the query parameter holding the locale to use, which is
   * defined in the config property {@code locale.queryparam}.
   * 
   * @return locale URL query parameter name, or null if none
   */
  String getLocaleQueryParam();

  /**
   * Returns true if any locale supported by this build of the app is RTL.
   */
  boolean hasAnyRtl();
}
