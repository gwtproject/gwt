// CHECKSTYLE_OFF: Copyrighted to Guava Authors.
/*
 * Copyright (C) 2017 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// CHECKSTYLE_ON

package java.util.concurrent;

import java.util.Collection;
import java.util.List;

/**
 * Emulation of ExecutorService.
 */
public interface ExecutorService extends Executor {

  void shutdown();

  List<Runnable> shutdownNow();

  boolean isShutdown();

  boolean isTerminated();

  // Blocking calls cannot be emulated on web. Subclasses of this class in shared code should mark
  // their override with @GwtIncompatible.
  // @GwtIncompatible("blocking")
  // boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

  <T> Future<T> submit(Callable<T> task);

  <T> Future<T> submit(Runnable task, T result);

  Future<?> submit(Runnable task);

  // Even though invokeAll and invokeAy methods below are blocking, they actually block on execution
  // of the task that is provided hence could be emulated by directly executing them.

  <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
      throws InterruptedException;

  <T> List<Future<T>> invokeAll(
      Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
      throws InterruptedException;

  <T> T invokeAny(Collection<? extends Callable<T>> tasks)
      throws InterruptedException, ExecutionException;

  <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException;
}
