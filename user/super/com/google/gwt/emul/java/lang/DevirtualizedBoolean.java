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

import static javaemul.internal.InternalPreconditions.checkNotNull;

/**
 * Wraps native <code>boolean</code> as an object.
 */
class DevirtualizedBoolean {

  public static int compare(boolean x, boolean y) {
    return (x == y) ? 0 : (x ? 1 : -1);
  }

  public static int hashCode(boolean value) {
    // The Java API doc defines these magic numbers.
    return value ? 1231 : 1237;
  }

  public static boolean logicalAnd(boolean a, boolean b) {
    return a && b;
  }

  public static boolean logicalOr(boolean a, boolean b) {
    return a || b;
  }

  public static boolean logicalXor(boolean a, boolean b) {
    return a ^ b;
  }

  public static boolean parseBoolean(String s) {
    return "true".equalsIgnoreCase(s);
  }

  public static String toString(boolean x) {
    return String.valueOf(x);
  }

  public static Boolean valueOf(boolean b) {
    return $createBoolean(b);
  }

  public static Boolean valueOf(String s) {
    return valueOf(parseBoolean(s));
  }

  public static boolean booleanValue(Boolean instance) {
    return booleanValue0(checkNotNull(instance));
  }

  public static int compareTo(Boolean instance, Boolean other) {
    return compare(booleanValue(instance), booleanValue(other));
  }

  public static boolean equals(Boolean instance, Object o) {
    return equals0(booleanValue(instance), o);
  }

  public static int hashCode(Boolean instance) {
    return hashCode(booleanValue(instance));
  }

  public static String toString(Boolean instance) {
    return toString(booleanValue(instance));
  }

  // CHECKSTYLE_OFF: Utility Methods for unboxed Boolean.
  static native Boolean $createBoolean(boolean x) /*-{
    return x;
  }-*/;

  static Boolean $createBoolean(String x) {
    return $createBoolean(DevirtualizedBoolean.parseBoolean(x));
  }
  // CHECKSTYLE_ON: End utility methods

  private static native boolean booleanValue0(Boolean instance) /*-{
    return instance;
  }-*/;

  private static native boolean equals0(boolean instance, Object other) /*-{
    return instance === other;
  }-*/;

  private DevirtualizedBoolean() {
  }
}
