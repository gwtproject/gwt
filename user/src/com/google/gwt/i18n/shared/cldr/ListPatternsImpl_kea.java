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
package com.google.gwt.i18n.shared.cldr;

import com.google.gwt.i18n.shared.ListPatterns;
import com.google.gwt.safehtml.shared.SafeHtml;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA:
//  cldrVersion=21.0
//  date=$Date: 2012-02-07 13:32:35 -0500 (Tue, 07 Feb 2012) $
//  number=$Revision: 6546 $
//  type=root

/**
 * List formatting for the "kea" locale.
 */
public class ListPatternsImpl_kea implements ListPatterns {
  
  @Override
  public String formatEntry(int index, int count, String left, String formattedTail) {
    switch (count) {
      case 2:
        return left + " y " + formattedTail;
    }
    if (index == 0) {
      return left + ", " + formattedTail;
    }
    if (index >= count - 2) {
      return left + " y " + formattedTail;
    }
    return left + ", " + formattedTail;
  }
  
  @Override
  public SafeHtml formatEntry(int index, int count, SafeHtml left, SafeHtml formattedTail) {
    switch (count) {
      case 2:
        return new com.google.gwt.safehtml.shared.SafeHtmlBuilder().append(left).appendHtmlConstant(" y ").append(formattedTail).toSafeHtml();
    }
    if (index == 0) {
      return new com.google.gwt.safehtml.shared.SafeHtmlBuilder().append(left).appendHtmlConstant(", ").append(formattedTail).toSafeHtml();
    }
    if (index >= count - 2) {
      return new com.google.gwt.safehtml.shared.SafeHtmlBuilder().append(left).appendHtmlConstant(" y ").append(formattedTail).toSafeHtml();
    }
    return new com.google.gwt.safehtml.shared.SafeHtmlBuilder().append(left).appendHtmlConstant(", ").append(formattedTail).toSafeHtml();
  }
}
