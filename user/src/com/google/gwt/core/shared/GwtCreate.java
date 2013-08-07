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

import com.google.gwt.core.ext.Generator;

/**
 * You may use this annotation to declare a factory method that uses a given generator.
 * <p>
 * This factory method must call {@link GwtCreatable#create(Class)} with the class literal
 * to use for source code generation.
 * <p>
 * This api does not yet support passing arbitrary classes to the generator.<br>
 * It does, however, allow you to rebind the same class more than once.
 * <p>
 * Note that using this annotation will create a single result across all permutations.<br>
 * 
 * @author "james@wetheinter.net"
 *
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface GwtCreate {

  /**
   * The generator class to use for rebinds.
   */
  Class<? extends Generator> generator();
  
}
