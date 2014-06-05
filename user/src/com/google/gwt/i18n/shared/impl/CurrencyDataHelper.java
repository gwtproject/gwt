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

/**
 * Helper for {@code CurrencyData} implementations, so code can be shared between
 * Java and JS implementations.
 * 
 * THIS CLASS IS AN IMPLEMENTATION DETAIL AND SHOULD NOT BE RELIED UPON.
 */
public final class CurrencyDataHelper {

  /**
   * Bit-field values.
   */
  public static final int DEPRECATED_FLAG = 128;
  public static final int POS_FIXED_FLAG = 16;
  public static final int POS_SUFFIX_FLAG = 8;
  public static final int PRECISION_MASK = 7;
  public static final int SPACE_FORCED_FLAG = 32;
  public static final int SPACING_FIXED_FLAG = 64;

  public static int getDefaultFractionDigits(int flagsAndPrecision) {
    return flagsAndPrecision & PRECISION_MASK;
  }

  public static boolean isDeprecated(int flagsAndPrecision) {
    return (flagsAndPrecision & DEPRECATED_FLAG) != 0;
  }

  public static boolean isSpaceForced(int flagsAndPrecision) {
    return (flagsAndPrecision & SPACE_FORCED_FLAG) != 0;
  }

  public static boolean isSpacingFixed(int flagsAndPrecision) {
    return (flagsAndPrecision & SPACING_FIXED_FLAG) != 0;
  }

  public static boolean isSymbolPositionFixed(int flagsAndPrecision) {
    return (flagsAndPrecision & POS_FIXED_FLAG) != 0;
  }

  public static boolean isSymbolPrefix(int flagsAndPrecision) {
    return (flagsAndPrecision & POS_SUFFIX_FLAG) != 0;
  }
  
  public static int encode(int fractionDigits, boolean deprecated, boolean suffixPosition,
      boolean fixedPosition, boolean spaceForced, boolean spacingFixed) {
    int val = fractionDigits & PRECISION_MASK;
    if (deprecated) val |= DEPRECATED_FLAG;
    if (suffixPosition) val |= POS_SUFFIX_FLAG;
    if (fixedPosition) val |= POS_FIXED_FLAG;
    if (spaceForced) val |= SPACE_FORCED_FLAG;
    if (spacingFixed) val |= SPACING_FIXED_FLAG;
    return val;
  }
}
