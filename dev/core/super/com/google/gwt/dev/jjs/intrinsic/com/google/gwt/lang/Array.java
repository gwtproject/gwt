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

import static com.google.gwt.core.shared.impl.InternalPreconditions.checkArrayType;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.impl.DoNotInline;
import com.google.gwt.core.client.impl.HasNoSideEffects;

/**
 * This is an intrinsic class that contains the implementation details for Java arrays. <p>
 *
 * This class should contain only static methods or fields.
 */
public final class Array {
  // Array element type classes
  private static final int TYPE_JAVA_OBJECT = 0;
  private static final int TYPE_JAVA_OBJECT_OR_JSO = 1;
  private static final int TYPE_JSO = 2;
  private static final int TYPE_JAVA_LANG_OBJECT = 3;
  private static final int TYPE_JAVA_LANG_STRING = 4;
  private static final int TYPE_JS_INTERFACE = 5;
  private static final int TYPE_PRIMITIVE_LONG = 6;
  private static final int TYPE_PRIMITIVE_NUMBER = 7;
  private static final int TYPE_PRIMITIVE_BOOLEAN = 8;

  private static final int ARRAY_PROCESS_BATCH_SIZE = 10000;

  /**
   * Creates a copy of the specified array.
   */
  public static <T> T[] clone(T[] array) {
    return cloneSubrange(array, 0, array.length);
  }

  /**
   * Creates a copy of a subrange of the specified array.
   */
  public static <T> T[] cloneSubrange(T[] array, int fromIndex, int toIndex) {
    Object result = arraySlice(array, fromIndex, toIndex);
    initValues(array.getClass(), Util.getCastableTypeMap(array), Array.getElementTypeId(array),
        Array.getElementTypeCategory(array), result);
    // implicit type arg not inferred (as of JDK 1.5.0_07)
    return Array.<T> asArray(result);
  }

  /**
   * Creates a new array of the exact same type and length as a given array.
   */
  public static <T> T[] createFrom(T[] array) {
    return createFrom(array, array.length);
  }

  /**
   * Creates an empty array of the exact same type as a given array, with the
   * specified length.
   */
  public static <T> T[] createFrom(T[] array, int length) {
    // TODO(rluble): The behaviour here seems erroneous as the array elements will not be
    // initialized but left undefined. However the usages seem to be safe and changing here
    // might have performace penalty. Maybe rename to createUninitializedFrom(), to make
    // the meaning clearer.
    Object result = initializeArrayElementsWithDefaults(TYPE_JAVA_OBJECT, length);
    initValues(array.getClass(), Util.getCastableTypeMap(array), Array.getElementTypeId(array),
        Array.getElementTypeCategory(array), result);
    // implicit type arg not inferred (as of JDK 1.5.0_07)
    return Array.<T> asArray(result);
  }

  /**
   * Creates an array like "new T[a][b][c][][]" by passing in a native JSON
   * array, [a, b, c].
   *
   * @param leafClassLiteral the class literal for the leaf class
   * @param castableTypeMap the map of types to which this array can be casted,
   *          in the form of a JSON map object
   * @param elementTypeId the typeId of array elements
   * @param elementTypeCategory whether the element type is java.lang.Object
   *        ({@link TYPE_JAVA_LANG_OBJECT}), is guaranteed to be a java object
   *        ({@link TYPE_JAVA_OBJECT}), is guaranteed to be a JSO
   *        ({@link TYPE_JSO}), can be either ({@link TYPE_JAVA_OBJECT_OR_JSO}) or
   *        or some primitive type {@link TYPE_PRIMITIVE_BOOLEAN}, {@link TYPE_PRIMITIVE_LONG} or
   *        {@link TYPE_PRIMITIVE_NUMBER}.
   * @param length the length of the array
   * @param dimensions the number of dimensions of the array
   * @return the new array
   */
  public static Object initDim(Class<?> leafClassLiteral, JavaScriptObject castableTypeMap,
      JavaScriptObject elementTypeId, int length, int elementTypeCategory, int dimensions) {
    Object result = initializeArrayElementsWithDefaults(elementTypeCategory, length);
    initValues(getClassLiteralForArray(leafClassLiteral, dimensions), castableTypeMap,
        elementTypeId, elementTypeCategory, result);
    return result;
  }

