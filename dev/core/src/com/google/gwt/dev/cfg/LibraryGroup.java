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
package com.google.gwt.dev.cfg;

import com.google.gwt.dev.javac.CompilationUnit;
import com.google.gwt.dev.jjs.PermutationResult;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.util.PersistenceBackedObject;
import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.base.Predicates;
import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Iterables;
import com.google.gwt.thirdparty.guava.common.collect.LinkedHashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A convenience wrapper around a set of libraries.<br />
 *
 * Indexes library contents to enable single step retrieval of resources by name.<br />
 *
 * Analyzes the library dependency tree to provide link ordering and property and rebound type
 * change analysis for partial generator execution.<br />
 *
 * Combines library resource lists for easy iteration over the complete set.
 */
public class LibraryGroup {

  /**
   * Factory function that constructs and returns a library group from a list of libraries.
   */
  public static LibraryGroup fromLibraries(List<Library> libraries) {
    LibraryGroup libraryGroup = new LibraryGroup();
    for (Library library : libraries) {
      libraryGroup.libraries.add(library);
    }
    libraryGroup.buildLibraryIndexes();
    return libraryGroup;
  }

  /**
   * Factory function that constructs and returns a library group from a list of zip library paths.
   */
  public static LibraryGroup fromZipPaths(List<String> zipLibraryPaths) {
    List<Library> zipLibraries = Lists.newArrayList();
    for (String zipLibraryPath : zipLibraryPaths) {
      zipLibraries.add(new ZipLibrary(zipLibraryPath));
    }
    return fromLibraries(zipLibraries);
  }

  private Set<String> compilationUnitTypeNames;
  private List<Library> libraries = Lists.newArrayList();
  private Map<String, Library> librariesByBuildResourcePath;
  private Map<String, Library> librariesByClassFilePath;
  private Map<String, Library> librariesByCompilationUnitTypeName;
  private Map<String, Library> librariesByName;
  private Map<String, Library> librariesByPublicResourcePath;
  private List<PersistenceBackedObject<PermutationResult>> permutationResultHandles;
  private Set<String> reboundTypeNames;
  private List<Library> rootLibraries;
  private Set<String> superSourceCompilationUnitTypeNames;

  private LibraryGroup() {
    // Private to force class construction via one of the public factory functions.
  }

  /**
   * Returns whether a build resource is is available at the given path.
   */
  public boolean containsBuildResource(String buildResourcePath) {
    return getLibrariesByBuildResourcePath().containsKey(buildResourcePath);
  }

  /**
   * Create and return a library group of just the given libraries referenced by name. Useful for
   * taking an overly broad library group of all libraries in a dependency tree and trimming it down
   * to a restricted set of strict top level dependencies.
   */
  public LibraryGroup createSubgroup(List<String> libraryNames) {
    return fromLibraries(getLibraries(libraryNames));
  }

  /**
   * Walks the parts of the library dependency graph that have not run the given generator
   * referenced by name and accumulates and returns a map from binding property name to newly legal
   * values that were declared in those libraries.
   */
  public Multimap<String, String> gatherNewBindingPropertyValuesForGenerator(String generatorName) {
    Multimap<String, String> newBindingPropertyValuesByName = LinkedHashMultimap.create();
    for (Library unprocessedLibrary : gatherLibrariesForGenerator(generatorName, true)) {
      newBindingPropertyValuesByName.putAll(unprocessedLibrary.getNewBindingPropertyValuesByName());
    }
    return newBindingPropertyValuesByName;
  }

  /**
   * Walks the parts of the library dependency graph that have not run the given generator
   * referenced by name and accumulates and returns a map from configuration property name to newly
   * set values that were declared in those libraries.
   */
  public Multimap<String, String> gatherNewConfigurationPropertyValuesForGenerator(
      String generatorName) {
    Multimap<String, String> newConfigurationPropertyValuesByName = LinkedHashMultimap.create();
    for (Library unprocessedLibrary : gatherLibrariesForGenerator(generatorName, true)) {
      newConfigurationPropertyValuesByName.putAll(
          unprocessedLibrary.getNewConfigurationPropertyValuesByName());
    }
    return newConfigurationPropertyValuesByName;
  }

