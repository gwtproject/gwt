/*
 * Copyright 2007 Google Inc.
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
package java.io;

import javaemul.internal.JsUtils;
import javaemul.internal.NativeArray;
import jsinterop.annotations.JsMethod;

/**
 * Provided for interoperability; RPC treats this interface synonymously with
 * {@link com.google.gwt.user.client.rpc.IsSerializable IsSerializable}.
 * The Java serialization protocol is explicitly not supported.
 */
public interface Serializable {
  // CHECKSTYLE_OFF: Utility methods.
  @JsMethod
  static boolean $isInstance(HasSerializableTypeMarker instance) {
    if (instance == null) {
      return false;
    }

    String type = JsUtils.typeOf(instance);
    return type.equals("boolean")
        || type.equals("number")
        || type.equals("string")
        || instance.getTypeMarker()
        // Arrays are implicitly instances of Serializable (JLS 10.7).
        || NativeArray.isArray(instance);
  }
  // CHECKSTYLE_ON: end utility methods
}
