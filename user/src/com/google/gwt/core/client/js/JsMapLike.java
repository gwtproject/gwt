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
 * Magic interface that when applied to JsTypes, implies that the underlying object can be indexed
 * into via the Javascript array reference operator.
 * @param <T> the value type held by the map
 */
@JsType
public interface JsMapLike<T> {
  @JsProperty(indexed = true)
  T at(String key);

  @JsProperty(indexed = true)
  int intAt(String key);

  @JsProperty(indexed = true)
  double numAt(String key);

  @JsProperty(indexed = true)
  boolean boolAt(String key);

  @JsProperty
  int length();
}
