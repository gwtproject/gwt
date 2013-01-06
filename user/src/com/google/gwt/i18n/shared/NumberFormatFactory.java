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
 * A factory for creating {@link NumberFormat} instances.  All implementations
 * of this interface must be immutable (other than caching effects).
 * <h3>Patterns</h3>
 * <p>
 * Formatting and parsing are based on customizable patterns that can include a
 * combination of literal characters and special characters that act as
 * placeholders and are replaced by their localized counterparts. Many
 * characters in a pattern are taken literally; they are matched during parsing
 * and output unchanged during formatting. Special characters, on the other
 * hand, stand for other characters, strings, or classes of characters. For
 * example, the '<code>#</code>' character is replaced by a localized digit.
 * </p>
 *
 * <p>
 * Often the replacement character is the same as the pattern character. In the
 * U.S. locale, for example, the '<code>,</code>' grouping character is
 * replaced by the same character '<code>,</code>'. However, the replacement
 * is still actually happening, and in a different locale, the grouping
 * character may change to a different character, such as '<code>.</code>'.
 * Some special characters affect the behavior of the formatter by their
 * presence. For example, if the percent character is seen, then the value is
 * multiplied by 100 before being displayed.
 * </p>
 *
 * <p>
 * The characters listed below are used in patterns. Localized symbols use the
 * corresponding characters taken from corresponding locale symbol collection,
 * which can be found in the properties files residing in the
 * <code><nobr>com.google.gwt.i18n.client.constants</nobr></code>. To insert
 * a special character in a pattern as a literal (that is, without any special
 * meaning) the character must be quoted. There are some exceptions to this
 * which are noted below.
 * </p>
 *
 * <table>
 * <tr>
 * <th>Symbol</th>
 * <th>Location</th>
 * <th>Localized?</th>
 * <th>Meaning</th>
 * </tr>
 *
 * <tr>
 * <td><code>0</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>Digit</td>
 * </tr>
 *
 * <tr>
 * <td><code>#</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>Digit, zero shows as absent</td>
 * </tr>
 *
 * <tr>
 * <td><code>.</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>Decimal separator or monetary decimal separator</td>
 * </tr>
 *
 * <tr>
 * <td><code>-</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>Minus sign</td>
 * </tr>
 *
 * <tr>
 * <td><code>,</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>Grouping separator</td>
 * </tr>
 *
 * <tr>
 * <td><code>E</code></td>
 * <td>Number</td>
 * <td>Yes</td>
 * <td>Separates mantissa and exponent in scientific notation; need not be
 * quoted in prefix or suffix</td>
 * </tr>
 *
 * <tr>
 * <td><code>;</code></td>
 * <td>Subpattern boundary</td>
 * <td>Yes</td>
 * <td>Separates positive and negative subpatterns</td>
 * </tr>
 *
 * <tr>
 * <td><code>%</code></td>
 * <td>Prefix or suffix</td>
 * <td>Yes</td>
 * <td>Multiply by 100 and show as percentage</td>
 * </tr>
 *
 * <tr>
 * <td><nobr><code>\u2030</code> (\u005Cu2030)</nobr></td>
 * <td>Prefix or suffix</td>
 * <td>Yes</td>
 * <td>Multiply by 1000 and show as per mille</td>
 * </tr>
 *
 * <tr>
 * <td><nobr><code>\u00A4</code> (\u005Cu00A4)</nobr></td>
 * <td>Prefix or suffix</td>
 * <td>No</td>
 * <td>Currency sign, replaced by currency symbol; if doubled, replaced by
 * international currency symbol; if present in a pattern, the monetary decimal
 * separator is used instead of the decimal separator</td>
 * </tr>
 *
 * <tr>
 * <td><code>'</code></td>
 * <td>Prefix or suffix</td>
 * <td>No</td>
 * <td>Used to quote special characters in a prefix or suffix; for example,
 * <code>"'#'#"</code> formats <code>123</code> to <code>"#123"</code>;
 * to create a single quote itself, use two in succession, such as
 * <code>"# o''clock"</code></td>
 * </tr>
 *
 * </table>
 *
 * <p>
 * A <code>NumberFormat</code> pattern contains a postive and negative
 * subpattern separated by a semicolon, such as
 * <code>"#,##0.00;(#,##0.00)"</code>. Each subpattern has a prefix, a
 * numeric part, and a suffix. If there is no explicit negative subpattern, the
 * negative subpattern is the localized minus sign prefixed to the positive
 * subpattern. That is, <code>"0.00"</code> alone is equivalent to
 * <code>"0.00;-0.00"</code>. If there is an explicit negative subpattern, it
 * serves only to specify the negative prefix and suffix; the number of digits,
 * minimal digits, and other characteristics are ignored in the negative
 * subpattern. That means that <code>"#,##0.0#;(#)"</code> has precisely the
 * same result as <code>"#,##0.0#;(#,##0.0#)"</code>.
 * </p>
 *
 * <p>
 * The prefixes, suffixes, and various symbols used for infinity, digits,
 * thousands separators, decimal separators, etc. may be set to arbitrary
 * values, and they will appear properly during formatting. However, care must
 * be taken that the symbols and strings do not conflict, or parsing will be
 * unreliable. For example, the decimal separator and thousands separator should
 * be distinct characters, or parsing will be impossible.
 * </p>
 *
 * <p>
 * The grouping separator is a character that separates clusters of integer
 * digits to make large numbers more legible. It commonly used for thousands,
 * but in some locales it separates ten-thousands. The grouping size is the
 * number of digits between the grouping separators, such as 3 for "100,000,000"
 * or 4 for "1 0000 0000".
 * </p>
 *
 * <h3>Pattern Grammar (BNF)</h3>
 * <p>
 * The pattern itself uses the following grammar:
 * </p>
 *
 * <table>
 * <tr>
 * <td>pattern</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">subpattern ('<code>;</code>'
 * subpattern)?</td>
 * </tr>
 * <tr>
 * <td>subpattern</td>
 * <td>:=</td>
 * <td>prefix? number exponent? suffix?</td>
 * </tr>
 * <tr>
 * <td>number</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">(integer ('<code>.</code>' fraction)?) |
 * sigDigits</td>
 * </tr>
 * <tr>
 * <td>prefix</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">'<code>\u005Cu0000</code>'..'<code>\u005CuFFFD</code>' -
 * specialCharacters</td>
 * </tr>
 * <tr>
 * <td>suffix</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">'<code>\u005Cu0000</code>'..'<code>\u005CuFFFD</code>' -
 * specialCharacters</td>
 * </tr>
 * <tr>
 * <td>integer</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">'<code>#</code>'* '<code>0</code>'*'<code>0</code>'</td>
 * </tr>
 * <tr>
 * <td>fraction</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">'<code>0</code>'* '<code>#</code>'*</td>
 * </tr>
 * <tr>
 * <td>sigDigits</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">'<code>#</code>'* '<code>@</code>''<code>@</code>'* '<code>#</code>'*</td>
 * </tr>
 * <tr>
 * <td>exponent</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">'<code>E</code>' '<code>+</code>'? '<code>0</code>'* '<code>0</code>'</td>
 * </tr>
 * <tr>
 * <td>padSpec</td>
 * <td>:=</td>
 * <td style="white-space: nowrap">'<code>*</code>' padChar</td>
 * </tr>
 * <tr>
 * <td>padChar</td>
 * <td>:=</td>
 * <td>'<code>\u005Cu0000</code>'..'<code>\u005CuFFFD</code>' - quote</td>
 * </tr>
 * </table>
 *
 * <p>
 * Notation:
 * </p>
 *
 * <table>
 * <tr>
 * <td>X*</td>
 * <td style="white-space: nowrap">0 or more instances of X</td>
 * </tr>
 *
 * <tr>
 * <td>X?</td>
 * <td style="white-space: nowrap">0 or 1 instances of X</td>
 * </tr>
 *
 * <tr>
 * <td>X|Y</td>
 * <td style="white-space: nowrap">either X or Y</td>
 * </tr>
 *
 * <tr>
 * <td>C..D</td>
 * <td style="white-space: nowrap">any character from C up to D, inclusive</td>
 * </tr>
 *
 * <tr>
 * <td>S-T</td>
 * <td style="white-space: nowrap">characters in S, except those in T</td>
 * </tr>
 * </table>
 *
 * <p>
 * The first subpattern is for positive numbers. The second (optional)
 * subpattern is for negative numbers.
 * </p>
 *
 *  <h3>Example</h3> {@example com.google.gwt.examples.NumberFormatExample}
 */
