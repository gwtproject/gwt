/*
 * Copyright 2006 Google Inc.
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
package java.lang;

import javaemul.internal.ArrayHelper;
import jsinterop.annotations.JsMethod;

/**
 * Indicates that a class implements <code>clone()</code>.
 */
public interface Cloneable {

  // CHECKSTYLE_OFF: Utility methods.
  @JsMethod
  static boolean $isInstance(HasCloneableTypeMarker instance) {
    if (instance == null) {
      return false;
    }

    return instance.getTypeMarker()
        // Arrays are implicitly instances of Cloneable (JLS 10.7).
        || ArrayHelper.isArray(instance);
  }
  // CHECKSTYLE_ON: end utility methods
}
