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
import com.google.gwt.junit.client.GWTTestCase;

import java.util.Iterator;

/**
 * Tests JsType functionality.
 */
// TODO(cromwellian): Add test cases for property overriding of @JsProperty methods in java object
public class JsTypeTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.Core";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    ScriptInjector.fromString("function MyJsInterface() {}\n"
        + "MyJsInterface.prototype.sum = function sum(bias) { return this.x + this.y + bias; }\n"
        + "MyJsInterface.prototype.go = function(cb) { cb('Hello'); }\n"
        + "MyJsInterface.prototype.callBack = function(f) { return f(500); }")
        .setWindow(TOP_WINDOW).inject();
    patchPrototype(MyClassExtendsJsPrototype.class);
  }

  /**
   * Workaround for the fact that the script is injected after defineClass() has been called.
   */
  private native void patchPrototype(Class<MyClassExtendsJsPrototype> myClass) /*-{
      @java.lang.Class::getPrototypeForClass(Ljava/lang/Class;)(myClass).prototype = $wnd.MyClass;
  }-*/;

  public void testVirtualUpRefs() {
    ListImpl listWithExport = new ListImpl(); // Exports .add().
    FooImpl listNoExport = new FooImpl(); // Does not export .add().

    // Use a loose type reference to force polymorphic dispatch.
    Collection collectionWithExport = alwaysTrue() ? listWithExport : listNoExport;
    collectionWithExport.add("Loose");
    assertEquals("LooseListImpl", listWithExport.x);

    // Use a loose type reference to force polymorphic dispatch.
    Collection collectionNoExport = alwaysTrue() ? listNoExport : listWithExport;
    collectionNoExport.add("Loose");
    assertEquals("LooseCollectionBaseFooImpl", listNoExport.x);

    // Calls directly.
    listNoExport.add("Tight");
    assertEquals("TightCollectionBaseFooImpl", listNoExport.x);

    // Calls through a bridge method.
    listWithExport.add("Tight");
    assertEquals("TightListImpl", listWithExport.x);
  }

  public void testConcreteJsTypeAccess() {
    ConcreteJsType concreteJsType = new ConcreteJsType();

    assertJsTypeHasFields(concreteJsType, "publicMethod", "publicField");
    assertJsTypeDoesntHaveFields(concreteJsType, "publicStaticMethod", "privateMethod",
        "protectedMethod", "packageMethod", "publicStaticField", "privateField", "protectedField",
        "packageField");
  }

  public void testConcreteJsTypeSubclassAccess() {
    ConcreteJsType concreteJsType = new ConcreteJsType();
    ConcreteJsTypeSubclass concreteJsTypeSubclass = new ConcreteJsTypeSubclass();

    // A subclass of a JsType is not itself a JsType.
    assertJsTypeDoesntHaveFields(concreteJsTypeSubclass, "publicSubclassMethod",
        "publicSubclassField", "publicStaticSubclassMethod", "privateSubclassMethod",
        "protectedSubclassMethod", "packageSubclassMethod", "publicStaticSubclassField",
        "privateSubclassField", "protectedSubclassField", "packageSubclassField");

    // But if it overrides an exported method then the overriding method will be exported.
    assertJsTypeHasFields(concreteJsType, "publicMethod");
    assertJsTypeHasFields(concreteJsTypeSubclass, "publicMethod");
    assertFalse(
        areSameFunction(concreteJsType, "publicMethod", concreteJsTypeSubclass, "publicMethod"));
    assertFalse(callIntFunction(concreteJsType, "publicMethod")
        == callIntFunction(concreteJsTypeSubclass, "publicMethod"));
  }

