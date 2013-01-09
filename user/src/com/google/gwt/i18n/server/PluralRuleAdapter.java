/*
 * Copyright 2011 Google Inc.
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
package com.google.gwt.i18n.server;

import com.google.gwt.i18n.client.PluralRule;
import com.google.gwt.i18n.client.PluralRule.PluralForm;
import com.google.gwt.i18n.shared.AlternateMessageSelector;

/**
 * Adapter between {@link PluralRule} and {@link AlternateMessageSelector}.
 * 
 * <p>Note that this is temporary - eventually, {@link PluralRule} will
 * implement {@link AlternateMessageSelector}.
 */
public class PluralRuleAdapter implements AlternateMessageSelector {

  private final PluralForm[] pluralForms;

  public PluralRuleAdapter(PluralRule pluralRule) {
    pluralForms = pluralRule.pluralForms();
  }

  @Override
  public boolean isFormAcceptable(String form) {
    if (form.startsWith("=")) {
      return true;
    }
    for (PluralForm pluralForm : pluralForms) {
      if (pluralForm.getName().equals(form)) {
        return true;
      }
    }
    return false;
  }
}