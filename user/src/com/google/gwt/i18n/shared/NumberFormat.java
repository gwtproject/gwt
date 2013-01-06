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
 * Locale-aware number formatting.
 * <p>
 * <b>NOTE: other methods <i>will</i> be added to this interface, so don't
 * implement it directly if you mind being broken.</b>
 */
public interface NumberFormat {

  /**
   * This method formats a double to produce a string.
   *
   * @param number The double to format
   * @return the formatted number string
   */
  String format(double number);

  /**
   * This method formats a Number to produce a string.
   * <p>
   * Any {@link Number} which is not a {@link java.math.BigDecimal},
   * {@link java.math.BigInteger}, or {@link Long} instance is formatted as a
   * {@code double} value.
   *
   * @param number The Number instance to format
   * @return the formatted number string
   */
  String format(Number number);

  /**
   * Returns the pattern used by this number format.
   */
  String getPattern();

  /**
   * Create a new {@link NumberFormat} instance with a different number of fractional digits used
   * for formatting with this instance.
   * 
   * @param digits the exact number of fractional digits for formatted
   *     values; must be >= 0
   * @return a new {@link NumberFormat} instance 
   */
  NumberFormat overrideFractionDigits(int digits);

  /**
   * Create a new {@link NumberFormat} instance with a different number of fractional digits used
   * for formatting with this instance.  Digits after {@code minDigits} that are zero will be
   * omitted from the formatted value.
   * 
   * @param minDigits the minimum number of fractional digits for formatted
   *     values; must be >= 0
   * @param maxDigits the maximum number of fractional digits for formatted
   *     values; must be >= {@code minDigits}
   * @return a new {@link NumberFormat} instance 
   */
  NumberFormat overrideFractionDigits(int minDigits, int maxDigits);

  /**
   * Parses text to produce a numeric value. A {@link NumberFormatException} is
   * thrown if either the text is empty or if the parse does not consume all
   * characters of the text.
   *
   * @param text the string being parsed
   * @return a double value representing the parsed number
   * @throws NumberFormatException if the entire text could not be converted
   *     into a double
   */
  double parse(String text) throws NumberFormatException;

  /**
   * Parses text to produce a numeric value.
   *
   * <p>
   * The method attempts to parse text starting at the index given by pos. If
   * parsing succeeds, then the index of <code>pos</code> is updated to the
   * index after the last character used (parsing does not necessarily use all
   * characters up to the end of the string), and the parsed number is returned.
   * The updated <code>pos</code> can be used to indicate the starting point
   * for the next call to this method. If an error occurs, then the index of
   * <code>pos</code> is not changed.
   * </p>
   *
   * @param text the string to be parsed
   * @param inOutPos position to pass in and get back
   * @return a double value representing the parsed number
   * @throws NumberFormatException if the text segment could not be converted
   *     into a double
   */
  double parse(String text, int[] inOutPos) throws NumberFormatException;

}
