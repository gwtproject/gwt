package com.google.gwt.user.client.ui;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasHTML;

public interface IsHyperlink extends IsWidget2, HasHTML, HasClickHandlers {

  String getTargetHistoryToken();

  void setTargetHistoryToken(String targetHistoryToken);

}
