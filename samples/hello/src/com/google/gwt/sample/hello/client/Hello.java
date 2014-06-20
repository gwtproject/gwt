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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * HelloWorld application.
 */
public class Hello implements EntryPoint {

  public void onModuleLoad() {
    final Double x = 13.0;
    final Boolean bool = false;

    Button b = new Button("Click me", new ClickHandler() {
      public void onClick(ClickEvent event) {
        double z = 12 + x;
        Object o = Math.random() > 0.5 ? z : null;
        Window.alert("Hello, AJAX " + z);
        Window.alert("Hello, AJAX " + (o != null));
        Window.alert("Hello, AJAX " + x.equals("Foo"));
        Window.alert("Hello, AJAX " + o.equals("Foo"));
        Window.alert("Hello, AJAX " + o.equals(13.0));
        Window.alert("Hello, AJAX " + o.equals(new Double(13.0)));
        Window.alert("Hello, AJAX " + o.equals(new Double("13.0")));
        Window.alert("Hello, AJAX " + x.equals(13.0));
        Window.alert("Hello, AJAX " + (x.hashCode() == 10));
        Window.alert("Hello Boolean should be true " + (bool.equals(Boolean.FALSE)));
        Window.alert("Hello Boolean should be false " + (bool.equals(new Boolean("true"))));
        Window.alert("Hello Boolean should be false " + (bool.equals(Boolean.parseBoolean("true"))));
        Window.alert("Hello Boolean should be false " + (bool.equals(true)));
        Window.alert("Hello Boolean should be false " + (bool.equals("true")));

        Window.alert("Hello Boolean should be 1237 " + (bool.hashCode()));

      }
    });

    RootPanel.get().add(b);
  }
}
