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
package com.google.gwt.emultest.java.lang;

import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Tests for java.lang.Character.
 */
@DoNotRunWith(Platform.HtmlUnitBug)
public class CharacterTest extends GWTTestCase {

  private static final char NUM_CHARS_HANDLED = 127;

  private static class CharSequenceAdapter implements CharSequence {
    private char[] charArray;
    private int start;
    private int end;

    CharSequenceAdapter(char[] charArray) {
      this(charArray, 0, charArray.length);
    }
    
    CharSequenceAdapter(char[] charArray, int start, int end) {
      this.charArray = charArray;
      this.start = start;
      this.end = end;
    }
    
    @Override
    public char charAt(int index) {
      return charArray[index + start];
    }

    @Override
    public int length() {
      return end - start;
    }

    @Override
    public java.lang.CharSequence subSequence(int start, int end) {
      return new CharSequenceAdapter(charArray, this.start + start,
          this.start + end);
    }
  }

  /**
   * Helper method which counts ASCII characters matching a predicate.
   */
  public static int countAscii(Predicate<Character> test) {
    int count = 0;
    for (char ch = 0; ch < NUM_CHARS_HANDLED; ch++) {
      if (test.test(ch)) {
        count++;
      }
    }
    return count;
  }

  public static int countAscii(Function<Character, Character> transformer,
                               Predicate<Character> test) {
    int count = 0;
    for (char ch = 0; ch < NUM_CHARS_HANDLED; ch++) {
      if (test.test(transformer.apply(ch))) {
        count++;
      }
    }
    return count;
  }

  public static int countUnicode(int[] codePoints, Predicate<Integer> test) {
    int c = 0;
    for (int i: codePoints) {
      if (test.test(i)) {
        c++;
      }
    }
    return c;
  }

  int[] letters = {'a', 'z', 'A', 'Z', 0x2c6, 0x2d1, 0x10380, 0x1039d};
  int[] digits = {'0', '9', 0x660, 0x669, 0x104a0, 0x104a9};
  int[] spaces = {' ', '\u00a0', '\u2028'};
  int[] controls = {0, 9, 0xa, 0xb, 0xc, 0xd, 0xe, 0x1f, 0x7f, 0x9f};
  int[] punctuation = {'@', '.'};
  int[] symbols = {0x2c5};
  int[] marks = {0x659, 0x10a39, 0x10379};
  int[] others = {-1, Character.MAX_CODE_POINT + 1};
  int[] allCodePoints = Stream.of(letters, digits, spaces, controls, punctuation, marks,
          symbols, others).flatMapToInt(Arrays::stream).toArray();

  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  public void testCharValue() {
    assertEquals((char) 32, new Character((char) 32).charValue());
  }

