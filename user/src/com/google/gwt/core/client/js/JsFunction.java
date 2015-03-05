/*
 * Copyright 2015 Google Inc.
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
 * Marks a type containing a Single Abstract Method (SAM) as eligible for automatic
 * conversion into a Javascript function.
 *
 * This enables lambda expressions to be passed directly to Javascript as callbacks.
 *
 * However a limitation is currently imposed to make this practical and inefficient. A class
 * may not implement or extend more than one @JsFunction type. This restriction allows the
 * compiler to construct a one-to-one mapping to the JS function generated and the
 * single abstract method to be invoked in Java and to preserve referential equality.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface JsFunction {
}
