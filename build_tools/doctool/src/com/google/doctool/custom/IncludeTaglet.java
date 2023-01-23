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

import com.google.doctool.ResourceIncluder;

import com.sun.source.doctree.DocTree;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * A taglet for slurping in the content of artbitrary files appearing on the
 * classpath into javadoc.
 */
public class IncludeTaglet extends AbstractTaglet {

  @Override
  public String getName() {
    return "gwt.include";
  }

  @Override
  public String toString(List<? extends DocTree> list, Element element) {
    StringBuilder results = new StringBuilder();
    for (DocTree docTree : list) {
      String text = getHtmlContent(docTree);

      try {
        String contents = ResourceIncluder.getResourceFromClasspathScrubbedForHTML(text);
        results.append("<blockquote><pre>").append(contents).append("</pre></blockquote>");
      } catch (IOException e) {
        e.printStackTrace();
        printMessage(Diagnostic.Kind.ERROR, "Error in reading file: " + e.getMessage(), element,
                docTree);
        // return empty to let javadoc report this
        return "";
      }
    }
    return results.toString();
  }

  @Override
  public Set<Location> getAllowedLocations() {
    return EnumSet.allOf(Location.class);
  }

  @Override
  public boolean isInlineTag() {
    return true;
  }

}
