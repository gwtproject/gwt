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
package com.google.gwt.dev.resource.impl;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import junit.framework.TestCase;

import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Tests for ResourceAccumulator.
 */
public class ResourceAccumulatorTest extends TestCase {

  public void testAddFile() throws Exception {
    Path rootDirectory = Files.createTempDirectory(null);
    Path subDirectory = createDirectoryIn("subdir", rootDirectory);

    ResourceAccumulator resourceAccumulator =
        new ResourceAccumulator(rootDirectory, createInclusivePathPrefixSet());

    assertTrue(getResources(resourceAccumulator).isEmpty());

    createFileIn("New.java", subDirectory);
    waitForFileEvents();

    List<AbstractResource> resources = getResources(resourceAccumulator);
    assertEquals(1, resources.size());
    assertTrue(resources.get(0).getPath().endsWith("New.java"));

    resourceAccumulator.shutdown();
  }

  public void testDeleteFile() throws Exception {
    Path rootDirectory = Files.createTempDirectory(null);
    Path subDirectory = createDirectoryIn("subdir", rootDirectory);
    Path originalFile = createFileIn("SomeFile.java", subDirectory);

    ResourceAccumulator resourceAccumulator =
        new ResourceAccumulator(rootDirectory, createInclusivePathPrefixSet());

    List<AbstractResource> resources = getResources(resourceAccumulator);
    assertEquals(1, resources.size());
    assertTrue(resources.get(0).getPath().endsWith("SomeFile.java"));

    Files.delete(originalFile);
    waitForFileEvents();

    assertTrue(getResources(resourceAccumulator).isEmpty());

    resourceAccumulator.shutdown();
  }

  public void testListensInNewDirectories() throws Exception {
    Path rootDirectory = Files.createTempDirectory(null);

    ResourceAccumulator resourceAccumulator =
        new ResourceAccumulator(rootDirectory, createInclusivePathPrefixSet());

    assertTrue(getResources(resourceAccumulator).isEmpty());

    // Create a new directory and contained file AFTER the root directory has started being listened
    // to.
    Path subDirectory = createDirectoryIn("subdir", rootDirectory);
    createFileIn("New.java", subDirectory);
    waitForFileEvents();

    List<AbstractResource> resources = getResources(resourceAccumulator);
    assertEquals(1, resources.size());
    assertTrue(resources.get(0).getPath().endsWith("New.java"));

    resourceAccumulator.shutdown();
  }

  public void testMultipleListeners() throws Exception {
    Path rootDirectory = Files.createTempDirectory(null);
    Path subDirectory = createDirectoryIn("subdir", rootDirectory);

    ResourceAccumulator resourceAccumulator1 =
        new ResourceAccumulator(rootDirectory, createInclusivePathPrefixSet());
    ResourceAccumulator resourceAccumulator2 =
        new ResourceAccumulator(rootDirectory, createInclusivePathPrefixSet());

    assertTrue(getResources(resourceAccumulator1).isEmpty());
    assertTrue(getResources(resourceAccumulator2).isEmpty());

    createFileIn("New.java", subDirectory);
    waitForFileEvents();

    List<AbstractResource> resources1 = getResources(resourceAccumulator1);
    assertEquals(1, resources1.size());
    assertTrue(resources1.get(0).getPath().endsWith("New.java"));

    List<AbstractResource> resources2 = getResources(resourceAccumulator2);
    assertEquals(1, resources2.size());
    assertTrue(resources2.get(0).getPath().endsWith("New.java"));

    resourceAccumulator1.shutdown();
    resourceAccumulator2.shutdown();
  }

  public void testRenameFile() throws Exception {
    Path rootDirectory = Files.createTempDirectory(null);
    Path subDirectory = createDirectoryIn("subdir", rootDirectory);
    Path originalFile = createFileIn("OriginalName.java", subDirectory);
    Path renamedFile = subDirectory.resolve("Renamed.java");

    ResourceAccumulator resourceAccumulator =
        new ResourceAccumulator(rootDirectory, createInclusivePathPrefixSet());

    List<AbstractResource> resources = getResources(resourceAccumulator);
    assertEquals(1, resources.size());
    assertTrue(resources.get(0).getPath().endsWith("OriginalName.java"));

    Files.move(originalFile, renamedFile);
    waitForFileEvents();

    resources = getResources(resourceAccumulator);
    assertEquals(1, resources.size());
    assertTrue(resources.get(0).getPath().endsWith("Renamed.java"));

    resourceAccumulator.shutdown();
  }

  public void testRenameDirectory() throws Exception {
    Path rootDirectory = Files.createTempDirectory(null);
    Path subDirectory = createDirectoryIn("original_dir", rootDirectory);
    createFileIn("Name1.java", subDirectory);
    createFileIn("Name2.java", subDirectory);
    Path renamedSubDirectory = rootDirectory.resolve("new_dir");

    ResourceAccumulator resourceAccumulator =
        new ResourceAccumulator(rootDirectory, createInclusivePathPrefixSet());

    List<AbstractResource> resources = getResources(resourceAccumulator);
    assertEquals(2, resources.size());
    assertTrue(resources.get(0).getPath().endsWith("original_dir/Name1.java"));
    assertTrue(resources.get(1).getPath().endsWith("original_dir/Name2.java"));

    Files.move(subDirectory, renamedSubDirectory);
    waitForFileEvents();

    resources = getResources(resourceAccumulator);
    assertEquals(2, resources.size());
    assertTrue(resources.get(0).getPath().endsWith("new_dir/Name1.java"));
    assertTrue(resources.get(1).getPath().endsWith("new_dir/Name2.java"));

    resourceAccumulator.shutdown();
  }

