/*
 * Copyright 2009 Google Inc.
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

import static javaemul.internal.InternalPreconditions.checkArraySize;
import static javaemul.internal.InternalPreconditions.checkPositionIndex;

/**
 * This implementation uses a dense array holding bit groups of size 31 to keep track of when bits
 * are set to true or false. Using 31 bits keeps our implementation within the range of V8's
 * "tagged small integer" and improves performance. Using a dense array also makes access faster on
 * V8.
 *
 * Not yet implemented:
 * public int previousClearBit(int fromIndex)
 * public int previousSetBit(int fromIndex)
 * public byte[] toByteArray()
 * public long[] toLongArray()
 * public static BitSet valueOf(bytes[] bytes)
 * public static BitSet valueOf(ByteBuffer bb)
 * public static BitSet valueOf(long[] longs)
 */
public class BitSet {
  private int[] array;

  private static native int[] createArray(int length) /*-{
    var arr = new Array(length);
    for (var i = 0; i < length; i++) {
      arr[i] = 0;
    }
    return arr;
  }-*/;

  public BitSet() {
    // Pre-allocate to a length of 10 and grow as needed.
    array = createArray(10);
  }

  public BitSet(int nbits) {
    checkArraySize(nbits);

    array = createArray(nbits / 31 + 1);
  }

  private BitSet(int[] array) {
    this.array = array;
  }

  private static void checkIndex(int bitIndex) {
    if (bitIndex < 0) {
      throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
    }
  }

  /**
   * Checks to ensure indexes are not negative and not in reverse order.
   *
   * @param fromIndex The lower bit index.
   * @param toIndex The upper bit index.
   */
  private static void checkRange(int fromIndex, int toIndex) {
    checkPositionIndex(fromIndex, toIndex);
    checkPositionIndex(toIndex, toIndex);
  }

  /**
   * Converts from a bit index to a word index.
   *
   * @param bitIndex The bit index.
   * @return The index of the word (array entry) holding that bit.
   */
  private static int wordIndex(int bitIndex) {
    return bitIndex / 31;
  }

  /**
   * Converts from a word index to the lowest index of the bit stored in that word.
   *
   * @param wordIndex The word index.
   * @return The lowest index of the bit stored in that word.
   */
  private static int bitIndex(int wordIndex) {
    return wordIndex * 31;
  }

  /**
   * Computes the offset within a word for a bit index. Within a word, the offset counts from
   * right to left and caps at 31. This helps leaving the highest bit as zero to ensure that
   * each word is a tagged small integer in V8.
   *
   * @param bitIndex The bit index.
   * @return The offset of the bit within a word, counting from right to left.
   */
  private static int bitOffset(int bitIndex) {
    return bitIndex % 31;
  }

  /**
   * Clones an int[] array.
   *
   * @param array The int[] array.
   * @return The cloned array.
   */
  private static native int[] clone(int[] array) /*-{
    var cloned = new Array(array.length);
    var i = array.length;
    while (i--) {
      cloned[i] = array[i];
    }
    return cloned;
  }-*/;

  /**
   * Sets all bits to true within the given range.
   *
   * @param fromIndex The lower bit index.
   * @param toIndex The upper bit index.
   */
  private void setInternal(int fromIndex, int toIndex) {
    maybeGrowArrayTo(toIndex);

    int first = wordIndex(fromIndex);
    int last = wordIndex(toIndex);
    int startBit = bitOffset(fromIndex);
    int endBit = bitOffset(toIndex);

    if (first == last) {
      // Set the bits in between first and last.
      maskInWord(array, first, startBit, endBit);
    } else {
      // Set the bits from fromIndex to the next 31 bit boundary.
      maskInWord(array, first++, startBit, 31);

      // Set the bits from the last 31 bit boundary to toIndex.
      maskInWord(array, last, 0, endBit);

      // Set everything in between.
      for (int i = first; i < last; i++) {
        array[i] = 0x7fffffff;
      }
    }
  }