  public void testCodePoint() {
    assertEquals(1, Character.charCount(65));
    assertEquals(2, Character.charCount(Character.MIN_SUPPLEMENTARY_CODE_POINT));
    char[] testPlain = new char[] { 'C', 'A', 'T' };
    char[] testUnicode = new char[] { 'C', '\uD801', '\uDF00', 'T' };
    CharSequence plainSequence = new CharSequenceAdapter(testPlain);
    CharSequence unicodeSequence = new CharSequenceAdapter(testUnicode);
    assertEquals(65, Character.codePointAt(testPlain, 1));
    assertEquals(65, Character.codePointAt(plainSequence, 1));
    assertEquals("codePointAt fails on surrogate pair", 67328,
        Character.codePointAt(testUnicode, 1));
    assertEquals("codePointAt fails on surrogate pair", 67328,
        Character.codePointAt(unicodeSequence, 1));
    assertEquals("codePointAt fails on first char of surrogate pair", 0xD801,
        Character.codePointAt(testUnicode, 1, 2));
    assertEquals(65, Character.codePointBefore(testPlain, 2));
    assertEquals(65, Character.codePointBefore(plainSequence, 2));
    assertEquals("codePointBefore fails on surrogate pair", 67328,
        Character.codePointBefore(testUnicode, 3));
    assertEquals("codePointBefore fails on surrogate pair", 67328,
        Character.codePointBefore(unicodeSequence, 3));
    assertEquals("codePointBefore fails on second char of surrogate pair",
        0xDF00, Character.codePointBefore(testUnicode, 3, 2));
    assertEquals("codePointCount(plain): ", 3,
        Character.codePointCount(testPlain, 0, 3));
    assertEquals("codePointCount(plain): ", 3,
        Character.codePointCount(plainSequence, 0, 3));
    assertEquals("codePointCount(unicode): ", 3,
        Character.codePointCount(testUnicode, 0, 4));
    assertEquals("codePointCount(unicode): ", 3,
        Character.codePointCount(unicodeSequence, 0, 4));
    assertEquals(1, Character.codePointCount(testPlain, 1, 1));
    assertEquals(1, Character.codePointCount(plainSequence, 1, 2));
    assertEquals(1, Character.codePointCount(testUnicode, 1, 2));
    assertEquals(1, Character.codePointCount(unicodeSequence, 1, 3));
    assertEquals(2, Character.codePointCount(testUnicode, 2, 2));
    assertEquals(2, Character.codePointCount(unicodeSequence, 2, 4));
    assertEquals(1, Character.offsetByCodePoints(testUnicode, 0, 4, 0, 1));
    assertEquals(1, Character.offsetByCodePoints(unicodeSequence, 0, 1));
    assertEquals("offsetByCodePoints(1,1): ", 3,
        Character.offsetByCodePoints(testUnicode, 0, 4, 1, 1));
    assertEquals("offsetByCodePoints(1,1): ", 3,
        Character.offsetByCodePoints(unicodeSequence, 1, 1));
    assertEquals("offsetByCodePoints(2,1): ", 3,
        Character.offsetByCodePoints(testUnicode, 0, 4, 2, 1));
    assertEquals("offsetByCodePoints(2,1): ", 3,
        Character.offsetByCodePoints(unicodeSequence, 2, 1));
    assertEquals(4, Character.offsetByCodePoints(testUnicode, 0, 4, 3, 1));
    assertEquals(4, Character.offsetByCodePoints(unicodeSequence, 3, 1));
    assertEquals(1, Character.offsetByCodePoints(testUnicode, 0, 4, 2, -1));
    assertEquals(1, Character.offsetByCodePoints(unicodeSequence, 2, -1));
    assertEquals(1, Character.offsetByCodePoints(testUnicode, 0, 4, 3, -1));
    assertEquals(1, Character.offsetByCodePoints(unicodeSequence, 3, -1));
    assertEquals("offsetByCodePoints(4.-1): ", 3,
        Character.offsetByCodePoints(testUnicode, 0, 4, 4, -1));
    assertEquals("offsetByCodePoints(4.-1): ", 3,
        Character.offsetByCodePoints(unicodeSequence, 4, -1));
    assertEquals(0, Character.offsetByCodePoints(testUnicode, 0, 4, 3, -2));
    assertEquals(0, Character.offsetByCodePoints(unicodeSequence, 3, -2));
    char[] nonBmpChar = new char[] { '\uD800', '\uDF46' };
    assertEquals(0x10346, Character.codePointAt(nonBmpChar, 0));
    assertEquals(1, Character.codePointCount(nonBmpChar, 0, 2));
  }

  public void testCompare() {
    assertTrue(Character.compare('A', 'B') < 0);
    assertTrue(Character.compare('B', 'A') > 0);
    assertEquals(0, Character.compare('C', 'C'));
    assertTrue(Character.compare('\uA001', '\uA000') > 0);
  }

  public void testCompareTo() {
    assertTrue(Character.valueOf('A').compareTo('B') < 0);
    assertTrue(Character.valueOf('B').compareTo('A') > 0);
    assertTrue(Character.valueOf('C').compareTo('C') == 0);
    assertTrue(Character.valueOf('\uA001').compareTo('\uA000') > 0);
  }

  public void testConstructor() {
    assertEquals(new Character((char) 32), new Character(' '));
  }

  public void testDigit() {
    assertEquals("wrong number of digits", 10, countAscii(Character::isDigit));
  }
  
