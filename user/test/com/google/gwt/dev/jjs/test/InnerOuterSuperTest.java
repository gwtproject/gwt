/*
 * Copyright 2007 Google Inc.
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

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests several inner class scenarios.
 */
public class InnerOuterSuperTest extends GWTTestCase {

  static class Outer {
    class OuterIsNotSuper {

      public int getValue() {
        return value;
      }
    }

    class OuterIsSuper extends Outer {

      public OuterIsSuper(int i) {
        super(i);
      }

      @Override
      public int checkDispatch() {
        return 2;
      }
      
      public int checkDispatchFromSub1() {
        return super.checkDispatch();
      }

      public int checkDispatchFromSub2() {
        return new Outer(1) {
          public int go() {
            return OuterIsSuper.super.checkDispatch();
          }
        }.go();
      }

      public OuterIsNotSuper unqualifiedAlloc() {
        return new OuterIsNotSuper();
      }
    }

    static class TestQualifiedSuperCall extends OuterIsNotSuper {
      public TestQualifiedSuperCall() {
        new Outer(1).new OuterIsSuper(2).super();
      }
    }

    class TestUnqualifiedSuperCall extends OuterIsNotSuper {
      public TestUnqualifiedSuperCall() {
        super();
      }
    }

    protected final int value;

    public Outer(int i) {
      value = i;
    }
    
    public int checkDispatch() {
      return 1;
    }
  }

  private final Outer outer  = new Outer(1);

  private final Outer.OuterIsSuper outerIsSuper = outer.new OuterIsSuper(2);

  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.jjs.CompilerSuite";
  }

  public void testOuterIsNotSuper() {
    Outer.OuterIsNotSuper x = outerIsSuper.new OuterIsNotSuper();
    assertEquals(2, x.getValue());
  }

  public void testOuterIsNotSuperAnon() {
    Outer.OuterIsNotSuper x = outerIsSuper.new OuterIsNotSuper() {
    };
    assertEquals(2, x.getValue());
  }

  public void testQualifiedSuperCall() {
    Outer.TestQualifiedSuperCall x = new Outer.TestQualifiedSuperCall();
    assertEquals(2, x.getValue());
  }

  public void testQualifiedSuperCallAnon() {
    Outer.TestQualifiedSuperCall x = new Outer.TestQualifiedSuperCall() {
    };
    assertEquals(2, x.getValue());
  }

  public void testSuperDispatch() {
    assertEquals(1, outerIsSuper.checkDispatchFromSub1());
    assertEquals(1, outerIsSuper.checkDispatchFromSub2());
  }

  public void testUnqualifiedAlloc() {
    Outer.OuterIsNotSuper x = outerIsSuper.unqualifiedAlloc();
    assertEquals(2, x.getValue());
  }

  public void testUnqualifiedSuperCall() {
    Outer.TestUnqualifiedSuperCall x = outerIsSuper.new TestUnqualifiedSuperCall();
    assertEquals(2, x.getValue());
  }

  public void testUnqualifiedSuperCallAnon() {
    Outer.TestUnqualifiedSuperCall x = outerIsSuper.new TestUnqualifiedSuperCall() {
    };
    assertEquals(2, x.getValue());
  }

  static class A {
    class B {
      public int v1 = Math.random() > 3 ? 0 : 1;
    }
  }

  int v2 = Math.random() > 3 ? 0 : 2;

  public void testDoubleOuter() {
    final int v3 = Math.random() > 3 ? 0 : 3;
    final int v4 = Math.random() > 3 ? 0 : 4;
    A.B ab = new A().new B() {
      int mv1 = v1;
      int mv2 = v2;
      int mv3 = v3;
      int mv4 = v4;

      public String toString() {
        return "" + mv1 + mv2 + mv3 + mv4 + " " + v1 + v2 + v3 + v4;
      }
    };
    assertEquals("1234 1234", ab.toString());
  }
}
