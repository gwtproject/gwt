/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev.util;

import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.thirdparty.guava.common.hash.Hashing;
import com.google.gwt.thirdparty.guava.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URLConnection;

/**
 * Utility for uniquely identifying the current compiler version.
 */
public class CompilerVersion {

  private static String compilerVersionHash;

  /**
   * Calculates and returns a hash to uniquely identify the current compiler version if possible.
   */
  public static synchronized String getHash() {
    if (compilerVersionHash != null) {
      return compilerVersionHash;
    }

    compilerVersionHash = "version-unknown";
    try {
      // Try to find the compiler jar file.
      URLConnection urlConnection = JNode.class.getResource("JNode.class").openConnection();
      // If it was found.
      if (urlConnection instanceof JarURLConnection) {
        // Get a hash of it's contents.
        String gwtdevJar = ((JarURLConnection) urlConnection).getJarFile().getName();
        compilerVersionHash = Files.hash(new File(gwtdevJar), Hashing.sha1()).toString();
      } else {
        System.err.println("Could not find the GWT compiler jarfile. "
            + "Serialization errors might occur when accessing disk caches.");
      }
    } catch (IOException e) {
      System.err.println("Failed to read the GWT compiler jarfile. "
          + "Serialization errors might occur when accessing disk caches.");
    }
    return compilerVersionHash;
  }
}
