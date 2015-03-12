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

import com.google.gwt.dev.jjs.SourceInfo;

/**
 * Base class for any reference type.
 */
public abstract class JReferenceType extends JType implements CanBeAbstract {

  private transient AnalysisDecoratedTypeSingletons analysisDecoratedTypeSingletons = null;
  /**
   * A reference type decorated with the result of static analysis. Only two analysis properties
   * are computed (mostly during type propagation in TypeTightener: nullness and exactness.
   */
  private static class JAnalysisDecoratedType extends JReferenceType {


    private final boolean canBeSubclass;
    private final boolean canBeNull;
    private final JReferenceType ref;

    private JAnalysisDecoratedType(JReferenceType ref, boolean canBeSubclass, boolean canBeNull) {
      super(ref.getSourceInfo(), ref.getName());
      assert ref.getUnderlyingType().canBeSubclass() != canBeSubclass ||
          ref.getUnderlyingType().canBeNull() != canBeNull : "An analysis type for " + ref +
          " should not have been constructed as it is equivalent to the original type";
      assert !(ref instanceof JNullType);
      this.ref = ref.getUnderlyingType();
      this.canBeSubclass = canBeSubclass;
      this.canBeNull = canBeNull;
    }

    @Override
    public boolean canBeNull() {
      return canBeNull;
    }

    @Override
    public boolean canBeSubclass() {
      return canBeSubclass;
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
      return ref.getAnalysisDecoratedTypeSingletons().getAnalysisDecoratedType(ref, ref.canBeNull(),
          ref.canBeSubclass());
    }

    @Override
    public String toString() {
      return (!canBeSubclass ? "(exact)" : "") + (!canBeNull ? "(non-null)" : "") +
          super.toString();
    }
  }

  /**
   * Each {@link JReferenceType} has access to the corresponding singletons (one per type of
   * analysis result).
   */
  static class AnalysisDecoratedTypeSingletons {
    private static final int NUM_PROPERTIES = 2;
    private final JAnalysisDecoratedType[] decoratedAnalysisTypeSingletons =
        new JAnalysisDecoratedType[2 * NUM_PROPERTIES];

    private int propertyIndex(boolean canBeNull, boolean canBeSubclass) {
      return (canBeNull ? 0 : 1) + (canBeSubclass ? 0 : 2);
    }
    public JReferenceType getAnalysisDecoratedType(
        JReferenceType type, boolean canBeNull, boolean canBeSubclass) {
      JReferenceType underlyingType = type.getUnderlyingType();
      if (underlyingType.canBeNull() == canBeNull &&
          underlyingType.canBeSubclass() == canBeSubclass) {
        return underlyingType;
      }
      int index = propertyIndex(canBeNull, canBeSubclass);
      JAnalysisDecoratedType result = decoratedAnalysisTypeSingletons[index];
      if (result == null) {
        result = decoratedAnalysisTypeSingletons[index] =
            new JAnalysisDecoratedType(type.getUnderlyingType(), canBeSubclass, canBeNull);
      }
      return result;
    }
  }


  public JReferenceType(SourceInfo info, String name) {
    super(info, name);
  }

  JReferenceType(SourceInfo info, String name,
      AnalysisDecoratedTypeSingletons analysisDecoratedTypeSingletons) {
    super(info, name);
    this.analysisDecoratedTypeSingletons = analysisDecoratedTypeSingletons;
  }
  @Override
  public boolean canBeNull() {
    return true;
  }

  @Override
  public boolean canBeSubclass() {
    return !isFinal();
  }

  @Override
  public final JLiteral getDefaultValue() {
    return JNullLiteral.INSTANCE;
  }

  @Override
  public String getJavahSignatureName() {
    return "L" + name.replaceAll("_", "_1").replace('.', '_') + "_2";
  }

  @Override
  public String getJsniSignatureName() {
    return "L" + name.replace('.', '/') + ';';
  }

  public JReferenceType weakenToNullable() {
    return weakenToNullable(this);
  }

  public JReferenceType weakenToNonExact() {
    return weakenToNonExact(this);
  }

  public JReferenceType strengthenToNonNull() {
    return strengthenToNonNull(this);
  }

  public JReferenceType stengthenToExact() {
    return stengthenToExact(this);
  }

  /**
   * If this type is a non-null type, returns the underlying (original) type.
   */
  public JReferenceType getUnderlyingType() {
    return this;
  }

  @Override
  public boolean replaces(JType originalType) {
    return super.replaces(originalType)
        && canBeNull() == ((JReferenceType) originalType).canBeNull();
  }

  protected AnalysisDecoratedTypeSingletons getAnalysisDecoratedTypeSingletons() {
    if (analysisDecoratedTypeSingletons == null) {
      analysisDecoratedTypeSingletons = new AnalysisDecoratedTypeSingletons();
    }
    return analysisDecoratedTypeSingletons;
  }

  private static JReferenceType weakenToNonExact(JReferenceType type) {
    if (type.canBeSubclass() || !type.getUnderlyingType().canBeSubclass()) {
      // If the underlying type is already exact (e.g. a final class, int[], null), we return it.
      // While this might be seen as an optimization, many parts of the compiler assume that the
      // underlying type is always the weakest form. So here we ensure that we don't weaken beyond
      // the underlying type.
      return type;
    }
    return
        type.getAnalysisDecoratedTypeSingletons().getAnalysisDecoratedType(type, type.canBeNull(),
            true);
  }

  private static JReferenceType weakenToNullable(JReferenceType type) {
    if (type.canBeNull()) {
      return type;
    }

    return type.getAnalysisDecoratedTypeSingletons().getAnalysisDecoratedType(type, true,
        type.canBeSubclass());
  }

  private static JReferenceType stengthenToExact(JReferenceType type) {
    if (!type.canBeSubclass()) {
      return type;
    }
    return
        type.getAnalysisDecoratedTypeSingletons().getAnalysisDecoratedType(type, type.canBeNull(),
            false);
  }

  private static JReferenceType strengthenToNonNull(JReferenceType type) {
    assert !(type.getUnderlyingType() instanceof JNullType);
    if (!type.canBeNull()) {
      return type;
    }
    return type.getAnalysisDecoratedTypeSingletons().getAnalysisDecoratedType(type, false,
        type.canBeSubclass());
  }
}
