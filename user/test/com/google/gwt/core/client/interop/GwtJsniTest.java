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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.js.JsProperty;
import com.google.gwt.core.client.js.JsType;
import com.google.gwt.dom.client.Document;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests JsType and JsExport.
 */
@DoNotRunWith({Platform.Devel, Platform.HtmlUnitBug})
public class GwtJsniTest extends GWTTestCase {


  @JsType
  interface JsObject {
    @JsProperty
    JsFunction constructor();
    boolean hasOwnProperty(String propName);
    boolean isPrototypeOf(JsObject obj);
    boolean propertyIsEnumerable(String propName);
    int valueOf();
    @JsProperty
    JsObject __proto__();
  }


  @JsType
  interface JsFunction extends JsObject {
    @JsProperty
    String name();
    JsObject apply(JsObject thisRef, JsArray args);
    JsFunction bind(JsObject thisRef);
    JsObject call(JsObject thisRef, Object... args);
  }

  @JsType
  interface JsNumber {
    double valueOf();
  }

  @JsType
  interface ArrayLike<T> {
    T at(int x);
    double numberAt(int x);
    @JsProperty
    int length();
  }

  @JsType
  interface JsArray<T> extends ArrayLike<T> {
    JsArray concat(JsArray array);
    String join(String sep);
    int lastIndexOf(JsObject obj);
    JsObject pop();
    void push(JsObject obj);
    JsObject shift();
    void unshift(JsObject obj);
    @JsProperty
    int length();
  }

  @JsType
  interface Node {
  }

  @JsType
  interface NodeList<T extends Node> extends ArrayLike<T> {
  }

  @JsType
  interface Element extends Node {
    NodeList<Element> getElementsByTagName(String tagName);
    void appendChild(Node n);
    @JsProperty
    Element innerHTML(String html);
  }

  @JsType
  interface HTMLDocument extends Element {
    <T extends Element> T createElement(String tagName);
  }

  @Override
  protected void gwtSetUp() throws Exception {

  }

  public String getModuleName() {
    return "com.google.gwt.core.Core";
  }


  public void testString() {
    assertEquals("Hello2", GWT.jsni("'Hello2'"));
  }

  public void testStringWithArgs() {
    assertEquals("Hello World", GWT.jsni("'Hello' + ' ' + $0", "World"));
  }

  public void testPrimitiveInt() {
    assertTrue(43 == GWT.jsniInt("43"));
  }

  public void testPrimitiveInWithIntArg() {
    // note, because of the emulation, the return type of JS number is boxed as a Double
    // the need for this willd is disappear when the magic method version is implemented
    assertTrue(42 == GWT.jsniInt("41 + $0", 1));
  }

  public void testDocument() {
    assertEquals(Document.get(), GWT.jsni("$doc"));
  }

  public void testStatements() {
    // note, because of the emulation, the return type of JS number is boxed as a Double
    // the need for this willd is disappear when the magic method version is implemented
    assertEquals(42,  GWT.jsniInt("var x = 41; return x + 1;"));
  }

  @JsType interface Function {
    int call(Object self, int arg1, int arg2);
  }

  public void testBlock() {
    Function f = GWT.jsni("return function(a,b) { return a + b; };");
    assertEquals(42, f.call(null, 41, 1));
  }

  public void testInterop() {
    JsArray array = GWT.jsni("Object.keys({foo: 1, bar: 2})");
    assertEquals(2, array.length());

//    JsFunction func = GWT.jsni("function() { return 42; }");
//    assertEquals(42, ((JsNumber) func.call(null)).valueOf());
  }

  public void testDOM() {
    HTMLDocument doc = GWT.jsni("$doc");
    assertEquals(3, doc.createElement("div").innerHTML("<ul><li><li><li></ul>").getElementsByTagName("li").length());
  }
}
