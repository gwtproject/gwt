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
package com.google.gwt.dev.jjs.test;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;

import java.util.List;

/**
 * Tests the Java arrays.
 */
public class ArrayTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.jjs.CompilerSuite";
  }

  private native Object createJsArray(int length) /*-{
    return new Array(length);
  }-*/;

  @DoNotRunWith(Platform.Devel)
  public void testArrays() {
    int[] c = new int[] {1, 2};
    int[][] d = new int[][] { {1, 2}, {3, 4}};
    int[][][] e = new int[][][] { { {1, 2}, {3, 4}}, { {5, 6}, {7, 8}}};
    assertEquals("[I", c.getClass().getName());
    assertEquals("[[I", d.getClass().getName());
    assertEquals("[I", d[1].getClass().getName());
    assertEquals("[[[I", e.getClass().getName());
    assertEquals("[[I", e[1].getClass().getName());
    assertEquals("[I", e[1][1].getClass().getName());

    assertEquals(2, c[1]);
    assertEquals(3, d[1][0]);
    assertEquals(8, e[1][1][1]);

    int[][][] b = new int[3][2][1];
    b[2][1][0] = 1;
    b = new int[3][2][];
    b[2][1] = null;
    b = new int[3][][];
    b[2] = null;
  }

  interface I { }
  interface IBar extends I { }
  interface IFoo extends I { }
  static class PolyA implements IFoo { }
  static class PolyB implements IBar { }

  public void testArrayCasts() {
    {
      Object f1 = new IFoo[1];
      assertEquals("[L" + IFoo.class.getName() + ";", f1.getClass().getName());
      assertFalse(f1 instanceof PolyA[][]);
      assertFalse(f1 instanceof IFoo[][]);
      assertFalse(f1 instanceof PolyA[]);
      assertTrue(f1 instanceof IFoo[]);
      assertFalse(f1 instanceof PolyA);
      assertFalse(f1 instanceof IFoo);
      assertTrue(f1 instanceof Object[]);
      assertFalse(f1 instanceof Object[][]);

      assertAllCanStore((Object[]) f1, new Object[] {new PolyA(), new IFoo() {
      }});
      assertNoneCanStore((Object[]) f1, new Object[] {
          new PolyB(), new Object(), new Object[0]});
    }

    {
      Object a1 = new PolyA[1];
      assertEquals("[L" + PolyA.class.getName() + ";", a1.getClass().getName());
      assertFalse(a1 instanceof PolyA[][]);
      assertFalse(a1 instanceof IFoo[][]);
      assertTrue(a1 instanceof PolyA[]);
      assertTrue(a1 instanceof IFoo[]);
      assertFalse(a1 instanceof PolyA);
      assertFalse(a1 instanceof IFoo);
      assertTrue(a1 instanceof Object[]);
      assertFalse(a1 instanceof Object[][]);

      assertAllCanStore((Object[]) a1, new Object[] {new PolyA()});
      assertNoneCanStore((Object[]) a1, new Object[] {new IFoo() {
      }, new PolyB(), new Object(), new Object[0]});
    }

    {
      Object f2 = new IFoo[1][];
      assertEquals("[[L" + IFoo.class.getName() + ";", f2.getClass().getName());
      assertFalse(f2 instanceof PolyA[][]);
      assertTrue(f2 instanceof IFoo[][]);
      assertFalse(f2 instanceof PolyA[]);
      assertFalse(f2 instanceof IFoo[]);
      assertFalse(f2 instanceof PolyA);
      assertFalse(f2 instanceof IFoo);
      assertTrue(f2 instanceof Object[]);
      assertTrue(f2 instanceof Object[][]);

      assertAllCanStore((Object[]) f2, new Object[] {new PolyA[0], new IFoo[0]});
      assertNoneCanStore((Object[]) f2, new Object[] {new IFoo() {
      }, new PolyB(), new Object(), new Object[0]});
    }

    {
      Object a2 = new PolyA[1][];
      assertEquals("[[L" + PolyA.class.getName() + ";", a2.getClass().getName());
      assertTrue(a2 instanceof PolyA[][]);
      assertTrue(a2 instanceof IFoo[][]);
      assertFalse(a2 instanceof PolyA[]);
      assertFalse(a2 instanceof IFoo[]);
      assertFalse(a2 instanceof PolyA);
      assertFalse(a2 instanceof IFoo);
      assertTrue(a2 instanceof Object[]);
      assertTrue(a2 instanceof Object[][]);

      assertAllCanStore((Object[]) a2, new Object[] {new PolyA[0]});
      assertNoneCanStore((Object[]) a2, new Object[] {new IFoo() {
      }, new PolyB(), new Object(), new Object[0], new IFoo[0]});
    }
  }

  public void testObjectArray_empty() {
    Object nativeArray = createJsArray(0);
    assertTrue(nativeArray instanceof Object[]);
    assertFalse(nativeArray instanceof Object[][]);
    assertFalse(nativeArray instanceof int[]);
    assertFalse(nativeArray instanceof List[]);
    assertTrue(nativeArray.getClass() == Object[].class);

    Object objectArray = new Object[0];
    assertTrue(objectArray instanceof Object[]);
    assertFalse(objectArray instanceof Object[][]);
    assertFalse(objectArray instanceof int[]);
    assertFalse(objectArray instanceof List[]);
    assertTrue(objectArray.getClass() == Object[].class);

    assertFalse(objectArray.equals(nativeArray));
  }

  @DoNotRunWith(Platform.Devel)
  public void testObjectArray_nonEmpty() {
    // Native array is an object array
    Object nativeArray = createJsArray(10);
    assertTrue(nativeArray instanceof Object[]);
    assertFalse(nativeArray instanceof Object[][]);
    assertFalse(nativeArray instanceof int[]);
    assertFalse(nativeArray instanceof List[]);
    assertTrue(nativeArray.getClass() == Object[].class);

    Object objectArray = new Object[10];
    assertTrue(objectArray instanceof Object[]);
    assertFalse(objectArray instanceof Object[][]);
    assertFalse(objectArray instanceof int[]);
    assertFalse(objectArray instanceof List[]);
    assertTrue(objectArray.getClass() == Object[].class);

    assertFalse(objectArray.equals(nativeArray));
  }

  public void testObjectObjectArray() {
    Object array = new Object[10][];
    assertTrue(array instanceof Object[]);
    assertTrue(array instanceof Object[][]);
    assertFalse(array instanceof int[]);
    assertFalse(array instanceof List[]);
    assertTrue(array.getClass() == Object[][].class);

    Object[] objectArray = (Object[]) array;
    objectArray[0] = new Object[0];
    objectArray[1] = new List[1];
    objectArray[2] = new Double[1];

    try {
      objectArray[3] = new int[1];
      fail("Should have thrown ArrayStoreException");
    } catch (ArrayStoreException expected) {
    }

    try {
      objectArray[4] = new Object();
      fail("Should have thrown ArrayStoreException");
    } catch (ArrayStoreException expected) {
    }
  }

  public void testArrayToString() {
    Object[] array = new Object[] { 1, 2 ,3 };
    assertEquals(Object[].class.getName(), ((Object) array).toString().split("@")[0]);
  }

  public void testPrimitiveArrayCasting() {
    Object array = new long[1];

    try {
      assertNotNull((Object[]) array);
      fail("Should have thrown ClassCastException");
    } catch (ClassCastException expected) {
    }

    Object[][] objectArrayArray = new Object[10][10];

    try {
      ((Object[]) objectArrayArray)[0] = array;
      fail("Should have thrown ArrayStoreException");
    } catch (ArrayStoreException expected) {
    }
  }

  /**
   * Ensures that dispatch to JavaScript native arrays that are NOT Java arrays works properly.
   */
  @DoNotRunWith(Platform.Devel)
  public void testNativeJavaScriptArray() {
    Object jsoArray = JavaScriptObject.createArray(10);
    assertEquals(Object[].class, jsoArray.getClass());
    assertTrue(jsoArray instanceof JavaScriptObject);
    assertTrue(jsoArray instanceof Object[]);

    Object objectArray = new Object[10];
    assertEquals(Object[].class, objectArray.getClass());
    assertTrue(objectArray instanceof Object[]);
    assertTrue(objectArray instanceof JavaScriptObject);

    assertTrue(jsoArray.toString().split("@")[0].equals(objectArray.toString().split("@")[0]));
  }

  private static void assertAllCanStore(Object[] dest, Object[] src) {
    for (int i = 0; i < src.length; ++i) {
      dest[0] = src[i];
    }
  }

  private static void assertNoneCanStore(Object[] dest, Object[] src) {
    for (int i = 0; i < src.length; ++i) {
      try {
        dest[0] = src[i];
        fail();
      } catch (ArrayStoreException e) {
      }
    }
  }
}
