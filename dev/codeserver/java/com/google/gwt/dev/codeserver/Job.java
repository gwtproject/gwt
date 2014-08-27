package com.google.gwt.dev.codeserver;

import com.google.gwt.dev.codeserver.Progress.Waiting;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableMap;
import com.google.gwt.thirdparty.guava.common.util.concurrent.Futures;
import com.google.gwt.thirdparty.guava.common.util.concurrent.ListenableFuture;
import com.google.gwt.thirdparty.guava.common.util.concurrent.SettableFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A request for Super Dev Mode to compile something, and its current state.
 *
 * <p>Jobs have three states: not submitted, in-progress, and done. (In-progress
 * includes both waiting in the queue and compiling.)
 *
 * <p>Jobs are thread-safe.
 */
class Job {
  private static final ConcurrentMap<String, AtomicInteger> moduleToNextId =
      new ConcurrentHashMap<String, AtomicInteger>();

  private final String id;

  // Input

  private final String moduleName;

  private final ImmutableMap<String, String> bindingProperties;

  // Output

  private final AtomicReference<Progress> progress = new AtomicReference<Progress>(Progress.IDLE);

  private final SettableFuture<Result> result = SettableFuture.create();

  /**
   * Creates a job to recompile a module.
   * @param moduleName The name of the module to recompile, suitable for
   *     passing to {@link Modules#get}.
   * @param bindingProperties  Properties that uniquely identify a permutation.
   *     (Otherwise, more than one permutation will be compiled.)
   */
  Job(String moduleName, Map<String, String> bindingProperties) {
    this.id = chooseNextId(moduleName);
    this.moduleName = moduleName;
    this.bindingProperties = ImmutableMap.copyOf(bindingProperties);
  }

  private static String chooseNextId(String moduleName) {
    moduleToNextId.putIfAbsent(moduleName, new AtomicInteger(0));
    return moduleName + "-" + moduleToNextId.get(moduleName).getAndIncrement();
  }

  /**
   * A string uniquely identifying this job (within this process).
   *
   * <p>Note that the number doesn't have any particular relationship
   * with the output directory's name since jobs can be submitted out of order.
   */
  String getId() {
    return id;
  }

  /**
   * The module to compile.
   */
  String getModuleName() {
    return moduleName;
  }

  /**
   * The binding properties to use for this recompile.
   */
  ImmutableMap<String, String> getBindingProperties() {
    return bindingProperties;
  }

  /**
   * Returns the current status of this job, without blocking.
   *
   * <p>Note that {@link Progress#IDLE} could mean either not started or already finished.
   * (Use getResultFuture().isDone() to distinguish them.)
   */
  Progress getProgress() {
    return progress.get();
  }

  /**
   * Blocks until we have the result of this recompile.
   */
  Result waitForResult() {
    return Futures.getUnchecked(getFutureResult());
  }

  /**
   * Returns a Future that will contain the result of this recompile.
   */
  ListenableFuture<Result> getFutureResult() {
    return result;
  }

  /**
   * Returns true if this job has been submitted to the JobRunner.
   * (That is, if {@link #onSubmitted} has ever been called.)
   */
  synchronized boolean wasSubmitted() {
    return progress.get() != Progress.IDLE || result.isDone();
  }

  /**
   * Returns true if {@link #onSubmitted} was called but {@link #onFinished} has not
   * been called.
   */
  synchronized boolean isInProgress() {
    return progress.get() != Progress.IDLE;
  }

  // === state transitions ===

  /**
   * Reports that this job has been submitted to the JobRunner.
   * @throws IllegalStateException if the job was already started.
   */
  synchronized void onSubmitted() {
    if (wasSubmitted()) {
      throw new IllegalStateException("Job has already started: " + id);
    }
    progress.set(new Waiting(this));
  }

  /**
   * Reports that this job has made progress.
   * @throws IllegalStateException if the job is not running.
   */
  synchronized void onCompilerProgress(Progress.Compiling newProgress) {
    if (!isInProgress()) {
      throw new IllegalStateException("Job is not running: " + id);
    }
    progress.set(newProgress);
  }

  /**
   * Reports that this job has finished.
   * @throws IllegalStateException if the job is not running.
   */
  synchronized void onFinished(Result newResult) {
    if (!isInProgress()) {
      throw new IllegalStateException("Job is not running: " + id);
    }
    progress.set(Progress.IDLE);
    result.set(newResult);
  }

  /**
   * The result of a recompile.
   */
  static class Result {

    /** non-null if successful */
    final CompileDir outputDir;

    /** non-null for an error */
    final Throwable error;

    Result(CompileDir outputDir, Throwable error) {
      assert (outputDir == null) != (error == null);
      this.outputDir = outputDir;
      this.error = error;
    }

    boolean isOk() {
      return error == null;
    }
  }
}
