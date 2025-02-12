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
package com.google.gwt.core.ext.linker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility methods for Linkers.
 */
public class LinkerUtils {
  /**
   * Gets the contents of a file from the class path as a String. Note: this
   * method is only guaranteed to work for resources in the same class loader
   * that contains this {@link LinkerUtils} class.
   *
   * @param path the partial path to the resource on the class path
   * @return the contents of the file
   * @throws IOException if the file could not be found or an error occurred
   *           while reading it
   */
  public static String readClasspathFileAsString(String path) throws IOException {
    try (InputStream in = LinkerUtils.class.getClassLoader().getResourceAsStream(path)) {
      if (in == null) {
        throw new FileNotFoundException(path);
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  private LinkerUtils() {
  }
}
