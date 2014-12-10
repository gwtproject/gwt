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
package com.google.gwt.dev;

import com.google.gwt.dev.jjs.impl.RapidTypeAnalyzer.AnalyzableTypeEnvironment;

import cern.colt.list.IntArrayList;

/**
 * Adapts a MinimalRebuildCache to act as an AnalyzableTypeEnvironment.
 */
public class MinimalRebuildCacheTypeEnvironmentAdapter implements AnalyzableTypeEnvironment {

  private MinimalRebuildCache minimalRebuildCache;

  public MinimalRebuildCacheTypeEnvironmentAdapter(MinimalRebuildCache minimalRebuildCache) {
    this.minimalRebuildCache = minimalRebuildCache;
  }

  @Override
  public int getClinitMethodIdFor(int typeId) {
    String typeName = minimalRebuildCache.getTypeNameById(typeId);
    return minimalRebuildCache.getMethodIdByName(typeName + "::$clinit()");
  }

  @Override
  public IntArrayList getContainedMethodIdsIn(int enclosingTypeId) {
    return minimalRebuildCache.getContainedMethodIdsIn(enclosingTypeId);
  }

  @Override
  public IntArrayList getMethodIdsCalledBy(int callerMethodId) {
    return minimalRebuildCache.getMethodIdsCalledBy(callerMethodId);
  }

  @Override
  public IntArrayList getOverriddenMethodIds(int overridingMethodId) {
    return minimalRebuildCache.getOverriddenMethodIds(overridingMethodId);
  }

  @Override
  public IntArrayList getOverridingMethodIds(int overriddenMethodId) {
    return minimalRebuildCache.getOverridingMethodIds(overriddenMethodId);
  }

  @Override
  public IntArrayList getStaticallyReferencedTypeIdsIn(int methodId) {
    return minimalRebuildCache.getStaticallyReferencedTypeIdsIn(methodId);
  }

  @Override
  public int getSuperTypeId(int typeId) {
    String typeName = minimalRebuildCache.getTypeNameById(typeId);
    String superTypeName = minimalRebuildCache.getImmediateTypeRelations()
        .getImmediateSuperclassesByClass().get(typeName);
    if (superTypeName != null) {
      return minimalRebuildCache.getTypeIdByName(superTypeName);
    }
    return -1;
  }

  @Override
  public IntArrayList getTypeIdsInstantiatedIn(int methodId) {
    return minimalRebuildCache.getTypeIdsInstantiatedIn(methodId);
  }
}
