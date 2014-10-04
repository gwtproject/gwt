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
package com.google.gwt.resources.converter;

import com.google.gwt.resources.css.ast.Context;
import com.google.gwt.resources.css.ast.CssProperty;
import com.google.gwt.resources.css.ast.CssProperty.ListValue;
import com.google.gwt.resources.css.ast.CssVisitor;

/**
 * Escapes white spaces in font-family declarations, thus allowing usage of fonts that might
 * be mistaken for constants.
 */
public class FontFamilyVisitor extends CssVisitor {
  @Override
  public boolean visit(CssProperty x, Context ctx) {

    if ("font-family".equals(x.getName())) {
      ListValue values = x.getValues();
      String css = values.toCss();
      StringBuilder valueBuilder = new StringBuilder();

      boolean first = true;

      for (String subProperty : css.split(",")) {
        if (first) {
          first = false;
        } else {
          valueBuilder.append(",");
        }
        subProperty = subProperty.trim();

        if (subProperty.contains(" ")) {
          valueBuilder.append("'" + subProperty + "'");
        } else {
          valueBuilder.append(subProperty);
        }
      }

      x.setValue(new SimpleValue(valueBuilder.toString()));
    }
    return false;
  }
}
