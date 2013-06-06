package com.google.gwt.user.client.ui;

import com.google.gwt.event.dom.client.HasAllDragAndDropHandlers;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllGestureHandlers;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;

@SuppressWarnings("deprecation")
public interface IsFocusPanel extends IsSimplePanel, HasFocus, SourcesClickEvents,
    SourcesMouseEvents, SourcesMouseWheelEvents, HasAllDragAndDropHandlers, HasAllMouseHandlers,
    HasClickHandlers, HasDoubleClickHandlers, HasAllKeyHandlers, HasAllFocusHandlers,
    HasAllGestureHandlers, HasAllTouchHandlers {

}
