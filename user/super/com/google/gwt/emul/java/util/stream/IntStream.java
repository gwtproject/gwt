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

import static javaemul.internal.InternalPreconditions.checkNotNull;
import static javaemul.internal.InternalPreconditions.checkState;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Spliterators.AbstractIntSpliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;

/**
 * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/stream/IntStream.html">
 * the official Java API doc</a> for details.
 */
public interface IntStream extends BaseStream<Integer, IntStream> {

  /**
   * Value holder for various stream operations.
   */
  static final class ValueConsumer implements IntConsumer {
    int value;

    @Override
    public void accept(int value) {
      this.value = value;
    }
  }

  static IntStream.Builder builder() {
    return new Builder() {
      private int[] items = new int[0];

      @Override
      public void accept(int t) {
        checkState(items != null, "Builder already built");
        items[items.length] = t;
      }

      @Override
      public IntStream build() {
        checkState(items != null, "Builder already built");
        IntStream stream = Arrays.stream(items);
        items = null;
        return stream;
      }
    };
  }

  static IntStream concat(IntStream a, IntStream b) {
    // This is nearly the same as flatMap, but inlined, wrapped around a single spliterator of
    // these two objects, and without close() called as the stream progresses. Instead, close is
    // invoked as part of the resulting stream's own onClose, so that either can fail without
    // affecting the other, and correctly collecting suppressed exceptions.

    // TODO replace this flatMap-ish spliterator with one that directly combines the two root
    // streams
    Spliterator<? extends IntStream> spliteratorOfStreams = Arrays.asList(a, b).spliterator();

    Spliterator.OfInt spliterator =
        new Spliterators.AbstractIntSpliterator(Long.MAX_VALUE, 0) {
          Spliterator.OfInt next;

          @Override
          public boolean tryAdvance(IntConsumer action) {
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

    IntStream result = new IntStreamSource(null, spliterator);

    result.onClose(a::close);
    result.onClose(b::close);

    return result;
  }

  static IntStream empty() {
    return new EmptyIntStreamSource(null);
  }

  static IntStream generate(final IntSupplier s) {
    AbstractIntSpliterator spliterator =
        new Spliterators.AbstractIntSpliterator(
            Long.MAX_VALUE, Spliterator.IMMUTABLE | Spliterator.ORDERED) {
          @Override
          public boolean tryAdvance(IntConsumer action) {
            action.accept(s.getAsInt());
            return true;
          }
        };

    return StreamSupport.intStream(spliterator, false);
  }

  static IntStream iterate(int seed, IntUnaryOperator f) {

    AbstractIntSpliterator spliterator =
        new Spliterators.AbstractIntSpliterator(
            Long.MAX_VALUE, Spliterator.IMMUTABLE | Spliterator.ORDERED) {
          private int next = seed;

          @Override
          public boolean tryAdvance(IntConsumer action) {
            action.accept(next);
            next = f.applyAsInt(next);
            return true;
          }
        };

    return StreamSupport.intStream(spliterator, false);
  }

  static IntStream of(int... values) {
    return Arrays.stream(values);
  }

  static IntStream of(int t) {
    // TODO consider a splittable that returns only a single value
    return of(new int[] {t});
  }

  static IntStream range(int startInclusive, int endExclusive) {
    if (startInclusive >= endExclusive) {
      return empty();
    }
    return rangeClosed(startInclusive, endExclusive - 1);
  }

  static IntStream rangeClosed(int startInclusive, int endInclusive) {
    if (startInclusive > endInclusive) {
      return empty();
    }
    int count = endInclusive - startInclusive + 1;

    AbstractIntSpliterator spliterator =
        new Spliterators.AbstractIntSpliterator(
            count,
            Spliterator.IMMUTABLE
                | Spliterator.SIZED
                | Spliterator.SUBSIZED
                | Spliterator.ORDERED
                | Spliterator.SORTED
                | Spliterator.DISTINCT) {
          private int next = startInclusive;

          @Override
          public Comparator<? super Integer> getComparator() {
            return null;
          }

          @Override
          public boolean tryAdvance(IntConsumer action) {
            if (next <= endInclusive) {
              action.accept(next++);
              return true;
            }
            return false;
          }
        };

    return StreamSupport.intStream(spliterator, false);
  }

  /**
   * See
   * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/stream/IntStream.Builder.html">
   * the official Java API doc</a> for details.
   */
  public interface Builder extends IntConsumer {
    @Override
    void accept(int t);

    default IntStream.Builder add(int t) {
      accept(t);
      return this;
    }

    IntStream build();
  }

  boolean allMatch(IntPredicate predicate);

  boolean anyMatch(IntPredicate predicate);

  DoubleStream asDoubleStream();

  LongStream asLongStream();

  OptionalDouble average();

  Stream<Integer> boxed();

  <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner);

  long count();

  IntStream distinct();

  IntStream filter(IntPredicate predicate);

  OptionalInt findAny();

  OptionalInt findFirst();

  IntStream flatMap(IntFunction<? extends IntStream> mapper);

  void forEach(IntConsumer action);

  void forEachOrdered(IntConsumer action);

  @Override
  PrimitiveIterator.OfInt iterator();

  IntStream limit(long maxSize);

  IntStream map(IntUnaryOperator mapper);

  DoubleStream mapToDouble(IntToDoubleFunction mapper);

  LongStream mapToLong(IntToLongFunction mapper);

  <U> Stream<U> mapToObj(IntFunction<? extends U> mapper);

  OptionalInt max();

  OptionalInt min();

  boolean noneMatch(IntPredicate predicate);

  @Override
  IntStream parallel();

  IntStream peek(IntConsumer action);

  OptionalInt reduce(IntBinaryOperator op);

  int reduce(int identity, IntBinaryOperator op);

  @Override
  IntStream sequential();

  IntStream skip(long n);

  IntStream sorted();

  @Override
  Spliterator.OfInt spliterator();

  int sum();

  IntSummaryStatistics summaryStatistics();

  int[] toArray();

  /**
   * Represents an empty stream, doing nothing for all methods.
   */
  static class EmptyIntStreamSource extends TerminatableStream<EmptyIntStreamSource>
      implements IntStream {
    public EmptyIntStreamSource(TerminatableStream<?> previous) {
      super(previous);
    }

    @Override
    public IntStream filter(IntPredicate predicate) {
      throwIfTerminated();
      return this;
    }

    @Override
    public IntStream map(IntUnaryOperator mapper) {
      throwIfTerminated();
      return this;
    }

    @Override
    public <U> Stream<U> mapToObj(IntFunction<? extends U> mapper) {
      throwIfTerminated();
      return new Stream.EmptyStreamSource<U>(this);
    }

    @Override
    public LongStream mapToLong(IntToLongFunction mapper) {
      throwIfTerminated();
      return new LongStream.EmptyLongStreamSource(this);
    }

    @Override
    public DoubleStream mapToDouble(IntToDoubleFunction mapper) {
      throwIfTerminated();
      return new DoubleStream.EmptyDoubleStreamSource(this);
    }

    @Override
    public IntStream flatMap(IntFunction<? extends IntStream> mapper) {
      throwIfTerminated();
      return this;
    }

    @Override
    public IntStream distinct() {
      throwIfTerminated();
      return this;
    }

    @Override
    public IntStream sorted() {
      throwIfTerminated();
      return this;
    }

    @Override
    public IntStream peek(IntConsumer action) {
      throwIfTerminated();
      return this;
    }

    @Override
    public IntStream limit(long maxSize) {
      throwIfTerminated();
      checkState(maxSize >= 0, "maxSize may not be negative");
      return this;
    }

    @Override
    public IntStream skip(long n) {
      throwIfTerminated();
      checkState(n >= 0, "n may not be negative");
      return this;
    }

    @Override
    public void forEach(IntConsumer action) {
      terminate();
      // do nothing
    }

    @Override
    public void forEachOrdered(IntConsumer action) {
      terminate();
      // do nothing
    }

    @Override
    public int[] toArray() {
      terminate();
      return new int[0];
    }

    @Override
    public int reduce(int identity, IntBinaryOperator op) {
      terminate();
      return identity;
    }

    @Override
    public OptionalInt reduce(IntBinaryOperator op) {
      terminate();
      return OptionalInt.empty();
    }

    @Override
    public <R> R collect(
        Supplier<R> supplier, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner) {
      terminate();
      return supplier.get();
    }

    @Override
    public int sum() {
      terminate();
      return 0;
    }

    @Override
    public OptionalInt min() {
      terminate();
      return OptionalInt.empty();
    }

    @Override
    public OptionalInt max() {
      terminate();
      return OptionalInt.empty();
    }

    @Override
    public long count() {
      terminate();
      return 0;
    }

    @Override
    public OptionalDouble average() {
      terminate();
      return OptionalDouble.empty();
    }

    @Override
    public IntSummaryStatistics summaryStatistics() {
      terminate();
      return new IntSummaryStatistics();
    }

    @Override
    public boolean anyMatch(IntPredicate predicate) {
      terminate();
      return false;
    }

    @Override
    public boolean allMatch(IntPredicate predicate) {
      terminate();
      return true;
    }

    @Override
    public boolean noneMatch(IntPredicate predicate) {
      terminate();
      return true;
    }

    @Override
    public OptionalInt findFirst() {
      terminate();
      return OptionalInt.empty();
    }

    @Override
    public OptionalInt findAny() {
      terminate();
      return OptionalInt.empty();
    }

    @Override
    public LongStream asLongStream() {
      throwIfTerminated();
      return new LongStream.EmptyLongStreamSource(this);
    }

    @Override
    public DoubleStream asDoubleStream() {
      throwIfTerminated();
      return new DoubleStream.EmptyDoubleStreamSource(this);
    }

    @Override
    public Stream<Integer> boxed() {
      throwIfTerminated();
      return new Stream.EmptyStreamSource<Integer>(this);
    }

    @Override
    public IntStream sequential() {
      throwIfTerminated();
      return this;
    }

    @Override
    public IntStream parallel() {
      throwIfTerminated();
      return this;
    }

    @Override
    public PrimitiveIterator.OfInt iterator() {
      return Spliterators.iterator(spliterator());
    }

    @Override
    public Spliterator.OfInt spliterator() {
      terminate();
      return Spliterators.emptyIntSpliterator();
    }

    @Override
    public boolean isParallel() {
      throwIfTerminated();
      return false;
    }

    @Override
    public IntStream unordered() {
      throwIfTerminated();
      return this;
    }
  }

  /**
   * Int to Int map spliterator.
   */
  static final class MapToIntSpliterator extends Spliterators.AbstractIntSpliterator {
    private final IntUnaryOperator map;
    private final Spliterator.OfInt original;

    public MapToIntSpliterator(IntUnaryOperator map, Spliterator.OfInt original) {
      super(
          original.estimateSize(),
          original.characteristics() & ~(Spliterator.SORTED | Spliterator.DISTINCT));
      checkNotNull(map);
      this.map = map;
      this.original = original;
    }

    @Override
    public boolean tryAdvance(final IntConsumer action) {
      return original.tryAdvance((int u) -> action.accept(map.applyAsInt(u)));
    }
  }

  /**
   * Int to Object map spliterator.
   * @param <T> the type of data in the object spliterator
   */
  static final class MapToObjSpliterator<T> extends Spliterators.AbstractSpliterator<T> {
    private final IntFunction<? extends T> map;
    private final Spliterator.OfInt original;

    public MapToObjSpliterator(IntFunction<? extends T> map, Spliterator.OfInt original) {
      super(
          original.estimateSize(),
          original.characteristics() & ~(Spliterator.SORTED | Spliterator.DISTINCT));
      checkNotNull(map);
      this.map = map;
      this.original = original;
    }

    @Override
    public boolean tryAdvance(final Consumer<? super T> action) {
      return original.tryAdvance((int u) -> action.accept(map.apply(u)));
    }
  }

  /**
   * Int to Long map spliterator.
   */
  static final class MapToLongSpliterator extends Spliterators.AbstractLongSpliterator {
    private final IntToLongFunction map;
    private final Spliterator.OfInt original;

    public MapToLongSpliterator(IntToLongFunction map, Spliterator.OfInt original) {
      super(
          original.estimateSize(),
          original.characteristics() & ~(Spliterator.SORTED | Spliterator.DISTINCT));
      checkNotNull(map);
      this.map = map;
      this.original = original;
    }

    @Override
    public boolean tryAdvance(final LongConsumer action) {
      return original.tryAdvance((int u) -> action.accept(map.applyAsLong(u)));
    }
  }

  /**
   * Int to Double map spliterator.
   */
  static final class MapToDoubleSpliterator extends Spliterators.AbstractDoubleSpliterator {
    private final IntToDoubleFunction map;
    private final Spliterator.OfInt original;

    public MapToDoubleSpliterator(IntToDoubleFunction map, Spliterator.OfInt original) {
      super(
          original.estimateSize(),
          original.characteristics() & ~(Spliterator.SORTED | Spliterator.DISTINCT));
      checkNotNull(map);
      this.map = map;
      this.original = original;
    }

    @Override
    public boolean tryAdvance(final DoubleConsumer action) {
      return original.tryAdvance((int u) -> action.accept(map.applyAsDouble(u)));
    }
  }

  /**
   * Int filter spliterator.
   */
  static final class FilterSpliterator extends Spliterators.AbstractIntSpliterator {
    private final IntPredicate filter;
    private final Spliterator.OfInt original;

    private boolean found;

    public FilterSpliterator(IntPredicate filter, Spliterator.OfInt original) {
      super(original.estimateSize(), original.characteristics() & ~Spliterator.SIZED);
      checkNotNull(filter);
      this.filter = filter;
      this.original = original;
    }

    @Override
    public Comparator<? super Integer> getComparator() {
      return original.getComparator();
    }

    @Override
    public boolean tryAdvance(final IntConsumer action) {
      found = false;
      while (!found
          && original.tryAdvance(
              (int item) -> {
                if (filter.test(item)) {
                  found = true;
                  action.accept(item);
                }
              })) {
        // do nothing, work is done in tryAdvance
      }

      return found;
    }
  }

  /**
   * Int skip spliterator.
   */
  static final class SkipSpliterator extends Spliterators.AbstractIntSpliterator {
    private long skip;
    private final Spliterator.OfInt original;

    public SkipSpliterator(long skip, Spliterator.OfInt original) {
      super(
          original.hasCharacteristics(Spliterator.SIZED)
              ? Math.max(0, original.estimateSize() - skip)
              : Long.MAX_VALUE,
          original.characteristics());
      this.skip = skip;
      this.original = original;
    }

    @Override
    public Comparator<? super Integer> getComparator() {
      return original.getComparator();
    }

    @Override
    public boolean tryAdvance(IntConsumer action) {
      while (skip > 0) {
        if (!original.tryAdvance((int ignore) -> { })) {
          return false;
        }
        skip--;
      }
      return original.tryAdvance(action);
    }
  }

  /**
   * Int limit spliterator.
   */
  static final class LimitSpliterator extends Spliterators.AbstractIntSpliterator {
    private final long limit;
    private final Spliterator.OfInt original;
    private int position = 0;

    public LimitSpliterator(long limit, Spliterator.OfInt original) {
      super(
          original.hasCharacteristics(Spliterator.SIZED)
              ? Math.min(original.estimateSize(), limit)
              : Long.MAX_VALUE,
          original.characteristics());
      this.limit = limit;
      this.original = original;
    }

    @Override
    public Comparator<? super Integer> getComparator() {
      return original.getComparator();
    }

    @Override
    public boolean tryAdvance(IntConsumer action) {
      if (position >= limit) {
        return false;
      }
      boolean result = original.tryAdvance(action);
      position++;
      return result;
    }
  }

  /**
   * Main implementation of IntStream, wrapping a single spliterator, and an optional parent stream.
   */
  static class IntStreamSource extends TerminatableStream<IntStreamSource> implements IntStream {
    private final Spliterator.OfInt spliterator;

    public IntStreamSource(TerminatableStream<?> previous, Spliterator.OfInt spliterator) {
      super(previous);
      this.spliterator = spliterator;
    }

    // terminals
    @Override
    public Spliterator.OfInt spliterator() {
      terminate();
      return spliterator;
    }

    @Override
    public PrimitiveIterator.OfInt iterator() {
      return Spliterators.iterator(spliterator());
    }

    @Override
    public OptionalInt findFirst() {
      terminate();
      ValueConsumer holder = new ValueConsumer();
      if (spliterator.tryAdvance(holder)) {
        return OptionalInt.of(holder.value);
      }
      return OptionalInt.empty();
    }

    @Override
    public OptionalInt findAny() {
      return findFirst();
    }

    @Override
    public boolean noneMatch(IntPredicate predicate) {
      return !anyMatch(predicate);
    }

    @Override
    public boolean allMatch(IntPredicate predicate) {
      return !anyMatch(predicate.negate());
    }

    @Override
    public boolean anyMatch(IntPredicate predicate) {
      return filter(predicate).findFirst().isPresent();
    }

    @Override
    public IntSummaryStatistics summaryStatistics() {
      return collect(
          IntSummaryStatistics::new,
          // TODO switch to a lambda reference once #9340 is fixed
          (intSummaryStatistics, value) -> intSummaryStatistics.accept(value),
          IntSummaryStatistics::combine);
    }

    @Override
    public OptionalDouble average() {
      IntSummaryStatistics stats = summaryStatistics();
      if (stats.getCount() == 0) {
        return OptionalDouble.empty();
      }
      return OptionalDouble.of(stats.getAverage());
    }

    @Override
    public long count() {
      terminate();
      long count = 0;
      while (spliterator.tryAdvance((int value) -> { })) {
        count++;
      }
      return count;
    }

    @Override
    public OptionalInt max() {
      IntSummaryStatistics stats = summaryStatistics();
      if (stats.getCount() == 0) {
        return OptionalInt.empty();
      }
      return OptionalInt.of(stats.getMax());
    }

    @Override
    public OptionalInt min() {
      IntSummaryStatistics stats = summaryStatistics();
      if (stats.getCount() == 0) {
        return OptionalInt.empty();
      }
      return OptionalInt.of(stats.getMin());
    }

    @Override
    public int sum() {
      return (int) summaryStatistics().getSum();
    }

    @Override
    public <R> R collect(
        Supplier<R> supplier, final ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner) {
      terminate();
      final R acc = supplier.get();
      spliterator.forEachRemaining((int value) -> accumulator.accept(acc, value));
      return acc;
    }

    @Override
    public OptionalInt reduce(IntBinaryOperator op) {
      ValueConsumer holder = new ValueConsumer();
      if (spliterator.tryAdvance(holder)) {
        return OptionalInt.of(reduce(holder.value, op));
      }
      terminate();
      return OptionalInt.empty();
    }

    @Override
    public int reduce(int identity, IntBinaryOperator op) {
      terminate();
      ValueConsumer holder = new ValueConsumer();
      holder.value = identity;
      spliterator.forEachRemaining(
          (int value) -> {
            holder.accept(op.applyAsInt(holder.value, value));
          });
      return holder.value;
    }

    @Override
    public int[] toArray() {
      terminate();
      int[] entries = new int[0];
      // this is legal in js, since the array will be backed by a JS array
      spliterator.forEachRemaining((int value) -> entries[entries.length] = value);

      return entries;
    }

    @Override
    public void forEachOrdered(IntConsumer action) {
      terminate();
      spliterator.forEachRemaining(action);
    }

    @Override
    public void forEach(IntConsumer action) {
      forEachOrdered(action);
    }
    // end terminals

    // intermediates

    @Override
    public IntStream filter(IntPredicate predicate) {
      throwIfTerminated();
      return new IntStreamSource(this, new FilterSpliterator(predicate, spliterator));
    }

    @Override
    public IntStream map(IntUnaryOperator mapper) {
      throwIfTerminated();
      return new IntStreamSource(this, new MapToIntSpliterator(mapper, spliterator));
    }

    @Override
    public <U> Stream<U> mapToObj(IntFunction<? extends U> mapper) {
      throwIfTerminated();
      return new Stream.StreamSource<U>(this, new MapToObjSpliterator<U>(mapper, spliterator));
    }

    @Override
    public LongStream mapToLong(IntToLongFunction mapper) {
      throwIfTerminated();
      return new LongStream.LongStreamSource(this, new MapToLongSpliterator(mapper, spliterator));
    }

    @Override
    public DoubleStream mapToDouble(IntToDoubleFunction mapper) {
      throwIfTerminated();
      return new DoubleStream.DoubleStreamSource(
          this, new MapToDoubleSpliterator(mapper, spliterator));
    }

    @Override
    public IntStream flatMap(IntFunction<? extends IntStream> mapper) {
      throwIfTerminated();
      final Spliterator<? extends IntStream> spliteratorOfStreams =
          new MapToObjSpliterator<>(mapper, spliterator);

      Spliterator.OfInt flatMapSpliterator =
          new Spliterators.AbstractIntSpliterator(Long.MAX_VALUE, 0) {
            IntStream nextStream;
            Spliterator.OfInt next;

            @Override
            public boolean tryAdvance(IntConsumer action) {
              // look for a new spliterator
              while (advanceToNextSpliterator()) {
                // if we have one, try to read and use it
                if (next.tryAdvance(action)) {
                  return true;
                } else {
                  nextStream.close();
                  nextStream = null;
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
                        nextStream = n;
                        next = n.spliterator();
                      }
                    })) {
                  return false;
                }
              }
              return true;
            }
          };

