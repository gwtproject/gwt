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

import static jsinterop.annotations.JsPackage.GLOBAL;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.junit.client.GWTTestCase;

import java.util.Iterator;

import javaemul.internal.annotations.DoNotInline;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Tests JsType functionality.
 */
@SuppressWarnings("cast")
public class JsTypeTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.Core";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    ScriptInjector.fromString("function JsTypeTest_MyNativeJsType() {}")
        .setWindow(ScriptInjector.TOP_WINDOW)
        .inject();
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

  @JsType
  interface A {
    boolean m(Object o);
  }

  private static class AImpl implements A {
    @Override
    public boolean m(Object o) {
      return o == null;
    }
  }

  public void testNativeMethodOverrideNoTypeTightenParam() {
    AImpl a = new AImpl();
    assertTrue(a.m(null));
    assertFalse((Boolean) callFunction(a, "m", new Object()));
  }

  private native void setTheField(ConcreteJsType obj, ConcreteJsType.A value)/*-{
    obj.notTypeTightenedField = value;
  }-*/;

  public void testRevealedOverrideJsType() {
    PlainParentType plainParentType = new PlainParentType();
    RevealedOverrideSubType revealedOverrideSubType = new RevealedOverrideSubType();

    // PlainParentType is neither @JsType or @JsType and so exports no functions.
    assertFalse(hasField(plainParentType, "run"));

    // RevealedOverrideSubType defines no functions itself, it only inherits them, but it still
    // exports run() because it implements the @JsType interface JsTypeRunnable.
    assertTrue(hasField(revealedOverrideSubType, "run"));

    ConcreteJsTypeJsSubclass subclass = new ConcreteJsTypeJsSubclass();
    assertEquals(100, subclass.publicMethodAlsoExposedAsNonJsMethod());
    SubclassInterface subclassInterface = subclass;
    assertEquals(100, subclassInterface.publicMethodAlsoExposedAsNonJsMethod());
  }

  @JsType(isNative = true)
  interface MyNativeJsTypeInterface {
  }

  class MyNativeJsTypeInterfaceImpl implements MyNativeJsTypeInterface {
  }

  public void testCasts() {
    Object myClass;
    assertNotNull(myClass = (ElementLikeNativeInterface) createMyNativeJsType());
    assertNotNull(myClass = (MyNativeJsTypeInterface) createMyNativeJsType());
    assertNotNull(myClass = (HTMLElementConcreteNativeJsType) createNativeButton());

    try {
      assertNotNull(myClass = (HTMLElementConcreteNativeJsType) createMyNativeJsType());
      fail();
    } catch (ClassCastException cce) {
      // Expected.
    }

    // Test cross cast for native types
    Object nativeButton1 = (HTMLElementConcreteNativeJsType) createNativeButton();
    Object nativeButton2 = (HTMLElementAnotherConcreteNativeJsType) nativeButton1;

    /*
     * If the optimizations are turned on, it is possible for the compiler to dead-strip the
     * variables since they are not used. Therefore the casts could potentially be stripped.
     */
    assertNotNull(myClass);
    assertNotNull(nativeButton1);
    assertNotNull(nativeButton2);
  }

  /**
   * A test class marked with JsType but isn't referenced from any Java code except instanceof.
   */
  @JsType(isNative = true)
  public interface MyNativeJsTypeInterfaceAndOnlyInstanceofReference {
  }

  /**
   * A test class marked with JsType but isn't referenced from any Java code except instanceof.
   */
  @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "JsTypeTest_MyNativeJsType")
  public static class AliasToMyNativeJsTypeWithOnlyInstanceofReference {
  }

  public void testInstanceOf_jsoWithProto() {
    Object object = createMyNativeJsType();

    assertTrue(object instanceof Object);
    assertFalse(object instanceof HTMLElementConcreteNativeJsType);
    assertFalse(object instanceof HTMLElementAnotherConcreteNativeJsType);
    assertFalse(object instanceof HTMLButtonElementConcreteNativeJsType);
    assertFalse(object instanceof Iterator);
    assertFalse(object instanceof MyNativeJsTypeInterfaceImpl);
    assertFalse(object instanceof ElementLikeNativeInterfaceImpl);
    assertFalse(object instanceof MyJsInterfaceWithOnlyInstanceofReference);
    assertTrue(object instanceof AliasToMyNativeJsTypeWithOnlyInstanceofReference);
    assertFalse(object instanceof ConcreteJsType);
    assertFalse(object instanceof MyNativeJsTypeInterface[]);
    assertFalse(object instanceof MyNativeJsTypeInterfaceImpl[][]);
  }

  public void testInstanceOf_jsoWithoutProto() {
    Object object = JavaScriptObject.createObject();

    assertTrue(object instanceof Object);
    assertFalse(object instanceof HTMLElementConcreteNativeJsType);
    assertFalse(object instanceof HTMLElementAnotherConcreteNativeJsType);
    assertFalse(object instanceof HTMLButtonElementConcreteNativeJsType);
    assertFalse(object instanceof Iterator);
    assertFalse(object instanceof MyNativeJsTypeInterfaceImpl);
    assertFalse(object instanceof ElementLikeNativeInterfaceImpl);
    assertFalse(object instanceof MyJsInterfaceWithOnlyInstanceofReference);
    assertFalse(object instanceof AliasToMyNativeJsTypeWithOnlyInstanceofReference);
    assertFalse(object instanceof ConcreteJsType);
    assertFalse(object instanceof MyNativeJsTypeInterface[]);
    assertFalse(object instanceof MyNativeJsTypeInterfaceImpl[][]);
  }

  public void testInstanceOf_jsoWithNativeButtonProto() {
    Object object = createNativeButton();

    assertTrue(object instanceof Object);
    assertTrue(object instanceof HTMLElementConcreteNativeJsType);
    assertTrue(object instanceof HTMLElementAnotherConcreteNativeJsType);
    assertTrue(object instanceof HTMLButtonElementConcreteNativeJsType);
    assertFalse(object instanceof Iterator);
    assertFalse(object instanceof MyNativeJsTypeInterfaceImpl);
    assertFalse(object instanceof ElementLikeNativeInterfaceImpl);
    assertFalse(object instanceof MyJsInterfaceWithOnlyInstanceofReference);
    assertFalse(object instanceof AliasToMyNativeJsTypeWithOnlyInstanceofReference);
    assertFalse(object instanceof ConcreteJsType);
    assertFalse(object instanceof MyNativeJsTypeInterface[]);
    assertFalse(object instanceof MyNativeJsTypeInterfaceImpl[][]);
  }

  public void testInstanceOf_implementsJsType() {
    // Foils type tightening.
    Object object = new ElementLikeNativeInterfaceImpl();

    assertTrue(object instanceof Object);
    assertFalse(object instanceof HTMLElementConcreteNativeJsType);
    assertFalse(object instanceof HTMLElementAnotherConcreteNativeJsType);
    assertFalse(object instanceof HTMLButtonElementConcreteNativeJsType);
    assertFalse(object instanceof Iterator);
    assertFalse(object instanceof MyNativeJsTypeInterfaceImpl);
    assertTrue(object instanceof ElementLikeNativeInterfaceImpl);
    assertFalse(object instanceof MyJsInterfaceWithOnlyInstanceofReference);
    assertFalse(object instanceof AliasToMyNativeJsTypeWithOnlyInstanceofReference);
    assertFalse(object instanceof ConcreteJsType);
    assertFalse(object instanceof MyNativeJsTypeInterface[]);
    assertFalse(object instanceof MyNativeJsTypeInterfaceImpl[][]);
  }

  public void testInstanceOf_implementsJsTypeWithPrototype() {
    // Foils type tightening.
    Object object = new MyNativeJsTypeInterfaceImpl();

    assertTrue(object instanceof Object);
    assertFalse(object instanceof HTMLElementConcreteNativeJsType);
    assertFalse(object instanceof HTMLElementAnotherConcreteNativeJsType);
    assertFalse(object instanceof HTMLButtonElementConcreteNativeJsType);
    assertFalse(object instanceof Iterator);
    assertTrue(object instanceof MyNativeJsTypeInterfaceImpl);
    assertFalse(object instanceof ElementLikeNativeInterfaceImpl);
    assertFalse(object instanceof MyJsInterfaceWithOnlyInstanceofReference);
    assertFalse(object instanceof AliasToMyNativeJsTypeWithOnlyInstanceofReference);
    assertFalse(object instanceof ConcreteJsType);
    assertFalse(object instanceof MyNativeJsTypeInterface[]);
    assertFalse(object instanceof MyNativeJsTypeInterfaceImpl[][]);
  }

  public void testInstanceOf_concreteJsType() {
    // Foils type tightening.
    Object object = new ConcreteJsType();

    assertTrue(object instanceof Object);
    assertFalse(object instanceof HTMLElementConcreteNativeJsType);
    assertFalse(object instanceof HTMLElementAnotherConcreteNativeJsType);
    assertFalse(object instanceof HTMLButtonElementConcreteNativeJsType);
    assertFalse(object instanceof Iterator);
    assertFalse(object instanceof MyNativeJsTypeInterfaceImpl);
    assertFalse(object instanceof ElementLikeNativeInterfaceImpl);
    assertFalse(object instanceof MyJsInterfaceWithOnlyInstanceofReference);
    assertFalse(object instanceof AliasToMyNativeJsTypeWithOnlyInstanceofReference);
    assertTrue(object instanceof ConcreteJsType);
    assertFalse(object instanceof MyNativeJsTypeInterface[]);
    assertFalse(object instanceof MyNativeJsTypeInterfaceImpl[][]);
  }

  @JsType(isNative = true, namespace = GLOBAL, name = "JsTypeTest_MyNativeJsType")
  static class MyNativeJsType { }

  static class MyNativeJsTypeSubclass extends MyNativeJsType { }

  static class MyNativeJsTypeSubclassWithIterator extends MyNativeJsType implements Iterable {
    @Override
    public Iterator iterator() {
      return null;
    }
  }

  public void testInstanceOf_extendsJsTypeWithProto() {
    // Foils type tightening.
    Object object = new MyNativeJsTypeSubclassWithIterator();

    assertTrue(object instanceof Object);
    assertTrue(object instanceof MyNativeJsType);
    assertFalse(object instanceof MyNativeJsTypeSubclass);
    assertTrue(object instanceof MyNativeJsTypeSubclassWithIterator);
    assertFalse(object instanceof HTMLElementConcreteNativeJsType);
    assertFalse(object instanceof HTMLElementAnotherConcreteNativeJsType);
    assertFalse(object instanceof HTMLButtonElementConcreteNativeJsType);
    assertTrue(object instanceof Iterable);
    assertFalse(object instanceof MyNativeJsTypeInterfaceImpl);
    assertFalse(object instanceof ElementLikeNativeInterfaceImpl);
    assertFalse(object instanceof MyJsInterfaceWithOnlyInstanceofReference);
    assertFalse(object instanceof AliasToMyNativeJsTypeWithOnlyInstanceofReference);
    assertFalse(object instanceof ConcreteJsType);
    assertFalse(object instanceof MyNativeJsTypeInterface[]);
    assertFalse(object instanceof MyNativeJsTypeInterfaceImpl[][]);
  }

  @JsType(isNative = true, namespace = "testfoo.bar")
  static class MyNamespacedNativeJsType {
  }

  public void testInstanceOf_withNameSpace() {
    Object obj1 = createMyNamespacedJsInterface();
    Object obj2 = createMyWrongNamespacedJsInterface();

    assertTrue(obj1 instanceof MyNamespacedNativeJsType);
    assertFalse(obj1 instanceof MyNativeJsType);

    assertFalse(obj2 instanceof MyNamespacedNativeJsType);
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

  private static native int callIntFunction(Object object, String functionName) /*-{
    return object[functionName]();
  }-*/;

  private static native Object callFunction(Object object, String functionName, Object param) /*-{
    return object[functionName](param);
  }-*/;

  private static native Object createNativeButton() /*-{
    return $doc.createElement("button");
  }-*/;

  private static native Object createMyNativeJsType() /*-{
    return new $wnd.JsTypeTest_MyNativeJsType();
  }-*/;

  private static native Object createMyNamespacedJsInterface() /*-{
    $wnd.testfoo = {};
    $wnd.testfoo.bar = {};
    $wnd.testfoo.bar.MyNamespacedNativeJsType = function(){};
    return new $wnd.testfoo.bar.MyNamespacedNativeJsType();
  }-*/;

  private static native Object createMyWrongNamespacedJsInterface() /*-{
    $wnd["testfoo.bar.MyNamespacedNativeJsType"] = function(){};
    return new $wnd['testfoo.bar.MyNamespacedNativeJsType']();
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

  static class SimpleJsTypeWithField {
    @JsProperty
    public SimpleJsTypeFieldInterface someField;
  }

  public void testJsTypeField() {
    assertTrue(new SimpleJsTypeFieldClass() != new SimpleJsTypeFieldClass());
    SimpleJsTypeWithField holder = new SimpleJsTypeWithField();
    fillJsTypeField(holder);
    SimpleJsTypeFieldInterface someField = holder.someField;
    assertNotNull(someField);
  }

  private native void fillJsTypeField(SimpleJsTypeWithField jstype) /*-{
    jstype.someField = {};
  }-*/;

  @JsType(isNative = true)
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

  private static native void setProperty(Object object, String name, int value) /*-{
    object[name] = value;
  }-*/;

  @JsType(isNative = true, namespace = GLOBAL, name = "Object")
  static class NativeJsTypeWithOverlay {

    @JsOverlay
    public static final int x = 2;

    public static native String[] keys(Object o);

    @JsOverlay @DoNotInline
    public static final boolean hasM(Object obj) {
      return keys(obj)[0].equals("m");
    }

    public native boolean hasOwnProperty(String name);

    @JsOverlay @DoNotInline
    public final boolean hasM() {
      return hasOwnProperty("m");
    }
  }

  private native NativeJsTypeWithOverlay createNativeJsTypeWithOverlay() /*-{
    return { m: function() { return 6; } };
  }-*/;

  public void testNativeJsTypeWithOverlay() {
    NativeJsTypeWithOverlay object = createNativeJsTypeWithOverlay();
    assertTrue(object.hasM());
    assertTrue(NativeJsTypeWithOverlay.hasM(object));
    assertEquals(2, NativeJsTypeWithOverlay.x);
  }
}
