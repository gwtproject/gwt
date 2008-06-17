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
package com.google.gwt.user.client.ui;

import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Tests for {@link Anchor}.
 */
public class AnchorTest extends GWTTestCase {

  private static final String TEST_URL0 = "http://www.google.com/";
  private static final String TEST_URL1 = "http://code.google.com/";

  @Override
  public String getModuleName() {
    return "com.google.gwt.user.UserTest";
  }

  public void testProperties() {
    Anchor anchor = new Anchor("foo", TEST_URL0);
    assertEquals("foo", anchor.getText());
    assertEquals("foo", anchor.getHTML());
    assertEquals(TEST_URL0, anchor.getHref());

    anchor.setText("bar");
    assertEquals("bar", anchor.getText());

    anchor.setHTML("baz");
    assertEquals("baz", anchor.getHTML());

    anchor.setHref(TEST_URL1);
    assertEquals(TEST_URL1, anchor.getHref());

    anchor.setDirection(HasDirection.Direction.RTL);
    assertEquals(HasDirection.Direction.RTL, anchor.getDirection());

    anchor.setWordWrap(true);
    assertEquals(true, anchor.getWordWrap());

    anchor.setTabIndex(42);
    assertEquals(42, anchor.getTabIndex());
  }
}
