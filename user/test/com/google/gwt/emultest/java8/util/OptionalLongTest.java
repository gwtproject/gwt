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
package com.google.gwt.emultest.java8.util;

import com.google.gwt.junit.client.GWTTestCase;

import java.util.NoSuchElementException;
import java.util.OptionalLong;

/**
 * Tests for OptionalLong JRE emulation.
 */
public class OptionalLongTest extends GWTTestCase {

  private static final long REFERENCE = 10L;
  private static final long OTHER_REFERENCE = 20L;
  private boolean[] mutableFlag;
  private OptionalLong empty;
  private OptionalLong present;

  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    mutableFlag = new boolean[1];
    empty = OptionalLong.empty();
    present = OptionalLong.of(REFERENCE);
  }

  public void testIsPresent() {
    // empty case
    assertFalse(empty.isPresent());

    // non-empty case
    assertTrue(present.isPresent());
  }

  public void testGetAsLong() {
    // empty case
    try {
      empty.getAsLong();
      fail("Empty Optional should throw NoSuchElementException");
    } catch (NoSuchElementException e) {
      // expected
    }

    // non-empty case
    assertEquals(REFERENCE, present.getAsLong());
  }

  public void testIfPresent() {
    // empty case
    empty.ifPresent(null); // should not fail as per JavaDoc
    empty.ifPresent(wrapped -> fail("Empty Optional should not execute consumer"));

    // non-empty case
    try {
      present.ifPresent(null);
      fail("Non-Empty Optional must throw NullPointerException if consumer is null");
    } catch (NullPointerException e) {
      // expected
    }

    present.ifPresent((wrapped) -> {
      assertEquals(REFERENCE, wrapped);
      mutableFlag[0] = true;
    });
    assertTrue("Consumer not executed", mutableFlag[0]);
  }

  public void testOrElse() {
    // empty case
    assertEquals(OTHER_REFERENCE, empty.orElse(OTHER_REFERENCE));

    // non-empty case
    assertEquals(REFERENCE, present.orElse(OTHER_REFERENCE));
  }

  public void testOrElseGet() {
    // empty case
    try {
      empty.orElseGet(null);
      fail("Empty Optional must throw NullPointerException if supplier is null");
    } catch (NullPointerException e) {
      // expected
    }

    assertEquals(OTHER_REFERENCE, empty.orElseGet(() -> OTHER_REFERENCE));

    // non-empty case
    assertEquals(REFERENCE, present.orElseGet(() -> {
      fail("Optional must not execute supplier");
      return OTHER_REFERENCE;
    }));
  }

  public void testOrElseThrow() {
    // empty case
    try {
      empty.orElseThrow(null);
      fail("Empty Optional must throw NullPointerException if supplier is null");
    } catch (NullPointerException e) {
      // expected
    }

    try {
      empty.orElseThrow(() -> null);
      fail("Empty Optional must throw NullPointerException if supplier returns null");
    } catch (NullPointerException e) {
      // expected
    }

    try {
      empty.orElseThrow(IllegalStateException::new);
      fail("Empty Optional must throw supplied exception");
    } catch (IllegalStateException e) {
      // expected
    }

    // non-empty case
    try {
      Object reference = present.orElseThrow(null);
      assertEquals(REFERENCE, reference);
    } catch (NullPointerException e) {
      fail("Optional must not throw NullPointerException if supplier is null");
    }

    assertEquals(REFERENCE, present.orElseThrow(() -> {
      fail("Optional must not execute supplier");
      return new RuntimeException("should not execute");
    }));
  }

  public void testEquals() {
    // empty case
    assertFalse(empty.equals(null));
    assertFalse(empty.equals("should not be equal"));
    assertFalse(empty.equals(present));
    assertTrue(empty.equals(empty));
    assertTrue(empty.equals(OptionalLong.empty()));

    // non empty case
    assertFalse(present.equals(null));
    assertFalse(present.equals("should not be equal"));
    assertFalse(present.equals(empty));
    assertFalse(present.equals(OptionalLong.of(OTHER_REFERENCE)));
    assertTrue(present.equals(present));
    assertTrue(present.equals(OptionalLong.of(REFERENCE)));
  }

  public void testHashcode() {
    // empty case
    assertEquals(0, empty.hashCode());

    // non empty case
    assertEquals(Long.hashCode(REFERENCE), present.hashCode());
  }

}
