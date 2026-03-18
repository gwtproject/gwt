/*
 * Copyright 2007 Google Inc.
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
package com.google.gwt.sample.hello.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.Timer;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;

/**
 * HelloWorld application.
 */
public class Hello implements EntryPoint {
  private final String html = "<b>hello</b><i>world</i>";

  public void onModuleLoad() {
    RichTextArea rta = new RichTextArea();
    rta.setHTML(html);

    rta.addInitializeHandler(new InitializeHandler() {
      @Override
      public void onInitialize(InitializeEvent event) {
        new Timer() {
          @Override
          public void run() {
            if (html.equalsIgnoreCase(rta.getHTML())) {
              Window.alert("pass");
            } else {
              Window.alert("fail, expected: " + html + ", actual: " + rta.getHTML());
            }
          }
        }.schedule(100);
      }
    });
    RootPanel.get().add(rta);
  }
}
