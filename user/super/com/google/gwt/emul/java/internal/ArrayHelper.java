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
package java.internal;


/**
 * Forwards array operations to GWT's internal array class.
 */
public class ArrayHelper {

  public static native <T> T[] cloneSubrange(T[] array, int fromIndex, int toIndex) /*-{
    return @com.google.gwt.lang.Array::cloneSubrange(*)(array, fromIndex, toIndex);
  }-*/;

  public static native <T> T[] createFrom(T[] array, int length) /*-{
    return @com.google.gwt.lang.Array::createFrom(*)(array, length);
  }-*/;

  public static native void nativeArraycopy(Object src, int srcOfs, Object dest, int destOfs,
      int len) /*-{
    @com.google.gwt.lang.Array::nativeArraycopy(*)(src, srcOfs, dest, destOfs, len);
  }-*/;

  public static native void nativeArrayInsert(Object src, int srcOfs, Object dest, int destOfs,
      int len)/*-{
    @com.google.gwt.lang.Array::nativeArrayInsert(*)(src, srcOfs, dest, destOfs, len);
  }-*/;
}

