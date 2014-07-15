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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;

abstract class DOMImpl {

  static final DOMImpl impl = GWT.create(DOMImpl.class);

  /**
   * Fast helper method to convert small doubles to 32-bit int.
   *
   * <p>Note: you should be aware that this uses JavaScript rounding and thus
   * does NOT provide the same semantics as <code>int b = (int) someDouble;</code>.
   * In particular, if x is outside the range [-2^31,2^31), then toInt32(x) would return a value
   * equivalent to x modulo 2^32, whereas (int) x would evaluate to either MIN_INT or MAX_INT.
   */
  protected static native int toInt32(double val) /*-{
    return val | 0;
  }-*/;

  public native void buttonClick(ButtonElement button) /*-{
    button.click();
  }-*/;

  public native ButtonElement createButtonElement(Document doc, String type) /*-{
    var e = doc.createElement("BUTTON");
    e.type = type;
    return e;
  }-*/;

  public native InputElement createCheckInputElement(Document doc) /*-{
    var e = doc.createElement("INPUT");
    e.type = 'checkbox';
    e.value = 'on';
    return e;
  }-*/;

  public native Element createElement(Document doc, String tag) /*-{
    return doc.createElement(tag);
  }-*/;

  public abstract NativeEvent createHtmlEvent(Document doc, String type,
      boolean canBubble, boolean cancelable);

  public native InputElement createInputElement(Document doc, String type) /*-{
    var e = doc.createElement("INPUT");
    e.type = type;
    return e;
  }-*/;

  public abstract InputElement createInputRadioElement(Document doc, String name);

  public abstract NativeEvent createKeyCodeEvent(Document document,
      String type, boolean ctrlKey, boolean altKey, boolean shiftKey,
      boolean metaKey, int keyCode);

  @Deprecated
  public abstract NativeEvent createKeyEvent(Document doc, String type,
      boolean canBubble, boolean cancelable, boolean ctrlKey, boolean altKey,
      boolean shiftKey, boolean metaKey, int keyCode, int charCode);

  public abstract NativeEvent createKeyPressEvent(Document document,
      boolean ctrlKey, boolean altKey, boolean shiftKey, boolean metaKey,
      int charCode);

  public abstract NativeEvent createMouseEvent(Document doc, String type,
      boolean canBubble, boolean cancelable, int detail, int screenX,
      int screenY, int clientX, int clientY, boolean ctrlKey, boolean altKey,
      boolean shiftKey, boolean metaKey, int button, Element relatedTarget);

  public ScriptElement createScriptElement(Document doc, String source) {
    ScriptElement elem = (ScriptElement) createElement(doc, "script");
    elem.setText(source);
    return elem;
  }

  public native void cssClearOpacity(Style style) /*-{
    style.opacity = '';
  }-*/;

  public String cssFloatPropertyName() {
    return "cssFloat";
  }

  public native void cssSetOpacity(Style style, double value) /*-{
    style.opacity = value;
  }-*/;

  public abstract void dispatchEvent(Element target, NativeEvent evt);

  public native boolean eventGetAltKey(NativeEvent evt) /*-{
    return !!evt.altKey;
  }-*/;

  public native int eventGetButton(NativeEvent evt) /*-{
    return evt.button | 0;
  }-*/;

  public abstract int eventGetCharCode(NativeEvent evt);

  public int eventGetClientX(NativeEvent evt) {
    return toInt32(eventGetSubPixelClientX(evt));
  }

  public int eventGetClientY(NativeEvent evt) {
    return toInt32(eventGetSubPixelClientY(evt));
  }

  public native boolean eventGetCtrlKey(NativeEvent evt) /*-{
    return !!evt.ctrlKey;
  }-*/;

  public native EventTarget eventGetCurrentTarget(NativeEvent event) /*-{
    return event.currentTarget;
  }-*/;

  public final native int eventGetKeyCode(NativeEvent evt) /*-{
    return evt.keyCode | 0;
  }-*/;

  public native boolean eventGetMetaKey(NativeEvent evt) /*-{
    return !!evt.metaKey;
  }-*/;

  public abstract int eventGetMouseWheelVelocityY(NativeEvent evt);

  public abstract EventTarget eventGetRelatedTarget(NativeEvent nativeEvent);

  public native double eventGetRotation(NativeEvent evt) /*-{
    return evt.rotation;
  }-*/;

  public native double eventGetScale(NativeEvent evt) /*-{
    return evt.scale;
  }-*/;

  public int eventGetScreenX(NativeEvent evt) {
    return toInt32(eventGetSubPixelScreenX(evt));
  }

  public int eventGetScreenY(NativeEvent evt) {
    return toInt32(eventGetSubPixelScreenY(evt));
  }

  public native boolean eventGetShiftKey(NativeEvent evt) /*-{
    return !!evt.shiftKey;
  }-*/;

  public abstract EventTarget eventGetTarget(NativeEvent evt);

