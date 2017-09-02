/*
 * Copyright 2017 Google Inc.
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
package java.lang.reflect;

import static javaemul.internal.InternalPreconditions.checkArgument;
import static javaemul.internal.InternalPreconditions.checkElementIndex;
import static javaemul.internal.InternalPreconditions.checkNotNull;

import javaemul.internal.JsUtils;

/**
 * See <a
 * href="http://java.sun.com/javase/6/docs/api/java/lang/reflect/Array.html">the
 * official Java API doc</a> for details.
 */
public final class Array {

  public static Object get(Object array, int index) {
    checkNotNull(array);

    if (array instanceof boolean[]) {
      return getBoolean(array, index);
    } else if (array instanceof byte[]) {
      return getByte(array, index);
    } else if (array instanceof char[]) {
      return getChar(array, index);
    } else if (array instanceof double[]) {
      return getDouble(array, index);
    } else if (array instanceof float[]) {
      return getFloat(array, index);
    } else if (array instanceof int[]) {
      return getInt(array, index);
    } else if (array instanceof long[]) {
      return getLong(array, index);
    } else if (array instanceof short[]) {
      return getShort(array, index);
    } else  {
      checkArgument(array instanceof Object[]);
      Object[] typedArray = (Object[]) array;
      checkElementIndex(index, typedArray.length);
      return typedArray[index];
    }
  }

  public static boolean getBoolean(Object array, int index) {
    checkNotNull(array);
    checkArgument(array instanceof boolean[]);

    boolean[] typedArray = (boolean[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static byte getByte(Object array, int index) {
    checkNotNull(array);
    checkArgument(array instanceof byte[]);

    byte[] typedArray = (byte[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static char getChar(Object array, int index) {
    checkNotNull(array);
    checkArgument(array instanceof char[]);

    char[] typedArray = (char[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static double getDouble(Object array, int index) {
    checkNotNull(array);
    checkArgument(array instanceof double[]);

    double[] typedArray = (double[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static float getFloat(Object array, int index) {
    checkNotNull(array);
    checkArgument(array instanceof float[]);

    float[] typedArray = (float[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static int getInt(Object array, int index) {
    checkNotNull(array);
    checkArgument(array instanceof int[]);

    int[] typedArray = (int[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static int getLength(Object array) {
    checkNotNull(array);
    return JsUtils.<Object[]>uncheckedCast(array).length;
  }

  public static long getLong(Object array, int index) {
    checkNotNull(array);
    checkArgument(array instanceof long[]);

    long[] typedArray = (long[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static short getShort(Object array, int index) {
    checkNotNull(array);
    checkArgument(array instanceof short[]);

    short[] typedArray = (short[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static void set(Object array, int index, Object value) {
    checkNotNull(array);

    if (array instanceof boolean[]) {
      setBoolean(array, index, (Boolean) value);
    } else if (array instanceof byte[]) {
      setByte(array, index, (Byte) value);
    } else if (array instanceof char[]) {
      setChar(array, index, (Character) value);
    } else if (array instanceof double[]) {
      setDouble(array, index, (Double) value);
    } else if (array instanceof float[]) {
      setFloat(array, index, (Float) value);
    } else if (array instanceof int[]) {
      setInt(array, index, (Integer) value);
    } else if (array instanceof long[]) {
      setLong(array, index, (Long) value);
    } else if (array instanceof short[]) {
      setShort(array, index, (Short) value);
    } else {
      checkArgument(array instanceof Object[])
      Object[] typedArray = (Object[]) array;
      checkElementIndex(index, typedArray.length);
      typedArray[index] = value;
    }
  }

  public static void setBoolean(Object array, int index, boolean value) {
    checkNotNull(array);
    checkArgument(array instanceof boolean[]);

    boolean[] typedArray = (boolean[]) array;
    checkElementIndex(index, typedArray.length);
    typedArray[index] = value;
  }

  public static void setByte(Object array, int index, byte value) {
    checkNotNull(array);
    checkArgument(array instanceof byte[]);

    byte[] typedArray = (byte[]) array;
    checkElementIndex(index, typedArray.length);
    typedArray[index] = value;
  }

  public static void setChar(Object array, int index, char value) {
    checkNotNull(array);
    checkArgument(array instanceof char[]);

    char[] typedArray = (char[]) array;
    checkElementIndex(index, typedArray.length);
    typedArray[index] = value;
  }

  public static void setDouble(Object array, int index, double value) {
    checkNotNull(array);
    checkArgument(array instanceof double[]);

    double[] typedArray = (double[]) array;
    checkElementIndex(index, typedArray.length);
    typedArray[index] = value;
  }

  public static void setFloat(Object array, int index, float value) {
    checkNotNull(array);
    checkArgument(array instanceof float[]);

    float[] typedArray = (float[]) array;
    checkElementIndex(index, typedArray.length);
    typedArray[index] = value;
  }

  public static void setInt(Object array, int index, int value) {
    checkNotNull(array);
    checkArgument(array instanceof int[]);

    int[] typedArray = (int[]) array;
    checkElementIndex(index, typedArray.length);
    typedArray[index] = value;
  }

  public static void setLong(Object array, int index, long value) {
    checkNotNull(array);
    checkArgument(array instanceof long[]);

    long[] typedArray = (long[]) array;
    checkElementIndex(index, typedArray.length);
    typedArray[index] = value;
  }

  public static void setShort(Object array, int index, short value) {
    checkNotNull(array);
    checkArgument(array instanceof short[]);

    short[] typedArray = (short[]) array;
    checkElementIndex(index, typedArray.length);
    typedArray[index] = value;
  }

  // Not implemented:
  // public static Object newInstance(Class<?> componentType, int... dimensions)
  // public static Object newInstance(Class<?> componentType, int length)

  private Array() {
  }
}
