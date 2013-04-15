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

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Functional test for UiHandler
 *
 * TODO(rluble); add more relevant tests.
 */
public class UiHandlerTest extends GWTTestCase {
  private WidgetBasedUi widgetUi;
  private DomBasedUi domUi;
  private com.google.gwt.user.client.ui.DockPanel root;

  @Override
  public String getModuleName() {
    return "com.google.gwt.uibinder.test.UiBinderSuite";
  }

  @Override
  public void gwtSetUp() throws Exception {
    super.gwtSetUp();
    UiBinderTestApp app = UiBinderTestApp.getInstance();
    widgetUi = app.getWidgetUi();
    domUi = app.getDomUi();
    root = widgetUi.root;
  }

  public void testValueChangeEvent() {
    widgetUi.event = null;
    widgetUi.myDoubleBox.setValue(0.0);
    widgetUi.myDoubleBox.setValue(10.0, true);
    assertNotNull(widgetUi.event);
    assertEquals(10.0, widgetUi.event.getValue());
  }

  /**
   * Tests that the code generated for handling events parametrized by wildcards work.
   */
  public void testValueChangeEventWildcard() {
    widgetUi.eventWildcard = null;
    widgetUi.myBadValueChangeWidget.setValue("");
    widgetUi.myBadValueChangeWidget.setValue("Changed");
    assertNotNull(widgetUi.eventWildcard);
    assertEquals("Changed", (String) widgetUi.eventWildcard.getValue());
  }
}