public interface NumberFormatFactory {

  /**
   * Provides the standard currency format for the current locale.
   *
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the default locale
   */
  NumberFormat getCurrencyFormat();

  /**
   * Provides the standard currency format for the current locale using a
   * specified currency.
   *
   * @param currencyData currency data to use
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   */
  NumberFormat getCurrencyFormat(CurrencyData currencyData);

  /**
   * Provides the standard currency format for the current locale using a
   * specified currency.
   *
   * @param currencyCode valid currency code, as defined in
   *     com.google.gwt.i18n.client.constants.CurrencyCodeMapConstants.properties
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   * @throws IllegalArgumentException if the currency code is unknown
   */
  NumberFormat getCurrencyFormat(String currencyCode);

  /**
   * Provides the standard decimal format for the default locale.
   *
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         decimal format for the default locale
   */
  NumberFormat getDecimalFormat();

  /**
   * Gets a <code>NumberFormat</code> instance for the default locale using
   * the specified pattern and the default currencyCode.
   *
   * @param pattern pattern for this formatter
   * @return a NumberFormat instance
   * @throws IllegalArgumentException if the specified pattern is invalid
   */
  NumberFormat getFormat(String pattern);

  /**
   * Gets a custom <code>NumberFormat</code> instance for the default locale
   * using the specified pattern and currency code.
   *
   * @param pattern pattern for this formatter
   * @param currencyData currency data
   * @return a NumberFormat instance
   * @throws IllegalArgumentException if the specified pattern is invalid
   */
  NumberFormat getFormat(String pattern, CurrencyData currencyData);

