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

/**
 * An interface for {@link DockLayoutPanel}.
 */
public interface IsDockLayoutPanel extends IsComplexPanel, RequiresResize, ProvidesResize,
    AnimatedLayout {

  /**
   * See {@link DockLayoutPanel#add(IsWidget)}.
   */
  void add(IsWidget widget);

  /**
   * See {@link DockLayoutPanel#addEast(IsWidget, double)}.
   */
  void addEast(IsWidget widget, double size);

  /**
   * See {@link DockLayoutPanel#addLineEnd(IsWidget, double)}.
   */
  void addLineEnd(IsWidget widget, double size);

  /**
   * See {@link DockLayoutPanel#addLineStart(IsWidget, double)}.
   */
  void addLineStart(IsWidget widget, double size);

  /**
   * See {@link DockLayoutPanel#addNorth(IsWidget, double)}.
   */
  void addNorth(IsWidget widget, double size);

  /**
   * See {@link DockLayoutPanel#addSouth(IsWidget, double)}.
   */
  void addSouth(IsWidget widget, double size);

  /**
   * See {@link DockLayoutPanel#addWest(IsWidget, double)}.
   */
  void addWest(IsWidget widget, double size);

  /**
   * See {@link DockLayoutPanel#insertEast(IsWidget, double, IsWidget)}.
   */
  void insertEast(IsWidget widget, double size, IsWidget before);

  /**
   * See {@link DockLayoutPanel#insertLineEnd(IsWidget, double, IsWidget)}.
   */
  void insertLineEnd(IsWidget widget, double size, IsWidget before);

  /**
   * See {@link DockLayoutPanel#insertLineStart(IsWidget, double, IsWidget)}.
   */
  void insertLineStart(IsWidget widget, double size, IsWidget before);

  /**
   * See {@link DockLayoutPanel#insertNorth(IsWidget, double, IsWidget)}.
   */
  void insertNorth(IsWidget widget, double size, IsWidget before);

  /**
   * See {@link DockLayoutPanel#insertSouth(IsWidget, double, IsWidget)}.
   */
  void insertSouth(IsWidget widget, double size, IsWidget before);

  /**
   * See {@link DockLayoutPanel#insertWest(IsWidget, double, IsWidget)}.
   */
  void insertWest(IsWidget widget, double size, IsWidget before);
}
