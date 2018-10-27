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

  private Flow() {}

  @FunctionalInterface
  public static interface Publisher<T> {
    public void subscribe(Subscriber<? super T> subscriber);
  }

  public static interface Subscriber<T> {
    public void onSubscribe(Subscription subscription);

    public void onNext(T item);

    public void onError(Throwable throwable);

    public void onComplete();
  }

  public static interface Subscription {
    public void request(long n);

    public void cancel();
  }

  public static interface Processor<T,R> extends Subscriber<T>, Publisher<R> {
  }

  static final int DEFAULT_BUFFER_SIZE = 256;

  public static int defaultBufferSize() {
    return DEFAULT_BUFFER_SIZE;
  }
}
