/*
 * Copyright 2021 Google Inc.
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

/** Defines utility static functions for long */
public final class LongUtils {

  public static long fromBits(int lowBits, int highBits) {
    long lowBitsLong = lowBits & 0x00000000ffffffffL;
    long highBitsLong = (long) highBits << 32;
    return highBitsLong | lowBitsLong;
  }

  public static int getHighBits(long value) {
    return (int) (value >>> 32);
  }

  private LongUtils() {}
}
