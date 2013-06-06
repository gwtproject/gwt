package com.google.gwt.user.client.ui;

import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;

public interface IsSimpleCheckBox extends IsFocusWidget, HasName, HasValue<Boolean>,
    IsEditor<LeafValueEditor<Boolean>> {

  String getFormValue();

  void setFormValue(String value);

}
