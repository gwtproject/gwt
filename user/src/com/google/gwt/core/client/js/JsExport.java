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
package com.google.gwt.core.client.js;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JsExport marks a constructor, static method, or static field, creating an unobfuscated alias in
 * the global scope.
 * <p>
 * When JsExport is applied to an entire class or interface, it is syntactic sugar for applying
 * JsExport to every public static field and method of the class, except for constructors. When
 * JsExport is applied to an entire class that is a java enum, all enumarations are exported as
 * well. JsNoExport may be used to opt-out a public method or field if JsExport has been applied to
 * an entire class.
 * <p>
 * Exported members act as an entry-point from the standpoint of the optimizer, and all code
 * reachable from an exported method is also considered live, so use with care.
 *
 * @see JsNoExport
 * @see JsNamespace
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Documented
public @interface JsExport {
  String value() default "";
}
