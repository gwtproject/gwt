/*
 * Copyright 2008 Google Inc.
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

package java.lang;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Locale;

import javaemul.internal.annotations.DoNotInline;

/**
 * Intrinsic string class.
 *
 */

public final class String implements Comparable<String>, CharSequence,
    Serializable {
  /* TODO(jat): consider whether we want to support the following methods;
   *
   * <ul>
   * <li>deprecated methods dealing with bytes (I assume not since I can't see
   * much use for them)
   * <ul>
   * <li>String(byte[] ascii, int hibyte)
   * <li>String(byte[] ascii, int hibyte, int offset, int count)
   * <li>getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin)
   * </ul>
   * <li>methods which in JS will essentially do nothing or be the same as other
   * methods
   * <ul>
   * <li>copyValueOf(char[] data)
   * <li>copyValueOf(char[] data, int offset, int count)
   * </ul>
   * <li>other methods which are not straightforward in JS
   * <ul>
   * <li>format(String format, Object... args)
   * </ul>
   * </ul>
   *
   * <p>Also, in general, we need to improve our support of non-ASCII characters. The
   * problem is that correct support requires large tables, and we don't want to
   * make users who aren't going to use that pay for it. There are two ways to do
   * that:
   * <ol>
   * <li>construct the tables in such a way that if the corresponding method is
   * not called the table will be elided from the output.
   * <li>provide a deferred binding target selecting the level of compatibility
   * required. Those that only need ASCII (or perhaps a different relatively small
   * subset such as Latin1-5) will not pay for large tables, even if they do call
   * toLowercase(), for example.
   * </ol>
   *
   * Also, if we ever add multi-locale support, there are a number of other
   * methods such as toLowercase(Locale) we will want to consider supporting. This
   * is probably rare, but there will be some apps (such as a translation tool)
   * which cannot be written without this support.
   *
   * Another category of incomplete support is that we currently just use the JS
   * regex support, which is not exactly the same as Java. We should support Java
   * syntax by mapping it into equivalent JS patterns, or emulating them.
   *
   * IMPORTANT NOTE: if newer JREs add new interfaces to String, please update
   * {@link Devirtualizer} and {@link JavaResourceBase}
   */
  public static final Comparator<String> CASE_INSENSITIVE_ORDER = DevirtualizedString.CASE_INSENSITIVE_ORDER;

  public static String copyValueOf(char[] v) {
    return DevirtualizedString.valueOf(v);
  }

  public static String copyValueOf(char[] v, int offset, int count) {
    return DevirtualizedString.valueOf(v, offset, count);
  }

  public static String valueOf(boolean x) {
    return DevirtualizedString.valueOf(x);
  }

  public static String valueOf(char x) {
    return DevirtualizedString.valueOf(x);
  }

  public static String valueOf(char x[], int offset, int count) {
    return DevirtualizedString.valueOf(x, offset, count);
  }

  public static String valueOf(char[] x) {
    return DevirtualizedString.valueOf(x);
  }

  public static String valueOf(double x) {
    return DevirtualizedString.valueOf(x);
  }

  public static String valueOf(float x) {
    return DevirtualizedString.valueOf(x);
  }

  public static String valueOf(int x) {
    return DevirtualizedString.valueOf(x);
  }

  public static String valueOf(long x) {
    return DevirtualizedString.valueOf(x);
  }

  public static String valueOf(Object x) {
    return DevirtualizedString.valueOf(x);
  }

  // CHECKSTYLE_OFF: This class has special needs.

  /**
   * @skip
   */
  static String[] __createArray(int numElements) {
    return new String[numElements];
  }

  public String() {
    /*
     * Call to $createString(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $createString(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createString();
  }

  public String(byte[] bytes) {
    /*
     * Call to $createString(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $createString(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createString(bytes);
  }

  public String(byte[] bytes, int ofs, int len) {
    /*
     * Call to $createString(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $createString(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createString(bytes, ofs, len);
  }

  public String(byte[] bytes, int ofs, int len, String charsetName)
      throws UnsupportedEncodingException {
    /*
     * Call to $createString(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $createString(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createString(bytes, ofs, len, charsetName);
  }

  public String(byte[] bytes, int ofs, int len, Charset charset) {
    /*
     * Call to $createString(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $createString(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createString(bytes, ofs, len, charset);
  }

  public String(byte[] bytes, String charsetName)
      throws UnsupportedEncodingException {
    /*
     * Call to $createString(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $createString(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createString(bytes, charsetName);
  }

  public String(byte[] bytes, Charset charset)
      throws UnsupportedEncodingException {
    /*
     * Call to $createString(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $createString(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createString(bytes, charset);
  }

  public String(char value[]) {
    /*
     * Call to $createString(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $createString(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createString(value);
  }

  public String(char value[], int offset, int count) {
    /*
     * Call to $createString(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $createString(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createString(value, offset, count);
  }

  public String(int codePoints[], int offset, int count) {
    /*
     * Call to $createString(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $createString(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createString(codePoints, offset, count);
  }

  public String(String other) {
    /*
     * Call to $createString(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $createString(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createString(other);
  }

  public String(StringBuffer sb) {
    /*
     * Call to $createString(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $createString(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createString(sb);
  }

  public String(StringBuilder sb) {
    /*
     * Call to $createString(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $createString(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createString(sb);
  }

  @Override
  public char charAt(int index) {
    return DevirtualizedString.charAt(this, index);
  }

  public int codePointAt(int index) {
    return DevirtualizedString.codePointAt(this, index);
  }

  public int codePointBefore(int index) {
    return DevirtualizedString.codePointBefore(this, index);
  }

  public int codePointCount(int beginIndex, int endIndex) {
    return DevirtualizedString.codePointCount(this, beginIndex, endIndex);
  }

  @Override
  public int compareTo(String other) {
    return DevirtualizedString.compareTo(this, other);
  }

  public int compareToIgnoreCase(String other) {
    return DevirtualizedString.compareToIgnoreCase(this, other);
  }

  public String concat(String str) {
    return DevirtualizedString.concat(this, str);
  }

  public boolean contains(CharSequence s) {
    return DevirtualizedString.contains(this, s);
  }

  public boolean contentEquals(CharSequence cs) {
    return DevirtualizedString.contentEquals(this, cs);
  }

  public boolean contentEquals(StringBuffer sb) {
    return DevirtualizedString.contentEquals(this, sb);
  }

  public boolean endsWith(String suffix) {
    return DevirtualizedString.endsWith(this, suffix);
  }

  // Marked with @DoNotInline because we don't have static eval for "==" yet.
  @DoNotInline
  @Override
  public boolean equals(Object other) {
    // Java equality is translated into triple equality which is a quick to compare strings for
    // equality without any instanceOf checks.
    return this == other;
  }

  public boolean equalsIgnoreCase(String other) {
    return DevirtualizedString.equalsIgnoreCase(this, other);
  }

  public byte[] getBytes() {
    return DevirtualizedString.getBytes(this);
  }

  public byte[] getBytes(String charsetName) throws UnsupportedEncodingException {
    return DevirtualizedString.getBytes(this, charsetName);
  }

  public byte[] getBytes(Charset charset) {
    return DevirtualizedString.getBytes(this, charset);
  }

  public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
    DevirtualizedString.getChars(this, srcBegin, srcEnd, dst, dstBegin);
  }

  /**
   * Magic; JSODevirtualizer will use this implementation.<p>
   *
   * Each class gets a synthetic stubs for getClass at AST construction time with the exception of
   * Object, JavaScriptObject and subclasses and String; see {@link GwtAstBuilder.createMembers()}.
   * <p>
   *
   * These stubs are replaced in {@link ReplaceGetClassOverrides} by an access to field __clazz
   * which is initialized in each class prototype to point to the class literal. String is
   * implemented as a plain JavaScript string hence lacking said field.<p>
   *
   * The devirtualizer {@code JsoDevirtualizer} will insert a trampoline that uses this
   * implementation.
   */
  @Override
  public Class<? extends Object> getClass() {
    return String.class;
  }

  @Override
  public int hashCode() {
    return DevirtualizedString.hashCode(this);
  }

  public int indexOf(int codePoint) {
    return DevirtualizedString.indexOf(this, codePoint);
  }

  public int indexOf(int codePoint, int startIndex) {
    return DevirtualizedString.indexOf(this, codePoint, startIndex);
  }

  public int indexOf(String str) {
    return DevirtualizedString.indexOf(this, str);
  }

  public int indexOf(String str, int startIndex) {
    return DevirtualizedString.indexOf(this, str, startIndex);
  }

  public String intern() {
    return DevirtualizedString.intern(this);
  }

  public boolean isEmpty() {
    return DevirtualizedString.isEmpty(this);
  }

  public int lastIndexOf(int codePoint) {
    return DevirtualizedString.lastIndexOf(this, codePoint);
  }

  public int lastIndexOf(int codePoint, int startIndex) {
    return DevirtualizedString.lastIndexOf(this, codePoint, startIndex);
  }

  public int lastIndexOf(String str) {
    return DevirtualizedString.lastIndexOf(this, str);
  }

  public int lastIndexOf(String str, int start) {
    return DevirtualizedString.lastIndexOf(this, str, start);
  }

  @Override
  public int length() {
    return DevirtualizedString.length(this);
  }

  public boolean matches(String regex) {
    return DevirtualizedString.matches(this, regex);
  }

  public int offsetByCodePoints(int index, int codePointOffset) {
    return DevirtualizedString.offsetByCodePoints(this, index, codePointOffset);
  }

  public boolean regionMatches(boolean ignoreCase, int toffset, String other,
      int ooffset, int len) {
    return DevirtualizedString.regionMatches(this, ignoreCase, toffset, other, ooffset, len);
  }

  public boolean regionMatches(int toffset, String other, int ooffset, int len) {
    return DevirtualizedString.regionMatches(this, toffset, other, ooffset, len);
  }

  public String replace(char from, char to) {
    return DevirtualizedString.replace(this, from, to);
  }

  public String replace(CharSequence from, CharSequence to) {
    return DevirtualizedString.replace(this, from, to);
  }

  public String replaceAll(String regex, String replace) {
    return DevirtualizedString.replaceAll(this, regex, replace);
  }

  public String replaceFirst(String regex, String replace) {
    return DevirtualizedString.replaceFirst(this, regex, replace);
  }

  public String[] split(String regex) {
    return DevirtualizedString.split(this, regex);
  }

  public String[] split(String regex, int maxMatch) {
    return DevirtualizedString.split(this, regex, maxMatch);
  }

  public boolean startsWith(String prefix) {
    return DevirtualizedString.startsWith(this, prefix);
  }

  public boolean startsWith(String prefix, int toffset) {
    return DevirtualizedString.startsWith(this, prefix, toffset);
  }

  @Override
  public CharSequence subSequence(int beginIndex, int endIndex) {
    return DevirtualizedString.subSequence(this, beginIndex, endIndex);
  }

  public String substring(int beginIndex) {
    return DevirtualizedString.substring(this, beginIndex);
  }

  public String substring(int beginIndex, int endIndex) {
    return DevirtualizedString.substring(this, beginIndex, endIndex);
  }

  public char[] toCharArray() {
    return DevirtualizedString.toCharArray(this);
  }

  public String toLowerCase() {
    return DevirtualizedString.toLowerCase(this);
  }

  public String toLowerCase(Locale locale) {
    return DevirtualizedString.toLowerCase(this, locale);
  }

  public String toUpperCase() {
    return DevirtualizedString.toUpperCase(this);
  }

  // See the notes in lowerCase pair.
  public String toUpperCase(Locale locale) {
    return DevirtualizedString.toUpperCase(this, locale);
  }

  @Override
  public String toString() {
    /*
     * Magic: this method is only used during compiler optimizations; the generated JS will instead alias
     * this method to the native String.prototype.toString() function.
     */
    return this;
  }

  public String trim() {
    return DevirtualizedString.trim(this);
  }

  // CHECKSTYLE_OFF: Utility Methods for unboxed String.

  static String $createString() {
    return "";
  }

  static String $createString(byte[] bytes) {
    return DevirtualizedString.$createString(bytes);
  }

  static String $createString(byte[] bytes, int ofs, int len) {
    return DevirtualizedString.$createString(bytes, ofs, len);
  }

  static String $createString(byte[] bytes, int ofs, int len, String charsetName)
      throws UnsupportedEncodingException {
    return DevirtualizedString.$createString(bytes, ofs, len, charsetName);
  }

  static String $createString(byte[] bytes, int ofs, int len, Charset charset) {
    return DevirtualizedString.$createString(bytes, ofs, len, charset);
  }

  static String $createString(byte[] bytes, String charsetName)
      throws UnsupportedEncodingException {
    return DevirtualizedString.$createString(bytes, charsetName);
  }

  static String $createString(byte[] bytes, Charset charset)
      throws UnsupportedEncodingException {
    return DevirtualizedString.$createString(bytes, charset);
  }

  static String $createString(char value[]) {
    return DevirtualizedString.$createString(value);
  }

  static String $createString(char value[], int offset, int count) {
    return DevirtualizedString.$createString(value, offset, count);
  }

  static String $createString(int[] codePoints, int offset, int count) {
    return DevirtualizedString.$createString(codePoints, offset, count);
  }

  static String $createString(String other) {
    return DevirtualizedString.$createString(other);
  }

  static String $createString(StringBuffer sb) {
    return DevirtualizedString.$createString(sb);
  }

  static String $createString(StringBuilder sb) {
    return DevirtualizedString.$createString(sb);
  }
  // CHECKSTYLE_ON: end utility methods
}
