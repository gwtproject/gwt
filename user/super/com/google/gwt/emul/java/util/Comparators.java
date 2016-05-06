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

import java.io.Serializable;

class Comparators {
  /*
   * This is a utility class that provides default Comparators. This class
   * exists so Arrays and Collections can share the natural comparator without
   * having to know internals of each other.
   *
   * This class is package protected since it is not in the JRE.
   */

  private static final Comparator<Comparable<Object>> INTERNAL_NATURAL_ORDER =
      new NaturalOrderComparator();

  private static final Comparator<Comparable<Object>> NATURAL_ORDER =
      new NaturalOrderComparator();

  private static final Comparator<Comparable<Object>> REVERSE_ORDER =
      new ReverseOrderComparator();

  private static final class NaturalOrderComparator implements Comparator<Comparable<Object>> {
    @Override
    public int compare(Comparable<Object> a, Comparable<Object> b) {
      return checkNotNull(a).compareTo(checkNotNull(b));
    }

    @Override
    public Comparator<Comparable<Object>> reversed() {
      return REVERSE_ORDER;
    }
  }

  private static final class ReverseOrderComparator implements Comparator<Comparable<Object>> {
    @Override
    public int compare(Comparable<Object> a, Comparable<Object> b) {
      return checkNotNull(b).compareTo(checkNotNull(a));
    }

    @Override
    public Comparator<Comparable<Object>> reversed() {
      return NATURAL_ORDER;
    }
  }

  static final class NullComparator<T> implements Comparator<T>, Serializable {
    private final boolean nullFirst;
    private final Comparator<T> delegate;

    @SuppressWarnings("unchecked")
    NullComparator(boolean nullFirst, Comparator<? super T> delegate) {
      this.nullFirst = nullFirst;
      this.delegate = (Comparator<T>) delegate;
    }

    @Override
    public int compare(T a, T b) {
      if (a == null) {
        return b == null ? 0 : (nullFirst ? -1 : 1);
      }
      if (b == null) {
        return nullFirst ? 1 : -1;
      }
      return delegate == null ? 0 : delegate.compare(a, b);
    }

    @Override
    public Comparator<T> reversed() {
      return new NullComparator<>(!nullFirst, delegate == null ? null : delegate.reversed());
    }

    @Override
    public Comparator<T> thenComparing(Comparator<? super T> other) {
      return new NullComparator<>(nullFirst, delegate == null ?
          other : delegate.thenComparing(other));
    }
  }

  /**
   * Returns the natural Comparator which compares two Objects
   * according to their <i>natural ordering</i>.
   * <p>
   * Example:
   *
   * <pre>Comparator&lt;String&gt; compareString = Comparators.natural()</pre>
   *
   * @return the natural Comparator
   */
  @SuppressWarnings("unchecked")
  public static <T> Comparator<T> natural() {
    return (Comparator<T>) NATURAL_ORDER;
  }

  @SuppressWarnings("unchecked")
  public static <T> Comparator<T> reverseOrder() {
    return (Comparator<T>) REVERSE_ORDER;
  }

  @SuppressWarnings("unchecked")
  static <T> Comparator<T> nullToNatural(Comparator<T> cmp) {
    return cmp == null ? (Comparator<T>) INTERNAL_NATURAL_ORDER : cmp;
  }

  static <T> Comparator<T> naturalToNull(Comparator<T> cmp) {
    return cmp == INTERNAL_NATURAL_ORDER ? null : cmp;
  }

  private Comparators() { }
}
