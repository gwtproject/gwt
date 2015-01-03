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
package elemental.json.impl;

import junit.framework.TestCase;
import elemental.json.JsonFactory;

/**
 * Tests {@link JreJsonArray}.
 *
 */
public class JreJsonArrayTest extends TestCase {

  private JsonFactory factory = new JreJsonFactory();

  public void testArrayEquals() {
    JreJsonArray array1 = new JreJsonArray(factory);
    array1.set(0, "foo");
    JreJsonArray array2 = new JreJsonArray(factory);
    array2.set(0, "foo");
    assertTrue(array1.jsEquals(array2));
  }

  public void testArrayNotEquals() {
    JreJsonArray array1 = new JreJsonArray(factory);
    array1.set(0, "foo");
    JreJsonArray array2 = new JreJsonArray(factory);
    array2.set(0, "bar");
    assertFalse(array1.jsEquals(array2));
  }

  public void testArrayNullEquals() {
    JreJsonArray array1 = new JreJsonArray(factory);
    array1.set(0, new JreJsonNull());
    JreJsonArray array2 = new JreJsonArray(factory);
    array2.set(0, new JreJsonNull());
    assertTrue(array1.jsEquals(array2));
  }

  public void testStringNotEquals() {
    JreJsonArray array1 = new JreJsonArray(factory);
    array1.set(0, "foo");
    JreJsonString aString = new JreJsonString("bar");
    assertFalse(array1.jsEquals(aString));
  }

  public void testNullNotEquals() {
    JreJsonArray array1 = new JreJsonArray(factory);
    array1.set(0, "foo");
    JreJsonNull aNull = new JreJsonNull();
    assertFalse(array1.jsEquals(aNull));
  }

  public void testArrayOfArrayEquals() {
    JreJsonArray array1 = new JreJsonArray(factory);
    JreJsonArray nestedArray1 = new JreJsonArray(factory);
    nestedArray1.set(0, "foo");
    array1.set(0, nestedArray1);
    JreJsonArray array2 = new JreJsonArray(factory);
    JreJsonArray nestedArray2 = new JreJsonArray(factory);
    nestedArray2.set(0, "foo");
    array2.set(0, nestedArray2);
    assertTrue(array1.jsEquals(array2));
  }

  public void testArrayOfArrayNotEquals() {
    JreJsonArray array1 = new JreJsonArray(factory);
    JreJsonArray nestedArray1 = new JreJsonArray(factory);
    nestedArray1.set(0, "foo");
    array1.set(0, nestedArray1);
    JreJsonArray array2 = new JreJsonArray(factory);
    JreJsonArray nestedArray2 = new JreJsonArray(factory);
    nestedArray2.set(0, "bar");
    array2.set(0, nestedArray2);
    assertFalse(array1.jsEquals(array2));
  }

}
