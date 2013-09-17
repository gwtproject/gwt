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
package com.google.gwt.lang;

/**
 * This class patches Object methods onto Array.prototype.
 *
 * {@link #patchArrayPrototype()} is called by the compiler to initiate
 * patchting of Array.prototype.
 */
public class ArrayPrototypePatcher {
  public static void patch() {
    patchArrayPrototypeFrom(new Object());
  }

  private static native void patchArrayPrototypeFrom(Object prototype) /*-{
    var value, name;
    var typemarker = prototype.@java.lang.Object::typeMarker;
    for (name in prototype) {
      value = prototype[name];
      if (typeof(value) == 'function' && value != typemarker) {
        Array.prototype[name] = value;
      }
    }
  }-*/;
}
