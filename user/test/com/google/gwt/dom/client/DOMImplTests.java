/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.dom.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Test the {@link DOMImpl} class.
 */
public class DOMImplTests extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.dom.DOMTest";
  }

  @DoNotRunWith(Platform.HtmlUnitLayout)
  public void testScrollLeft() {
    Document doc = Document.get();

    doc.getBody().setInnerHTML("<div style='width: 50000px; height: 50000px'></div>");

    DOMImpl impl = GWT.create(DOMImpl.class);

    impl.setScrollLeft(doc, 15);

    int scrollLeft = impl.getScrollLeft(doc);

    assertEquals(15, scrollLeft);
  }

  @DoNotRunWith(Platform.HtmlUnitLayout)
  public void testScrollTop() {
    Document doc = Document.get();

    doc.getBody().setInnerHTML("<div style='width: 50000px; height: 50000px'></div>");

    DOMImpl impl = GWT.create(DOMImpl.class);

    impl.setScrollTop(doc, 15);

    int scrollTop = impl.getScrollTop(doc);

    assertEquals(15, scrollTop);
  }
}
