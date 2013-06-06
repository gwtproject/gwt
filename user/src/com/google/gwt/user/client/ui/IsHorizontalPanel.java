package com.google.gwt.user.client.ui;

public interface IsHorizontalPanel extends IsCellPanel, HasAlignment, InsertPanel.ForIsWidget {

  void insert(IsWidget w, int beforeIndex);

}
