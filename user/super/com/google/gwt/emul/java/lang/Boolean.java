/*
 * Copyright 2007 Google Inc.
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

import java.io.Serializable;

/**
 * Wraps native <code>boolean</code> as an object.
 */
public final class Boolean implements Comparable<Boolean>, Serializable {
  /*
   * TODO: figure out how to not clinit this class on direct field access.
   */

  // CHECKSTYLE_OFF: These have to be created somewhere.
  public static final Boolean FALSE = DevirtualizedBoolean.$createBoolean(false);
  public static final Boolean TRUE = DevirtualizedBoolean.$createBoolean(true);

  // CHECKSTYLE_ON

  public static final Class<Boolean> TYPE = boolean.class;

  public static int compare(boolean x, boolean y) {
    return (x == y) ? 0 : (x ? 1 : -1);
  }

  public static int hashCode(boolean value) {
    return DevirtualizedBoolean.hashCode(value);
  }

  public static boolean logicalAnd(boolean a, boolean b) {
    return DevirtualizedBoolean.logicalAnd(a, b);
  }

  public static boolean logicalOr(boolean a, boolean b) {
    return DevirtualizedBoolean.logicalOr(a, b);
  }

  public static boolean logicalXor(boolean a, boolean b) {
    return DevirtualizedBoolean.logicalXor(a, b);
  }

  public static boolean parseBoolean(String s) {
    return DevirtualizedBoolean.parseBoolean(s);
  }

  public static String toString(boolean x) {
    return DevirtualizedBoolean.toString(x);
  }

  public static Boolean valueOf(boolean b) {
    return DevirtualizedBoolean.valueOf(b);
  }

  public static Boolean valueOf(String s) {
    return DevirtualizedBoolean.valueOf(s);
  }

  public Boolean(boolean value) {
    /*
     * Call to $createBoolean(value) must be here so that the method is referenced and not pruned
     * before new Boolean(value) is replaced by $createBoolean(value) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createBoolean(value);
  }

  public Boolean(String s) {
     /*
     * Call to $createBoolean(value) must be here so that the method is referenced and not pruned
     * before new Boolean(value) is replaced by $createBoolean(value) by
     * RewriteConstructorCallsForUnboxedTypes.
     */
    $createBoolean(s);
  }

  public boolean booleanValue() {
    return DevirtualizedBoolean.booleanValue(this);
  }

  @Override
  public int compareTo(Boolean b) {
    return DevirtualizedBoolean.compareTo(this, b);
  }

  @Override
  public boolean equals(Object o) {
    return DevirtualizedBoolean.equals(this, o);
  }

  @Override
  public int hashCode() {
    return DevirtualizedBoolean.hashCode(this);
  }

  @Override
  public String toString() {
    return DevirtualizedBoolean.toString(this);
  }

  // CHECKSTYLE_OFF: Utility Methods for unboxed Boolean.
  static Boolean $createBoolean(boolean x) {
    return DevirtualizedBoolean.$createBoolean(x);
  }

  static Boolean $createBoolean(String x) {
    return DevirtualizedBoolean.$createBoolean(x);
  }
  // CHECKSTYLE_ON: End utility methods
}
