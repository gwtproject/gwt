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

  static class D {
    public int d1 = Math.random() > 3 ? 0 : 1;
    class E {
      public D getD() {
        return D.this;
      }
      public int e1 = Math.random() > 3 ? 0 : 3;
      class F {
        public int f1 = Math.random() > 3 ? 0 : 4;
        public E getE() {
          return E.this;
        }
        public D getD() {
          return D.this;
        }
      }
    }
  }

  static class DD extends D {
    public int dd1 = Math.random() > 3 ? 0 : 5;
    class EE extends E {
      EE() {
      }

      EE(int ee2) {
        this.ee2 = ee2;
      }
      public int ee1 = Math.random() > 3 ? 0 : 6;
      public int ee2 = Math.random() > 3 ? 0 : 7;
      class FF extends F {
        public int ff1 = Math.random() > 3 ? 0 : 8;

        public String toString() {
          return "" + d1 + e1 + f1 + dd1 + ee1 + ee2 + ff1;
        }

        public EE getEE() {
          return EE.this;
        }

        public DD getDD() {
          return DD.this;
        }
      }

      public DD getDD() {
        return DD.this;
      }

      public FF unrelatedFF() {
       return new EE(9).new FF();
      }

      public FF unrelatedSubFF() {
        return new EE(9).new FF() {
          public String toString() {
            // The interesting thing here is which instance of EE ee2 refers to.
            // JDT thinks it refers to EE instance used as a qualifier where JDK thinks it refers to
            // the enclosing EE instance.
            return super.toString() + ee2;
          }
        };
      }
    }
  }

  public void testDoubleOuter_VeryNested() {
    final int v20 = Math.random() > 3 ? 0 : 20;
    final int v21 = Math.random() > 3 ? 0 : 21;

    DD  dd = new DD();
    DD.EE ee = dd.new EE();
    DD.EE.FF ff1 = ee.new FF();
    DD.EE.FF ff2 = ee.unrelatedFF();
    DD.EE.FF subff2 = ee.unrelatedSubFF();

    assertEquals("1345678", ff1.toString());
    assertEquals("1345698", ff2.toString());
    assertEquals("13456989", subff2.toString());

    assertSame(ff1.getDD(), ee.getDD());
    assertNotSame(ff1.getEE(), ff2.getEE());
  }
}
