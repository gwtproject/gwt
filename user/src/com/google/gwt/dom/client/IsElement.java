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
public interface IsElement extends HasStyle {

  // TODO(stephen) Add the rest of the Element methods

  String getId();

  void setId(String id);

  int getOffsetWidth();

  int getOffsetHeight();

  int getClientHeight();

  int getClientWidth();

  int getScrollHeight();

  int getScrollWidth();

  int getScrollTop();

  int getScrollLeft();

  void setScrollTop(int scrollTop);

  void setScrollLeft(int scrollLeft);

  String getAttribute(String name);

  void setAttribute(String name, String value);

  String getInnerText();

  void setInnerText(String text);

  String getInnerHTML();

  void setInnerHTML(String html);

  void setInnerSafeHtml(SafeHtml html);

  void appendChild(IsElement element);

  void removeFromParent();

  Element asElement();

}
