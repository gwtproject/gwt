/*
 * Copyright 2025 GWT Project Authors
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
package com.google.gwt.emultest.java17.util;

import com.google.gwt.emultest.java.util.EmulTestBase;

import java.util.Objects;

/**
 * Tests {@link Objects} additions up to Java 17 (checkIndex* is part of Java 16).
 */
public class ObjectsTest extends EmulTestBase {

    public void testCheckIndex() {
        assertEquals(50000000000L, Objects.checkIndex(50000000000L, 100000000000L));
        assertThrows(IndexOutOfBoundsException.class,
            () -> Objects.checkIndex(-50000000000L, 50000000000L));
        assertThrows(IndexOutOfBoundsException.class,
            () -> Objects.checkIndex(100000000000L, 50000000000L));
        assertThrows(IndexOutOfBoundsException.class,
            () -> Objects.checkIndex(50000000000L, 50000000000L));
    }

    public void testCheckFromToIndex() {
        assertEquals(50000000000L,
            Objects.checkFromToIndex(50000000000L, 70000000000L, 100000000000L));
        assertEquals(0L, Objects.checkFromToIndex(0, 100000000000L, 100000000000L));
        assertThrows(IndexOutOfBoundsException.class,
            () -> Objects.checkFromToIndex(-50000000000L, 10000000000L, 50000000000L));
        assertThrows(IndexOutOfBoundsException.class,
            () -> Objects.checkFromToIndex(100000000000L, 10000000000L, 50000000000L));
        assertThrows(IndexOutOfBoundsException.class,
            () -> Objects.checkFromToIndex(10000000000L, 100000000000L, 50000000000L));
    }

    public void testCheckFromIndexSize() {
        assertEquals(50000000000L,
            Objects.checkFromIndexSize(50000000000L, 20000000000L, 100000000000L));
        assertEquals(0L, Objects.checkFromIndexSize(0, 100000000000L, 100000000000L));
        assertThrows(IndexOutOfBoundsException.class,
            () -> Objects.checkFromIndexSize(-50000000000L, 10000000000L, 50000000000L));
        assertThrows(IndexOutOfBoundsException.class,
            () -> Objects.checkFromIndexSize(100000000000L, 10000000000L, 50000000000L));
        assertThrows(IndexOutOfBoundsException.class,
            () -> Objects.checkFromIndexSize(10000000000L, 100000000000L, 50000000000L));
        assertThrows(IndexOutOfBoundsException.class,
            () -> Objects.checkFromIndexSize(10000000000L, -50000000000L, 50000000000L));
    }

}