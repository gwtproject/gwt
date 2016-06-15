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
import com.google.gwt.dev.jjs.SourceOrigin;

/**
 * Base class for any reference type.
 */
public abstract class JReferenceType extends JType implements CanBeAbstract {

  public static final JReferenceType NULL_TYPE =
      new JReferenceType(SourceOrigin.UNKNOWN, "null") {
        @Override
        AnalysisResult getAnalysisResult() {
          return AnalysisResult.NULLABLE_EXACT;
        }

        @Override
        public String getJavahSignatureName() {
          return "N";
        }

        @Override
        public String getJsniSignatureName() {
          return "N";
        }

        @Override
        public JEnumType isEnumOrSubclass() {
          return null;
        }

        @Override
        public boolean isAbstract() {
          return false;
        }

        @Override
        public boolean isFinal() {
          return true;
        }

        @Override
        public boolean isJsoType() {
          return false;
        }

        @Override
        public boolean isArrayType() {
          return false;
        }

        @Override
        public boolean isNullType() {
          return true;
        }

        @Override
        public boolean isJsType() {
          return false;
        }

        @Override
        public boolean isJsFunction() {
          return false;
        }

        @Override
        public boolean isJsFunctionImplementation() {
          return false;
        }

        @Override
        public boolean isJsNative() {
          return false;
        }

        @Override
        public boolean canBeImplementedExternally() {
          return false;
        }

        @Override
        public boolean canBeReferencedExternally() {
          return false;
        }

        @Override
        public boolean isJavaLangObject() {
          return false;
        }

        @Override
        public void traverse(JVisitor visitor, Context ctx) {
          if (visitor.visit(this, ctx)) {
          }
          visitor.endVisit(this, ctx);
        }

        private Object readResolve() {
          return NULL_TYPE;
        }

        @Override
        public JReferenceType strengthenToNonNull() {
          throw new UnsupportedOperationException();
        }
      };

  private transient AnalysisDecoratedTypePool analysisDecoratedTypePool = null;

  enum AnalysisResult {
    NULLABLE_NOT_EXACT(true, false),
    NOT_NULLABLE_NOT_EXACT(false, false),
    NULLABLE_EXACT(true, true),
    NOT_NULLABLE_EXACT(false, true);

    private final boolean isNullable;
    private final boolean isExact;

    AnalysisResult(boolean isNullable, boolean isExact) {
      this.isNullable = isNullable;
      this.isExact = isExact;
    }

    private boolean isNullable() {
      return isNullable;
    }

    private boolean isExact() {
      return isExact;
    }
  }
  /**
   * A reference type decorated with the result of static analysis. Only two analysis properties
   * are computed (mostly during type propagation in TypeTightener: nullness and exactness.
   */
  private static class JAnalysisDecoratedType extends JReferenceType {

    private final AnalysisResult analysisResult;
    private final JReferenceType ref;

    private JAnalysisDecoratedType(JReferenceType ref, AnalysisResult analysisResult) {
      super(ref.getSourceInfo(), ref.getName());
      this.analysisResult = analysisResult;
      assert ref.getUnderlyingType().getAnalysisResult() != analysisResult :
          "An analysis type for " + ref +
          " should not have been constructed as it is equivalent to the original type";
      assert !ref.isNullType();
      assert !(ref instanceof JAnalysisDecoratedType);
      this.ref = ref;
    }

    @Override
    AnalysisDecoratedTypePool getAnalysisDecoratedTypePool() {
      return ref.getAnalysisDecoratedTypePool();
    }

    @Override
    AnalysisResult getAnalysisResult() {
      return analysisResult;
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
    public boolean isArrayType() {
      return ref.isArrayType();
    }

    @Override
    public boolean isJsType() {
      return ref.isJsType();
    }

    @Override
    public boolean isJsFunction() {
      return ref.isJsFunction();
    }

    @Override
    public boolean isJsFunctionImplementation() {
      return ref.isJsFunctionImplementation();
    }

    @Override
    public boolean isJsoType() {
      return ref.isJsoType();
    }

    @Override
    public boolean isJsNative() {
      return ref.isJsNative();
    }

    @Override
    public boolean canBeImplementedExternally() {
      return ref.canBeImplementedExternally();
    }

    @Override
    public boolean canBeReferencedExternally() {
      return ref.canBeReferencedExternally();
    }

    @Override
    public boolean isJavaLangObject() {
      return ref.isJavaLangObject();
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
      return getAnalysisDecoratedTypePool().getAnalysisDecoratedType(ref, analysisResult);
    }

    @Override
    public String getDescription() {
      return super.getDescription() + (!canBeNull() ? " (non-null)" : "") +
          (!canBeSubclass() ? "(exact) " : "");
    }
  }

  /**
   * Each {@link JReferenceType} has access to the corresponding singletons (one per type of
   * analysis result).
   */
  private static class AnalysisDecoratedTypePool {
    private final JAnalysisDecoratedType[] decoratedAnalysisTypePool =
        new JAnalysisDecoratedType[AnalysisResult.values().length - 1];

