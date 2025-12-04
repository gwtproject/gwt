/*
 * Copyright 2025 Google Inc.
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
package com.google.gwt.user.rebind.rpc.testcases.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface SimplifiedRecursiveTypeGraphInstantiability extends IsSerializable {
  /**
   * Not serializable; interface only.
   */
  interface A extends IsSerializable {
  }
  
  /**
   * Default serializable, but with back-reference to B for which the question of instantiable
   * subtypes depends on the serializability of this class. The reference to {@link C} which has a
   * back-reference to {@link A} and B being its only subtypes candidate helps reproduce issue
   * 10181.
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
