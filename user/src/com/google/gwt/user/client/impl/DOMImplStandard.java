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
package com.google.gwt.user.client.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

/**
 * Base implementation of {@link com.google.gwt.user.client.impl.DOMImpl} shared
 * by those browsers that come a bit closer to supporting a common standard (ie,
 * not IE).
 */
abstract class DOMImplStandard extends DOMImpl {

  private static Element captureElem;

  private static JavaScriptObject dispatchCapturedEvent;

  private static JavaScriptObject dispatchCapturedMouseEvent;

  private static JavaScriptObject dispatchDragEvent;

  private static JavaScriptObject dispatchEvent;

  private static JavaScriptObject dispatchUnhandledEvent;

  @Override
  public Element eventGetFromElement(Event evt) {
    if (evt.getType().equals(BrowserEvents.MOUSEOVER)) {
      return evt.getRelatedEventTarget().cast();
    }

    if (evt.getType().equals(BrowserEvents.MOUSEOUT)) {
      return evt.getEventTarget().cast();
    }

    return null;
  }

  @Override
  public Element eventGetToElement(Event evt) {
    if (evt.getType().equals(BrowserEvents.MOUSEOVER)) {
      return evt.getEventTarget().cast();
    }

    if (evt.getType().equals(BrowserEvents.MOUSEOUT)) {
      return evt.getRelatedEventTarget().cast();
    }

    return null;
  }

  @Override
  public native Element getChild(Element elem, int index) /*-{
    var count = 0, child = elem.firstChild;
    while (child) {
      if (child.nodeType == 1) {
        if (index == count)
          return child;
        ++count;
      }
      child = child.nextSibling;
    }

    return null;
  }-*/;

  @Override
  public native int getChildCount(Element elem) /*-{
    var count = 0, child = elem.firstChild;
    while (child) {
      if (child.nodeType == 1)
        ++count;
      child = child.nextSibling;
    }
    return count;
  }-*/;

  @Override
  public native int getChildIndex(Element parent, Element toFind) /*-{
    var count = 0, child = parent.firstChild;
    while (child) {
      if (child === toFind) {
        return count;
      }
      if (child.nodeType == 1) {
        ++count;
      }
      child = child.nextSibling;
    }
    return -1;
  }-*/;

  @Override
  public native void insertChild(Element parent, Element toAdd, int index) /*-{
    var count = 0, child = parent.firstChild, before = null;
    while (child) {
      if (child.nodeType == 1) {
        if (count == index) {
          before = child;
          break;
        }
        ++count;
      }
      child = child.nextSibling;
    }

    parent.insertBefore(toAdd, before);
  }-*/;

  @Override
  public void releaseCapture(Element elem) {
    maybeInitializeEventSystem();
    if (captureElem == elem) {
      captureElem = null;
    }
  }

  @Override
  public void setCapture(Element elem) {
    maybeInitializeEventSystem();
    captureElem = elem;
  }

  @Override
  public void sinkBitlessEvent(Element elem, String eventTypeName) {
    maybeInitializeEventSystem();
    sinkBitlessEventImpl(elem, eventTypeName);
  }

  @Override
  public void sinkEvents(Element elem, int bits) {
    maybeInitializeEventSystem();
    sinkEventsImpl(elem, bits);
  }

  @Override
  protected native void disposeEventSystem() /*-{
    $wnd.removeEventListener('click', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.removeEventListener('dblclick', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.removeEventListener('mousedown', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.removeEventListener('mouseup', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.removeEventListener('mousemove', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.removeEventListener('mouseover', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.removeEventListener('mouseout', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.removeEventListener('mousewheel', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.removeEventListener('keydown', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedEvent, true);
    $wnd.removeEventListener('keyup', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedEvent, true);
    $wnd.removeEventListener('keypress', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedEvent, true);

    // Touch and gesture events are not actually mouse events, but we treat
    // them as such, so that DOM#setCapture() and DOM#releaseCapture() work.
    $wnd.removeEventListener('touchstart', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.removeEventListener('touchmove', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.removeEventListener('touchend', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.removeEventListener('touchcancel', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.removeEventListener('gesturestart', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.removeEventListener('gesturechange', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.removeEventListener('gestureend', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
  }-*/;

  @Override
  protected native void initEventSystem() /*-{
    @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent = $entry(
      @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent(*)
    );

    @com.google.gwt.user.client.impl.DOMImplStandard::dispatchDragEvent = $entry(
      @com.google.gwt.user.client.impl.DOMImplStandard::dispatchDragEvent(*)
    );

    @com.google.gwt.user.client.impl.DOMImplStandard::dispatchUnhandledEvent = $entry(
      @com.google.gwt.user.client.impl.DOMImplStandard::dispatchUnhandledEvent(*)
    );

    @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedEvent = $entry(
      @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedEvent(*)
    );

    @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent = $entry(
      @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent(*)
    );

    $wnd.addEventListener('click', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.addEventListener('dblclick', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.addEventListener('mousedown', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.addEventListener('mouseup', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.addEventListener('mousemove', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.addEventListener('mouseover', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.addEventListener('mouseout', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.addEventListener('mousewheel', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.addEventListener('keydown', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedEvent, true);
    $wnd.addEventListener('keyup', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedEvent, true);
    $wnd.addEventListener('keypress', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedEvent, true);

    // Touch and gesture events are not actually mouse events, but we treat
    // them as such, so that DOM#setCapture() and DOM#releaseCapture() work.
    $wnd.addEventListener('touchstart', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.addEventListener('touchmove', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.addEventListener('touchend', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.addEventListener('touchcancel', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.addEventListener('gesturestart', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.addEventListener('gesturechange', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
    $wnd.addEventListener('gestureend', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchCapturedMouseEvent, true);
  }-*/;

