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

  @Override
  Element getLegacyDocumentScrollingElement(Document doc) {
    // Old WebKit needs body.scrollLeft in both quirks mode and strict mode.
    return doc.getBody();
  }
}

