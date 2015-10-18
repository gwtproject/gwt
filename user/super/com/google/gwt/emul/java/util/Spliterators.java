/*
 * Copyright 2015 Google Inc.
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

import static javaemul.internal.InternalPreconditions.checkCriticalArgument;
import static javaemul.internal.InternalPreconditions.checkCriticalPositionIndexes;
import static javaemul.internal.InternalPreconditions.checkCritcalState;
import static javaemul.internal.InternalPreconditions.checkNotNull;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/**
 * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Spliterators.html">
 * the official Java API doc</a> for details.
 */
public class Spliterators {

  /**
   * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Spliterators.AbstractSpliterator.html">
   * the official Java API doc</a> for details.
   */
  public static abstract class AbstractSpliterator<T> implements Spliterator<T> {
    static final class ValueConsumer<T> implements Consumer<T> {
      Object value;

      @Override
      public void accept(T value) {
        this.value = value;
      }
    }

    private final ValueConsumer<T> consumer = new ValueConsumer<>();
    private final int characteristics;
    private long sizeEstimate;

    protected AbstractSpliterator(long size, int characteristics) {
      this.sizeEstimate = size;
      this.characteristics = (characteristics & Spliterator.SIZED) != 0 ?
          characteristics | Spliterator.SUBSIZED : characteristics;
    }

    @Override
    public int characteristics() {
      return characteristics;
    }

    @Override
    public long estimateSize() {
      return sizeEstimate;
    }

    @Override
    public Spliterator<T> trySplit() {
      if (sizeEstimate == 0) {
        return null;
      }

      if (!tryAdvance(consumer)) {
        return null;
      }

      int n = BATCH;
      if (sizeEstimate != Long.MAX_VALUE) {
        n = Math.min((int) sizeEstimate, n);
      }
      Object[] a = new Object[n];
      int i = 0;
      do {
        a[i] = consumer.value;
      } while (++i < n && tryAdvance(consumer));
      consumer.value = null;
      if (sizeEstimate != Long.MAX_VALUE) {
        sizeEstimate -= i;
      }
      return new ArraySpliterator<>(a, 0, i, characteristics);
    }
  }

  /**
   * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Spliterators.AbstractDoubleSpliterator.html">
   * the official Java API doc</a> for details.
   */
  public static abstract class AbstractDoubleSpliterator implements Spliterator.OfDouble {
    static final class ValueConsumer implements DoubleConsumer {
      double value;

      @Override
      public void accept(double value) {
        this.value = value;
      }
    }

    private final ValueConsumer consumer = new ValueConsumer();
    private final int characteristics;
    private long sizeEstimate;

    protected AbstractDoubleSpliterator(long size, int characteristics) {
      this.sizeEstimate = size;
      this.characteristics = (characteristics & Spliterator.SIZED) != 0 ?
          characteristics | Spliterator.SUBSIZED : characteristics;
    }

    @Override
    public int characteristics() {
      return characteristics;
    }

    @Override
    public long estimateSize() {
      return sizeEstimate;
    }

    @Override
    public OfDouble trySplit() {
      if (sizeEstimate == 0) {
        return null;
      }

      if (!tryAdvance(consumer)) {
        return null;
      }

      int n = BATCH;
      if (sizeEstimate != Long.MAX_VALUE) {
        n = Math.min((int) sizeEstimate, n);
      }
      double[] a = new double[n];
      int i = 0;
      do {
        a[i] = consumer.value;
      } while (++i < n && tryAdvance(consumer));
      if (sizeEstimate != Long.MAX_VALUE) {
        sizeEstimate -= i;
      }
      return new DoubleArraySpliterator(a, 0, i, characteristics);
    }
  }

  /**
   * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Spliterators.AbstractIntSpliterator.html">
   * the official Java API doc</a> for details.
   */
  public static abstract class AbstractIntSpliterator implements Spliterator.OfInt {
    static final class ValueConsumer implements IntConsumer {
      int value;

      @Override
      public void accept(int value) {
        this.value = value;
      }
    }

    private final ValueConsumer consumer = new ValueConsumer();
    private final int characteristics;
    private long sizeEstimate;

    protected AbstractIntSpliterator(long size, int characteristics) {
      this.sizeEstimate = size;
      this.characteristics = (characteristics & Spliterator.SIZED) != 0 ?
          characteristics | Spliterator.SUBSIZED : characteristics;
    }

    @Override
    public int characteristics() {
      return characteristics;
    }

