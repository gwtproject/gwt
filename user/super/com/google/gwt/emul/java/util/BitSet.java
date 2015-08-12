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

import static javaemul.internal.Coercions.ensureInt;
import static javaemul.internal.InternalPreconditions.checkArraySize;
import static javaemul.internal.InternalPreconditions.checkPositionIndex;

import javaemul.internal.ArrayHelper;

/**
 * This implementation uses a dense array holding bit groups of size 31 to keep track of when bits
 * are set to true or false. Using 31 bits keeps our implementation within the range of V8's
 * "tagged small integer" and improves performance. Using a dense array also makes access faster on
 * V8.
 *
 * Not yet implemented:
 * public byte[] toByteArray()
 * public long[] toLongArray()
 * public static BitSet valueOf(ByteBuffer)
 * public static BitSet valueOf(LongBuffer)
 */
public class BitSet {
  private static final int WORD_MASK = 0x7fffffff;
  private static final int BITS_PER_WORD = 31;

  private final int[] array;

  public BitSet() {
    array = new int[10];
  }

  public BitSet(int nbits) {
    checkArraySize(nbits);
    array = new int[wordIndex(nbits - 1) + 1];
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
   * Converts from a bit index to a word index.
   *
   * @param bitIndex The bit index.
   * @return The index of the word (array entry) holding that bit.
   */
  private static int wordIndex(int bitIndex) {
    return bitIndex / BITS_PER_WORD;
  }

  /**
   * Converts from a word index to the lowest index of the bit stored in that word.
   *
   * @param wordIndex The word index.
   * @return The lowest index of the bit stored in that word.
   */
  private static int bitIndex(int wordIndex) {
    return wordIndex * BITS_PER_WORD;
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
    return bitIndex % BITS_PER_WORD;
  }

  /**
   * Sets all bits to true within the given range.
   *
   * @param fromIndex The lower bit index.
   * @param toIndex The upper bit index.
   */
  private void setInternal(int fromIndex, int toIndex) {
    int first = wordIndex(fromIndex);
    int last = wordIndex(toIndex);

    maybeGrowArrayTo(array, last + 1);

    int startBit = bitOffset(fromIndex);
    int endBit = bitOffset(toIndex);

    if (first == last) {
      // Set the bits in between first and last.
      maskInWord(array, first, startBit, endBit);
    } else {
      // Set the bits from fromIndex to the next 31 bit boundary.
      maskInWord(array, first, startBit, BITS_PER_WORD);

      // Set the bits from the last 31 bit boundary to toIndex.
      maskInWord(array, last, 0, endBit);

      // Set everything in between.
      for (int i = first + 1; i < last; i++) {
        array[i] = WORD_MASK;
      }
    }
  }

  private static void maybeGrowArrayTo(int[] array, int newLength) {
    if (newLength > array.length) {
      ArrayHelper.setLength(array, newLength);
    }
  }

  /**
   * Returns the index of the last word containing a true bit in an array, or -1 if none.
   *
   * @param array The array.
   * @return The index of the last word containing a true bit, or -1 if none.
   */
  private static int lastSetWordIndex(int[] array) {
    for (int i = array.length - 1; i >= 0; i--) {
      if (wordAt(array, i) != 0) {
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

    to = 32 - to;
    int word = wordAt(array, index);
    word ^= (((0xffffffff >>> from) << from) << to) >>> to;
    array[index] = word & WORD_MASK;
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
    int word = wordAt(array, index);
    word |= ((0xffffffff >>> from) << (from + to)) >>> to;
    array[index] = word & WORD_MASK;
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

    int word = wordAt(array, index);
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
      array[index] = word & WORD_MASK;
    }
  }

  private static int wordAt(int[] array, int index) {
    return ensureInt(array[index]);
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
    int lastIndex = lastSetWordIndex(set.array);
    int limit = Math.min(array.length, lastIndex + 1);
    int index = 0;
    for (; index < limit; index++) {
      int word = wordAt(array, index);
      if (word != 0) {
        array[index] =  word & wordAt(set.array, index);
      }
    }
    for (; index < array.length; index++) {
      array[index] = 0;
    }
  }

  public void andNot(BitSet set) {
    // a & !a is false, and all falses result in an empty BitSet.
    if (this == set) {
      clear();
      return;
    }

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
    int lastIndex = lastSetWordIndex(set.array);
    int limit = Math.min(array.length, lastIndex + 1);
    for (int index = 0; index < limit; index++) {
      int word = wordAt(set.array, index);
      if (word != 0) {
        array[index] = wordAt(array, index) & (~word & WORD_MASK);
      }
    }
  }

  public int cardinality() {
    int count = 0;
    for (int i = 0; i < array.length; i++) {
      count += Integer.bitCount(wordAt(array, i));
    }
    return count;
  }

  public void clear() {
    Arrays.fill(array, 0, array.length, 0);
  }

  public void clear(int bitIndex) {
    checkIndex(bitIndex);

    int index = wordIndex(bitIndex);
    if (index >= array.length) {
      return;
    }

    int word = wordAt(array, index);
    if (word != 0) {
      array[index] = word & ~(1 << bitOffset(bitIndex)) & WORD_MASK;
    }
  }

  public void clear(int fromIndex, int toIndex) {
    checkPositionIndex(fromIndex, toIndex);

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
      maskOutWord(array, first, startBit, BITS_PER_WORD);

      // Clear the bits from the last 31 bit boundary to the toIndex.
      maskOutWord(array, last, 0, endBit);

      // Clear everything in between.
      for (int i = first + 1; i < last; i++) {
        array[i] = 0;
      }
    }
  }

  public Object clone() {
    return new BitSet(Arrays.copyOf(array, array.length));
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof BitSet)) {
      return false;
    }

