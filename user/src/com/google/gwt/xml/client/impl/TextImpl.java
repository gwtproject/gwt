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
import com.google.gwt.xml.client.Text;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * This class is the implementation of the XML DOM Text interface.
 */
class TextImpl extends CharacterDataImpl implements Text {

  @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
  static class NativeTextImpl extends NativeCharacterDataImpl {
    native NativeTextImpl splitText(int offset);
  }

  private final NativeTextImpl text;

  protected TextImpl(NativeTextImpl o) {
    super(o);
    this.text = o;
  }

  /**
   * This function delegates to the native method <code>splitText</code> in
   * XMLParserImpl.
   */
  @Override
  public Text splitText(int offset) {
    try {
      return (Text) NodeImpl.build(text.splitText(offset));
    } catch (Exception e) {
      throw new DOMNodeException(DOMException.INVALID_MODIFICATION_ERR, e, this);
    }
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    String[] x = getData().split("(?=[;&<>\'\"])", -1);
    for (int i = 0; i < x.length; i++) {
      if (x[i].startsWith(";")) {
        b.append("&semi;");
        b.append(x[i].substring(1));
      } else if (x[i].startsWith("&")) {
        b.append("&amp;");
        b.append(x[i].substring(1));
      } else if (x[i].startsWith("\"")) {
        b.append("&quot;");
        b.append(x[i].substring(1));
      } else if (x[i].startsWith("'")) {
        b.append("&apos;");
        b.append(x[i].substring(1));
      } else if (x[i].startsWith("<")) {
        b.append("&lt;");
        b.append(x[i].substring(1));
      } else if (x[i].startsWith(">")) {
        b.append("&gt;");
        b.append(x[i].substring(1));
      } else {
        b.append(x[i]);
      }
    }
    return b.toString();
  }
}
