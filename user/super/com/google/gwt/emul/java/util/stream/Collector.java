package java.util.stream;

import java.lang.Override;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.function.Function;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.BinaryOperator;
import java.util.function.BiConsumer;

public interface Collector<T,A,R> {
  public enum Characteristics { CONCURRENT, IDENTITY_FINISH, UNORDERED }

  public static <T,A,R> Collector<T,A,R> of(Supplier<A> supplier, BiConsumer<A,T> accumulator, BinaryOperator<A> combiner, Function<A,R> finisher, Collector.Characteristics... characteristics) {
    return new CollectorImpl<T, A, R>(
        supplier,
        accumulator,
        new HashSet<Characteristics>(Arrays.asList(characteristics)),
        combiner,
        finisher
    );
  }

  public static <T,R> Collector<T,R,R> of(Supplier<R> supplier, BiConsumer<R,T> accumulator, BinaryOperator<R> combiner, Collector.Characteristics... characteristics) {
    return new CollectorImpl<T, R, R>(
        supplier,
        accumulator,
        new HashSet<Characteristics>(Arrays.asList(characteristics)),
        combiner,
        Function.<R>identity()
    );
  }

  Supplier<A> supplier();

  BiConsumer<A,T> accumulator();

  Set<Collector.Characteristics> characteristics();

  BinaryOperator<A> combiner();

  Function<A,R> finisher();

  static final class CollectorImpl<T, A, R> implements Collector<T, A, R> {
    private final Supplier<A> supplier;
    private final BiConsumer<A, T> accumulator;
    private final Set<Collector.Characteristics> characteristics;
    private final BinaryOperator<A> combiner;
    private final Function<A, R> finisher;

    public CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, Set<Characteristics> characteristics, BinaryOperator<A> combiner, Function<A, R> finisher) {
      this.supplier = supplier;
      this.accumulator = accumulator;
      this.characteristics = characteristics;
      this.combiner = combiner;
      this.finisher = finisher;
    }

    @Override
    public Supplier<A> supplier() {
      return supplier;
    }

    @Override
    public BiConsumer<A, T> accumulator() {
      return accumulator;
    }

    @Override
    public BinaryOperator<A> combiner() {
      return combiner;
    }

    @Override
    public Function<A, R> finisher() {
      return finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return characteristics;
    }
  }
}