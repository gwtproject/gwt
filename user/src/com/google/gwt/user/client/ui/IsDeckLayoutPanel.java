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
 * An interface for {@link DeckLayoutPanel}.
 */
public interface IsDeckLayoutPanel extends IsComplexPanel, AnimatedLayout, RequiresResize,
    ProvidesResize, InsertPanel.ForIsWidget, AcceptsOneWidget {

  /**
   * See {@link DeckLayoutPanel#getAnimationDuration()}.
   */
  int getAnimationDuration();

  /**
   * See {@link DeckLayoutPanel#getVisibleIsWidget()}.
   */
  IsWidget.Extended getVisibleIsWidget();

  /**
   * See {@link DeckLayoutPanel#insert(IsWidget, IsWidget)}.
   */
  void insert(IsWidget widget, IsWidget before);

  /**
   * See {@link DeckLayoutPanel#isAnimationVertical()}.
   */
  boolean isAnimationVertical();

  /**
   * See {@link DeckLayoutPanel#setAnimationDuration(int)}.
   */
  void setAnimationDuration(int duration);

  /**
   * See {@link DeckLayoutPanel#setAnimationVertical(boolean)}.
   */
  void setAnimationVertical(boolean isVertical);

  /**
   * See {@link DeckLayoutPanel#showWidget(int)}.
   */
  void showWidget(int index);

  /**
   * See {@link DeckLayoutPanel#showWidget(IsWidget)}.
   */
  void showWidget(IsWidget w);

}
