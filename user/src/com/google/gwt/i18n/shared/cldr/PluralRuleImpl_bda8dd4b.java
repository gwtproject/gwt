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
 *   asa af bem bez bg bn brx ca cgg chr da de dv ee el en eo es et eu fi fo fur fy gl gsw gu ha haw he is it jmc kaj kcg kk kl ksb ku lb lg mas ml mn mr nah nb nd ne nl nn no nr ny nyn om or pa pap ps pt rof rm rwk saq seh sn so sq ss ssy st sv sw syr ta te teo tig tk tn ts ur wae ve vun xh xog zu
 */
public class PluralRuleImpl_bda8dd4b extends VariantSelectorBase {
  
  public PluralRuleImpl_bda8dd4b() {
    super(new VariantForm[] {
      VariantForm.ONE,
    });
  };
  
  @Override
  public String getFormDescription(VariantForm form) {
    switch (form) {
      case ONE:
        return "n is 1";
      default:
        return "anything else";
    }
  }
  
  @Override
  public VariantForm select(double n) {
    if (n == 1) {
      return VariantForm.ONE;
    }
    return VariantForm.OTHER;
  }
}
