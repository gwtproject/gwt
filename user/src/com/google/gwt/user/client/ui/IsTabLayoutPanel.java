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

import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface IsTabLayoutPanel extends IsWidget.Extended, HasWidgets.ForExtendedIsWidget,
    IndexedPanel.ForExtendedIsWidget, ProvidesResize, AnimatedLayout, HasBeforeSelectionHandlers<Integer>,
    HasSelectionHandlers<Integer> {

  void add(IsWidget w, String tabText);

  void add(IsWidget w, String tabText, boolean tabIsHtml);

  void add(IsWidget w, com.google.gwt.user.client.ui.IsWidget tabWidget);

  HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<Integer> handler);

  HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler);

  int getAnimationDuration();

  int getSelectedIndex();

  IsWidget.Extended getTabIsWidget(int index);

  IsWidget.Extended getTabIsWidget(IsWidget child);

  void insert(IsWidget child, int beforeIndex);

  void insert(IsWidget child, String tabText, boolean tabIsHtml, int beforeIndex);

  void insert(IsWidget child, String tabText, int beforeIndex);

  void insert(IsWidget child, IsWidget tabWidget, int beforeIndex);

  boolean isAnimationVertical();

  void selectTab(int index);

  void selectTab(int index, boolean fireEvents);

  void selectTab(IsWidget child);

  void selectTab(IsWidget child, boolean fireEvents);

  void setTabHTML(int index, String html);

  void setTabHTML(int index, SafeHtml html);

  void setTabText(int index, String text);

  void setAnimationDuration(int duration);

  void setAnimationVertical(boolean isVertical);
}
