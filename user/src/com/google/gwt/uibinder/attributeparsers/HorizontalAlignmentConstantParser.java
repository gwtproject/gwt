/*
 * Copyright 2007 Google Inc.
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
package com.google.gwt.uibinder.attributeparsers;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.uibinder.rebind.MortalLogger;
import com.google.gwt.uibinder.rebind.XMLElement;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import java.util.HashMap;
import java.util.Locale;

/**
 * Parses a
 * {@link com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant}
 * .
 */
class HorizontalAlignmentConstantParser extends StrictAttributeParser {

  private static final String PREFIX = HasHorizontalAlignment.class.getCanonicalName()
      + ".ALIGN_";
  private static final HashMap<String, String> values = new HashMap<String, String>();

  static {
    values.put("LEFT", PREFIX + "LEFT");
    values.put("CENTER", PREFIX + "CENTER");
    values.put("RIGHT", PREFIX + "RIGHT");
    values.put("JUSTIFY", PREFIX + "JUSTIFY");
    values.put("DEFAULT", PREFIX + "DEFAULT");
    values.put("LOCALE_START", PREFIX + "LOCALE_START");
    values.put("LOCALE_END", PREFIX + "LOCALE_END");
    values.put("ALIGN_LEFT", PREFIX + "LEFT");
    values.put("ALIGN_CENTER", PREFIX + "CENTER");
    values.put("ALIGN_RIGHT", PREFIX + "RIGHT");
    values.put("ALIGN_JUSTIFY", PREFIX + "JUSTIFY");
    values.put("ALIGN_DEFAULT", PREFIX + "DEFAULT");
    values.put("ALIGN_LOCALE_START", PREFIX + "LOCALE_START");
    values.put("ALIGN_LOCALE_END", PREFIX + "LOCALE_END");
  }

  HorizontalAlignmentConstantParser(FieldReferenceConverter converter,
      JType type, MortalLogger logger) {
    super(converter, logger, type);
  }

  @Override
  public String parse(XMLElement source, String value) throws UnableToCompleteException {
    String translated = values.get(value.toUpperCase(Locale.ROOT));
    if (translated != null) {
      return translated;
    }
    return super.parse(source, value);
  }
}
