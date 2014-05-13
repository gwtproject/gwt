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
