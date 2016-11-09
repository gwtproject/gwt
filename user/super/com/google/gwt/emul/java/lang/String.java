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

import static javaemul.internal.InternalPreconditions.checkCriticalStringBounds;
import static javaemul.internal.InternalPreconditions.checkNotNull;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Comparator;
import java.util.Locale;
import java.util.StringJoiner;

import javaemul.internal.ArrayHelper;
import javaemul.internal.EmulatedCharset;
import javaemul.internal.HashCodes;
import javaemul.internal.JsUtils;
import javaemul.internal.NativeRegExp;
import javaemul.internal.annotations.DoNotInline;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Intrinsic string class.
 */
// Needed to have constructors not fail compilation internally at Google
@SuppressWarnings({ "ReturnValueIgnored", "unusable-by-js" })
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
   * <li>methods added in Java 1.6 (the issue is how will it impact users
   * building against Java 1.5)
   * <ul>
   * <li>isEmpty()
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
  public static final Comparator<String> CASE_INSENSITIVE_ORDER = new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
      return a.compareToIgnoreCase(b);
    }
  };

  public static String copyValueOf(char[] v) {
    return valueOf(v);
  }

  public static String copyValueOf(char[] v, int offset, int count) {
    return valueOf(v, offset, count);
  }

  public static String join(CharSequence delimiter, CharSequence... elements) {
    StringJoiner joiner = new StringJoiner(delimiter);
    for (CharSequence e : elements) {
      joiner.add(e);
    }
    return joiner.toString();
  }

  public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
    StringJoiner joiner = new StringJoiner(delimiter);
    for (CharSequence e : elements) {
      joiner.add(e);
    }
    return joiner.toString();
  }

  public static String valueOf(boolean x) {
    return "" + x;
  }

  public static String valueOf(char x) {
    return NativeString.fromCharCode(x);
  }

  public static String valueOf(char x[], int offset, int count) {
    int end = offset + count;
    checkCriticalStringBounds(offset, end, x.length);
    // Work around function.prototype.apply call stack size limits:
    // https://code.google.com/p/v8/issues/detail?id=2896
    // Performance: http://jsperf.com/string-fromcharcode-test/13
    int batchSize = ArrayHelper.ARRAY_PROCESS_BATCH_SIZE;
    String s = "";
    for (int batchStart = offset; batchStart < end;) {
      int batchEnd = Math.min(batchStart + batchSize, end);
      s += fromCharCode(ArrayHelper.unsafeClone(x, batchStart, batchEnd));
      batchStart = batchEnd;
    }
    return s;
  }

  private static String fromCharCode(Object[] array) {
    return getFromCharCodeFunction().apply(null, array);
  }

  @JsType(isNative = true, name = "Function", namespace = JsPackage.GLOBAL)
  private static class NativeFunction {
    public native String apply(String thisContext, Object[] argsArray);
  }

  @JsProperty(name = "String.fromCharCode", namespace = "<window>")
  private static native NativeFunction getFromCharCodeFunction();

  public static String valueOf(char[] x) {
    return valueOf(x, 0, x.length);
  }

  public static String valueOf(double x) {
    return "" + x;
  }

  public static String valueOf(float x) {
    return "" + x;
  }

  public static String valueOf(int x) {
    return "" + x;
  }

  public static String valueOf(long x) {
    return "" + x;
  }

  // valueOf needs to be treated special:
  // J2cl uses it for String concat and thus it can not use string concatenation itself.
  public static String valueOf(Object x) {
    return x == null ? "null" : x.toString();
  }

  /**
   * This method converts Java-escaped dollar signs "\$" into JavaScript-escaped
   * dollar signs "$$", and removes all other lone backslashes, which serve as
   * escapes in Java but are passed through literally in JavaScript.
   *
   * @skip
   */
  private static String translateReplaceString(String replaceStr) {
    int pos = 0;
    while (0 <= (pos = replaceStr.indexOf("\\", pos))) {
      if (replaceStr.charAt(pos + 1) == '$') {
        replaceStr = replaceStr.substring(0, pos) + "$"
            + replaceStr.substring(++pos);
      } else {
        replaceStr = replaceStr.substring(0, pos) + replaceStr.substring(++pos);
      }
    }
    return replaceStr;
  }

  private static Charset getCharset(String charsetName) throws UnsupportedEncodingException {
    try {
      return Charset.forName(charsetName);
    } catch (UnsupportedCharsetException e) {
      throw new UnsupportedEncodingException(charsetName);
    }
  }

  static String fromCodePoint(int codePoint) {
    if (codePoint >= Character.MIN_SUPPLEMENTARY_CODE_POINT) {
      char hiSurrogate = Character.getHighSurrogate(codePoint);
      char loSurrogate = Character.getLowSurrogate(codePoint);
      return String.valueOf(hiSurrogate)
          + String.valueOf(loSurrogate);
    } else {
      return String.valueOf((char) codePoint);
    }
  }

  public String() {
    /*
     * Call to $create(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $create(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $create();
  }

  public String(byte[] bytes) {
    /*
     * Call to $create(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $create(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $create(bytes);
  }

  public String(byte[] bytes, int ofs, int len) {
    /*
     * Call to $create(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $create(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $create(bytes, ofs, len);
  }

  public String(byte[] bytes, int ofs, int len, String charsetName)
      throws UnsupportedEncodingException {
    /*
     * Call to $create(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $create(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $create(bytes, ofs, len, charsetName);
  }

  public String(byte[] bytes, int ofs, int len, Charset charset) {
    /*
     * Call to $create(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $create(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $create(bytes, ofs, len, charset);
  }

  public String(byte[] bytes, String charsetName)
      throws UnsupportedEncodingException {
    /*
     * Call to $create(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $create(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $create(bytes, charsetName);
  }

  public String(byte[] bytes, Charset charset) {
    /*
     * Call to $create(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $create(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $create(bytes, charset);
  }

  public String(char value[]) {
    /*
     * Call to $create(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $create(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $create(value);
  }

  public String(char value[], int offset, int count) {
    /*
     * Call to $create(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $create(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $create(value, offset, count);
  }

  public String(int codePoints[], int offset, int count) {
    /*
     * Call to $create(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $create(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $create(codePoints, offset, count);
  }

  public String(String other) {
    /*
     * Call to $create(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $create(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $create(other);
  }

  public String(StringBuffer sb) {
    /*
     * Call to $create(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $create(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $create(sb);
  }

  public String(StringBuilder sb) {
    /*
     * Call to $create(args) must be here so that the method is referenced and not
     * pruned before new String(args) is replaced by $create(args) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $create(sb);
  }

  private NativeString asNativeString() {
    return toNative(this);
  }

  private static native NativeString toNative(String str) /*-{
    return str;
  }-*/;

  @Override
  public char charAt(int index) {
    return asNativeString().charCodeAt(index);
  }

  public int codePointAt(int index) {
    return Character.codePointAt(this, index, length());
  }

  public int codePointBefore(int index) {
    return Character.codePointBefore(this, index, 0);
  }

  public int codePointCount(int beginIndex, int endIndex) {
    return Character.codePointCount(this, beginIndex, endIndex);
  }

  @Override
  public int compareTo(String other) {
    return JsUtils.compare(checkNotNull(this), checkNotNull(other));
  }

  public int compareToIgnoreCase(String other) {
    return toLowerCase().compareTo(other.toLowerCase());
  }

  public String concat(String str) {
    return checkNotNull(this) + checkNotNull(str);
  }

  public boolean contains(CharSequence s) {
    return indexOf(s.toString()) != -1;
  }

  public boolean contentEquals(CharSequence cs) {
    return equals(cs.toString());
  }

  public boolean contentEquals(StringBuffer sb) {
    return equals(sb.toString());
  }

  public boolean endsWith(String suffix) {
    // If IE8 supported negative start index, we could have just used "-suffixlength".
    int suffixlength = suffix.length();
    return asNativeString().substr(length() - suffixlength, suffixlength).equals(suffix);
  }

  // Marked with @DoNotInline because we don't have static eval for "==" yet.
  @DoNotInline
  @Override
  public boolean equals(Object other) {
    // Java equality is translated into triple equality which is a quick to compare strings for
    // equality without any instanceOf checks.
    return checkNotNull(this) == other;
  }

  public boolean equalsIgnoreCase(String other) {
    checkNotNull(this);
    if (other == null) {
      return false;
    }
    if (equals(other)) {
      return true;
    }
    return length() == other.length() && toLowerCase().equals(other.toLowerCase());
  }

  public byte[] getBytes() {
    // default character set for GWT is UTF-8
    return getBytes(EmulatedCharset.UTF_8);
  }

  public byte[] getBytes(String charsetName) throws UnsupportedEncodingException {
    return getBytes(getCharset(charsetName));
  }

  public byte[] getBytes(Charset charset) {
    return ((EmulatedCharset) charset).getBytes(this);
  }

  public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
    checkCriticalStringBounds(srcBegin, srcEnd, length());
    checkCriticalStringBounds(dstBegin, dstBegin + (srcEnd - srcBegin), dst.length);
    getChars0(srcBegin, srcEnd, dst, dstBegin);
  }

  private void getChars0(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
    while (srcBegin < srcEnd) {
      dst[dstBegin++] = charAt(srcBegin++);
    }
  }

  @Override
  public int hashCode() {
    return HashCodes.getStringHashCode(this);
  }

  public int indexOf(int codePoint) {
    return indexOf(fromCodePoint(codePoint));
  }

  public int indexOf(int codePoint, int startIndex) {
    return indexOf(fromCodePoint(codePoint), startIndex);
  }

  public int indexOf(String str) {
    return asNativeString().indexOf(str);
  }

  public int indexOf(String str, int startIndex) {
    return asNativeString().indexOf(str, startIndex);
  }

  public String intern() {
    return checkNotNull(this);
  }

  public boolean isEmpty() {
    return length() == 0;
  }

  public int lastIndexOf(int codePoint) {
    return lastIndexOf(fromCodePoint(codePoint));
  }

  public int lastIndexOf(int codePoint, int startIndex) {
    return lastIndexOf(fromCodePoint(codePoint), startIndex);
  }

  public int lastIndexOf(String str) {
    return asNativeString().lastIndexOf(str);
  }

  public int lastIndexOf(String str, int start) {
    return asNativeString().lastIndexOf(str, start);
  }

  @Override
  public int length() {
    return asNativeString().length;
  }

  /**
   * Regular expressions vary from the standard implementation. The
   * <code>regex</code> parameter is interpreted by JavaScript as a JavaScript
   * regular expression. For consistency, use only the subset of regular
   * expression syntax common to both Java and JavaScript.
   *
   * TODO(jat): properly handle Java regex syntax
   */
  public boolean matches(String regex) {
    // We surround the regex with '^' and '$' because it must match the entire string.
    return new NativeRegExp("^(" + regex + ")$").test(this);
  }

  public int offsetByCodePoints(int index, int codePointOffset) {
    return Character.offsetByCodePoints(this, index, codePointOffset);
  }

  public boolean regionMatches(boolean ignoreCase, int toffset, String other,
      int ooffset, int len) {
    checkNotNull(other);
    if (toffset < 0 || ooffset < 0 || len <= 0) {
      return false;
    }
    if (toffset + len > length() || ooffset + len > other.length()) {
      return false;
    }

    String left = asNativeString().substr(toffset, len);
    String right = other.asNativeString().substr(ooffset, len);
    return ignoreCase ? left.equalsIgnoreCase(right) : left.equals(right);
  }

  public boolean regionMatches(int toffset, String other, int ooffset, int len) {
    return regionMatches(false, toffset, other, ooffset, len);
  }

  public String replace(char from, char to) {
    // Translate 'from' into unicode escape sequence (\\u and a four-digit hexadecimal number).
    // Escape sequence replacement is used instead of a string literal replacement
    // in order to escape regexp special characters (e.g. '.').
    String hex = Integer.toHexString(from);
    String regex = "\\u" + "0000".substring(hex.length()) + hex;
    String replace = NativeString.fromCharCode(to);
    return nativeReplaceAll(regex, replace);
  }

  public String replace(CharSequence from, CharSequence to) {
    // Implementation note: This uses a regex replacement instead of
    // a string literal replacement because Safari does not
    // follow the spec for "$$" in the replacement string: it
    // will insert a literal "$$". IE and Firefox, meanwhile,
    // treat "$$" as "$".

    // Escape regex special characters from literal replacement string.
    String regex = from.toString().replaceAll("([/\\\\\\.\\*\\+\\?\\|\\(\\)\\[\\]\\{\\}$^])", "\\\\$1");
    // Escape $ since it is for match backrefs and \ since it is used to escape
    // $.
    String replacement = to.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("\\$", "\\\\$");

    return replaceAll(regex, replacement);
  }

  /**
   * Regular expressions vary from the standard implementation. The
   * <code>regex</code> parameter is interpreted by JavaScript as a JavaScript
   * regular expression. For consistency, use only the subset of regular
   * expression syntax common to both Java and JavaScript.
   *
   * TODO(jat): properly handle Java regex syntax
   */
  public String replaceAll(String regex, String replace) {
    replace = translateReplaceString(replace);
    return nativeReplaceAll(regex, replace);
  }

  String nativeReplaceAll(String regex, String replace) {
    return asNativeString().replace(new NativeRegExp(regex, "g"), replace);
  }

  /**
   * Regular expressions vary from the standard implementation. The
   * <code>regex</code> parameter is interpreted by JavaScript as a JavaScript
   * regular expression. For consistency, use only the subset of regular
   * expression syntax common to both Java and JavaScript.
   *
   * TODO(jat): properly handle Java regex syntax
   */
  public String replaceFirst(String regex, String replace) {
    replace = translateReplaceString(replace);
    NativeRegExp jsRegEx = new NativeRegExp(regex);
    return asNativeString().replace(jsRegEx, replace);
  }

  private static native int getMatchIndex(Object matchObject) /*-{
    return matchObject.index;
  }-*/;

  private static native int getMatchLength(Object matchObject, int index) /*-{
    return matchObject[index].length;
  }-*/;

  /**
   * Regular expressions vary from the standard implementation. The
   * <code>regex</code> parameter is interpreted by JavaScript as a JavaScript
   * regular expression. For consistency, use only the subset of regular
   * expression syntax common to both Java and JavaScript.
   */
  public String[] split(String regex) {
    return split(regex, 0);
  }

  /**
   * Regular expressions vary from the standard implementation. The
   * <code>regex</code> parameter is interpreted by JavaScript as a JavaScript
   * regular expression. For consistency, use only the subset of regular
   * expression syntax common to both Java and JavaScript.
   *
   * TODO(jat): properly handle Java regex syntax
   */
  public String[] split(String regex, int maxMatch) {
    // The compiled regular expression created from the string
    NativeRegExp compiled = new NativeRegExp(regex, "g");
    // the Javascipt array to hold the matches prior to conversion
    String[] out = new String[0];
    // how many matches performed so far
    int count = 0;
    // The current string that is being matched; trimmed as each piece matches
    String trail = this;
    // used to detect repeated zero length matches
    // Must be null to start with because the first match of "" makes no
    // progress by intention
    String lastTrail = null;
    // We do the split manually to avoid Javascript incompatibility
    while (true) {
      // None of the information in the match returned are useful as we have no
      // subgroup handling
      Object matchObj = compiled.exec(trail);
      if (matchObj == null || trail == "" || (count == (maxMatch - 1) && maxMatch > 0)) {
        out[count] = trail;
        break;
      } else {
        out[count] = trail.substring(0, getMatchIndex(matchObj));
        trail = trail.substring(
            getMatchIndex(matchObj) + getMatchLength(matchObj, 0), trail.length());
        // Force the compiled pattern to reset internal state
        compiled.lastIndex = 0;
        // Only one zero length match per character to ensure termination
        if (lastTrail == trail) {
          out[count] = trail.substring(0, 1);
          trail = trail.substring(1);
        }
        lastTrail = trail;
        count++;
      }
    }
    // all blank delimiters at the end are supposed to disappear if maxMatch == 0;
    // however, if the input string is empty, the output should consist of a
    // single empty string
    if (maxMatch == 0 && this.length() > 0) {
      int lastNonEmpty = out.length;
      while (lastNonEmpty > 0 && out[lastNonEmpty - 1] == "") {
        --lastNonEmpty;
      }
      if (lastNonEmpty < out.length) {
        ArrayHelper.setLength(out, lastNonEmpty);
      }
    }
    return out;
  }

  public boolean startsWith(String prefix) {
    return startsWith(prefix, 0);
  }

  public boolean startsWith(String prefix, int toffset) {
    return toffset >= 0 && asNativeString().substr(toffset, prefix.length()).equals(prefix);
  }

  @Override
  public CharSequence subSequence(int beginIndex, int endIndex) {
    return substring(beginIndex, endIndex);
  }

  public String substring(int beginIndex) {
    return asNativeString().substr(beginIndex);
  }

  public String substring(int beginIndex, int endIndex) {
    return asNativeString().substr(beginIndex, endIndex - beginIndex);
  }

  public char[] toCharArray() {
    int n = length();
    char[] charArr = new char[n];
    getChars0(0, n, charArr, 0);
    return charArr;
  }

  /**
   * Transforms the String to lower-case in a locale insensitive way.
   * <p>
   * Unlike JRE, we don't do locale specific transformation by default. That is backward compatible
   * for GWT and in most of the cases that is what the developer actually wants. If you want to make
   * a transformation based on native locale of the browser, you can do
   * {@code toLowerCase(Locale.getDefault())} instead.
   */
  public String toLowerCase() {
    return asNativeString().toLowerCase();
  }

  /**
   * If provided {@code locale} is {@link Locale#getDefault()}, uses javascript's
   * {@code toLocaleLowerCase} to do a locale specific transformation. Otherwise, it will fallback
   * to {@code toLowerCase} which performs the right thing for the limited set of Locale's
   * predefined in GWT Locale emulation.
   */
  public String toLowerCase(Locale locale) {
    return locale == Locale.getDefault()
        ? asNativeString().toLocaleLowerCase() : asNativeString().toLowerCase();
  }

  // See the notes in lowerCase pair.
  public String toUpperCase() {
    return asNativeString().toLocaleUpperCase();
  }

  // See the notes in lowerCase pair.
  public String toUpperCase(Locale locale) {
    return locale == Locale.getDefault()
        ? asNativeString().toLocaleUpperCase() : asNativeString().toUpperCase();
  }

  @Override
  public String toString() {
    /*
     * Magic: this method is only used during compiler optimizations; the generated JS will instead alias
     * this method to the native String.prototype.toString() function.
     */
    return checkNotNull(this);
  }

  public String trim() {
    int length = length();
    int start = 0;
    while (start < length && charAt(start) <= ' ') {
      start++;
    }
    int end = length;
    while (end > start && charAt(end - 1) <= ' ') {
      end--;
    }
    return start > 0 || end < length ? substring(start, end) : this;
  }

  @JsType(isNative = true, name = "String", namespace = "<window>")
  private static class NativeString {
    public static native String fromCharCode(char x);
    public int length;
    public native char charCodeAt(int index);
    public native int indexOf(String str);
    public native int indexOf(String str, int startIndex);
    public native int lastIndexOf(String str);
    public native int lastIndexOf(String str, int start);
    public native String replace(NativeRegExp regex, String replace);
    public native String substr(int beginIndex);
    public native String substr(int beginIndex, int len);
    public native String toLocaleLowerCase();
    public native String toLocaleUpperCase();
    public native String toLowerCase();
    public native String toUpperCase();
  }

  // CHECKSTYLE_OFF: Utility Methods for unboxed String.

  protected static String $create() {
    return "";
  }

  protected static String $create(byte[] bytes) {
    return $create(bytes, 0, bytes.length);
  }

  protected static String $create(byte[] bytes, int ofs, int len) {
    return $create(bytes, ofs, len, EmulatedCharset.UTF_8);
  }

  protected static String $create(byte[] bytes, int ofs, int len, String charsetName)
      throws UnsupportedEncodingException {
    return $create(bytes, ofs, len, String.getCharset(charsetName));
  }

  protected static String $create(byte[] bytes, int ofs, int len, Charset charset) {
    return String.valueOf(((EmulatedCharset) charset).decodeString(bytes, ofs, len));
  }

  protected static String $create(byte[] bytes, String charsetName)
      throws UnsupportedEncodingException {
    return $create(bytes, 0, bytes.length, charsetName);
  }

  protected static String $create(byte[] bytes, Charset charset) {
    return $create(bytes, 0, bytes.length, charset);
  }

  protected static String $create(char value[]) {
    return String.valueOf(value);
  }

  protected static String $create(char value[], int offset, int count) {
    return String.valueOf(value, offset, count);
  }

  protected static String $create(int[] codePoints, int offset, int count) {
    char[] chars = new char[count * 2];
    int charIdx = 0;
    while (count-- > 0) {
      charIdx += Character.toChars(codePoints[offset++], chars, charIdx);
    }
    return String.valueOf(chars, 0, charIdx);
  }

  protected static String $create(String other) {
    return checkNotNull(other);
  }

  protected static String $create(StringBuffer sb) {
    return sb.toString();
  }

  protected static String $create(StringBuilder sb) {
    return sb.toString();
  }

  @JsMethod
  protected static boolean $isInstance(Object instance) {
    return "string".equals(JsUtils.typeOf(instance));
  }
  // CHECKSTYLE_ON: end utility methods
}