      return new IntStreamSource(this, flatMapSpliterator);
    }

    @Override
    public IntStream distinct() {
      throwIfTerminated();
      HashSet<Integer> seen = new HashSet<>();
      return filter(seen::add);
    }

    @Override
    public IntStream sorted() {
      throwIfTerminated();

      AbstractIntSpliterator sortedSpliterator =
          new Spliterators.AbstractIntSpliterator(
              spliterator.estimateSize(), spliterator.characteristics() | Spliterator.SORTED) {
            Spliterator.OfInt ordered = null;

            @Override
            public Comparator<? super Integer> getComparator() {
              return null;
            }

            @Override
            public boolean tryAdvance(IntConsumer action) {
              if (ordered == null) {
                int[] list = new int[0];
                spliterator.forEachRemaining((int item) -> list[list.length] = item);
                Arrays.sort(list);
                ordered = Spliterators.spliterator(list, characteristics());
              }
              return ordered.tryAdvance(action);
            }
          };

      return new IntStreamSource(this, sortedSpliterator);
    }

    @Override
    public IntStream peek(IntConsumer action) {
      checkNotNull(action);
      throwIfTerminated();

      AbstractIntSpliterator peekSpliterator =
          new Spliterators.AbstractIntSpliterator(
              spliterator.estimateSize(), spliterator.characteristics()) {
            @Override
            public boolean tryAdvance(final IntConsumer innerAction) {
              return spliterator.tryAdvance(action.andThen(innerAction));
            }
          };

      return new IntStreamSource(this, peekSpliterator);
    }

    @Override
    public IntStream limit(long maxSize) {
      throwIfTerminated();
      checkState(maxSize >= 0, "maxSize may not be negative");
      return new IntStreamSource(this, new LimitSpliterator(maxSize, spliterator));
    }

    @Override
    public IntStream skip(long n) {
      throwIfTerminated();
      checkState(n >= 0, "n may not be negative");
      if (n == 0) {
        return this;
      }
      return new IntStreamSource(this, new SkipSpliterator(n, spliterator));
    }

    @Override
    public LongStream asLongStream() {
      return mapToLong(i -> (long) i);
    }

    @Override
    public DoubleStream asDoubleStream() {
      return mapToDouble(i -> (double) i);
    }

    @Override
    public Stream<Integer> boxed() {
      return mapToObj(Integer::valueOf);
    }

    @Override
    public IntStream sequential() {
      throwIfTerminated();
      return this;
    }

    @Override
    public IntStream parallel() {
      throwIfTerminated();
      return this;
    }

    @Override
    public boolean isParallel() {
      throwIfTerminated();
      return false;
    }

    @Override
    public IntStream unordered() {
      throwIfTerminated();
      return this;
    }
  }
}
