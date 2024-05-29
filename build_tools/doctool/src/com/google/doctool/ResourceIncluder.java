/*
 * Copyright 2006 Google Inc.
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
package com.google.doctool;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility methods related to including external resources in doc.
 */
public class ResourceIncluder {

  /**
   * Copied from {@link com.google.gwt.util.tools.Utility#close(AutoCloseable)}.
   */
  public static void close(AutoCloseable is) {
    try {
      if (is != null) {
        is.close();
      }
    } catch (Exception e) {
    }
  }

  public static String getResourceFromClasspathScrubbedForHTML(String partialPath)
          throws IOException {
    String contents;
    contents = getFileFromClassPath(partialPath);
    contents = scrubForHtml(contents);
    return contents;
  }

  /**
   * Copied from
   * {@link com.google.gwt.util.tools.Utility#getFileFromClassPath(String)}.
   */
  private static String getFileFromClassPath(String partialPath)
      throws IOException {
    InputStream in = ResourceIncluder.class.getClassLoader().getResourceAsStream(
        partialPath);
    try {
      if (in == null) {
        throw new FileNotFoundException(partialPath);
      }
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int bytesRead;
      while (true) {
        bytesRead = in.read(buffer);
        if (bytesRead >= 0) {
          // Copy the bytes out.
          os.write(buffer, 0, bytesRead);
        } else {
          // End of input stream.
          break;
        }
      }

      return os.toString(StandardCharsets.UTF_8);
    } finally {
      close(in);
    }
  }

  private static String scrubForHtml(String contents) {
    char[] chars = contents.toCharArray();
    int len = chars.length;
    StringBuffer sb = new StringBuffer(len);
    for (int i = 0; i < len; ++i) {
      char c = chars[i];
      switch (c) {
        case '\r':
          // collapse \r\n into \n
          if (i == len - 1 || chars[i + 1] != '\n') {
            sb.append('\n');
          }
          break;
        case '&':
          sb.append("&amp;");
          break;
        case '<':
          sb.append("&lt;");
          break;
        case '>':
          sb.append("&gt;");
          break;
        default:
          sb.append(c);
          break;
      }
    }
    return sb.toString();
  }
}
