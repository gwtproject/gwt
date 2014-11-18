/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.dev.javac;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.javac.MemoryUnitCache.UnitCacheEntry;
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.dev.util.log.speedtracer.DevModeEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.thirdparty.guava.common.annotations.VisibleForTesting;
import com.google.gwt.thirdparty.guava.common.base.Preconditions;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.hash.Hashing;
import com.google.gwt.thirdparty.guava.common.io.Files;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.JarURLConnection;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;

/**
 * The directory containing persistent unit cache files.
 * (Helper class for {@link PersistentUnitCache}.)
 */
class PersistentUnitCacheDir {

  private static final String DIRECTORY_NAME = "gwt-unitCache";
  private static final String CACHE_FILE_PREFIX = "gwt-unitCache-";

  static final String CURRENT_VERSION_CACHE_FILE_PREFIX =
      CACHE_FILE_PREFIX + compilerVersion() + "-";

  private final TreeLogger logger;
  private final File dir;
  private final CurrentFile currentFile = new CurrentFile();

  PersistentUnitCacheDir(TreeLogger logger, File parentDir) throws UnableToCompleteException {
    this.logger = logger;

    /*
     * We must canonicalize the path here, otherwise we might set cacheDirectory
     * to something like "/path/to/x/../gwt-unitCache". If this were to happen,
     * the mkdirs() call below would create "/path/to/gwt-unitCache" but
     * not "/path/to/x".
     * Further accesses via the uncanonicalized path will fail if "/path/to/x"
     * had not been created by other means.
     *
     * Fixes issue 6443
     */
    try {
      parentDir = parentDir.getCanonicalFile();
    } catch (IOException e) {
      logger.log(TreeLogger.WARN, "Can't get canonical directory for "
          + parentDir.getAbsolutePath(), e);
      throw new UnableToCompleteException();
    }

    dir = chooseCacheDir(parentDir);
    if (!dir.isDirectory() && !dir.mkdirs()) {
      logger.log(TreeLogger.WARN, "Can't create directory: " + dir.getAbsolutePath());
      throw new UnableToCompleteException();
    }

    if (!dir.canRead()) {
      logger.log(Type.WARN, "Can't read directory: " + dir.getAbsolutePath());
      throw new UnableToCompleteException();
    }

    logger.log(TreeLogger.TRACE, "Persistent unit cache dir set to: " + dir.getAbsolutePath());

    currentFile.open(logger, createEmptyCacheFile(logger, dir));
  }

  String getPath() {
    return dir.getAbsolutePath();
  }

  /**
   * Delete all cache files in the directory except for the currently open file.
   */
  synchronized void deleteCacheFilesExceptOpen() {
    SpeedTracerLogger.Event deleteEvent = SpeedTracerLogger.start(DevModeEventType.DELETE_CACHE);
    logger.log(TreeLogger.TRACE, "Deleting cache files from " + dir);

    // We want to delete cache files from previous versions as well.
    List<File> allVersionsList = listFiles(CACHE_FILE_PREFIX);
    int deleteCount = 0;
    for (File candidate : allVersionsList) {
      if (deleteUnlessOpen(candidate)) {
        deleteCount++;
      }
    }

    logger.log(TreeLogger.TRACE, "Deleted " + deleteCount + " cache files from " + dir);
    deleteEvent.end();
  }

  synchronized void rotate() throws UnableToCompleteException {
    currentFile.rotate(logger, createEmptyCacheFile(logger, dir));
  }

  synchronized List<File> listCacheFilesToLoad() {
    return removeCurrent(listFiles(CURRENT_VERSION_CACHE_FILE_PREFIX));
  }

  synchronized boolean deleteUnlessOpen(File cacheFile) {
    if (currentFile.isOpen(cacheFile)) {
      return false;
    }
    logger.log(Type.TRACE, "deleting file: " + cacheFile);
    boolean deleted = cacheFile.delete();
    if (!deleted) {
      logger.log(Type.WARN, "unable to delete file: " + cacheFile);
    }
    return deleted;
  }

  synchronized void writeObject(UnitCacheEntry entry) throws UnableToCompleteException {
    currentFile.writeObject(logger, entry);
  }

  synchronized void closeCurrentFile() {
    currentFile.close(logger);
  }

  @VisibleForTesting
  static File chooseCacheDir(File parentDir) {
    return new File(parentDir, DIRECTORY_NAME);
  }

  /**
   * Lists files in the cache directory that start with the given prefix.
   *
   * <p>The files will be sorted according to {@link java.io.File#compareTo}, which
   * differs on Unix versus Windows, but is good enough to sort by age
   * for the names we use.</p>
   */
  private List<File> listFiles(String prefix) {
    File[] files = dir.listFiles();
    if (files == null) {
      // Shouldn't happen, just satisfying null check warning.
      return Collections.emptyList();
    }
    List<File> out = Lists.newArrayList();
    for (File file : files) {
      if (file.getName().startsWith(prefix)) {
        out.add(file);
      }
    }
    Collections.sort(out);
    return out;
  }

  /**
   * Removes the currently open file from a list of files.
   * @return the new list.
   */
  private List<File> removeCurrent(Iterable<File> fileList) {
    List<File> out = Lists.newArrayList();
    for (File file : fileList) {
      if (!currentFile.isOpen(file)) {
        out.add(file);
      }
    }
    return out;
  }

