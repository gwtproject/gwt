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
}