  /**
   * Returns a subarray.
   *
   * @param array The array to slice from.
   * @param fromIndex The index to slice from, inclusive.
   * @param toIndex The index to slice to, exclusive.
   * @return The sliced subarray
   */
  private static native int[] slice(int[] array, int fromIndex, int toIndex) /*-{
    return array.slice(fromIndex, toIndex);
  }-*/;

  private void maybeGrowArrayTo(int newLength) {
    if (newLength > array.length) {
      for (int i = array.length; i < newLength; i++) {
        array[i] = 0;
      }
    }
  }

  /**
   * Returns the index of the last word containing a true bit in an array, or -1 if none.
   *
   * @param array The array.
   * @return The index of the last word containing a true bit, or -1 if none.
   */
  private static int lastSetWordIndex(int[] array) {
    int length = array.length;
    if (length == 0) {
      return -1;
    }

    for (int i = length - 1; i >= 0; i--) {
      if (array[i] != 0) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Flips all bits in a word within the given range.
   *
   * @param array The array.
   * @param index The word index.
   * @param from The lower bit index.
   * @param to The upper bit index.
   */
  private static void flipMaskedWord(int[] array, int index, int from, int to) {
    if (from == to) {
      return;
    }

    int word = array[index];
    to = 32 - to;
    word ^= (((0xffffffff >>> from) << from) << to) >>> to;
    array[index] = word & 0x7fffffff;
  }

  /**
   * Sets all bits to true in a word within the given bit range.
   *
   * @param array The array.
   * @param index The word index.
   * @param from The lower bit index.
   * @param to The upper bit index.
   */
  private static void maskInWord(int[] array, int index, int from, int to) {
    if (from == to) {
      return;
    }

    to = 32 - to;
    int value = array[index];
    value |= ((0xffffffff >>> from) << (from + to)) >>> to;
    array[index] = value & 0x7fffffff;
  }

  /**
   * Sets all bits to false in a word within the given bit range.
   *
   * @param array The array.
   * @param index The word index.
   * @param from The lower bit index.
   * @param to The upper bit index.
   */
  private static void maskOutWord(int[] array, int index, int from, int to) {
    if (from == to) {
      return;
    }

    int word = array[index];
    if (word != 0) {
      int mask;
      if (from != 0) {
        mask = 0xffffffff >>> (32 - from);
      } else {
        mask = 0;
      }
      // Shifting by 32 is the same as shifting by 0.
      if (to != 32) {
        mask |= 0xffffffff << to;
      }

      word &= mask;
      array[index] = word & 0x7fffffff;
    }
  }

  private static int nextSetWord(int[] array, int index) {
    if (index >= array.length) {
      return -1;
    }

    for (int i = index; i < array.length; i++) {
      if (array[i] != 0) {
        return i;
      }
    }
    return -1;
  }

  public void and(BitSet set) {
    // a & a is just a.
    if (this == set) {
      return;
    }

    // Truth table
    //
    // Case | a     | b     | a & b | Change?
    // 1    | false | false | false | a is already false
    // 2    | false | true  | false | a is already false
    // 3    | true  | false | false | set a to false
    // 4    | true  | true  | true  | a is already true
    //
    // We only need to change something in case 3, so iterate over set a.
    int index = 0;
    while ((index = nextSetWord(array, index)) != -1) {
      array[index] = array[index] & set.array[index];
      index++;
    }
  }

  public void andNot(BitSet set) {
    // a & !a is false, and all falses result in an empty BitSet.
    if (this == set) {
      clear();
      return;
    }

    int last = lastSetWordIndex(array);

    // Truth table
    //
    // Case | a     | b     | !b    | a & !b | Change?
    // 1    | false | false | true  | false  | a is already false
    // 2    | false | true  | false | false  | a is already false
    // 3    | true  | false | true  | true   | a is already true
    // 4    | true  | true  | false | false  | set a to false
    //
    // We only need to change something in case 4. Whenever b is true, a should be false, so
    // iterate over set b.
    int index = 0;
    while ((index = nextSetWord(set.array, index)) != -1) {
      array[index] = array[index] & ~set.array[index] & 0x7fffffff;
      if (++index > last) {
        // Nothing further will affect anything.
        break;
      }
    }
  }

  public int cardinality() {
    int count = 0;
    for (int i = 0; i < array.length; i++) {
      count += Integer.bitCount(array[i]);
    }
    return count;
  }

  public void clear() {
    for (int i = 0; i < array.length; i++) {
      array[i] = 0;
    }
  }

  public void clear(int bitIndex) {
    checkIndex(bitIndex);

    int index = wordIndex(bitIndex);
    if (index >= array.length) {
      return;
    }

    int word = array[index];
    if (word != 0) {
      array[index] = word & ~(1 << bitOffset(bitIndex)) & 0x7fffffff;
    }
  }

  public void clear(int fromIndex, int toIndex) {
    checkRange(fromIndex, toIndex);

    int length = length();

    if (fromIndex >= length) {
      return;
    }

    toIndex = Math.min(toIndex, length);
    int first = wordIndex(fromIndex);
    int last = wordIndex(toIndex);
    int startBit = bitOffset(fromIndex);
    int endBit = bitOffset(toIndex);

    if (first == last) {
      // Clear the bits in between first and last.
      maskOutWord(array, first, startBit, endBit);
    } else {
      // Clear the bits from fromIndex to the next 31 bit boundary.
      maskOutWord(array, first++, startBit, 31);

      // Clear the bits from the last 31 bit boundary to the toIndex.
      maskOutWord(array, last, 0, endBit);

      // Clear everything in between.
      for (int i = first; i < last; i++) {
        array[i] = 0;
      }
    }
  }

  public Object clone() {
    return new BitSet(clone(array));
  }

  @Override
  public boolean equals(Object obj) {
    if (this != obj) {

      if (!(obj instanceof BitSet)) {
        return false;
      }

      BitSet other = (BitSet) obj;

      if (lastSetWordIndex(array) != lastSetWordIndex(other.array)) {
        return false;
      }

      int index = 0;
      while ((index = nextSetWord(array, index)) != -1) {
        if (array[index] != other.array[index]) {
          return false;
        }
        index++;
      }
    }

    return true;
  }

  public void flip(int bitIndex) {
    checkIndex(bitIndex);

    int index = wordIndex(bitIndex);
    int offset = bitOffset(bitIndex);

    maybeGrowArrayTo(index);

    int word = array[index];
    if (((word >>> offset) & 1) == 1) {
      array[index] = word & ~(1 << offset) & 0x7fffffff;
    } else {
      array[index] = (word | (1 << offset)) & 0x7fffffff;
    }
  }

  public void flip(int fromIndex, int toIndex) {
    checkRange(fromIndex, toIndex);

    int length = length();

    // If we are flipping bits beyond our length, we are setting them to true.
    if (fromIndex >= length) {
      setInternal(fromIndex, toIndex);
      return;
    }

    if (toIndex >= length) {
      setInternal(length, toIndex);
      toIndex = length;
    }

    int first = wordIndex(fromIndex);
    int last = wordIndex(toIndex);
    int startBit = bitOffset(fromIndex);
    int endBit = bitOffset(toIndex);

    if (first == last) {
      // Flip the bits in between first and last.
      flipMaskedWord(array, first, startBit, endBit);

    } else {
      // Flip the bits from fromIndex to the next 31 bit boundary.
      flipMaskedWord(array, first++, startBit, 31);

      // Flip the bits from the last 31 bit boundary to the toIndex.
      flipMaskedWord(array, last, 0, endBit);

      // Flip everything in between.
      for (int i = first; i < last; i++) {
        array[i] = ~array[i] & 0x7fffffff;
      }
    }
  }

  public boolean get(int bitIndex) {
    checkIndex(bitIndex);

    int index = wordIndex(bitIndex);
    if (index >= array.length) {
      return false;
    }

    // Shift and mask the bit out
    return ((array[index] >>> bitOffset(bitIndex)) & 1) == 1;
  }

  public BitSet get(int fromIndex, int toIndex) {
    checkRange(fromIndex, toIndex);
    if (fromIndex == toIndex) {
      return new BitSet();
    }

    toIndex = Math.min(toIndex, length());

    // The bit shift offset for each group of bits
    int rightShift = bitOffset(fromIndex);

    if (rightShift == 0) {
      int subFrom = wordIndex(fromIndex);
      int subTo = wordIndex(toIndex + 31);
      int[] subSet = slice(array, subFrom, subTo);
      int leftOvers = bitOffset(toIndex);
      maskOutWord(subSet, subTo - subFrom - 1, leftOvers, 31);
      return new BitSet(subSet);
    }

    BitSet subSet = new BitSet();

    int first = wordIndex(fromIndex);
    int last = wordIndex(toIndex);

    if (first == last) {
      // Number of bits to cut from the end
      int end = 32 - bitOffset(toIndex);
      int word = array[first];
      word = ((word << end) >>> end) >>> rightShift;
      if (word != 0) {
        subSet.array[0] = word;
      }
    } else {
      // This holds the newly packed bits.
      int current = 0;

      // The raw index into the subset
      int subIndex = 0;

      // Fence post, carry over initial bits.
      int word = array[first++];
      current = word >>> rightShift;

      // A left shift will be used to shift our bits to the top of "current".
      int leftShift = 31 - rightShift;

      // Loop through everything in the middle.
      for (int i = first; i <= last; i++) {
        word = array[i];

        // Shift out the bits from the top, OR them into current bits.
        current |= word << leftShift;

        if (current != 0) {
          subSet.array[subIndex] = current;
        }

        subIndex++;

        // Carry over the unused bits.
        current = word >>> rightShift;
      }

      // Fence post, flush out the extra bits, but don't go past the "end".
      int end = 32 - bitOffset(toIndex);
      current = (current << (rightShift + end)) >>> (rightShift + end);
      if (current != 0) {
        subSet.array[subIndex] = current;
      }
    }

    return subSet;
  }

  /**
   * This hash is different than the one described in Sun's documentation. The
   * described hash uses 64 bit integers and that's not practical in JavaScript.
   */
  @Override
  public int hashCode() {
    // FNV constants
    final int fnvOffset = 0x811c9dc5;
    final int fnvPrime = 0x1000193;

    final int last = lastSetWordIndex(array);
    int hash = fnvOffset ^ last;

    for (int i = 0; i <= last; i++) {
      int value = array[i];
      // Hash one byte at a time using FNV1.
      hash = (hash * fnvPrime) ^ (value & 0xff);
      hash = (hash * fnvPrime) ^ ((value >>> 8) & 0xff);
      hash = (hash * fnvPrime) ^ ((value >>> 16) & 0xff);
      hash = (hash * fnvPrime) ^ (value >>> 24);
    }

    return hash;
  }

  public boolean intersects(BitSet set) {
    if (this == set) {
      // If it has any set bits then it intersects itself.
      return lastSetWordIndex(array) != -1;
    }

    int index = 0;
    while ((index = nextSetWord(array, index)) != -1) {
      if ((array[index] & set.array[index]) != 0) {
        return true;
      }
      if (++index >= set.array.length) {
        // Nothing further can intersect.
        break;
      }
    }

    return false;
  }

  public boolean isEmpty() {
    return length() == 0;
  }

  public int length() {
    int last = lastSetWordIndex(array);

    if (last == -1) {
      return 0;
    }

    // Compute the bit index of the leftmost bit.
    int[] offsets = { 16, 8, 4, 2, 1 };
    int[] bitMasks = { 0xffff0000, 0xff00, 0xf0, 0xc, 0x2 };
    int position = bitIndex(last) + 1;
    int word = array[last];
    for (int i = 0; i < offsets.length; i++) {
      if ((word & bitMasks[i]) != 0) {
        word >>>= offsets[i];
        position += offsets[i];
      }
    }
    return position;
  }

  public int nextClearBit(int fromIndex) {
    checkIndex(fromIndex);
    int index = wordIndex(fromIndex);

    // Special case for first word.
    int fromBit = fromIndex - bitIndex(index);
    int word = array[index];
    for (int i = fromBit; i < 31; i++) {
      if ((word & (1 << i)) == 0) {
        return bitIndex(index) + i;
      }
    }

    // Loop through the rest.
    do {
      index++;
      word = array[index];
    } while (word == 0x7fffffff);
    return bitIndex(index) + Integer.numberOfTrailingZeros(~word);
  }

  public int nextSetBit(int fromIndex) {
    checkIndex(fromIndex);

    int index = wordIndex(fromIndex);

    // Check the current word.
    int word = array[index];
    if (word != 0) {
      for (int i = bitOffset(fromIndex); i < 31; i++) {
        if ((word & (1 << i)) != 0) {
          return bitIndex(index) + i;
        }
      }
    }

    index++;

    // Find the next set word.
    index = nextSetWord(array, index);
    if (index == -1) {
      return -1;
    }

    // Return the next set bit.
    return bitIndex(index) + Integer.numberOfTrailingZeros(array[index]);
  }

  public void or(BitSet set) {
    // a | a is just a.
    if (this == set) {
      return;
    }

    // Truth table
    //
    // Case | a     | b     | a | b | Change?
    // 1    | false | false | false | a is already false
    // 2    | false | true  | true  | set a to true
    // 3    | true  | false | true  | a is already true
    // 4    | true  | true  | true  | a is already true
    //
    // We only need to change something in case 2. Case 2 only happens when b is true, so iterate
    // over set b
    int index = 0;
    while ((index = nextSetWord(set.array, index)) != -1) {
      array[index] = array[index] | set.array[index];
      index++;
    }
  }

  public void set(int bitIndex) {
    checkIndex(bitIndex);
    int index = wordIndex(bitIndex);
    maybeGrowArrayTo(index);
    array[index] = (array[index] | (1 << bitOffset(bitIndex))) & 0x7fffffff;
  }

  public void set(int bitIndex, boolean value) {
    if (value) {
      set(bitIndex);
    } else {
      clear(bitIndex);
    }
  }

  public void set(int fromIndex, int toIndex) {
    checkRange(fromIndex, toIndex);
    setInternal(fromIndex, toIndex);
  }

  public void set(int fromIndex, int toIndex, boolean value) {
    if (value) {
      set(fromIndex, toIndex);
    } else {
      clear(fromIndex, toIndex);
    }
  }

  public int size() {
    return array.length * 32;
  }

  @Override
  public String toString() {
    if (length() == 0) {
      return "{}";
    }

    StringBuilder sb = new StringBuilder("{");
    int next = nextSetBit(0);
    sb.append(next);

    while ((next = nextSetBit(next + 1)) != -1) {
      sb.append(", ");
      sb.append(next);
    }

    sb.append("}");
    return sb.toString();
  }

  public void xor(BitSet set) {
    // a ^ a is all false, so return an empty BitSet.
    if (this == set) {
      clear();
      return;
    }

    // Truth table
    //
    // Case | a     | b     | a ^ b | Change?
    // 1    | false | false | false | a is already false
    // 2    | false | true  | true  | set a to true
    // 3    | true  | false | true  | a is already true
    // 4    | true  | true  | false | set a to false
    //
    // We need to change something in cases 2 and 4. Cases 2 and 4 only happen when b is true,
    // so iterate over set b.
    int index = 0;
    while ((index = nextSetWord(set.array, index)) != -1) {
      array[index] = array[index] ^ set.array[index];
      index++;
    }
  }
}
