package com.google.gwt.user.client.ui;


import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasErrorHandlers;
import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;

public interface IsImage extends IsWidget2, HasLoadHandlers, HasErrorHandlers, HasClickHandlers, HasAllMouseHandlers {

  String getUrl();

  void setUrl(String url);

  void setUrl(SafeUri url);

  void setResource(ImageResource imageResource);

}
