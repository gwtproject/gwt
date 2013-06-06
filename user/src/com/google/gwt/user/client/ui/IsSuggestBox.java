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
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

/**
 * An interface for {@link SuggestBox}.
 */
@SuppressWarnings("deprecation")
public interface IsSuggestBox extends IsWidget.Extended, HasText, HasFocus, HasAnimation, HasEnabled,
    SourcesClickEvents, SourcesChangeEvents, SourcesKeyboardEvents, FiresSuggestionEvents,
    HasAllKeyHandlers, HasValue<String>, HasSelectionHandlers<Suggestion>,
    IsEditor<LeafValueEditor<String>> {

  /**
   * See {@link SuggestBox#showSuggestionList()}.
   */
  void showSuggestionList();

  /**
   * See {@link SuggestBox#getSuggestionDisplay()}.
   */
  SuggestionDisplay getSuggestionDisplay();

  /**
   * See {@link SuggestBox#getSuggestOracle()}.
   */
  SuggestOracle getSuggestOracle();

  /**
   * See {@link SuggestBox#setAutoSelectEnabled(boolean)}.
   */
  void setAutoSelectEnabled(boolean selectsFirstItem);

  /**
   * See {@link SuggestBox#setLimit(int)}.
   */
  void setLimit(int limit);

  /**
   * See {@link SuggestBox#getLimit()}.
   */
  int getLimit();

}
