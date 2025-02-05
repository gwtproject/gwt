/*
 * Copyright 2025 GWT Project Authors
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
package com.google.gwt.user.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * IO and template utility classes for GWT command-line tools.
 */
public class CommandLineCreatorUtils {

  /**
   * @param parent Parent directory
   * @param fileName New file name
   * @param overwrite Is overwriting an existing file allowed?
   * @return Handle to the file
   * @throws IOException If the file cannot be created, or if the file already
   *           existed and overwrite was false.
   */
  public static File createNormalFile(File parent, String fileName,
                                      boolean overwrite, boolean ignore) throws IOException {
    File file = new File(parent, fileName);
    if (file.createNewFile()) {
      System.out.println("Created file " + file);
      return file;
    }

    if (!file.exists() || file.isDirectory()) {
      throw new IOException(file.getPath() + " : could not create normal file.");
    }

    if (ignore) {
      System.out.println(file + " already exists; skipping");
      return null;
    }

    if (!overwrite) {
      throw new IOException(
          file.getPath()
              + " : already exists; please remove it or use the -overwrite or -ignore option.");
    }

    System.out.println("Overwriting existing file " + file);
    return file;
  }

  /**
   * @param dirPath Requested path for the directory.
   * @param create Create the directory if it does not already exist?
   * @return A {@link File} representing a directory that now exists.
   * @throws IOException If the directory is not found and/or cannot be created.
   */
  public static File getDirectory(String dirPath, boolean create)
      throws IOException {
    return getDirectory(null, dirPath, create);
  }

  /**
   * @param parent Parent directory of the requested directory.
   * @param dirName Requested name for the directory.
   * @param create Create the directory if it does not already exist?
   * @return A {@link File} representing a directory that now exists.
   * @throws IOException If the directory is not found and/or cannot be created.
   */
  public static File getDirectory(File parent, String dirName, boolean create)
      throws IOException {
    File dir = new File(parent, dirName);
    boolean alreadyExisted = dir.exists();

    if (create) {
      // No need to check mkdirs result because we check for dir.exists()
      dir.mkdirs();
    }

    if (!dir.exists() || !dir.isDirectory()) {
      if (create) {
        throw new IOException(dir.getPath() + " : could not create directory.");
      } else {
        throw new IOException(dir.getPath() + " : could not find directory.");
      }
    }

    if (create && !alreadyExisted) {
      System.out.println("Created directory " + dir);
    }

    return dir;
  }

  /**
   * Gets the contents of a file from the class path as a String. Note: this
   * method is only guaranteed to work for resources in the same class loader
   * that contains this {@link CommandLineCreatorUtils} class.
   *
   * @param partialPath the partial path to the resource on the class path
   * @return the contents of the file
   * @throws IOException if the file could not be found or an error occurred
   *           while reading it
   */
  public static String getFileFromClassPath(String partialPath)
      throws IOException {
    try (InputStream in = CommandLineCreatorUtils.class.getClassLoader().getResourceAsStream(
        partialPath)) {
      if (in == null) {
        throw new FileNotFoundException(partialPath);
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  public static String getInstallPath() {
    return InstallPathHolder.INSTALL_PATH;
  }

  private static class InstallPathHolder {
    private static final String INSTALL_PATH = computeInstallationPath();
    private static String computeInstallationPath() {
      try {
        String override = System.getProperty("gwt.devjar");
        if (override == null) {
          String partialPath = CommandLineCreatorUtils.class.getName().replace('.', '/').concat(
              ".class");
          URL url = CommandLineCreatorUtils.class.getClassLoader().getResource(partialPath);
          if (url != null && "jar".equals(url.getProtocol())) {
            String path = url.toString();
            String jarPath = path.substring(path.indexOf("file:"),
                path.lastIndexOf('!'));
            File devJarFile = new File(URI.create(jarPath));
            if (!devJarFile.isFile()) {
              throw new IOException("Could not find jar file; "
                  + devJarFile.getCanonicalPath()
                  + " does not appear to be a valid file");
            }

            String dirPath = jarPath.substring(0, jarPath.lastIndexOf('/') + 1);
            File installDirFile = new File(URI.create(dirPath));
            if (!installDirFile.isDirectory()) {
              throw new IOException("Could not find installation directory; "
                  + installDirFile.getCanonicalPath()
                  + " does not appear to be a valid directory");
            }

            return installDirFile.getCanonicalPath().replace(
                File.separatorChar, '/');
          } else {
            throw new IOException(
                "Cannot determine installation directory; apparently not running from a jar");
          }
        } else {
          override = override.replace('\\', '/');
          int pos = override.lastIndexOf('/');
          if (pos < 0) {
            return "";
          } else {
            return override.substring(0, pos);
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(
            "Installation problem detected, please reinstall GWT", e);
      }
    }
  }

  public static void writeTemplateFile(File file, String contents,
                                       Map<String, String> replacements) throws IOException {
    String replacedContents = contents;
    Set<Map.Entry<String, String>> entries = replacements.entrySet();
    for (Iterator<Map.Entry<String, String>> iter = entries.iterator(); iter.hasNext();) {
      Map.Entry<String, String> entry = iter.next();
      String replaceThis = entry.getKey();
      String withThis = entry.getValue();
      withThis = withThis.replaceAll("\\\\", "\\\\\\\\");
      withThis = withThis.replaceAll("\\$", "\\\\\\$");
      replacedContents = replacedContents.replaceAll(replaceThis, withThis);
    }

    try (PrintWriter pw = new PrintWriter(file)) {
      LineNumberReader lnr = new LineNumberReader(new StringReader(replacedContents));
      for (String line = lnr.readLine(); line != null; line = lnr.readLine()) {
        pw.println(line);
      }
    }
  }
}
