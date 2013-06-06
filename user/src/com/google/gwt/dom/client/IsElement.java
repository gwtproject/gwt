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
package com.google.gwt.dom.client;

import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * An interface for {@link Element}.
 */
public interface IsElement {

  /**
   * See {@link Element#addClassName(java.lang.String)}.
   */
  boolean addClassName(String className);

  /**
   * See {@link Element#blur()}.
   */
  void blur();

  /**
   * See {@link Element#focus()}.
   */
  void focus();

  /**
   * See {@link Element#getAbsoluteBottom()}.
   */
  int getAbsoluteBottom();

  /**
   * See {@link Element#getAbsoluteLeft()}.
   */
  int getAbsoluteLeft();

  /**
   * See {@link Element#getAbsoluteTop()}.
   */
  int getAbsoluteTop();

  /**
   * See {@link Element#getAbsoluteRight()}.
   */
  int getAbsoluteRight();

  /**
   * See {@link Element#getAttribute(java.lang.String)}.
   */
  String getAttribute(String name);

  /**
   * See {@link Element#getClassName()}.
   */
  String getClassName();

  /**
   * See {@link Element#getClientHeight()}.
   */
  int getClientHeight();

  /**
   * See {@link Element#getClientWidth()}.
   */
  int getClientWidth();

  /**
   * See {@link Element#getDir()}.
   */
  String getDir();

  /**
   * See {@link Element#getDraggable()}.
   */
  String getDraggable();

  /**
   * See {@link Element#getFirstChildElement()}.
   */
  IsElement getFirstChildElement();

  /**
   * See {@link Element#getId()}.
   */
  String getId();

  /**
   * See {@link Element#getInnerHTML()}.
   */
  String getInnerHTML();

  /**
   * See {@link Element#getInnerText()}.
   */
  String getInnerText();

  /**
   * See {@link Element#getLang()}.
   */
  String getLang();

  /**
   * See {@link Element#getNextSiblingElement()}.
   */
  IsElement getNextSiblingElement();

  /**
   * See {@link Element#getOffsetHeight()}.
   */
  int getOffsetHeight();

  /**
   * See {@link Element#getOffsetLeft()}.
   */
  int getOffsetLeft();

  /**
   * See {@link Element#getOffsetParent()}.
   */
  IsElement getOffsetParent();

  /**
   * See {@link Element#getOffsetTop()}.
   */
  int getOffsetTop();

  /**
   * See {@link Element#getOffsetWidth()}.
   */
  int getOffsetWidth();

  /**
   * See {@link Element#getPreviousSiblingElement()}.
   */
  IsElement getPreviousSiblingElement();

  /**
   * See {@link Element#getPropertyBoolean(java.lang.String)}.
   */
  boolean getPropertyBoolean(String name);

  /**
   * See {@link Element#getPropertyDouble(java.lang.String)}.
   */
  double getPropertyDouble(String name);

  /**
   * See {@link Element#getPropertyInt(java.lang.String)}.
   */
  int getPropertyInt(String name);

  /**
   * See {@link Element#getPropertyObject(java.lang.String)}.
   */
  Object getPropertyObject(String name);

  /**
   * See {@link Element#getPropertyString(java.lang.String)}.
   */
  String getPropertyString(String name);

  /**
   * See {@link Element#getScrollHeight()}.
   */
  int getScrollHeight();

  /**
   * See {@link Element#getScrollTop()}.
   */
  int getScrollTop();

  /**
   * See {@link Element#getScrollLeft()}.
   */
  int getScrollLeft();

  /**
   * See {@link Element#getScrollWidth()}.
   */
  int getScrollWidth();

  /**
   * See {@link Element#getString()}.
   */
  String getString();

  /**
   * See {@link Element#getStyle()}.
   */
  IsStyle getStyle();

  /**
   * See {@link Element#getTabIndex()}.
   */
  int getTabIndex();

  /**
   * See {@link Element#getTagName()}.
   */
  String getTagName();

  /**
   * See {@link Element#getTitle()}.
   */
  String getTitle();

  /**
   * See {@link Element#hasAttribute(java.lang.String)}.
   */
  boolean hasAttribute(String name);

  /**
   * See {@link Element#hasClassName(java.lang.String)}.
   */
  boolean hasClassName(String className);

  /**
   * See {@link Element#hasTagName(java.lang.String)}.
   */
  boolean hasTagName(String name);

  /**
   * See {@link Element#removeAttribute(java.lang.String)}.
   */
  void removeAttribute(String name);

  /**
   * See {@link Element#removeClassName(java.lang.String)}.
   */
  boolean removeClassName(String className);

  /**
   * See {@link Element#replaceClassName(java.lang.String, java.lang.String)}.
   */
  void replaceClassName(String oldClassName, String newClassName);

  /**
   * See {@link Element#scrollIntoView()}.
   */
  void scrollIntoView();

  /**
   * See {@link Element#setAttribute(java.lang.String, java.lang.String)}.
   */
  void setAttribute(String name, String value);

  /**
   * See {@link Element#setClassName(java.lang.String)}.
   */
  void setClassName(String className);

  /**
   * See {@link Element#setDir(java.lang.String)}.
   */
  void setDir(String dir);

  /**
   * See {@link Element#setDraggable(java.lang.String)}.
   */
  void setDraggable(String draggable);

  /**
   * See {@link Element#setId(java.lang.String)}.
   */
  void setId(String id);

  /**
   * See {@link Element#setInnerHTML(java.lang.String)}.
   */
  void setInnerHTML(String html);

  /**
   * See {@link Element#setInnerSafeHtml(com.google.gwt.safehtml.shared.SafeHtml)}.
   */
  void setInnerSafeHtml(SafeHtml html);

  /**
   * See {@link Element#setInnerText(java.lang.String)}.
   */
  void setInnerText(String text);

  /**
   * See {@link Element#setLang(java.lang.String)}.
   */
  void setLang(String lang);

  /**
   * See {@link Element#setPropertyBoolean(java.lang.String, boolean)}.
   */
  void setPropertyBoolean(String name, boolean value);

  /**
   * See {@link Element#setPropertyDouble(java.lang.String, double)}.
   */
  void setPropertyDouble(String name, double value);

  /**
   * See {@link Element#setPropertyInt(java.lang.String, int)}.
   */
  void setPropertyInt(String name, int value);

  /**
   * See {@link Element#setPropertyObject(java.lang.String, java.lang.Object)}.
   */
  void setPropertyObject(String name, Object value);

  /**
   * See {@link Element#setPropertyString(java.lang.String, java.lang.String)}.
   */
  void setPropertyString(String name, String value);

  /**
   * See {@link Element#setScrollLeft(int)}.
   */
  void setScrollLeft(int scrollLeft);

  /**
   * See {@link Element#setScrollTop(int)}.
   */
  void setScrollTop(int scrollTop);

  /**
   * See {@link Element#setTabIndex(int)}.
   */
  void setTabIndex(int tabIndex);

  /**
   * See {@link Element#setTitle(java.lang.String)}.
   */
  void setTitle(String title);

  /**
   * See {@link Element#appendChild(com.google.gwt.dom.client.IsElement)}.
   */
  void appendChild(IsElement element);

  /**
   * See {@link Element#removeFromParent()}.
   */
  void removeFromParent();

  /**
   * @return the concrete {@link Element}
   */
  Element asElement();

}
