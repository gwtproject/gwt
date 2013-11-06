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
package com.google.gwt.uibinder.test.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Integration tests covering potential name conflicts in {@link UiBinder}.
 */
public class UiBinderNameConflictsTest extends GWTTestCase {

  static class Ui extends Composite {
    interface Binder extends UiBinder<HTMLPanel, Ui> {
    }

    @UiField Button template;
    @UiField Button owner;
  }

  @Override
  public String getModuleName() {
    return "com.google.gwt.uibinder.test.UiBinderSuite";
  }

  public void testNameConflicts() {
    Ui.Binder binder = GWT.create(Ui.Binder.class);
    Ui subject = new Ui();
    binder.createAndBindUi(subject);

    assertEquals("templateBtn", subject.template.getText());
    assertEquals("ownerBtn", subject.owner.getText());
  }
}
