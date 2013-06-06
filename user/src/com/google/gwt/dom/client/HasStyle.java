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

/**
 * A characteristic interface for types that can have their style changed.
 *
 * This interface is meant to implemented by both {@link Element} and Widget so
 * that common UI logic (like "hide" or "show") can work against both elements
 * and widgets.
 */
public interface HasStyle {

  void addStyleName(String styleName);

  void removeStyleName(String styleName);

  void setStyleName(String styleName);

  boolean hasStyleName(String styleName);

  String getStyleName();

  IsStyle getIsStyle();

}
