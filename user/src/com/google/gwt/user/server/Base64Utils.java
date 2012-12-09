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
package com.google.gwt.user.server;

/**
 * A utility to decode and encode byte arrays as Strings, using only "safe"
 * characters.
 */
@Deprecated
public class Base64Utils {

  /**
   * Decode a base64 string into a byte array.
   * 
   * @param data the encoded data.
   * @return a byte array.
   * @see #fromBase64(String)
   */
  public static byte[] fromBase64(String data) {
    return com.google.gwt.util.tools.shared.Base64Utils.fromBase64(data);
  }

  /**
   * Decode a base64 string into a long value.
   */
  public static long longFromBase64(String value) {
    return com.google.gwt.util.tools.shared.Base64Utils.longFromBase64(value);
  }

  /**
   * Converts a byte array into a base 64 encoded string. Null is encoded as
   * null, and an empty array is encoded as an empty string. Otherwise, the byte
   * data is read 3 bytes at a time, with bytes off the end of the array padded
   * with zeros. Each 24-bit chunk is encoded as 4 characters from the sequence
   * [A-Za-z0-9$_]. If one of the source positions consists entirely of padding
   * zeros, an '=' character is used instead.
   * 
   * @param data a byte array, which may be null or empty
   * @return a String
   */
  public static String toBase64(byte[] data) {
    return com.google.gwt.util.tools.shared.Base64Utils.toBase64(data);
  }

  /**
   * Return a string containing a base-64 encoded version of the given long
   * value.  Leading groups of all zero bits are omitted.
   */
  public static String toBase64(long value) {
    return com.google.gwt.util.tools.shared.Base64Utils.toBase64(value);
  }
}
