/*
 * Copyright 2009 Google Inc.
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
package com.google.gwt.dev.util;

import com.google.gwt.dev.util.collect.HashSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

/**
 * An {@link OutputFileSet} on a directory.
 */
public class OutputFileSetOnDirectory extends OutputFileSet {
  private final Set<String> createdDirs = new HashSet<String>();
  private final File dir;
  private final String prefix;

  public OutputFileSetOnDirectory(File dir, String prefix) {
    super(dir.getAbsolutePath());
    this.dir = dir;
    this.prefix = prefix;
  }

  @Override
  public void close() {
  }

  @Override
  protected OutputStream createNewOutputStream(String path,
      final long timeStampMillis) throws IOException {
    final File file = pathToFile(path);
    mkdirs(file.getParentFile());
    return new FileOutputStream(file) {
      @Override
      public void close() throws IOException {
        super.close();
        if (timeStampMillis != TIMESTAMP_UNAVAILABLE) {
          file.setLastModified(timeStampMillis);
        }
      }
    };
  }

  /**
   * A faster bulk version of {@link File#mkdirs()} that avoids recreating the
   * same directory multiple times.
   */
  private void mkdirs(File dir) throws IOException {
    if (dir == null) {
      return;
    }
    String path = dir.getPath();
    if (createdDirs.contains(path)) {
      return;
    }
    createdDirs.add(path);
    if (!dir.exists()) {
      mkdirs(dir.getParentFile());
      if (!dir.mkdir()) {
        throw new IOException("unable to create directory: " + dir.getAbsolutePath());
      }
    }
  }

  private File pathToFile(String path) throws IOException {
    return new File(dir, prefix + path).getCanonicalFile();
  }
}
