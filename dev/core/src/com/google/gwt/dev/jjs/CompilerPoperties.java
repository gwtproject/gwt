/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev.jjs;

import com.google.gwt.core.ext.linker.SelectionProperty;
import com.google.gwt.core.ext.linker.SoftPermutation;

import java.util.Map.Entry;

/**
 * Properties that the compiler is aware of.
 */
public final class CompilerPoperties {
  public static final String USE_SOURCE_MAPS_BINDING_PROPERTY = "compiler.useSourceMaps";
  public static final String USE_SYMBOL_MAPS_BINDING_PROPERTY = "compiler.useSymbolMaps";
  public static final String COMPILER_STACK_MODE_BINDING_PROPERTY = "compiler.stackMode";

  public static boolean isTrueInAnyPermutation(
      SoftPermutation[] softPermutations, String propertyName) {

    for (SoftPermutation perm : softPermutations) {
      for (Entry<SelectionProperty, String> propMapEntry : perm.getPropertyMap().entrySet()) {
        if (propMapEntry.getKey().getName().equals(propertyName)
            && Boolean.valueOf(propMapEntry.getValue())) {
          return true;
        }
      }
    }
    return false;
  }
}
