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
   * This is a utility class that provides a default Comparator. This class
   * exists so Arrays and Collections can share the natural comparator without
   * having to know internals of each other.
   *
   * This class is package protected since it is not in the JRE.
   */

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
    return (a, b) -> checkNotNull((Comparable<Object>) a).compareTo(checkNotNull(b));
  }

  @SuppressWarnings("unchecked")
  public static <T> Comparator<T> reverseOrder() {
    return (a, b) -> checkNotNull((Comparable<Object>) b).compareTo(checkNotNull(a));
  }
}
