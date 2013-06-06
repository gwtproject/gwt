package com.google.gwt.user.client.ui;

import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

public interface IsScrollPanel extends IsSimplePanel, HasScrollHandlers, RequiresResize, ProvidesResize {

  int getHorizontalScrollPosition();

  void setHorizontalScrollPosition(int position);

  int getScrollPosition();

  void setScrollPosition(int position);

  void setAlwaysShowScrollBars(boolean alwaysShow);

  void scrollToTop();

  void scrollToRight();

  void scrollToLeft();

  void scrollToBottom();

  // should be in UIObject
  void setWidth(String width);

  // should be in UIObject
  void setHeight(String height);

}
