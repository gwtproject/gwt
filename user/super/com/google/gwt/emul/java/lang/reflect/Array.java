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

import javaemul.internal.ArrayHelper;

/**
 * See <a
 * href="http://java.sun.com/javase/6/docs/api/java/lang/reflect/Array.html">the
 * official Java API doc</a> for details.
 */
public final class Array {

  public static Object get(Object array, int index) {
    if (array instanceof boolean[]) {
      return getBooleanImpl(array, index);
    } else if (array instanceof byte[]) {
      return getByteImpl(array, index);
    } else if (array instanceof char[]) {
      return getCharImpl(array, index);
    } else if (array instanceof double[]) {
      return getDoubleImpl(array, index);
    } else if (array instanceof float[]) {
      return getFloatImpl(array, index);
    } else if (array instanceof int[]) {
      return getIntImpl(array, index);
    } else if (array instanceof long[]) {
      return getLongImpl(array, index);
    } else if (array instanceof short[]) {
      return getShortImpl(array, index);
    } else  {
      checkArgument(array instanceof Object[]);
      Object[] typedArray = (Object[]) array;
      checkElementIndex(index, typedArray.length);
      return typedArray[index];
    }
  }

  public static boolean getBoolean(Object array, int index) {
    checkArgument(array instanceof boolean[]);
    return getBooleanImpl(array, index);
  }

  private static boolean getBooleanImpl(Object array, int index) {
    boolean[] typedArray = (boolean[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static byte getByte(Object array, int index) {
    checkArgument(array instanceof byte[]);
    return getByteImpl(array, index);
  }

  private static byte getByteImpl(Object array, int index) {
    byte[] typedArray = (byte[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static char getChar(Object array, int index) {
    checkArgument(array instanceof char[]);
    return getCharImpl(array, index);
  }

  private static char getCharImpl(Object array, int index) {
    char[] typedArray = (char[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static double getDouble(Object array, int index) {
    checkArgument(array instanceof double[]);
    return getDoubleImpl(array, index);
  }

  private static double getDoubleImpl(Object array, int index) {
    double[] typedArray = (double[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static float getFloat(Object array, int index) {
    checkArgument(array instanceof float[]);
    return getFloatImpl(array, index);
  }

  private static float getFloatImpl(Object array, int index) {
    float[] typedArray = (float[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static int getInt(Object array, int index) {
    checkArgument(array instanceof int[]);
    return getIntImpl(array, index);
  }

  private static int getIntImpl(Object array, int index) {
    int[] typedArray = (int[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static int getLength(Object array) {
    checkNotNull(array);
    return ArrayHelper.getLength(array);
  }

  public static long getLong(Object array, int index) {
    checkArgument(array instanceof long[]);
    return getLongImpl(array, index);
  }

  private static long getLongImpl(Object array, int index) {
    long[] typedArray = (long[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static short getShort(Object array, int index) {
    checkArgument(array instanceof short[]);
    return getShortImpl(array, index);
  }

  private static short getShortImpl(Object array, int index) {
    short[] typedArray = (short[]) array;
    checkElementIndex(index, typedArray.length);
    return typedArray[index];
  }

  public static void set(Object array, int index, Object value) {
    if (array instanceof boolean[]) {
      setBooleanImpl(array, index, (Boolean) value);
    } else if (array instanceof byte[]) {
      setByteImpl(array, index, (Byte) value);
    } else if (array instanceof char[]) {
      setCharImpl(array, index, (Character) value);
    } else if (array instanceof double[]) {
      setDoubleImpl(array, index, (Double) value);
    } else if (array instanceof float[]) {
      setFloatImpl(array, index, (Float) value);
    } else if (array instanceof int[]) {
      setIntImpl(array, index, (Integer) value);
    } else if (array instanceof long[]) {
      setLongImpl(array, index, (Long) value);
    } else if (array instanceof short[]) {
      setShortImpl(array, index, (Short) value);
    } else {
      checkArgument(array instanceof Object[]);
      Object[] typedArray = (Object[]) array;
      checkElementIndex(index, typedArray.length);
      typedArray[index] = value;
    }
  }

  public static void setBoolean(Object array, int index, boolean value) {
    checkArgument(array instanceof boolean[]);
    setBooleanImpl(array, index, value);
  }

  private static void setBooleanImpl(Object array, int index, boolean value) {
    boolean[] typedArray = (boolean[]) array;
    checkElementIndex(index, typedArray.length);
    typedArray[index] = value;
  }

  public static void setByte(Object array, int index, byte value) {
    checkArgument(array instanceof byte[]);
    setByteImpl(array, index, value);
  }

  private static void setByteImpl(Object array, int index, byte value) {
    byte[] typedArray = (byte[]) array;
    checkElementIndex(index, typedArray.length);
    typedArray[index] = value;
  }

  public static void setChar(Object array, int index, char value) {
    checkArgument(array instanceof char[]);
    setCharImpl(array, index, value);
  }

  private static void setCharImpl(Object array, int index, char value) {
    char[] typedArray = (char[]) array;
    checkElementIndex(index, typedArray.length);
    typedArray[index] = value;
  }

  public static void setDouble(Object array, int index, double value) {
    checkArgument(array instanceof double[]);
    setDoubleImpl(array, index, value);
  }

  private static void setDoubleImpl(Object array,int index, double value) {
    double[] typedArray = (double[]) array;
    checkElementIndex(index, typedArray.length);
    typedArray[index] = value;
  }

  public static void setFloat(Object array, int index, float value) {
    checkArgument(array instanceof float[]);
    setFloatImpl(array, index, value);
  }

  private static void setFloatImpl(Object array, int index, float value) {
    float[] typedArray = (float[]) array;
    checkElementIndex(index, typedArray.length);
    typedArray[index] = value;
  }

  public static void setInt(Object array, int index, int value) {
    checkArgument(array instanceof int[]);
    setIntImpl(array, index, value);
  }

  private static void setIntImpl(Object array, int index, int value) {
    int[] typedArray = (int[]) array;
    checkElementIndex(index, typedArray.length);
    typedArray[index] = value;
  }

  public static void setLong(Object array, int index, long value) {
    checkArgument(array instanceof long[]);
    setLongImpl(array, index, value);
  }

  private static void setLongImpl(Object array, int index, long value) {
    long[] typedArray = (long[]) array;
    checkElementIndex(index, typedArray.length);
    typedArray[index] = value;
  }

  public static void setShort(Object array, int index, short value) {
    checkArgument(array instanceof short[]);
    setShortImpl(array, index, value);
  }

  private static void setShortImpl(Object array, int index, short value) {
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
