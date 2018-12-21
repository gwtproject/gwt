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
package java.util;

import static javaemul.internal.InternalPreconditions.checkNotNull;

import java.util.function.UnaryOperator;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsNonNull;
import jsinterop.annotations.JsType;

/**
 * Represents a sequence of objects.
 * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/List.html">
 * the official Java API doc</a> for details.
 *
 * @param <E> element type
 */
@JsType
public interface List<E> extends Collection<E> {

  @JsIgnore
  static <E> List<E> of() {
    return Collections.emptyList();
  }

  @JsIgnore
  static <E> List<E> of(E e1) {
    return Collections.singletonList(checkNotNull(e1));
  }

  @JsIgnore
  static <E> List<E> of(
      E e1,
      E e2
  ) {
    List<E> list = new ArrayList<>(2);
    list.add(checkNotNull(e1));
    list.add(checkNotNull(e2));
    return Collections.unmodifiableList(list);
  }

  @JsIgnore
  static <E> List<E> of(
      E e1,
      E e2,
      E e3
  ) {
    List<E> list = new ArrayList<>(3);
    list.add(checkNotNull(e1));
    list.add(checkNotNull(e2));
    list.add(checkNotNull(e3));
    return Collections.unmodifiableList(list);
  }

  @JsIgnore
  static <E> List<E> of(
      E e1,
      E e2,
      E e3,
      E e4
  ) {
    List<E> list = new ArrayList<>(4);
    list.add(checkNotNull(e1));
    list.add(checkNotNull(e2));
    list.add(checkNotNull(e3));
    list.add(checkNotNull(e4));
    return Collections.unmodifiableList(list);
  }

  @JsIgnore
  static <E> List<E> of(
      E e1,
      E e2,
      E e3,
      E e4,
      E e5
  ) {
    List<E> list = new ArrayList<>(5);
    list.add(checkNotNull(e1));
    list.add(checkNotNull(e2));
    list.add(checkNotNull(e3));
    list.add(checkNotNull(e4));
    list.add(checkNotNull(e5));
    return Collections.unmodifiableList(list);
  }

  @JsIgnore
  static <E> List<E> of(
      E e1,
      E e2,
      E e3,
      E e4,
      E e5,
      E e6
  ) {
    List<E> list = new ArrayList<>(6);
    list.add(checkNotNull(e1));
    list.add(checkNotNull(e2));
    list.add(checkNotNull(e3));
    list.add(checkNotNull(e4));
    list.add(checkNotNull(e5));
    list.add(checkNotNull(e6));
    return Collections.unmodifiableList(list);
  }

  @JsIgnore
  static <E> List<E> of(
      E e1,
      E e2,
      E e3,
      E e4,
      E e5,
      E e6,
      E e7
  ) {
    List<E> list = new ArrayList<>(7);
    list.add(checkNotNull(e1));
    list.add(checkNotNull(e2));
    list.add(checkNotNull(e3));
    list.add(checkNotNull(e4));
    list.add(checkNotNull(e5));
    list.add(checkNotNull(e6));
    list.add(checkNotNull(e7));
    return Collections.unmodifiableList(list);
  }

  @JsIgnore
  static <E> List<E> of(
      E e1,
      E e2,
      E e3,
      E e4,
      E e5,
      E e6,
      E e7,
      E e8
  ) {
    List<E> list = new ArrayList<>(8);
    list.add(checkNotNull(e1));
    list.add(checkNotNull(e2));
    list.add(checkNotNull(e3));
    list.add(checkNotNull(e4));
    list.add(checkNotNull(e5));
    list.add(checkNotNull(e6));
    list.add(checkNotNull(e7));
    list.add(checkNotNull(e8));
    return Collections.unmodifiableList(list);
  }

  @JsIgnore
  static <E> List<E> of(
      E e1,
      E e2,
      E e3,
      E e4,
      E e5,
      E e6,
      E e7,
      E e8,
      E e9
  ) {
    List<E> list = new ArrayList<>(9);
    list.add(checkNotNull(e1));
    list.add(checkNotNull(e2));
    list.add(checkNotNull(e3));
    list.add(checkNotNull(e4));
    list.add(checkNotNull(e5));
    list.add(checkNotNull(e6));
    list.add(checkNotNull(e7));
    list.add(checkNotNull(e8));
    list.add(checkNotNull(e9));
    return Collections.unmodifiableList(list);
  }

  @JsIgnore
  static <E> List<E> of(
      E e1,
      E e2,
      E e3,
      E e4,
      E e5,
      E e6,
      E e7,
      E e8,
      E e9,
      E e10
  ) {
    List<E> list = new ArrayList<>(10);
    list.add(checkNotNull(e1));
    list.add(checkNotNull(e2));
    list.add(checkNotNull(e3));
    list.add(checkNotNull(e4));
    list.add(checkNotNull(e5));
    list.add(checkNotNull(e6));
    list.add(checkNotNull(e7));
    list.add(checkNotNull(e8));
    list.add(checkNotNull(e9));
    list.add(checkNotNull(e10));
    return Collections.unmodifiableList(list);
  }

  @JsIgnore
  static <E> List<E> of(E... elements) {
    for (int i = 0; i < elements.length; i++) {
      checkNotNull(elements[i]);
    }
    return Collections.unmodifiableList(new ArrayList<>(Arrays.asList(elements)));
  }

  @JsMethod(name = "addAtIndex")
  void add(int index, E element);

  @JsMethod(name = "addAllAtIndex")
  boolean addAll(int index, Collection<? extends E> c);

  @JsMethod(name = "getAtIndex")
  E get(int index);

  int indexOf(Object o);

  int lastIndexOf(Object o);

  @JsIgnore
  ListIterator<E> listIterator();

  @JsIgnore
  ListIterator<E> listIterator(int from);

  @JsMethod(name = "removeAtIndex")
  E remove(int index);

  @JsIgnore
  default void replaceAll(UnaryOperator<E> operator) {
    checkNotNull(operator);
    for (int i = 0, size = size(); i < size; i++) {
      set(i, operator.apply(get(i)));
    }
  }

  @JsMethod(name = "setAtIndex")
  E set(int index, E element);

  @JsIgnore
  @SuppressWarnings("unchecked")
  default void sort(Comparator<? super E> c) {
    Object[] a = toArray();
    Arrays.sort(a, (Comparator<Object>) c);
    for (int i = 0; i < a.length; i++) {
      set(i, (E) a[i]);
    }
  }

  @JsIgnore
  @Override
  default Spliterator<E> spliterator() {
    return Spliterators.spliterator(this, Spliterator.ORDERED);
  }

  @JsNonNull
  List<E> subList(int fromIndex, int toIndex);
}
