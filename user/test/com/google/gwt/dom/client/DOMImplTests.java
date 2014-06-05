package com.google.gwt.dom.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Test the {@link DOMImpl} class
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