  /**
   * Creates an array like "new T[a][b][c][][]" by passing in a native JSON
   * array, [a, b, c].
   *
   * @param leafClassLiteral the class literal for the leaf class
   * @param castableTypeMapExprs the JSON castableTypeMap of each dimension,
   *          from highest to lowest
   * @param elementTypeIds the elementTypeId of each dimension, from highest to lowest
   * @param leafElementTypeCategory whether the element type is java.lang.Object
   *        ({@link TYPE_JAVA_LANG_OBJECT}), is guaranteed to be a java object
   *        ({@link TYPE_JAVA_OBJECT}), is guaranteed to be a JSO
   *        ({@link TYPE_JSO}), can be either ({@link TYPE_JAVA_OBJECT_OR_JSO}) or
   *        or some primitive type {@link TYPE_PRIMITIVE_BOOLEAN}, {@link TYPE_PRIMITIVE_LONG} or
   *        {@link TYPE_PRIMITIVE_NUMBER}.
   * @param dimExprs the length of each dimension, from highest to lower
   * @return the new array
   */
  public static Object initDims(Class<?> leafClassLiteral, JavaScriptObject[] castableTypeMapExprs,
      JavaScriptObject[] elementTypeIds, int leafElementTypeCategory, int[] dimExprs, int count) {
    return initDims(leafClassLiteral, castableTypeMapExprs, elementTypeIds, leafElementTypeCategory,
        dimExprs, 0, count);
  }

  /**
   * Creates an array like "new T[][]{a,b,c,d}" by passing in a native JSON
   * array, [a, b, c, d].
   *
   * @param arrayClass the class of the array
   * @param castableTypeMap the map of types to which this array can be casted,
   *          in the form of a JSON map object
   * @param elementTypeId the typeId of array elements
   * @param elementTypeCategory whether the element type is java.lang.Object
   *        ({@link TYPE_JAVA_LANG_OBJECT}), is guaranteed to be a java object
   *        ({@link TYPE_JAVA_OBJECT}), is guaranteed to be a JSO
   *        ({@link TYPE_JSO}), can be either ({@link TYPE_JAVA_OBJECT_OR_JSO}) or
   *        or some primitive type {@link TYPE_PRIMITIVE_BOOLEAN}, {@link TYPE_PRIMITIVE_LONG} or
   *        {@link TYPE_PRIMITIVE_NUMBER}.
   * @param array the JSON array that will be transformed into a GWT array
   * @return values; having wrapped it for GWT
   */
  public static Object initValues(Class<?> arrayClass, JavaScriptObject castableTypeMap,
      JavaScriptObject elementTypeId, int elementTypeCategory, Object array) {
    setClass(array, arrayClass);
    Util.setCastableTypeMap(array, castableTypeMap);
    Util.setTypeMarker(array);
    Array.setElementTypeId(array, elementTypeId);
    Array.setElementTypeCategory(array, elementTypeCategory);
    return array;
  }

  /**
   * Copy an array using native Javascript. The destination array must be a real
   * Java array (ie, already has the GWT type info on it). No error checking is performed -- the
   * caller is expected to have verified everything first.
   *
   * @param src source array for copy
   * @param srcOfs offset into source array
   * @param dest destination array for copy
   * @param destOfs offset into destination array
   * @param len number of elements to copy
   */
  public static void nativeArraycopy(Object src, int srcOfs, Object dest, int destOfs, int len) {
    nativeArraySplice(src, srcOfs, dest, destOfs, len, true);
  }