//  // TODO: uncomment when bridge methods are being generated for revealed overrides.
//  public void testRevealedOverrideJsType() {
//    PlainParentType plainParentType = new PlainParentType();
//    RevealedOverrideSubType revealedOverrideSubType = new RevealedOverrideSubType();
//
//    // PlainParentType is neither @JsExport or @JsType and so exports no functions.
//    assertFalse(hasField(plainParentType, "run"));
//
//    // RevealedOverrideSubType defines no functions itself, it only inherits them, but it still
//    // exports run() because it implements the @JsType interface JsTypeRunnable.
//    assertTrue(hasField(revealedOverrideSubType, "run"));
//  }

  public void testSubClassWithSuperCalls() {
    MyClassExtendsJsPrototype mc = new MyClassExtendsJsPrototype();
    assertEquals(150, mc.sum(1));
  }

  public void testJsProperties() {
    MyClassExtendsJsPrototype mc = new MyClassExtendsJsPrototype();
    // Tests both fluent and non-fluent accessors.
    mc.x(-mc.x()).setY(0);
    assertEquals(58, mc.sum(0));
  }

  public void testJsPropertyIsX() {
    JsPoint point = (JsPoint) JavaScriptObject.createObject();

    assertFalse(point.isX());
    point.setX(10);
    assertTrue(point.isX());
    point.y(999).x(0);
    assertFalse(point.isX());
  }

  public void testJsPropertyHasX() {
    JsPoint point = (JsPoint) JavaScriptObject.createObject();

    assertFalse(point.hasX());
    point.setX(10);
    assertTrue(point.hasX());
    point.y(999).x(0);
    assertTrue(point.hasX());
  }

  public void testJsPropertyGetX() {
    JsPoint point = (JsPoint) JavaScriptObject.createObject();

    assertTrue(isUndefined(point.getX()));
    point.setX(10);
    assertEquals(10, point.getX());
    point.y(999).x(0);
    assertEquals(0, point.getX());
  }

  public void testJsPropertyX() {
    JsPoint point = (JsPoint) JavaScriptObject.createObject();

    assertTrue(isUndefined(point.x()));
    point.setX(10);
    assertEquals(10, point.x());
    point.y(999).x(0);
    assertEquals(0, point.x());
  }

  public void testCasts() {
    MyJsInterface myClass;
    assertNotNull(myClass = (MyJsInterface) createMyJsInterface());

    try {
      assertNotNull(myClass = (MyJsInterface) createNativeButton());
      fail();
    } catch (ClassCastException cce) {
      // Expected.
    }

    ElementLikeJsInterface button;
    // JsTypes without prototypes can cross-cast like JSOs
    assertNotNull(button = (ElementLikeJsInterface) createMyJsInterface());

    /*
     * If the optimizations are turned on, it is possible for the compiler to dead-strip the
     * variables since they are not used. Therefore the casts could potentially be stripped.
     */
    assertNotNull(myClass);
    assertNotNull(button);
  }

  public void testInstanceOf_jsoWithSyntheticProto() {
    Object object = createMyJsInterface();

    assertTrue(object instanceof Object);
    assertFalse(object instanceof HTMLAnotherElement);
    assertFalse(object instanceof HTMLButtonElement);
    assertFalse(object instanceof HTMLElement);
    assertFalse(object instanceof Iterator);
    assertTrue(object instanceof MyJsInterface);
    assertTrue(object instanceof ElementLikeJsInterface);
  }

  public void testInstanceOf_jsoSansProto() {
    Object object = JavaScriptObject.createObject();

    assertTrue(object instanceof Object);
    assertFalse(object instanceof HTMLAnotherElement);
    assertFalse(object instanceof HTMLButtonElement);
    assertFalse(object instanceof HTMLElement);
    assertFalse(object instanceof Iterator);
    assertFalse(object instanceof MyJsInterface);
    assertTrue(object instanceof ElementLikeJsInterface);
  }

  public void testInstanceOf_jsoWithNativeButtonProto() {
    Object object = createNativeButton();

    assertTrue(object instanceof Object);
    assertTrue(object instanceof HTMLAnotherElement);
    assertTrue(object instanceof HTMLButtonElement);
    assertTrue(object instanceof HTMLElement);
    assertFalse(object instanceof Iterator);
    assertFalse(object instanceof MyJsInterface);
    assertTrue(object instanceof ElementLikeJsInterface);
  }

  public void testInstanceOf_javaImplementorOfInterfaceWithProto() {
    // Foils type tightening.
    Object object = alwaysTrue() ? new MyCustomHtmlButtonWithIterator() : new Object();

    assertTrue(object instanceof Object);
    assertTrue(object instanceof HTMLAnotherElement);
    assertTrue(object instanceof HTMLButtonElement);
    assertTrue(object instanceof HTMLElement);
    assertTrue(object instanceof Iterable);
    /*
     * TODO: this works, but only because Object can't be type-tightened to HTMLElement. But it will
     * evaluate statically to false for HTMLElement instanceof HTMLAnotherElement. Depending on what
     * the spec decides, fix JTypeOracle so that canTheoreticallyCast returns the appropriate
     * result, as well as add a test here that can be type-tightened.
     */
    assertFalse(object instanceof MyJsInterface);
    assertTrue(object instanceof ElementLikeJsInterface);
  }

  public void testInstanceOfWithNameSpace() {
    Object obj1 = createMyNamespacedJsInterface();
    Object obj2 = createMyWrongNamespacedJsInterface();

    assertTrue(obj1 instanceof MyNamespacedJsInterface);
    assertFalse(obj1 instanceof MyJsInterface);

    assertFalse(obj2 instanceof MyNamespacedJsInterface);
  }

  public void testEnumeration() {
    assertEquals(2, callPublicMethodFromEnumeration(MyEnumWithJsType.TEST1));
    assertEquals(3, callPublicMethodFromEnumeration(MyEnumWithJsType.TEST2));
  }

  public void testEnumJsTypeAccess() {
    assertJsTypeHasFields(MyEnumWithJsType.TEST2, "publicMethod", "publicField");
    assertJsTypeDoesntHaveFields(MyEnumWithJsType.TEST2, "publicStaticMethod", "privateMethod",
        "protectedMethod", "packageMethod", "publicStaticField", "privateField", "protectedField",
        "packageField");
  }

  public void testEnumSubclassEnumeration() {
    assertEquals(100, callPublicMethodFromEnumerationSubclass(MyEnumWithSubclassGen.A));
    assertEquals(200, callPublicMethodFromEnumerationSubclass(MyEnumWithSubclassGen.B));
    assertEquals(1, callPublicMethodFromEnumerationSubclass(MyEnumWithSubclassGen.C));
  }

  public void testJsFunctionBasic() {
    MyJsFunctionInterface jsFunctionInterface = new MyJsFunctionInterface() {
      @Override
      public int foo(int a) {
        return a + 2;
      }
    };
    assertEquals(12, jsFunctionInterface.foo(10));
    assertEquals(12, callAsFunction(jsFunctionInterface, 10));
    assertEquals(12, callAsCallBackFunction(jsFunctionInterface, 10));
  }

  public void testJsFunctionSubInterface() {
    MyJsFunctionSubInterface jsFunctionSubInterface = new MyJsFunctionSubInterface() {
        @Override
      public int foo(int a) {
        return a + 3;
      }
    };
    assertEquals(13, jsFunctionSubInterface.foo(10));
    assertEquals(13, callAsFunction(jsFunctionSubInterface, 10));
    assertEquals(13, callAsCallBackFunction(jsFunctionSubInterface, 10));
  }

  public void testJsFunctionSubImpl() {
    MyJsFunctionInterfaceSubImpl jsFunctionInterfaceSubImpl = new MyJsFunctionInterfaceSubImpl();
    assertEquals(21, jsFunctionInterfaceSubImpl.foo(10));
    assertEquals(21, callAsFunction(jsFunctionInterfaceSubImpl, 10));
    assertEquals(21, callAsCallBackFunction(jsFunctionInterfaceSubImpl, 10));
  }

  public void testJsFunctionMultipleInheritance() {
    MyJsFunctionMultipleInheritance jsFunctionMultipleInheritance =
        new MyJsFunctionMultipleInheritance();
    assertEquals(21, jsFunctionMultipleInheritance.foo(10));
    assertEquals(21, callAsFunction(jsFunctionMultipleInheritance, 10));
    assertEquals(21, callAsCallBackFunction(jsFunctionMultipleInheritance, 10));
  }

  public void testJsFunctionObjInJavaFuncInJS() {
    MyJsInterface mc = (MyJsInterface) createMyJsInterface();
    assertEquals(500, mc.callBack(new MyJsFunctionInterface() {
      @Override
      public int foo(int a) {
        return a;
      }
    }));
  }

  public void testJsFunctionInteraction() {
    MyJsFunctionInterfaceImpl jsFunctionInterfaceImpl = new MyJsFunctionInterfaceImpl();
    // public JsType method works fine at Java side.
    assertEquals(5, jsFunctionInterfaceImpl.bar());
    // public JsType method works fine at JS side.
    assertEquals(5, callIntFunction(jsFunctionInterfaceImpl, "bar"));

    // SAM works fine at Java side.
    assertEquals(11, jsFunctionInterfaceImpl.foo(10));
    // SAM can be called as a function at JS side.
    assertEquals(11, callAsFunction(jsFunctionInterfaceImpl, 10));
    assertEquals(11, callAsCallBackFunction(jsFunctionInterfaceImpl, 10));

    // public JsType fields works fine both at Java and JS side.
    assertEquals(10, jsFunctionInterfaceImpl.publicField);
    assertEquals(10, getField(jsFunctionInterfaceImpl, "publicField"));
    setField(jsFunctionInterfaceImpl, "publicField", 100);
    assertEquals(100, jsFunctionInterfaceImpl.publicField);
    assertEquals(100, getField(jsFunctionInterfaceImpl, "publicField"));
  }

  private static native boolean alwaysTrue() /*-{
    return !!$wnd;
  }-*/;

  private static native boolean areSameFunction(
      Object thisObject, String thisFunctionName, Object thatObject, String thatFunctionName) /*-{
    return thisObject[thisFunctionName] === thatObject[thatFunctionName];
  }-*/;

  private static native int callIntFunction(Object object, String functionName) /*-{
    return object[functionName]();
  }-*/;

  private static native Object createNativeButton() /*-{
    return $doc.createElement("button");
  }-*/;

  private static native Object createMyJsInterface() /*-{
    return new $wnd.MyJsInterface();
  }-*/;

  private static native Object createMyNamespacedJsInterface() /*-{
    $wnd.testfoo = {};
    $wnd.testfoo.bar = {};
    $wnd.testfoo.bar.MyJsInterface = function(){};
    return new $wnd.testfoo.bar.MyJsInterface();
  }-*/;

  private static native Object createMyWrongNamespacedJsInterface() /*-{
    $wnd["testfoo.bar.MyJsInterface"] = function(){};
    return new $wnd['testfoo.bar.MyJsInterface']();
  }-*/;

  private static native boolean isUndefined(int value) /*-{
    return value === undefined;
  }-*/;

  private static native boolean hasField(Object object, String fieldName) /*-{
    return object[fieldName] != undefined;
  }-*/;

  private static native void setField(Object object, String fieldName, int value) /*-{
    object[fieldName] = value;
  }-*/;

  private static native int getField(Object object, String fieldName) /*-{
    return object[fieldName];
  }-*/;

  private static native int callPublicMethodFromEnumeration(MyEnumWithJsType enumeration) /*-{
    return enumeration.idxAddOne();
  }-*/;

  private static native int callPublicMethodFromEnumerationSubclass(
      MyEnumWithSubclassGen enumeration) /*-{
    return enumeration.foo();
  }-*/;

  private static native int callAsFunction(Object obj, int arg) /*-{
    return obj(arg);
  }-*/;

  private static native int callAsCallBackFunction(Object obj, int arg) /*-{
    var onCall = function(f, arg) { return f(arg); };
    return onCall(obj, arg);
  }-*/;

  private static void assertJsTypeHasFields(Object obj, String... fields) {
    for (String field : fields) {
      assertTrue("Field '" + field + "' should be exported", hasField(obj, field));
    }
  }

  private static void assertJsTypeDoesntHaveFields(Object obj, String... fields) {
    for (String field : fields) {
      assertFalse("Field '" + field + "' should not be exported", hasField(obj, field));
    }
  }
}
