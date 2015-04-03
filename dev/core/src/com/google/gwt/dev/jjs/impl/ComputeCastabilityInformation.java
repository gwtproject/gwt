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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.SourceOrigin;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.HasName;
import com.google.gwt.dev.jjs.ast.JArrayRef;
import com.google.gwt.dev.jjs.ast.JArrayType;
import com.google.gwt.dev.jjs.ast.JBinaryOperation;
import com.google.gwt.dev.jjs.ast.JCastMap;
import com.google.gwt.dev.jjs.ast.JCastOperation;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JInstanceOf;
import com.google.gwt.dev.jjs.ast.JNullType;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JReferenceType;
import com.google.gwt.dev.jjs.ast.JType;
import com.google.gwt.dev.jjs.ast.JTypeOracle;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Builds minimal cast maps to cover cast and instanceof operations. Depends
 * on {@link CatchBlockNormalizer}, {@link CompoundAssignmentNormalizer},
 * {@link Devirtualizer}, and {@link LongCastNormalizer} having already run.
 * <p>
 * May or may not include trivial casts depending on configuration.
 */
public class ComputeCastabilityInformation {
  private class AssignTypeCastabilityVisitor extends JVisitor {

    private final Set<JReferenceType> alreadyRan = Sets.newHashSet();
    private final Map<JReferenceType, JCastMap> castableTypesMap = Maps.newIdentityHashMap();
    private final List<JArrayType> instantiatedArrayTypes = Lists.newArrayList();
    private final Multimap<JReferenceType, JReferenceType> castSourceTypesPerCastTargetType =
        HashMultimap.create();

    {
      for (JArrayType arrayType : program.getAllArrayTypes()) {
        if (typeOracle.isInstantiatedType(arrayType)) {
          instantiatedArrayTypes.add(arrayType);
        }
      }

      // Force entries for Object and String.
      recordCastInternal(program.getTypeJavaLangObject(), program.getTypeJavaLangObject());
      recordCastInternal(program.getTypeJavaLangString(), program.getTypeJavaLangObject());

      // Force entries for interfaces implemented by  String
      recordCastInternal(program.getIndexedType("Serializable"), program.getTypeJavaLangObject());
      recordCastInternal(program.getIndexedType("CharSequence"), program.getTypeJavaLangObject());
      recordCastInternal(program.getIndexedType("Comparable"), program.getTypeJavaLangObject());
    }

    public void computeTypeCastabilityMaps() {
      // do String first (which will pull in Object also, it's superclass).
      computeCastMap(program.getTypeJavaLangString());
      assert (castableTypesMap.size() == 2);

      /*
       * Compute the list of classes than can successfully satisfy cast
       * requests, along with the set of types they can be successfully cast to.
       * Do it in super type order.
       */
      for (JReferenceType type : program.getDeclaredTypes()) {
        if (type instanceof JClassType) {
          computeCastMap(type);
        }
      }

      for (JArrayType type : instantiatedArrayTypes) {
        computeCastMap(type);
      }

      // pass our info to JProgram
      program.initTypeInfo(castableTypesMap);
    }

    /*
     * If this expression could possibly generate an ArrayStoreException, we
     * must record a query on the element type being assigned to.
     */
    @Override
    public void endVisit(JBinaryOperation x, Context ctx) {
      if (x.getOp().isAssignment() && x.getLhs() instanceof JArrayRef) {

        // first, calculate the transitive closure of all possible runtime types
        // the lhs could be
        JArrayRef lhsArrayRef = (JArrayRef) x.getLhs();
        JType elementType = lhsArrayRef.getType();
        if (elementType instanceof JNullType) {
          // will generate a null pointer exception instead
          return;
        }

        // primitives are statically correct
        if (!(elementType instanceof JReferenceType)) {
          return;
        }

        // element type being final means the assignment is statically correct
        if (elementType.isFinal()) {
          return;
        }

        /*
         * For every instantiated array type that could -in theory- be the
         * runtime type of the lhs, we must record a cast from the rhs to the
         * prospective element type of the lhs.
         */
        JType rhsType = x.getRhs().getType();
        assert (rhsType instanceof JReferenceType);

        JArrayType lhsArrayType = lhsArrayRef.getArrayType();
        for (JArrayType arrayType : instantiatedArrayTypes) {
          if (typeOracle.canTheoreticallyCast(arrayType, lhsArrayType)) {
            JType itElementType = arrayType.getElementType();
            if (itElementType instanceof JReferenceType) {
              recordCast(itElementType, x.getRhs());
            }
          }
        }
      }
    }

    @Override
    public void endVisit(JCastOperation x, Context ctx) {
      if (disableCastChecking || x.getCastType() == program.getTypeNull()) {
        return;
      }
      recordCast(x.getCastType(), x.getExpr());
    }

    @Override
    public void endVisit(JInstanceOf x, Context ctx) {
      assert (x.getTestType() != program.getTypeNull());
      recordCast(x.getTestType(), x.getExpr());
    }

