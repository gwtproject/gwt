package com.google.gwt.user.rebind.rpc.testcases.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface SimplifiedRecursiveTypeGraphInstantiability extends IsSerializable {
  /**
   * Not serializable; interface only
   */
  interface A extends IsSerializable {
  }
  
  /**
   * Default serializable, but with back-reference to B for which the question of instantiable subtypes
   * depends on the serializability of this class. The reference to {@link C} which
   * has a back-reference to {@link A} and B being its only subtypes candidate
   * helps reproduce issue 10181.
   */
  class B implements A {
    B b;
    C c;
  }

  class C implements IsSerializable {
    A A;
  }

  A getA();
}
