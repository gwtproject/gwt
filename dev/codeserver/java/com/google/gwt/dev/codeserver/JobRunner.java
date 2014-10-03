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

import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.MinimalRebuildCacheManager;
import com.google.gwt.dev.javac.UnitCacheSingleton;

import java.io.IOException;
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

  private class CleanerRunnable implements Runnable {

    private final String moduleName;
    private Exception exception;

    public CleanerRunnable(String moduleName) {
      this.moduleName = moduleName;
    }

    @Override
    public void run() {
      try {
        MinimalRebuildCacheManager.deleteCaches(moduleName);
        UnitCacheSingleton.clearCache();
      } catch (IOException e) {
        exception = e;
      } catch (UnableToCompleteException e) {
        exception = e;
      }
    }
  }

  private final JobEventTable table;
  private final OutboxTable outboxes;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  JobRunner(JobEventTable table, OutboxTable outboxes) {
    this.table = table;
    this.outboxes = outboxes;
  }

  /**
   * Schedules a cleaner job and then waits.
   */
  void clean(final String moduleName) throws IOException, UnableToCompleteException,
      InterruptedException {
    CleanerRunnable cleanerRunnable = new CleanerRunnable(moduleName);

    // Will clean after all scheduled work is completed. Relies on executor single-threadedness.
    executor.submit(cleanerRunnable).wait();

    if (cleanerRunnable.exception instanceof IOException) {
      throw new IOException(cleanerRunnable.exception);
    } else if (cleanerRunnable.exception instanceof UnableToCompleteException) {
      throw new UnableToCompleteException();
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
}
