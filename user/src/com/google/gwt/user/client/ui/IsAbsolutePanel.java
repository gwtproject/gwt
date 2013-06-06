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

/**
 * An interface for {@link AbsolutePanel}.
 */
public interface IsAbsolutePanel extends IsComplexPanel, InsertPanel.ForIsWidget {

  /**
   * See {@link AbsolutePanel#add(IsWidget, int, int)}.
   */
  void add(IsWidget w, int left, int top);

  /**
   * See {@link AbsolutePanel#getWidgetLeft(IsWidget)}.
   */
  int getWidgetLeft(IsWidget w);

  /**
   * See {@link AbsolutePanel#getWidgetTop(IsWidget)}.
   */
  int getWidgetTop(IsWidget w);

  /**
   * See {@link AbsolutePanel#insert(IsWidget, int)}.
   */
  void insert(IsWidget w, int beforeIndex);

  /**
   * See {@link AbsolutePanel#insert(IsWidget, int, int, int)}.
   */
  void insert(IsWidget w, int left, int top, int beforeIndex);

  /**
   * See {@link AbsolutePanel#setWidgetPosition(IsWidget, int, int)}.
   */
  void setWidgetPosition(IsWidget w, int left, int top);

}
