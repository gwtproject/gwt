/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.user.client.ui;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.GestureChangeHandler;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.HasAllDragAndDropHandlers;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllGestureHandlers;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * A {@link Composite} that wraps a {@link Focusable} widget.
 *
 * @param <T> type of the widget wrapped
 */
public class FocusComposite<T extends FocusWidget> extends Composite<T>
    implements Focusable, HasEnabled, HasAllFocusHandlers, HasAllKeyHandlers, HasClickHandlers,
    HasDoubleClickHandlers, HasAllMouseHandlers, HasAllDragAndDropHandlers, HasAllGestureHandlers,
    HasAllTouchHandlers {

  @Override
  public int getTabIndex() {
    return getDelegate().getTabIndex();
  }

  @Override
  public void setAccessKey(char key) {
    getDelegate().setAccessKey(key);
  }

  @Override
  public void setFocus(boolean focused) {
    getDelegate().setFocus(focused);
  }

  @Override
  public void setTabIndex(int index) {
    getDelegate().setTabIndex(index);
  }

  @Override
  public HandlerRegistration addFocusHandler(FocusHandler handler) {
    return getDelegate().addFocusHandler(handler);
  }

  @Override
  public HandlerRegistration addBlurHandler(BlurHandler handler) {
    return getDelegate().addBlurHandler(handler);
  }

  @Override
  public void setEnabled(boolean enabled) {
    getDelegate().setEnabled(enabled);
  }

  @Override
  public boolean isEnabled() {
    return getDelegate().isEnabled();
  }

  @Override
  public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
    return getDelegate().addKeyDownHandler(handler);
  }

  @Override
  public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
    return getDelegate().addKeyPressHandler(handler);
  }

  @Override
  public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
    return getDelegate().addKeyUpHandler(handler);
  }

  @Override
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return getDelegate().addClickHandler(handler);
  }

  @Override
  public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
    return getDelegate().addDoubleClickHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
    return getDelegate().addMouseDownHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
    return getDelegate().addMouseMoveHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
    return getDelegate().addMouseOutHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
    return getDelegate().addMouseOverHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
    return getDelegate().addMouseUpHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
    return getDelegate().addMouseWheelHandler(handler);
  }

  @Override
  public HandlerRegistration addDragHandler(DragHandler handler) {
    return getDelegate().addDragHandler(handler);
  }

  @Override
  public HandlerRegistration addDropHandler(DropHandler handler) {
    return getDelegate().addDropHandler(handler);
  }

  @Override
  public HandlerRegistration addDragStartHandler(DragStartHandler handler) {
    return getDelegate().addDragStartHandler(handler);
  }

  @Override
  public HandlerRegistration addDragEndHandler(DragEndHandler handler) {
    return getDelegate().addDragEndHandler(handler);
  }

  @Override
  public HandlerRegistration addDragEnterHandler(DragEnterHandler handler) {
    return getDelegate().addDragEnterHandler(handler);
  }

  @Override
  public HandlerRegistration addDragLeaveHandler(DragLeaveHandler handler) {
    return getDelegate().addDragLeaveHandler(handler);
  }

  @Override
  public HandlerRegistration addDragOverHandler(DragOverHandler handler) {
    return getDelegate().addDragOverHandler(handler);
  }

  @Override
  public HandlerRegistration addGestureStartHandler(GestureStartHandler handler) {
    return getDelegate().addGestureStartHandler(handler);
  }

  @Override
  public HandlerRegistration addGestureChangeHandler(GestureChangeHandler handler) {
    return getDelegate().addGestureChangeHandler(handler);
  }

  @Override
  public HandlerRegistration addGestureEndHandler(GestureEndHandler handler) {
    return getDelegate().addGestureEndHandler(handler);
  }

  @Override
  public HandlerRegistration addTouchCancelHandler(TouchCancelHandler handler) {
    return getDelegate().addTouchCancelHandler(handler);
  }

  @Override
  public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
    return getDelegate().addTouchEndHandler(handler);
  }

  @Override
  public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler) {
    return getDelegate().addTouchMoveHandler(handler);
  }

  @Override
  public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
    return getDelegate().addTouchStartHandler(handler);
  }
}