    BitSet other = (BitSet) obj;

    if (lastSetWordIndex(array) != lastSetWordIndex(other.array)) {
      return false;
    }

    for (int index = 0; index < array.length; index++) {
      if (wordAt(array, index) != wordAt(other.array, index)) {
        return false;
      }
    }
    return true;
  }

  public void flip(int bitIndex) {
    checkIndex(bitIndex);

    int index = wordIndex(bitIndex);
    int offset = bitOffset(bitIndex);

    maybeGrowArrayTo(array, index + 1);

    int word = wordAt(array, index);
    if (((word >>> offset) & 1) == 1) {
      word = word & ~(1 << offset);
    } else {
      word = (word | (1 << offset));
    }
    array[index] = word & WORD_MASK;
  }

  public void flip(int fromIndex, int toIndex) {
    checkPositionIndex(fromIndex, toIndex);

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
      flipMaskedWord(array, first, startBit, BITS_PER_WORD);

      // Flip the bits from the last 31 bit boundary to the toIndex.
      flipMaskedWord(array, last, 0, endBit);

      // Flip everything in between.
      for (int i = first + 1; i < last; i++) {
        array[i] = ~wordAt(array, i) & WORD_MASK;
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
    return ((wordAt(array, index) >>> bitOffset(bitIndex)) & 1) == 1;
  }

  public BitSet get(int fromIndex, int toIndex) {
    checkPositionIndex(fromIndex, toIndex);

    int length = length();
    if (length <= fromIndex || fromIndex == toIndex) {
      return new BitSet(0);
    }

    toIndex = Math.min(toIndex, length);

    // The bit shift offset for each group of bits
    int rightShift = bitOffset(fromIndex);

    if (rightShift == 0) {
      int subFrom = wordIndex(fromIndex);
      int subTo = wordIndex(toIndex + BITS_PER_WORD);
      int[] subSet = Arrays.copyOfRange(array, subFrom, subTo);
      int leftOvers = bitOffset(toIndex);
      maskOutWord(subSet, subTo - subFrom - 1, leftOvers, BITS_PER_WORD);
      return new BitSet(subSet);
    }

    BitSet subSet = new BitSet(toIndex - fromIndex);

    int first = wordIndex(fromIndex);
    int last = wordIndex(toIndex);

    if (first == last) {
      // Number of bits to cut from the end
      int end = 32 - bitOffset(toIndex);
      int word = wordAt(array, first);
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
      int word = wordAt(array, first);
      current = word >>> rightShift;

      // A left shift will be used to shift our bits to the top of "current".
      int leftShift = BITS_PER_WORD - rightShift;

      // Loop through everything in the middle.
      for (int i = first + 1; i <= last; i++) {
        word = wordAt(array, i);

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
      int value = wordAt(array, i);
      // Hash one byte at a time using FNV1 and make sure we don't overflow 32 bit int.
      hash = ensureInt(hash * fnvPrime) ^ (value & 0xff);
      hash = ensureInt(hash * fnvPrime) ^ ((value >>> 8) & 0xff);
      hash = ensureInt(hash * fnvPrime) ^ ((value >>> 16) & 0xff);
      hash = ensureInt(hash * fnvPrime) ^ (value >>> 24);
    }

    return hash;
  }

  public boolean intersects(BitSet set) {
    if (this == set) {
      // If it has any set bits then it intersects itself.
      return lastSetWordIndex(array) != -1;
    }

    int limit = Math.min(array.length, set.array.length);
    for (int index = 0; index < limit; index++) {
      int word = wordAt(array, index);
      if (word != 0 && (word & wordAt(set.array, index)) != 0) {
        return true;
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
    int word = wordAt(array, last);
    return bitIndex(last) + (32 - Integer.numberOfLeadingZeros(word));
  }

  public int nextClearBit(int fromIndex) {
    checkIndex(fromIndex);

    int index = wordIndex(fromIndex);
    if (index >= array.length) {
      return fromIndex;
    }
    // Special case for the first word.
    int word = ~wordAt(array, index) & WORD_MASK;
    word &= (WORD_MASK << bitOffset(fromIndex));
    while (word == 0) {
      if (++index == array.length) {
        return bitIndex(index);
      }
      word = ~wordAt(array, index) & WORD_MASK;
    }
    return bitIndex(index) + Integer.numberOfTrailingZeros(word);
  }

  public int nextSetBit(int fromIndex) {
    checkIndex(fromIndex);

    int index = wordIndex(fromIndex);
    if (index >= array.length) {
      return -1;
    }
    // Special case for the first word.
    int word = wordAt(array, index) & (WORD_MASK << bitOffset(fromIndex));
    while (word == 0) {
      if (++index >= array.length) {
        return -1;
      }
      word = wordAt(array, index);
    }
    return bitIndex(index) + Integer.numberOfTrailingZeros(word);
  }

  public int previousClearBit(int fromIndex) {
    if (fromIndex == -1) {
      return -1;
    }
    checkIndex(fromIndex);

    int index = wordIndex(fromIndex);
    if (index >= array.length) {
      return fromIndex;
    }
    // Special case for the first word.
    int word = ~wordAt(array, index) & WORD_MASK;
    word &= (WORD_MASK >>> (BITS_PER_WORD - bitOffset(fromIndex) - 1));
    while (word == 0) {
      if (--index < 0) {
        return -1;
      }
      word = ~wordAt(array, index) & WORD_MASK;
    }
    return bitIndex(index) + (32 - Integer.numberOfLeadingZeros(word)) - 1;
  }

  public int previousSetBit(int fromIndex) {
    if (fromIndex == -1) {
      return -1;
    }
    checkIndex(fromIndex);

    int index = wordIndex(fromIndex);
    if (index >= array.length) {
      return -1;
    }
    // Special case for the first word.
    int word = wordAt(array, index) & (WORD_MASK >>> (BITS_PER_WORD - bitOffset(fromIndex) - 1));
    while (word == 0) {
      if (--index < 0) {
        return -1;
      }
      word = wordAt(array, index);
    }
    return bitIndex(index) + (32 - Integer.numberOfLeadingZeros(word)) - 1;
  }

  public void or(BitSet set) {
    // a | a is just a.
    if (this == set) {
      return;
    }

    int lastWordIndex = lastSetWordIndex(set.array);
    maybeGrowArrayTo(array, lastWordIndex + 1);

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
    for (int index = 0; index <= lastWordIndex; index++) {
      int word = wordAt(set.array, index);
      if (word != 0) {
        array[index] = wordAt(array, index) | word;
      }
    }
  }

  public void set(int bitIndex) {
    checkIndex(bitIndex);
    int index = wordIndex(bitIndex);
    maybeGrowArrayTo(array, index + 1);
    array[index] = wordAt(array, index) | (1 << bitOffset(bitIndex));
  }

  public void set(int bitIndex, boolean value) {
    if (value) {
      set(bitIndex);
    } else {
      clear(bitIndex);
    }
  }

  public void set(int fromIndex, int toIndex) {
    checkPositionIndex(fromIndex, toIndex);
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
    if (isEmpty()) {
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

    int lastWordIndex = lastSetWordIndex(set.array);
    maybeGrowArrayTo(array, lastWordIndex + 1);

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
    for (int index = 0; index <= lastWordIndex; index++) {
      int word = wordAt(set.array, index);
      if (word != 0) {
        array[index] = wordAt(array, index) ^ word;
      }
    }
  }

  public static BitSet valueOf(byte[] words) {
    int len = words.length;
    while (len > 0 && words[len - 1] == 0) {
      len--;
    }
    BitSet bitSet = new BitSet(len * Byte.SIZE);
    int bitIndex = 0;
    for (int i = 0; i < len; i++) {
      addByte(bitSet.array, words[i], bitIndex);
      bitIndex += Byte.SIZE;
    }
    return bitSet;
  }

  public static BitSet valueOf(long[] words) {
    int len = words.length;
    while (len > 0 && words[len - 1] == 0) {
      len--;
    }
    BitSet bitSet = new BitSet(len * Long.SIZE);
    int bitIndex = 0;
    for (int i = 0; i < len; i++) {
      addLong(bitSet.array, words[i], bitIndex);
      bitIndex += Long.SIZE;
    }
    return bitSet;
  }

  private static void addByte(int[] words, byte bits, int bitIndex) {
    if (bits != 0) {
      int wordIndex = wordIndex(bitIndex);
      int bitOffset = bitOffset(bitIndex);
      int first = ((bits & 0xff) << bitOffset) & WORD_MASK;
      if (first != 0) {
        words[wordIndex] = wordAt(words, wordIndex) | first;
      }
      int second = (bits & 0xff) >>> (BITS_PER_WORD - bitOffset);
      if (second != 0) {
        words[wordIndex + 1] = wordAt(words, wordIndex + 1) | second;
      }
    }
  }

  private static void addInt(int[] words, int bits, int bitIndex) {
    if (bits != 0) {
      addByte(words, (byte) (bits & 0xff), bitIndex);
      addByte(words, (byte) ((bits >> 8) & 0xff), bitIndex + Byte.SIZE);
      addByte(words, (byte) ((bits >> 16) & 0xff), bitIndex + 2 * Byte.SIZE);
      addByte(words, (byte) ((bits >> 24) & 0xff), bitIndex + 3 * Byte.SIZE);
    }
  }

  private static void addLong(int[] words, long bits, int bitIndex) {
    if (bits != 0) {
      int low = (int) bits;
      addInt(words, low, bitIndex);
      int high = (int) (bits >>> 32);
      addInt(words, high, bitIndex + Integer.SIZE);
    }
  }
}