  public void testSurrogates() {
    assertFalse(Character.isHighSurrogate('\uDF46'));
    assertTrue(Character.isLowSurrogate('\uDF46'));
    assertTrue(Character.isHighSurrogate('\uD800'));
    assertFalse(Character.isLowSurrogate('\uD800'));
    assertFalse(Character.isHighSurrogate('X'));
    assertFalse(Character.isLowSurrogate('X'));
    assertTrue(Character.isSurrogatePair('\uD800', '\uDF46'));
    assertFalse(Character.isSurrogatePair('\uDF46', '\uD800'));
    assertFalse(Character.isSurrogatePair('A', '\uDF46'));
    assertFalse(Character.isSurrogatePair('\uD800', 'A'));
    char[] chars = Character.toChars(0x10346);
    assertEquals(0xD800, chars[0]);
    assertEquals(0xDF46, chars[1]);
    assertEquals(2, Character.toChars(67328, chars, 0));
    assertEquals(0xD801, chars[0]);
    assertEquals(0xDF00, chars[1]);
    assertEquals(1, Character.toChars(65, chars, 0));
    assertEquals('A', chars[0]);
    assertTrue(Character.isSupplementaryCodePoint(0x10346));
    assertFalse(Character.isSupplementaryCodePoint(65));
    assertTrue(Character.isValidCodePoint(0x10346));
    assertTrue(Character.isValidCodePoint(65));
    assertFalse(Character.isValidCodePoint(0x1FFFFFFF));
    assertEquals(0x10346, Character.toCodePoint('\uD800', '\uDF46'));
  }

  public void testLetter() {
    assertEquals("wrong number of letters", 52, countAscii(Character::isLetter));
  }

  public void testLetterOrDigit() {
    assertEquals("wrong number of letters + digits", 62,
        countAscii(Character::isLetterOrDigit));
  }

  public void testLowerCase() {
    assertEquals("wrong number of lowercase letters", 26,
        countAscii(Character::isLowerCase));
    assertEquals("wrong number of lowercase letters after toLowerCase", 52,
        countAscii(Character::toLowerCase, Character::isLowerCase));
  }

  @SuppressWarnings("deprecation")
  public void testSpace() {
    assertEquals("wrong number of spaces", 5, countAscii(Character::isSpace));
  }

  public void testToFromDigit() {
    for (int i = 0; i < 16; i++) {
      assertEquals(i, Character.digit(Character.forDigit(i, 16), 16));
    }
    assertEquals(1, Character.digit(hideFromCompiler('1'), 10));
    assertEquals('9', Character.forDigit(hideFromCompiler(9), 10));
    assertEquals(-1, Character.digit(hideFromCompiler('7'), 6));
    assertEquals(-1, Character.digit(hideFromCompiler('8'), 8));
    assertEquals(-1, Character.digit(hideFromCompiler('A'), 10));
    assertEquals(35, Character.digit(hideFromCompiler('Z'), 36));
  }

  public void testToFromDigitInt() {
    int[] zeros = {48, 1632, 1776, 1984, 2406, 2534, 2662, 2790,
        2918, 3046, 3174, 3302, 3430, 3558, 3664, 3792, 3872, 4160, 4240, 6112, 6160, 6470, 6608,
        6784, 6800, 6992, 7088, 7232, 7248, 42528, 43216, 43264, 43472, 43504, 43600, 44016,
        65296, 66720, 69734, 69872, 69942, 70096, 70384, 70736, 70864, 71248, 71360, 71472,
        71904, 72784, 73040, 92768, 93008, 120782, 120792, 120802, 120812, 120822, 125264};

    for (int zero: zeros) {
      assertEquals(0, Character.digit(zero, 10));
    }
    assertEquals(35, Character.digit(hideFromCompiler(65338), 36));
    assertEquals(35, Character.digit(hideFromCompiler(65370), 36));
    assertEquals("only letters and digits have numeric value", 0,
        countUnicode(punctuation, c -> Character.digit(c, 10) != -1));
  }

  @SuppressWarnings("deprecation")
  public void testIsSpace() {
    assertFalse(Character.isSpace('a'));
    assertFalse(Character.isSpace('_'));

    assertTrue(Character.isSpace(' '));
    assertTrue(Character.isSpace('\n'));
  }

