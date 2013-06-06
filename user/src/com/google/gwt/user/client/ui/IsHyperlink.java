package com.google.gwt.user.client.ui;


import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.shared.HasDirectionEstimator;

@SuppressWarnings("deprecation")
public interface IsHyperlink extends IsWidget2, HasHTML, SourcesClickEvents, HasClickHandlers, HasDirectionEstimator, HasDirectionalSafeHtml {

  String getTargetHistoryToken();

  void setTargetHistoryToken(String targetHistoryToken);

}
