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

import com.sun.javadoc.Tag;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.util.DocTreeScanner;
import com.sun.source.util.SimpleDocTreeVisitor;
import jdk.javadoc.doclet.Taglet;

import javax.lang.model.element.Element;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A taglet for slurping in the content of artbitrary files appearing on the
 * classpath into javadoc.
 */
public class IncludeTaglet implements Taglet {

  @Override
  public String getName() {
    return "gwt.include";
  }

  @Override
  public String toString(List<? extends DocTree> list, Element element) {
    //TODO handle more than one
    for (DocTree docTree : list) {
      return docTree.accept(new DocTreeScanner<String, Void>() {
        @Override
        public String visitText(TextTree node, Void unused) {
          String contents = ResourceIncluder.getResourceFromClasspathScrubbedForHTML(node.getBody());
          return "<blockquote><pre>" + contents + "</pre></blockquote>";
        }
      }, null);
    }
    return null;
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
