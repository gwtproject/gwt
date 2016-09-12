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

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * This class is the base class for all DOM object wrappers.
 */
class DOMItem {

  @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
  static class NativeDomItem {
  }

  private NativeDomItem domItem;

  protected DOMItem(NativeDomItem jso) {
    this.domItem = jso;
  }

  /**
   * This method determines equality for DOMItems.
   *
   * @param o - the other object being tested for equality
   * @return true iff the two objects are equal.
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object o) {
    if (o instanceof DOMItem) {
      return this.domItem == ((DOMItem) o).domItem;
    }
    return false;
  }

  /**
   * Returns the hash code for this DOMItem.
   */
  @Override
  public int hashCode() {
    return domItem.hashCode();
  }
}
