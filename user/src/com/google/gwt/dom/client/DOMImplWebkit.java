/*
 * Copyright 2011 Google Inc.
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
 * WebKit based implementation of {@link com.google.gwt.dom.client.DOMImplStandardBase}.
 */
class DOMImplWebkit extends DOMImplStandardBase {

  /**
   * Return true if using Webkit 525.x (Safari 3) or earlier.
   * 
   * @return true if using Webkit 525.x (Safari 3) or earlier.
   */
  private static native boolean isWebkit525OrBefore() /*-{
    var result = /safari\/([\d.]+)/.exec(navigator.userAgent.toLowerCase());
    if (result) {
      var version = (parseFloat(result[1]));
      if (version < 526) {
        return true;
      }
    }
    return false;
  }-*/;

  /**
   * Webkit events sometimes target the text node inside of the element instead
   * of the element itself, so we need to get the parent of the text node.
   */
  @Override
  public native EventTarget eventGetTarget(NativeEvent evt) /*-{
    var target = evt.target;
    if (target && target.nodeType == 3) {
      target = target.parentNode;
    }
    return target;
  }-*/;

  /**
   * Webkit based browsers require that we set the webkit-user-drag style
   * attribute to make an element draggable.
   */
  @Override
  public void setDraggable(Element elem, String draggable) {
    super.setDraggable(elem, draggable);
    if ("true".equals(draggable)) {
      elem.getStyle().setProperty("webkitUserDrag", "element");
    } else {
      elem.getStyle().clearProperty("webkitUserDrag");
    }
  }

  /**
   * The type property on a button element is read-only in safari, so we need to
   * set it using setAttribute.
   */
  @Override
  public native ButtonElement createButtonElement(Document doc, String type) /*-{
    var e = doc.createElement("BUTTON");
    e.setAttribute('type', type);
    return e;
  }-*/;

  /**
   * Safari 2 does not support {@link ScriptElement#setText(String)}.
   */
  @Override
  public ScriptElement createScriptElement(Document doc, String source) {
    ScriptElement elem = (ScriptElement) createElement(doc, "script");
    elem.setInnerText(source);
    return elem;
  }

  @Override
  public int getScrollLeft(Document doc) {
    // Safari always applies document scrolling to the body element, even in
    // strict mode.
    return doc.getBody().getScrollLeft();
  }

  @Override
  public int getScrollTop(Document doc) {
    // Safari always applies document scrolling to the body element, even in
    // strict mode.
    return doc.getBody().getScrollTop();
  }

  @Override
  public native int getTabIndex(Element elem) /*-{ 
    // tabIndex is undefined for divs and other non-focusable elements prior to
    // Safari 4.
    return typeof elem.tabIndex != 'undefined' ? elem.tabIndex : -1;
  }-*/;

  @Override
  public void setScrollLeft(Document doc, int left) {
    // Safari always applies document scrolling to the body element, even in
    // strict mode.
    doc.getBody().setScrollLeft(left);
  }

  @Override
  public void setScrollTop(Document doc, int top) {
    // Safari always applies document scrolling to the body element, even in
    // strict mode.
    doc.getBody().setScrollTop(top);
  }
}

