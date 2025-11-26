package com.google.gwt.user.rebind.rpc.testcases.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface RecursiveTypeGraphInstantiability extends IsSerializable {
  /**
   * Not serializable.
   */
  interface A extends IsSerializable {
  }

  /**
   * Auto serializable, but with back-reference to A for which the question of instantiable subtypes
   * depends on the serializability of this class; all other sub-classes are not instantiable.
   */
  class B implements A {
    private A a;

    public A getA() {
      return a;
    }

    public void setA(A a) {
      this.a = a;
    }
  }

  /**
   * Not serializable due to Object field.
   */
  class C extends B {
    Object field;
  }

  /**
   * Not instantiable either, due to non-default constructor and final field
   */
  class D implements A {
    private final int i;

    public D(int i) {
      super();
      this.i = i;
    }

    public int getI() {
      return i;
    }
  }

  /**
   * Not instantiable; see {@link D}
   */
  class E implements A {
    private final int i;

    public E(int i) {
      super();
      this.i = i;
    }

    public int getI() {
      return i;
    }
  }

  /**
   * Not instantiable; see {@link D}
   */
  class F implements A {
    private final int i;

    public F(int i) {
      super();
      this.i = i;
    }

    public int getI() {
      return i;
    }
  }

  /**
   * Not instantiable; see {@link D}
   */
  class G implements A {
    private final int i;

    public G(int i) {
      super();
      this.i = i;
    }

    public int getI() {
      return i;
    }
  }

  A getA();
}
