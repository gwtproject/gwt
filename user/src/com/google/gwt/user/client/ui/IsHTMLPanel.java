package com.google.gwt.user.client.ui;

import com.google.gwt.dom.client.IsElement;

public interface IsHTMLPanel extends IsComplexPanel {

  void add(IsWidget widget, IsElement elem);

  void addAndReplaceElement(IsWidget widget, IsElement elem);

  void addAndReplaceElement(IsWidget widget, String id);

}
