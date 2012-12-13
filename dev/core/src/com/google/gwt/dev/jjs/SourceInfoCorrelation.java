/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.dev.jjs;

import com.google.gwt.dev.jjs.Correlation.Axis;
import com.google.gwt.dev.jjs.CorrelationFactory.RealCorrelationFactory;
import com.google.gwt.dev.util.Util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Tracks file and line information for AST nodes.
 * 
 * TODO: make this package-protected?
 */
public class SourceInfoCorrelation implements SourceInfo {

  private static final int NUM_AXES = Axis.values().length;

  /**
   * Holds the origin data for the SourceInfo.
   */
  private SourceOrigin origin;

  /**
   * My parent node, or <code>null</code> if I have no parent.
   */
  private SourceInfoCorrelation parent;

  /**
   * Records the first Correlation on any given Axis applied to the SourceInfo.
   * Each index of this array corresponds to the Correlation.Axis with the same
   * ordinal().
   */
  private Correlation[] primaryCorrelations = null;

  public SourceInfoCorrelation(SourceOrigin origin) {
    this.origin = origin;
    this.parent = null;
  }

  public SourceInfoCorrelation(SourceInfoCorrelation parent, SourceOrigin origin) {
    this.origin = origin;
    this.parent = parent;
  }

  /**
   * Add a Correlation to the SourceInfo.
   */
  public void addCorrelation(Correlation c) {
    if (primaryCorrelations == null) {
      primaryCorrelations = new Correlation[NUM_AXES];
    }
    int index = c.getAxis().ordinal();
    primaryCorrelations[index] = c;
  }

  public Correlation getCorrelation(Axis axis) {
    if (primaryCorrelations != null) {
      Correlation c = primaryCorrelations[axis.ordinal()];
      if (c != null) {
        return c;
      }
    }
    if (parent != null) {
      return parent.getCorrelation(axis);
    }
    return null;
  }

  public Correlation[] getCorrelations() {
    if (parent == null) {
      if (primaryCorrelations == null) {
        return new Correlation[NUM_AXES];
      } else {
        return primaryCorrelations.clone();
      }
    } else {
      Correlation[] result = parent.getCorrelations();
      if (primaryCorrelations != null) {
        for (int i = 0; i < NUM_AXES; ++i) {
          Correlation c = primaryCorrelations[i];
          if (c != null) {
            result[i] = c;
          }
        }
      }
      return result;
    }
  }

  public CorrelationFactory getCorrelator() {
    return RealCorrelationFactory.INSTANCE;
  }

  public int getEndPos() {
    return getOrigin().getEndPos();
  }

  public String getFileName() {
    return getOrigin().getFileName();
  }

  public SourceOrigin getOrigin() {
    return origin;
  }

  public int getStartLine() {
    return getOrigin().getStartLine();
  }

  public int getStartPos() {
    return getOrigin().getStartPos();
  }

  public SourceInfo makeChild() {
    return new SourceInfoCorrelation(this, this.origin);
  }

  public SourceInfo makeChild(SourceOrigin origin) {
    return new SourceInfoCorrelation(this, origin);
  }

  @Override
  public String toString() {
    return origin.toString();
  }

  /**
   * Empty constructor for externalization.
   */
  public SourceInfoCorrelation() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(origin);
    out.writeObject(parent);
    Util.serializeArray(primaryCorrelations, out);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    origin = (SourceOrigin) in.readObject();
    parent = (SourceInfoCorrelation) in.readObject();
    primaryCorrelations = Util.deserializeObjectArray(in, Correlation.class);
  }
}
