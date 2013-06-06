/*
 * Copyright 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.user.client.ui;

import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

/**
 * An interface for {@link CellPanel}.
 */
public interface IsCellPanel extends IsComplexPanel {

  /**
   * See {@link CellPanel#getSpacing()}.
   */
  int getSpacing();

  /**
   * See {@link CellPanel#setBorderWidth(int)}.
   */
  void setBorderWidth(int width);

  /**
   * See {@link CellPanel#setSpacing(int)}.
   */
  void setSpacing(int spacing);

  /**
   * See {@link CellPanel#setCellHeight(IsWidget, java.lang.String)}.
   */
  void setCellHeight(IsWidget w, String height);

  /**
   * See {@link CellPanel#setCellHorizontalAlignment(IsWidget, com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant)}.
   */
  void setCellHorizontalAlignment(IsWidget w, HorizontalAlignmentConstant align);

  /**
   * See {@link CellPanel#setCellVerticalAlignment(IsWidget, com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant)}.
   */
  void setCellVerticalAlignment(IsWidget w, VerticalAlignmentConstant align);

  /**
   * See {@link CellPanel#setCellWidth(IsWidget, java.lang.String)}.
   */
  void setCellWidth(IsWidget w, String width);

}
