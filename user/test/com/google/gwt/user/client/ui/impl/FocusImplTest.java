package com.google.gwt.user.client.ui.impl;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Test FocusImpl.
 */
public class FocusImplTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.user.User";
  }

  // https://code.google.com/p/google-web-toolkit/issues/detail?id=897
  public void testSetFocus_NotThrowingException() {
    FocusPanel focusPanel = new FocusPanel();
    RootPanel.get().add(focusPanel);
    focusPanel.getElement().getStyle().setDisplay(Display.NONE);
    focusPanel.setFocus(true);
  }
}
