package com.google.gwt.user.client.ui;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

public interface IsFormPanel extends IsSimplePanel {

  HandlerRegistration addSubmitCompleteHandler(SubmitCompleteHandler handler);

  HandlerRegistration addSubmitHandler(SubmitHandler handler);

  String getAction();

  String getEncoding();

  String getMethod();

  String getTarget();

  void reset();

  void setAction(String action);

  void setAction(SafeUri url);

  void setEncoding(String encoding);

  void setMethod(String method);

  void submit();

}
