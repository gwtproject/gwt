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
 *   pl
 */
public class PluralRuleImpl_pl extends VariantSelectorBase {
  
  public PluralRuleImpl_pl() {
    super(new VariantForm[] {
      VariantForm.FEW,
      VariantForm.MANY,
      VariantForm.ONE,
    });
  };
  
  @Override
  public String getFormDescription(VariantForm form) {
    switch (form) {
      case FEW:
        return "n mod 10 in 2..4 and n mod 100 not in 12..14";
      case MANY:
        return "n is not 1 and n mod 10 in 0..1 or n mod 10 in 5..9 or n mod 100 in 12..14";
      case ONE:
        return "n is 1";
      default:
        return "anything else";
    }
  }
  
  @Override
  public VariantForm select(double n) {
    double n10 = n % 10;
    double n100 = n % 100;
    if (((n - (long) n == 0.0) && (n10 >= 2 && n10 <= 4)) && ((n - (long) n != 0.0) || (n100 < 12 || n100 > 14))) {
      return VariantForm.FEW;
    }
    if ((((n != 1) && ((n - (long) n == 0.0) && (n10 >= 0 && n10 <= 1))) || ((n - (long) n == 0.0) && (n10 >= 5 && n10 <= 9))) || ((n - (long) n == 0.0) && (n100 >= 12 && n100 <= 14))) {
      return VariantForm.MANY;
    }
    if (n == 1) {
      return VariantForm.ONE;
    }
    return VariantForm.OTHER;
  }
}
