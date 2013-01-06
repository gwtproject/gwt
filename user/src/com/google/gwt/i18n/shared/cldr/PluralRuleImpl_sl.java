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
 *   sl
 */
public class PluralRuleImpl_sl extends VariantSelectorBase {
  
  public PluralRuleImpl_sl() {
    super(new VariantForm[] {
      VariantForm.FEW,
      VariantForm.ONE,
      VariantForm.TWO,
    });
  };
  
  @Override
  public String getFormDescription(VariantForm form) {
    switch (form) {
      case FEW:
        return "n mod 100 in 3..4";
      case ONE:
        return "n mod 100 is 1";
      case TWO:
        return "n mod 100 is 2";
      default:
        return "anything else";
    }
  }
  
  @Override
  public VariantForm select(double n) {
    double n100 = n % 100;
    if ((n - (long) n == 0.0) && (n100 >= 3 && n100 <= 4)) {
      return VariantForm.FEW;
    }
    if (n100 == 1) {
      return VariantForm.ONE;
    }
    if (n100 == 2) {
      return VariantForm.TWO;
    }
    return VariantForm.OTHER;
  }
}
