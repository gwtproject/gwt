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
package java.lang;

import com.google.gwt.core.client.UnsafeNativeLong;

/**
 * Array_Helper allows for different treatment of GWT's arrays vs. j2cl's arrays.
 *
 * <p>For GWT this version of the class is used refering to GWT's Arrays class, while in j2cl
 * this classes is super sourced with JavaScript.
 */
public class Array_Helper {
  
  public static native <T> T[] clone(T[] array) /*-{
    return @com.google.gwt.lang.Array::clone(*)(array);
  }-*/;
  
  public static native <T> T[] cloneSubrange(T[] array, int fromIndex, int toIndex) /*-{
    return @com.google.gwt.lang.Array::cloneSubrange(*)(array, fromIndex, toIndex);
  }-*/;
  
  public static native <T> T[] createFrom(T[] array) /*-{
    return @com.google.gwt.lang.Array::createFrom(*)(array, array.length);
  }-*/;

  public static native <T> T[] createFrom(T[] array, int length) /*-{
    return @com.google.gwt.lang.Array::createFrom(*)(array, length);
  }-*/;
  
  public static native void nativeArraycopy(Object src, int srcOfs, Object dest, int destOfs,
      int len) /*-{
    @com.google.gwt.lang.Array::nativeArraycopy(*)(src, srcOfs, dest, destOfs, len);
  }-*/;
  
  public static native void nativeArrayInsert(Object src, int srcOfs, Object dest, int destOfs,
      int len) /*-{
    @com.google.gwt.lang.Array::nativeArrayInsert(*)(src, srcOfs, dest, destOfs, len);
  }-*/;

  /**
   * Returns the length of an array via Javascript.
   */
  public static native int getArrayLength(Object array) /*-{
    return array.length;
  }-*/;
  
  public static native void setArrayLength(Object array, int newSize) /*-{
    array.length = newSize;
  }-*/;
  
  public static native void splice(Object[] array, int index, int deleteCount) /*-{
    array.splice(index, deleteCount);
  }-*/;

  public static native void splice(Object[] array, int index, int deleteCount,
    Object value) /*-{
    array.splice(index, deleteCount, value);
  }-*/;
  
  @UnsafeNativeLong
  public static native void nativeLongSort(Object array) /*-{
    array.sort(@com.google.gwt.lang.LongLib::compare(Lcom/google/gwt/lang/LongLibBase$LongEmul;Lcom/google/gwt/lang/LongLibBase$LongEmul;));
  }-*/;

  /**
   * Sort a subset of an array of number primitives.
   */
  @UnsafeNativeLong
  public static native void nativeLongSort(Object array, int fromIndex,
      int toIndex) /*-{
    var temp = array.slice(fromIndex, toIndex);
    temp.sort(@com.google.gwt.lang.LongLib::compare(Lcom/google/gwt/lang/LongLibBase$LongEmul;Lcom/google/gwt/lang/LongLibBase$LongEmul;));
    var n = toIndex - fromIndex;
    @com.google.gwt.lang.Array::nativeArraycopy(Ljava/lang/Object;ILjava/lang/Object;II)(
        temp, 0, array, fromIndex, n)
  }-*/;

  /**
   * Sort an entire array of number primitives.
   */
  public static native void nativeNumberSort(Object array) /*-{
    array.sort(function(a, b) {
      return a - b;
    });
  }-*/;

  /**
   * Sort a subset of an array of number primitives.
   */
  public static native void nativeNumberSort(Object array, int fromIndex,
      int toIndex) /*-{
    var temp = array.slice(fromIndex, toIndex);
    temp.sort(function(a, b) {
      return a - b;
    });
    var n = toIndex - fromIndex;
    @com.google.gwt.lang.Array::nativeArraycopy(Ljava/lang/Object;ILjava/lang/Object;II)(
        temp, 0, array, fromIndex, n)
  }-*/;
}

