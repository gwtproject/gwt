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

import com.google.gwt.thirdparty.guava.common.base.Predicates;
import com.google.gwt.thirdparty.guava.common.collect.Iterables;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Stack class based on ArrayList, that allows push nulls.
 *
 * @param <T> the value type
 */
public final class Stack<T> implements Iterable<T> {

  private ArrayList<T> elements = Lists.newArrayList();

  /**
   * Returns the number of elements in the stack (including pushed nulls).
   */
  public int size() {
    return elements.size();
  }

  /**
   * Returns true if the stack contains element, false otherwise.
   */
  public boolean contains(T element) {
    return elements.contains(element);
  }

  /**
   * Returns true if the stack is empty false otherwise.
   */
  public boolean isEmpty() {
    return elements.isEmpty();
  }

  @Override
  public Iterator<T> iterator() {
    return elements.iterator();
  }

  /**
   * Returns the top of the stack.
   */
  public T peek() {
    return elements.get(elements.size() - 1);
  }

  /**
   * Returns the top of the stack and removes it.
   * @return
   */
  public T pop() {
    return elements.remove(elements.size() - 1);
  }

  /**
   * Pops {@code count} elements from the stack and returns them as a list with to top of the stack
   * last. If {@code filterNulls} is true null values are filtered from the returned list.
   */
  public List<T> pop(int count, boolean filterNulls) {
    int size = elements.size();
    List<T> nodesToPop = elements.subList(size - count, size);
    List<T> result;
    if (filterNulls) {
      result = Lists.newArrayList(Iterables.filter(nodesToPop, Predicates.notNull()));
    } else {
      result = Lists.newArrayList(nodesToPop);
    }
    nodesToPop.clear();
    return result;
  }

  public void push(T value) {
    elements.add(value);
  }
}
