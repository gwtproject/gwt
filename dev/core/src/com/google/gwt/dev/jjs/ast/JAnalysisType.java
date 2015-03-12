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
package com.google.gwt.dev.jjs.ast;

/**
 * A type containing more precise information about the actual types that can populate an object
 * with this type.
 */
public class JAnalysisType extends JReferenceType {
  /**
   * Models the results of type analysis:
   * <ul>
   *   <li> EXACT:     Means that the analysis determined the underlying type (in {@code ref}) is
   *                   the exact type that can inhabit an object typed as such; not a subclass and
   *                   not {@code null}.
   *   </li>
   *   <li> NOT_NULL:  Means that the analysis determined the underlying type (in {@code ref}) or
   *                   any of its subtypes can inhabit an object typed as such but not {@code null}.
   *   </li>
   *</ul>
   * TODO(rluble): add EXACT_OR_NULL to model the other options and abstract the combination
   * operations to this class.
   */

  private enum AnalysisResult {
    NOT_NULL() {

      @Override
      boolean canBeSubclass() {
        return true;
      }

      @Override
      boolean canBeNull() {
        return false;
      }
    },
    EXACT() {

      @Override
      boolean canBeSubclass() {
        return false;
      }

      @Override
      boolean canBeNull() {
        return false;
      }
    };

    abstract boolean canBeSubclass();
    abstract boolean canBeNull();
  };

  /**
   * Each {@link JReferenceType} has access to the corresponding singletons (one per type of
   * analysis result).
   */
  static class AnalysisTypeSingletonsForType {
    private final JAnalysisType[] analysisTypeSingletons =
        new JAnalysisType[AnalysisResult.values().length];
    public JAnalysisType getNonNull(JReferenceType type) {
      return getAnalysisType(AnalysisResult.NOT_NULL, type);
    }
    public JAnalysisType getExact(JReferenceType type) {
      return getAnalysisType(AnalysisResult.EXACT, type);
    }
    public JAnalysisType getAnalysisType(AnalysisResult analysisResult, JReferenceType type) {
      assert !(type instanceof JAnalysisType);
      if (analysisTypeSingletons[analysisResult.ordinal()] == null) {
        analysisTypeSingletons[analysisResult.ordinal()] = new JAnalysisType(type, analysisResult);
      }
      return analysisTypeSingletons[analysisResult.ordinal()];
    }
  }

  private final AnalysisResult analysisResult;
  private final JReferenceType ref;

  private JAnalysisType(JReferenceType ref, AnalysisResult analysisResult) {
    super(ref.getSourceInfo(), ref.getName());
    assert !(ref instanceof JNullType);
    this.ref = ref.getUnderlyingType();
    this.analysisResult = analysisResult;
  }

  @Override
  public boolean canBeNull() {
    return analysisResult.canBeNull();
  }

  @Override
  public boolean canBeASubclass() {
    return analysisResult.canBeSubclass();
  }

  @Override
  public String getJavahSignatureName() {
    return ref.getJavahSignatureName();
  }

  @Override
  public String getJsniSignatureName() {
    return ref.getJsniSignatureName();
  }

  @Override
  public JEnumType isEnumOrSubclass() {
    return ref.isEnumOrSubclass();
  }

  @Override
  public JAnalysisType getNonNull() {
    return this;
  }

  @Override
  public JAnalysisType getExact() {
    return getAnalysisTypeSet().getExact(ref);
  }

  @Override
  public JReferenceType getUnderlyingType() {
    return ref;
  }

  @Override
  public boolean isAbstract() {
    return ref.isAbstract();
  }

  @Override
  public boolean isExternal() {
    return ref.isExternal();
  }

  @Override
  public boolean isFinal() {
    return ref.isFinal();
  }

  @Override
  public void traverse(JVisitor visitor, Context ctx) {
    visitor.accept(ref);
  }

  private Object readResolve() {
    // Reuse the instance stored in the ref type to make sure there is only one analysis result
    // per type.
    return ref.getAnalysisTypeSet().getAnalysisType(analysisResult, ref);
  }
}
