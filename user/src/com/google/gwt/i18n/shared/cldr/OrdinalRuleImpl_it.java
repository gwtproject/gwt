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

import com.google.gwt.i18n.shared.impl.VariantSelectorBase;
// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA

/**
 * Ordinal rule implementation for locales:
 *   it
 */
public class OrdinalRuleImpl_it extends VariantSelectorBase {
  
  public OrdinalRuleImpl_it() {
    super(new VariantForm[] {
      VariantForm.MANY,
    });
  };
  
  @Override
  public String getFormDescription(VariantForm form) {
    switch (form) {
      case MANY:
        return "n in 11,8,80,800";
      default:
        return "anything else";
    }
  }
  
  @Override
  public VariantForm select(double n) {
    if ((n - (long) n == 0.0) && ((n == 11) || (n == 8) || (n == 80) || (n == 800))) {
      return VariantForm.MANY;
    }
    return VariantForm.OTHER;
  }
}
