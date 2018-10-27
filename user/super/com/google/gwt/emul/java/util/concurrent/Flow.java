/*
 * Copyright 2018 Google Inc.
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
package java.util.concurrent;

/**
 * Emulation of Flow.
 */
public final class Flow {

  private Flow() { }

  /**
   * Emulation of Publisher.
   *
   * @param <T> the published item type
   */
  @FunctionalInterface
  public interface Publisher<T> {
    void subscribe(Subscriber<? super T> subscriber);
  }

  /**
   * Emulation of Subscriber.
   *
   * @param <T> the subscribed item type
   */
  public interface Subscriber<T> {
    void onSubscribe(Subscription subscription);

    void onNext(T item);

    void onError(Throwable throwable);

    void onComplete();
  }

  /**
   * Emulation of Subscription.
   */
  public interface Subscription {
    void request(long n);

    void cancel();
  }

  /**
   * Emulation of Processor.
   *
   * @param <T> the subscribed item type
   * @param <R> the published item type
   */
  public interface Processor<T,R> extends Subscriber<T>, Publisher<R> {
  }

  private static final int DEFAULT_BUFFER_SIZE = 256;

  public static int defaultBufferSize() {
    return DEFAULT_BUFFER_SIZE;
  }
}
