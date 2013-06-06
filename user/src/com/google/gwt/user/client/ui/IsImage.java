package com.google.gwt.user.client.ui;

import com.google.gwt.event.dom.client.HasAllDragAndDropHandlers;
import com.google.gwt.event.dom.client.HasAllGestureHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.HasErrorHandlers;
import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;

@SuppressWarnings("deprecation")
public interface IsImage extends IsWidget2, SourcesLoadEvents, HasLoadHandlers, HasErrorHandlers,
    SourcesClickEvents, HasClickHandlers, HasDoubleClickHandlers, HasAllDragAndDropHandlers,
    HasAllGestureHandlers, HasAllMouseHandlers, HasAllTouchHandlers, SourcesMouseEvents {

  String getUrl();

  void setUrl(String url);

  void setUrl(SafeUri url);

  void setResource(ImageResource imageResource);

}
