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
import com.google.gwt.dev.codeserver.Job.Result;

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
  private final ProgressTable table;
  private final Modules modules;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  JobRunner(ProgressTable table, Modules modules) {
    this.table = table;
    this.modules = modules;
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
        recompile(job, modules);
      }
    });
    job.getLogger().log(Type.TRACE, "added job to queue");
  }

  private static void recompile(Job job, Modules modules) {
    job.getLogger().log(Type.INFO, "starting job: " + job.getId());
    ModuleState m = modules.get(job.getModuleName());
    if (m == null) {
      String msg = "skipped a compile job with an unknown module: " + job.getModuleName();
      job.getLogger().log(Type.WARN, msg);
      job.onFinished(new Result(job, null,  new RuntimeException(msg)));
      return;
    }

    m.recompile(job);
  }
}
