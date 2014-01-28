/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.dev.javac;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.cfg.LibraryGroup;
import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.google.gwt.thirdparty.guava.common.base.Preconditions;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * A cache that finds compilation unit instances in a library group.<br />
 *
 * Removals are not supported since libraries are immutable and the removals would only be needed if
 * library contents were invalid.
 */
public class LibraryGroupUnitCache implements UnitCache {

  private static final String JAVA_SUFFIX = ".java";

  /**
   * Translates type names to resource locations to ease lookups since the unit cache system caches
   * based on resource location but the natural mode of interaction with this cache is via type
   * names.
   */
  public static String typeNameToResourceLocation(String typeName) {
    Preconditions.checkState(!typeName.endsWith(JAVA_SUFFIX));

    // If typeName refers to a nested type using binary syntax.
    if (typeName.contains("$")) {
      typeName = typeName.split("\\$")[0];
    } else {
      // If typeName refers to a nested type using internal syntax.
      LinkedList<String> packagesAndTypes =
          Lists.newLinkedList(Arrays.asList(typeName.split("\\.")));
      String typeShortName = null;
      while (!packagesAndTypes.isEmpty()
          && Character.isUpperCase(packagesAndTypes.getLast().charAt(0))) {
        typeShortName = packagesAndTypes.removeLast();
      }

      typeName = Joiner.on(".").join(packagesAndTypes)
          + (typeShortName != null ? "." + typeShortName : "");
    }

    return typeName.replace(".", "/") + JAVA_SUFFIX;
  }

  private Map<String, CompilationUnit> compilationUnitsByTypeName = Maps.newLinkedHashMap();
  private Set<String> knownEmptyResourceLocations = Sets.newLinkedHashSet();
  private LibraryGroup libraryGroup;

  public LibraryGroupUnitCache(LibraryGroup libraryGroup) {
    this.libraryGroup = libraryGroup;
  }

  /**
   * Adds a {@link CompilationUnit} to the cache.<br />
   *
   * Though this class is intended primarily to expose and cache compilation units from previously
   * compiled library files it must also be prepared to accept brand new compilation units resulting
   * from live compilation as this is an absolute requirement for the current compiler design.
   */
  @Override
  public void add(CompilationUnit compilationUnit) {
    String typeName = compilationUnit.getTypeName();
    if (compilationUnitsByTypeName.containsKey(typeName)) {
      return;
    }

    compilationUnitsByTypeName.put(typeName, compilationUnit);
    knownEmptyResourceLocations.remove(typeNameToResourceLocation(typeName));
  }

  @Override
  public void addArchivedUnit(CompilationUnit compilationUnit) {
    throw new UnsupportedOperationException(
        "When using a library group as the source for unit caching, surfacing "
        + "other sources of previously compiled compilation units (.gwtar) is not supported.");
  }

  @Override
  public void cleanup(TreeLogger logger) {
    compilationUnitsByTypeName.clear();
    knownEmptyResourceLocations.clear();
  }

  /**
   * Finds and returns the compilation unit for the type name referenced in the provided ContentId.
   * <br />
   *
   * Normally ContentId based lookups are expected to point at a particular revision of a type. But
   * when sourcing compilation units from a LibraryGroup there is only one version of any
   * compilation unit and that version is by definition "most current".
   */
  @Override
  public CompilationUnit find(ContentId contentId) {
    String typeName = contentId.getSourceTypeName();
    return find(typeNameToResourceLocation(typeName));
  }

  @Override
  public CompilationUnit find(String resourceLocation) {
    String typeName = Shared.toTypeName(resourceLocation);
    if (compilationUnitsByTypeName.containsKey(typeName)) {
      return compilationUnitsByTypeName.get(typeName);
    }

    if (knownEmptyResourceLocations.contains(resourceLocation)) {
      return null;
    }

    CompilationUnit compilationUnit = libraryGroup.getCompilationUnitByTypeName(typeName);
    if (compilationUnit == null) {
      knownEmptyResourceLocations.add(resourceLocation);
      return null;
    }
    compilationUnitsByTypeName.put(compilationUnit.getTypeName(), compilationUnit);
    return compilationUnit;
  }

  @Override
  public void remove(CompilationUnit unit) {
    throw new UnsupportedOperationException(
        "Compilation units can not be removed from immutable libraries.");
  }
}
