/*
 * Copyright 2016 Google Inc.
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
package java.util.stream;

import static javaemul.internal.InternalPreconditions.checkState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

/**
 * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/stream/DoubleStream.html">
 * the official Java API doc</a> for details.
 */
public interface DoubleStream extends BaseStream<Double, DoubleStream> {

  /**
   * See <a
   * href="https://docs.oracle.com/javase/8/docs/api/java/util/stream/DoubleStream.Builder.html">the
   * official Java API doc</a> for details.
   */
  interface Builder extends DoubleConsumer {
    @Override
    void accept(double t);

    default DoubleStream.Builder add(double t) {
      accept(t);
      return this;
    }

    DoubleStream build();
  }

  /**
   * See <a
   * href="https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/util/stream/DoubleStream.DoubleMapMultiConsumer.html">
   * the official Java API doc</a> for details.
   */
  interface DoubleMapMultiConsumer {
    void accept(double value, DoubleConsumer consumer);
  }

  static Builder builder() {
    return new Builder() {
      private double[] items = new double[0];

      @Override
      public void accept(double t) {
        checkState(items != null, "Builder already built");
        items[items.length] = t;
      }

      @Override
      public DoubleStream build() {
        checkState(items != null, "Builder already built");
        DoubleStream stream = Arrays.stream(items);
        items = null;
        return stream;
      }
    };
  }

  static DoubleStream concat(DoubleStream a, DoubleStream b) {
    // This is nearly the same as flatMap, but inlined, wrapped around a single spliterator of
    // these two objects, and without close() called as the stream progresses. Instead, close is
    // invoked as part of the resulting stream's own onClose, so that either can fail without
    // affecting the other, and correctly collecting suppressed exceptions.

    // TODO replace this flatMap-ish spliterator with one that directly combines the two root
    // streams
    Spliterator<? extends DoubleStream> spliteratorOfStreams = Arrays.asList(a, b).spliterator();

    Spliterator.OfDouble spliterator =
        new Spliterators.AbstractDoubleSpliterator(Long.MAX_VALUE, 0) {
          Spliterator.OfDouble next;

          @Override
          public boolean tryAdvance(DoubleConsumer action) {
            // look for a new spliterator
            while (advanceToNextSpliterator()) {
              // if we have one, try to read and use it
              if (next.tryAdvance(action)) {
                return true;
              } else {
                // failed, null it out so we can find another
                next = null;
              }
            }
            return false;
          }

          private boolean advanceToNextSpliterator() {
            while (next == null) {
              if (!spliteratorOfStreams.tryAdvance(
                  n -> {
                    if (n != null) {
                      next = n.spliterator();
                    }
                  })) {
                return false;
              }
            }
            return true;
          }
        };

    DoubleStream result = new DoubleStreamImpl(null, spliterator);

    return result.onClose(a::close).onClose(b::close);
  }

  static DoubleStream empty() {
    return new DoubleStreamImpl.Empty(null);
  }

  static DoubleStream generate(DoubleSupplier s) {
    Spliterator.OfDouble spliterator =
        new Spliterators.AbstractDoubleSpliterator(
            Long.MAX_VALUE, Spliterator.IMMUTABLE | Spliterator.ORDERED) {
          @Override
          public boolean tryAdvance(DoubleConsumer action) {
            action.accept(s.getAsDouble());
            return true;
          }
        };

    return StreamSupport.doubleStream(spliterator, false);
  }

  static DoubleStream iterate(double seed, DoubleUnaryOperator f) {
    return iterate(seed, ignore -> true, f);
  }

  static DoubleStream iterate(double seed, DoublePredicate hasNext, DoubleUnaryOperator f) {
    Spliterator.OfDouble spliterator =
        new Spliterators.AbstractDoubleSpliterator(
            Long.MAX_VALUE, Spliterator.IMMUTABLE | Spliterator.ORDERED) {
          private boolean first = true;
          private double next = seed;
          private boolean terminated = false;

          @Override
          public boolean tryAdvance(DoubleConsumer action) {
            if (terminated) {
              return false;
            }
            if (!first) {
              next = f.applyAsDouble(next);
            }
            first = false;

            if (!hasNext.test(next)) {
              terminated = true;
              return false;
            }
            action.accept(next);
            return true;
          }
        };
    return StreamSupport.doubleStream(spliterator, false);
  }