    @Override
    public long estimateSize() {
      return sizeEstimate;
    }

    @Override
    public OfInt trySplit() {
      if (sizeEstimate == 0) {
        return null;
      }

      if (!tryAdvance(consumer)) {
        return null;
      }

      int n = BATCH;
      if (sizeEstimate != Long.MAX_VALUE) {
        n = Math.min((int) sizeEstimate, n);
      }
      int[] a = new int[n];
      int i = 0;
      do {
        a[i] = consumer.value;
      } while (++i < n && tryAdvance(consumer));
      if (sizeEstimate != Long.MAX_VALUE) {
        sizeEstimate -= i;
      }
      return new IntArraySpliterator(a, 0, i, characteristics);
    }
  }

  /**
   * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Spliterators.AbstractLongSpliterator.html">
   * the official Java API doc</a> for details.
   */
  public static abstract class AbstractLongSpliterator implements Spliterator.OfLong {
    static final class ValueConsumer implements LongConsumer {
      long value;

      @Override
      public void accept(long value) {
        this.value = value;
      }
    }

    private final ValueConsumer consumer = new ValueConsumer();
    private final int characteristics;
    private long sizeEstimate;

    protected AbstractLongSpliterator(long size, int characteristics) {
      this.sizeEstimate = size;
      this.characteristics = (characteristics & Spliterator.SIZED) != 0 ?
          characteristics | Spliterator.SUBSIZED : characteristics;
    }

    @Override
    public int characteristics() {
      return characteristics;
    }

    @Override
    public long estimateSize() {
      return sizeEstimate;
    }

