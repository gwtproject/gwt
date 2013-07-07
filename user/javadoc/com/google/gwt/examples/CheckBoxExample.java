/*
 * Copyright 2006 Google Inc.
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
package com.google.gwt.examples;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.RootPanel;

public class CheckBoxExample implements EntryPoint {

  @Override
  public void onModuleLoad() {
    // Make a new check box, and select it by default.
    CheckBox cb = new CheckBox("Foo");
    cb.setValue(true);

    // Hook up a handler to find out when it's clicked.
    cb.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        boolean checked = ((CheckBox) event.getSource()).getValue();
        Window.alert("It is " + (checked ? "" : "not ") + "checked");
      }
    });

    // Add it to the root panel.
    RootPanel.get().add(cb);
  }
}