  /**
   * Gets a custom <code>NumberFormat</code> instance for the default locale
   * using the specified pattern and currency code.
   *
   * @param pattern pattern for this formatter
   * @param currencyCode international currency code
   * @return a NumberFormat instance
   * @throws IllegalArgumentException if the specified pattern is invalid
   *     or the currency code is unknown
   */
  NumberFormat getFormat(String pattern, String currencyCode);

  /**
   * Provides the global currency format for the current locale, using its
   * default currency.
   * 
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   */
  NumberFormat getGlobalCurrencyFormat();

  /**
   * Provides the global currency format for the current locale, using a
   * specified currency.
   *
   * @param currencyData currency data to use
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   */
  NumberFormat getGlobalCurrencyFormat(CurrencyData currencyData);
  
  /**
   * Provides the global currency format for the current locale, using a
   * specified currency.
   *
   * @param currencyCode valid currency code, as defined in
   *     com.google.gwt.i18n.client.constants.CurrencyCodeMapConstants.properties
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   * @throws IllegalArgumentException if the currency code is unknown
   */
  NumberFormat getGlobalCurrencyFormat(String currencyCode);

  /**
   * Provides the standard percent format for the default locale.
   *
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         percent format for the default locale
   */
  NumberFormat getPercentFormat();

  /**
   * Provides the standard scientific format for the default locale.
   *
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         scientific format for the default locale
   */
  NumberFormat getScientificFormat();

  /**
   * Provides the simple currency format for the current locale using its
   * default currency. Note that these formats may be ambiguous if the
   * currency isn't clear from other content on the page.
   *
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   */
  NumberFormat getSimpleCurrencyFormat();

  /**
   * Provides the simple currency format for the current locale using a
   * specified currency. Note that these formats may be ambiguous if the
   * currency isn't clear from other content on the page.
   *
   * @param currencyData currency data to use
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   */
  NumberFormat getSimpleCurrencyFormat(CurrencyData currencyData);

  /**
   * Provides the simple currency format for the current locale using a
   * specified currency. Note that these formats may be ambiguous if the
   * currency isn't clear from other content on the page.
   * 
   * @param currencyCode valid currency code, as defined in
   *        com.google.gwt.i18n.client
   *        .constants.CurrencyCodeMapConstants.properties
   * @return a <code>NumberFormat</code> capable of producing and consuming
   *         currency format for the current locale
   * @throws IllegalArgumentException if the currency code is unknown
   */
  NumberFormat getSimpleCurrencyFormat(String currencyCode);
}
