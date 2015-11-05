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
package com.google.gwt.dev.jjs.test;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * This should probably be refactored at some point.
 */
public class MiscellaneousTest extends GWTTestCase {

  interface I {
  }

  interface IBar extends I {
  }

  interface IFoo extends I {
  }

  static class PolyA implements IFoo {
    @Override
    public String toString() {
      return "A";
    }
  }

  static class PolyB implements IBar {
    @Override
    public String toString() {
      return "B";
    }
  }

  private static class HasClinit {
    public static int i = 1;

    private static HasClinit sInstance = new HasClinit();

    public static int sfoo() {
      return sInstance.foo();
    }

    private static native void clinitInNative() /*-{
    }-*/;

    private int foo() {
      this.toString();
      return 3;
    }
  }

  private static volatile boolean FALSE = false;

  private static volatile boolean TRUE = true;


  private static native void clinitFromNative() /*-{
    @com.google.gwt.dev.jjs.test.MiscellaneousTest$HasClinit::i = 5;
  }-*/;

  private static native void noOp() /*-{
  }-*/;

  private static native void throwNativeException() /*-{
    var a; a.asdf();
  }-*/;

  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.jjs.CompilerSuite";
  }

  public void testAssociativityCond() {
    int result = (TRUE ? TRUE : FALSE) ? 100 : 200;
    assertEquals(100, result);
  }

  @SuppressWarnings("cast")
  public void testCasts() {
    Object o = FALSE ? (Object) new PolyA() : (Object) new PolyB();
    assertTrue(o instanceof I);
    assertFalse(o instanceof IFoo);
    assertTrue(o instanceof IBar);
    assertFalse(o instanceof PolyA);
    assertTrue(o instanceof PolyB);
    try {
      o = (PolyA) o;
      fail();
    } catch (ClassCastException e) {
    }
  }

  public void testClinit() {
    ++HasClinit.i;
    HasClinit x = new HasClinit();
    ++x.i;
    new HasClinit().i++;
    HasClinit.i /= HasClinit.i;
    HasClinit.sfoo();
    HasClinit.i /= HasClinit.sfoo();
    HasClinit.clinitInNative();
    clinitFromNative();
  }

  public void testExceptions() {
    int i;
    for (i = 0; i < 5; ++i) {
      boolean hitOuter = false;
      boolean hitInner = false;
      try {
        try {
          switch (i) {
            case 0:
              throw new RuntimeException();
            case 1:
              throw new IndexOutOfBoundsException();
            case 2:
              throw new Exception();
            case 3:
              throw new StringIndexOutOfBoundsException();
            case 4:
              throwNativeException();
          }
        } catch (StringIndexOutOfBoundsException e) {
          assertEquals(3, i);
        } finally {
          hitInner = true;
        }
      } catch (IndexOutOfBoundsException f) {
        assertEquals(1, i);
      } catch (JavaScriptException js) {
        assertEquals(4, i);
      } catch (RuntimeException g) {
        assertEquals(0, i);
      } catch (Throwable e) {
        assertEquals(2, i);
      } finally {
        assertTrue(hitInner);
        hitOuter = true;
      }
      assertTrue(hitOuter);
    }
    assertEquals(5, i);
  }

  public void testIssue2479() {
    if (TRUE) {
      FALSE = false;
    } else if (FALSE) {
      TRUE = true;
    } else {
      noOp();
    }
  }

  @Override
  public String toString() {
    return "com.google.gwt.dev.jjs.test.MiscellaneousTest";
  }
}
