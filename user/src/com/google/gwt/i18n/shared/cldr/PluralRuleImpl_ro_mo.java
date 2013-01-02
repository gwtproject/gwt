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

import com.google.gwt.i18n.shared.impl.VariantSelectorBase;

// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA
//  ro mo
public class PluralRuleImpl_ro_mo extends VariantSelectorBase {

  public PluralRuleImpl_ro_mo() {
    super(new VariantForm[] {
      VariantForm.FEW,
      VariantForm.ONE,
    });
  };

  @Override
  public String getFormDescription(VariantForm form) {
    switch (form) {
      case FEW:
        return "n is 0 OR n is not 1 AND n mod 100 in 1..19";
      case ONE:
        return "n is 1";
      default:
        return "anything else";
    }
  }

  @Override
  public VariantForm select(double n) {
    double n100 = n % 100;
    if ((n == 0) || ((n != 1) && ((n - (long) n == 0.0) && (n100 >= 1 && n100 <= 19)))) {
      return VariantForm.FEW;
    }
    if (n == 1) {
      return VariantForm.ONE;
    }
    return VariantForm.OTHER;
  }
}