  public void testIsWhitepace() {
    char[] separators = {
        '\u0020', // SPACE.
        '\u1680', // OGHAM SPACE MARK.
        '\u2000', // EN QUAD.
        '\u2001', // EM QUAD.
        '\u2002', // EN SPACE.
        '\u2003', // EM SPACE.
        '\u2004', // THREE-PER-EM SPACE.
        '\u2005', // FOUR-PER-EM SPACE.
        '\u2006', // SIX-PER-EM SPACE.
        '\u2008', // PUNCTUATION SPACE.
        '\u2009', // THIN SPACE.
        '\u200A', // HAIR SPACE.
        '\u2028', // LINE SEPARATOR.
        '\u2029', // PARAGRAPH SEPARATOR.
        '\u205F', // MEDIUM MATHEMATICAL SPACE.
        '\u3000' // IDEOGRAPHIC SPACE.
    };

    char[] nonBreakingSpaceSeparators = {
        '\u00A0', // NO-BREAK SPACE.
        '\u2007', // FIGURE SPACE.
        '\u202F', // NARROW NO-BREAK SPACE.
        '\uFFEF'  // ZERO WIDTH NO-BREAK SPACE.
    };

    char[] specialCases = {
      '\t', // HORIZONTAL TABULATION.
      '\n', // LINE FEED.
      '\u000B', // VERTICAL TABULATION.
      '\f', // FORM FEED.
      '\r', // CARRIAGE RETURN.
      '\u001C', // FILE SEPARATOR.
      '\u001D', // GROUP SEPARATOR.
      '\u001E', // RECORD SEPARATOR.
      '\u001F' // UNIT SEPARATOR.
    };

    char[] typicalCounterExamples = {
        'a', // LATIN SMALL LETTER A.
        'B', // LATIN CAPITAL LETTER B.
        '_', // LOW LINE.
        '\u2500', // BOX DRAWINGS LIGHT HORIZONTAL.
        '\u180E', // MONGOLIAN VOWEL SEPARATOR, was considered whitespace in Java 8.
    };

    int[] supplementaryCounterExamples = {
        0x2070E, // UNICODE HAN CHARACTER 'to castrate a fowl, a capon'.
        0x20731, // UNICODE HAN CHARACTER 'to peel, pare'.
        0x29D98, // UNICODE HAN CHARACTER 'a general name for perch, etc.'.
    };

    // Must match unicode space separator characters.
    for (char c : separators) {
      assertTrue(Character.isWhitespace(c));
      assertTrue(Character.isWhitespace((int) c));
    }

    // But NOT the non-breaking spaces.
    for (char c : nonBreakingSpaceSeparators) {
      assertFalse(Character.isWhitespace(c));
      assertFalse(Character.isWhitespace((int) c));
    }

    // The ASCII legacy cases.
    for (char c : specialCases) {
      assertTrue(Character.isWhitespace(c));
      assertTrue(Character.isWhitespace((int) c));
    }

    // Behave appropriately on other characters, like the alphabet.
    for (char c : typicalCounterExamples) {
      assertFalse(Character.isWhitespace(c));
      assertFalse(Character.isWhitespace((int) c));
    }

    // Support for non-UCS-2 characters.
    for (int c : supplementaryCounterExamples) {
      assertFalse(Character.isWhitespace(c));
    }
  }

  public void test_isTitleCaseC() {
    char[] expectedTitleCaseChars = {
      (char) 0x01c5,
      (char) 0x01c8,
      (char) 0x01cb,
      (char) 0x01f2,
      (char) 0x1f88,
      (char) 0x1f89,
      (char) 0x1f8a,
      (char) 0x1f8b,
      (char) 0x1f8c,
      (char) 0x1f8d,
      (char) 0x1f8e,
      (char) 0x1f8f,
      (char) 0x1f98,
      (char) 0x1f99,
      (char) 0x1f9a,
      (char) 0x1f9b,
      (char) 0x1f9c,
      (char) 0x1f9d,
      (char) 0x1f9e,
      (char) 0x1f9f,
      (char) 0x1fa8,
      (char) 0x1fa9,
      (char) 0x1faa,
      (char) 0x1fab,
      (char) 0x1fac,
      (char) 0x1fad,
      (char) 0x1fae,
      (char) 0x1faf,
      (char) 0x1fbc,
      (char) 0x1fcc,
      (char) 0x1ffc
    };

    char[] foundChars = new char[expectedTitleCaseChars.length];
    int lastFoundCharIndex = 0;
    for (char c = 0; c < 65535; c++) {
      if (Character.isTitleCase(c)) {
        foundChars[lastFoundCharIndex++] = c;
      }
    }

    assertTrue(Arrays.equals(expectedTitleCaseChars, foundChars));
  }