  /**
   * Walks the parts of the library dependency graph that have not run the given generator
   * referenced by name and accumulates and returns a set of newly rebound type names.
   */
  public Set<String> gatherNewReboundTypeNamesForGenerator(String generatorName) {
    Set<String> newReboundTypeNames = Sets.newHashSet();
    for (Library unprocessedLibrary : gatherLibrariesForGenerator(generatorName, true)) {
      newReboundTypeNames.addAll(unprocessedLibrary.getReboundTypeNames());
    }
    return newReboundTypeNames;
  }

  /**
   * Walks the parts of the library dependency graph that have already run the given generator
   * referenced by name and accumulates and returns the set of old rebound type names.
   */
  public Set<String> gatherOldReboundTypeNamesForGenerator(String generatorName) {
    Set<String> oldReboundTypeNames = Sets.newHashSet();
    for (Library processedLibrary : gatherLibrariesForGenerator(generatorName, false)) {
      oldReboundTypeNames.addAll(processedLibrary.getReboundTypeNames());
    }
    return oldReboundTypeNames;
  }

  /**
   * Returns the resource referenced by name if present or null;
   */
  public Resource getBuildResourceByPath(String buildResourcePath) {
    if (!getLibrariesByBuildResourcePath().containsKey(buildResourcePath)) {
      return null;
    }
    Library libraryImpl = getLibrariesByBuildResourcePath().get(buildResourcePath);
    return libraryImpl.getBuildResourceByPath(buildResourcePath);
  }

  /**
   * Returns the set of all build resource paths.
   */
  public Set<String> getBuildResourcePaths() {
    return getLibrariesByBuildResourcePath().keySet();
  }

  /**
   * Opens and returns an input stream for the given class file path if present or null.
   */
  public InputStream getClassFileStream(String classFilePath) {
    Set<String> classFilePaths = getLibrariesByClassFilePath().keySet();
    if (!classFilePaths.contains(Libraries.computeClassFileName(classFilePath))) {
      return null;
    }
    Library library =
        getLibrariesByClassFilePath().get(Libraries.computeClassFileName(classFilePath));
    return library.getClassFileStream(classFilePath);
  }

  /**
   * Returns the compilation unit for the given compilation unit type name if present or null.
   */
  public CompilationUnit getCompilationUnitByTypeName(String typeName) {
    if (!getLibrariesByCompilationUnitTypeName().containsKey(typeName)) {
      return null;
    }
    Library libraryImpl = getLibrariesByCompilationUnitTypeName().get(typeName);
    return libraryImpl.getCompilationUnitByTypeName(typeName);
  }

  /**
   * Returns the set of all compilation unit type names.
   */
  public Set<String> getCompilationUnitTypeNames() {
    if (compilationUnitTypeNames == null) {
      compilationUnitTypeNames = Sets.newLinkedHashSet();
      for (Library libraryImpl : libraries) {
        compilationUnitTypeNames.addAll(libraryImpl.getCompilationUnitTypeNames());
      }
      compilationUnitTypeNames = Collections.unmodifiableSet(compilationUnitTypeNames);
    }
    return compilationUnitTypeNames;
  }

  /**
   * Returns the list of all permutation result handles (one per library) in library link order.
   */
  public List<PersistenceBackedObject<PermutationResult>> getPermutationResultHandlesInLinkOrder() {
    if (permutationResultHandles == null) {
      permutationResultHandles = Lists.newArrayList();
      for (Library library : libraries) {
        permutationResultHandles.add(library.getPermutationResultHandle());
      }
      permutationResultHandles = Collections.unmodifiableList(permutationResultHandles);
    }
    return permutationResultHandles;
  }

  /**
   * Returns the resource referenced by name if present or null;
   */
  public Resource getPublicResourceByPath(String path) {
    if (!getLibrariesByPublicResourcePath().containsKey(path)) {
      return null;
    }
    Library libraryImpl = getLibrariesByPublicResourcePath().get(path);
    return libraryImpl.getPublicResourceByPath(path);
  }

  /**
   * Returns the set of all public resource paths.
   */
  public Set<String> getPublicResourcePaths() {
    return getLibrariesByPublicResourcePath().keySet();
  }

