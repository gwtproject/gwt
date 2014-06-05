/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.tools.cldr;

import org.unicode.cldr.util.XPathParts;
import java.util.Map;

/**
 * A query XPath, to be compared to a CLDR XPath.
 */
public class XPathQuery {

  private final XPathParts query;
  
  public XPathQuery(String query) {
    this.query = new XPathParts();
    this.query.set(query);
  }
  
  public boolean matches(XPathParts entry) {
    for (int i = 0; i < query.size(); ++i) {
      if (i >= entry.size()) {
        return false;
      }
      String qe = query.getElement(i);
      if (!qe.equals(entry.getElement(i))) {
        return false;
      }
      for (Map.Entry<String, String> attr : query.getAttributes(i).entrySet()) {
        if (!attr.getValue().equals(entry.getAttributes(i).get(attr.getKey()))) {
          return false;
        }
      }
    }
    return true;
  }
  
  @Override
  public String toString() {
    return query.toString();
  }
}
