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

import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.i18n.shared.HasDirectionEstimator;

/**
 * An interface for {@link ListBox}.
 */
@SuppressWarnings("deprecation")
public interface IsListBox extends IsFocusWidget, SourcesChangeEvents, HasChangeHandlers, HasName,
    HasDirectionEstimator {

  /**
   * See {@link ListBox#addItem(java.lang.String)}.
   */
  void addItem(String item);

  /**
   * See {@link ListBox#addItem(java.lang.String, java.lang.String)}.
   */
  void addItem(String item, String value);

  /**
   * See {@link ListBox#clear()}.
   */
  void clear();

  /**
   * See {@link ListBox#getItemCount()}.
   */
  int getItemCount();

  /**
   * See {@link ListBox#getItemText(int)}.
   */
  String getItemText(int index);

  /**
   * See {@link ListBox#getSelectedIndex()}.
   */
  int getSelectedIndex();

  /**
   * See {@link ListBox#getValue(int)}.
   */
  String getValue(int index);

  /**
   * See {@link ListBox#getVisibleItemCount()}.
   */
  int getVisibleItemCount();

  /**
   * See {@link ListBox#insertItem(java.lang.String, int)}.
   */
  void insertItem(String item, int index);

  /**
   * See {@link ListBox#insertItem(java.lang.String, java.lang.String, int)}.
   */
  void insertItem(String item, String value, int index);

  /**
   * See {@link ListBox#isItemSelected(int)}.
   */
  boolean isItemSelected(int index);

  /**
   * See {@link ListBox#isMultipleSelect()}.
   */
  boolean isMultipleSelect();

  /**
   * See {@link ListBox#removeItem(int)}.
   */
  void removeItem(int index);

  /**
   * See {@link ListBox#setItemSelected(int, boolean)}.
   */
  void setItemSelected(int index, boolean selected);

  /**
   * See {@link ListBox#setItemText(int, java.lang.String)}.
   */
  void setItemText(int index, String text);

  /**
   * See {@link ListBox#setName(java.lang.String)}.
   */
  void setName(String name);

  /**
   * See {@link ListBox#setSelectedIndex(int)}.
   */
  void setSelectedIndex(int index);

  /**
   * See {@link ListBox#setValue(int, java.lang.String)}.
   */
  void setValue(int index, String value);

  /**
   * See {@link ListBox#setVisibleItemCount(int)}.
   */
  void setVisibleItemCount(int visibleItems);
}
