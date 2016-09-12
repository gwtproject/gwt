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

import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.ProcessingInstruction;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * This class implements the XML DOM ProcessingInstruction interface.
 */
class ProcessingInstructionImpl extends NodeImpl implements
    ProcessingInstruction {

  @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
  static class NativeProcessingInstructionImpl extends NativeNodeImpl {
    String data;
    String target;
  }

  private final NativeProcessingInstructionImpl instructions;

  protected ProcessingInstructionImpl(NativeProcessingInstructionImpl o) {
    super(o);
    this.instructions = o;
  }

  /**
   * This function delegates to the native method <code>getData</code> in
   * XMLParserImpl.
   */
  @Override
  public String getData() {
    return instructions.data;
  }

  /**
   * This function delegates to the native method <code>getTarget</code> in
   * XMLParserImpl.
   */
  @Override
  public String getTarget() {
    return instructions.target;
  }

  /**
   * This function delegates to the native method <code>setData</code> in
   * XMLParserImpl.
   */
  @Override
  public void setData(String data) {
    try {
      instructions.data = data;
    } catch (Exception e) {
      throw new DOMNodeException(DOMException.INVALID_CHARACTER_ERR, e, this);
    }
  }

  @Override
  public String toString() {
    return XMLParserImpl.getInstance().toStringImpl(this);
  }
}
