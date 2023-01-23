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

import com.sun.source.doctree.DocTree;

import javax.lang.model.element.Element;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * A taglet for including GWT tip tags in javadoc output.
 */
public class TipTaglet extends AbstractTaglet {

  @Override
  public String getName() {
    return "tip";
  }

  @Override
  public Set<Location> getAllowedLocations() {
    return EnumSet.allOf(Location.class);
  }

  @Override
  public String toString(List<? extends DocTree> list, Element element) {
    if (list == null || list.size() == 0) {
      return null;
    }
    StringBuilder result = new StringBuilder("<DT><B>Tip:</B></DT><DD>");
    if (list.size() == 1) {
      result.append(getHtmlContent(list.get(0)));
    } else {
      result.append("<UL>");
      for (int i = 0; i < list.size(); i++) {
        result.append("<LI>");
        result.append(getHtmlContent(list.get(i)));
        result.append("</LI>");
      }
      result.append("</UL>");
    }
    result.append("</DD>");
    return result.toString();
  }

  @Override
  public boolean isInlineTag() {
    return false;
  }

}
