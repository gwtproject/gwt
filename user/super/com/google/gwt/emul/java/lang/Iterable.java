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
package java.lang;

import static javaemul.internal.InternalPreconditions.checkNotNull;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

/**
 * Allows an instance of a class implementing this interface to be used in the
 * foreach statement. <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Iterable.html">[Sun
 * docs]</a>
 * 
 * @param <T> type of returned iterator
 */
public interface Iterable<T> {
  Iterator<T> iterator();

  default void forEach(Consumer<? super T> consumer) {
    checkNotNull(consumer);
    for (T e : this) {
      consumer.accept(e);
    }
  }

  default Spliterator<T> spliterator() {
    return Spliterators.spliteratorUnknownSize(iterator(), 0);
  }
}
