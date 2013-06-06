package com.google.gwt.user.client.ui;


import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public interface IsSuggestBox extends IsWidget2, HasText, HasAnimation, HasValue<String>, HasAllKeyHandlers, HasSelectionHandlers<Suggestion> {

  void showSuggestionList();

  SuggestionDisplay getSuggestionDisplay();

  SuggestOracle getSuggestOracle();

  void setAutoSelectEnabled(boolean selectsFirstItem);

  void setLimit(int limit);

  int getLimit();

}
