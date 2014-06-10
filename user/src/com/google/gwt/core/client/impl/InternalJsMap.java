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
package com.google.gwt.core.client.impl;

/**
 * A simple wrapper around JavaScriptObject to provide {@link java.util.Map}-like semantics.
 *
 * <p>Note: This class is experimental and be changed or removed at any time.
 *
 * @param <T> the type of mapped values
 */
public interface InternalJsMap<T> {
  T get(String key);

  void set(String key, T value);

  void remove(String key);

  boolean contains(String key);

  String[] keys();
}
