/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.core.shared;

import com.google.gwt.core.client.GWT;

/**
 * This is a "magic interface" to ease the use of GWT.create.
 * It allows you to pass around references to class literals that still work in GWT.create.
 * <p>
 * To use:<pre>
 * GwtCreatable classLit = GWT.create(MyClass.class);
 * MyClass classLit = GWT.create(classLit.getCreatable());
 * </pre><p>
 * Do NOT extend this class unless you are returning a class literal,
 * a final reference to a class literal, a non-jsni method with only one return statement,
 * which must return a class literal (or any combination thereof).
 * <p>
 * The compiler still needs to be able to trace down a class literal from the source code.
 * <p>
 * The AST visitor only exposes the runtime scope of one block of code at a time;
 * we cannot trace through method parameters, but we can trace through source code.
 * <p>
 * When we pass a Class through a method, the scope of what was passed is not available;
 * everything just looks like an object of type class.  
 * <p>
 * GwtCreatable, however, is turned into a class which returns the literal,
 * thus allowing the compiler to look at the JExpression passed to it,
 * grab its methods, and look at the return statement, which points to a class literal.
 * 
 * @author "james@wetheinter.net"
 *
 */
public abstract class GwtCreatable <T> {

  private final Class<T> creatable;

  protected GwtCreatable(Class<T> creatable) {
    this.creatable = creatable;
  }

  public Class<T> getCreatable() {
    return creatable;
  }

  /**
   * This is a magic method!
   * <p>
   * The gwt compiler will be transforming this call to "enhance" the returned object;
   * it will actually be a dynamic subclass with .create() explcitly calling GWT.create(T.class);
   * 
   * @param clazz - A class literal (or direct reference to a class literal)
   * @return - A factory capable of calling GWT.create on the resolved literal sent to this method.
   * <p>
   * Note, "direct reference" means any combination of:
   * <ul><li>
   * a final local variable with an initializer pointing to class literal
   * </li><li>
   * a final field with an initializer pointing to class literal
   * (setting in the constructor is not good enough)
   * </li><li>
   * a static method with only one return statement, returning a class literal
   * </li><li>
   * any combination of the above methods of reference.
   * </li></ul>
   * <br>
   * None of the above methods enable passing class literals through method calls,
   * but GwtCreatable can encapsulate your class literal in a typesafe container.
   * 
   */
  @SuppressWarnings("unchecked")
  public static <R, T extends R> GwtCreatable<R> create(final Class<T> clazz) {
    return (GwtCreatable<R>) new GwtCreatable<T>(clazz) {
      @Override
      public T create() {
        // Compiler never gets here; only gwt dev uses this
        return GWT.create(clazz);
      }
    };
  }

  public abstract T create();

  @Override
  public boolean equals(Object obj) {
    return obj == this
        || (obj instanceof GwtCreatable && getCreatable() == ((GwtCreatable<?>) obj).getCreatable());
  }

  @Override
  public int hashCode() {
    return getCreatable().hashCode();
  }
}
