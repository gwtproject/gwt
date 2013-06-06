/*
 * Copyright 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.user.client.ui;

import com.google.gwt.dom.client.IsElement;

/**
 * An interface for {@link UIObject}.
 */
public interface IsUIObject extends HasVisibility {

  /**
   * See {@link UIObject#addStyleName(java.lang.String)}.
   */
  void addStyleName(String styleName);

  /**
   * See {@link UIObject#ensureDebugId(java.lang.String)}.
   */
  void ensureDebugId(String id);

  /**
   * See {@link UIObject#getAbsoluteLeft()}.
   */
  int getAbsoluteLeft();

  /**
   * See {@link UIObject#getAbsoluteTop()}.
   */
  int getAbsoluteTop();

  /**
   * See {@link UIObject#getElement()}.
   */
  IsElement getElement();

  /**
   * See {@link UIObject#getOffsetHeight()}.
   */
  int getOffsetHeight();

  /**
   * See {@link UIObject#getOffsetWidth()}.
   */
  int getOffsetWidth();

  /**
   * See {@link UIObject#getStyleName()}.
   */
  String getStyleName();

  /**
   * See {@link UIObject#getTitle()}.
   */
  String getTitle();

  /**
   * See {@link UIObject#hasStyleName(java.lang.String)}.
   */
  boolean hasStyleName(String styleName);

  /**
   * See {@link UIObject#setHeight(java.lang.String)}.
   */
  void setHeight(String height);

  /**
   * See {@link UIObject#setPixelSize(int, int)}.
   */
  void setPixelSize(int width, int height);

  /**
   * See {@link UIObject#setSize(java.lang.String, java.lang.String)}.
   */
  void setSize(String width, String height);

  /**
   * See {@link UIObject#setStyleName(java.lang.String)}.
   */
  void setStyleName(String styleName);

  /**
   * See {@link UIObject#setTitle(java.lang.String)}.
   */
  void setTitle(String title);

  /**
   * See {@link UIObject#setWidth(java.lang.String)}.
   */
  void setWidth(String width);

  /**
   * See {@link UIObject#removeStyleName(java.lang.String)}.
   */
  void removeStyleName(String styleName);

}