  /**
   * Creates a new, empty file with a name based on the current system time.
   */
  private static File createEmptyCacheFile(TreeLogger logger, File dir)
      throws UnableToCompleteException {
    File newFile = null;
    long timestamp = System.currentTimeMillis();
    try {
      do {
        newFile = new File(dir, CURRENT_VERSION_CACHE_FILE_PREFIX +
            String.format("%016X", timestamp++));
      } while (!newFile.createNewFile());
    } catch (IOException ex) {
      logger.log(TreeLogger.WARN, "Can't create new cache log file "
          + newFile.getAbsolutePath() + ".", ex);
      throw new UnableToCompleteException();
    }

    if (!newFile.canWrite()) {
      logger.log(TreeLogger.WARN, "Can't write to new cache log file "
          + newFile.getAbsolutePath() + ".");
      throw new UnableToCompleteException();
    }

    return newFile;
  }

  // TODO: use CompilerVersion class after it's committed.
  private static String compilerVersion() {
    String hash = "unknown";
    try {
      URLConnection urlConnection =
          JNode.class.getResource("JNode.class").openConnection();
      if (urlConnection instanceof JarURLConnection) {
        String gwtdevJar = ((JarURLConnection) urlConnection).getJarFile().getName();
        hash = Files.hash(new File(gwtdevJar), Hashing.sha1()).toString();
      } else {
        System.err.println("Could not find the GWT compiler jar file. "
            + "Serialization errors might occur when accessing the persistent unit cache.");
      }
    } catch (IOException e) {
      System.err.println("Could not compute the hash for the GWT compiler jar file."
          + "Serialization errors might occur when accessing the persistent unit cache.");
      e.printStackTrace();
    }
    return hash;
  }

  /**
   * The current file and stream being written to by the persistent unit cache, if any.
   *
   * <p>Not thread safe. (Parent class handles concurrency.)
   */
  private static class CurrentFile {
    private File file;
    private ObjectOutputStream stream;
    private int unitsWritten = 0;

    /**
     * Opens a file for writing compilation units.
     * Overwrites the file (it's typically empty).
     * A cache file may not already be open.
     */
    void open(TreeLogger logger, File toOpen)
        throws UnableToCompleteException {
      Preconditions.checkState(!isOpen(), "persistent cache file already open");

      logger.log(Type.TRACE, "created cache file: " + toOpen);
      ObjectOutputStream newStream = openObjectStream(logger, toOpen);

      this.file = toOpen;
      this.stream = newStream;
      unitsWritten = 0;
    }

    /**
     * Writes an entry to the currently open file.
     */
    void writeObject(TreeLogger logger, UnitCacheEntry entry)
        throws UnableToCompleteException {
      if (!isOpen()) {
        logger.log(Type.TRACE, "skipped writing entry to persistent unit cache (no file open)");
        return; // perhaps it was closed while we were on the queue?
      }
      CompilationUnit unit = Preconditions.checkNotNull(entry.getUnit());
      try {
        stream.writeObject(unit);
        unitsWritten++;
      } catch (IOException e) {
        logger.log(TreeLogger.ERROR, "Error saving compilation unit to cache file: " + file, e);
        throw new UnableToCompleteException();
      }
    }

    /**
     * Closes the current file and deletes it if it's empty. If no file is open, does nothing.
     */
    void close(TreeLogger logger) {
      if (!isOpen()) {
        return;
      }

      logger.log(Type.TRACE, "closing cache file: " + file + " (" + unitsWritten + " units written)");

      try {
        stream.close();
      } catch (IOException e) {
        // ignore
      }

      if (unitsWritten == 0) {
        // Remove useless empty file.
        logger.log(Type.TRACE, "deleting empty file: " + file);
        boolean deleted = file.delete();
        if (!deleted) {
          logger.log(Type.INFO, "Couldn't delete persistent unit cache file: " + file);
        }
      }

      file = null;
      stream = null;
      unitsWritten = 0;
    }

    /**
     * Closes the current file (if any) and opens a new one.
     * (This drops any object references kept alive by Java serialization.)
     * @param toOpen where to put the new file
     */
    void rotate(TreeLogger logger, File toOpen) throws UnableToCompleteException {
      logger.log(Type.TRACE, "rotating persistent unit cache");
      close(logger);
      open(logger, toOpen);
    }

    /**
     * Returns true if we are currently reading the given cache file.
     */
    boolean isOpen(File other) {
      return isOpen() && file.equals(other);
    }

    private boolean isOpen() {
      return file != null;
    }

    private static ObjectOutputStream openObjectStream(TreeLogger logger, File file)
        throws UnableToCompleteException {

      FileOutputStream fstream = null;
      try {
        fstream = new FileOutputStream(file);
        return new ObjectOutputStream(new BufferedOutputStream(fstream));
      } catch (IOException e) {
        logger.log(Type.ERROR, "Can't open persistent unit cache file", e);
        if (fstream != null) {
          try {
            fstream.close();
          } catch (IOException e2) {
            // ignore
          }
        }
        throw new UnableToCompleteException();
      }
    }
  }
}
