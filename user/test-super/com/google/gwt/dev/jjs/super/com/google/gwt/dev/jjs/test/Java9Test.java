/*
 * Copyright 2023 GwtProject contributors
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
package com.google.gwt.dev.jjs.test;

import com.google.gwt.core.client.GwtScriptOnly;
import com.google.gwt.junit.client.GWTTestCase;

import java.util.function.Predicate;
import java.util.function.Supplier;

@GwtScriptOnly
public class Java9Test extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "com.google.gwt.dev.jjs.Java9Test";
    }

    class Resource implements AutoCloseable {
        boolean isOpen = true;

        public void close() {
            this.isOpen = false;
        }
    }

    public void testTryWithResourcesJava9() {
        Resource r1 = new Resource();
        assertTrue(r1.isOpen);
        Resource r2Copy;
        try (r1; Resource r2 = new Resource()) {
            assertTrue(r1.isOpen);
            assertTrue(r2.isOpen);
            r2Copy = r2;
        }
        assertFalse(r1.isOpen);
        assertFalse(r2Copy.isOpen);
    }

    interface Selector extends Predicate<String> {
        @Override
        boolean test(String object);

        default Selector trueSelector() {
            // Unused variable that creates a lambda with a bridge for the method test. The bug #9598
            // was caused by GwtAstBuilder associating the bridge method Lambda.test(Object) on the
            // lambda below to the method Predicate.test(Object), causing the method resolution in the
            // code that refers to the Predicate.test(Object) in the test below to refer to
            // Lambda.test(Object) which is the wrong method.
            return receiver -> true;
        }
    }

    private interface InterfaceWithPrivateMethods {
        int implementedMethod();

        default int defaultMethod() {
            return privateMethod();
        }

        private int privateMethod() {
            return implementedMethod();
        }

        private int staticPrivateMethod() {
            return 42;
        }
    }

    public void testInterfacePrivateMethodsJava9() {
        InterfaceWithPrivateMethods implementor = () -> 50;
        assertEquals(50, implementor.implementedMethod());
        assertEquals(50, implementor.defaultMethod());
        assertEquals(42, implementor.staticPrivateMethod());
    }

    public void testAnonymousDiamondJava9() {
        Supplier<String> helloSupplier = new Supplier<>() {
            @Override
            public String get() {
                return "hello";
            }
        };
        assertEquals("hello", helloSupplier.get());
    }
}