  public void testToString() {
    assertEquals(" ", new Character((char) 32).toString());
  }

  public void testUpperCase() {
    assertEquals("wrong number of uppercase letters", 26,
        countAscii(Character::isUpperCase));
    assertEquals("wrong number of uppercase letters after toUpperCase", 52,
        countAscii(Character::toUpperCase, Character::isUpperCase));
  }
  
  public void testValueOf() {
    assertEquals('A', Character.valueOf('A').charValue());
  }

  public void testIsLetterInt() {
    assertEquals("No other characters should be letters",
        letters.length, countUnicode(allCodePoints, Character::isLetter));
    assertEquals("Unicode letters should be recognized",
        letters.length, countUnicode(letters, Character::isLetter));
  }

  public void testIsDigitInt() {
    assertEquals("Unicode digits should be recognized",
        digits.length, countUnicode(digits, Character::isDigit));
    assertEquals("No other characters should be digits",
        digits.length, countUnicode(allCodePoints, Character::isDigit));
  }

  public void testIsDigitOrLetterInt() {
    assertEquals("Unicode digits should be recognized",
        digits.length, countUnicode(digits, Character::isLetterOrDigit));
    assertEquals("Unicode letters should be recognized",
        digits.length, countUnicode(digits, Character::isLetterOrDigit));
    assertEquals("No other characters should match letter or digit",
        digits.length + letters.length,
        countUnicode(allCodePoints, Character::isLetterOrDigit));
  }

  public void testIsSpaceCharInt() {
    assertEquals("Unicode spaces should be recognized",
        spaces.length, countUnicode(spaces, Character::isSpaceChar));
    assertEquals("No other characters should match space",
        spaces.length,
        countUnicode(allCodePoints, Character::isSpaceChar));
  }

  public void testIsDefined() {
    assertEquals("Should recognize defined characters",
        allCodePoints.length - others.length,
        countUnicode(allCodePoints, Character::isDefined));
    assertEquals("No other characters should be defined",
        0,
        countUnicode(others, Character::isDefined));
  }

  public void testIsISOControl() {
    assertTrue(Character.isISOControl(hideFromCompiler((char) 0)));
    assertTrue(Character.isISOControl(hideFromCompiler((char) 0x1f)));
    assertFalse(Character.isISOControl(hideFromCompiler((char) 0x20)));
    assertFalse(Character.isISOControl(hideFromCompiler((char) 0x7E)));
    assertTrue(Character.isISOControl(hideFromCompiler((char) 0x7F)));
    assertTrue(Character.isISOControl(hideFromCompiler((char) 0x9F)));
    assertFalse(Character.isISOControl(hideFromCompiler((char) 0xA0)));
  }

  public void testIsISOControlInt() {
    assertTrue(Character.isISOControl(hideFromCompiler(0)));
    assertTrue(Character.isISOControl(hideFromCompiler(0x1f)));
    assertFalse(Character.isISOControl(hideFromCompiler(0x20)));
    assertFalse(Character.isISOControl(hideFromCompiler(0x7E)));
    assertTrue(Character.isISOControl(hideFromCompiler(0x7F)));
    assertTrue(Character.isISOControl(hideFromCompiler(0x9F)));
    assertFalse(Character.isISOControl(hideFromCompiler(0xA0)));
  }

  public void testIsSurrogate() {
    assertFalse(Character.isSurrogate(hideFromCompiler((char) 0)));
    assertTrue(Character.isSurrogate(hideFromCompiler(Character.MIN_HIGH_SURROGATE)));
    assertTrue(Character.isSurrogate(hideFromCompiler(Character.MAX_HIGH_SURROGATE)));
    assertTrue(Character.isSurrogate(hideFromCompiler(Character.MIN_LOW_SURROGATE)));
    assertTrue(Character.isSurrogate(hideFromCompiler(Character.MAX_LOW_SURROGATE)));
    assertFalse(Character.isSurrogate(hideFromCompiler((char) (Character.MAX_LOW_SURROGATE + 1))));
  }

  protected <T> T hideFromCompiler(T value) {
    if (Math.random() < -1) {
      // Can never happen, but fools the compiler enough not to optimize this call.
      fail();
    }
    return value;
  }
}
