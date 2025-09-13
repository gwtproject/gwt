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
package java.lang;

/** Performs case operations. Abstracted so can be platform specific. */
final class CaseMapper {

  public static char charToLowerCase(char c) {
    return String.valueOf(c).toLowerCase().charAt(0);
  }

  public static char charToUpperCase(char c) {
    String upper = String.valueOf(c).toUpperCase();
    return hasExtraCodePoints(upper) ? c : upper.charAt(0);
  }

  public static int intToLowerCase(int codePoint) {
    return String.NativeString.fromCodePoint(codePoint).toLowerCase().codePointAt(0);
  }

  public static int intToUpperCase(int codePoint) {
    String upper = String.NativeString.fromCodePoint(codePoint).toUpperCase();
    return hasExtraCodePoints(upper) ? codePoint : upper.codePointAt(0);
  }

  // If String.toUpperCase produces more than 1 codepoint, Character.toUpperCase should
  // act either as identity or title-case conversion (not supported in GWT).
  private static boolean hasExtraCodePoints(String str) {
    return str.asNativeString().codePointAt(1) > 0;
  }

  private CaseMapper() {}
}
