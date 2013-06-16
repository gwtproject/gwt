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

public interface IsValueBoxBase<T> extends IsFocusWidget, HasChangeHandlers, HasName,
    HasDirectionEstimator, AutoDirectionHandler.Target, HasValue<T>, IsEditor<ValueBoxEditor<T>> {

  String getSelectedText();

  int getSelectionLength();

  boolean isReadOnly();

  void setCursorPos(int pos);

  void setReadOnly(boolean readOnly);

  void setSelectionRange(int pos, int length);

  void setAlignment(TextAlignment align);
}