  /**
   * Returns the set of names of types which are the subject of GWT.create() calls in source code in
   * any of the contained libraries.
   */
  public Set<String> getReboundTypeNames() {
    if (reboundTypeNames == null) {
      reboundTypeNames = Sets.newLinkedHashSet();
      for (Library libraryImpl : libraries) {
        reboundTypeNames.addAll(libraryImpl.getReboundTypeNames());
      }
      reboundTypeNames = Collections.unmodifiableSet(reboundTypeNames);
    }
    return reboundTypeNames;
  }

  /**
   * Returns the set of compilation unit type names for all contained super source compilation
   * units.
   */
  public Set<String> getSuperSourceCompilationUnitTypeNames() {
    if (superSourceCompilationUnitTypeNames == null) {
      superSourceCompilationUnitTypeNames = Sets.newLinkedHashSet();
      for (Library libraryImpl : libraries) {
        superSourceCompilationUnitTypeNames.addAll(
            libraryImpl.getSuperSourceCompilationUnitTypeNames());
      }
      superSourceCompilationUnitTypeNames =
          Collections.unmodifiableSet(superSourceCompilationUnitTypeNames);
    }
    return superSourceCompilationUnitTypeNames;
  }

  // VisibleForTesting
  List<Library> getLibraries() {
    return libraries;
  }

  // VisibleForTesting
  List<Library> getLibraries(Collection<String> libraryNames) {
    return Lists.newArrayList(
        Maps.filterKeys(librariesByName, Predicates.in(libraryNames)).values());
  }

  private Iterable<Library> asLibraries(Set<String> libraryNames) {
    return Iterables.transform(libraryNames, new Function<String, Library>() {
        @Override
      public Library apply(String libraryName) {
        return librariesByName.get(libraryName);
      }
    });
  }

  private void buildLibraryIndexes() {
    librariesByName = Maps.newLinkedHashMap();
    for (Library libraryImpl : libraries) {
      librariesByName.put(libraryImpl.getLibraryName(), libraryImpl);
    }

    Multimap<Library, Library> parentLibrariesByChildLibrary = HashMultimap.create();
    Multimap<Library, Library> childLibrariesByParentLibrary = HashMultimap.create();
    for (Library parentLibrary : libraries) {
      for (String childLibraryName : parentLibrary.getDependencyLibraryNames()) {
        Library childLibrary = librariesByName.get(childLibraryName);
        if (childLibrary == null) {
          // Naively this would seem to indicate an incomplete provided gwtlib library set, but
          // actually occurs as a byproduct of overly complicated build system supersource library
          // generation. Ignore.
          continue;
        }
        parentLibrariesByChildLibrary.put(childLibrary, parentLibrary);
        childLibrariesByParentLibrary.put(parentLibrary, childLibrary);
      }
    }

    rootLibraries = Lists.newArrayList();
    for (Library library : libraries) {
      boolean libraryHasParents = parentLibrariesByChildLibrary.containsKey(library);
      if (libraryHasParents) {
        continue;
      }
      rootLibraries.add(library);
    }

    List<Library> librariesInLinkOrder = Lists.newArrayList();
    Set<Library> maybeLeafLibraries = Sets.newLinkedHashSet(libraries);
    while (!maybeLeafLibraries.isEmpty()) {
      List<Library> leafLibraries = Lists.newArrayList();
      for (Library maybeLeafLibrary : maybeLeafLibraries) {
        if (childLibrariesByParentLibrary.get(maybeLeafLibrary).isEmpty()) {
          leafLibraries.add(maybeLeafLibrary);
        }
      }
      librariesInLinkOrder.addAll(leafLibraries);

      maybeLeafLibraries.clear();
      for (Library leafLibrary : leafLibraries) {
        Collection<Library> parentLibraries = parentLibrariesByChildLibrary.removeAll(leafLibrary);
        maybeLeafLibraries.addAll(parentLibraries);
        for (Library parentLibrary : parentLibraries) {
          childLibrariesByParentLibrary.remove(parentLibrary, leafLibrary);
        }
      }
    }

    libraries = librariesInLinkOrder;
  }

