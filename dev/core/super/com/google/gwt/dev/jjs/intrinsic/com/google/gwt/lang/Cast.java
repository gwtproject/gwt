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
package com.google.gwt.lang;

import com.google.gwt.core.client.JavaScriptObject;

// CHECKSTYLE_NAMING_OFF: Uses legacy conventions of underscore prefixes.

/**
 * This is a magic class the compiler uses to perform any cast operations that
 * require code.
 */
final class Cast {

  /**
   * As plain JavaScript Strings (not monkey patcheed) are used to model Java Strings,
   * {@code  stringCastMap} stores runtime type info for cast purposes for string objects.
   *
   * NOTE: it is important that it is initialized to null so that Cast does not require a clinit.
   */
  private static JavaScriptObject stringCastMap = null;

  static native boolean canCast(Object src, int dstId) /*-{
    return src.@java.lang.Object::castableTypeMap && !!src.@java.lang.Object::castableTypeMap[dstId]
        || @com.google.gwt.lang.Cast::isJavaString(Ljava/lang/Object;)(src) &&
        !!@com.google.gwt.lang.Cast::stringCastMap[dstId];
  }-*/;

  // Not functional yet. Works under the assumption that queryId is seedId. This will become true
  // when separate compilation switches to using type name Strings as seedId. Will eventually be the
  // implementation for class.isAssignableFrom().
  static native boolean canCastSeed(String srcId, String dstId) /*-{
    var srcSeed = @com.google.gwt.lang.SeedUtil::seedTable[srcId];
    return (srcSeed.prototype.@java.lang.Object::castableTypeMap &&
            !!srcSeed.prototype.@java.lang.Object::castableTypeMap[dstId]);
  }-*/;

  static native String charToString(char x) /*-{
    return String.fromCharCode(x);
  }-*/;

  static Object dynamicCast(Object src, int dstId) {
    if (src != null && !canCast(src, dstId)) {
      throw new ClassCastException();
    }
    return src;
  }

  /**
   * Allow a dynamic cast to an object, always succeeding if it's a JSO.
   */
  static Object dynamicCastAllowJso(Object src, int dstId) {
    if (src != null && !isJavaScriptObject(src) &&
        !canCast(src, dstId)) {
      throw new ClassCastException();
    }
    return src;
  }

  /**
   * Allow a cast to JSO only if there's no type ID.
   */
  static Object dynamicCastJso(Object src) {
    if (src != null && isJavaObject(src)) {
      throw new ClassCastException();
    }
    return src;
  }

  static boolean instanceOf(Object src, int dstId) {
    return (src != null) && canCast(src, dstId);
  }

  static boolean instanceOfJso(Object src) {
    return (src != null) && isJavaScriptObject(src);
  }

  /**
   * Returns true if the object is a Java object and can be cast, or if it's a
   * non-null JSO.
   */
  static boolean instanceOfOrJso(Object src, int dstId) {
    return (src != null) &&
        (isJavaScriptObject(src) || canCast(src, dstId));
  }

  static boolean isJavaObject(Object src) {
    return isNonStringJavaObject(src) || isJavaString(src);
  }

  static boolean isJavaScriptObject(Object src) {
    return !isNonStringJavaObject(src) && !isJavaString(src);
  }

  static boolean isJavaScriptObjectOrString(Object src) {
    return !isNonStringJavaObject(src);
  }

  /**
   * Uses the not operator to perform a null-check; do NOT use on anything that
   * could be a String.
   */
  static native boolean isNotNull(Object src) /*-{
    // Coerce to boolean.
    return !!src;
  }-*/;

  /**
   * Uses the not operator to perform a null-check; do NOT use on anything that
   * could be a String.
   */
  static native boolean isNull(Object src) /*-{
    return !src;
  }-*/;

  static native boolean jsEquals(Object a, Object b) /*-{
    return a == b;
  }-*/;

  static native boolean jsNotEquals(Object a, Object b) /*-{
    return a != b;
  }-*/;

  static native Object maskUndefined(Object src) /*-{
    return (src == null) ? null : src;
  }-*/;

  /**
   * See JLS 5.1.3.
   */
  static native byte narrow_byte(double x) /*-{
    return x << 24 >> 24;
  }-*/;

  /**
   * See JLS 5.1.3.
   */
  static native char narrow_char(double x) /*-{
    return x & 0xFFFF;
  }-*/;

  /**
   * See JLS 5.1.3.
   */
  static native int narrow_int(double x) /*-{
    return ~~x;
  }-*/;

  /**
   * See JLS 5.1.3.
   */
  static native short narrow_short(double x) /*-{
    return x << 16 >> 16;
  }-*/;

  /**
   * See JLS 5.1.3 for why we do a two-step cast. First we round to int, then
   * narrow to byte.
   */
  static byte round_byte(double x) {
    return narrow_byte(round_int(x));
  }

  /**
   * See JLS 5.1.3 for why we do a two-step cast. First we round to int, then
   * narrow to char.
   */
  static char round_char(double x) {
    return narrow_char(round_int(x));
  }

  /**
   * See JLS 5.1.3.
   */
  static native int round_int(double x) /*-{
    // TODO: reference java.lang.Integer::MAX_VALUE when we get clinits fixed
    return ~~Math.max(Math.min(x, 2147483647), -2147483648);
  }-*/;

  /**
   * See JLS 5.1.3 for why we do a two-step cast. First we rount to int, then
   * narrow to short.
   */
  static short round_short(double x) {
    return narrow_short(round_int(x));
  }

  /**
   * Check a statically false cast, which can succeed if the argument is null.
   * Called by compiler-generated code based on static type information.
   */
  static Object throwClassCastExceptionUnlessNull(Object o)
      throws ClassCastException {
    if (o != null) {
      throw new ClassCastException();
    }
    return o;
  }

  private static native JavaScriptObject getNullMethod() /*-{
    return @null::nullMethod();
  }-*/;

  /**
   * Returns whether the Object is a Java String.
   *
   * Java strings are translated to JavaScript strings.
   */
  // Visible for getIndexedMethod()
  static native boolean isJavaString(Object src) /*-{
    return typeof src.valueOf() == "string";
  }-*/;

  /**
   * Returns whether the Object is a Java Object but not a String.
   *
   * Depends on all Java Objects (except for String) having the typeMarker field
   * generated, and set to the nullMethod for the current GWT module.  Note this
   * test essentially tests whether an Object is a java object for the current
   * GWT module.  Java Objects from external GWT modules are not recognizable as
   * Java Objects in this context.
   */
  // Visible for getIndexedMethod()
  static boolean isNonStringJavaObject(Object src) {
    return Util.getTypeMarker(src) == getNullMethod();
  }
}

// CHECKSTYLE_NAMING_ON
