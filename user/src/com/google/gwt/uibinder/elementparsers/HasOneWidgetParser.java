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
package com.google.gwt.uibinder.elementparsers;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.uibinder.rebind.FieldWriter;
import com.google.gwt.uibinder.rebind.UiBinderWriter;
import com.google.gwt.uibinder.rebind.XMLElement;

/**
 * Parses any widgets that implement
 * {@link com.google.gwt.user.client.ui.HasOneWidget}.
 *
 * This handles all panels that support a single-argument setWidget() method.
 */
public class HasOneWidgetParser implements ElementParser {

  public void parse(XMLElement elem, String fieldName, JClassType type,
      UiBinderWriter writer) throws UnableToCompleteException {
    boolean childFound = false;

    // Parse single child (throws error if more than one is found)
    for (XMLElement child : elem.consumeChildElements()) {
      if (childFound) {
        writer.die("More than one child found");
      }
      if (!writer.isWidgetElement(child)) {
        writer.die(child, "Child of %s expected to be widget", elem);
      }
      FieldWriter childField = writer.parseElementToField(child);
      writer.addStatement("%1$s.setWidget(%2$s);", fieldName,
        childField.getNextReference());
      childFound = true;
    }
  }
}
