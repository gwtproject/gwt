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

/**
 * Mozilla implementation of StandardBrowser.
 */
class DOMImplMozilla extends DOMImplStandard {

  @Override
  public native void buttonClick(ButtonElement button) /*-{
    var doc = button.ownerDocument;
    if (doc != null) {
      var evt = doc.createEvent('MouseEvents');
      evt.initMouseEvent('click', true, true, null, 0, 0,
        0, 0, 0, false, false, false, false, 0, null);
      button.dispatchEvent(evt);
    }
  }-*/;

  @Override
  public NativeEvent createKeyCodeEvent(Document doc, String type,
      boolean ctrlKey, boolean altKey, boolean shiftKey, boolean metaKey,
      int keyCode) {
    return createKeyEventImpl(doc, type, true, true, ctrlKey, altKey, shiftKey,
        metaKey, keyCode, 0);
  }

  @Override
  @Deprecated
  public NativeEvent createKeyEvent(Document doc, String type,
      boolean canBubble, boolean cancelable, boolean ctrlKey, boolean altKey,
      boolean shiftKey, boolean metaKey, int keyCode, int charCode) {
    return createKeyEventImpl(doc, type, canBubble, cancelable, ctrlKey,
        altKey, shiftKey, metaKey, keyCode, charCode);
  }

  @Override
  public NativeEvent createKeyPressEvent(Document doc, boolean ctrlKey,
      boolean altKey, boolean shiftKey, boolean metaKey, int charCode) {
    return createKeyEventImpl(doc, "keypress", true, true, ctrlKey, altKey,
        shiftKey, metaKey, 0, charCode);
  }

  @Override
  public int getAbsoluteLeft(Element elem) {
    return getAbsoluteLeftImpl(elem.getOwnerDocument().getViewportElement(),
        elem);
  }

  @Override
  public int getAbsoluteTop(Element elem) {
    return getAbsoluteTopImpl(elem.getOwnerDocument().getViewportElement(),
        elem);
  }

  @Override
  public native int getBodyOffsetLeft(Document doc) /*-{
    var style = $wnd.getComputedStyle(doc.documentElement, null);
    return parseInt(style.marginLeft, 10) + parseInt(style.borderLeftWidth, 10);
  }-*/;

  @Override
  public native int getBodyOffsetTop(Document doc) /*-{
    var style = $wnd.getComputedStyle(doc.documentElement, null);
    return parseInt(style.marginTop, 10) + parseInt(style.borderTopWidth, 10);
  }-*/;

  @Override
  public native int getNodeType(Node node) /*-{
    try {
      return node.nodeType;
    } catch (e) {
      // Give up on 'Permission denied to get property HTMLDivElement.nodeType'
      // '0' is not a valid node type, which is appropriate in this case, since
      // the node in question is completely inaccessible.
      //
      // See https://bugzilla.mozilla.org/show_bug.cgi?id=208427
      // and http://code.google.com/p/google-web-toolkit/issues/detail?id=1909
      // Fixed FF 148, reevaluate once ESR > 148
      return 0;
    }
  }-*/;

  @Override
  public int getScrollLeft(Element elem) {
    return super.getScrollLeft(elem);
  }

  @Override
  public void setScrollLeft(Element elem, int left) {
    super.setScrollLeft(elem, left);
  }

  private native NativeEvent createKeyEventImpl(Document doc, String type,
      boolean canBubble, boolean cancelable, boolean ctrlKey, boolean altKey,
      boolean shiftKey, boolean metaKey, int keyCode, int charCode) /*-{
    if (!!window.KeyboardEvent) {
      return new KeyboardEvent(type, {
        ctrlKey: ctrlKey,
        altKey: altKey,
        shiftKey: shiftKey,
        metaKey: metaKey,
        keyCode: keyCode,
        charCode: charCode,
        bubbles: canBubble,
        cancelable: cancelable
      });
    }
    var evt = doc.createEvent('KeyboardEvent');
    if (evt.initKeyEvent) {
      // Gecko
      evt.initKeyEvent(type, canBubble, cancelable, null, ctrlKey, altKey,
        shiftKey, metaKey, keyCode, charCode);
    } else {
      // This happens to be IE11+ as of today
      if ($wnd.console) {
        $wnd.console.error("Synthetic keyboard events are not supported in this browser");
      }
    }
    return evt;
  }-*/;

  private native int getAbsoluteLeftImpl(Element viewport, Element elem) /*-{
    return (elem.getBoundingClientRect().left + viewport.scrollLeft) | 0;
  }-*/;

  private native int getAbsoluteTopImpl(Element viewport, Element elem) /*-{
    return (elem.getBoundingClientRect().top + viewport.scrollTop) | 0;
  }-*/;
}

