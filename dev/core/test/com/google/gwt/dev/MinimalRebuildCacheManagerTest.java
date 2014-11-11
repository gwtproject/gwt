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

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.jjs.ast.JTypeOracle;
import com.google.gwt.dev.util.DiskCachingUtil;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableMap;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Tests for {@link MinimalRebuildCacheManager}.
 */
public class MinimalRebuildCacheManagerTest extends TestCase {

  public void testNoSuchCache() throws IOException {
    File baseCacheDir = DiskCachingUtil.computePreferredCacheDir(
        Lists.newArrayList("com.google.FooModule"), TreeLogger.NULL);
    MinimalRebuildCacheManager minimalRebuildCacheManager =
        new MinimalRebuildCacheManager(baseCacheDir);

    // Make sure we start with a blank slate.
    minimalRebuildCacheManager.deleteCaches();

    // Construct and empty cache and also ask the manager to get a cache which does not exist.
    MinimalRebuildCache emptyCache = new MinimalRebuildCache();
    MinimalRebuildCache noSuchCache = minimalRebuildCacheManager.getCache("com.google.FooModule",
        Maps.<String, String> newHashMap());

    // Show that the manager created a new empty cache for the request of a cache that does not
    // exist.
    assertFalse(emptyCache == noSuchCache);
    assertTrue(emptyCache.hasSameContent(noSuchCache));
  }

  public void testReload() throws IOException, InterruptedException, ExecutionException {
    String moduleName = "com.google.FooModule";
    File baseCacheDir =
        DiskCachingUtil.computePreferredCacheDir(Lists.newArrayList(moduleName), TreeLogger.NULL);
    MinimalRebuildCacheManager minimalRebuildCacheManager =
        new MinimalRebuildCacheManager(baseCacheDir);
    Map<String, String> bindingProperites = Maps.<String, String> newHashMap();

    // Make sure we start with a blank slate.
    minimalRebuildCacheManager.deleteCaches();

    MinimalRebuildCache startingCache =
        minimalRebuildCacheManager.getCache(moduleName, bindingProperites);

    // Record and compute a bunch of random data.
    Map<String, Long> currentModifiedBySourcePath = new ImmutableMap.Builder<String, Long>().put(
        "Foo.java", 0L).put("Bar.java", 0L).put("Baz.java", 0L).build();
    startingCache.recordDiskSourceResources(currentModifiedBySourcePath);
    startingCache.recordNestedTypeName("Foo", "Foo");
    startingCache.setJsForType(TreeLogger.NULL, "Foo", "Some Js for Foo");
    startingCache.addTypeReference("Bar", "Foo");
    startingCache.getImmediateTypeRelations().getImmediateSuperclassesByClass().put("Baz", "Foo");
    startingCache.addTypeReference("Foo", "Foo$Inner");
    Map<String, Long> laterModifiedBySourcePath = new ImmutableMap.Builder<String, Long>().put(
        "Foo.java", 9999L).put("Bar.java", 0L).put("Baz.java", 0L).build();
    startingCache.recordDiskSourceResources(laterModifiedBySourcePath);
    startingCache.setRootTypeNames(Sets.newHashSet("Foo", "Bar", "Baz"));
    startingCache.computeReachableTypeNames();
    startingCache.computeAndClearStaleTypesCache(TreeLogger.NULL,
        new JTypeOracle(null, startingCache, true));

    // Save and reload the cache.
    Future<Void> setCacheFuture = minimalRebuildCacheManager.writeCacheAsync(moduleName,
        bindingProperites, startingCache);
    setCacheFuture.get();
    MinimalRebuildCache reloadedCache =
        minimalRebuildCacheManager.readCache(moduleName, bindingProperites);

    // Show that the manager created a new empty cache for the request of a cache that does not
    // exist.
    assertFalse(startingCache == reloadedCache);
    assertTrue(startingCache.hasSameContent(reloadedCache));
  }
}
