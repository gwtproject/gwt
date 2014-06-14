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

import static com.google.gwt.core.client.js.Js.array;
import static com.google.gwt.core.client.js.Js.js;
import static com.google.gwt.core.client.js.Js.jsInt;
import static com.google.gwt.core.client.js.Js.map;

import com.google.gwt.core.client.js.JsArray;
import com.google.gwt.core.client.js.JsArrayLike;
import com.google.gwt.core.client.js.JsMapLike;
import com.google.gwt.core.client.js.JsProperty;
import com.google.gwt.core.client.js.JsType;
import com.google.gwt.dom.client.Document;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;

import java.util.ArrayList;

/**
 * Tests JsType and JsExport.
 */
@DoNotRunWith({Platform.Devel, Platform.HtmlUnitBug})
public class JsJsniTest extends GWTTestCase {

  @JsType
  interface Node {
  }

  @JsType
  interface NodeList<T extends Node> extends JsArrayLike<T> {
  }

  @JsType
  interface Element extends Node {
    NodeList<Element> getElementsByTagName(String tagName);
    void appendChild(Node n);
    @JsProperty
    Element innerHTML(String html);
    @JsProperty
    String innerText();
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
    assertEquals("Hello2", js("'Hello2'"));
  }

  public void testStringWithArgs() {
    assertEquals("Hello World", js("'Hello' + ' ' + $0", "World"));
  }

  public void testPrimitiveInt() {
    assertTrue(43 == jsInt("43"));
  }

  public void testPrimitiveInWithIntArg() {
    assertTrue(42 == jsInt("41 + $0", 1));
  }

  public void testDocument() {
    assertEquals(Document.get(), js("$doc"));
  }

  public void testStatements() {
    assertEquals(42,  jsInt("var x = 41; return x + 1;"));
  }

  public void testBlock() {
    int result = jsInt("if ($0) { return $1; } else { return $2; }", true, 42, 99);
    assertEquals(42, result);
  }

  public void testInterop() {
    JsArray array = js("Object.keys({foo: 1, bar: 2})");
    assertEquals(2, array.length());

    JsMapLike map = js("({foo: 1, bar: 2})");
    assertEquals(2, map.intAt("bar"));
  }

  public void testDOM() {
    HTMLDocument doc = js("$doc");
    NodeList<Element> query =
        doc.createElement("div").innerHTML("<ul><li><li>hello</li><li></ul>")
            .getElementsByTagName("li");
    assertEquals(3, query.length());
    assertEquals("hello", query.at(1).innerText());
    boolean found = false;
    for (Element x : query) {
      if (x.innerText().equals("hello")) {
        found = true;
        break;
      }
    }

    assertTrue(found);
  }

  public void testFastArrayListIteration() {
    ArrayList<String> al = new ArrayList<String>();
    al.add("Apples");
    al.add("Oranges");
    al.add("Grapes");
    String result = "";
    for (String x : al) {
      result += x;
    }
    assertEquals("ApplesOrangesGrapes", result);
  }

  public void testJsArrayLiteral() {
    JsArray array = array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    int sum = 0;
    for (int i = 0; i < array.length(); i++) {
      sum += array.intAt(i);
    }
    assertEquals(55, sum);
  }

  public void testJsMapLiteral() {
    JsMapLike<String> map = map("Alice", 42, "Bert", 21, "Candice", 37);
    assertEquals(42, map.intAt("Alice"));
    assertEquals(21, map.intAt("Bert"));
    assertEquals(37, map.intAt("Candice"));
  }
}
