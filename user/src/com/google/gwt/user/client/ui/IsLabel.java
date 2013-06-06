package com.google.gwt.user.client.ui;

import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.dom.client.HasAllDragAndDropHandlers;
import com.google.gwt.event.dom.client.HasAllGestureHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.i18n.client.HasDirection;

@SuppressWarnings("deprecation")
public interface IsLabel extends IsWidget2, HasDirectionalText, HasDirection, HasClickHandlers,
    HasDoubleClickHandlers, SourcesClickEvents, SourcesMouseEvents, HasAllDragAndDropHandlers,
    HasAllGestureHandlers, HasAllMouseHandlers, HasAllTouchHandlers,
    IsEditor<LeafValueEditor<String>> {

}
