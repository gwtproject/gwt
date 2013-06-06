package com.google.gwt.user.client.ui;

import com.google.gwt.user.client.ui.AnimatedLayout;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

public interface IsDockLayoutPanel extends IsComplexPanel, RequiresResize, ProvidesResize, AnimatedLayout {

  void add(IsWidget widget);

  void addEast(IsWidget widget, double size);

  void addLineEnd(IsWidget widget, double size);

  void addLineStart(IsWidget widget, double size);

  void addNorth(IsWidget widget, double size);

  void addSouth(IsWidget widget, double size);

  void addWest(IsWidget widget, double size);

  void insertEast(IsWidget widget, double size, IsWidget before);

  void insertLineEnd(IsWidget widget, double size, IsWidget before);

  void insertLineStart(IsWidget widget, double size, IsWidget before);

  void insertNorth(IsWidget widget, double size, IsWidget before);

  void insertSouth(IsWidget widget, double size, IsWidget before);

  void insertWest(IsWidget widget, double size, IsWidget before);
}
