/*
 * Copyright 2010 Google Inc.
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

package com.google.gwt.core.ext.linker;

import com.google.gwt.core.ext.Linker;
import com.google.gwt.core.linker.SoycReportLinker;
import com.google.gwt.dev.js.SizeBreakdown;
import com.google.gwt.dev.util.Util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Captures some metrics from the compile permutations step of the build.
 */
@Transferable
public class CompilationMetricsArtifact extends Artifact<CompilationMetricsArtifact> {

  private long compileElapsedMilliseconds;
  private long elapsedMilliseconds;
  private int permutationId;
  private String permutationDescription;
  private int[] jsSize;

  public CompilationMetricsArtifact(int permutationId) { 
    this(SoycReportLinker.class, permutationId);
  }
  
  protected CompilationMetricsArtifact(Class<? extends Linker> linker, int permutationId) {
    super(linker);
    this.permutationId = permutationId;
  }

  /**
   * @return wall clock time elapsed since start of compilation
   */
  public long getCompileElapsedMilliseconds() {
    return compileElapsedMilliseconds;
  }

  /**
   * @return wall clock time elapsed since start of execution
   */
  public long getElapsedMilliseconds() {
    return elapsedMilliseconds;
  }

  /**
   * @return a map containing the name of the JavaScript fragments and their
   *         sizes.
   */
  public int[] getJsSize() {
    return this.jsSize;
  }

  /**
   * @return the permutation id associated with compiling this permutation.
   */
  public int getPermuationId() {
    return permutationId;
  }

  /**
   * @return human readable description of the permutation
   */
  public String getPermutationDescription() {
    return permutationDescription;
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  /**
   * @param compileElapsedMilliseconds wall clock time elapsed since start of
   *        compilation
   */
  public CompilationMetricsArtifact setCompileElapsedMilliseconds(long compileElapsedMilliseconds) {
    this.compileElapsedMilliseconds = compileElapsedMilliseconds;
    return this;
  }

  /**
   * @param elapsedMilliseconds wall clock time elapsed since JVM startup
   */
  public CompilationMetricsArtifact setElapsedMilliseconds(long elapsedMilliseconds) {
    this.elapsedMilliseconds = elapsedMilliseconds;
    return this;
  }

  /**
   * @param sizeBreakdowns breakdown of sizes in JavaScript fragments
   */
  public void setJsSize(SizeBreakdown[] sizeBreakdowns) {
    this.jsSize = new int[sizeBreakdowns.length];
    for (int i = 0; i < sizeBreakdowns.length; ++i) {
      this.jsSize[i] = sizeBreakdowns[i].getSize();
    }
  }

  /**
   * @param permutationDescription human readable description of the permutation
   */
  public CompilationMetricsArtifact setPermutationDescription(String permutationDescription) {
    this.permutationDescription = permutationDescription;
    return this;
  }

  @Override
  protected int compareToComparableArtifact(CompilationMetricsArtifact o) {
    return getName().compareTo(o.getName());
  }

  @Override
  protected final Class<CompilationMetricsArtifact> getComparableArtifactType() {
    return CompilationMetricsArtifact.class;
  }

  private String getName() {
    return "CompilationMetricsArtifact-" + permutationId;
  }
  /**
   * Empty constructor for externalization.
   */
  public CompilationMetricsArtifact() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeLong(compileElapsedMilliseconds);
    out.writeInt(permutationId);
    Util.serializeString(permutationDescription, out);
    out.writeInt(jsSize.length);
    for (int sz : jsSize) {
      out.writeInt(sz);
    }
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    compileElapsedMilliseconds = in.readLong();
    permutationId = in.readInt();
    permutationDescription = Util.deserializeString(in);
    int sz = in.readInt();
    jsSize = new int[sz];
    for (int i = 0; i < sz; i++) {
      jsSize[i] = in.readInt();
    }
  }
}
