/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.dev;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.EmittedArtifact.Visibility;
import com.google.gwt.dev.cfg.ResourceLoader;
import com.google.gwt.dev.json.JsonArray;
import com.google.gwt.dev.json.JsonException;
import com.google.gwt.dev.json.JsonObject;
import com.google.gwt.dev.util.OutputFileSet;
import com.google.gwt.thirdparty.guava.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * DebugSourceCopier copies the source code needed to debug a GWT application.
 */
class DebugSourceCopier {

  // This pattern should match SourceMapArtifact.sourceMapFilenameForFragment.
  private static final Pattern isSourceMapFile = Pattern.compile("sourceMap[0-9]+\\.json$");

  /**
   * Copies all source files referenced in least one sourcemap to dest.
   *
   * @param artifacts contains the sourcemaps we need source code for
   *   and also generated source code to copy.
   * @param src contains the non-generated source code
   * @param dest where to copy the files
   * @param destPrefix a prefix to add to a source file's path when writing it.
   * This is in addition to any prefix inside the OutputFileSet.
   */
  static void copySources(TreeLogger log, ArtifactSet artifacts, ResourceLoader src,
      OutputFileSet dest, String destPrefix)
      throws UnableToCompleteException {

    Set<EmittedArtifact> sourceMaps = findSourceMaps(log, artifacts);

    if (sourceMaps.isEmpty()) {
      log.log(Type.WARN, "Skipped writing source for debuggers because no sourcemaps were"
          + " generated. (Hint: set compiler.useSourceMaps)");
      return;
    }

    Set<String> wantedFiles = getSourcePaths(log, sourceMaps);
    Set<String> nonSourceFiles = new LinkedHashSet<String>();

    // First, check if each file is a source file.
    for (String path : wantedFiles) {
      try {
        if (!copySourceFile(path, src, dest, destPrefix)) {
          nonSourceFiles.add(path);
        }
      } catch (IOException e) {
        log.log(Type.ERROR, "Unable to copy source file: " + path, e);
        throw new UnableToCompleteException();
      }
    }

    // Next see if they are generated files.
    for (EmittedArtifact candidate : artifacts.find(EmittedArtifact.class)) {
      if (!candidate.getVisibility().matches(Visibility.Source)) {
        continue;
      }
      if (!nonSourceFiles.contains(candidate.getPartialPath())) {
        continue;
      }
      copyGeneratedFile(log, candidate, dest, destPrefix);
      nonSourceFiles.remove(candidate.getPartialPath());
    }

    // That should be everything. If not, log a warning.
    if (!nonSourceFiles.isEmpty()) {
      log.log(Type.WARN, "Unable to find all source code needed by debuggers. " +
          nonSourceFiles.size() + " files from sourcemaps weren't found.");
      if (log.isLoggable(Type.DEBUG)) {
        TreeLogger missing = log.branch(Type.DEBUG, "Missing files:");
        for (String path : nonSourceFiles) {
          missing.log(Type.DEBUG, path);
        }
      }
    }
  }

  /**
   * Finds all emitted sourcemaps.
   */
  private static Set<EmittedArtifact> findSourceMaps(TreeLogger log, ArtifactSet artifacts) {
    Set<EmittedArtifact> sourceMaps = new LinkedHashSet<EmittedArtifact>();
    for (EmittedArtifact candidate : artifacts.find(EmittedArtifact.class)) {

      // When running from the Link entry point, all artifacts have become
      // JarEntryEmittedArtifacts so we we can't depend on the type.
      // So, use the filename instead. (This seems fragile.)
      boolean isSourceMap = isSourceMapFile.matcher(candidate.getPartialPath()).find();

      if (isSourceMap) {
        log.log(Type.DEBUG, "found sourcemap: " + candidate.getPartialPath());
        sourceMaps.add(candidate);
      }
    }
    return sourceMaps;
  }

  /**
   * Finds the path of each source file that contributed to at least one sourcemap.
   */
  private static Set<String> getSourcePaths(TreeLogger log, Set<EmittedArtifact> sourceMaps)
      throws UnableToCompleteException {
    Set<String> sourceFiles = new LinkedHashSet<String>();
    for (EmittedArtifact map : sourceMaps) {
      // TODO maybe improve performance by not re-reading the sourcemap files.
      // (We'd need another way for SourceMapRecorder to pass the list of files here.)
      JsonObject json = loadSourceMap(log, map);
      JsonArray sources = json.get("sources").asArray();
      for (int i = 0; i < sources.getLength(); i++) {
        sourceFiles.add(sources.get(i).asString().getString());
      }
    }
    return sourceFiles;
  }

  /**
   * Reads a sourcemap as a JSON object.
   */
  private static JsonObject loadSourceMap(TreeLogger log, EmittedArtifact map)
      throws UnableToCompleteException {
    JsonObject json;
    try {
      json = JsonObject.parse(new InputStreamReader(map.getContents(log)));
    } catch (JsonException e) {
      log.log(Type.ERROR, "Unable to parse sourcemap: " + map.getPartialPath(), e);
      throw new UnableToCompleteException();
    } catch (IOException e) {
      log.log(Type.ERROR, "Unable to read sourcemap: " + map.getPartialPath(), e);
      throw new UnableToCompleteException();
    }
    return json;
  }

  /**
   * Copies a source file from the module to a directory or jar.
   * Returns false if the source file wasn't found.
   */
  private static boolean copySourceFile(String path, ResourceLoader loader,
      OutputFileSet dest, String destPrefix) throws IOException {

    URL resource = loader.getResource(path);
    if (resource == null) {
      return false;
    }

    InputStream in = resource.openStream();
    try {
      OutputStream out = dest.openForWrite(destPrefix + path);
      try {
        ByteStreams.copy(in, out);
      } finally {
        out.close();
      }
    } finally {
      in.close();
    }

    return true;
  }

  private static void copyGeneratedFile(TreeLogger log, EmittedArtifact src,
      OutputFileSet dest, String destPrefix) throws UnableToCompleteException {

    String newPath = destPrefix + src.getPartialPath();
    try {
      OutputStream out = dest.openForWrite(newPath, src.getLastModified());
      src.writeTo(log, out);
      out.close();
    } catch (IOException e) {
      log.log(TreeLogger.ERROR, "Fatal error emitting artifact: " + newPath, e);
      throw new UnableToCompleteException();
    }
  }
}
