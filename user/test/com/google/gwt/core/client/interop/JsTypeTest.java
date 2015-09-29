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
import com.google.gwt.core.client.js.JsFunction;
import com.google.gwt.core.client.js.JsProperty;
import com.google.gwt.core.client.js.JsType;
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
        + "MyJsInterface.staticX = 33;"
        + "MyJsInterface.answerToLife = function() { return 42;};"
        + "MyJsInterface.prototype.sum = function sum(bias) { return this.x + bias; };")
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

    assertEquals(10, callIntFunction(concreteJsType, "publicMethod"));
  }

  public void testAbstractJsTypeAccess() {
    AbstractJsType jsType = new AbstractJsType() {
      @Override
      public int publicMethod() {
        return 32;
      }
    };

    assertJsTypeHasFields(jsType, "publicMethod");
    assertEquals(32, callIntFunction(jsType, "publicMethod"));
    assertEquals(32, jsType.publicMethod());
  }

  public void testConcreteJsTypeSubclassAccess() {
    ConcreteJsTypeSubclass concreteJsTypeSubclass = new ConcreteJsTypeSubclass();

    // A subclass of a JsType is not itself a JsType.
    assertJsTypeDoesntHaveFields(concreteJsTypeSubclass, "publicSubclassMethod",
        "publicSubclassField", "publicStaticSubclassMethod", "privateSubclassMethod",
        "protectedSubclassMethod", "packageSubclassMethod", "publicStaticSubclassField",
        "privateSubclassField", "protectedSubclassField", "packageSubclassField");

    // But if it overrides an exported method then the overriding method will be exported.
    assertJsTypeHasFields(concreteJsTypeSubclass, "publicMethod");

    assertEquals(20, callIntFunction(concreteJsTypeSubclass, "publicMethod"));
    assertEquals(10, concreteJsTypeSubclass.publicSubclassMethod());
  }

  public void testConcreteJsTypeNoTypeTightenField() {
    // If we type-tighten, java side will see no calls and think that field could only AImpl1.
    ConcreteJsType concreteJsType = new ConcreteJsType();
    setTheField(concreteJsType, new ConcreteJsType.AImpl2());
    assertEquals(101, concreteJsType.notTypeTightenedField.x());
  }

  private native void setTheField(ConcreteJsType obj, ConcreteJsType.A value)/*-{
    obj.notTypeTightenedField = value;
  }-*/;

  public void testRevealedOverrideJsType() {
    PlainParentType plainParentType = new PlainParentType();
    RevealedOverrideSubType revealedOverrideSubType = new RevealedOverrideSubType();

    // PlainParentType is neither @JsExport or @JsType and so exports no functions.
    assertFalse(hasField(plainParentType, "run"));

    // RevealedOverrideSubType defines no functions itself, it only inherits them, but it still
    // exports run() because it implements the @JsType interface JsTypeRunnable.
    assertTrue(hasField(revealedOverrideSubType, "run"));

    ConcreteJsTypeJsSubclass subclass = new ConcreteJsTypeJsSubclass();
    assertEquals(100, subclass.publicMethodAlsoExposedAsNonJsMethod());
    SubclassInterface subclassInterface = alwaysTrue() ? subclass : new SubclassInterface() {
      @Override
      public int publicMethodAlsoExposedAsNonJsMethod() {
        return 0;
      }
    };
    assertEquals(100, subclassInterface.publicMethodAlsoExposedAsNonJsMethod());
  }

  public void testConcreteNativeType() {
    assertEquals(33, MyJsClassWithPrototype.staticX);
    MyJsClassWithPrototype.staticX = 34;
    assertEquals(34, MyJsClassWithPrototype.staticX);
    assertEquals(42, MyJsClassWithPrototype.answerToLife());

    MyJsClassWithPrototype obj = new MyJsClassWithPrototype();
    assertTrue(isUndefined(obj.x));
    obj.x = 72;
    assertEquals(72, obj.x);
    assertEquals(74, obj.sum(2));

    assertTrue(isUndefined(obj.getY()));
    obj.setY(91);
    assertEquals(91, obj.getY());
  }

  public void testConcreteNativeType_sublasss() {
    MyClassExtendsJsPrototype mc = new MyClassExtendsJsPrototype();
    assertEquals(143, mc.sum(1));

    mc.x = -mc.x;
    assertEquals(58, mc.sum(0));

    assertEquals(52, mc.getY());
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

  public void testPropertyBridges_accidental() {
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

  static public class MyJsInterfaceWithPrototypeImplNeedsBridgeAndSubclass
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

  static class  MyJsInterfaceWithPrototypeImplNeedsBridgeSubclass
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

  public void testPropertyBridges_subclass() {
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

  public void testCasts() {
    MyJsInterfaceWithPrototype myClass;
    assertNotNull(myClass = (MyJsInterfaceWithPrototype) createMyJsInterface());

    try {
      assertNotNull(myClass = (MyJsInterfaceWithPrototype) createNativeButton());
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

  public void testInstanceOf_jsoWithProto() {
    Object object = createMyJsInterface();

    assertTrue(object instanceof Object);
    assertFalse(object instanceof HTMLAnotherElement);
    assertFalse(object instanceof HTMLButtonElement);
    assertFalse(object instanceof HTMLElement);
    assertFalse(object instanceof Iterator);
    assertTrue(object instanceof MyJsInterfaceWithPrototype);
    assertFalse(object instanceof MyJsInterfaceWithPrototypeImpl);
    assertTrue(object instanceof ElementLikeJsInterface);
    assertFalse(object instanceof ElementLikeJsInterfaceImpl);
    assertTrue(object instanceof MyJsInterfaceWithOnlyInstanceofReference);
    assertTrue(object instanceof MyJsInterfaceWithPrototypeAndOnlyInstanceofReference);
    assertFalse(object instanceof MyJsClassWithPrototypeAndOnlyInstanceofReference);
    assertFalse(object instanceof ConcreteJsType);
  }

  public void testInstanceOf_jsoWithoutProto() {
    Object object = JavaScriptObject.createObject();

    assertTrue(object instanceof Object);
    assertFalse(object instanceof HTMLAnotherElement);
    assertFalse(object instanceof HTMLButtonElement);
    assertFalse(object instanceof HTMLElement);
    assertFalse(object instanceof Iterator);
    assertFalse(object instanceof MyJsInterfaceWithPrototype);
    assertFalse(object instanceof MyJsInterfaceWithPrototypeImpl);
    assertTrue(object instanceof ElementLikeJsInterface);
    assertFalse(object instanceof ElementLikeJsInterfaceImpl);
    assertTrue(object instanceof MyJsInterfaceWithOnlyInstanceofReference);
    assertFalse(object instanceof MyJsInterfaceWithPrototypeAndOnlyInstanceofReference);
    assertFalse(object instanceof MyJsClassWithPrototypeAndOnlyInstanceofReference);
    assertFalse(object instanceof ConcreteJsType);
  }

  public void testInstanceOf_jsoWithNativeButtonProto() {
    Object object = createNativeButton();

    assertTrue(object instanceof Object);
    assertTrue(object instanceof HTMLAnotherElement);
    assertTrue(object instanceof HTMLButtonElement);
    assertTrue(object instanceof HTMLElement);
    assertFalse(object instanceof Iterator);
    assertFalse(object instanceof MyJsInterfaceWithPrototype);
    assertFalse(object instanceof MyJsInterfaceWithPrototypeImpl);
    assertTrue(object instanceof ElementLikeJsInterface);
    assertFalse(object instanceof ElementLikeJsInterfaceImpl);
    assertTrue(object instanceof MyJsInterfaceWithOnlyInstanceofReference);
    assertFalse(object instanceof MyJsInterfaceWithPrototypeAndOnlyInstanceofReference);
    assertTrue(object instanceof MyJsClassWithPrototypeAndOnlyInstanceofReference);
    assertFalse(object instanceof ConcreteJsType);
  }

  public void testInstanceOf_implementsJsType() {
    // Foils type tightening.
    Object object = alwaysTrue() ? new ElementLikeJsInterfaceImpl() : new Object();

    assertTrue(object instanceof Object);
    assertFalse(object instanceof HTMLAnotherElement);
    assertFalse(object instanceof HTMLButtonElement);
    assertFalse(object instanceof HTMLElement);
    assertFalse(object instanceof Iterator);
    assertFalse(object instanceof MyJsInterfaceWithPrototype);
    assertFalse(object instanceof MyJsInterfaceWithPrototypeImpl);
    assertTrue(object instanceof ElementLikeJsInterface);
    assertTrue(object instanceof ElementLikeJsInterfaceImpl);
    assertFalse(object instanceof MyJsInterfaceWithOnlyInstanceofReference);
    assertFalse(object instanceof MyJsInterfaceWithPrototypeAndOnlyInstanceofReference);
    assertFalse(object instanceof MyJsClassWithPrototypeAndOnlyInstanceofReference);
    assertFalse(object instanceof ConcreteJsType);
  }

  public void testInstanceOf_implementsJsTypeWithPrototype() {
    // Foils type tightening.
    Object object = alwaysTrue() ? new MyJsInterfaceWithPrototypeImpl() : new Object();

    assertTrue(object instanceof Object);
    assertFalse(object instanceof HTMLAnotherElement);
    assertFalse(object instanceof HTMLButtonElement);
    assertFalse(object instanceof HTMLElement);
    assertFalse(object instanceof Iterator);
    assertTrue(object instanceof MyJsInterfaceWithPrototype);
    assertTrue(object instanceof MyJsInterfaceWithPrototypeImpl);
    assertFalse(object instanceof ElementLikeJsInterface);
    assertFalse(object instanceof ElementLikeJsInterfaceImpl);
    assertFalse(object instanceof MyJsInterfaceWithOnlyInstanceofReference);
    assertFalse(object instanceof MyJsInterfaceWithPrototypeAndOnlyInstanceofReference);
    assertFalse(object instanceof MyJsClassWithPrototypeAndOnlyInstanceofReference);
    assertFalse(object instanceof ConcreteJsType);
  }

  public void testInstanceOf_concreteJsType() {
    // Foils type tightening.
    Object object = alwaysTrue() ? new ConcreteJsType() : new Object();

    assertTrue(object instanceof Object);
    assertFalse(object instanceof HTMLAnotherElement);
    assertFalse(object instanceof HTMLButtonElement);
    assertFalse(object instanceof HTMLElement);
    assertFalse(object instanceof Iterator);
    assertFalse(object instanceof MyJsInterfaceWithPrototype);
    assertFalse(object instanceof MyJsInterfaceWithPrototypeImpl);
    assertFalse(object instanceof ElementLikeJsInterface);
    assertFalse(object instanceof ElementLikeJsInterfaceImpl);
    assertFalse(object instanceof MyJsInterfaceWithOnlyInstanceofReference);
    assertFalse(object instanceof MyJsInterfaceWithPrototypeAndOnlyInstanceofReference);
    assertFalse(object instanceof MyJsClassWithPrototypeAndOnlyInstanceofReference);
    assertTrue(object instanceof ConcreteJsType);
  }

  public void testInstanceOf_extendsJsTypeWithProto() {
    // Foils type tightening.
    Object object = alwaysTrue() ? new MyCustomHtmlButtonWithIterator() : new Object();

    assertTrue(object instanceof Object);
    assertTrue(object instanceof HTMLAnotherElement);
    assertTrue(object instanceof HTMLButtonElement);
    assertTrue(object instanceof HTMLElement);
    assertTrue(object instanceof Iterable);
    assertFalse(object instanceof MyJsInterfaceWithPrototype);
    assertFalse(object instanceof MyJsInterfaceWithPrototypeImpl);
    assertFalse(object instanceof ElementLikeJsInterface);
    assertFalse(object instanceof ElementLikeJsInterfaceImpl);
    assertFalse(object instanceof MyJsInterfaceWithOnlyInstanceofReference);
    assertFalse(object instanceof MyJsInterfaceWithPrototypeAndOnlyInstanceofReference);
    assertTrue(object instanceof MyJsClassWithPrototypeAndOnlyInstanceofReference);
    assertFalse(object instanceof ConcreteJsType);
  }

  public void testInstanceOfWithNameSpace() {
    Object obj1 = createMyNamespacedJsInterface();
    Object obj2 = createMyWrongNamespacedJsInterface();

    assertTrue(obj1 instanceof MyNamespacedJsInterface);
    assertFalse(obj1 instanceof MyJsInterfaceWithPrototype);

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

  private static native boolean alwaysTrue() /*-{
    return !!$wnd;
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

  private static native int callPublicMethodFromEnumeration(MyEnumWithJsType enumeration) /*-{
    return enumeration.idxAddOne();
  }-*/;

  private static native int callPublicMethodFromEnumerationSubclass(
      MyEnumWithSubclassGen enumeration) /*-{
    return enumeration.foo();
  }-*/;

  private static native int readX(Object object) /*-{
    return object.x;
  }-*/;

  private static native void writeX(Object object, int value) /*-{
    return object.x = value;
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

  @JsType
  interface SimpleJsTypeFieldInterface {
  }

  static class SimpleJsTypeFieldClass implements SimpleJsTypeFieldInterface {
  }

  @JsType
  static class SimpleJsTypeWithField {
    public SimpleJsTypeFieldInterface someField;
  }

  public void testJsTypeField() {
    new SimpleJsTypeFieldClass();
    SimpleJsTypeWithField holder = new SimpleJsTypeWithField();
    fillJsTypeField(holder);
    SimpleJsTypeFieldInterface someField = holder.someField;
    assertNotNull(someField);
  }

  private native void fillJsTypeField(SimpleJsTypeWithField jstype) /*-{
    jstype.someField = {};
  }-*/;

  @JsType
  interface InterfaceWithSingleJavaConcrete {
    int m();
  }

  static class JavaConcrete implements InterfaceWithSingleJavaConcrete {
    public int m() {
      return 5;
    }
  }

  private native Object nativeObjectImplementingM() /*-{
    return {m: function() { return 3;} }
  }-*/;

  public void testSingleJavaConcreteInterface() {
    // Create a couple of instances and use the objects in some way to avoid complete pruning
    // of JavaConcrete
    assertTrue(new JavaConcrete() != new JavaConcrete());
    assertSame(5, new JavaConcrete().m());
    assertSame(3, ((InterfaceWithSingleJavaConcrete) nativeObjectImplementingM()).m());
  }

  @JsFunction
  interface JsFunctionInterface {
    int m();
  }

  static class JavaConcreteJsFunction implements JsFunctionInterface {
    public int m() {
      return 5;
    }
  }

  private native Object nativeJsFunction() /*-{
    return function() { return 3;};
  }-*/;

  public void testSingleJavaConcreteJsFunction() {
    // Create a couple of instances and use the objects in some way to avoid complete pruning
    // of JavaConcrete
    assertTrue(new JavaConcreteJsFunction() != new JavaConcreteJsFunction());
    assertSame(5, new JavaConcreteJsFunction().m());
    assertSame(3, ((JsFunctionInterface) nativeJsFunction()).m());
  }
}