  public void testRenameParentDirectory() throws Exception {
    if (SystemUtils.IS_OS_WINDOWS) {
      return; // moving a directory while WatchService is running -> access denied
    }
    Path rootDirectory = Files.createTempDirectory(null);
    Path parentDirectory = createDirectoryIn("original_dir", rootDirectory);
    Path subDirectory = createDirectoryIn("subdir", parentDirectory);
    createFileIn("Name1.java", subDirectory);
    createFileIn("Name2.java", subDirectory);
    Path renamedParentDirectory = rootDirectory.resolve("new_dir");

    ResourceAccumulator resourceAccumulator =
        new ResourceAccumulator(rootDirectory, createInclusivePathPrefixSet());

    List<AbstractResource> resources = getResources(resourceAccumulator);
    assertEquals(2, resources.size());
    assertTrue(resources.get(0).getPath().endsWith("original_dir/subdir/Name1.java"));
    assertTrue(resources.get(1).getPath().endsWith("original_dir/subdir/Name2.java"));

    Files.move(parentDirectory, renamedParentDirectory);
    waitForFileEvents();

    resources = getResources(resourceAccumulator);
    assertEquals(2, resources.size());
    assertTrue(resources.get(0).getPath().endsWith("new_dir/subdir/Name1.java"));
    assertTrue(resources.get(1).getPath().endsWith("new_dir/subdir/Name2.java"));

    resourceAccumulator.shutdown();
  }

  public void testSymlinkInfiniteLoop() throws Exception {
    if (SystemUtils.IS_OS_WINDOWS) {
      return; // symlinks not working on Windows
    }
    Path rootDirectory = Files.createTempDirectory(null);
    Path subDirectory = Files.createTempDirectory(null);

    ResourceAccumulator resourceAccumulator =
        new ResourceAccumulator(rootDirectory, createInclusivePathPrefixSet());

    assertTrue(getResources(resourceAccumulator).isEmpty());

    // Symlink in a loop
    java.nio.file.Files.createSymbolicLink(rootDirectory.resolve("sublink"),
        subDirectory);
    java.nio.file.Files.createSymbolicLink(subDirectory.resolve("sublink"),
        rootDirectory);
    createFileIn("New.java", subDirectory);
    waitForFileEvents();

    try {
      // Should throw an error if resourceAccumulator got stuck in an infinite directory scan loop.
      getResources(resourceAccumulator);
      fail();
    } catch (FileSystemException expected) {
      // Expected
    }

    resourceAccumulator.shutdown();
  }

  public void testSymlinks() throws Exception {
    if (SystemUtils.IS_OS_WINDOWS) {
      return; // symlinks not working on Windows
    }
    Path scratchDirectory = Files.createTempDirectory(null);
    Path newFile = createFileIn("New.java", scratchDirectory);
    Path rootDirectory = Files.createTempDirectory(null);
    Path subDirectory = Files.createTempDirectory(null);

    ResourceAccumulator resourceAccumulator =
        new ResourceAccumulator(rootDirectory, createInclusivePathPrefixSet());

    assertTrue(getResources(resourceAccumulator).isEmpty());

    // Symlink in a subdirectory and then symlink in a contained file.
    Files.createSymbolicLink(rootDirectory.resolve("sublink"),
        subDirectory);
    Files.createSymbolicLink(subDirectory.resolve("New.java"),
        newFile);
    waitForFileEvents();

    List<AbstractResource> resources = getResources(resourceAccumulator);
    assertEquals(1, resources.size());
    assertTrue(resources.get(0).getPath().endsWith("sublink/New.java"));

    resourceAccumulator.shutdown();
  }

  private static Path createDirectoryIn(String fileName, Path inDirectory) throws IOException {
    Path newDirectory = inDirectory.resolve(fileName);
    return Files.createDirectory(newDirectory);
  }

  private static Path createFileIn(String fileName, Path inDirectory) throws IOException {
    Path newFile = inDirectory.resolve(fileName);
    return Files.createFile(newFile);
  }

  private List<AbstractResource> getResources(ResourceAccumulator resourceAccumulator)
      throws IOException {
    resourceAccumulator.refreshResources();
    List<AbstractResource> list = Lists.newArrayList(resourceAccumulator.getResources().keySet());
    Collections.sort(list, new Comparator<AbstractResource>() {
      @Override
      public int compare(AbstractResource a, AbstractResource b) {
        return a.getLocation().compareTo(b.getLocation());
      }
    });
    return list;
  }

  private Set<PathPrefixSet> pathPrefixes = Sets.newHashSet();

  @Override
  public void tearDown() {
    pathPrefixes.clear();
  }

  private PathPrefixSet createInclusivePathPrefixSet() {
    PathPrefixSet pathPrefixSet = new PathPrefixSet();
    pathPrefixSet.add(new PathPrefix("", null));

    // Keep the reference until the end of the test to create a strong reference, otherwise
    // will get GCed as ResourceAccumulator refers to it weakly.
    pathPrefixes.add(pathPrefixSet);
    return pathPrefixSet;
  }

  private void waitForFileEvents() throws InterruptedException {
    Thread.sleep(100);
  }
}
