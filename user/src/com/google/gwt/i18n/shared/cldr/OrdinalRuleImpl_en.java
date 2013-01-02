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
//  en
public class OrdinalRuleImpl_en extends VariantSelectorBase {

  public OrdinalRuleImpl_en() {
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
        return "n mod 10 is 3 and n mod 100 is not 13";
      case ONE:
        return "n mod 10 is 1 and n mod 100 is not 11";
      case TWO:
        return "n mod 10 is 2 and n mod 100 is not 12";
      default:
        return "anything else";
    }
  }

  @Override
  public VariantForm select(double n) {
    double n10 = n % 10;
    double n100 = n % 100;
    if ((n10 == 3) && (n100 != 13)) {
      return VariantForm.FEW;
    }
    if ((n10 == 1) && (n100 != 11)) {
      return VariantForm.ONE;
    }
    if ((n10 == 2) && (n100 != 12)) {
      return VariantForm.TWO;
    }
    return VariantForm.OTHER;
  }
}
