/*
 * Copyright 2011 Google Inc.
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
import com.google.gwt.dev.jjs.InternalCompilerException;
import com.google.gwt.dev.jjs.impl.GwtAstBuilder;
import com.google.gwt.dev.util.StringInterningObjectInputStream;
import com.google.gwt.dev.util.log.speedtracer.DevModeEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;
import com.google.gwt.thirdparty.guava.common.annotations.VisibleForTesting;
import com.google.gwt.thirdparty.guava.common.util.concurrent.Futures;
import com.google.gwt.util.tools.Utility;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class that manages a persistent cache of {@link CompilationUnit} instances.
 * Writes out {@link CompilationUnit} instances to a cache in a
 * background thread.
 * <p>
 * The persistent cache is implemented as a directory of log files with a date
 * timestamp. A new log file gets created each time a new PersistentUnitCache is
 * instantiated, (once per invocation of the compiler or DevMode). The design is
 * intended to support only a single PersistentUnitCache instance in the
 * compiler at a time.
 * <p>
 * As new units are compiled, the cache data is appended to a log. This allows
 * Java serialization to efficiently store references. The next time the cache
 * is started, all logs are replayed and loaded into the cache in chronological
 * order, with newer units taking precedence. A new cache file is created for
 * any newly compiled units in this session. After a threshold of a certain
 * number of files in the directory is reached
 * {@link PersistentUnitCache#CACHE_FILE_THRESHOLD} , the cache files are
 * consolidated back into a single file.
 *
 * <p>
 * System Properties (see {@link UnitCacheSingleton}).
 *
 * <ul>
 * <li>gwt.persistentunitcache : enables the persistent cache (eventually will
 * be default)</li>
 * <li>gwt.persistentunitcachedir=<dir>: sets or overrides the cache directory</li>
 * </ul>
 *
 * <p>
 * Known Issues:
 *
 * <ul>
 * <li>This design uses an eager cache to load every unit in the cache on the
 * first reference to find() or add(). When the cache is large (10000 units), it
 * uses lots of heap and takes 5-10 seconds. Once the PersistentUnitCache is
 * created, it starts eagerly loading the cache in a background thread).</li>
 *
 * <li>Although units logged to disk with the same resource path are eventually
 * cleaned up, the most recently compiled unit stays in the cache forever. This
 * means that stale units that are no longer referenced will never be purged,
 * unless by some external action (e.g. ant clean).</li>
 *
 * <li>Unless ant builds are made aware of the cache directory, the cache will
 * persist if a user does an ant clean.</li>
 * </ul>
 *
 */
class PersistentUnitCache extends MemoryUnitCache {

  /**
   * If there are more than this many files in the cache, clean up the old
   * files.
   */
  static final int CACHE_FILE_THRESHOLD = 40;

  /**
   * There is no significance in the return value, we just want to be able
   * to tell if the purgeOldCacheFilesTask has completed.
   */
  private Future<?> purgeTaskStatus;
  private AtomicBoolean purgeInProgress = new AtomicBoolean(false);

  private final Runnable shutdownThreadTask = new Runnable() {
    @Override
    public void run() {
      logger.log(Type.TRACE, "Shutdown hook called for persistent unit cache");
      cacheDir.closeCurrentFile();
      logger.log(TreeLogger.TRACE, "Shutting down PersistentUnitCache thread");
      backgroundService.shutdownNow();
    }
  };

  /**
   * Saved to be able to wait for UNIT_MAP_LOAD_TASK to complete.
   */
  private Future<?> unitMapLoadStatus;

  /**
   * Used to execute the above Runnables in a background thread.
   */
  private ExecutorService backgroundService;

  private int addedSinceLastCleanup = 0;

  /**
   * A directory to store the cache files that should persist between
   * invocations.
   */
  private final PersistentUnitCacheDir cacheDir;
  private final TreeLogger logger;

  PersistentUnitCache(final TreeLogger logger, File parentDir) throws UnableToCompleteException {
    this.logger = logger;
    cacheDir = new PersistentUnitCacheDir(logger, parentDir);

    start();
  }

