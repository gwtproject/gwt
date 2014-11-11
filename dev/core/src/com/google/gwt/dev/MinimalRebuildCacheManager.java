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

import com.google.gwt.dev.util.CompilerVersion;
import com.google.gwt.thirdparty.guava.common.cache.Cache;
import com.google.gwt.thirdparty.guava.common.cache.CacheBuilder;
import com.google.gwt.thirdparty.guava.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gwt.util.tools.Utility;
import com.google.gwt.util.tools.shared.Md5Utils;
import com.google.gwt.util.tools.shared.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Reads and writes MinimalRebuildCache instances.
 */
public class MinimalRebuildCacheManager {

  private final File minimalRebuildCacheDir;
  private final Cache<String, MinimalRebuildCache> minimalRebuildCachesByName =
      CacheBuilder.newBuilder().maximumSize(3).build();
  private static final String REBUILD_CACHE_PREFIX = "gwt-rebuildCache";
  private final ScheduledExecutorService writeScheduler =
      Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setDaemon(true).build());

  public MinimalRebuildCacheManager(File baseCacheDir) {
    if (baseCacheDir != null) {
      minimalRebuildCacheDir = new File(baseCacheDir, REBUILD_CACHE_PREFIX);
    } else {
      minimalRebuildCacheDir = null;
    }
  }

  /**
   * Delete all in memory caches and all on disk associated with the given module.
   */
  public synchronized void deleteCaches() throws IOException {
    minimalRebuildCachesByName.invalidateAll();

    if (minimalRebuildCacheDir == null || !minimalRebuildCacheDir.exists()) {
      return;
    }

    for (File cacheFile : minimalRebuildCacheDir.listFiles()) {
      if (!cacheFile.delete()) {
        throw new IOException("Couldn't delete " + cacheFile);
      }
    }
    if (!minimalRebuildCacheDir.delete()) {
      throw new IOException("Couldn't delete " + minimalRebuildCacheDir);
    }
  }

  /**
   * Return the MinimalRebuildCache specific to the given module and binding properties.
   * <p>
   * If no such cache is found then it can be optionally loaded off disk.
   * <p>
   * Barring that a cache instance will be created in memory.
   */
  public MinimalRebuildCache getCache(String moduleName, Map<String, String> bindingProperties) {
    String cacheName = computeMinimalRebuildCacheName(moduleName, bindingProperties);

    MinimalRebuildCache minimalRebuildCache = minimalRebuildCachesByName.getIfPresent(cacheName);

    // If there's no cache already in memory, try to load a cache from disk.
    if (minimalRebuildCache == null && minimalRebuildCacheDir != null) {
      // Might return null.
      minimalRebuildCache = readCache(moduleName, bindingProperties);
      if (minimalRebuildCache != null) {
        minimalRebuildCachesByName.put(cacheName, minimalRebuildCache);
      }
    }

    // If there's still no cache loaded, just create a blank one.
    if (minimalRebuildCache == null) {
      minimalRebuildCache = new MinimalRebuildCache();
      minimalRebuildCachesByName.put(cacheName, minimalRebuildCache);
      return minimalRebuildCache;
    }

    // Return a copy.
    MinimalRebuildCache mutableMinimalRebuildCache = new MinimalRebuildCache();
    mutableMinimalRebuildCache.copyFrom(minimalRebuildCache);
    return mutableMinimalRebuildCache;
  }

  /**
   * Stores a MinimalRebuildCache specific to the given module and binding properties.
   * <p>
   * A copy of the cache will be lazily persisted to disk as well.
   */
  public void setCache(String moduleName, Map<String, String> bindingProperties,
      MinimalRebuildCache knownGoodMinimalRebuildCache) {
    String cacheName = computeMinimalRebuildCacheName(moduleName, bindingProperties);

    minimalRebuildCachesByName.put(cacheName, knownGoodMinimalRebuildCache);

    // Lazily write the cache to disk so that compiles can be fast the next time the Java process is
    // restarted.
    writeCacheAsync(moduleName, bindingProperties, knownGoodMinimalRebuildCache);
  }

  /**
   * Find, read and return the MinimalRebuildCache unique to this module, binding properties and
   * working directory.
   */
  synchronized MinimalRebuildCache readCache(String moduleName,
      Map<String, String> bindingProperties) {
    MinimalRebuildCache minimalRebuildCache = null;

    // Find the cache file unique to this module, binding properties and working directory.
    File minimalRebuildCacheFile = computeMinimalRebuildCacheFile(moduleName, bindingProperties);

    // If the file exists.
    if (minimalRebuildCacheFile.exists()) {
      ObjectInputStream objectInputStream = null;
      // Try to read it.
      try {
        objectInputStream = new ObjectInputStream(
            new BufferedInputStream(new FileInputStream(minimalRebuildCacheFile)));
        minimalRebuildCache = (MinimalRebuildCache) objectInputStream.readObject();
      } catch (IOException e) {
        System.err.println("Unable to read the rebuild cache from disk.");
        Utility.close(objectInputStream);
        minimalRebuildCacheFile.delete();
      } catch (ClassNotFoundException e) {
        System.err.println("Unable to read the rebuild cache from disk.");
        Utility.close(objectInputStream);
        minimalRebuildCacheFile.delete();
      } finally {
        Utility.close(objectInputStream);
      }
    }
    return minimalRebuildCache;
  }

  /**
   * Asynchronously writes the provided MinimalRebuildCache to disk.
   * <p>
   * Persisted caches are uniquely named based on the compiler version, current module name, binding
   * properties and the location where the JVM was launched.
   * <p>
   * Care is taken to completely and successfully write a new cache (to a different location on
   * disk) before replacing the old cache (at the regular location on disk).
   * <p>
   * Write requests will occur in the order requested and will queue up if requests are made faster
   * than they can be completed.
   */
  Future<Void> writeCacheAsync(final String moduleName, final Map<String, String> bindingProperties,
      final MinimalRebuildCache minimalRebuildCache) {
    return writeScheduler.submit(new Runnable() {
        @Override
      public void run() {
        writeCache(moduleName, bindingProperties, minimalRebuildCache);
      }
    }, null);
  }

  private File computeMinimalRebuildCacheFile(String moduleName,
      Map<String, String> bindingProperties) {
    return new File(minimalRebuildCacheDir,
        computeMinimalRebuildCacheName(moduleName, bindingProperties));
  }

  private String computeMinimalRebuildCacheName(String moduleName,
      Map<String, String> bindingProperties) {
    String currentWorkingDirectory = System.getProperty("user.dir");
    String compilerVersionHash = CompilerVersion.getHash();
    String bindingPropertiesString = bindingProperties.toString();

    String consistentHash = StringUtils.toHexString(Md5Utils.getMd5Digest((
        compilerVersionHash + moduleName + currentWorkingDirectory + bindingPropertiesString)
        .getBytes()));
    return REBUILD_CACHE_PREFIX + "-" + consistentHash;
  }

  /**
   * Writes the provided MinimalRebuildCache to disk.
   * <p>
   * Persisted caches are uniquely named based on the compiler version, current module name, binding
   * properties and the location where the JVM was launched.
   * <p>
   * Care is taken to completely and successfully write a new cache (to a different location on
   * disk) before replacing the old cache (at the regular location on disk).
   * <p>
   * Write requests will occur in the order requested and will queue up if requests are made faster
   * than they can be completed.
   */
  private synchronized void writeCache(String moduleName, Map<String, String> bindingProperties,
      MinimalRebuildCache minimalRebuildCache) {
    if (minimalRebuildCacheDir == null) {
      return;
    }

    File oldMinimalRebuildCacheFile = computeMinimalRebuildCacheFile(moduleName, bindingProperties);
    File newMinimalRebuildCacheFile =
        new File(oldMinimalRebuildCacheFile.getAbsoluteFile() + ".new");

    // Ensure the cache folder exists.
    oldMinimalRebuildCacheFile.getParentFile().mkdirs();

    // Write the new cache to disk.
    ObjectOutputStream objectOutputStream = null;
    try {
      objectOutputStream = new ObjectOutputStream(
          new BufferedOutputStream(new FileOutputStream(newMinimalRebuildCacheFile)));
      objectOutputStream.writeObject(minimalRebuildCache);
      objectOutputStream.close();

      // Replace the old cache file with the new one.
      oldMinimalRebuildCacheFile.delete();
      newMinimalRebuildCacheFile.renameTo(oldMinimalRebuildCacheFile);
    } catch (IOException e) {
      System.err.println("Unable to save the rebuild cache to disk.");
      newMinimalRebuildCacheFile.delete();
    } finally {
      try {
        if (objectOutputStream != null) {
          objectOutputStream.close();
        }
      } catch (IOException e) {
        // Can't do anything about that.
      }
    }
  }
}
