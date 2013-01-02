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
//  br
public class PluralRuleImpl_br extends VariantSelectorBase {

  public PluralRuleImpl_br() {
    super(new VariantForm[] {
      VariantForm.FEW,
      VariantForm.MANY,
      VariantForm.ONE,
      VariantForm.TWO,
    });
  };

  @Override
  public String getFormDescription(VariantForm form) {
    switch (form) {
      case FEW:
        return "n mod 10 in 3..4,9 and n mod 100 not in 10..19,70..79,90..99";
      case MANY:
        return "n mod 1000000 is 0 and n is not 0";
      case ONE:
        return "n mod 10 is 1 and n mod 100 not in 11,71,91";
      case TWO:
        return "n mod 10 is 2 and n mod 100 not in 12,72,92";
      default:
        return "anything else";
    }
  }

  @Override
  public VariantForm select(double n) {
    double n10 = n % 10;
    double n100 = n % 100;
    double n1000000 = n % 1000000;
    if (((n - (long) n == 0.0) && ((n10 >= 3 && n10 <= 4) || (n10 == 9))) && ((n - (long) n != 0.0) || ((n100 < 10 || n100 > 19) && (n100 < 70 || n100 > 79) && (n100 < 90 || n100 > 99)))) {
      return VariantForm.FEW;
    }
    if ((n1000000 == 0) && (n != 0)) {
      return VariantForm.MANY;
    }
    if ((n10 == 1) && ((n - (long) n != 0.0) || ((n100 != 11) && (n100 != 71) && (n100 != 91)))) {
      return VariantForm.ONE;
    }
    if ((n10 == 2) && ((n - (long) n != 0.0) || ((n100 != 12) && (n100 != 72) && (n100 != 92)))) {
      return VariantForm.TWO;
    }
    return VariantForm.OTHER;
  }
}
