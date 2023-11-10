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

/**
 * An interface to generate a series of elements, one at a time. <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/Enumeration.html">[Sun
 * docs]</a>
 * 
 * @param <E> the type being enumerated.
 */
public interface Enumeration<E> {

  boolean hasMoreElements();

  E nextElement();

  default Iterator<E> asIterator() {
    return new Iterator<E>() {
      @Override
      public boolean hasNext() {
        return hasMoreElements();
      }
      @Override
      public E next() {
        return nextElement();
      }
    };
  }
}
