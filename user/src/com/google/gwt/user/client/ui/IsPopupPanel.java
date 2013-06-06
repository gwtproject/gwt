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

public interface IsPopupPanel extends IsSimplePanel, HasAnimation, HasCloseHandlers<PopupPanel> {

  void setPopupPosition(int left, int top);

  void setPopupPositionAndShow(PositionCallback callback);

  void center();

  void show();

  void hide();

  void setGlassEnabled(boolean enabled);

  void setGlassStyleName(String styleName);

  boolean isShowing();

  void addAutoHidePartner(IsElement element);

  boolean isAutoHideEnabled();

  void setAutoHideEnabled(boolean autoHide);

  boolean isAutoHideOnHistoryEventsEnabled();

  void setAutoHideOnHistoryEventsEnabled(boolean enabled);

  boolean isModal();

  void setModal(boolean modal);

}
