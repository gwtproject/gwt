package com.google.gwt.dev.codeserver;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.dev.codeserver.Job.Result;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableList;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableList.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Executes requests to compile modules using Super Dev Mode.
 *
 * <p>Guarantees that only one thread invokes the GWT compiler at a time and reports
 * progress on waiting jobs.
 */
public class JobRunner {
  private final Modules modules;
  private final TreeLogger logger;

  /**
   * The waiting jobs that will be run in the order submitted.
   */
  private final BlockingDeque<Job> queue = new LinkedBlockingDeque<Job>();

  /**
   * The currently running job or null if idle.
   * The job will be in the "in-progress" state or finished.
   */
  private final AtomicReference<Job> running = new AtomicReference<Job>();

  JobRunner(final Modules modules, final TreeLogger logger) {
    this.modules = modules;
    this.logger = logger;
    startWorkerThread();
  }

  /**
   * Submits a job to be executed. (Returns immediately.)
   */
  void submit(Job job) {
    if (job.wasSubmitted()) {
      throw new IllegalStateException("job already started: " + job.getId());
    }
    job.onSubmitted();
    queue.add(job);
  }

  /**
   * Returns the progress of the currently running job, or {@link Progress#IDLE}
   * if nothing is running.
   */
  Progress getRunningJobProgress() {
    Job job = running.get();
    if (job == null) {
      return Progress.IDLE;
    }
    return job.getProgress();
  }

  /**
   * Returns the jobs running or waiting on the queue.
   * (The running job is first.)
   * TODO: hook this up.
   */
  ImmutableList<Progress> getSnapshot() {
    List<Job> jobs = new ArrayList<Job>(queue);
    Job first = running.get();

    // Fix up (unlikely) race condition: if jobs execute quickly, we might see the
    // same job both waiting and running. Anything ahead of it has already finished.
    while (jobs.contains(first)) {
      Job job = jobs.remove(0);
      if (job != first) {
        assert job.getFutureResult().isDone();
      }
    }

    jobs.add(0, first);

    Builder<Progress> builder = ImmutableList.builder();
    for (Job job : jobs) {
      Progress progress = job.getProgress();
      if (progress != Progress.IDLE) {
        builder.add(progress);
      }
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

      // Wait until someone gives us a job.
      Job job = queue.takeFirst();

      // Report this job as running.
      // (However, its progress is still set to waiting until the compiler makes its first
      // progress report.)
      running.set(job);

      recompile(job);

      // Report that we are idle if it looks like we will block.
      // (Otherwise, transition directly to the next job to avoid reporting idle.)
      if (queue.isEmpty()) {
        running.set(null);
      }
    }
  }

  private void recompile(Job job) {

    ModuleState module = modules.get(job.getModuleName());
    if (module == null) {
      String msg = "JobRunner: skipped job with unknown module: " + job.getModuleName();
      logger.log(Type.WARN, msg);
      job.onFinished(new Result(null,  new RuntimeException(msg)));
      return;
    }

    module.recompile(job);
  }
}
