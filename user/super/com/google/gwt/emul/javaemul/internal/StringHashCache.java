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
package javaemul.internal;

import static javaemul.internal.Coercions.ensureInt;

/**
 * Hashcode caching for strings.
 */
class StringHashCache {
  /**
   * The "old" cache; it will be dumped when front is full.
   */
  private static InternalJsMap<Object> back = InternalJsMapFactory.newJsMap();
  /**
   * Tracks the number of entries in front.
   */
  private static int count = 0;
  /**
   * The "new" cache; it will become back when it becomes full.
   */
  private static InternalJsMap<Object> front = InternalJsMapFactory.newJsMap();
  /**
   * Pulled this number out of thin air.
   */
  private static final int MAX_CACHE = 256;

  public static int getHashCode(String str) {
    // Check the front store.
    Object result = front.get(str);
    if (!JsUtils.isUndefined(result)) {
      return unsafeCastToInt(result);
    }
    // Check the back store.
    result = back.get(str);
    int hashCode = JsUtils.isUndefined(result) ? compute(str) : unsafeCastToInt(result);
    // Increment can trigger the swap/flush; call after checking back but
    // before writing to front.
    increment();
    front.set(str, unsafeCastToObject(hashCode));

    return hashCode;
  }

  private static int compute(String str) {
    int hashCode = 0;
    int n = str.length();
    int nBatch = n - 4;
    int i = 0;

    // Process batches of 4 characters at a time and add them to the hash coercing to 32 bits
    while (i < nBatch) {
      hashCode = str.charAt(i + 3)
          + 31 * (str.charAt(i + 2)
          + 31 * (str.charAt(i + 1)
          + 31 * (str.charAt(i)
          + 31 * hashCode)));

      hashCode = ensureInt(hashCode); // make sure we don't overflow
      i += 4;
    }

    // Now process the leftovers
    while (i < n) {
      hashCode = hashCode * 31 + str.charAt(i++);
    }
    hashCode = ensureInt(hashCode); // make sure we don't overflow

    return hashCode;
  }

  private static void increment() {
    if (count == MAX_CACHE) {
      back = front;
      front = InternalJsMapFactory.newJsMap();
      count = 0;
    }
    ++count;
  }

  private static native int unsafeCastToInt(Object o) /*-{
    return o;
  }-*/;

  private static native Object unsafeCastToObject(int i) /*-{
    return i;
  }-*/;
}
