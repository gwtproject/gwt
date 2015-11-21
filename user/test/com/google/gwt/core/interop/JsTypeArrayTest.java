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
package com.google.gwt.core.interop;

import com.google.gwt.junit.client.GWTTestCase;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Tests JsType with array functionality.
 */
public class JsTypeArrayTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.Interop";
  }

  /* MAKE SURE EACH TYPE IS ONLY USED ONCE PER TEST CASE */

  @JsType(isNative = true)
  interface SimpleJsTypeReturnFromNative { }

  public void testJsTypeArray_returnFromNative() {
    SimpleJsTypeReturnFromNative[] array = returnJsTypeFromNative();
    assertEquals(2, array.length);
    assertNotNull(array[0]);
  }

  private native SimpleJsTypeReturnFromNative[] returnJsTypeFromNative() /*-{
    return [{}, {}];
  }-*/;

  @JsType(isNative = true)
  interface SimpleJsTypeReturnFromNativeWithAMethod {
    @JsProperty int getId();
  }

  public void testJsTypeArray_returnFromNativeWithACall() {
    SimpleJsTypeReturnFromNativeWithAMethod[] array = returnJsTypeWithIdsFromNative();
    assertEquals(2, array[1].getId());
  }

  private native SimpleJsTypeReturnFromNativeWithAMethod[] returnJsTypeWithIdsFromNative() /*-{
    return [{id:1}, {id:2}];
  }-*/;

  @JsType(isNative = true)
  interface SimpleJsTypeAsAField { }

  @JsType
  static class SimpleJsTypeAsAFieldHolder {
    public SimpleJsTypeAsAField[] arrayField;
  }

  // TODO(rluble): Needs fixes in ImlementCastsAndTypeChecks, ArrayNormalizer and maybe type oracle.
  public void testJsTypeArray_asAField() {
    SimpleJsTypeAsAFieldHolder holder = new SimpleJsTypeAsAFieldHolder();
    fillArrayField(holder);
    SimpleJsTypeAsAField[] array = holder.arrayField;
    assertEquals(2, array.length);
    assertNotNull(array[0]);
  }

  private native static void fillArrayField(SimpleJsTypeAsAFieldHolder holder) /*-{
    holder.arrayField = [{}, {}];
  }-*/;

  @JsType(isNative = true)
  interface SimpleJsTypeAsAParam { }

  @JsType
  static class SimpleJsTypeAsAParamHolder {
    private SimpleJsTypeAsAParam[] theParam;

    public void setArrayParam(SimpleJsTypeAsAParam[] param) {
      theParam = param;
    }
  }

  public void testJsTypeArray_asAParam() {
    SimpleJsTypeAsAParamHolder holder = new SimpleJsTypeAsAParamHolder();
    fillArrayParam(holder);
    SimpleJsTypeAsAParam[] array = holder.theParam;
    assertEquals(2, array.length);
    assertNotNull(array[0]);
  }

  private native void fillArrayParam(SimpleJsTypeAsAParamHolder holder) /*-{
    holder.setArrayParam([{}, {}]);
  }-*/;

  @JsType(isNative = true)
  static class SimpleJsTypeReturnForMultiDimArray {
    @JsProperty public native int getId();
  }

  public void testJsType3DimArray_castFromNativeWithACall() {
    SimpleJsTypeReturnForMultiDimArray[][][] array =
        (SimpleJsTypeReturnForMultiDimArray[][][]) returnJsType3DimFromNative();
    assertEquals(1, array.length);
    assertEquals(2, array[0].length);
    assertEquals(3, array[0][0].length);
    assertEquals(2, array[0][0][1].getId());
  }

  private native Object returnJsType3DimFromNative() /*-{
    return [ [ [{id:1}, {id:2}, {}], [] ] ];
  }-*/;

  private native SimpleJsTypeReturnForMultiDimArray getSimpleJsType(int i) /*-{
    return {id:i};
  }-*/;

  public void testObjectArray_castFromNative() {
    SimpleJsTypeReturnForMultiDimArray[] array =
        (SimpleJsTypeReturnForMultiDimArray[]) returnObjectArrayFromNative();
    try {
      assertNotNull((Object[]) array);

    } catch (ClassCastException expected) {
    }
    assertEquals(3, array.length);
    assertEquals("1", array[0]);
  }

  public void testJsTypeArray_objectArrayInterchangeability() {
    Object[] objArray = new Object[1];

    SimpleJsTypeReturnForMultiDimArray[][][] array =
        (SimpleJsTypeReturnForMultiDimArray[][][]) objArray;

    objArray[0] = new Object[2];
    ((Object[]) objArray[0])[0] = new Object[3];
    array[0][0][1] = getSimpleJsType(2);
    assertEquals(1, array.length);
    assertEquals(2, array[0].length);
    assertEquals(3, array[0][0].length);
    assertEquals(2, array[0][0][1].getId());
  }

  public void testObjectArray_instanceOf() {
    Object array = new Object[0];
    assertTrue(array instanceof Object[]);
    assertFalse(array instanceof Double[]);
    assertFalse(array instanceof int[]);
    assertFalse(array instanceof SimpleJsTypeReturnForMultiDimArray);
    assertTrue(array instanceof SimpleJsTypeReturnForMultiDimArray[]);
    assertTrue(array instanceof SimpleJsTypeReturnForMultiDimArray[][]);
    assertTrue(array instanceof SimpleJsTypeReturnForMultiDimArray[][][]);
  }

  public void testJsTypeArray_instanceOf() {
    Object array = returnJsType3DimFromNative();
    assertFalse(array instanceof Object[]);
    assertFalse(array instanceof Double[]);
    assertFalse(array instanceof int[]);
    assertFalse(array instanceof SimpleJsTypeReturnForMultiDimArray);
    assertTrue(array instanceof SimpleJsTypeReturnForMultiDimArray[]);
    assertTrue(array instanceof SimpleJsTypeReturnForMultiDimArray[][]);
    assertTrue(array instanceof SimpleJsTypeReturnForMultiDimArray[][][]);
  }

  private native Object returnObjectArrayFromNative() /*-{
    return ["1", "2", "3"];
  }-*/;
}
