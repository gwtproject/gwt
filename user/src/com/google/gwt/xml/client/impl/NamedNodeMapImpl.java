/*
 * Copyright 2007 Google Inc.
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
package com.google.gwt.xml.client.impl;

import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.impl.NodeImpl.NativeNodeImpl;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * This class implements the NamedNodeMap interface.
 */
class NamedNodeMapImpl extends NodeListImpl implements NamedNodeMap {

  @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
  static class NativeNamedNodeMapImpl  extends NativeNodeListImpl {
    native NativeNodeImpl getNamedItem(String name);
    native NativeNodeImpl removeNamedItem(String name);
    native NativeNodeImpl setNamedItem(NativeDomItem value);
  }

  private final NativeNamedNodeMapImpl nodeMap;

  protected NamedNodeMapImpl(NativeNamedNodeMapImpl o) {
    super(o);
    this.nodeMap = o;
  }

  /**
   * This method gets the item at the index position.
   * 
   * @param name - the name of the item 
   * @return the item retrieved from the name
   */
  @Override
  public Node getNamedItem(String name) {
    return NodeImpl.build(nodeMap.getNamedItem(name));
  }

  /**
   * This function delegates to the native method <code>removeNamedItem</code>
   * in XMLParserImpl.
   */
  public Node removeNamedItem(String name) {
    try {
      return NodeImpl.build(nodeMap.removeNamedItem(name));
    } catch (Exception e) {
      throw new DOMNodeException(DOMException.INVALID_MODIFICATION_ERR, e, this);
    }
  }

  /**
   * This function delegates to the native method <code>setNamedItem</code> in XMLParserImpl.
   */
  public Node setNamedItem(Node arg) {
    try {
      return NodeImpl.build(nodeMap.setNamedItem(((NodeImpl) arg).node));
    } catch (Exception e) {
      throw new DOMNodeException(DOMException.INVALID_MODIFICATION_ERR, e, this);
    }
  }
}
