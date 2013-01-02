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
//  gd
public class PluralRuleImpl_gd extends VariantSelectorBase {

  public PluralRuleImpl_gd() {
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
        return "n in 3..10,13..19";
      case ONE:
        return "n in 1,11";
      case TWO:
        return "n in 2,12";
      default:
        return "anything else";
    }
  }

  @Override
  public VariantForm select(double n) {
    if ((n - (long) n == 0.0) && ((n >= 3 && n <= 10) || (n >= 13 && n <= 19))) {
      return VariantForm.FEW;
    }
    if ((n - (long) n == 0.0) && ((n == 1) || (n == 11))) {
      return VariantForm.ONE;
    }
    if ((n - (long) n == 0.0) && ((n == 2) || (n == 12))) {
      return VariantForm.TWO;
    }
    return VariantForm.OTHER;
  }
}
