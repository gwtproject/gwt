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
//  cy
public class PluralRuleImpl_cy extends VariantSelectorBase {

  public PluralRuleImpl_cy() {
    super(new VariantForm[] {
      VariantForm.FEW,
      VariantForm.MANY,
      VariantForm.ONE,
      VariantForm.TWO,
      VariantForm.ZERO,
    });
  };

  @Override
  public String getFormDescription(VariantForm form) {
    switch (form) {
      case FEW:
        return "n is 3";
      case MANY:
        return "n is 6";
      case ONE:
        return "n is 1";
      case TWO:
        return "n is 2";
      case ZERO:
        return "n is 0";
      default:
        return "anything else";
    }
  }

  @Override
  public VariantForm select(double n) {
    if (n == 3) {
      return VariantForm.FEW;
    }
    if (n == 6) {
      return VariantForm.MANY;
    }
    if (n == 1) {
      return VariantForm.ONE;
    }
    if (n == 2) {
      return VariantForm.TWO;
    }
    if (n == 0) {
      return VariantForm.ZERO;
    }
    return VariantForm.OTHER;
  }
}
