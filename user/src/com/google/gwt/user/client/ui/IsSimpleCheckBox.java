package com.google.gwt.user.client.ui;

import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasValue;

public interface IsSimpleCheckBox extends IsFocusWidget, HasName, HasValue<Boolean> {

  String getFormValue();

  void setFormValue(String value);

}
