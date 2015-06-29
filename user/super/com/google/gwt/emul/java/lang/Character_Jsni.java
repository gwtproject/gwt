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
package java.lang;

/**
 * Contains Jsni parts of Character. 
 */
public final class Character_Jsni {
  
  /*
   * TODO: correct Unicode handling.
   */
  static native boolean isDigit(char c) /*-{
    return (null != String.fromCharCode(c).match(/\d/));
  }-*/;

  /*
   * TODO: correct Unicode handling.
   */
  static native boolean isLetter(char c) /*-{
    return (null != String.fromCharCode(c).match(/[A-Z]/i));
  }-*/;

  /*
   * TODO: correct Unicode handling.
   */
  static native boolean isLetterOrDigit(char c) /*-{
    return (null != String.fromCharCode(c).match(/[A-Z\d]/i));
  }-*/;

  // The regex would just be /\s/, but browsers handle non-breaking spaces inconsistently. Also,
  // the Java definition includes separators.
  static native boolean isWhitespace(int codePoint) /*-{
    return (null !== String.fromCharCode(codePoint).match(
      /[\t-\r \u1680\u180E\u2000-\u2006\u2008-\u200A\u2028\u2029\u205F\u3000\uFEFF]|[\x1C-\x1F]/
    ));
  }-*/;

  static native char toLowerCase(char c) /*-{
    return String.fromCharCode(c).toLowerCase().charCodeAt(0);
  }-*/;

  static native char toUpperCase(char c) /*-{
    return String.fromCharCode(c).toUpperCase().charCodeAt(0);
  }-*/;
}
