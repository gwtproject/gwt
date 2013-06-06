package com.google.gwt.user.client.ui;

public interface IsVerticalPanel extends IsCellPanel, HasAlignment, InsertPanel.ForIsWidget {

  void insert(IsWidget w, int beforeIndex);

}