  /**
   * Insert one array into another native Javascript. The destination array must be a real
   * Java array (ie, already has the GWT type info on it). No error checking is performed -- the
   * caller is expected to have verified everything first.
   *
   * @param src source array where the data is taken from
   * @param srcOfs offset into source array
   * @param dest destination array for the data to be inserted
   * @param destOfs offset into destination array
   * @param len number of elements to insert
   */
  public static void nativeArrayInsert(Object src, int srcOfs, Object dest, int destOfs,
      int len) {
    nativeArraySplice(src, srcOfs, dest, destOfs, len, false);
  }

  /**
   * A replacement for Array.prototype.splice to overcome the limits imposed to the number of
   * function parameters by browsers.
   */
  private static native void nativeArraySplice(
      Object src, int srcOfs, Object dest, int destOfs, int len, boolean overwrite) /*-{
    // Work around function.prototype.apply call stack size limits:
    // https://code.google.com/p/v8/issues/detail?id=2896
    // Performance: http://jsperf.com/java-system-arraycopy/2
    if (src === dest) {
      // copying to the same array, make a copy first
      src = src.slice(srcOfs, srcOfs + len);
      srcOfs = 0;
    }
    for (var batchStart = srcOfs, end = srcOfs + len; batchStart < end;) { // increment in block
      var batchEnd = Math.min(batchStart + @Array::ARRAY_PROCESS_BATCH_SIZE, end);
      len = batchEnd - batchStart;
      Array.prototype.splice.apply(dest, [destOfs, overwrite ? len : 0]
          .concat(src.slice(batchStart, batchEnd)));
      batchStart = batchEnd;
      destOfs += len;
    }
  }-*/;

  /**
   * Performs an array assignment, after validating the type of the value being
   * stored. The form of the type check depends on the value of elementTypeId and
   * elementTypeCategory as follows:
   * <p>
   * If the elementTypeCategory is {@link TYPE_JAVA_OBJECT}, this indicates a normal cast check
   * should be performed, using the elementTypeId as the cast destination type.
   * JavaScriptObjects cannot be stored in this case.
   * <p>
   * If the elementTypeId is {@link TYPE_JAVA_LANG_OBJECT}, this is the cast target for the Object
   * type, in which case all types can be stored, including JavaScriptObject.
   * <p>
   * If the elementTypeId is {@link TYPE_JSO}, this indicates that only JavaScriptObjects can be
   * stored.
   * <p>
   * If the elementTypeId is {@link TYPE_JAVA_OBJECT_OR_JSO}, this indicates that both
   * JavaScriptObjects, and Java types can be stored. In the case of Java types, a normal cast check
   * should be performed, using the elementTypeId as the cast destination type.
   * This case is provided to support arrays declared with an interface type, which has dual
   * implementations (i.e. interface types which have both Java and JavaScriptObject
   * implementations).
   * <p>
   * Attempting to store an object that cannot satisfy the castability check
   * throws an {@link ArrayStoreException}.
   */
  public static Object setCheck(Object array, int index, Object value) {
    checkArrayType(value == null || canSet(array, value));
    return set(array, index, value);
  }

  @HasNoSideEffects
  private static boolean canSet(Object array, Object value) {
    switch (Array.getElementTypeCategory(array)) {
      case TYPE_JAVA_LANG_STRING:
        return Cast.isJavaString(value);
      case TYPE_JAVA_OBJECT:
        return Cast.canCast(value, Array.getElementTypeId(array));
      case TYPE_JSO:
        return Cast.isJavaScriptObject(value);
      case TYPE_JAVA_OBJECT_OR_JSO:
        return Cast.isJavaScriptObject(value)
            || Cast.canCast(value, Array.getElementTypeId(array));
      default:
        return true;
    }
  }

  private static native Object arraySlice(Object array, int fromIndex, int toIndex) /*-{
    return array.slice(fromIndex, toIndex);
  }-*/;

