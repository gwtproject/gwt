/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.util.collect.IntMultimap;
import com.google.gwt.dev.util.collect.IntStack;

import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;

import java.util.BitSet;

/**
 * Uses method and type control flow indexes to compute a reachable types set.
 * <p>
 * Achieves high performance by operating strictly on methods and types which have been mapped to
 * unique int ids but this performance comes at the cost of forcing callers to provide such a
 * compliant AnalyzableTypeEnvironment.
 */
public class RapidTypeAnalyzer {

  /**
   * An interface for a type environment sufficient for rapid type analysis.
   */
  public interface AnalyzableTypeEnvironment {

    /**
     * Returns the id of clinit method of the given type and all types must have a clinit method.
     */
    int getClinitMethodIdFor(int typeId);

    /**
     * Returns a list of the ids of methods contained in the given type and null if there are none.
     */
    IntArrayList getContainedMethodIdsIn(int enclosingTypeId);

    /**
     * Returns a list of the ids of methods called by the given method and null if there are none.
     */
    IntArrayList getMethodIdsCalledBy(int callerMethodId);

    /**
     * Returns a list of the ids of methods overriding the given method and null if there are none.
     */
    IntArrayList getOverriddenMethodIds(int overridingMethodId);

    /**
     * Returns a list of the ids of methods overridden by the given method and null if there are
     * none.
     */
    IntArrayList getOverridingMethodIds(int overriddenMethodId);

    /**
     * Returns a list of the ids of types that are statically referenced within the given method and
     * null if there are none.
     */
    IntArrayList getStaticallyReferencedTypeIdsIn(int methodId);

    /**
     * Returns the id of the super type of the given type and -1 if there is no super type.
     */
    int getSuperTypeId(int typeId);

    /**
     * Returns a list of the ids of types instantiated within the given method and null if there are
     * none.
     */
    IntArrayList getTypeIdsInstantiatedIn(int methodId);
  }

  /**
   * Contains control flow indexes upon which this analysis is based.
   */
  private AnalyzableTypeEnvironment analyzableTypeEnvironment;

  /**
   * A set of ids of methods can not be considered reachable.
   */
  private OpenIntIntHashMap bannedMethodIds = new OpenIntIntHashMap();

  /**
   * The set of ids of types which have so far been found to be instantiated. They are tracked so
   * that so processing the consequences of the instantiation of a type can be limited to just once
   * per type.
   */
  private BitSet instantiatedTypeIds = new BitSet();

  /**
   * A mapping from known-to-exist method ids to the ids of methods that both override them and are
   * contained in types that are known to be instantiated. If at some point a known-to-exist method
   * becomes reachable then the previously characterized associated overriding methods immediately
   * become reachable as well.
   */
  private IntMultimap knownOverridingMethodIdsByOverriddenMethodId = new IntMultimap();

  /**
   * The ids of all methods that override some already known reachable method. If at some point a
   * type becomes instantiated than any of its methods that are already in this set suddenly become
   * reachable.
   */
  private BitSet overidingMethodIdsOfReachableMethods = new BitSet();

  /**
   * Ids of methods that have been reached and processed and should not be reprocessed.
   * <p>
   * AKA the closed set.
   */
  private BitSet reachableMethodIds = new BitSet();

  /**
   * The answer calculated by this reachability analyzer, aka the accumulation of ids of types that
   * have been calculated to be reachable.
   */
  private OpenIntIntHashMap reachableTypeIds = new OpenIntIntHashMap();

  /**
   * Ids of methods that are known to be reachable but for which the consequences of that
   * reachability has not been processed. As exploration progresses newly seen methods are enqueued
   * here for later processing.
   * <p>
   * AKA the open set.
   */
  private IntStack unprocessedReachableMethodIds = new IntStack();

  public RapidTypeAnalyzer(AnalyzableTypeEnvironment analyzableTypeEnvironment) {
    this.analyzableTypeEnvironment = analyzableTypeEnvironment;
  }

  /**
   * Follow control flow to find and return the set of ids of reachable types.
   */
  public IntArrayList computeReachableTypeIds() {
    while (!unprocessedReachableMethodIds.isEmpty()) {
      int reachableMethodId = unprocessedReachableMethodIds.pop();

      addReachableTypeIds(
          analyzableTypeEnvironment.getStaticallyReferencedTypeIdsIn(reachableMethodId));
      markTypeIdsInstantiated(
          analyzableTypeEnvironment.getTypeIdsInstantiatedIn(reachableMethodId));
      markMethodIdsReachable(analyzableTypeEnvironment.getMethodIdsCalledBy(reachableMethodId),
          true);
    }
    return reachableTypeIds.keys();
  }

