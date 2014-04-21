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

import com.google.gwt.thirdparty.guava.common.base.Strings;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableSet;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

/**
 * Generates source code for subclasses during deferred binding requests. Subclasses must be
 * thread-safe.
 * <p>
 * Well-behaved generators can speed up execution by strictly specifying their minimal input using
 * the {@code RequiredInput} annotation.
 */
public abstract class Generator {

  public static final Set<String> ALL_PROPERTIES = ImmutableSet.of();

  /**
   * An annotation indicating that a Generator can be run with purely local information during
   * incremental compilation.
   * <p>
   * Can be customized to require access to some specific non-local information.
   * <p>
   * This information is used by Generator invocation during incremental compilation to run
   * Generator as early as possible in the compile tree (and thus as parallelized as possible) while
   * still creating accurate output.
   * <p>
   * Any unannotated Generator falls back on the safest but also slowest behavior.
   */
  @Retention(RetentionPolicy.RUNTIME)
  public @interface RunsLocal {

    /**
     * Whether a Generator needs access to all possible types to be able to run accurately.
     * <p>
     * When false, the Generator is safe to be run separately for each library in an incremental
     * compile using only locally available information. When true the Generator invocation must be
     * delayed to the root of the compile tree when the global set of types can be made available.
     * In general this result of pushing Generator execution towards the root of the tree has
     * negative performance consequences on incremental compile performance.
     * <p>
     * Generators that want to be as fast as possible should strive to make use of purely local
     * type information.
     */
    boolean requiresGlobalTypeSet() default false;

    /**
     * The list of names of properties which will be accessed by this Generator. It is assumed that
     * any change in the values of these properties will affect the content of Generator output.
     * <p>
     * Any Generator that depends on properties will have its execution delayed to the point in the
     * compile tree where it is known that the properties it cares about have stopped changing. In
     * general this result of pushing Generator execution towards the root of the tree has negative
     * performance consequences on incremental compile performance.
     * <p>
     * Generators that want to be as fast as possible should strive not to read any properties.
     * <p>
     * Set to {@code {"%ALL%"}} to indicate a need to arbitrarily access any property.
     */
    String[] requiresProperties() default {};
  }

  /**
   * Returns a Set of the names of properties that will be accessed by the given Generator.
   */
  public static Set<String> getAccessedPropertyNames(Class<? extends Generator> generatorClass) {
    RunsLocal runsLocalAnnotation = generatorClass.getAnnotation(RunsLocal.class);
    // If the Generator says nothing about its required input.
    if (runsLocalAnnotation == null) {
      return ALL_PROPERTIES;
    }
    String[] properties = runsLocalAnnotation.requiresProperties();
    if (properties.length == 1 && "%ALL%".equals(properties[0])) {
      return ALL_PROPERTIES;
    }
    return Sets.newHashSet(properties);
  }

  /**
   * Returns whether the given Generator depends on access to the global set of types to be able to
   * run accurately.
   */
  public static boolean contentDependsOnTypes(Class<? extends Generator> generatorClass) {
    RunsLocal runsLocalAnnotation = generatorClass.getAnnotation(RunsLocal.class);
    // If the Generator says nothing about its required input.
    if (runsLocalAnnotation == null) {
      // Assume that it needs a global type set.
      return true;
    }
    // Otherwise let it answer.
    return runsLocalAnnotation.requiresGlobalTypeSet();
  }

  /**
   * Returns whether the given Generator depends on any of the properties whose names have been
   * passed.
   */
  public static boolean caresAboutProperties(Class<? extends Generator> generatorClass,
      Set<String> propertyNames) {
    Set<String> accessedPropertyNames = Generator.getAccessedPropertyNames(generatorClass);

    // If this generator cares about all properties.
    if (accessedPropertyNames == ALL_PROPERTIES) {
      // Then if some properties were supplied, it cares about them.
      return !propertyNames.isEmpty();
    }

    // Otherwise an explicit list of cared about properties was supplied. Return whether any of the
    // supplied properties is cared about.
    return !Sets.intersection(accessedPropertyNames, propertyNames).isEmpty();
  }

  private static final int MAX_SIXTEEN_BIT_NUMBER_STRING_LENGTH = 5;

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
   * Returns an escaped version of a String that is valid as a Java class name.<br />
   *
   * Illegal characters become "_" + the character integer padded to 5 digits like "_01234". The
   * padding prevents collisions like the following "_" + "123" + "4" = "_" + "1234". The "_" escape
   * character is escaped to "__".
   */
  public static String escapeClassName(String unescapedString) {
    char[] unescapedCharacters = unescapedString.toCharArray();
    StringBuilder escapedCharacters = new StringBuilder();

    boolean firstCharacter = true;
    for (char unescapedCharacter : unescapedCharacters) {
      if (firstCharacter && !Character.isJavaIdentifierStart(unescapedCharacter)) {
        // Escape characters that can't be the first in a class name.
        escapeAndAppendCharacter(escapedCharacters, unescapedCharacter);
      } else if (!Character.isJavaIdentifierPart(unescapedCharacter)) {
        // Escape characters that can't be in a class name.
        escapeAndAppendCharacter(escapedCharacters, unescapedCharacter);
      } else if (unescapedCharacter == '_') {
        // Escape the escape character.
        escapedCharacters.append("__");
      } else {
        // Leave valid characters alone.
        escapedCharacters.append(unescapedCharacter);
      }

      firstCharacter = false;
    }

    return escapedCharacters.toString();
  }

  private static void escapeAndAppendCharacter(
      StringBuilder escapedCharacters, char unescapedCharacter) {
    String numberString = Integer.toString(unescapedCharacter);
    numberString = Strings.padStart(numberString, MAX_SIXTEEN_BIT_NUMBER_STRING_LENGTH, '0');
    escapedCharacters.append("_" + numberString);
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
}