  protected native void sinkBitlessEventImpl(Element elem, String eventTypeName) /*-{
    switch(eventTypeName) {
      case "drag":
        elem.ondrag           = @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent;
        break;
      case "dragend":
        elem.ondragend        = @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent;
        break;
      case "dragenter":
        elem.ondragenter      = @com.google.gwt.user.client.impl.DOMImplStandard::dispatchDragEvent;
        break;
      case "dragleave":
        elem.ondragleave      = @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent;
        break;
      case "dragover":
        elem.ondragover       = @com.google.gwt.user.client.impl.DOMImplStandard::dispatchDragEvent;
        break;
      case "dragstart":
        elem.ondragstart      = @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent;
        break;
      case "drop":
        elem.ondrop           = @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent;
        break;
      case "canplaythrough":
      case "ended":
      case "loadedmetadata":
      case "progress":
        // First call removeEventListener, so as not to add the same event listener more than once
        elem.removeEventListener(eventTypeName, @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, false);
        elem.addEventListener(eventTypeName, @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, false);
        break;
      default:
        // catch missing cases
        throw "Trying to sink unknown event type " + eventTypeName;
    }
  }-*/;

  protected native void sinkEventsImpl(Element elem, int bits) /*-{
    var chMask = (elem.__eventBits || 0) ^ bits;
    elem.__eventBits = bits;
    if (!chMask) return;

    if (chMask & 0x00001) elem.onclick       = (bits & 0x00001) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x00002) elem.ondblclick    = (bits & 0x00002) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x00004) elem.onmousedown   = (bits & 0x00004) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x00008) elem.onmouseup     = (bits & 0x00008) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x00010) elem.onmouseover   = (bits & 0x00010) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x00020) elem.onmouseout    = (bits & 0x00020) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x00040) elem.onmousemove   = (bits & 0x00040) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x00080) elem.onkeydown     = (bits & 0x00080) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x00100) elem.onkeypress    = (bits & 0x00100) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x00200) elem.onkeyup       = (bits & 0x00200) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x00400) elem.onchange      = (bits & 0x00400) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x00800) elem.onfocus       = (bits & 0x00800) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x01000) elem.onblur        = (bits & 0x01000) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x02000) elem.onlosecapture = (bits & 0x02000) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x04000) elem.onscroll      = (bits & 0x04000) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x08000) elem.onload        = (bits & 0x08000) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchUnhandledEvent : null;
    if (chMask & 0x10000) elem.onerror       = (bits & 0x10000) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x20000) elem.onmousewheel  = (bits & 0x20000) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x40000) elem.oncontextmenu = (bits & 0x40000) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x80000) elem.onpaste       = (bits & 0x80000) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x100000) elem.ontouchstart = (bits & 0x100000) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x200000) elem.ontouchmove  = (bits & 0x200000) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x400000) elem.ontouchend   = (bits & 0x400000) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x800000) elem.ontouchcancel= (bits & 0x800000) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x1000000) elem.ongesturestart  =(bits & 0x1000000) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x2000000) elem.ongesturechange =(bits & 0x2000000) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    if (chMask & 0x4000000) elem.ongestureend    = (bits & 0x4000000) ?
        @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
  }-*/;

  private static void dispatchEvent(Event evt) {
    Element element = getFirstAncestorWithListener(evt);
    if (element == null) {
      return;
    }
    DOM.dispatchEvent(evt, element.getNodeType() != 1 ? null : element, getEventListener(element));
  }

  private static Element getFirstAncestorWithListener(Event evt) {
    Element curElem = evt.getCurrentEventTarget().cast();
    while (curElem != null && getEventListener(curElem) == null) {
      curElem = curElem.getParentNode().cast();
    }
    return curElem;
  }

  private static void dispatchDragEvent(Event evt) {
    // Some drag events must call preventDefault to prevent native text selection.
    evt.preventDefault();
    dispatchEvent(evt);
  }

  private static void dispatchUnhandledEvent(Event evt) {
    Element element = evt.getCurrentEventTarget().cast();
    element.setPropertyString("__gwtLastUnhandledEvent", evt.getType());
    dispatchEvent(evt);
  }

  private static void dispatchCapturedEvent(Event evt) {
    DOM.previewEvent(evt);
  }

  private static void dispatchCapturedMouseEvent(Event evt) {
    boolean cancelled = !DOM.previewEvent(evt);
    if (cancelled || captureElem == null) {
      return;
    }
    if (DOM.dispatchEvent(evt, captureElem)) {
      evt.stopPropagation();
    }
  }
}