    public JReferenceType getAnalysisDecoratedType(JReferenceType type, AnalysisResult request) {
      JReferenceType underlyingType = type.getUnderlyingType();
      if (underlyingType.getAnalysisResult() == request) {
        return underlyingType;
      }
      assert request != AnalysisResult.NULLABLE_NOT_EXACT;
      int poolIndex = request.ordinal() - 1;
      JAnalysisDecoratedType result = decoratedAnalysisTypePool[poolIndex];
      if (result == null) {
        result = decoratedAnalysisTypePool[poolIndex] =
            new JAnalysisDecoratedType(underlyingType, request);
      }
      return result;
    }
  }

  public JReferenceType(SourceInfo info, String name) {
    super(info, name);
  }

  @Override
  public final boolean canBeNull() {
    return getAnalysisResult().isNullable();
  }

  @Override
  public final boolean canBeSubclass() {
    boolean exact = getAnalysisResult().isExact();
    assert !exact || canBeStrengthenedToExactType() :
        "A JSO or native type can never be EXACT but " + name + " is.";
    return !exact;
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

  @Override
  public boolean isPrimitiveType() {
    return false;
  }

  public JReferenceType weakenToNullable() {
    if (getUnderlyingType() == this) {
      // Underlying types cannot be weakened.
      return this;
    }
    switch (getAnalysisResult()) {
      case NOT_NULLABLE_NOT_EXACT:
        return getAnalysisDecoratedTypePool().getAnalysisDecoratedType(
            this, AnalysisResult.NULLABLE_NOT_EXACT);
      case NOT_NULLABLE_EXACT:
        return getAnalysisDecoratedTypePool().getAnalysisDecoratedType(
            this, AnalysisResult.NULLABLE_EXACT);
      case NULLABLE_EXACT:
      case NULLABLE_NOT_EXACT:
        return this;
    }
    throw new AssertionError("Unknown AnalysisResult " + getAnalysisResult().toString());
  }

  public JReferenceType weakenToNonExact() {
    if (getUnderlyingType() == this) {
      // Underlying types cannot be weakened.
      return this;
    }
    switch (getAnalysisResult()) {
      case NULLABLE_EXACT:
        return getAnalysisDecoratedTypePool().getAnalysisDecoratedType(
            this, AnalysisResult.NULLABLE_NOT_EXACT);
      case NOT_NULLABLE_EXACT:
        return getAnalysisDecoratedTypePool().getAnalysisDecoratedType(
            this, AnalysisResult.NOT_NULLABLE_NOT_EXACT);
      case NOT_NULLABLE_NOT_EXACT:
      case NULLABLE_NOT_EXACT:
        return this;
    }
    throw new AssertionError("Unknown AnalysisResult " + getAnalysisResult().toString());
  }

  private boolean canBeStrengthenedToExactType() {
    return !isJsoType() && !canBeImplementedExternally();
  }

  private boolean canBeStrengthenedToNonNull() {
    // JSOs can not be strengthened because there is code that assumes that null is a JSO, and
    // instance methods calls never throw NPE on null.
    // Some methods like JavaScriptObject.cast() will confuse the compiler, due to modeling the
    // return type as non-null.
    return !isJsoType();
  }

  @Override
  public JReferenceType strengthenToNonNull() {
    if (!canBeStrengthenedToNonNull()) {
      return this;
    }
    switch (getAnalysisResult()) {
      case NULLABLE_NOT_EXACT:
        return getAnalysisDecoratedTypePool().getAnalysisDecoratedType(
            this, AnalysisResult.NOT_NULLABLE_NOT_EXACT);
      case NULLABLE_EXACT:
        return getAnalysisDecoratedTypePool().getAnalysisDecoratedType(
            this, AnalysisResult.NOT_NULLABLE_EXACT);
      case NOT_NULLABLE_NOT_EXACT:
      case NOT_NULLABLE_EXACT:
        return this;
    }
    throw new AssertionError("Unknown AnalysisResult " + getAnalysisResult().toString());
  }

  public JReferenceType strengthenToExact() {
    if (!canBeStrengthenedToExactType()) {
      return this;
    }
    switch (getAnalysisResult()) {
      case NOT_NULLABLE_NOT_EXACT:
        return getAnalysisDecoratedTypePool().getAnalysisDecoratedType(
            this, AnalysisResult.NOT_NULLABLE_EXACT);
      case NULLABLE_NOT_EXACT:
        return getAnalysisDecoratedTypePool().getAnalysisDecoratedType(
            this, AnalysisResult.NULLABLE_EXACT);
      case NULLABLE_EXACT:
      case NOT_NULLABLE_EXACT:
        return this;
    }
    throw new AssertionError("Unknown AnalysisResult " + getAnalysisResult().toString());
  }

  /**
   * If this type is a non-null type, returns the underlying (original) type.
   */
  @Override
  public JReferenceType getUnderlyingType() {
    return this;
  }

  @Override
  public boolean replaces(JType originalType) {
    return super.replaces(originalType) && canBeNull() == originalType.canBeNull();
  }

  AnalysisDecoratedTypePool getAnalysisDecoratedTypePool() {
    assert !(this instanceof JAnalysisDecoratedType);
    if (analysisDecoratedTypePool == null) {
      analysisDecoratedTypePool = new AnalysisDecoratedTypePool();
    }
    return analysisDecoratedTypePool;
  }

  AnalysisResult getAnalysisResult() {
    if (isFinal() && canBeStrengthenedToExactType()) {
      return AnalysisResult.NULLABLE_EXACT;
    }
    return AnalysisResult.NULLABLE_NOT_EXACT;
  }
}
