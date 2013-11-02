/*
 * Copyright 2010 Google Inc.
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
 * Implemented by panels that have only one widget.
 * 
 * @see SimplePanel
 */
public interface HasOneWidget extends AcceptsOneWidget {

  /**
   * Extends HasOneWidget with methods for {@IsWidget.Extended}.
   */
  public interface ForExtendedIsWidget extends HasOneWidget {
    /**
     * @return the panel's child {@link IsWidget.Extended} widget.
     */
    IsWidget.Extended getIsWidget();
  }

  /**
   * Gets the panel's child widget.
   * 
   * @return the child widget, or <code>null</code> if none is present
   */
  Widget getWidget();

  /**
   * Sets this panel's widget. Any existing child widget will be removed.
   * 
   * @param w the panel's new widget, or <code>null</code> to clear the panel
   */
  void setWidget(Widget w);
}
