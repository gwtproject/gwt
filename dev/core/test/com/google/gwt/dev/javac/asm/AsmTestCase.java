/*
 * Copyright 2009 Google Inc.
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
package com.google.gwt.dev.javac.asm;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;

/**
 * Base class for ASM unit tests that defines some useful methods.
 */
public abstract class AsmTestCase extends TestCase {

  private static final ClassLoader CLASSLOADER = CollectClassDataTest.class.getClassLoader();

  public AsmTestCase() {
    super();
  }

  /**
   * Read the bytes of a class.
   *
   * @param className binary name (ie com.Foo$Bar) of the class to read
   * @return bytes from class file or null if not found
   */
  protected byte[] getClassBytes(String className) {
    String classPath = className.replace('.', '/')
        + ".class";
    try (InputStream is = CLASSLOADER.getResourceAsStream(classPath)) {
      if (is != null) {
        return is.readAllBytes();
      }
    } catch (IOException ignored) {
    }
    return null;
  }
}
