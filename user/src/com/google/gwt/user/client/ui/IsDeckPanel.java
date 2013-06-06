/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.user.client.ui;

/**
 * An interface for {@link DeckPanel}.
 */
public interface IsDeckPanel extends IsComplexPanel, HasAnimation, InsertPanel.ForIsWidget {

  /**
   * See {@link DeckPanel#getVisibleWidget()}.
   */
  int getVisibleWidget();

  /**
   * See {@link DeckPanel#insert(IsWidget, int)}.
   */
  void insert(IsWidget w, int beforeIndex);

  /**
   * See {@link DeckPanel#isAnimationEnabled()}.
   */
  boolean isAnimationEnabled();

  /**
   * See {@link DeckPanel#setAnimationEnabled(boolean)}.
   */
  void setAnimationEnabled(boolean enabled);

  /**
   * See {@link DeckPanel#showWidget(int)}.
   */
  void showWidget(int index);

}
