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
package com.google.gwt.dev.util.collect;

import com.google.gwt.thirdparty.guava.common.collect.Lists;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A Stack class based on ArrayList.
 *
 * @param <T> the value type
 */
public final class Stack<T> implements Iterable<T> {

  private ArrayList<T> elements = Lists.newArrayList();

  public void push(T value) {
    elements.add(value);
  }

  public T pop() {
    return elements.remove(elements.size() - 1);
  }

  public T peek() {
    return elements.get(elements.size() - 1);
  }

  @Override
  public Iterator<T> iterator() {
    return elements.iterator();
  }

  public int size() {
    return elements.size();
  }

  public boolean isEmpty() {
    return elements.isEmpty();
  }

  public boolean contains(T element) {
    return elements.contains(element);
  }
}
