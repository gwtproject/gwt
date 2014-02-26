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
import com.google.gwt.core.client.js.JsInterface;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * HelloWorld application.
 */
public class Hello implements EntryPoint {

  public interface ArrayLike{
    Object get(int i);
  }

  public interface MapLike {
    void put(String key, String value);
  }

  @JsInterface(prototype="HTMLElement")
  public interface HTMLElement extends ArrayLike {
    void addEventListener(String foo);
  }

  @JsInterface(prototype="HTMLButtonElement")
  public interface HTMLButtonElement extends HTMLElement, MapLike {
    void click();
  }


  public void onModuleLoad() {

  }
}
