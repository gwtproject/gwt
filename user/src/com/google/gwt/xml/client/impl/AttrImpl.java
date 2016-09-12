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

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * This class implements the XML Attr interface.
 */
class AttrImpl extends NodeImpl implements Attr {

  @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
  static class NativeAttrImpl extends NativeNodeImpl {
    String name;
    boolean specified;
    String value;
  }

  private final NativeAttrImpl attrValue;

  protected AttrImpl(NativeAttrImpl o) {
    super(o);
    this.attrValue = o;
  }

  /**
   * This function delegates to the native method <code>getName</code> in
   * XMLParserImpl.
   */
  @Override
  public String getName() {
    return attrValue.name;
  }

  /**
   * This function delegates to the native method <code>getSpecified</code> in
   * XMLParserImpl.
   */
  @Override
  public boolean getSpecified() {
    return attrValue.specified;
  }

  /**
   * This function delegates to the native method <code>getValue</code> in
   * XMLParserImpl.
   */
  @Override
  public String getValue() {
    return attrValue.value;
  }
}