  /**
   * Enqueues the methods within a given type (and some related overriding method ids) as reachable
   * and not yet processed.
   */
  public void markContainedMethodIdsReachable(int typeId) {
    IntArrayList containedMethodIds = analyzableTypeEnvironment.getContainedMethodIdsIn(typeId);
    if (containedMethodIds == null) {
      return;
    }
    markMethodIdsReachable(containedMethodIds, true);
  }

  /**
   * Prevent a method from ever being considered reachable.
   */
  public void markMethodIdBanned(int bannedMethodId) {
    bannedMethodIds.put(bannedMethodId, bannedMethodId);
  }

  /**
   * Enqueues a given method id (and some related overriding method ids) as reachable and not yet
   * processed.
   */
  public void markMethodIdReachable(int methodId, boolean cascade) {
    if (bannedMethodIds.containsKey(methodId)) {
      // Ignore banned methods.
      return;
    }
    // If the given method has already been marked reachable.
    if (reachableMethodIds.get(methodId)) {
      // Ignore it.
      return;
    }

    overidingMethodIdsOfReachableMethods.set(methodId);
    unprocessedReachableMethodIds.push(methodId);
    reachableMethodIds.set(methodId);
    IntArrayList overridingMethodIds = analyzableTypeEnvironment.getOverridingMethodIds(methodId);
    if (overridingMethodIds != null) {
      for (int i = 0; i < overridingMethodIds.size(); i++) {
        int overridingMethodId = overridingMethodIds.get(i);
        overidingMethodIdsOfReachableMethods.set(overridingMethodId);
      }
    }

    if (cascade) {
      // The current method is being marked reachable so any methods that override it should be
      // marked reachable as well.
      IntArrayList values = knownOverridingMethodIdsByOverriddenMethodId.get(methodId);
      if (values != null) {
        for (int i = 0; i < values.size(); i++) {
          int overridingMethodId = values.get(i);
          // Don't cascade in this nested invocation since at this point any overriding methods are
          // already marked reachable.
          markMethodIdReachable(overridingMethodId, false);
        }
      }
    }
  }

  /**
   * Records that a type is reachable and enqueues its clinit for traversal.
   */
  public void markTypeIdReachable(int typeId) {
    if (!reachableTypeIds.containsKey(typeId)) {
      reachableTypeIds.put(typeId, typeId);
      int clinitMethodId = analyzableTypeEnvironment.getClinitMethodIdFor(typeId);
      markMethodIdReachable(clinitMethodId, true);
    }
  }

  private void addReachableTypeIds(IntArrayList typeIds) {
    if (typeIds == null) {
      return;
    }
    for (int i = 0; i < typeIds.size(); i++) {
      int typeId = typeIds.get(i);
      markTypeIdReachable(typeId);
    }
  }

  private void markMethodIdsReachable(IntArrayList methodIds, boolean cascade) {
    if (methodIds == null) {
      return;
    }
    for (int i = 0; i < methodIds.size(); i++) {
      markMethodIdReachable(methodIds.get(i), cascade);
    }
  }

  private void markTypeIdInstantiated(int typeId) {
    if (!instantiatedTypeIds.get(typeId)) {
      instantiatedTypeIds.set(typeId);
      markTypeIdReachable(typeId);
      IntArrayList containedMethodIds = analyzableTypeEnvironment.getContainedMethodIdsIn(typeId);

      if (containedMethodIds != null) {
        for (int i = 0; i < containedMethodIds.size(); i++) {
          int containedMethodId = containedMethodIds.get(i);
          IntArrayList overriddenMethodIds =
              analyzableTypeEnvironment.getOverriddenMethodIds(containedMethodId);
          if (overriddenMethodIds != null) {
            for (int j = 0; j < overriddenMethodIds.size(); j++) {
              int overriddenMethodId = overriddenMethodIds.get(j);
              knownOverridingMethodIdsByOverriddenMethodId.put(overriddenMethodId,
                  containedMethodId);
            }
          }
        }

        for (int i = 0; i < containedMethodIds.size(); i++) {
          int containedMethodId = containedMethodIds.get(i);
          if (overidingMethodIdsOfReachableMethods.get(containedMethodId)) {
            markMethodIdReachable(containedMethodId, true);
          }
        }
      }

      int superTypeId = analyzableTypeEnvironment.getSuperTypeId(typeId);
      if (superTypeId != -1) {
        markTypeIdInstantiated(superTypeId);
      }
    }
  }

  private void markTypeIdsInstantiated(IntArrayList typeIds) {
    if (typeIds == null) {
      return;
    }
    for (int i = 0; i < typeIds.size(); i++) {
      int typeId = typeIds.get(i);
      markTypeIdInstantiated(typeId);
    }
  }
}
