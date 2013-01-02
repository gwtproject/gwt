/*
 * Copyright 2012 Google Inc.
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

import com.google.gwt.core.client.GwtScriptOnly;

import com.google.gwt.i18n.shared.DateTimeFormatInfo;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA:
//  cldrVersion=21.0
//  date=$Date: 2011-09-21 14:34:31 -0400 (Wed, 21 Sep 2011) $
//  number=$Revision: 6157 $
//  type=GB
@GwtScriptOnly   // use the pure Java version in DevMode for speed
public class LocaleInfoImpl_gd_GB extends LocaleInfoImpl_gd {

  @Override
  public String getLocaleName() {
    return "gd_GB";
  }

  @Override
  protected DateTimeFormatInfo createDateTimeFormatInfo() {
    return new com.google.gwt.i18n.shared.cldr.DateTimeFormatInfoImpl_gd_GB();
  }
}
