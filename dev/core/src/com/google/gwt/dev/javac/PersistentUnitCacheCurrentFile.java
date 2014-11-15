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
import com.google.gwt.thirdparty.guava.common.base.Preconditions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * The current file and stream being written to by the persistent unit cache, if any.
 * (Helper class for {@link PersistentUnitCache}
 */
class PersistentUnitCacheCurrentFile {
  private File file;
  private ObjectOutputStream stream;
  private int unitsWritten = 0;

  /**
   * Opens a cache file in the given directory for writing unit cache entries.
   * A cache file may not already be open.
   */
  synchronized void open(TreeLogger logger, File cacheDirectory)
      throws UnableToCompleteException {
    Preconditions.checkState(!isOpen(), "persistent cache file already open");

    File newFile = createCacheFile(logger, cacheDirectory);
    logger.log(Type.TRACE, "created cache file: " + newFile);
    ObjectOutputStream newStream = openObjectStream(logger, newFile);

    this.file = newFile;
    this.stream = newStream;
    unitsWritten = 0;
  }

  /**
   * Writes an entry to the currently open file.
   */
  synchronized void writeObject(TreeLogger logger, UnitCacheEntry entry) throws IOException {
    if (!isOpen()) {
      logger.log(Type.TRACE, "skipped writing entry to persistent unit cache (no file open)");
      return; // perhaps it was closed while we were on the queue?
    }
    CompilationUnit unit = Preconditions.checkNotNull(entry.getUnit());
    stream.writeObject(unit);
    unitsWritten++;
  }

  /**
   * Closes the current file and deletes it if it's empty. If no file is open, does nothing.
   */
  synchronized void close(TreeLogger logger) {
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
   * @param cacheDirectory where to put the new file
   */
  synchronized void rotate(TreeLogger logger, File cacheDirectory)
      throws UnableToCompleteException {
    logger.log(Type.TRACE, "rotating persistent unit cache");
    close(logger);
    open(logger, cacheDirectory);
  }

  /**
   * Deletes a file unless it is the currently open cache file.
   * (This is here to ensure there is no race with rotate.)
   */
  synchronized void deleteUnlessOpen(TreeLogger logger, File toDelete) {
    if (isOpen(toDelete)) {
      return;
    }
    logger.log(Type.TRACE, "deleting file: " + toDelete);
    if (!toDelete.delete()) {
      logger.log(Type.WARN, "unable to delete file: " + toDelete);
    }
  }

  /**
   * Returns true if we are currently reading the given cache file.
   * (To avoid a race, the caller must ensure that rotate() is not called.)
   */
  synchronized boolean isOpen(File other) {
    return isOpen() && file.equals(other);
  }

  private boolean isOpen() {
    return file != null;
  }

  /**
   * Creates a new, empty file with a name based on the current system time.
   */
  private static File createCacheFile(TreeLogger logger, File cacheDirectory)
      throws UnableToCompleteException {
    File newFile = null;
    long timestamp = System.currentTimeMillis();
    try {
      do {
        newFile = new File(cacheDirectory, PersistentUnitCache.CURRENT_VERSION_CACHE_FILE_PREFIX +
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
