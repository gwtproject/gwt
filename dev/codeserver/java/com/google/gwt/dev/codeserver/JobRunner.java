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
package com.google.gwt.dev.codeserver;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.MinimalRebuildCacheManager;
import com.google.gwt.dev.javac.UnitCacheSingleton;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executes requests to compile modules using Super Dev Mode.
 *
 * <p>Guarantees that only one thread invokes the GWT compiler at a time and reports
 * progress on waiting jobs.
 *
 * <p>JobRunners are thread-safe.
 */
public class JobRunner {

  private final JobEventTable table;
  private final OutboxTable outboxes;
  private final File baseCacheDir;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  JobRunner(JobEventTable table, OutboxTable outboxes, File baseCacheDir) {
    this.table = table;
    this.outboxes = outboxes;
    this.baseCacheDir = baseCacheDir;
  }

  /**
   * Schedules a cleaner job and then waits.
   */
  void clean(TreeLogger logger) throws CleanFailedException {
    // Will clean after all scheduled work is completed. Relies on executor single-threadedness.
    try {
      executor.submit(new CleanerJob()).get();
      logger.log(TreeLogger.INFO, "Cleaned disk caches.");
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      String failureMessage = null;

      if (cause instanceof IOException || cause instanceof UnableToCompleteException) {
        failureMessage = "Failed to clean disk caches.";
      }

      logger.log(TreeLogger.WARN, failureMessage);
      throw new CleanFailedException(failureMessage, cause);
    } catch (InterruptedException e) {
      logger.log(TreeLogger.WARN, "Shutdown was requested while waiting for clean to complete.");
      throw new CleanFailedException("Shutdown was requested while waiting for clean to complete.",
          e);
    }
  }

  /**
   * Submits a job to be executed. (Returns immediately.)
   */
  synchronized void submit(final Job job) {
    if (table.wasSubmitted(job)) {
      throw new IllegalStateException("job already submitted: " + job.getId());
    }
    job.onSubmitted(table);
    executor.submit(new Runnable() {
      @Override
      public void run() {
        recompile(job, outboxes);
      }
    });
    job.getLogger().log(Type.TRACE, "added job to queue");
  }

  private static void recompile(Job job, OutboxTable outboxes) {
    job.getLogger().log(Type.INFO, "starting job: " + job.getId());
    job.getOutbox().recompile(job);
  }

  /**
   * An exception that indicates cache cleaning failure.
   */
  public static class CleanFailedException extends Exception {
    public CleanFailedException(String message, Throwable throwable) {
      super(message, throwable);
    }
  }

  /**
   * A runnable for clearing both unit and minimalRebuild caches.
   * <p>
   * By packaging it as a runnable as running it in the ExecutorService any danger of clearing
   * caches at the same time as an active compile job is avoided.
   */
  private class CleanerJob implements Callable<Void> {
    @Override
    public Void call() throws Exception {
      MinimalRebuildCacheManager.deleteCaches(baseCacheDir);
      UnitCacheSingleton.clearCache();
      return null;
    }
  }
}
