package com.google.gwt.user.client.ui;

import com.google.gwt.event.dom.client.HasLoadHandlers;


public interface IsFrame extends IsWidget2, HasLoadHandlers {

  String getUrl();

  void setUrl(String url);

  // Should be in UIObject
  void setSize(String width, String height);

}
