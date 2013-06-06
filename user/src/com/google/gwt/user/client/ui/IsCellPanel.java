package com.google.gwt.user.client.ui;


import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.IsWidget;

public interface IsCellPanel extends IsComplexPanel {

  int getSpacing();

  void setBorderWidth(int width);

  void setSpacing(int spacing);

  void setCellHeight(IsWidget w, String height);

  void setCellHorizontalAlignment(IsWidget w, HorizontalAlignmentConstant align);

  void setCellVerticalAlignment(IsWidget w, VerticalAlignmentConstant align);

  void setCellWidth(IsWidget w, String width);

}
