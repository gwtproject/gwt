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
package com.google.gwt.core.client.interop;

import com.google.gwt.core.client.js.JsProperty;
import com.google.gwt.core.client.js.JsType;
import com.google.gwt.core.client.js.impl.PrototypeOfJsType;

@JsType(prototype = "$wnd.MyJsInterface")
interface MyJsInterface {

  @JsType(prototype = "MyJsInterface")
  interface LocalMyClass {
  }

  @JsType
  interface ButtonLikeJso {
  }

  @JsProperty
  int x();

  @JsProperty MyJsInterface x(int a);

  @JsProperty
  int getY();

  @JsProperty
  void setY(int a);

  int sum(int bias);

  @PrototypeOfJsType
  static class Prototype implements MyJsInterface {

    @Override
    public int x() {
      return 0;
    }

    @Override
    public MyJsInterface x(int a) {
      return this;
    }

    @Override
    public int getY() {
      return 0;
    }

    @Override
    public void setY(int a) {
    }

    @Override
    public int sum(int bias) {
      return 0;
    }
  }
}