  /**
   * Walks the library dependency graph and collects a list of libraries that either have or have
   * not run the given generator depending on the given gatherNotProcessed boolean.
   */
  private List<Library> gatherLibrariesForGenerator(String generatorName, boolean generatorWasRun) {
    Set<Library> exploredLibraries = Sets.newHashSet();
    LinkedList<Library> unexploredLibraries = Lists.newLinkedList();
    List<Library> librariesForGenerator = Lists.newArrayList();

    unexploredLibraries.addAll(rootLibraries);
    while (!unexploredLibraries.isEmpty()) {
      Library library = unexploredLibraries.removeFirst();
      exploredLibraries.add(library);

      boolean alreadyProcessed = library.getRanGeneratorNames().contains(generatorName);
      if (generatorWasRun && alreadyProcessed) {
        continue;
      }

      librariesForGenerator.add(library);

      for (Library dependencyLibrary : asLibraries(library.getDependencyLibraryNames())) {
        if (exploredLibraries.contains(dependencyLibrary)) {
          continue;
        }
        unexploredLibraries.add(dependencyLibrary);
      }
    }

    return librariesForGenerator;
  }

  private Map<String, Library> getLibrariesByBuildResourcePath() {
    if (librariesByBuildResourcePath == null) {
      librariesByBuildResourcePath = Maps.newLinkedHashMap();
      for (Library libraryImpl : libraries) {
        Set<String> buildResourcePaths = libraryImpl.getBuildResourcePaths();
        for (String buildResourcePath : buildResourcePaths) {
          librariesByBuildResourcePath.put(buildResourcePath, libraryImpl);
        }
      }
      librariesByBuildResourcePath = Collections.unmodifiableMap(librariesByBuildResourcePath);
    }
    return librariesByBuildResourcePath;
  }

  private Map<String, Library> getLibrariesByClassFilePath() {
    if (librariesByClassFilePath == null) {
      librariesByClassFilePath = Maps.newLinkedHashMap();

      // Record regular class files first.
      for (Library libraryImpl : libraries) {
        Set<String> classFilePaths = libraryImpl.getClassFilePaths();
        for (String classFilePath : classFilePaths) {
          librariesByClassFilePath.put(classFilePath, libraryImpl);
        }
      }

      // Overwrite with superSource class files, so they have higher priority.
      for (Library libraryImpl : libraries) {
        Set<String> superSourceClassFilePaths = libraryImpl.getSuperSourceClassFilePaths();
        for (String superSourceClassFilePath : superSourceClassFilePaths) {
          librariesByClassFilePath.put(superSourceClassFilePath, libraryImpl);
        }
      }

      librariesByClassFilePath = Collections.unmodifiableMap(librariesByClassFilePath);
    }
    return librariesByClassFilePath;
  }

  private Map<String, Library> getLibrariesByCompilationUnitTypeName() {
    if (librariesByCompilationUnitTypeName == null) {
      librariesByCompilationUnitTypeName = Maps.newLinkedHashMap();

      // Record regular compilation units first.
      for (Library libraryImpl : libraries) {
        for (String compilationUnitTypeName : libraryImpl.getCompilationUnitTypeNames()) {
          librariesByCompilationUnitTypeName.put(compilationUnitTypeName, libraryImpl);
        }
      }

      // Overwrite with superSource compilation units, so they have higher priority.
      for (Library libraryImpl : libraries) {
        for (String superSourceCompilationUnitTypeName :
            libraryImpl.getSuperSourceCompilationUnitTypeNames()) {
          librariesByCompilationUnitTypeName.put(superSourceCompilationUnitTypeName, libraryImpl);
        }
      }

      librariesByCompilationUnitTypeName =
          Collections.unmodifiableMap(librariesByCompilationUnitTypeName);
    }
    return librariesByCompilationUnitTypeName;
  }

  private Map<String, Library> getLibrariesByPublicResourcePath() {
    if (librariesByPublicResourcePath == null) {
      librariesByPublicResourcePath = Maps.newLinkedHashMap();
      for (Library libraryImpl : libraries) {
        Set<String> publicResourcePaths = libraryImpl.getPublicResourcePaths();
        for (String publicResourcePath : publicResourcePaths) {
          librariesByPublicResourcePath.put(publicResourcePath, libraryImpl);
        }
      }

      librariesByPublicResourcePath = Collections.unmodifiableMap(librariesByPublicResourcePath);
    }
    return librariesByPublicResourcePath;
  }
}
