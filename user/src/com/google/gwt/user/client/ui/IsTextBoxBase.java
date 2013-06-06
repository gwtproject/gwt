package com.google.gwt.user.client.ui;

import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.TextBoxBase.TextAlignConstant;

@SuppressWarnings("deprecation")
public interface IsTextBoxBase extends IsValueBoxBase<String> {

  @Deprecated
  void setTextAlignment(TextAlignConstant align);

  TextBoxBase asWidget();

}
