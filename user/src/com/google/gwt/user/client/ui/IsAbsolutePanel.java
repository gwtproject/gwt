package com.google.gwt.user.client.ui;

public interface IsAbsolutePanel extends IsComplexPanel, InsertPanel.ForIsWidget {

  void add(IsWidget w, int left, int top);

  int getIsWidgetLeft(IsWidget w);

  int getIsWidgetTop(IsWidget w);

  void insert(IsWidget w, int beforeIndex);

  void insert(IsWidget w, int left, int top, int beforeIndex);

  void setWidgetPosition(IsWidget w, int left, int top);

}