  public final native String eventGetType(NativeEvent evt) /*-{
    return evt.type;
  }-*/;

  public abstract void eventPreventDefault(NativeEvent evt);

  public native void eventSetKeyCode(NativeEvent evt, char key) /*-{
    evt.keyCode = key;
  }-*/;

  public native void eventStopPropagation(NativeEvent evt) /*-{
    evt.stopPropagation();
  }-*/;

  public abstract String eventToString(NativeEvent evt);

  public int getAbsoluteLeft(Element elem) {
    return toInt32(getSubPixelAbsoluteLeft(elem));
  }

  public int getAbsoluteTop(Element elem) {
    return toInt32(getSubPixelAbsoluteTop(elem));
  }

  public native String getAttribute(Element elem, String name) /*-{
    return elem.getAttribute(name) || '';
  }-*/;

  public native int getBodyOffsetLeft(Document doc) /*-{
    return 0;
  }-*/;

  public native int getBodyOffsetTop(Document doc) /*-{
    return 0;
  }-*/;

  public native JsArray<Touch> getChangedTouches(NativeEvent evt) /*-{
    return evt.changedTouches;
  }-*/;

  public native Element getFirstChildElement(Element elem) /*-{
    var child = elem.firstChild;
    while (child && child.nodeType != 1)
      child = child.nextSibling;
    return child;
  }-*/;

  public native String getInnerHTML(Element elem) /*-{
    return elem.innerHTML;
  }-*/;

  public native String getInnerText(Element node) /*-{
    // To mimic IE's 'innerText' property in the W3C DOM, we need to recursively
    // concatenate all child text nodes (depth first).
    var text = '', child = node.firstChild;
    while (child) {
      // 1 == Element node
      if (child.nodeType == 1) {
        text += this.@com.google.gwt.dom.client.DOMImpl::getInnerText(Lcom/google/gwt/dom/client/Element;)(child);
      } else if (child.nodeValue) {
        text += child.nodeValue;
      }
      child = child.nextSibling;
    }
    return text;
  }-*/;

  public native Element getNextSiblingElement(Element elem) /*-{
    var sib = elem.nextSibling;
    while (sib && sib.nodeType != 1)
      sib = sib.nextSibling;
    return sib;
  }-*/;

  public native int getNodeType(Node node) /*-{
    return node.nodeType;
  }-*/;

  /**
   * Returns a numeric style property (such as zIndex) that may need to be
   * coerced to a string.
   */
  public String getNumericStyleProperty(Style style, String name) {
    return getStyleProperty(style, name);
  }

  public native Element getParentElement(Node node) /*-{
    var parent = node.parentNode;
    if (!parent || parent.nodeType != 1) {
      parent = null;
    }
    return parent;
  }-*/;

  public native Element getPreviousSiblingElement(Element elem) /*-{
    var sib = elem.previousSibling;
    while (sib && sib.nodeType != 1)
      sib = sib.previousSibling;
    return sib;
  }-*/;

  public int getScrollLeft(Document doc) {
    return doc.getViewportElement().getScrollLeft();
  }

  public int getScrollLeft(Element elem) {
    return toInt32(getSubPixelScrollLeft(elem));
  }

  public int getScrollTop(Document doc) {
    return doc.getViewportElement().getScrollTop();
  }

  public native String getStyleProperty(Style style, String name) /*-{
    return style[name];
  }-*/;

  public native int getTabIndex(Element elem) /*-{
    return elem.tabIndex;
  }-*/;

  public native String getTagName(Element elem) /*-{
    return elem.tagName;
  }-*/;

  public native JsArray<Touch> getTargetTouches(NativeEvent evt) /*-{
    return evt.targetTouches;
  }-*/;

  public native JsArray<Touch> getTouches(NativeEvent evt) /*-{
    return evt.touches;
  }-*/;

  public native boolean hasAttribute(Element elem, String name) /*-{
    return elem.hasAttribute(name);
  }-*/;

  public abstract boolean isOrHasChild(Node parent, Node child);

  public native void scrollIntoView(Element elem) /*-{
    var left = elem.offsetLeft, top = elem.offsetTop;
    var width = elem.offsetWidth, height = elem.offsetHeight;

    if (elem.parentNode != elem.offsetParent) {
      left -= elem.parentNode.offsetLeft;
      top -= elem.parentNode.offsetTop;
    }

    var cur = elem.parentNode;
    while (cur && (cur.nodeType == 1)) {
      if (left < cur.scrollLeft) {
        cur.scrollLeft = left;
      }
      if (left + width > cur.scrollLeft + cur.clientWidth) {
        cur.scrollLeft = (left + width) - cur.clientWidth;
      }
      if (top < cur.scrollTop) {
        cur.scrollTop = top;
      }
      if (top + height > cur.scrollTop + cur.clientHeight) {
        cur.scrollTop = (top + height) - cur.clientHeight;
      }

      var offsetLeft = cur.offsetLeft, offsetTop = cur.offsetTop;
      if (cur.parentNode != cur.offsetParent) {
        offsetLeft -= cur.parentNode.offsetLeft;
        offsetTop -= cur.parentNode.offsetTop;
      }

      left += offsetLeft - cur.scrollLeft;
      top += offsetTop - cur.scrollTop;
      cur = cur.parentNode;
    }
  }-*/;

