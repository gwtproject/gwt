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

import com.google.gwt.event.dom.client.HasAllDragAndDropHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * An interface for {@link HTMLTable}.
 */
@SuppressWarnings("deprecation")
public interface IsHTMLTable extends IsPanel, SourcesTableEvents, HasAllDragAndDropHandlers,
    HasClickHandlers, HasDoubleClickHandlers {

  /**
   * See {@link HTMLTable#clear(boolean)}.
   */
  void clear(boolean clearInnerHTML);

  /**
   * See {@link HTMLTable#clearCell(int, int)}.
   */
  boolean clearCell(int row, int column);

  /**
   * See {@link HTMLTable#getCellCount(int)}.
   */
  int getCellCount(int row);

  /**
   * See {@link HTMLTable#getCellPadding()}.
   */
  int getCellPadding();

  /**
   * See {@link HTMLTable#getCellSpacing()}.
   */
  int getCellSpacing();

  /**
   * See {@link HTMLTable#getHTML(int, int)}.
   */
  String getHTML(int row, int column);

  /**
   * See {@link HTMLTable#getRowCount()}.
   */
  int getRowCount();

  /**
   * See {@link HTMLTable#getText(int, int)}.
   */
  String getText(int row, int column);

  /**
   * See {@link HTMLTable#getWidget(int, int)}.
   */
  IsWidget getWidget(int row, int column);

  /**
   * See {@link HTMLTable#isCellPresent(int, int)}.
   */
  boolean isCellPresent(int row, int column);

  /**
   * See {@link HTMLTable#setBorderWidth(int)}.
   */
  void setBorderWidth(int width);

  /**
   * See {@link HTMLTable#setCellPadding(int)}.
   */
  void setCellPadding(int padding);

  /**
   * See {@link HTMLTable#setCellSpacing(int)}.
   */
  void setCellSpacing(int spacing);

  /**
   * See {@link HTMLTable#setHTML(int, int, java.lang.String)}.
   */
  void setHTML(int row, int column, String html);

  /**
   * See {@link HTMLTable#setHTML(int, int, com.google.gwt.safehtml.shared.SafeHtml)}.
   */
  void setHTML(int row, int column, SafeHtml html);

  /**
   * See {@link HTMLTable#setText(int, int, java.lang.String)}.
   */
  void setText(int row, int column, String text);

  /**
   * See {@link HTMLTable#setWidget(int, int, IsWidget)}.
   */
  void setWidget(int row, int column, IsWidget widget);

}
