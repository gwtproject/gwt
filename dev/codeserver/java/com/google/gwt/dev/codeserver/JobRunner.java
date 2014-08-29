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
import com.google.gwt.dev.codeserver.Job.Result;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableList;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableList.Builder;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Executes requests to compile modules using Super Dev Mode.
 *
 * <p>Guarantees that only one thread invokes the GWT compiler at a time and reports
 * progress on waiting jobs.
 *
 * <p>JobRunners are thread-safe.
 */
public class JobRunner {
  private final Modules modules;
  private final TreeLogger logger;

  /**
   * The waiting jobs that will be run in the order submitted.
   * (Must be accessed within a synchronized block.)
   */
  private final Queue<Job> queue = new LinkedList<Job>();

  /**
   * The currently running job or null if idle.
   * The job will be in the "in-progress" state or finished.
   * (Must be accessed using a synchronized block.)
   */
  private Job running = null;

  JobRunner(final Modules modules, final TreeLogger logger) {
    this.modules = modules;
    this.logger = logger;
    startWorkerThread();
  }

  /**
   * Submits a job to be executed. (Returns immediately.)
   */
  synchronized void submit(Job job) {
    if (job.wasSubmitted()) {
      throw new IllegalStateException("job already started: " + job.getId());
    }
    job.onSubmitted();
    queue.add(job);
    notifyAll();
    job.getLogger().log(Type.TRACE, "added job to queue");
  }

  /**
   * Returns the progress of the currently running job, or {@link Progress#IDLE}
   * if nothing is running.
   */
  synchronized Progress getRunningJobProgress() {
    if (running == null) {
      return Progress.IDLE;
    }
    return running.getProgress();
  }

  /**
   * Returns the jobs running or waiting on the queue in the order they will be
   * executed. (The running job is first.)
   * TODO: hook this up.
   */
  synchronized ImmutableList<Progress> getSnapshot() {
    Builder<Progress> builder = ImmutableList.builder();
    if (running != null) {
      builder.add(running.getProgress());
    }
    for (Job waiting : queue) {
      builder.add(waiting.getProgress());
    }
    return builder.build();
  }

  private void startWorkerThread() {
    Runnable runner = new Runnable() {
      @Override
      public void run() {
        try {
          runEachJob();
        } catch (InterruptedException e) {
          logger.log(Type.WARN, "JobRunner thread interrupted");
        } finally {
          logger.log(Type.WARN, "JobRunner thread exiting");
        }
      }
    };
    Thread thread = new Thread(runner, "Super Dev Mode: JobRunner");
    thread.setDaemon(true);
    thread.start();
  }

  /**
   * Calls the GWT compiler for each job submitted to the queue.
   */
  @SuppressWarnings("InfiniteLoopStatement")
  private void runEachJob() throws InterruptedException {
    while (true) {
      Job job = waitForJob();
      job.getLogger().log(Type.INFO, "removed job from queue: " + job.getId());
      recompile(job, modules);
    }
  }

  /**
   * Blocks until we have a job. Then moves the job to "running" and returns it.
   *
   * (The running Job will be set to null while waiting.)
   */
  private synchronized Job waitForJob() throws InterruptedException {
    running = null;
    while (queue.isEmpty()) {
      wait();
    }
    running = queue.remove();
    return running;
  }

  private static void recompile(Job job, Modules modules) {
    ModuleState module = modules.get(job.getModuleName());
    if (module == null) {
      String msg = "skipped a compile job with an unknown module: " + job.getModuleName();
      job.getLogger().log(Type.WARN, msg);
      job.onFinished(new Result(null,  new RuntimeException(msg)));
      return;
    }

    module.recompile(job);
  }
}
