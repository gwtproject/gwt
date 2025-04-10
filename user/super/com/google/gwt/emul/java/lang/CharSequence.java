/*
 * Copyright 2008 Google Inc.
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

import static javaemul.internal.InternalPreconditions.checkElement;

import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import javaemul.internal.JsUtils;
import jsinterop.annotations.JsMethod;

/**
 * Abstracts the notion of a sequence of characters.
 */
public interface CharSequence {
  char charAt(int index);

  int length();

  CharSequence subSequence(int start, int end);

  @Override
  String toString();

  default IntStream chars() {
    return  StreamSupport.intStream(() -> {
      PrimitiveIterator.OfInt it = new PrimitiveIterator.OfInt() {
        int cursor;

        @Override
        public int nextInt() {
          checkElement(hasNext());
          return charAt(cursor++);
        }

        @Override
        public boolean hasNext() {
          return cursor < length();
        }
      };
      return Spliterators.spliterator(it, length(), Spliterator.ORDERED);
    }, Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.ORDERED, false);
  }

  default IntStream codePoints() {
    return  StreamSupport.intStream(() -> {
      PrimitiveIterator.OfInt it = new PrimitiveIterator.OfInt() {
        int cursor;

        @Override
        public int nextInt() {
          checkElement(hasNext());
          int codePoint = CharSequence.this.toString().codePointAt(cursor++);
          if (codePoint >= 1 << 16) {
            cursor++;
          }
          return codePoint;
        }

        @Override
        public boolean hasNext() {
          return cursor < length();
        }
      };
      return Spliterators.spliterator(it, length(), Spliterator.ORDERED);
    }, Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.ORDERED, false);
  }

  default boolean isEmpty() {
    return length() == 0;
  }

  static int compare(CharSequence cs1, CharSequence cs2) {
    return cs1.toString().compareTo(cs2.toString());
  }

  // CHECKSTYLE_OFF: Utility methods.
  @JsMethod
  static boolean $isInstance(HasCharSequenceTypeMarker instance) {
    if (JsUtils.typeOf(instance).equals("string")) {
      return true;
    }

    return instance != null && instance.getTypeMarker() == true;
  }
  // CHECKSTYLE_ON: end utility methods
}
