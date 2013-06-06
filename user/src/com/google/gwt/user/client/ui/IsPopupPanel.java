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
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

/**
 * An interface for {@link PopupPanel}.
 */
public interface IsPopupPanel extends IsSimplePanel, HasAnimation, HasCloseHandlers<PopupPanel> {

  /**
   * See {@link PopupPanel#setPopupPosition(int, int)}.
   */
  void setPopupPosition(int left, int top);

  /**
   * See {@link PopupPanel#setPopupPositionAndShow(com.google.gwt.user.client.ui.PopupPanel.PositionCallback)}.
   */
  void setPopupPositionAndShow(PositionCallback callback);

  /**
   * See {@link PopupPanel#center()}.
   */
  void center();

  /**
   * See {@link PopupPanel#show()}.
   */
  void show();

  /**
   * See {@link PopupPanel#hide()}.
   */
  void hide();

  /**
   * See {@link PopupPanel#setGlassEnabled(boolean)}.
   */
  void setGlassEnabled(boolean enabled);

  /**
   * See {@link PopupPanel#setGlassStyleName(java.lang.String)}.
   */
  void setGlassStyleName(String styleName);

  /**
   * See {@link PopupPanel#isShowing()}.
   */
  boolean isShowing();

  /**
   * See {@link PopupPanel#addAutoHidePartner(com.google.gwt.dom.client.IsElement)}.
   */
  void addAutoHidePartner(IsElement element);

  /**
   * See {@link PopupPanel#isAutoHideEnabled()}.
   */
  boolean isAutoHideEnabled();

  /**
   * See {@link PopupPanel#setAutoHideEnabled(boolean)}.
   */
  void setAutoHideEnabled(boolean autoHide);

  /**
   * See {@link PopupPanel#isAutoHideOnHistoryEventsEnabled()}.
   */
  boolean isAutoHideOnHistoryEventsEnabled();

  /**
   * See {@link PopupPanel#setAutoHideOnHistoryEventsEnabled(boolean)}.
   */
  void setAutoHideOnHistoryEventsEnabled(boolean enabled);

  /**
   * See {@link PopupPanel#isModal()}.
   */
  boolean isModal();

  /**
   * See {@link PopupPanel#setModal(boolean)}.
   */
  void setModal(boolean modal);

}
