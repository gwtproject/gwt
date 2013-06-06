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
 * An interface for {@link Grid}.
 */
public interface IsGrid extends IsHTMLTable {

  /**
   * See {@link Grid#getColumnCount()}.
   */
  int getColumnCount();

  /**
   * See {@link Grid#insertRow(int)}.
   */
  int insertRow(int beforeRow);

  /**
   * See {@link Grid#removeRow(int)}.
   */
  void removeRow(int row);

  /**
   * See {@link Grid#resize(int, int)}.
   */
  void resize(int rows, int columns);

  /**
   * See {@link Grid#resizeColumns(int)}.
   */
  void resizeColumns(int columns);

  /**
   * See {@link Grid#resizeRows(int)}.
   */
  void resizeRows(int rows);

}
