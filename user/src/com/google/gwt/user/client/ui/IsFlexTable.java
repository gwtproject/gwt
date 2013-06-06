/*
 * Copyright 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.user.client.ui;

/**
 * An interface for {@link FlexTable}.
 */
public interface IsFlexTable extends IsHTMLTable {

  /**
   * See {@link FlexTable#addCell(int)}.
   */
  void addCell(int row);

  /**
   * See {@link FlexTable#insertCell(int, int)}.
   */
  void insertCell(int beforeRow, int beforeColumn);

  /**
   * See {@link FlexTable#insertRow(int)}.
   */
  int insertRow(int beforeRow);

  /**
   * See {@link FlexTable#removeAllRows()}.
   */
  void removeAllRows();

  /**
   * See {@link FlexTable#removeCell(int, int)}.
   */
  void removeCell(int row, int col);

  /**
   * See {@link FlexTable#removeCells(int, int, int)}.
   */
  void removeCells(int row, int column, int num);

  /**
   * See {@link FlexTable#removeRow(int)}.
   */
  void removeRow(int row);


}
