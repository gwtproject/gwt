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
package com.google.gwt.core.client.js.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.js.JsArrayLike;

import java.util.Iterator;

/**
 * Internal adapter used by compiler to synthesize Iterables for Javascript arrays.
 * @param <T> the type held by the array
*/
public class JsArrayLikeIterableAdapter<T> implements Iterable<T> {
  private JsArrayLike<T> instance;

  public static <T> Iterable<T> iterableTrampoline(Object it) {
    if (it instanceof Iterable) {
      return (Iterable<T>) it;
    }
    if (it instanceof JavaScriptObject) {
      return new JsArrayLikeIterableAdapter<T>((JsArrayLike<T>) it);
    }
    throw new IllegalArgumentException("Object " + it +
        " is neither a Javascript object nor a Java class implementing Iterable<T>");
  }

  public JsArrayLikeIterableAdapter(JsArrayLike<T> instance) {
    this.instance = instance;
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {
      int i = 0;
      @Override
      public boolean hasNext() {
        return instance != null && i < instance.length();
      }

      @Override
      public T next() {
        return instance.at(i++);
      }

      public void remove() { throw new UnsupportedOperationException("Can't remove"); }
    };
  }
}
