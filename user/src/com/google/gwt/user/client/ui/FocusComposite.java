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
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.GestureChangeHandler;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllGestureHandlers;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
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
 * A {@link Composite} that wraps {@link Focusable} widgets.
 *
 * @param <T> type of the widget wrapped
 */
public abstract class FocusComposite<T extends FocusWidget> extends TypedComposite<T>
    implements Focusable, HasAllFocusHandlers, HasAllKeyHandlers, HasClickHandlers,
    HasAllMouseHandlers, HasAllGestureHandlers, HasAllTouchHandlers {

  @Override
  public int getTabIndex() {
    return getCheckedWidget().getTabIndex();
  }

  @Override
  public void setAccessKey(char key) {
    getCheckedWidget().setAccessKey(key);
  }

  @Override
  public void setFocus(boolean focused) {
    getCheckedWidget().setFocus(focused);
  }

  @Override
  public void setTabIndex(int index) {
    getCheckedWidget().setTabIndex(index);
  }

  @Override
  public HandlerRegistration addFocusHandler(FocusHandler handler) {
    return getCheckedWidget().addFocusHandler(handler);
  }

  @Override
  public HandlerRegistration addBlurHandler(BlurHandler handler) {
    return getCheckedWidget().addBlurHandler(handler);
  }

  @Override
  public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
    return getCheckedWidget().addKeyDownHandler(handler);
  }

  @Override
  public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
    return getCheckedWidget().addKeyPressHandler(handler);
  }

  @Override
  public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
    return getCheckedWidget().addKeyUpHandler(handler);
  }

  @Override
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    return getCheckedWidget().addClickHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
    return getCheckedWidget().addMouseDownHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
    return getCheckedWidget().addMouseMoveHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
    return getCheckedWidget().addMouseOutHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
    return getCheckedWidget().addMouseOverHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
    return getCheckedWidget().addMouseUpHandler(handler);
  }

  @Override
  public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
    return getCheckedWidget().addMouseWheelHandler(handler);
  }

  @Override
  public HandlerRegistration addGestureStartHandler(GestureStartHandler handler) {
    return getCheckedWidget().addGestureStartHandler(handler);
  }

  @Override
  public HandlerRegistration addGestureChangeHandler(GestureChangeHandler handler) {
    return getCheckedWidget().addGestureChangeHandler(handler);
  }

  @Override
  public HandlerRegistration addGestureEndHandler(GestureEndHandler handler) {
    return getCheckedWidget().addGestureEndHandler(handler);
  }

  @Override
  public HandlerRegistration addTouchCancelHandler(TouchCancelHandler handler) {
    return getCheckedWidget().addTouchCancelHandler(handler);
  }

  @Override
  public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
    return getCheckedWidget().addTouchEndHandler(handler);
  }

  @Override
  public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler) {
    return getCheckedWidget().addTouchMoveHandler(handler);
  }

  @Override
  public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
    return getCheckedWidget().addTouchStartHandler(handler);
  }
}
