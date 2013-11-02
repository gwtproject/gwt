/*
 * Copyright 2006 Google Inc.
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
package com.google.gwt.core.ext;

import com.google.gwt.thirdparty.guava.common.collect.ImmutableSet;


/**
 * Generates source code for subclasses during deferred binding requests. Subclasses must be
 * thread-safe.<br />
 *
 * Carries some metadata (related property names and property/type output instability) that can be
 * used to determine when and on what scope of input to run a particular generator during separate
 * compilation.
 */
public abstract class Generator {

  /**
   * Escapes string content to be a valid string literal.
   *
   * @return an escaped version of <code>unescaped</code>, suitable for being enclosed in double
   *         quotes in Java source
   */
  public static String escape(String unescaped) {
    int extra = 0;
    for (int in = 0, n = unescaped.length(); in < n; ++in) {
      switch (unescaped.charAt(in)) {
        case '\0':
        case '\n':
        case '\r':
        case '\"':
        case '\\':
          ++extra;
          break;
      }
    }

    if (extra == 0) {
      return unescaped;
    }

    char[] oldChars = unescaped.toCharArray();
    char[] newChars = new char[oldChars.length + extra];
    for (int in = 0, out = 0, n = oldChars.length; in < n; ++in, ++out) {
      char c = oldChars[in];
      switch (c) {
        case '\0':
          newChars[out++] = '\\';
          c = '0';
          break;
        case '\n':
          newChars[out++] = '\\';
          c = 'n';
          break;
        case '\r':
          newChars[out++] = '\\';
          c = 'r';
          break;
        case '\"':
          newChars[out++] = '\\';
          c = '"';
          break;
        case '\\':
          newChars[out++] = '\\';
          c = '\\';
          break;
      }
      newChars[out] = c;
    }

    return String.valueOf(newChars);
  }

  /**
   * Generate a default constructible subclass of the requested type. The generator throws
   * <code>UnableToCompleteException</code> if for any reason it cannot provide a substitute class
   *
   * @return the name of a subclass to substitute for the requested class, or return
   *         <code>null</code> to cause the requested type itself to be used
   */
  public abstract String generate(TreeLogger logger, GeneratorContext context, String typeName)
      throws UnableToCompleteException;

  /**
   * Returns the set of names of all properties that are accessed by generator execution and affect
   * its behavior. Returning a null indicates that *all* properties are considered relevant.<br />
   *
   * Generator implementations should customize this to allow for streamlined generator execution in
   * separate compiles. Failure to customize will result in the generator being treated in the most
   * safe manner which is as if its output is affected by every property.
   */
  public ImmutableSet<String> getAccessedPropertyNames() {
    return null;
  }

  /**
   * Whether the *content* of created files (not the list of files created) changes as the set value
   * of configuration properties or the list of legal values of binding properties changes.<br />
   *
   * Some generators aren't actually unstable in this way but returning true is the safest default.
   * Newly defined generators should set this to false if possible, to benefit from improved
   * compilation speed.
   */
  public boolean contentDependsOnProperties() {
    return true;
  }

  /**
   * Whether the *content* of created files (not the list of files created) changes as more types
   * are created that match some subtype query.<br />
   *
   * Most generators aren't actually unstable in this way but returning true is the safest default.
   * Newly defined generators should set this to false if possible, to benefit from improved
   * compilation speed.
   */
  public boolean isUnstableOnTypes() {
    return true;
  }
}
