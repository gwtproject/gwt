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
package com.google.gwt.i18n.shared.impl;

import com.google.gwt.core.client.GwtScriptOnly;

/**
 * A helper method that uses gets a string from the digits of a double.  This
 * is the pure Java version.
 */
@GwtScriptOnly
public class NumberFormatHelper {

  /**
   * Convert a double to a string with {@code digits} precision.  The resulting
   * string may still be in exponential notation.
   *
   * @param d double value
   * @param digits number of digits of precision to include
   * @return non-localized string representation of {@code d}
   */
  static native String toPrecision(double d, int digits) /*-{
    return d.toPrecision(digits);
  }-*/;
}
