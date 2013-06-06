package com.google.gwt.user.client.ui;

import com.google.gwt.event.dom.client.HasAllDragAndDropHandlers;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllGestureHandlers;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasFocus;

@SuppressWarnings("deprecation")
public interface IsFocusWidget extends IsWidget2, HasClickHandlers, HasDoubleClickHandlers,
    HasAllFocusHandlers, HasAllKeyHandlers, HasAllMouseHandlers, HasEnabled, HasFocus,
    HasAllDragAndDropHandlers, HasAllGestureHandlers, HasAllTouchHandlers, SourcesMouseEvents {

}
