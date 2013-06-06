package com.google.gwt.user.client.ui;

import com.google.gwt.dom.client.IsElement;

import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.PopupPanel;
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
