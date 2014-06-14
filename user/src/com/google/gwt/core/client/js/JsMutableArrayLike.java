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
 * @param <T> the type held by the array
 */
@JsType
public interface JsMutableArrayLike<T> extends JsArrayLike<T> {
  @JsProperty(indexed = true)
  void setAt(int index, T object);

  @JsProperty(indexed = true)
  void setAt(int index, int value);

  @JsProperty(indexed = true)
  void setAt(int index, double value);

  @JsProperty(indexed = true)
  void setAt(int index, boolean value);
}
