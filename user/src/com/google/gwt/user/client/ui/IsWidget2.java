package com.google.gwt.user.client.ui;

import com.google.gwt.dom.client.HasStyle;
import com.google.gwt.dom.client.IsElement;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;

/**
 * Augments {@code IsWidget} with all of the methods from {@link Widget} itself.
 * 
 * This allows unit testing against widget-like instances that may actually be mocks or stubs.
 */
public interface IsWidget2 extends EventListener, HasHandlers, HasStyle, HasAttachHandlers,
    IsWidget {

  // HasCss addStyleName/removeStyleName is really from IsUIObject

  // really from IsUIObject
  int getAbsoluteTop();

  // really from IsUIObject
  int getAbsoluteLeft();

  // really from IsUIObject
  int getOffsetHeight();

  // really from IsUIObject
  int getOffsetWidth();

  void ensureDebugId(String id);

  boolean isAttached();

  IsElement getIsElement();

  <H extends EventHandler> HandlerRegistration addDomHandler(final H handler, DomEvent.Type<H> type);

}