    private boolean canTriviallyCastJsoSemantics(JReferenceType type, JReferenceType qType) {
      type = type.getUnderlyingType();
      qType = qType.getUnderlyingType();

      if (typeOracle.canTriviallyCast(type, qType)) {
        return true;
      }

      if (type instanceof JArrayType && qType instanceof JArrayType) {
        JArrayType aType = (JArrayType) type;
        JArrayType aqType = (JArrayType) qType;
        return (typeOracle.isJavaScriptObject(aType.getLeafType()) &&
            typeOracle.isJavaScriptObject(aqType.getLeafType()));
      }

      return (typeOracle.isJavaScriptObject(type) &&
          typeOracle.isJavaScriptObject(qType));
    }

    /**
     * Create the mapping from a class to the types it can be cast to.
     */
    private void computeCastMap(JReferenceType type) {
      if (type == null || alreadyRan.contains(type)) {
        return;
      }
      assert (type == type.getUnderlyingType());

      alreadyRan.add(type);

      // Visit super type.
      if (type instanceof JClassType) {
        computeCastMap(((JClassType) type).getSuperClass());
      }

      if (!typeOracle.isInstantiatedType(type) ||
          typeOracle.isJavaScriptObject(type)) {
        return;
      }

      // Find all possible query types which I can satisfy
      Set<JReferenceType> castableTypes = new TreeSet<JReferenceType>(HasName.BY_NAME_COMPARATOR);

      /*
       * NOTE: non-deterministic iteration over HashSet and HashMap. Okay
       * because we're sorting the results.
       */
      for (JReferenceType castTargetType : castSourceTypesPerCastTargetType.keySet()) {
        if (!canTriviallyCastJsoSemantics(type, castTargetType)) {
          continue;
        }

        Collection<JReferenceType> castSourceTypes =
            castSourceTypesPerCastTargetType.get(castTargetType);
        /**
         * Handles JSO[] -> JSO[] case now that canCastTrivially doesn't deal
         * with JSO cross-casts anymore.
         */
        for (JReferenceType castSourceType : castSourceTypes) {
          if (canTriviallyCastJsoSemantics(type, castSourceType) ||
              typeOracle.isJavaScriptObject(castTargetType)) {
            boolean isTrivialCast = castTargetType == program.getTypeJavaLangObject()
                || castTargetType == program.getJavaScriptObject();
            if (recordTrivialCasts || !isTrivialCast) {
              castableTypes.add(castTargetType);
            }
            break;
          }
        }
      }

      /*
       * Don't add an entry for empty answer sets, except for Object and String
       * which require entries.
       */
      if (castableTypes.isEmpty() && type != program.getTypeJavaLangObject()
          && type != program.getTypeJavaLangString()) {
        return;
      }

      // add an entry for me
      castableTypesMap.put(type, new JCastMap(SourceOrigin.UNKNOWN, program.getTypeJavaLangObject(),
          Collections.unmodifiableSet(castableTypes)));
    }

    private void recordCast(JType targetType, JExpression rhs) {
      if (!(targetType instanceof JReferenceType)) {
        return;
      }
      targetType = targetType.getUnderlyingType();

      assert rhs.getType() instanceof JReferenceType;

      JReferenceType rhsType = (JReferenceType) rhs.getType().getUnderlyingType();
      if (!recordTrivialCasts
          && typeOracle.canTriviallyCast(rhsType, (JReferenceType) targetType)) {
        // don't record a type for trivial casts that won't generate code
        return;
      }

      if (!recordTrivialCasts && typeOracle.isJavaScriptObject(targetType)) {
        // If the target type is a JavaScriptObject, don't record an id.
        return;
      }

      recordCastInternal((JReferenceType) targetType, rhsType);
    }

    private void recordCastInternal(JReferenceType toType, JReferenceType rhsType) {
      toType = toType.getUnderlyingType();
      rhsType = rhsType.getUnderlyingType();

      if (toType instanceof JArrayType) {
        // Arrays of any subclass of JavaScriptObject are considered arrays of JavaScriptObject
        // for casting and instanceof purposes.
        toType =  (JReferenceType) program.normalizeJsoType(toType);
      }

      castSourceTypesPerCastTargetType.put(toType, rhsType);
    }
  }

  public static void exec(JProgram program, boolean disableCastChecking,
      boolean recordTrivialCasts) {
    new ComputeCastabilityInformation(program, disableCastChecking, recordTrivialCasts).execImpl();
  }

  public static void exec(JProgram program, boolean disableCastChecking) {
    new ComputeCastabilityInformation(program, disableCastChecking, false).execImpl();
  }

  private final boolean disableCastChecking;

  private final boolean recordTrivialCasts;

  private final JProgram program;

  private final JTypeOracle typeOracle;

  private ComputeCastabilityInformation(JProgram program, boolean disableCastChecking,
      boolean recordTrivialCasts) {
    this.program = program;
    this.typeOracle = program.typeOracle;
    this.disableCastChecking = disableCastChecking;
    this.recordTrivialCasts = recordTrivialCasts;
  }

  private void execImpl() {
    AssignTypeCastabilityVisitor assigner = new AssignTypeCastabilityVisitor();
    assigner.accept(program);
    assigner.computeTypeCastabilityMaps();
  }
}
