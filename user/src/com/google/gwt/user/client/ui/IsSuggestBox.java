package com.google.gwt.user.client.ui;

import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

@SuppressWarnings("deprecation")
public interface IsSuggestBox extends IsWidget2, HasText, HasFocus, HasAnimation, HasEnabled,
    SourcesClickEvents, SourcesChangeEvents, SourcesKeyboardEvents, FiresSuggestionEvents,
    HasAllKeyHandlers, HasValue<String>, HasSelectionHandlers<Suggestion>,
    IsEditor<LeafValueEditor<String>> {

  void showSuggestionList();

  SuggestionDisplay getSuggestionDisplay();

  SuggestOracle getSuggestOracle();

  void setAutoSelectEnabled(boolean selectsFirstItem);

  void setLimit(int limit);

  int getLimit();

}
