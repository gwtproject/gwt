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

import static com.google.gwt.core.client.ScriptInjector.TOP_WINDOW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.core.client.js.JsProperty;
import com.google.gwt.core.client.js.JsType;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests JsType functionality.
 */
// TODO(cromwellian): Add test cases for property overriding of @JsProperty methods in java object
public class JsPropertyTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.Core";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    ScriptInjector.fromString("function MyJsInterface() {}\n"
        + "MyJsInterface.staticX = 33;"
        + "MyJsInterface.answerToLife = function() { return 42;};"
        + "MyJsInterface.prototype.sum = function sum(bias) { return this.x + bias; };")
        .setWindow(TOP_WINDOW).inject();
    patchPrototype(MyClassExtendsNativeJsType.class);
  }

  /**
   * Workaround for the fact that the script is injected after defineClass() has been called.
   */
  private native void patchPrototype(Class<MyClassExtendsNativeJsType> myClass) /*-{
      @java.lang.Class::getPrototypeForClass(Ljava/lang/Class;)(myClass).prototype = $wnd.MyClass;
  }-*/;

  @JsType
  interface MyJsTypeInterfaceWithProperty {
    @JsProperty
    int getX();

    @JsProperty
    void setX(int x);
  }

  static class MyJavaTypeImplementingMyJsTypeInterfaceWithProperty
      implements MyJsTypeInterfaceWithProperty {
    private int x;

    public int getX() {
      return x;
    }

    public void setX(int x) {
      this.x = x;
    }
  }

  public void testJavaClassImplementingMyJsTypeInterfaceWithProperty() {
    MyJavaTypeImplementingMyJsTypeInterfaceWithProperty obj =
        new MyJavaTypeImplementingMyJsTypeInterfaceWithProperty();
    assertEquals(0, readX(obj));
    assertEquals(0, obj.getX());

    writeX(obj, 10);
    assertEquals(10, readX(obj));
    assertEquals(10, obj.getX());

    obj.setX(12);
    assertEquals(12, readX(obj));
    assertEquals(12, obj.getX());

    MyJsTypeInterfaceWithProperty intf = new MyJavaTypeImplementingMyJsTypeInterfaceWithProperty();
    assertEquals(0, readX(intf));
    assertEquals(0, intf.getX());

    writeX(intf, 10);
    assertEquals(10, readX(intf));
    assertEquals(10, intf.getX());

    intf.setX(12);
    assertEquals(12, readX(intf));
    assertEquals(12, intf.getX());
  }

  @JsType
  static class MyJsTypeConcreteClass {
    private int x;

    @JsProperty
    public int getY() {
      return x;
    }

    @JsProperty
    public void setY(int x) {
      this.x = x;
    }
  }

  public void testConcreteJsType() {
    MyJsTypeConcreteClass obj = new MyJsTypeConcreteClass();
    assertEquals(0, readY(obj));
    assertEquals(0,obj.getY());

    writeY(obj, 10);
    assertEquals(10, readY(obj));
    assertEquals(10, obj.getY());

    obj.setY(12);
    assertEquals(12, readY(obj));
    assertEquals(12, obj.getY());
  }

  @JsType(prototype = "MyJsInterface")
  static class MyNativeJsTypeConcreteClass {

    public MyNativeJsTypeConcreteClass() { }
    public static int staticX;

    public static native int answerToLife();

    public int x;

    @JsProperty
    public native int getY();

    @JsProperty
    public native void setY(int x);

    public native int sum(int bias);
  }

  public void testConcreteNativeJsType() {
    assertEquals(33, MyNativeJsTypeConcreteClass.staticX);
    MyNativeJsTypeConcreteClass.staticX = 34;
    assertEquals(34, MyNativeJsTypeConcreteClass.staticX);
    assertEquals(42, MyNativeJsTypeConcreteClass.answerToLife());

    MyNativeJsTypeConcreteClass obj = new MyNativeJsTypeConcreteClass();
    assertTrue(isUndefined(obj.x));
    obj.x = 72;
    assertEquals(72, obj.x);
    assertEquals(74, obj.sum(2));

    assertTrue(isUndefined(obj.getY()));
    obj.setY(91);
    assertEquals(91, obj.getY());
  }

  static class MyClassExtendsNativeJsType extends MyNativeJsTypeConcreteClass {

    MyClassExtendsNativeJsType() {
      this.x = 42;
      setY(52);
    }

    @Override
    public int sum(int bias) {
      return super.sum(bias) + 100;
    }
  }

  public void testConcreteNativeType_sublasss() {
    MyClassExtendsNativeJsType mc = new MyClassExtendsNativeJsType();
    assertEquals(143, mc.sum(1));

    mc.x = -mc.x;
    assertEquals(58, mc.sum(0));

    assertEquals(52, mc.getY());
  }

  static class MyJsInterfaceWithPrototypeImplNeedsBridge
      extends SimpleAccidental implements MyJsInterfaceWithPrototype {
  }

  static abstract class SimpleAccidental {
    private int x;

    public int getX() {
      return x;
    }

    public void setX(int x) {
      this.x = x;
    }

    public int sum(int bias) {
      return bias + x;
    }
  }

  public void testJsPropertyBridges_accidental() {
    MyJsInterfaceWithPrototype object = new MyJsInterfaceWithPrototypeImplNeedsBridge();

    object.setX(3);
    assertEquals(3, object.getX());

    SimpleAccidental simpleAccidental = (SimpleAccidental) object;

    simpleAccidental.setX(3);
    assertEquals(3, simpleAccidental.getX());
    assertEquals(3, readX(object));

    writeX(object, 4);
    assertEquals(4, simpleAccidental.getX());
    assertEquals(4, readX(object));

    assertEquals(3 + 4, simpleAccidental.sum(3));
  }

  static class MyJsInterfaceWithPrototypeImplNeedsBridgeAndSubclass
      extends SimpleSubclass implements MyJsInterfaceWithPrototype {
  }

  static abstract class SimpleSubclass {
    private int x;

    public int getX() {
      return x;
    }

    public void setX(int x) {
      this.x = x;
    }

    public int sum(int bias) {
      return bias + x;
    }
  }

  static class MyJsInterfaceWithPrototypeImplNeedsBridgeSubclass
      extends MyJsInterfaceWithPrototypeImplNeedsBridgeAndSubclass {
    private int y;

    public int getX() {
      return y;
    }

    public void setX(int y) {
      this.y = y;
    }

    public void setParentX(int value) {
      super.setX(value);
    }

    public int getXPlusY() {
      return super.getX() + y;
    }
  }

  public void testJsPropertyIsX() {
    JsTypeIsProperty object = (JsTypeIsProperty) JavaScriptObject.createObject();

    assertFalse(object.isX());
    object.setX(true);
    assertTrue(object.isX());
    object.setX(false);
    assertFalse(object.isX());
  }

  public void testJsPropertyGetX() {
    JsTypeGetProperty object = (JsTypeGetProperty) JavaScriptObject.createObject();

    assertTrue(isUndefined(object.getX()));
    object.setX(10);
    assertEquals(10, object.getX());
    object.setX(0);
    assertEquals(0, object.getX());
  }

  public void testJsPropertyBridges_subclass() {
    MyJsInterfaceWithPrototype object = new MyJsInterfaceWithPrototypeImplNeedsBridgeSubclass();

    object.setX(3);
    assertEquals(3, object.getX());

    SimpleSubclass simple = (SimpleSubclass) object;

    simple.setX(3);
    assertEquals(3, simple.getX());
    assertEquals(3, readX(object));

    writeX(object, 4);
    assertEquals(4, simple.getX());
    assertEquals(4, readX(object));

    MyJsInterfaceWithPrototypeImplNeedsBridgeSubclass subclass =
        (MyJsInterfaceWithPrototypeImplNeedsBridgeSubclass) object;

    subclass.setParentX(5);
    assertEquals(8, simple.sum(3));
    assertEquals(9, subclass.getXPlusY());
  }

  @JsType
  interface MyJsInterfaceWithProtectedNames {
    String var();

    @JsProperty
    String getNullField(); // Defined in object scope but shouldn't obfuscate

    @JsProperty
    String getImport();

    @JsProperty
    void setImport(String str);
  }

  public void testProtectedNames() {
    MyJsInterfaceWithProtectedNames obj = createMyJsInterfaceWithProtectedNames();
    assertEquals("var", obj.var());
    assertEquals("nullField", obj.getNullField());
    assertEquals("import", obj.getImport());
    obj.setImport("import2");
    assertEquals("import2", obj.getImport());
  }

  private static native MyJsInterfaceWithProtectedNames createMyJsInterfaceWithProtectedNames() /*-{
    var a = {};
    a["nullField"] = "nullField";
    a["import"] = "import";
    a["var"] = function() { return "var"; };
    return a;
  }-*/;

  private static native boolean isUndefined(int value) /*-{
    return value === undefined;
  }-*/;

  private static native boolean hasField(Object object, String fieldName) /*-{
    return object[fieldName] != undefined;
  }-*/;

  private static native int readX(Object object) /*-{
    return object.x;
  }-*/;

  private static native void writeX(Object object, int value) /*-{
    object.x = value;
  }-*/;

  private static native int readY(Object object) /*-{
    return object.y;
  }-*/;

  private static native void writeY(Object object, int value) /*-{
    object.y = value;
  }-*/;

  public static void assertJsTypeHasFields(Object obj, String... fields) {
    for (String field : fields) {
      assertTrue("Field '" + field + "' should be exported", hasField(obj, field));
    }
  }

  public static void assertJsTypeDoesntHaveFields(Object obj, String... fields) {
    for (String field : fields) {
      assertFalse("Field '" + field + "' should not be exported", hasField(obj, field));
    }
  }
}
