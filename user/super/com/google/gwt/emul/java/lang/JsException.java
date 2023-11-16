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
package java.lang;

/**
 * Abstracts an object thrown natively in JavaScript. Thrown objects are most of the time a
 * JavaScript Error but not guaranteed to be as JavaScript code can throw anything including
 * primitives like {@code null}, numbers, etc.
 */
public class JsException extends RuntimeException {

  private static final Object UNINITIALIZED = "__noinit__";

  private Object backingJsObject = UNINITIALIZED;

  protected JsException(Object backingJsObject) {
    super(backingJsObject);
    this.backingJsObject = backingJsObject;
  }

  JsException(String msg) {
    super(msg);
  }

  JsException() {
    super();
  }

  // Note that this initialization path is only used for J2CL transpiler, not in GWT.
  @Override
  void privateInitError(Object error) {
    super.privateInitError(backingJsObject == UNINITIALIZED ? error : backingJsObject);
  }
}
