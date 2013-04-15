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
package com.google.gwt.uibinder.test.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasName;

/**
 * A Widget that has an event parametrized by a wildcard.
 * Note that parameterizing events by wildcards is not good practice.
 * 
 */
public class BadValueChangeWidget extends FocusWidget implements
    HasChangeHandlers, HasName {

  private boolean valueChangeHandlerInitialized;

  String myValue = "";

  HasValueChangeHandlers<String> sender;

  protected BadValueChangeWidget() {
    super(Document.get().createDivElement());
    sender = new HasValueChangeHandlers<String>() {
      @Override
      public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return BadValueChangeWidget.this.addHandler(handler, ValueChangeEvent.getType());
      }

      @Override
      public void fireEvent(GwtEvent<?> event) {
        BadValueChangeWidget.this.fireEvent(event);
      }
    };
  }

  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return addDomHandler(handler, ChangeEvent.getType());
  }

  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<?> handler) {
    // Initialization code
    if (!valueChangeHandlerInitialized) {
      valueChangeHandlerInitialized = true;
      addChangeHandler(new ChangeHandler() {
        public void onChange(ChangeEvent event) {
          ValueChangeEvent.fire(sender, myValue);
        }
      });
    }
    return addHandler(handler, ValueChangeEvent.getType());
  }

  public String getName() {
    return DOM.getElementProperty(getElement(), "name");
  }

  public String getValue() {
    return myValue;
  }

  public void setName(String name) {
    DOM.setElementProperty(getElement(), "name", name);
  }

  public void setValue(String value) {
    String oldValue = getValue();
    myValue = value;
    ValueChangeEvent.fireIfNotEqual(sender, oldValue, value);
  }

  @Override
  protected void onLoad() {
    super.onLoad();
  }
}