  public native void selectAdd(SelectElement select, OptionElement option,
      OptionElement before) /*-{
    select.add(option, before);
  }-*/;

  public native void selectClear(SelectElement select) /*-{
    select.options.length = 0;
  }-*/;

  public native int selectGetLength(SelectElement select) /*-{
    return select.options.length;
  }-*/;

  public native NodeList<OptionElement> selectGetOptions(SelectElement select) /*-{
    return select.options;
  }-*/;

  public native void selectRemoveOption(SelectElement select, int index) /*-{
    select.remove(index);
  }-*/;

  public native void setDraggable(Element elem, String draggable) /*-{
    elem.draggable = draggable;
  }-*/;

  public native void setInnerText(Element elem, String text) /*-{
    // Remove all children first.
    while (elem.firstChild) {
      elem.removeChild(elem.firstChild);
    }
    // Add a new text node.
    if (text != null) {
      elem.appendChild(elem.ownerDocument.createTextNode(text));
    }
  }-*/;

  public void setScrollLeft(Document doc, int left) {
    doc.getViewportElement().setScrollLeft(left);
  }

  public native void setScrollLeft(Element elem, int left) /*-{
    elem.scrollLeft = left;
  }-*/;

  public void setScrollTop(Document doc, int top) {
    doc.getViewportElement().setScrollTop(top);
  }

  public native String toString(Element elem) /*-{
    return elem.outerHTML;
  }-*/;

  public int touchGetClientX(Touch touch) {
    return toInt32(touchGetSubPixelClientX(touch));
  }

  public int touchGetClientY(Touch touch) {
    return toInt32(touchGetSubPixelClientY(touch));
  }

  public native int touchGetIdentifier(Touch touch) /*-{
    return touch.identifier;
  }-*/;

  public int touchGetPageX(Touch touch) {
    return toInt32(touchGetSubPixelPageX(touch));
  }

  public int touchGetPageY(Touch touch) {
    return toInt32(touchGetSubPixelPageY(touch));
  }

  public int touchGetScreenX(Touch touch) {
    return toInt32(touchGetSubPixelScreenX(touch));
  }

  public int touchGetScreenY(Touch touch) {
    return toInt32(touchGetSubPixelScreenY(touch));
  }

  public native EventTarget touchGetTarget(Touch touch) /*-{
    return touch.target;
  }-*/;

  private native double getSubPixelAbsoluteLeft(Element elem) /*-{
    var left = 0;
    var curr = elem;
    // This intentionally excludes body which has a null offsetParent.
    while (curr.offsetParent) {
      left -= curr.scrollLeft;
      curr = curr.parentNode;
    }
    while (elem) {
      left += elem.offsetLeft;
      elem = elem.offsetParent;
    }
    return left;
  }-*/;

  private native double getSubPixelAbsoluteTop(Element elem) /*-{
    var top = 0;
    var curr = elem;
    // This intentionally excludes body which has a null offsetParent.
    while (curr.offsetParent) {
      top -= curr.scrollTop;
      curr = curr.parentNode;
    }
    while (elem) {
      top += elem.offsetTop;
      elem = elem.offsetParent;
    }
    return top;
  }-*/;

  private native double eventGetSubPixelScreenX(NativeEvent evt) /*-{
    return evt.screenX || 0;
  }-*/;

  private native double eventGetSubPixelScreenY(NativeEvent evt) /*-{
    return evt.screenY || 0;
  }-*/;

  private native double getSubPixelScrollLeft(Element elem) /*-{
    return elem.scrollLeft || 0;
  }-*/;

  private native double touchGetSubPixelClientX(Touch touch) /*-{
    return touch.clientX || 0;
  }-*/;

  private native double touchGetSubPixelClientY(Touch touch) /*-{
    return touch.clientY || 0;
  }-*/;

  private native double touchGetSubPixelPageX(Touch touch) /*-{
    return touch.pageX || 0;
  }-*/;

  private native double touchGetSubPixelPageY(Touch touch) /*-{
    return touch.pageY || 0;
  }-*/;

  private native double touchGetSubPixelScreenX(Touch touch) /*-{
    return touch.screenX || 0;
  }-*/;

  private native double touchGetSubPixelScreenY(Touch touch) /*-{
    return touch.screenY || 0;
  }-*/;

  private native double eventGetSubPixelClientX(NativeEvent evt) /*-{
    return evt.clientX || 0;
  }-*/;

  private native double eventGetSubPixelClientY(NativeEvent evt) /*-{
    return evt.clientY || 0;
  }-*/;
}
