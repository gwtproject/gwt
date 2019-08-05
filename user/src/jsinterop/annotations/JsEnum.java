/*
 * Copyright 2018 Google Inc.
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
package jsinterop.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JsEnum marks a Java enum as being represented as a Closure enum, either one that already exists
 * from the external JavaScript environment, or one that will be accessible from the external
 * JavaScript environment.
 *
 * <p>The underlying type of the Closure enum can be specified via {@link #hasCustomValue} and
 * declaring an instance field named "value" of the desired type.
 *
 * <p>If the JsEnum is non-native and has custom values, a constructor is required to allow
 * specifying the values for the enum constants, as in the following example
 *
 * <pre><code>
 *  {@literal @}JsEnum(hasCustomValue=true)
 *   enum IntJsEnum {
 *     TEN(10),
 *     TWENTY(20);
 *
 *     int value;
 *
 *     IntJsEnum(int value) { this.value = value; }
 *   }
 * </code></pre>
 *
 * <p>If the JsEnum is native and has custom values, the value field is still required but
 * constructors are not needed nor allowed.
 *
 * <p>JsEnums do not support the full Java semantics:
 *
 * <ul>
 *   <li>No instance fields are allowed other than {@code value},
 *   <li>{@link Enum#name()} and {@link Enum#values()} are not supported.
 *   <li>{@link Enum#ordinal()} is supported only for non-native JsEnums that don't have custom
 *       values.
 *   <li>The class is initialization might be delayed until a static field/method is accessed.
 * </ul>
 *
 * <p>The JavaScript namespace and name can be specified in a manner similar to JsTypes via {@link
 * #namespace} and {@link #name} respectively.
 *
 * <p>Methods and fields declared in the JsEnum are only accessible in Java code. Only the enum
 * constants are accessible from JavaScript code.
 *
 * <p>This annotation is only supported by J2CL.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface JsEnum {

  /**
   * Customizes the name of the type in generated JavaScript. If not provided, the simple Java name
   * will be used.
   */
  String name() default "<auto>";

  /** Customizes the namespace of the type in generated JavaScript. */
  String namespace() default "<auto>";

  /** When set to {@code true}, this JsEnum is a native Closure enum type. */
  boolean isNative() default false;

  /**
   * When set to {@code true}, this JsEnum will have a custom value defined by a field named
   * 'value'. The only allowed types for the value field are String and primitive types with the
   * exception of long.
   */
  boolean hasCustomValue() default false;
}
