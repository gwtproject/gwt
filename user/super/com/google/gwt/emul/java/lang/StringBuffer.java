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

import com.google.gwt.core.client.impl.StringBufferImpl;

/**
 * A fast way to create strings using multiple appends. This is implemented
 * using a {@link StringBufferImpl} that is chosen with deferred binding.
 * 
 * Most methods will give expected performance results. Exceptions are
 * {@link #setCharAt(int, char)}, which is O(n), and {@link #length()}, which
 * forces a {@link #toString()} and thus should not be used many times on the
 * same <code>StringBuffer</code>.
 * 
 * This class uses StringBuilder for implementation.
 */
public class StringBuffer implements CharSequence, Appendable {
  private final StringBuilder builder;

  public StringBuffer() {
    builder = new StringBuilder();
  }

  public StringBuffer(CharSequence s) {
    builder = new StringBuilder(s);
  }

  /**
   * This implementation does not track capacity; using this constructor is
   * functionally equivalent to using the zero-argument constructor.
   */
  public StringBuffer(int capacity) {
    builder = new StringBuilder(capacity);
  }

  public StringBuffer(String s) {
    builder = new StringBuilder(s);
  }

  public StringBuffer append(boolean x) {
    builder.append(x);
    return this;
  }

  public StringBuffer append(char x) {
    builder.append(x);
    return this;
  }

  public StringBuffer append(char[] x) {
    builder.append(x);
    return this;
  }

  public StringBuffer append(char[] x, int start, int len) {
    builder.append(x, start, len);
    return this;
  }

  public StringBuffer append(CharSequence x) {
    builder.append(x);
    return this;
  }

  public StringBuffer append(CharSequence x, int start, int end) {
    builder.append(x, start, end);
    return this;
  }

  public StringBuffer append(double x) {
    builder.append(x);
    return this;
  }

  public StringBuffer append(float x) {
    builder.append(x);
    return this;
  }

  public StringBuffer append(int x) {
    builder.append(x);
    return this;
  }

  public StringBuffer append(long x) {
    builder.append(x);
    return this;
  }

  public StringBuffer append(Object x) {
    builder.append(x);
    return this;
  }

  public StringBuffer append(String x) {
    builder.append(x);
    return this;
  }

  public StringBuffer append(StringBuffer x) {
    builder.append(x);
    return this;
  }

  /**
   * This implementation does not track capacity; always returns
   * {@link Integer#MAX_VALUE}.
   */
  public int capacity() {
    return builder.capacity();
  }

  public char charAt(int index) {
    return builder.charAt(index);
  }

  public StringBuffer delete(int start, int end) {
    builder.delete(start, end);
    return this;
  }

  public StringBuffer deleteCharAt(int start) {
    builder.deleteCharAt(start);
    return this;
  }

  /**
   * This implementation does not track capacity; calling this method has no
   * effect.
   */
  public void ensureCapacity(int capacity) {
    builder.ensureCapacity(capacity);
  }

  public void getChars(int srcStart, int srcEnd, char[] dst, int dstStart) {
    builder.getChars(srcStart, srcEnd, dst, dstStart);
  }

  public int indexOf(String x) {
    return builder.indexOf(x);
  }

  public int indexOf(String x, int start) {
    return builder.indexOf(x, start);
  }

  public StringBuffer insert(int index, boolean x) {
    builder.insert(index, x);
    return this;
  }

  public StringBuffer insert(int index, char x) {
    builder.insert(index, x);
    return this;
  }

  public StringBuffer insert(int index, char[] x) {
    builder.insert(index, x);
    return this;
  }

  public StringBuffer insert(int index, char[] x, int offset, int len) {
    builder.insert(index, x, offset, len);
    return this;
  }

  public StringBuffer insert(int index, CharSequence chars) {
    builder.insert(index, chars);
    return this;
  }

  public StringBuffer insert(int index, CharSequence chars, int start, int end) {
    builder.insert(index, chars, start, end);
    return this;
  }

  public StringBuffer insert(int index, double x) {
    builder.insert(index, x);
    return this;
  }

  public StringBuffer insert(int index, float x) {
    builder.insert(index, x);
    return this;
  }

  public StringBuffer insert(int index, int x) {
    builder.insert(index, x);
    return this;
  }

  public StringBuffer insert(int index, long x) {
    builder.insert(index, x);
    return this;
  }

  public StringBuffer insert(int index, Object x) {
    builder.insert(index, x);
    return this;
  }

  public StringBuffer insert(int index, String x) {
    builder.insert(index, x);
    return this;
  }

  public int lastIndexOf(String s) {
    return builder.lastIndexOf(s);
  }

  public int lastIndexOf(String s, int start) {
    return builder.lastIndexOf(s, start);
  }

  public int length() {
    return builder.length();
  }

  public StringBuffer replace(int start, int end, String toInsert) {
    builder.replace(start, end, toInsert);
    return this;
  }

  public StringBuffer reverse() {
    builder.reverse();
    return this;
  }

  /**
   * Warning! This method is <b>much</b> slower than the JRE implementation. If
   * you need to do character level manipulation, you are strongly advised to
   * use a char[] directly.
   */
  public void setCharAt(int index, char x) {
    builder.setCharAt(index, x);
  }

  public void setLength(int newLength) {
    builder.setLength(newLength);
  }

  public CharSequence subSequence(int start, int end) {
    return builder.subSequence(start, end);
  }

  public String substring(int begin) {
    return builder.substring(begin);
  }

  public String substring(int begin, int end) {
    return builder.substring(begin, end);
  }

  @Override
  public String toString() {
    return builder.toString();
  }

  public void trimToSize() {
    builder.trimToSize();
  }
}
