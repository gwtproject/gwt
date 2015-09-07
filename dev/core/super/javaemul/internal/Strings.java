/*
 * Copyright 2015 Google Inc.
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

package javaemul.internal;

import static javaemul.internal.InternalPreconditions.checkStringBounds;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Comparator;
import java.util.Locale;

/**
 * Strings contains the implementation for String.java as static methods.
 */
public final class Strings {

  public static final Comparator<String> CASE_INSENSITIVE_ORDER = new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
      return compareToIgnoreCase(a, b);
    }
  };

  public static String copyValueOf(char[] v) {
    return valueOf(v);
  }

  public static String copyValueOf(char[] v, int offset, int count) {
    return valueOf(v, offset, count);
  }

  public static String valueOf(boolean x) {
    return "" + x;
  }

  public static native String valueOf(char x) /*-{
    return String.fromCharCode(x);
  }-*/;

  public static String valueOf(char x[], int offset, int count) {
    int end = offset + count;
    checkStringBounds(offset, end, x.length);
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

  private static native String fromCharCode(Object array) /*-{
    return String.fromCharCode.apply(null, array);
  }-*/;

  public static java.lang.String valueOf(char[] x) {
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

  public static String valueOf(Object x) {
    return "" + x;
  }

  // CHECKSTYLE_OFF: This class has special needs.

  static native String nativeSubString(String str, int beginIndex, int len) /*-{
    return str.substr(beginIndex, len);
  }-*/;

  /**
   * This method converts Java-escaped dollar signs "\$" into JavaScript-escaped
   * dollar signs "$$", and removes all other lone backslashes, which serve as
   * escapes in Java but are passed through literally in JavaScript.
   */
  private static String translateReplaceString(String replaceStr) {
    int pos = 0;
    while (0 <= (pos = indexOf(replaceStr, "\\", pos))) {
      if (charAt(replaceStr, pos + 1) == '$') {
        replaceStr = substring(replaceStr, 0, pos) + "$"
            + substring(replaceStr, ++pos);
      } else {
        replaceStr = substring(replaceStr, 0, pos) + substring(replaceStr, ++pos);
      }
    }
    return replaceStr;
  }



  // CHECKSTYLE_ON

  private static native int compareTo0(String thisStr, String otherStr) /*-{
    if (thisStr == otherStr) {
      return 0;
    }
    return thisStr < otherStr ? -1 : 1;
  }-*/;

  public static Charset getCharset(String charsetName) throws UnsupportedEncodingException {
    try {
      return Charset.forName(charsetName);
    } catch (UnsupportedCharsetException e) {
      throw new UnsupportedEncodingException(charsetName);
    }
  }

  private static String fromCodePoint(int codePoint) {
    if (codePoint >= Character.MIN_SUPPLEMENTARY_CODE_POINT) {
      char hiSurrogate = Characters.getHighSurrogate(codePoint);
      char loSurrogate = Characters.getLowSurrogate(codePoint);
      return String.valueOf(hiSurrogate)
          + String.valueOf(loSurrogate);
    } else {
      return String.valueOf((char) codePoint);
    }
  }

  // The following public static methods are the implementation for String, they all get the
  // String instance passed as the first parameter and treat string as an opaque Class without
  // any (Java) instance methods.
  // This allows j2cl to statically dispatch method calls to Strings.java for String java methods,
  // while GWT just calls these methods from its String class passing this as the first parameter.
  public static native char charAt(String s, int index) /*-{
    return s.charCodeAt(index);
  }-*/;

  public static int codePointAt(String s, int index) {
    return Characters.codePointAt(s, index, length(s));
  }

  public static int codePointBefore(String s, int index) {
    return Characters.codePointBefore(s, index, 0);
  }

  public static int codePointCount(String s, int beginIndex, int endIndex) {
    return Character.codePointCount(s, beginIndex, endIndex);
  }

  public static int compareTo(String s, String other) {
    return compareTo0(s, other);
  }

  public static int compareToIgnoreCase(String s, String other) {
    return compareTo0(s.toLowerCase(), other.toLowerCase());
  }

  public static native String concat(String s, String str) /*-{
    return s + str;
  }-*/;

  public static boolean contains(String s, CharSequence sequence) {
    return indexOf(s, sequence.toString()) != -1;
  }

  public static boolean contentEquals(String s, CharSequence cs) {
    return equals(s, cs.toString());
  }

  public static boolean contentEquals(String s, StringBuffer sb) {
    return equals(s, sb.toString());
  }

  public static boolean endsWith(String s, String suffix) {
    // If IE8 supported negative start index, we could have just used "-suffixlength".
    int suffixlength = length(suffix);
    String sub = nativeSubString(s, length(s) - suffixlength, suffixlength);
    return equals(sub, suffix);
  }

  public static native boolean equals(String s, Object other) /*-{
    return s === other;
  }-*/;

  public static native boolean equalsIgnoreCase(String s, String other) /*-{
    if (other == null) {
      return false;
    }
    if (s == other) {
      return true;
    }
    return (s.length == other.length) && (s.toLowerCase() == other.toLowerCase());
  }-*/;

  public static byte[] getBytes(String s) {
    // default character set for GWT is UTF-8
    return getBytes(s, EmulatedCharset.UTF_8);
  }

  public static byte[] getBytes(String s, String charsetName) throws UnsupportedEncodingException {
    return getBytes(s, getCharset(charsetName));
  }

  public static byte[] getBytes(String s, Charset charset) {
    return ((EmulatedCharset) charset).getBytes(s);
  }

  public static void getChars(String s, int srcBegin, int srcEnd, char[] dst, int dstBegin) {
    for (int srcIdx = srcBegin; srcIdx < srcEnd; ++srcIdx) {
      dst[dstBegin++] = charAt(s, srcIdx);
    }
  }

  public static int hashCode(String s) {
    return HashCodes.hashCodeForString(s);
  }

  public static int indexOf(String s, int codePoint) {
    return indexOf(s, fromCodePoint(codePoint));
  }

  public static int indexOf(String s, int codePoint, int startIndex) {
    return indexOf(s, fromCodePoint(codePoint), startIndex);
  }

  public static native int indexOf(String s, String str) /*-{
    return s.indexOf(str);
  }-*/;

  public static native int indexOf(String s, String str, int startIndex) /*-{
    return s.indexOf(str, startIndex);
  }-*/;

  public static native String intern(String s) /*-{
    return s;
  }-*/;

  public static native boolean isEmpty(String s) /*-{
    return !s.length;
  }-*/;

  public static int lastIndexOf(String s, int codePoint) {
    return lastIndexOf(s, fromCodePoint(codePoint));
  }

  public static int lastIndexOf(String s, int codePoint, int startIndex) {
    return lastIndexOf(s, fromCodePoint(codePoint), startIndex);
  }

  public static native int lastIndexOf(String s, String str) /*-{
    return s.lastIndexOf(str);
  }-*/;

  public static native int lastIndexOf(String s, String str, int start) /*-{
    return s.lastIndexOf(str, start);
  }-*/;

  public static native int length(String s) /*-{
    return s.length;
  }-*/;

  /**
   * Regular expressions vary from the standard implementation. The
   * <code>regex</code> parameter is interpreted by JavaScript as a JavaScript
   * regular expression. For consistency, use only the subset of regular
   * expression syntax common to both Java and JavaScript.
   *
   * TODO(jat): properly handle Java regex syntax
   */
  public static native boolean matches(String s, String regex) /*-{
    // We surround the regex with '^' and '$' because it must match
    // the entire string.
    return new RegExp('^(' + regex + ')$').test(s);
  }-*/;

  public static int offsetByCodePoints(String s, int index, int codePointOffset) {
    return Character.offsetByCodePoints(s, index, codePointOffset);
  }

  public static boolean regionMatches(String s, boolean ignoreCase, int toffset, String other,
      int ooffset, int len) {
    if (other == null) {
      throw new NullPointerException();
    }
    if (toffset < 0 || ooffset < 0 || len <= 0) {
      return false;
    }
    if (toffset + len > length(s) || ooffset + len > length(other)) {
      return false;
    }

    String left = nativeSubString(s, toffset, len);
    String right = nativeSubString(other, ooffset, len);
    return ignoreCase ? equalsIgnoreCase(left, right) : equals(left, right);
  }

  public static boolean regionMatches(String s, int toffset, String other, int ooffset, int len) {
    return regionMatches(s, false, toffset, other, ooffset, len);
  }

  public static String replace(String s, char from, char to) {
    String hex = Integer.toHexString(from);
    return replace0(s, hex, to);
  }

  private static native String replace0(String s, String hex, char to) /*-{
    // Translate 'from' into unicode escape sequence (\\u and a four-digit hexadecimal number).
    // Escape sequence replacement is used instead of a string literal replacement
    // in order to escape regexp special characters (e.g. '.').
    var regex = "\\u" + "0000".substring(hex.length) + hex;
    return s.replace(RegExp(regex, "g"), String.fromCharCode(to));
  }-*/;

  public static String replace(String s, CharSequence from, CharSequence to) {
    // Implementation note: This uses a regex replacement instead of
    // a string literal replacement because Safari does not
    // follow the spec for "$$" in the replacement string: it
    // will insert a literal "$$". IE and Firefox, meanwhile,
    // treat "$$" as "$".

    // Escape regex special characters from literal replacement string.
    String regex = replaceAll(from.toString(), "([/\\\\\\.\\*\\+\\?\\|\\(\\)\\[\\]\\{\\}$^])", "\\\\$1");
    // Escape $ since it is for match backrefs and \ since it is used to escape
    // $.
    String replacement = replaceAll(to.toString(), "\\\\", "\\\\\\\\").replaceAll("\\$", "\\\\$");

    return replaceAll(s, regex, replacement);
  }

  /**
   * Regular expressions vary from the standard implementation. The
   * <code>regex</code> parameter is interpreted by JavaScript as a JavaScript
   * regular expression. For consistency, use only the subset of regular
   * expression syntax common to both Java and JavaScript.
   *
   * TODO(jat): properly handle Java regex syntax
   */
  public static String replaceAll(String s, String regex, String replace) {
    replace = translateReplaceString(replace);
    return replaceAll0(s, regex, "g", replace);
  }

  private static native String replaceAll0(String s, String regex, String regExpParam,
      String replace) /*-{
    return s.replace(RegExp(regex, regExpParam), replace);
  }-*/;

  /**
   * Regular expressions vary from the standard implementation. The
   * <code>regex</code> parameter is interpreted by JavaScript as a JavaScript
   * regular expression. For consistency, use only the subset of regular
   * expression syntax common to both Java and JavaScript.
   *
   * TODO(jat): properly handle Java regex syntax
   */
  public static String replaceFirst(String s, String regex, String replace) {
    replace = translateReplaceString(replace);
    return replaceAll0(s, regex, "", replace);
  }

  /**
   * Regular expressions vary from the standard implementation. The
   * <code>regex</code> parameter is interpreted by JavaScript as a JavaScript
   * regular expression. For consistency, use only the subset of regular
   * expression syntax common to both Java and JavaScript.
   */
  public static String[] split(String s, String regex) {
    return split(s, regex, 0);
  }

  /**
   * Regular expressions vary from the standard implementation. The
   * <code>regex</code> parameter is interpreted by JavaScript as a JavaScript
   * regular expression. For consistency, use only the subset of regular
   * expression syntax common to both Java and JavaScript.
   *
   * TODO(jat): properly handle Java regex syntax
   */
  public static String[] split(String s, String regex, int maxMatch) {
    Object splittedJsArray = splitNative(s, regex, maxMatch);

    int length = ArrayHelper.getLength(splittedJsArray);
    String[] out = new String[length];
    for ( int i = 0; i < length; ++i) {
      out[i] = getStringValueFromArray(splittedJsArray, i);
    }
    return out;
  }

  private static native String getStringValueFromArray(Object array, int index) /*-{
    return array[index];
  }-*/;

  private static native String[] splitNative(String s, String regex, int maxMatch) /*-{
    // The compiled regular expression created from the string
    var compiled = new RegExp(regex, "g");
    // the Javascipt array to hold the matches prior to conversion
    var out = [];
    // how many matches performed so far
    var count = 0;
    // The current string that is being matched; trimmed as each piece matches
    var trail = s;
    // used to detect repeated zero length matches
    // Must be null to start with because the first match of "" makes no
    // progress by intention
    var lastTrail = null;
    // We do the split manually to avoid Javascript incompatibility
    while (true) {
      // None of the information in the match returned are useful as we have no
      // subgroup handling
      var matchObj = compiled.exec(trail);
      if (matchObj == null || trail == "" || (count == (maxMatch - 1) && maxMatch > 0)) {
        out[count] = trail;
        break;
      } else {
        out[count] = trail.substring(0, matchObj.index);
        trail = trail.substring(matchObj.index + matchObj[0].length, trail.length);
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
    if (maxMatch == 0 && s.length > 0) {
      var lastNonEmpty = out.length;
      while (lastNonEmpty > 0 && out[lastNonEmpty - 1] == "") {
        --lastNonEmpty;
      }
      if (lastNonEmpty < out.length) {
        out.splice(lastNonEmpty, out.length - lastNonEmpty);
      }
    }

    return out;
  }-*/;

  public static boolean startsWith(String s, String prefix) {
    return startsWith(s, prefix, 0);
  }

  public static boolean startsWith(String s, String prefix, int toffset) {
    String sub = nativeSubString(s, toffset, prefix.length());
    return toffset >= 0 && equals(sub, prefix);
  }

  public static CharSequence subSequence(String s, int beginIndex, int endIndex) {
    return substring(s, beginIndex, endIndex);
  }

  public static String substring(String s, int beginIndex) {
    return nativeSubString(s, beginIndex, length(s) - beginIndex);
  }

  public static String substring(String s, int beginIndex, int endIndex) {
    return nativeSubString(s, beginIndex, endIndex - beginIndex);
  }

  public static char[] toCharArray(String s) {
    int n = length(s);
    char[] charArr = new char[n];
    getChars(s, 0, n, charArr, 0);
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
  public static native String toLowerCase(String s) /*-{
    return s.toLowerCase();
  }-*/;

  /**
   * Transforms the String to lower-case based on the native locale of the browser.
   */
  private static native String toLocaleLowerCase(String s) /*-{
    return s.toLocaleLowerCase();
  }-*/;

  /**
   * If provided {@code locale} is {@link Locale#getDefault()}, uses javascript's
   * {@code toLocaleLowerCase} to do a locale specific transformation. Otherwise, it will fallback
   * to {@code toLowerCase} which performs the right thing for the limited set of Locale's
   * predefined in GWT Locale emulation.
   */
  public static String toLowerCase(String s, Locale locale) {
    return locale == Locale.getDefault() ? toLocaleLowerCase(s) : toLowerCase(s);
  }

  // See the notes in lowerCase pair.
  public static native String toUpperCase(String s) /*-{
    return s.toUpperCase();
  }-*/;

  // See the notes in lowerCase pair.
  private static native String toLocaleUpperCase(String s) /*-{
    return s.toLocaleUpperCase();
  }-*/;

  // See the notes in lowerCase pair.
  public static String toUpperCase(String s, Locale locale) {
    return locale == Locale.getDefault() ? toLocaleUpperCase(s) : toUpperCase(s);
  }

  public static String toString(String s) {
    return s;
  }

  public static String trim(String s) {
    int length = length(s);
    int start = 0;
    while (start < length && charAt(s, start) <= ' ') {
      start++;
    }
    int end = length;
    while (end > start && charAt(s, end - 1) <= ' ') {
      end--;
    }
    return start > 0 || end < length ? substring(s, start, end) : s;
  }
}
