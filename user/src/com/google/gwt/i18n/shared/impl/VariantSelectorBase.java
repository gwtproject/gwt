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
package com.google.gwt.i18n.shared.impl;

import com.google.gwt.i18n.shared.AlternateMessageSelector;
import com.google.gwt.i18n.shared.VariantSelector;

/**
 * Base class for {@link VariantSelector} implementations.
 */
public abstract class VariantSelectorBase implements VariantSelector, AlternateMessageSelector {

  private final VariantForm[] forms;
  
  protected VariantSelectorBase(VariantForm[] forms) {
    this.forms = forms;
  }

  @Override
  public final VariantForm[] getForms() {
    return forms;
  }

  @Override
  public abstract String getFormDescription(VariantForm form);

  @Override
  public abstract VariantForm select(double n);

  @Override
  public final boolean isFormAcceptable(String form) {
    // TODO(jat): should this be here or elsewhere?
    if (form.startsWith("=")) {
      return true;
    }
    if (OTHER_FORM_NAME.equals(form)) {
      return true;
    }
    for (VariantForm availableForm : forms) {
      if (availableForm.name().equals(form)) {
        return true;
      }
    }
    return false;
  }
}
