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
 * Plural rule implementation for locales:
 *   iu kw naq se sma smi smj smn sms
 */
public class PluralRuleImpl_7b518ead extends VariantSelectorBase {
  
  public PluralRuleImpl_7b518ead() {
    super(new VariantForm[] {
      VariantForm.ONE,
      VariantForm.TWO,
    });
  };
  
  @Override
  public String getFormDescription(VariantForm form) {
    switch (form) {
      case ONE:
        return "n is 1";
      case TWO:
        return "n is 2";
      default:
        return "anything else";
    }
  }
  
  @Override
  public VariantForm select(double n) {
    if (n == 1) {
      return VariantForm.ONE;
    }
    if (n == 2) {
      return VariantForm.TWO;
    }
    return VariantForm.OTHER;
  }
}
