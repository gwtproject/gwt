/*
 * Copyright 2010 Google Inc.
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
package com.google.gwt.dev.javac.testing.impl;


import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Loads the actual source of a type. This should be used only for types
 * directly tested by this package's tests. Note that use of this class
 * requires your source files to be on your classpath.
 * <p>
 * Subject to change/removal in the future.
 */
public class RealJavaResource extends MockJavaResource {
  public RealJavaResource(Class<?> clazz) {
    super(clazz.getName());
  }

  @Override
  public CharSequence getContent() {
    String resourceName = getTypeName().replace('.', '/') + ".java";
    try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
      if (stream == null) {
        return null;
      }
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
