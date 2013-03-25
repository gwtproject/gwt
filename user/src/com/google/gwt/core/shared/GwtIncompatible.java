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
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An example of a GwtIncompatible annotation.
 *
 * Any class, method or field with an annotation @GwtIncompatible (with any package prefix) is
 * ignored by the GWT compiler.
 *
 * It is recommended that projects create their own copy to allow for dependency-free usage.
 *
 * For example:
 *
 * {@code
 * class A {
 *
 *   int field;
 *
 *   @GwtIncompatible("incompatible class")
 *   class Inner {
 *     ....
 *   }
 *
 *   @GwtIncompatible("incompatible field")
 *   int field2 = methodThatisNotSupportedbyGwt();
 *
 *   void method1() { }
 *
 *   @GwtIncompatible("incompatbile method")
 *   void method2() {}
 * }
 * }
 *
 * is seen by the Gwt compiler as
 *
 * {@code
 * class A {
 *
 *   int field;
 *
 *   void method1() { }
 *
 * }
 * }
 *
 * Warning: this may have surprising effects when combined with method overloading or inheritance.
 */
@Retention(RetentionPolicy.CLASS)
@Target({
    ElementType.TYPE, ElementType.METHOD,
    ElementType.CONSTRUCTOR, ElementType.FIELD })
@Documented
public @interface GwtIncompatible {
  /**
   * Attributes can be used to explain why the code is incompatible.
   * All attributes on a GwtIncompatible annotation are ignored by the GWT compiler.
   */
}
