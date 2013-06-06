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

/**
 * An interface for {@link TabLayoutPanel}.
 */
public interface IsTabLayoutPanel extends IsWidget.Extended, HasWidgets.ForExtendedIsWidget,
    IndexedPanel.ForExtendedIsWidget, ProvidesResize, AnimatedLayout, HasBeforeSelectionHandlers<Integer>,
    HasSelectionHandlers<Integer> {

  /**
   * See {@link TabLayoutPanel#add(IsWidget, java.lang.String)}.
   */
  void add(IsWidget w, String tabText);

  /**
   * See {@link TabLayoutPanel#add(IsWidget, java.lang.String, boolean)}.
   */
  void add(IsWidget w, String tabText, boolean tabIsHtml);

  /**
   * See {@link TabLayoutPanel#add(IsWidget, com.google.gwt.user.client.ui.IsWidget)}.
   */
  void add(IsWidget w, com.google.gwt.user.client.ui.IsWidget tabWidget);

  /**
   * See {@link TabLayoutPanel#addBeforeSelectionHandler(BeforeSelectionHandler<Integer>)}.
   */
  HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<Integer> handler);

  /**
   * See {@link TabLayoutPanel#addSelectionHandler(SelectionHandler<Integer>)}.
   */
  HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler);

  /**
   * See {@link TabLayoutPanel#getAnimationDuration()}.
   */
  int getAnimationDuration();

  /**
   * See {@link TabLayoutPanel#getSelectedIndex()}.
   */
  int getSelectedIndex();

  /**
   * See {@link TabLayoutPanel#getTabIsWidget(int)}.
   */
  IsWidget.Extended getTabIsWidget(int index);

  /**
   * See {@link TabLayoutPanel#getTabIsWidget(IsWidget)}.
   */
  IsWidget.Extended getTabIsWidget(IsWidget child);

  /**
   * See {@link TabLayoutPanel#insert(IsWidget, int)}.
   */
  void insert(IsWidget child, int beforeIndex);

  /**
   * See {@link TabLayoutPanel#insert(IsWidget, java.lang.String, boolean, int)}.
   */
  void insert(IsWidget child, String tabText, boolean tabIsHtml, int beforeIndex);

  /**
   * See {@link TabLayoutPanel#insert(IsWidget, java.lang.String, int)}.
   */
  void insert(IsWidget child, String tabText, int beforeIndex);

  /**
   * See {@link TabLayoutPanel#insert(IsWidget, IsWidget, int)}.
   */
  void insert(IsWidget child, IsWidget tabWidget, int beforeIndex);

  /**
   * See {@link TabLayoutPanel#isAnimationVertical()}.
   */
  boolean isAnimationVertical();

  /**
   * See {@link TabLayoutPanel#selectTab(int)}.
   */
  void selectTab(int index);

  /**
   * See {@link TabLayoutPanel#selectTab(int, boolean)}.
   */
  void selectTab(int index, boolean fireEvents);

  /**
   * See {@link TabLayoutPanel#selectTab(IsWidget)}.
   */
  void selectTab(IsWidget child);

  /**
   * See {@link TabLayoutPanel#selectTab(IsWidget, boolean)}.
   */
  void selectTab(IsWidget child, boolean fireEvents);

  /**
   * See {@link TabLayoutPanel#setTabHTML(int, java.lang.String)}.
   */
  void setTabHTML(int index, String html);

  /**
   * See {@link TabLayoutPanel#setTabHTML(int, com.google.gwt.safehtml.shared.SafeHtml)}.
   */
  void setTabHTML(int index, SafeHtml html);

  /**
   * See {@link TabLayoutPanel#setTabText(int, java.lang.String)}.
   */
  void setTabText(int index, String text);

  /**
   * See {@link TabLayoutPanel#setAnimationDuration(int)}.
   */
  void setAnimationDuration(int duration);

  /**
   * See {@link TabLayoutPanel#setAnimationVertical(boolean)}.
   */
  void setAnimationVertical(boolean isVertical);
}
