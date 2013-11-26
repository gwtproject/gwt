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
import com.google.gwt.user.client.Window;

/**
 * HelloWorld application.
 */
public class Hello implements EntryPoint {

  @JsInterface(prototype = "HTMLElement")
  public interface HTMLElement {
    void setAttribute(String atr, String value);
  }

  @JsInterface
  public interface GoogleMap extends HTMLElement {}



  @JsInterface
  interface MyAnchor{}

  @JsInterface
  interface BaseInter {
    int m();
  }
  @JsInterface(prototype = "HTMLButtonElement")
  interface Button {
    void click();
  }

  @JsInterface
  interface SomeCallback {
    void callbackA();
  }
  static class Base implements BaseInter {
    public int m() {
      return 42;
    }
  }

  static class Child extends Base implements SomeCallback {

    @Override
    public void callbackA() {
      Window.alert("foo");
    }
  }

  public void onModuleLoad() {
    Button b = (Button) foo();
    b.click();
//    b.toString();
    register(new Child());

    MyAnchor map = abc(); // Fails here w/ CCE
    Window.alert("" + map);

    GoogleMap map2 = abc();
    map2.setAttribute("style","height:400px; display:block");
  }

  public static native Object foo() /*-{
    return $doc.createElement("button");
  }-*/;

  public static native void register(Base e) /*-{
    $wnd.__r = e;
  }-*/;

  private static native <T> T abc() /*-{
      return $doc.createElement("a");
  }-*/;
}
