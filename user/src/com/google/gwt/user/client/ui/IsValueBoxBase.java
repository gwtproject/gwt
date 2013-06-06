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

import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.ui.client.adapters.ValueBoxEditor;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.i18n.client.AutoDirectionHandler;
import com.google.gwt.i18n.shared.HasDirectionEstimator;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;

/**
 * An interface for {@link ValueBoxBase}.
 */
public interface IsValueBoxBase<T> extends IsFocusWidget, HasChangeHandlers, HasName,
    HasDirectionEstimator, AutoDirectionHandler.Target, HasValue<T>, IsEditor<ValueBoxEditor<T>> {

  /**
   * See {@link ValueBoxBase#getSelectedText()}.
   */
  String getSelectedText();

  /**
   * See {@link ValueBoxBase#getSelectionLength()}.
   */
  int getSelectionLength();

  /**
   * See {@link ValueBoxBase#isReadOnly()}.
   */
  boolean isReadOnly();

  /**
   * See {@link ValueBoxBase#setCursorPos(int)}.
   */
  void setCursorPos(int pos);

  /**
   * See {@link ValueBoxBase#setReadOnly(boolean)}.
   */
  void setReadOnly(boolean readOnly);

  /**
   * See {@link ValueBoxBase#setSelectionRange(int, int)}.
   */
  void setSelectionRange(int pos, int length);

  /**
   * See {@link ValueBoxBase#setAlignment(com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment)}.
   */
  void setAlignment(TextAlignment align);
}
