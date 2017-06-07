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
package com.google.gwt.user.client.ui.impl;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * IE10/IE11 implementation.
 * Fix insertHtml and createLink method.
 */
public class RichTextAreaImplIE10_11 extends RichTextAreaImplIE8toIE10 {

  /**
   * It is necessary to keep the last position to insert the current cursor position
   */
  protected JavaScriptObject lastRange;

  @Override
  protected void onElementInitialized() {
    super.onElementInitialized();
    initSelectionHook();
  }

  /**
   * Adding hook onselectionchange.
   */
  private native void initSelectionHook() /*-{
      var _this = this;
      var elem = _this.@com.google.gwt.user.client.ui.impl.RichTextAreaImpl::elem;
      elem.contentWindow.document.onselectionchange = $entry(function () {
          var range = elem.contentWindow.document.getSelection().getRangeAt(0);
          _this.@com.google.gwt.user.client.ui.impl.RichTextAreaImplIE10_11::lastRange = range;
      })
  }-*/;

  @Override
  public void createLink(String url) {
    insertHTML("<a href=" + url + ">" + url + "</a>");
  }

  @Override
  public native void insertHTML(String html) /*-{
      try {
          var elem = this.@com.google.gwt.user.client.ui.impl.RichTextAreaImpl::elem;
          var doc = elem.contentWindow.document;
          var selection = doc.getSelection();
          var lastRange = this.@com.google.gwt.user.client.ui.impl.RichTextAreaImplIE10_11::lastRange;
          elem.contentWindow.focus();
          if (lastRange) {
              selection.addRange(lastRange);
          }
          var range = selection.getRangeAt(0);
          range.deleteContents();
          range.insertNode(range.createContextualFragment(html));
          selection.removeAllRanges();
          selection.addRange(range);
      }
      catch (e) {
          console.log(e);
          return;
      }
  }-*/;
}
