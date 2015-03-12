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
 * A reference type decorated with the result of static analysis. Only two analysis properties
 * are computed (mostly during type propagation in TypeTightener: nullness and exactness.
 */
public class JAnalysisDecoratedType extends JReferenceType {

  /**
   * Each {@link JReferenceType} has access to the corresponding singletons (one per type of
   * analysis result).
   */
  static class AnalysisDecoratedTypeSingletons {

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

  private static final int NUM_PROPERTIES = 2;
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
  public JReferenceType weakenToNullable() {
    return weakenToNullable(this);
  }

  @Override
  public JReferenceType weakenToNonExact() {
    return weakenToNonExact(this);
  }

  @Override
  public JReferenceType stengthenToExact() {
    return stengthenToExact(this);
  }

  @Override
  public JReferenceType strengthenToNonNull() {
    return strengthenToNonNull(this);
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

  public static JReferenceType weakenToNonExact(JReferenceType type) {
    if (type.canBeSubclass() || !type.getUnderlyingType().canBeSubclass()) {
      // Already non exact or underlying type is always exact (e.g. a final class, int[], null).
      return type;
    }
    return
        type.getAnalysisDecoratedTypeSingletons().getAnalysisDecoratedType(type, type.canBeNull(),
        true);
  }

  public static JReferenceType weakenToNullable(JReferenceType type) {
    if (type.canBeNull()) {
      return type;
    }

    return type.getAnalysisDecoratedTypeSingletons().getAnalysisDecoratedType(type, true,
        type.canBeSubclass());
  }

  public static JReferenceType stengthenToExact(JReferenceType type) {
    if (!type.canBeSubclass()) {
      return type;
    }
    return
        type.getAnalysisDecoratedTypeSingletons().getAnalysisDecoratedType(type, type.canBeNull(),
        false);
  }

  public static JReferenceType strengthenToNonNull(JReferenceType type) {
    assert !(type.getUnderlyingType() instanceof JNullType);
    if (!type.canBeNull()) {
      return type;
    }
    return type.getAnalysisDecoratedTypeSingletons().getAnalysisDecoratedType(type, false,
        type.canBeSubclass());
  }

  @Override
  public String toString() {
    return (!canBeSubclass ? "(exact)" : "") + (!canBeNull ? "(non-null)" : "") + super.toString();
  }
}
