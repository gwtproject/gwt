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

import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;

/**
 * A factory for creating {@link DateTimeFormatImpl} instances.  All
 * implementations of this interface must be immutable (other than caching
 * effects).
 */
public interface DateTimeFormatFactory {

  /**
   * Get a DateTimeFormat instance for a predefined format.
   * 
   * <p>See {@link CustomDateTimeFormat} if you need a localized format that is
   * not supported here.
   * 
   * @param predef {@link PredefinedFormat} describing desired format
   * @return a DateTimeFormat instance for the specified format
   */
  DateTimeFormat getFormat(PredefinedFormat predef);

  /**
   * Returns a DateTimeFormat object using the specified pattern. If you need to
   * format or parse repeatedly using the same pattern, it is highly recommended
   * that you cache the returned <code>DateTimeFormat</code> object and reuse it
   * rather than calling this method repeatedly.
   * 
   * <p>Note that the pattern supplied is used as-is -- for example, if you
   * supply "MM/dd/yyyy" as the pattern, that is the order you will get the
   * fields, even in locales where the order is different.  It is recommended to
   * use {@link #getFormat(PredefinedFormat)} instead -- if you use this method,
   * you are taking responsibility for localizing the patterns yourself.
   * 
   * @param pattern string to specify how the date should be formatted
   * 
   * @return a <code>DateTimeFormat</code> object that can be used for format or
   *         parse date/time values matching the specified pattern
   * 
   * @throws IllegalArgumentException if the specified pattern could not be
   *           parsed
   */
  DateTimeFormat getFormat(String pattern);
}