    @Override
    public OfLong trySplit() {
      if (sizeEstimate == 0) {
        return null;
      }

      if (!tryAdvance(consumer)) {
        return null;
      }

      int n = BATCH;
      if (sizeEstimate != Long.MAX_VALUE) {
        n = Math.min((int) sizeEstimate, n);
      }
      long[] a = new long[n];
      int i = 0;
      do {
        a[i] = consumer.value;
      } while (++i < n && tryAdvance(consumer));
      if (sizeEstimate != Long.MAX_VALUE) {
        sizeEstimate -= i;
      }
      return new LongArraySpliterator(a, 0, i, characteristics);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> Spliterator<T> emptySpliterator() {
    return (Spliterator<T>) EmptySpliterator.OF_REF;
  }

  public static Spliterator.OfDouble emptyDoubleSpliterator() {
    return EmptySpliterator.OF_DOUBLE;
  }

  public static Spliterator.OfInt emptyIntSpliterator() {
    return EmptySpliterator.OF_INT;
  }

  public static Spliterator.OfLong emptyLongSpliterator() {
    return EmptySpliterator.OF_LONG;
  }

  public static <T> Spliterator<T> spliterator(Object[] array, int characteristics) {
    return new ArraySpliterator<>(checkNotNull(array), characteristics);
  }

  public static <T> Spliterator<T> spliterator(Object[] array, int fromIndex, int toIndex,
                                               int characteristics) {
    checkNotNull(array);
    checkCriticalPositionIndexes(fromIndex, toIndex, array.length);
    return new ArraySpliterator<>(array, fromIndex, toIndex, characteristics);
  }

  public static Spliterator.OfInt spliterator(int[] array,
                                              int characteristics) {
    return new IntArraySpliterator(checkNotNull(array), characteristics);
  }

  public static Spliterator.OfInt spliterator(int[] array, int fromIndex, int toIndex,
                                              int characteristics) {
    checkNotNull(array);
    checkCriticalPositionIndexes(fromIndex, toIndex, array.length);
    return new IntArraySpliterator(array, fromIndex, toIndex, characteristics);
  }

  public static Spliterator.OfLong spliterator(long[] array,
                                               int characteristics) {
    return new LongArraySpliterator(checkNotNull(array), characteristics);
  }

  public static Spliterator.OfLong spliterator(long[] array, int fromIndex, int toIndex,
                                               int characteristics) {
    checkNotNull(array);
    checkCriticalPositionIndexes(fromIndex, toIndex, array.length);
    return new LongArraySpliterator(array, fromIndex, toIndex, characteristics);
  }

  public static Spliterator.OfDouble spliterator(double[] array, int characteristics) {
    return new DoubleArraySpliterator(checkNotNull(array), characteristics);
  }

  public static Spliterator.OfDouble spliterator(double[] array, int fromIndex, int toIndex,
                                                 int characteristics) {
    checkNotNull(array);
    checkCriticalPositionIndexes(fromIndex, toIndex, array.length);
    return new DoubleArraySpliterator(array, fromIndex, toIndex, characteristics);
  }

  public static <T> Spliterator<T> spliterator(Collection<? extends T> c, int characteristics) {
    return new IteratorSpliterator<>(checkNotNull(c), characteristics);
  }

  public static <T> Spliterator<T> spliterator(Iterator<? extends T> iterator, long size,
                                               int characteristics) {
    return new IteratorSpliterator<>(checkNotNull(iterator), size, characteristics);
  }

  public static <T> Spliterator<T> spliteratorUnknownSize(Iterator<? extends T> iterator,
                                                          int characteristics) {
    return new IteratorSpliterator<>(checkNotNull(iterator), characteristics);
  }

  public static Spliterator.OfInt spliterator(PrimitiveIterator.OfInt iterator, long size,
                                              int characteristics) {
    return new IntIteratorSpliterator(checkNotNull(iterator), size, characteristics);
  }

  public static Spliterator.OfInt spliteratorUnknownSize(PrimitiveIterator.OfInt iterator,
                                                         int characteristics) {
    return new IntIteratorSpliterator(checkNotNull(iterator), characteristics);
  }

  public static Spliterator.OfLong spliterator(PrimitiveIterator.OfLong iterator, long size,
                                               int characteristics) {
    return new LongIteratorSpliterator(checkNotNull(iterator), size, characteristics);
  }

  public static Spliterator.OfLong spliteratorUnknownSize(PrimitiveIterator.OfLong iterator,
                                                          int characteristics) {
    return new LongIteratorSpliterator(checkNotNull(iterator), characteristics);
  }

  public static Spliterator.OfDouble spliterator(PrimitiveIterator.OfDouble iterator, long size,
                                                 int characteristics) {
    return new DoubleIteratorSpliterator(checkNotNull(iterator), size, characteristics);
  }

  public static Spliterator.OfDouble spliteratorUnknownSize(PrimitiveIterator.OfDouble iterator,
                                                            int characteristics) {
    return new DoubleIteratorSpliterator(checkNotNull(iterator), characteristics);
  }

  public static <T> Iterator<T> iterator(Spliterator<? extends T> spliterator) {
    return new ConsumerIterator<>(spliterator);
  }

  public static PrimitiveIterator.OfDouble iterator(Spliterator.OfDouble spliterator) {
    return new DoubleConsumerIterator(spliterator);
  }

  public static PrimitiveIterator.OfInt iterator(Spliterator.OfInt spliterator) {
    return new IntConsumerIterator(spliterator);
  }

  public static PrimitiveIterator.OfLong iterator(Spliterator.OfLong spliterator) {
    return new LongConsumerIterator(spliterator);
  }

  private static abstract class EmptySpliterator<T, S extends Spliterator<T>, C> {

    static final Spliterator<Object> OF_REF = new EmptySpliterator.OfRef<>();
    static final Spliterator.OfDouble OF_DOUBLE = new EmptySpliterator.OfDouble();
    static final Spliterator.OfInt OF_INT = new EmptySpliterator.OfInt();
    static final Spliterator.OfLong OF_LONG = new EmptySpliterator.OfLong();

    public int characteristics() {
      return Spliterator.SIZED | Spliterator.SUBSIZED;
    }

    public long estimateSize() {
      return 0;
    }

    public void forEachRemaining(C consumer) {
      checkNotNull(consumer);
    }

    public boolean tryAdvance(C consumer) {
      checkNotNull(consumer);
      return false;
    }

    public S trySplit() {
      return null;
    }

    private static final class OfRef<T>
        extends EmptySpliterator<T, Spliterator<T>, Consumer<? super T>>
        implements Spliterator<T> {

      OfRef() {
      }
    }

    private static final class OfDouble
        extends EmptySpliterator<Double, Spliterator.OfDouble, DoubleConsumer>
        implements Spliterator.OfDouble {

      OfDouble() {
      }
    }

    private static final class OfInt
        extends EmptySpliterator<Integer, Spliterator.OfInt, IntConsumer>
        implements Spliterator.OfInt {

      OfInt() {
      }
    }

    private static final class OfLong
        extends EmptySpliterator<Long, Spliterator.OfLong, LongConsumer>
        implements Spliterator.OfLong {

      OfLong() {
      }
    }
  }

  private static final class ConsumerIterator<T> implements Consumer<T>, Iterator<T> {
    private final Spliterator<? extends T> spliterator;
    private T nextElement;
    private boolean hasElement = false;

    ConsumerIterator(Spliterator<? extends T> spliterator) {
      this.spliterator = checkNotNull(spliterator);
    }

    @Override
    public void accept(T t) {
      nextElement = t;
      hasElement = true;
    }

    @Override
    public boolean hasNext() {
      if (!hasElement) {
        spliterator.tryAdvance(this);
      }
      return hasElement;
    }

    @Override
    public T next() {
      checkCriticalArgument(hasElement || hasNext());
      hasElement = false;
      return nextElement;
    }
  }

  private static final class DoubleConsumerIterator
      implements DoubleConsumer, PrimitiveIterator.OfDouble {

    private final Spliterator.OfDouble spliterator;
    private double nextElement;
    private boolean hasElement = false;

    DoubleConsumerIterator(Spliterator.OfDouble spliterator) {
      this.spliterator = checkNotNull(spliterator);
    }

    @Override
    public void accept(double d) {
      nextElement = d;
      hasElement = true;
    }

    @Override
    public boolean hasNext() {
      if (!hasElement) {
        spliterator.tryAdvance(this);
      }
      return hasElement;
    }

    @Override
    public double nextDouble() {
      checkCriticalArgument(hasElement || hasNext());
      hasElement = false;
      return nextElement;
    }
  }

  private static final class IntConsumerIterator
      implements IntConsumer, PrimitiveIterator.OfInt {

    private final Spliterator.OfInt spliterator;
    private int nextElement;
    private boolean hasElement = false;

    IntConsumerIterator(Spliterator.OfInt spliterator) {
      this.spliterator = checkNotNull(spliterator);
    }

    @Override
    public void accept(int i) {
      nextElement = i;
      hasElement = true;
    }

    @Override
    public boolean hasNext() {
      if (!hasElement) {
        spliterator.tryAdvance(this);
      }
      return hasElement;
    }

    @Override
    public int nextInt() {
      checkCriticalArgument(hasElement || hasNext());
      hasElement = false;
      return nextElement;
    }
  }

  private static final class LongConsumerIterator
      implements LongConsumer, PrimitiveIterator.OfLong {

    private final Spliterator.OfLong spliterator;
    private long nextElement;
    private boolean hasElement = false;

    LongConsumerIterator(Spliterator.OfLong spliterator) {
      this.spliterator = checkNotNull(spliterator);
    }

    @Override
    public void accept(long l) {
      nextElement = l;
      hasElement = true;
    }

    @Override
    public boolean hasNext() {
      if (!hasElement) {
        spliterator.tryAdvance(this);
      }
      return hasElement;
    }

    @Override
    public long nextLong() {
      checkCriticalArgument(hasElement || hasNext());
      hasElement = false;
      return nextElement;
    }
  }

  private static class IteratorSpliterator<T> extends AbstractSpliterator<T> {
    private final Iterator<? extends T> it;

    IteratorSpliterator(Collection<? extends T> collection, int characteristics) {
      this(collection.iterator(), collection.size(), characteristics);
    }

    IteratorSpliterator(Iterator<? extends T> iterator, long size, int characteristics) {
      super(size, (characteristics & Spliterator.CONCURRENT) == 0 ?
          characteristics | Spliterator.SIZED | Spliterator.SUBSIZED : characteristics);
      this.it = iterator;
    }

    IteratorSpliterator(Iterator<? extends T> iterator, int characteristics) {
      super(Long.MAX_VALUE, characteristics & ~(Spliterator.SIZED | Spliterator.SUBSIZED));
      this.it = iterator;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> consumer) {
      it.forEachRemaining(consumer);
    }

    @Override
    public Comparator<? super T> getComparator() {
      checkCritcalState(hasCharacteristics(Spliterator.SORTED));
      return null;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> consumer) {
      checkNotNull(consumer);
      if (!it.hasNext()) {
        return false;
      }

      consumer.accept(it.next());
      return true;
    }
  }

  static final class DoubleIteratorSpliterator extends AbstractDoubleSpliterator {
    private final PrimitiveIterator.OfDouble it;

    DoubleIteratorSpliterator(PrimitiveIterator.OfDouble iterator, long size, int characteristics) {
      super(size, (characteristics & Spliterator.CONCURRENT) == 0 ?
          characteristics | Spliterator.SIZED | Spliterator.SUBSIZED : characteristics);
      this.it = iterator;
    }

    DoubleIteratorSpliterator(PrimitiveIterator.OfDouble iterator, int characteristics) {
      super(Long.MAX_VALUE, characteristics & ~(Spliterator.SIZED | Spliterator.SUBSIZED));
      this.it = iterator;
    }

    @Override
    public void forEachRemaining(DoubleConsumer consumer) {
      it.forEachRemaining(consumer);
    }

    @Override
    public Comparator<? super Double> getComparator() {
      checkCritcalState(hasCharacteristics(Spliterator.SORTED));
      return null;
    }

    @Override
    public boolean tryAdvance(DoubleConsumer consumer) {
      checkNotNull(consumer);
      if (it.hasNext()) {
        consumer.accept(it.nextDouble());
        return true;
      }
      return false;
    }
  }

  static final class IntIteratorSpliterator extends AbstractIntSpliterator {
    private final PrimitiveIterator.OfInt it;

    IntIteratorSpliterator(PrimitiveIterator.OfInt iterator, long size, int characteristics) {
      super(size, (characteristics & Spliterator.CONCURRENT) == 0 ?
          characteristics | Spliterator.SIZED | Spliterator.SUBSIZED : characteristics);
      this.it = iterator;
    }

    IntIteratorSpliterator(PrimitiveIterator.OfInt iterator, int characteristics) {
      super(Long.MAX_VALUE, characteristics & ~(Spliterator.SIZED | Spliterator.SUBSIZED));
      this.it = iterator;
    }

    @Override
    public void forEachRemaining(IntConsumer consumer) {
      it.forEachRemaining(consumer);
    }

    @Override
    public Comparator<? super Integer> getComparator() {
      checkCritcalState(hasCharacteristics(Spliterator.SORTED));
      return null;
    }

    @Override
    public boolean tryAdvance(IntConsumer consumer) {
      checkNotNull(consumer);
      if (it.hasNext()) {
        consumer.accept(it.nextInt());
        return true;
      }
      return false;
    }
  }

  static final class LongIteratorSpliterator extends AbstractLongSpliterator {
    private final PrimitiveIterator.OfLong it;

    LongIteratorSpliterator(PrimitiveIterator.OfLong iterator, long size, int characteristics) {
      super(size, (characteristics & Spliterator.CONCURRENT) == 0 ?
          characteristics | Spliterator.SIZED | Spliterator.SUBSIZED : characteristics);
      this.it = iterator;
    }

    LongIteratorSpliterator(PrimitiveIterator.OfLong iterator, int characteristics) {
      super(Long.MAX_VALUE, characteristics & ~(Spliterator.SIZED | Spliterator.SUBSIZED));
      this.it = iterator;
    }

    @Override
    public void forEachRemaining(LongConsumer consumer) {
      it.forEachRemaining(consumer);
    }

    @Override
    public Comparator<? super Long> getComparator() {
      checkCritcalState(hasCharacteristics(Spliterator.SORTED));
      return null;
    }

    @Override
    public boolean tryAdvance(LongConsumer consumer) {
      checkNotNull(consumer);
      if (it.hasNext()) {
        consumer.accept(it.nextLong());
        return true;
      }
      return false;
    }
  }

  static final class ArraySpliterator<T> implements Spliterator<T> {
    private final Object[] array;
    private int index;
    private final int limit;
    private final int characteristics;

    ArraySpliterator(Object[] array, int characteristics) {
      this(array, 0, array.length, characteristics);
    }

    ArraySpliterator(Object[] array, int from, int limit, int characteristics) {
      this.array = array;
      this.index = from;
      this.limit = limit;
      this.characteristics = characteristics | Spliterator.SIZED | Spliterator.SUBSIZED;
    }

    @Override
    public int characteristics() {
      return characteristics;
    }

    @Override
    public long estimateSize() {
      return limit - index;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void forEachRemaining(Consumer<? super T> consumer) {
      while (tryAdvance(consumer)) { }
    }

    @Override
    public Comparator<? super T> getComparator() {
      checkCritcalState(hasCharacteristics(Spliterator.SORTED));
      return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean tryAdvance(Consumer<? super T> consumer) {
      checkNotNull(consumer);
      if (index < limit) {
        consumer.accept((T) array[index++]);
        return true;
      }
      return false;
    }

    @Override
    public Spliterator<T> trySplit() {
      int lo = index;
      int mid = (lo + limit) / 2;
      if (lo >= mid) {
        return null;
      }
      index = mid;
      return new ArraySpliterator<>(array, lo, mid, characteristics);
    }
  }

  static final class DoubleArraySpliterator implements Spliterator.OfDouble {
    private final double[] array;
    private int index;
    private final int limit;
    private final int characteristics;

    DoubleArraySpliterator(double[] array, int characteristics) {
      this(array, 0, array.length, characteristics);
    }

    DoubleArraySpliterator(double[] array, int from, int limit, int characteristics) {
      this.array = array;
      this.index = from;
      this.limit = limit;
      this.characteristics = characteristics | Spliterator.SIZED | Spliterator.SUBSIZED;
    }

    @Override
    public int characteristics() {
      return characteristics;
    }

    @Override
    public long estimateSize() {
      return limit - index;
    }

    @Override
    public void forEachRemaining(DoubleConsumer consumer) {
      while (tryAdvance(consumer)) { }
    }

    @Override
    public Comparator<? super Double> getComparator() {
      checkCritcalState(hasCharacteristics(Spliterator.SORTED));
      return null;
    }

    @Override
    public boolean tryAdvance(DoubleConsumer consumer) {
      checkNotNull(consumer);
      if (index < limit) {
        consumer.accept(array[index++]);
        return true;
      }
      return false;
    }

    @Override
    public OfDouble trySplit() {
      int lo = index;
      int mid = (lo + limit) / 2;
      if (lo >= mid) {
        return null;
      }
      index = mid;
      return new DoubleArraySpliterator(array, lo, mid, characteristics);
    }
  }

  static final class IntArraySpliterator implements Spliterator.OfInt {
    private final int[] array;
    private int index;
    private final int limit;
    private final int characteristics;

    IntArraySpliterator(int[] array, int characteristics) {
      this(array, 0, array.length, characteristics);
    }

    IntArraySpliterator(int[] array, int from, int limit, int characteristics) {
      this.array = array;
      this.index = from;
      this.limit = limit;
      this.characteristics = characteristics | Spliterator.SIZED | Spliterator.SUBSIZED;
    }

    @Override
    public int characteristics() {
      return characteristics;
    }

    @Override
    public long estimateSize() {
      return limit - index;
    }

    @Override
    public void forEachRemaining(IntConsumer consumer) {
      while (tryAdvance(consumer)) { }
    }

    @Override
    public Comparator<? super Integer> getComparator() {
      checkCritcalState(hasCharacteristics(Spliterator.SORTED));
      return null;
    }

    @Override
    public boolean tryAdvance(IntConsumer consumer) {
      checkNotNull(consumer);
      if (index < limit) {
        consumer.accept(array[index++]);
        return true;
      }
      return false;
    }

    @Override
    public OfInt trySplit() {
      int lo = index;
      int mid = (lo + limit) / 2;
      if (lo >= mid) {
        return null;
      }
      index = mid;
      return new IntArraySpliterator(array, lo, mid, characteristics);
    }
  }

  static final class LongArraySpliterator implements Spliterator.OfLong {
    private final long[] array;
    private int index;
    private final int limit;
    private final int characteristics;

    LongArraySpliterator(long[] array, int characteristics) {
      this(array, 0, array.length, characteristics);
    }

    LongArraySpliterator(long[] array, int from, int limit, int characteristics) {
      this.array = array;
      this.index = from;
      this.limit = limit;
      this.characteristics = characteristics | Spliterator.SIZED | Spliterator.SUBSIZED;
    }

    @Override
    public int characteristics() {
      return characteristics;
    }

    @Override
    public long estimateSize() {
      return limit - index;
    }

    @Override
    public void forEachRemaining(LongConsumer consumer) {
      while (tryAdvance(consumer)) { }
    }

    @Override
    public Comparator<? super Long> getComparator() {
      checkCritcalState(hasCharacteristics(Spliterator.SORTED));
      return null;
    }

    @Override
    public boolean tryAdvance(LongConsumer consumer) {
      checkNotNull(consumer);
      if (index < limit) {
        consumer.accept(array[index++]);
        return true;
      }
      return false;
    }

    @Override
    public OfLong trySplit() {
      int lo = index;
      int mid = (lo + limit) / 2;
      if (lo >= mid) {
        return null;
      }
      index = mid;
      return new LongArraySpliterator(array, lo, mid, characteristics);
    }
  }

  private static final int BATCH = 1024;

  private Spliterators() { }

}
