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

@SuppressWarnings("deprecation")
public interface IsListBox extends IsFocusWidget, SourcesChangeEvents, HasChangeHandlers, HasName,
    HasDirectionEstimator {

  void addItem(String item);

  void addItem(String item, String value);

  void clear();

  int getItemCount();

  String getItemText(int index);

  int getSelectedIndex();

  String getValue(int index);

  int getVisibleItemCount();

  void insertItem(String item, int index);

  void insertItem(String item, String value, int index);

  boolean isItemSelected(int index);

  boolean isMultipleSelect();

  void removeItem(int index);

  void setItemSelected(int index, boolean selected);

  void setItemText(int index, String text);

  void setName(String name);

  void setSelectedIndex(int index);

  void setValue(int index, String value);

  void setVisibleItemCount(int visibleItems);
}
