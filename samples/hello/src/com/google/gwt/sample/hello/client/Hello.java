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
import com.google.gwt.core.client.js.JsExport;
import com.google.gwt.core.client.js.JsInterface;
import com.google.gwt.user.client.Window;

/**
 * HelloWorld application.
 */
public class Hello implements EntryPoint {

  interface Callback {
    void run();
  }

  @JsInterface(prototype = "$wnd.MyClass")
  public interface MyClass {
    int getX();
    void doIt(Callback cb);

      public static class Prototype implements MyClass {
          public Prototype(int i) {
          }

          @Override
          public native int getX() /*-{
              return 2;
          }-*/;

        @Override
        public native void doIt(Callback cb) /*-{
            return;
        }-*/;
      }
  }

  static class MyChildClass extends MyClass.Prototype {

    public MyChildClass() {
      super(99);
    }

    @JsExport("$wnd.MyChildClass.anotherMethod")
    public static void anotherMethod() { Window.alert("Hello");}
    @Override
      public int getX() {
          return               super.getX() +
              10;
      }
  }

  public void onModuleLoad() {
     MyChildClass foo = new MyChildClass();
    foo.doIt(new Callback() {
      @Override
      public void run() {
        Window.alert("hello");
      }
    });
     Object bar = Math.random() > 0.0001 ? foo : "Hello";
     Window.alert((bar instanceof MyClass)+"");
     Window.alert("getX = " + foo.getX());
  }

}
