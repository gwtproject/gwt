package com.google.gwt.user.client.ui;

import com.google.gwt.i18n.client.HasDirection;

public interface IsTextArea extends IsTextBoxBase, HasDirection {

  int getCharacterWidth();

  void setCharacterWidth(int characterWidth);

  int getVisibleLines();

  void setVisibleLines(int visibleLines);

}
