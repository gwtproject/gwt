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
package com.google.gwt.xml.client.impl;

import com.google.gwt.xml.client.Attr;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * This method implements the Element interface.
 */
class ElementImpl extends NodeImpl implements Element {

  @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
  static class NativeElementImpl extends NativeNodeImpl {
    String data;
    NativeElementImpl firstChild;
    String tagName;

    native String getAttribute(String name);
    native NativeNodeImpl getAttributeNode(String name);
    native void removeAttribute(String name);
    native void setAttribute(String name, String value);
  }

  private final NativeElementImpl element;

  protected ElementImpl(NativeElementImpl o) {
    super(o);
    this.element = o;
  }

  /**
   * This function delegates to the native method <code>getAttribute</code> in
   * XMLParserImpl.
   */
  @Override
  public String getAttribute(String tagName) {
    return element.getAttribute(tagName);
  }

  /**
   * This function delegates to the native method <code>getAttributeNode</code>
   * in XMLParserImpl.
   */
  @Override
  public Attr getAttributeNode(String tagName) {
    return (Attr) NodeImpl.build(element.getAttributeNode(tagName));
  }

  /**
   * This function delegates to the native method <code>getElementsByTagName</code> in
   * XMLParserImpl.
   */
  @Override
  public NodeList getElementsByTagName(String tagName) {
    return new NodeListImpl(XMLParserImpl.getElementsByTagName(element, tagName));
  }

  /**
   * This function delegates to the native method <code>getTagName</code> in
   * XMLParserImpl.
   */
  @Override
  public String getTagName() {
    return element.tagName;
  }

  /**
   * This function delegates to the native method <code>hasAttribute</code> in
   * XMLParserImpl.
   */
  @Override
  public boolean hasAttribute(String tagName) {
    return getAttribute(tagName) != null;
  }

  /**
   * This function delegates to the native method <code>removeAttribute</code>
   * in XMLParserImpl.
   */
  @Override
  public void removeAttribute(String name) throws DOMNodeException {
    try {
      element.removeAttribute(name);
    } catch (Exception e) {
      throw new DOMNodeException(DOMException.INVALID_MODIFICATION_ERR, e, this);
    }
  }

  /**
   * This function delegates to the native method <code>setAttribute</code> in
   * XMLParserImpl.
   */
  @Override
  public void setAttribute(String name, String value) throws DOMNodeException {
    try {
      element.setAttribute(name, value);
    } catch (Exception e) {
      throw new DOMNodeException(DOMException.INVALID_MODIFICATION_ERR, e, this);
    }
  }
}
