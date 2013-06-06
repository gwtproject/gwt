package com.google.gwt.dom.client;

/**
 * A characteristic interface for types that can have their style changed.
 *
 * This interface is meant to implemented by both {@link Element} and Widget so
 * that common UI logic (like "hide" or "show") can work against both elements
 * and widgets.
 */
public interface HasStyle {

  void addStyleName(String styleName);

  void removeStyleName(String styleName);

  void setStyleName(String styleName);

  String getStyleName();

  IsStyle getIsStyle();

}
