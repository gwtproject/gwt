/*
 * Copyright 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
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

/**
 * An interface for {@link Image}.
 */
@SuppressWarnings("deprecation")
public interface IsImage extends IsWidget.Extended, SourcesLoadEvents, HasLoadHandlers, HasErrorHandlers,
    SourcesClickEvents, HasClickHandlers, HasDoubleClickHandlers, HasAllDragAndDropHandlers,
    HasAllGestureHandlers, HasAllMouseHandlers, HasAllTouchHandlers, SourcesMouseEvents {

  /**
   * See {@link Image#getUrl()}.
   */
  String getUrl();

  /**
   * See {@link Image#setUrl(java.lang.String)}.
   */
  void setUrl(String url);

  /**
   * See {@link Image#setUrl(com.google.gwt.safehtml.shared.SafeUri)}.
   */
  void setUrl(SafeUri url);

  /**
   * See {@link Image#setResource(com.google.gwt.resources.client.ImageResource)}.
   */
  void setResource(ImageResource imageResource);

}
