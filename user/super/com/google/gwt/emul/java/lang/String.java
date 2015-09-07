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

import javaemul.internal.EmulatedCharset;
import javaemul.internal.Strings;
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
  public static final Comparator<String> CASE_INSENSITIVE_ORDER = Strings.CASE_INSENSITIVE_ORDER;

  public static String copyValueOf(char[] v) {
    return Strings.valueOf(v);
  }

  public static String copyValueOf(char[] v, int offset, int count) {
    return Strings.valueOf(v, offset, count);
  }

  public static String valueOf(boolean x) {
    return Strings.valueOf(x);
  }

  public static String valueOf(char x) {
    return Strings.valueOf(x);
  }

  public static String valueOf(char x[], int offset, int count) {
    return Strings.valueOf(x, offset, count);
  }

  public static String valueOf(char[] x) {
    return Strings.valueOf(x);
  }

  public static String valueOf(double x) {
    return Strings.valueOf(x);
  }

  public static String valueOf(float x) {
    return Strings.valueOf(x);
  }

  public static String valueOf(int x) {
    return Strings.valueOf(x);
  }

  public static String valueOf(long x) {
    return Strings.valueOf(x);
  }

  public static String valueOf(Object x) {
    return Strings.valueOf(x);
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
    return Strings.charAt(this, index);
  }

  public int codePointAt(int index) {
    return Strings.codePointAt(this, index);
  }

  public int codePointBefore(int index) {
    return Strings.codePointBefore(this, index);
  }

  public int codePointCount(int beginIndex, int endIndex) {
    return Strings.codePointCount(this, beginIndex, endIndex);
  }

  @Override
  public int compareTo(String other) {
    return Strings.compareTo(this, other);
  }

  public int compareToIgnoreCase(String other) {
    return Strings.compareToIgnoreCase(this, other);
  }

  public String concat(String str) {
    return Strings.concat(this, str);
  }

  public boolean contains(CharSequence s) {
    return Strings.contains(this, s);
  }

  public boolean contentEquals(CharSequence cs) {
    return Strings.contentEquals(this, cs);
  }

  public boolean contentEquals(StringBuffer sb) {
    return Strings.contentEquals(this, sb);
  }

  public boolean endsWith(String suffix) {
    return Strings.endsWith(this, suffix);
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
    return Strings.equalsIgnoreCase(this, other);
  }

  public byte[] getBytes() {
    return Strings.getBytes(this);
  }

  public byte[] getBytes(String charsetName) throws UnsupportedEncodingException {
    return Strings.getBytes(this, charsetName);
  }

  public byte[] getBytes(Charset charset) {
    return Strings.getBytes(this, charset);
  }

  public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
    Strings.getChars(this, srcBegin, srcEnd, dst, dstBegin);
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
    return Strings.hashCode(this);
  }

  public int indexOf(int codePoint) {
    return Strings.indexOf(this, codePoint);
  }

  public int indexOf(int codePoint, int startIndex) {
    return Strings.indexOf(this, codePoint, startIndex);
  }

  public int indexOf(String str) {
    return Strings.indexOf(this, str);
  }

  public int indexOf(String str, int startIndex) {
    return Strings.indexOf(this, str, startIndex);
  }

  public String intern() {
    return Strings.intern(this);
  }

  public boolean isEmpty() {
    return Strings.isEmpty(this);
  }

  public int lastIndexOf(int codePoint) {
    return Strings.lastIndexOf(this, codePoint);
  }

  public int lastIndexOf(int codePoint, int startIndex) {
    return Strings.lastIndexOf(this, codePoint, startIndex);
  }

  public int lastIndexOf(String str) {
    return Strings.lastIndexOf(this, str);
  }

  public int lastIndexOf(String str, int start) {
    return Strings.lastIndexOf(this, str, start);
  }

  @Override
  public int length() {
    return Strings.length(this);
  }

  public boolean matches(String regex) {
    return Strings.matches(this, regex);
  }

  public int offsetByCodePoints(int index, int codePointOffset) {
    return Strings.offsetByCodePoints(this, index, codePointOffset);
  }

  public boolean regionMatches(boolean ignoreCase, int toffset, String other,
      int ooffset, int len) {
    return Strings.regionMatches(this, ignoreCase, toffset, other, ooffset, len);
  }

  public boolean regionMatches(int toffset, String other, int ooffset, int len) {
    return Strings.regionMatches(this, toffset, other, ooffset, len);
  }

  public String replace(char from, char to) {
    return Strings.replace(this, from, to);
  }

  public String replace(CharSequence from, CharSequence to) {
    return Strings.replace(this, from, to);
  }

  public String replaceAll(String regex, String replace) {
    return Strings.replaceAll(this, regex, replace);
  }

  public String replaceFirst(String regex, String replace) {
    return Strings.replaceFirst(this, regex, replace);
  }

  public String[] split(String regex) {
    return Strings.split(this, regex);
  }

  public String[] split(String regex, int maxMatch) {
    return Strings.split(this, regex, maxMatch);
  }

  public boolean startsWith(String prefix) {
    return Strings.startsWith(this, prefix);
  }

  public boolean startsWith(String prefix, int toffset) {
    return Strings.startsWith(this, prefix, toffset);
  }

  @Override
  public CharSequence subSequence(int beginIndex, int endIndex) {
    return Strings.subSequence(this, beginIndex, endIndex);
  }

  public String substring(int beginIndex) {
    return Strings.substring(this, beginIndex);
  }

  public String substring(int beginIndex, int endIndex) {
    return Strings.substring(this, beginIndex, endIndex);
  }

  public char[] toCharArray() {
    return Strings.toCharArray(this);
  }

  public String toLowerCase() {
    return Strings.toLowerCase(this);
  }

  public String toLowerCase(Locale locale) {
    return Strings.toLowerCase(this, locale);
  }

  public String toUpperCase() {
    return Strings.toUpperCase(this);
  }

  // See the notes in lowerCase pair.
  public String toUpperCase(Locale locale) {
    return Strings.toUpperCase(this, locale);
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
    return Strings.trim(this);
  }

  // CHECKSTYLE_OFF: Utility Methods for unboxed String.

  static String $createString() {
    return "";
  }

  static String $createString(byte[] bytes) {
    return $createString(bytes, 0, bytes.length);
  }

  static String $createString(byte[] bytes, int ofs, int len) {
    return $createString(bytes, ofs, len, EmulatedCharset.UTF_8);
  }

  static String $createString(byte[] bytes, int ofs, int len, String charsetName)
      throws UnsupportedEncodingException {
    return $createString(bytes, ofs, len, Strings.getCharset(charsetName));
  }

  static String $createString(byte[] bytes, int ofs, int len, Charset charset) {
    return String.valueOf(((EmulatedCharset) charset).decodeString(bytes, ofs, len));
  }

  static String $createString(byte[] bytes, String charsetName)
      throws UnsupportedEncodingException {
    return $createString(bytes, 0, bytes.length, charsetName);
  }

  static String $createString(byte[] bytes, Charset charset)
      throws UnsupportedEncodingException {
    return $createString(bytes, 0, bytes.length, charset.name());
  }

  static String $createString(char value[]) {
    return String.valueOf(value);
  }

  static String $createString(char value[], int offset, int count) {
    return String.valueOf(value, offset, count);
  }

  static String $createString(int[] codePoints, int offset, int count) {
    char[] chars = new char[count * 2];
    int charIdx = 0;
    while (count-- > 0) {
      charIdx += Character.toChars(codePoints[offset++], chars, charIdx);
    }
    return String.valueOf(chars, 0, charIdx);
  }

  static String $createString(String other) {
    return other;
  }

  static String $createString(StringBuffer sb) {
    return String.valueOf(sb);
  }

  static String $createString(StringBuilder sb) {
    return String.valueOf(sb);
  }
  // CHECKSTYLE_ON: end utility methods
}
