/*
 * Copyright 2024 GWT Project Authors
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
package com.google.gwt.emultest.java.internal;

import com.google.gwt.junit.client.GWTTestCase;

import java.util.Objects;

public class VarArgsTest extends GWTTestCase {
  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  public void testVarargsObjects() {
    assertEquals(new VarargsSummary<>(1, Object[].class, null),
            acceptsObjects((Object) null));
    // noinspection ConfusingArgumentToVarargsMethod
    assertEquals(new VarargsSummary<>(-1, null, null),
            acceptsObjects(null));
    assertEquals(new VarargsSummary<>(-1, null, null),
            acceptsObjects((Object[]) null));
    assertEquals(new VarargsSummary<>(0, Object[].class, null),
            acceptsObjects());
    assertEquals(new VarargsSummary<>(1, Object[].class, null),
            acceptsObjects("hello"));
    assertEquals(new VarargsSummary<>(2, Object[].class, null),
            acceptsObjects("hello", "world"));
    // noinspection ConfusingArgumentToVarargsMethod
    assertEquals(new VarargsSummary<>(2, String[].class, null),
            acceptsObjects(new String[]{"hello", "world"}));
    assertEquals(new VarargsSummary<>(2, Object[].class, null),
            acceptsObjects(new Object[]{"hello", "world"}));
  }

  private VarargsSummary<Void> acceptsObjects(Object... values) {
    return new VarargsSummary<>(
            values == null ? -1 : values.length,
            values == null ? null : values.getClass(),
            null);
  }

  public void testVarargsObjectsWithOtherParam() {
    assertEquals(new VarargsSummary<>(1, Object[].class, 1),
            acceptsObjectsAndOtherParam(1, (Object) null));
    // noinspection ConfusingArgumentToVarargsMethod
    assertEquals(new VarargsSummary<>(-1, null, 2),
            acceptsObjectsAndOtherParam(2, null));
    assertEquals(new VarargsSummary<>(-1, null, 3),
            acceptsObjectsAndOtherParam(3, (Object[]) null));
    assertEquals(new VarargsSummary<>(0, Object[].class, 4),
            acceptsObjectsAndOtherParam(4));
    assertEquals(new VarargsSummary<>(1, Object[].class, 5),
            acceptsObjectsAndOtherParam(5, "hello"));
    assertEquals(new VarargsSummary<>(2, Object[].class, 6),
            acceptsObjectsAndOtherParam(6, "hello", "world"));
    // noinspection ConfusingArgumentToVarargsMethod
    assertEquals(new VarargsSummary<>(2, String[].class, 7),
            acceptsObjectsAndOtherParam(7, new String[]{"hello", "world"}));
    assertEquals(new VarargsSummary<>(2, Object[].class, 8),
            acceptsObjectsAndOtherParam(8, new Object[]{"hello", "world"}));
  }

  private VarargsSummary<Integer> acceptsObjectsAndOtherParam(int number, Object... values) {
    return new VarargsSummary<>(
            values == null ? -1 : values.length,
            values == null ? null : values.getClass(),
            number);
  }

  public void testObjectsWithOtherVarargsParam() {
    assertEquals(new VarargsSummary<>(1, Object[].class, 1),
            acceptsObjectsAndGenericParam(1, (Object) null));
    // noinspection ConfusingArgumentToVarargsMethod
    assertEquals(new VarargsSummary<>(-1, null, "2"),
            acceptsObjectsAndGenericParam("2", null));
    assertEquals(new VarargsSummary<>(-1, null, null),
            acceptsObjectsAndGenericParam(null, (Object[]) null));
    assertEquals(new VarargsSummary<>(0, Object[].class, 4),
            acceptsObjectsAndGenericParam(4));
    assertEquals(new VarargsSummary<>(1, Object[].class, 5),
            acceptsObjectsAndGenericParam(5, "hello"));
    assertEquals(new VarargsSummary<>(2, Object[].class, 6),
            acceptsObjectsAndGenericParam(6, "hello", "world"));
    // noinspection ConfusingArgumentToVarargsMethod
    assertEquals(new VarargsSummary<>(2, String[].class, 7),
            acceptsObjectsAndGenericParam(7, new String[]{"hello", "world"}));
    assertEquals(new VarargsSummary<>(2, Object[].class, 8),
            acceptsObjectsAndGenericParam(8, new Object[]{"hello", "world"}));
  }

  private <T> VarargsSummary<T> acceptsObjectsAndGenericParam(T generic, Object... values) {
    return new VarargsSummary<>(
            values == null ? -1 : values.length,
            values == null ? null : values.getClass(),
            generic);
  }

  public void testGenericVarargsWithOtherParam() {
    assertEquals(new VarargsSummary<>(1, Object[].class, 1),
            acceptsGenericVarargsAndOtherParam(1, (Object) null));
    // noinspection ConfusingArgumentToVarargsMethod
    assertEquals(new VarargsSummary<>(-1, null, 2),
            acceptsGenericVarargsAndOtherParam(2, null));
    assertEquals(new VarargsSummary<>(-1, null, 3),
            acceptsGenericVarargsAndOtherParam(3, (Object[]) null));
    assertEquals(new VarargsSummary<>(0, Object[].class, 4),
            acceptsGenericVarargsAndOtherParam(4));
    assertEquals(new VarargsSummary<>(1, String[].class, 5),
            acceptsGenericVarargsAndOtherParam(5, "hello"));
    assertEquals(new VarargsSummary<>(2, String[].class, 6),
            acceptsGenericVarargsAndOtherParam(6, "hello", "world"));
    assertEquals(new VarargsSummary<>(2, String[].class, 7),
            acceptsGenericVarargsAndOtherParam(7, new String[]{"hello", "world"}));
    assertEquals(new VarargsSummary<>(2, Object[].class, 8),
            acceptsGenericVarargsAndOtherParam(8, new Object[]{"hello", "world"}));
  }

  private <T> VarargsSummary<Integer> acceptsGenericVarargsAndOtherParam(int number, T... values) {
    return new VarargsSummary<>(
            values == null ? -1 : values.length,
            values == null ? null : values.getClass(),
            number);
  }

  public static final class VarargsSummary<T> {
    private final int count;
    private final Class<?> paramType;
    private final T value;

    public VarargsSummary(int count, Class<?> paramType, T value) {
      this.count = count;
      this.paramType = paramType;
      this.value = value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      VarargsSummary<?> that = (VarargsSummary<?>) o;
      return count == that.count
              && Objects.equals(paramType, that.paramType)
              && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(count, paramType, value);
    }

    @Override
    public String toString() {
      return "count=" + count +
              ", paramType=" + paramType +
              ", value=" + value;
    }
  }
}