  private void start() {
    backgroundService = Executors.newSingleThreadExecutor();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          Future<Boolean> status = backgroundService.submit(shutdownThreadTask, Boolean.TRUE);
          // Don't let the shutdown hang more than 5 seconds
          status.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
          // ignore
        } catch (RejectedExecutionException e) {
          // already shutdown, ignore
        } catch (ExecutionException e) {
          logger.log(TreeLogger.ERROR, "Error during shutdown", e);
        } catch (TimeoutException e) {
          // ignore
        } finally {
          backgroundService.shutdownNow();
        }
      }
    });

    /**
     * Load up cached units from the persistent store in the background. The
     * {@link #add(CompilationUnit)} and {@link #find(String)} methods block if
     * invoked before this thread finishes.
     */
    unitMapLoadStatus = backgroundService.submit(new Runnable() {
      @Override
      public void run() {
        loadUnitMap();
      }
    });
  }

  /**
   * Enqueue a unit to be written by the background thread.
   */
  @Override
  public void add(CompilationUnit newUnit) {
    internalAdd(newUnit);
  }

  @VisibleForTesting
  Future<?> internalAdd(CompilationUnit newUnit) {
    awaitUnitCacheMapLoad();
    addedSinceLastCleanup++;
    super.add(newUnit);
    return addImpl(unitMap.get(newUnit.getResourcePath()));
  }

  @Override
  public void clear() throws UnableToCompleteException {
    super.clear();

    backgroundService.submit(new Runnable() {
      @Override
      public void run() {
        cacheDir.deleteClosedCacheFiles();
      }
    }, Boolean.TRUE);
    Futures.getUnchecked(backgroundService.submit(shutdownThreadTask));
    start();
  }

  /**
   * Cleans up old cache files in the directory, migrating everything previously
   * loaded in them to the current cache file.
   *
   * Normally, only newly compiled units are written to the current log, but
   * when it is time to cleanup, valid units from older log files need to be
   * re-written.
   */
  @Override
  public void cleanup(TreeLogger logger) {
    logger.log(Type.TRACE, "Cleanup called");
    awaitUnitCacheMapLoad();

    if (backgroundService.isShutdown()) {
      logger.log(TreeLogger.TRACE, "Skipped cleanup");
      return;
    }
    boolean shouldRotate = addedSinceLastCleanup > 0;
    logger.log(TreeLogger.TRACE, "Added " + addedSinceLastCleanup +
        " units to cache since last cleanup.");
    addedSinceLastCleanup = 0;
    try {
      List<File> cacheFiles = cacheDir.listCacheFilesToLoad();
      logger.log(TreeLogger.TRACE, cacheFiles.size() + " persistent unit files in directory");
      if (cacheFiles.size() < CACHE_FILE_THRESHOLD) {
        if (shouldRotate) {
          startRotating();
        }
        return;
      }

      // Check to see if the previous purge task finished.
      boolean inProgress = purgeInProgress.getAndSet(true);
      if (inProgress) {
        try {
          purgeTaskStatus.get(0, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
        } catch (TimeoutException ex) {
          // purge is currently in progress.
          return;
        }
      }

      logger.log(Type.TRACE, "Cleaning up persistent unit cache files");
      /*
       * Resend all units read in from the in-memory cache to the background
       * thread. They will be re-written out and the old cache files removed.
       */
      synchronized (unitMap) {
        for (UnitCacheEntry unitCacheEntry : unitMap.values()) {
          if (unitCacheEntry.getOrigin() == UnitOrigin.ARCHIVE) {
            // Units from GWTAR archives should not be kept in the persistent unit cache on disk
            // because they are already being kept in their original GWTAR file location.
            continue;
          }
          addImpl(unitCacheEntry);
        }
      }

      purgeTaskStatus = backgroundService.submit(new Runnable() {
        @Override
        public void run() {
          try {
            cacheDir.deleteClosedCacheFiles();
            cacheDir.rotate();
          } catch (UnableToCompleteException e) {
            backgroundService.shutdownNow();
          } finally {
            purgeInProgress.set(false);
          }
        }
      }, Boolean.TRUE);

    } catch (ExecutionException ex) {
      throw new InternalCompilerException("Error purging cache", ex);
    } catch (RejectedExecutionException ex) {
      // Cache background thread is not running - ignore
    }
  }

  @VisibleForTesting
  Future<?> startRotating() {
    return backgroundService.submit(new Runnable() {
      @Override
      public void run() {
        try {
          cacheDir.rotate();
        } catch (UnableToCompleteException e) {
          backgroundService.shutdownNow();
        }
      }
    });
  }

  @Override
  public CompilationUnit find(ContentId contentId) {
    awaitUnitCacheMapLoad();
    return super.find(contentId);
  }

  @Override
  public CompilationUnit find(String resourcePath) {
    awaitUnitCacheMapLoad();
    return super.find(resourcePath);
  }

  /**
   * For Unit testing - shutdown the persistent cache.
   *
   * @throws ExecutionException
   * @throws InterruptedException
   */
  void shutdown() throws InterruptedException, ExecutionException {
    logger.log(Type.INFO, "shutdown called");
    try {
      Future<?> future = backgroundService.submit(shutdownThreadTask);
      backgroundService.shutdown();
      future.get();
    } catch (RejectedExecutionException ex) {
      // background thread is not running - ignore
    }
  }

  private Future<?> addImpl(final UnitCacheEntry entry) {
    try {
      return backgroundService.submit(new Runnable() {
        @Override
        public void run() {
          try {
            cacheDir.writeObject(entry);
          } catch (UnableToCompleteException e) {
            backgroundService.shutdownNow();
          }
        }
      });
    } catch (RejectedExecutionException ex) {
      // background thread is not running, ignore
      return null;
    }
  }

  private synchronized void awaitUnitCacheMapLoad() {
    // wait on initial load of unit map to complete.
    try {
      if (unitMapLoadStatus != null) {
        unitMapLoadStatus.get();
        // no need to check any more.
        unitMapLoadStatus = null;
      }
    } catch (InterruptedException e) {
      throw new InternalCompilerException("Interrupted waiting for unit cache map to load.", e);
    } catch (ExecutionException e) {
      logger.log(TreeLogger.ERROR, "Failure in unit cache map load.", e);
      // keep going
      unitMapLoadStatus = null;
    }
  }

  /**
   * Load everything cached on disk into memory.
   */
  private void loadUnitMap() {
    Event loadPersistentUnitEvent =
        SpeedTracerLogger.start(DevModeEventType.LOAD_PERSISTENT_UNIT_CACHE);
    if (logger.isLoggable(TreeLogger.TRACE)) {
      logger.log(TreeLogger.TRACE, "Looking for previously cached Compilation Units in "
          + cacheDir.getPath());
    }
    try {
      List<File> files = cacheDir.listCacheFilesToLoad();
      for (File cacheFile : files) {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        ObjectInputStream inputStream = null;
        boolean deleteCacheFile = false;
        try {
          fis = new FileInputStream(cacheFile);
          bis = new BufferedInputStream(fis);
          /*
           * It is possible for the next call to throw an exception, leaving
           * inputStream null and fis still live.
           */
          inputStream = new StringInterningObjectInputStream(bis);
          while (true) {
            CachedCompilationUnit unit = (CachedCompilationUnit) inputStream.readObject();
            if (unit == null) {
              break;
            }
            if (unit.getTypesSerializedVersion() != GwtAstBuilder.getSerializationVersion()) {
              continue;
            }
            UnitCacheEntry entry = new UnitCacheEntry(unit, UnitOrigin.PERSISTENT);
            UnitCacheEntry existingEntry = unitMap.get(unit.getResourcePath());
            /*
             * Don't assume that an existing entry is stale - an entry might
             * have been loaded already from another source like a
             * CompilationUnitArchive that is more up to date. If the
             * timestamps are the same, accept the latest version. If it turns
             * out to be stale, it will be recompiled and the updated unit
             * will win this test the next time the session starts.
             */
            if (existingEntry != null
                && unit.getLastModified() >= existingEntry.getUnit().getLastModified()) {
              super.remove(existingEntry.getUnit());
              unitMap.put(unit.getResourcePath(), entry);
              unitMapByContentId.put(unit.getContentId(), entry);
            } else if (existingEntry == null) {
              unitMap.put(unit.getResourcePath(), entry);
              unitMapByContentId.put(unit.getContentId(), entry);
            }
          }
        } catch (EOFException ex) {
          // Go on to the next file.
        } catch (IOException ex) {
          deleteCacheFile = true;
          if (logger.isLoggable(TreeLogger.TRACE)) {
            logger.log(TreeLogger.TRACE, "Ignoring and deleting cache log "
                + cacheFile.getAbsolutePath() + " due to read error.", ex);
          }
        } catch (ClassNotFoundException ex) {
          deleteCacheFile = true;
          if (logger.isLoggable(TreeLogger.TRACE)) {
            logger.log(TreeLogger.TRACE, "Ignoring and deleting cache log "
                + cacheFile.getAbsolutePath() + " due to deserialization error.", ex);
          }
        } finally {
          Utility.close(inputStream);
          Utility.close(bis);
          Utility.close(fis);
        }
        if (deleteCacheFile) {
          logger.log(Type.WARN, "Deleting " + cacheFile + " due to an exception");
          cacheDir.deleteUnlessOpen(cacheFile);
        } else {
          if (logger.isLoggable(TreeLogger.TRACE)) {
            logger.log(TreeLogger.TRACE, cacheFile.getName() + ": Load complete");
          }
        }
      }
    } finally {
      loadPersistentUnitEvent.end();
    }
  }
}
