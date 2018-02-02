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
 * JsNullable marks a type variable as nullable indicating that program elements of such a type can
 * hold a {@code null} value.
 *
 * <p>Note that JsNullable can only be used on type variables in the context of a JsConstructor,
 * a JsMethod, a JsProperty or a JsFunction method.
 *
 * <p>This annotation is informational in the GWT compiler but other compilers or tools might use it
 * for example to annotate types in the output JavaScript program.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
@Documented
public @interface JsNullable { }