  /**
   * Use JSNI to effect a castless type change.
   */
  private static native <T> T[] asArray(Object array) /*-{
    return array;
  }-*/;

  /**
   * Creates a primitive JSON array of a given the element type class.
   */
  private static native Object initializeArrayElementsWithDefaults(
      int elementTypeCategory, int length) /*-{
    var array = new Array(length);
    var initValue;
    switch (elementTypeCategory) {
      case @com.google.gwt.lang.Array::TYPE_PRIMITIVE_LONG:
        // Fill array with the type used by LongLib
        // TODO(rluble): This should refer to the zero long value defined in LongLib
        initValue = {l: 0, m: 0, h:0};
        break;
      case @com.google.gwt.lang.Array::TYPE_PRIMITIVE_NUMBER:
          initValue = 0;
        break;
      case @com.google.gwt.lang.Array::TYPE_PRIMITIVE_BOOLEAN:
        initValue = false;
        break;
      default:
        // Do not initialize as undefined is equivalent to null
        return array;
    }

    for ( var i = 0; i < length; ++i) {
      array[i] = initValue;
    }
    return array;
  }-*/;

  private static Object initDims(Class<?> leafClassLiteral, JavaScriptObject[] castableTypeMapExprs,
      JavaScriptObject[] elementTypeIds, int leafElementTypeCategory, int[] dimExprs,
      int index, int count) {
    int length = dimExprs[index];
    boolean isLastDim = (index == (count - 1));
    // All dimensions but the last are plain reference types.
    int elementTypeCategory = isLastDim ? leafElementTypeCategory : TYPE_JAVA_OBJECT;

    Object result = initializeArrayElementsWithDefaults(elementTypeCategory, length);
    initValues(getClassLiteralForArray(leafClassLiteral, count - index),
        castableTypeMapExprs[index], elementTypeIds[index], elementTypeCategory, result);

    if (!isLastDim) {
      // Recurse to next dimension.
      ++index;
      for (int i = 0; i < length; ++i) {
        set(result, i, initDims(leafClassLiteral, castableTypeMapExprs,
            elementTypeIds, leafElementTypeCategory, dimExprs, index, count));
      }
    }
    return result;
  }

  // This method is package protected so that it is indexed. {@link ImplementClassLiteralsAsFields}
  // will insert calls to this method when array class literals are constructed.
  //
  // Inlining is prevented on this very hot method to avoid a subtantial increase in
  // {@link JsInliner} execution time.
  @DoNotInline
  static <T> Class<T> getClassLiteralForArray(Class<?> clazz , int dimensions) {
    return getClassLiteralForArrayImpl(clazz, dimensions);
  }

  // DO NOT INLINE this method into {@link getClassLiteralForArray}.
  // The purpose of this method is to avoid introducing a public api to {@link java.lang.Class}.
  private static native <T>  Class<T> getClassLiteralForArrayImpl(
      Class<?> clazz , int dimensions) /*-{
    return @java.lang.Class::getClassLiteralForArray(*)(clazz, dimensions);
  }-*/;

  /**
   * Sets a value in the array.
   */
  private static native Object set(Object array, int index, Object value) /*-{
    return array[index] = value;
  }-*/;

  // violator pattern so that the field remains private
  private static native void setClass(Object o, Class<?> clazz) /*-{
    o.@java.lang.Object::___clazz = clazz;
  }-*/;

  private static native void setElementTypeId(Object array, JavaScriptObject elementTypeId) /*-{
    array.__elementTypeId$ = elementTypeId;
  }-*/;

  private static native JavaScriptObject getElementTypeId(Object array) /*-{
    return array.__elementTypeId$;
  }-*/;

  private static native void setElementTypeCategory(Object array, int elementTypeCategory) /*-{
    array.__elementTypeCategory$ = elementTypeCategory;
  }-*/;

  private static native int getElementTypeCategory(Object array) /*-{
    return array.__elementTypeCategory$;
  }-*/;

  private Array() {
  }
}