  static DoubleStream of(double... values) {
    return Arrays.stream(values);
  }

  static DoubleStream of(double t) {
    // TODO consider a splittable that returns only a single value
    return of(new double[] {t});
  }

  boolean allMatch(DoublePredicate predicate);

  boolean anyMatch(DoublePredicate predicate);

  OptionalDouble average();

  Stream<Double> boxed();

  <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator, BiConsumer<R, R> combiner);

  long count();

  DoubleStream distinct();

  default DoubleStream dropWhile(DoublePredicate predicate) {
    Spliterator.OfDouble prev = spliterator();
    Spliterator.OfDouble spliterator =
        new Spliterators.AbstractDoubleSpliterator(prev.estimateSize(),
                prev.characteristics() & ~(Spliterator.SIZED | Spliterator.SUBSIZED)) {
          private boolean drop = true;
          private boolean found;

          @Override
          public boolean tryAdvance(DoubleConsumer action) {
            found = false;
            if (drop) {
              // drop items until we find one that matches
              while (drop && prev.tryAdvance((double item) -> {
                if (!predicate.test(item)) {
                  drop = false;
                  found = true;
                  action.accept(item);
                }
              })) {
                // do nothing, work is done in tryAdvance
              }
              // only return true if we accepted at least one item
              return found;
            } else {
              // accept one item, return result
              return prev.tryAdvance(action);
            }
          }
        };
    return StreamSupport.doubleStream(spliterator, false);
  }

  DoubleStream filter(DoublePredicate predicate);

  OptionalDouble findAny();

  OptionalDouble findFirst();

  DoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper);

  void forEach(DoubleConsumer action);

  void forEachOrdered(DoubleConsumer action);

  @Override
  PrimitiveIterator.OfDouble iterator();

  DoubleStream limit(long maxSize);

  DoubleStream map(DoubleUnaryOperator mapper);

  IntStream mapToInt(DoubleToIntFunction mapper);

  LongStream mapToLong(DoubleToLongFunction mapper);

  <U> Stream<U> mapToObj(DoubleFunction<? extends U> mapper);

  OptionalDouble max();

  OptionalDouble min();

  boolean noneMatch(DoublePredicate predicate);

  @Override
  DoubleStream parallel();

  DoubleStream peek(DoubleConsumer action);

  OptionalDouble reduce(DoubleBinaryOperator op);

  double reduce(double identity, DoubleBinaryOperator op);

  @Override
  DoubleStream sequential();

  DoubleStream skip(long n);

  DoubleStream sorted();

  @Override
  Spliterator.OfDouble spliterator();

  double sum();

  DoubleSummaryStatistics summaryStatistics();

  default DoubleStream takeWhile(DoublePredicate predicate) {
    Spliterator.OfDouble original = spliterator();
    Spliterator.OfDouble spliterator =
        new Spliterators.AbstractDoubleSpliterator(original.estimateSize(),
                original.characteristics() & ~(Spliterator.SIZED | Spliterator.SUBSIZED)) {
          private boolean take = true;
          private boolean found;

          @Override
          public boolean tryAdvance(DoubleConsumer action) {
            found = false;
            if (!take) {
              // already failed the check
              return false;
            }
            original.tryAdvance((double item) -> {
              if (predicate.test(item)) {
                found = true;
                action.accept(item);
              } else {
                take = false;
              }
            });
            return found;
          }
        };
    return StreamSupport.doubleStream(spliterator, false);
  }

  default DoubleStream mapMulti(DoubleStream.DoubleMapMultiConsumer mapper) {
    return flatMap(element -> {
      List<Double> buffer = new ArrayList<>();
      mapper.accept(element, (DoubleConsumer) buffer::add);
      return buffer.stream().mapToDouble(n -> n);
    });
  }

  double[] toArray();
}
