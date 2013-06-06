package com.google.gwt.user.client.ui;


import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.i18n.shared.HasDirectionEstimator;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;

public interface IsValueBoxBase<T> extends IsFocusWidget, HasChangeHandlers, HasDirectionEstimator, HasText, HasName, HasValue<T> {

  String getSelectedText();

  int getSelectionLength();

  boolean isReadOnly();

  void setCursorPos(int pos);

  void setReadOnly(boolean readOnly);

  void setSelectionRange(int pos, int length);

  void setAlignment(TextAlignment align);
}
