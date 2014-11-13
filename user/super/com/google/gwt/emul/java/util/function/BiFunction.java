/*
 * Copyright 2014 Google Inc.
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
package java.util.function;

import com.google.gwt.core.shared.impl.InternalPreconditions;

/**
 * See <a href="https://docs.oracle.com/javase/8/docs/api/java/util/function/BiFunction.html">
 * the official Java API doc</a> for details.
 *
 * @param <T> type of the first argument
 * @param <U> type of the second argument
 * @param <R> type of the return value
 */
@FunctionalInterface
public interface BiFunction<T, U, R> {

  R apply(T t, U u);

  default <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after) {
    InternalPreconditions.checkNotNull(after);
    return (t, u) -> after.apply(apply(t, u));
  }

}
