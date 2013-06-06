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

import com.google.gwt.dom.client.IsElement;

/**
 * An interface for {@link HTMLPanel}.
 */
public interface IsHTMLPanel extends IsComplexPanel {

  /**
   * See {@link HTMLPanel#add(IsWidget, com.google.gwt.dom.client.IsElement)}.
   */
  void add(IsWidget widget, IsElement elem);

  /**
   * See {@link HTMLPanel#addAndReplaceElement(IsWidget, com.google.gwt.dom.client.IsElement)}.
   */
  void addAndReplaceElement(IsWidget widget, IsElement elem);

  /**
   * See {@link HTMLPanel#addAndReplaceElement(IsWidget, java.lang.String)}.
   */
  void addAndReplaceElement(IsWidget widget, String id);

}
