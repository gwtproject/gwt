/*
 * Copyright 2008 Google Inc.
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

package com.google.gwt.museum.client.common;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Helper class to create visual tests.
 * 
 * @param <V> value type
 * @param <T> target type
 */
public class EventReporter<V, T> extends SimpleLogger implements
    ValueChangeHandler<V>, SelectionHandler<Suggestion>,
    ChangeHandler, BlurHandler, FocusHandler, ClickHandler,
    CloseHandler<T>, KeyDownHandler, KeyUpHandler, KeyPressHandler,
    MouseDownHandler, MouseUpHandler, MouseOutHandler, MouseOverHandler,
    MouseMoveHandler, MouseWheelHandler {

  /**
   * Add/remove handlers via check box.
   * 
   */
  public abstract class CheckBoxEvent extends CheckBox implements
      ValueChangeHandler<Boolean> {
    String name;

    private HandlerRegistration registration;

    public CheckBoxEvent(String name, HasWidgets p) {
      this.name = name;
      this.setText(name);
      p.add(this);
      this.addValueChangeHandler(this);
      this.setValue(true, true);
    }

    public abstract HandlerRegistration addHandler();

    public void onValueChange(ValueChangeEvent<Boolean> event) {
      if (event.getValue().booleanValue()) {
        report("add " + name);
        registration = addHandler();
      } else {
        report("remove " + name);
        removeHandler();
      }
    }

    public void removeHandler() {
      registration.removeHandler();
      registration = null;
    }
  }

  public EventReporter() {
  }

  public EventReporter(HasWidgets parent) {
    parent.add(this);
  }

  public void addClickHandler(final HasClickHandlers h, HasWidgets p) {
    addClickHandler(h, p, getInfo(h) + " click handler");
  }

  public void addClickHandler(final HasClickHandlers h, HasWidgets p,
      String title) {

    new CheckBoxEvent(title, p) {
      @Override
      public HandlerRegistration addHandler() {
        return h.addClickHandler(EventReporter.this);
      }
    };
  }

  @Override
  public String getInfo(Object sender) {
    if (sender instanceof HasText) {
      return ((HasText) sender).getText();
    } else if (sender instanceof UIObject
        && ((UIObject) sender).getTitle() != null) {
      return ((UIObject) sender).getTitle();
    } else if (sender instanceof HasHTML) {
      return ((HasHTML) sender).getHTML();
    } else {
      return sender.toString();
    }
  }

  public void onBlur(BlurEvent event) {
    report(event);
  }

  public void onChange(ChangeEvent event) {
    report(event);
  }

  public void onClick(ClickEvent event) {
    report(event);
  }

  public void onClose(CloseEvent<T> event) {
    report("close " + getInfo(event.getTarget()));
  }

  public void onFocus(FocusEvent event) {
    report(event);
  }

  public void onKeyDown(KeyDownEvent event) {
    report(event);
  }

  public void onKeyPress(KeyPressEvent event) {
    report(event);
  }

  public void onKeyUp(KeyUpEvent event) {
    report(event);
  }

  @Override
  public void onMouseDown(MouseDownEvent event) {
    report(event);
  }

  @Override
  public void onMouseMove(MouseMoveEvent event) {
    report(event);
  }

  @Override
  public void onMouseOut(MouseOutEvent event) {
    report(event);
  }

  @Override
  public void onMouseOver(MouseOverEvent event) {
    report(event);
  }

  @Override
  public void onMouseUp(MouseUpEvent event) {
    report(event);
  }

  @Override
  public void onMouseWheel(MouseWheelEvent event) {
    report(event);
  }

  public void onSelection(SelectionEvent<Suggestion> event) {
    report(event);
  }

  public void onValueChange(ValueChangeEvent<V> event) {
    report(event);
  }
}
