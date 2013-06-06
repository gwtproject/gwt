package com.google.gwt.user.client.ui;

import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.i18n.shared.HasDirectionEstimator;

@SuppressWarnings("deprecation")
public interface IsListBox extends IsFocusWidget, SourcesChangeEvents, HasChangeHandlers, HasName, HasDirectionEstimator {

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
