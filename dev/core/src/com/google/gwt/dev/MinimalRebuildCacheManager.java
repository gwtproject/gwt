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
import com.google.gwt.thirdparty.guava.common.collect.Maps;
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

  private static Map<String, MinimalRebuildCache> minimalRebuildCachesByName = Maps.newHashMap();
  private static final String REBUILD_CACHE_PREFIX = "gwt-rebuildCache";
  private static ScheduledExecutorService writeScheduler =
      Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setDaemon(true).build());

  /**
   * Delete all in memory caches and all on disk associated with the given module.
   */
  public static synchronized void deleteCaches(File baseCacheDir)
      throws IOException {
    minimalRebuildCachesByName.clear();

    File minimalRebuildCacheFolder = computeMinimalRebuildCacheFolder(baseCacheDir);
    if (!minimalRebuildCacheFolder.exists()) {
      return;
    }

    for (File cacheFile : minimalRebuildCacheFolder.listFiles()) {
      if (!cacheFile.delete()) {
        throw new IOException("Couldn't delete " + cacheFile);
      }
    }
    if (!minimalRebuildCacheFolder.delete()) {
      throw new IOException("Couldn't delete " + minimalRebuildCacheFolder);
    }
  }

  /**
   * Return the MinimalRebuildCache specific to the given module and binding properties.
   * <p>
   * If no such cache is found then it can be optionally loaded off disk.
   * <p>
   * Barring that a cache instance will be created in memory.
   */
  public static MinimalRebuildCache getCache(String moduleName, File baseCacheDir,
      Map<String, String> bindingProperties) {
    String cacheName = computeMinimalRebuildCacheName(moduleName, bindingProperties);

    MinimalRebuildCache minimalRebuildCache = minimalRebuildCachesByName.get(cacheName);

    // If there's no cache already in memory, try to load a cache from disk.
    if (minimalRebuildCache == null) {
      // Might return null.
      minimalRebuildCache = readCache(moduleName, baseCacheDir, bindingProperties);
      minimalRebuildCachesByName.put(cacheName, minimalRebuildCache);
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
  public static void setCache(String moduleName, File baseCacheDir,
      Map<String, String> bindingProperties, MinimalRebuildCache knownGoodMinimalRebuildCache) {
    String cacheName = computeMinimalRebuildCacheName(moduleName, bindingProperties);

    minimalRebuildCachesByName.put(cacheName, knownGoodMinimalRebuildCache);

    // Lazily write the cache to disk so that compiles can be fast the next time the Java process is
    // restarted.
    writeCacheAsync(moduleName, baseCacheDir, bindingProperties, knownGoodMinimalRebuildCache);
  }

  /**
   * Find, read and return the MinimalRebuildCache unique to this module, binding properties and
   * working directory.
   */
  static synchronized MinimalRebuildCache readCache(String moduleName, File baseCacheDir,
      Map<String, String> bindingProperties) {
    MinimalRebuildCache minimalRebuildCache = null;

    // Find the cache file unique to this module, binding properties and working directory.
    File minimalRebuildCacheFile =
        computeMinimalRebuildCacheFile(moduleName, baseCacheDir, bindingProperties);

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
  static Future<Void> writeCacheAsync(final String moduleName, final File baseCacheDir,
      final Map<String, String> bindingProperties, final MinimalRebuildCache minimalRebuildCache) {
    return writeScheduler.submit(new Runnable() {
        @Override
      public void run() {
        writeCache(moduleName, baseCacheDir, bindingProperties, minimalRebuildCache);
      }
    }, null);
  }

  private static File computeMinimalRebuildCacheFile(String moduleName, File baseCacheDir,
      Map<String, String> bindingProperties) {
    return new File(computeMinimalRebuildCacheFolder(baseCacheDir),
        computeMinimalRebuildCacheName(moduleName, bindingProperties));
  }

  private static File computeMinimalRebuildCacheFolder(File baseCacheDir) {
    return new File(baseCacheDir, REBUILD_CACHE_PREFIX);
  }

  private static String computeMinimalRebuildCacheName(String moduleName,
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
  private static synchronized void writeCache(String moduleName, File baseCacheDir,
      Map<String, String> bindingProperties, MinimalRebuildCache minimalRebuildCache) {
    File oldMinimalRebuildCacheFile =
        computeMinimalRebuildCacheFile(moduleName, baseCacheDir, bindingProperties);
    File newMinimalRebuildCacheFile =
        new File(oldMinimalRebuildCacheFile.getAbsoluteFile() + ".new");

    // Ensure the cache folder exists.
    oldMinimalRebuildCacheFile.getParentFile().mkdirs();

    // Write write the new cache to disk.
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
