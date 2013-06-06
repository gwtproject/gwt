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

  boolean addClassName(String className);

  void blur();

  void focus();

  int getAbsoluteBottom();

  int getAbsoluteLeft();

  int getAbsoluteTop();

  int getAbsoluteRight();

  String getAttribute(String name);

  String getClassName();

  int getClientHeight();

  int getClientWidth();

  String getDir();

  String getDraggable();

  IsElement getFirstChildElement();

  String getId();

  String getInnerHTML();

  String getInnerText();

  String getLang();

  IsElement getNextSiblingElement();

  int getOffsetHeight();

  int getOffsetLeft();

  IsElement getOffsetParent();

  int getOffsetTop();

  int getOffsetWidth();

  IsElement getPreviousSiblingElement();

  boolean getPropertyBoolean(String name);

  double getPropertyDouble(String name);

  int getPropertyInt(String name);

  Object getPropertyObject(String name);

  String getPropertyString(String name);

  int getScrollHeight();

  int getScrollTop();

  int getScrollLeft();

  int getScrollWidth();

  String getString();

  IsStyle getStyle();

  int getTabIndex();

  String getTagName();

  String getTitle();

  boolean hasAttribute(String name);

  boolean hasClassName(String className);

  boolean hasTagName(String name);

  void removeAttribute(String name);

  boolean removeClassName(String className);

  void replaceClassName(String oldClassName, String newClassName);

  void scrollIntoView();

  void setAttribute(String name, String value);

  void setClassName(String className);

  void setDir(String dir);

  void setDraggable(String draggable);

  void setId(String id);

  void setInnerHTML(String html);

  void setInnerSafeHtml(SafeHtml html);

  void setInnerText(String text);

  void setLang(String lang);

  void setPropertyBoolean(String name, boolean value);

  void setPropertyDouble(String name, double value);

  void setPropertyInt(String name, int value);

  void setPropertyObject(String name, Object value);

  void setPropertyString(String name, String value);

  void setScrollLeft(int scrollLeft);

  void setScrollTop(int scrollTop);

  void setTabIndex(int tabIndex);

  void setTitle(String title);

  void appendChild(IsElement element);

  void removeFromParent();

  Element asElement();

}
