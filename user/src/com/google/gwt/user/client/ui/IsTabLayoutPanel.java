package com.google.gwt.user.client.ui;

import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ProvidesResize;

public interface IsTabLayoutPanel extends IsWidget2, HasWidgets.ForIsWidget, IndexedPanel.ForIsWidget, ProvidesResize,
    AnimatedLayout, HasBeforeSelectionHandlers<Integer>,
    HasSelectionHandlers<Integer> {

  void add(IsWidget w, String tabText);

  void add(IsWidget w, String tabText, boolean tabIsHtml);

  void add(IsWidget w, com.google.gwt.user.client.ui.IsWidget tabWidget);

  HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<Integer> handler);

  HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler);

  int getSelectedIndex();

  IsWidget getTabIsWidget(int index);

  IsWidget getTabIsWidget(IsWidget child);

  void insert(IsWidget child, int beforeIndex);

  void insert(IsWidget child, String tabText, boolean tabIsHtml, int beforeIndex);

  void insert(IsWidget child, String tabText, int beforeIndex);

  void insert(IsWidget child, IsWidget tabWidget, int beforeIndex);

  void selectTab(int index);

  void selectTab(int index, boolean fireEvents);

  void selectTab(IsWidget child);

  void selectTab(IsWidget child, boolean fireEvents);

  void setTabHTML(int index, String html);

  void setTabText(int index, String text);

}
