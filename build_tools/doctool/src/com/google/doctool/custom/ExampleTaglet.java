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
package com.google.doctool.custom;

import com.google.doctool.LinkResolver;
import com.google.doctool.LinkResolver.ExtraClassResolver;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * A taglet for slurping examples into javadoc output.
 */
public class ExampleTaglet implements Taglet {

  public static void register(Map tagletMap) {
    ExampleTaglet tag = new ExampleTaglet();
    Taglet t = (Taglet) tagletMap.get(tag.getName());
    if (t != null) {
      tagletMap.remove(tag.getName());
    }
    tagletMap.put(tag.getName(), tag);
  }

  @Override
  public String getName() {
    return "example";
  }

  @Override
  public boolean inConstructor() {
    return true;
  }

  @Override
  public boolean inField() {
    return true;
  }

  @Override
  public boolean inMethod() {
    return true;
  }

  @Override
  public boolean inOverview() {
    return true;
  }

  @Override
  public boolean inPackage() {
    return true;
  }

  @Override
  public boolean inType() {
    return true;
  }

  @Override
  public boolean isInlineTag() {
    return true;
  }

  @Override
  public String toString(Tag tag) {
    SourcePosition position = LinkResolver.resolveLink(tag,
        new ExtraClassResolver() {
          @Override
          public ClassDoc findClass(String className) {
            return GWTJavaDoclet.root.classNamed(className);
          }
        });

    String slurpSource = slurpSource(position);
    // The <pre> tag still requires '<' and '>' characters to be escaped
    slurpSource = slurpSource.replace("<", "&lt;");
    slurpSource = slurpSource.replace(">", "&gt;");
    return "<blockquote><pre>" + slurpSource + "</pre></blockquote>";
  }

  @Override
  public String toString(Tag[] tags) {
    if (tags == null || tags.length == 0) {
      return null;
    }
    String result = "";
    for (int i = 0; i < tags.length; i++) {
      result += toString(tags[i]);
    }
    return result;
  }

  private static String slurpSource(SourcePosition position) {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(position.file()));
      for (int i = 0, n = position.line() - 1; i < n; ++i) {
        br.readLine();
      }

      StringBuffer lines = new StringBuffer();
      String line = br.readLine();
      int braceDepth = 0;
      int indent = -1;
      boolean seenSemiColonOrBrace = false;
      while (line != null) {
        if (indent == -1) {
          for (indent = 0; Character.isWhitespace(line.charAt(indent)); ++indent) {
            // just accumulate
          }
        }

        if (line.length() >= indent) {
          line = line.substring(indent);
        }

        lines.append(line).append("\n");
        for (int i = 0, n = line.length(); i < n; ++i) {
          char c = line.charAt(i);
          if (c == '{') {
            seenSemiColonOrBrace = true;
            ++braceDepth;
          } else if (c == '}') {
            --braceDepth;
          } else if (c == ';') {
            seenSemiColonOrBrace = true;
          }
        }

        if (braceDepth > 0 || !seenSemiColonOrBrace) {
          line = br.readLine();
        } else {
          break;
        }
      }

      return lines.toString();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (br != null) {
          br.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return "";
  }

}
