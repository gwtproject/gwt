/*
 * Copyright 2018 Google Inc.
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
package com.google.gwt.emultest.java.util.concurrent.atomic;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests for {@link java.util.concurrent.atomic.AtomicReference} that runs in plain junit and GWT
 * modes.
 */
public class AtomicReferenceTest extends EmulTestBase {

  public void testNoArgConstructor() {
    AtomicReference<Object> ar = new AtomicReference<>();
    assertNull(ar.get());
  }

  public void testWithValueConstructor() {
    Object object = new Object();
    AtomicReference<Object> ar = new AtomicReference<>(object);
    assertEquals(object, ar.get());
  }

  public void testCompareAndSetPositive() {
    Object expect = new Object();
    Object update = new Object();

    AtomicReference<Object> ar = new AtomicReference<>(expect);
    boolean returnValue = ar.compareAndSet(expect, update);

    assertTrue(returnValue);
    assertEquals(update, ar.get());
  }

  public void testCompareAndSetNegative() {
    Object expect = new Object();
    Object update = new Object();
    Object notExpect = new Object();

    AtomicReference<Object> ar = new AtomicReference<>(expect);
    boolean returnValue = ar.compareAndSet(notExpect, update);

    assertFalse(returnValue);
    assertEquals(expect, ar.get());
  }

  public void testGetAndSet() {
    Object old = new Object();
    Object update = new Object();

    AtomicReference<Object> ar = new AtomicReference<>(old);
    Object returnValue = ar.getAndSet(update);

    assertEquals(old, returnValue);
    assertEquals(update, ar.get());
  }

  public void testLazySet() {
    Object old = new Object();
    Object update = new Object();

    AtomicReference<Object> ar = new AtomicReference<>(old);
    ar.lazySet(update);

    assertEquals(update, ar.get());
  }

  public void testSet() {
    Object old = new Object();
    Object update = new Object();

    AtomicReference<Object> ar = new AtomicReference<>(old);
    ar.lazySet(update);

    assertEquals(update, ar.get());
  }

  public void testWeakCompareAndSetPositive() {
    Object expect = new Object();
    Object update = new Object();

    AtomicReference<Object> ar = new AtomicReference<>(expect);
    boolean returnValue = ar.weakCompareAndSet(expect, update);

    assertTrue(returnValue);
    assertEquals(update, ar.get());
  }

  public void testWeakCompareAndSetNegative() {
    Object expect = new Object();
    Object update = new Object();
    Object notExpect = new Object();

    AtomicReference<Object> ar = new AtomicReference<>(expect);
    boolean returnValue = ar.weakCompareAndSet(notExpect, update);

    assertFalse(returnValue);
    assertEquals(expect, ar.get());
  }
}
