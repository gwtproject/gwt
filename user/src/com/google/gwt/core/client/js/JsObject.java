/*
 * Copyright 2014 Google Inc.
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

/**
 * Base class of all Javascript Object interop types.
 */
@JsType
public interface JsObject {
  @JsProperty
  JsObject constructor();
  boolean hasOwnProperty(String propName);
  boolean isPrototypeOf(JsObject obj);
  boolean propertyIsEnumerable(String propName);

  /**
   * TODO: technically, it can be a bool or String as well. Figure out the API later.
   * double valueOf();
   */
  String toLocaleString();
}
