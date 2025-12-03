package com.google.gwt.user.rebind.rpc.testcases.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface RecursiveTypeGraphInstantiability extends IsSerializable {
  /**
   * Not serializable; interface only
   */
  interface A extends IsSerializable {
  }
  
  /**
   * Not serializable; interface only, the second interface implemented by {@link B}
   */
  interface D extends IsSerializable {
  }

  /**
   * Default-serializable, but with back-reference to A for which the question of instantiable subtypes
   * depends on the serializability of this class. The reference to {@link C} which
   * has a back-reference to {@link B} and B being its only subtypes candidate
   * helps reproduce issue 10181.
   */
  class B implements A, D {
    A a;
    B b;
    C c;
  }

  /**
   * Default-serializable with back-reference to B through D, a different interface, triggering another
   * descent through checkSubtypes, finding an already "done" TIC for {@link B} with
   * {@code instantiable==false}. Being "done", it is used to decide instantiability of the only
   * subclass candidate of {@link D}, which is {@link B}, resulting in {@code false} for
   * the instantiability of {@link D}, and thus of {@link C}, and thus of {@link B}, causing
   * the test case to fail prior to a fix for issue 10181.
   */
  class C implements IsSerializable {
    D d;
  }

  A getA();
}
