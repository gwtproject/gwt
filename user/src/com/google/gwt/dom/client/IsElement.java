package com.google.gwt.dom.client;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HasText;

/** An interface for {@link Element}. */
public interface IsElement extends HasStyle, HasText {

  String getId();

  void setId(String id);

  int getOffsetWidth();

  int getOffsetHeight();

  int getClientHeight();

  int getClientWidth();

  int getScrollHeight();

  int getScrollWidth();

  int getScrollTop();

  int getScrollLeft();

  void setScrollTop(int scrollTop);

  void setScrollLeft(int scrollLeft);

  String getAttribute(String name);

  void setAttribute(String name, String value);

  String getInnerText();

  void setInnerText(String text);

  String getInnerHTML();

  void setInnerHTML(String html);

  void setInnerSafeHtml(SafeHtml html);

  void appendChild(IsElement element);

  void removeFromParent();

  Element asElement();

}
