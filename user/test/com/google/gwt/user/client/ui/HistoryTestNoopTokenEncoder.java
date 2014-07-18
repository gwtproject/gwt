/*
 * Copyright 2014 Google Inc.
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

import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;

/**
 * Tests for the history system without encoding of history tokens.
 */
public class HistoryTestNoopTokenEncoder extends HistoryTest {

  private static native boolean isFirefox() /*-{
    var ua = navigator.userAgent.toLowerCase();
    var docMode = $doc.documentMode;
    return (ua.indexOf('gecko') != -1 && typeof(docMode) == 'undefined');
  }-*/;

  @Override
  public String getModuleName() {
    return "com.google.gwt.user.HistoryTestNoopTokenEncoder";
  }

  @Override
  protected void assertLocationHash(String expectedToken, String originalToken) {
    if (isFirefox()) {
      // Firefox does breaks without double encoding and there is no sane way
      // of asserting anything
    } else {
      assertEquals(originalToken, getCurrentLocationHash());
    }
  }

  @Override
  @DoNotRunWith(Platform.HtmlUnitUnknown)
  public void testHistory() {
    if (isFirefox()) {
      // History in FF with tokens that need encoding is broken
      // So we use tokens in FF that do not need encoding
      runHistoryTest("token1", "token2");
    } else {
      super.testHistory();
    }
  }

  @Override
  @DoNotRunWith(Platform.HtmlUnitUnknown)
  public void testReplaceItem() {
    if (isFirefox()) {
      // History in FF with tokens that need encoding is broken
      // So we use tokens in FF that do not need encoding
      runReplaceItem("token1", "token2", "token3");
    } else {
      super.testReplaceItem();
    }
  }

  @Override
  @DoNotRunWith(Platform.HtmlUnitBug)
  public void testReplaceItemNoEvent() {
    if (isFirefox()) {
      // History in FF with tokens that need encoding is broken
      // So we use tokens in FF that do not need encoding
      runReplaceItemNoEvent("token1", "token2", "token2");
    } else {
      super.testReplaceItemNoEvent();
    }
  }

  @Override
  @DoNotRunWith(Platform.HtmlUnitBug)
  public void testEmptyHistoryTokens() {
    super.testEmptyHistoryTokens();
  }

  @Override
  @DoNotRunWith(Platform.HtmlUnitBug)
  public void testTokenEscaping() {
    super.testTokenEscaping();
  }
}
