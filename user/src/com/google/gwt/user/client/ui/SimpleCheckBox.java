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
package com.google.gwt.user.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;

/**
 * A simple checkbox widget, with no label.
 * 
 * <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-SimpleCheckBox { }</li>
 * <li>.gwt-SimpleCheckBox-disabled { Applied when checkbox is disabled }</li>
 * </ul>
 */
public class SimpleCheckBox extends Widget implements HasName,
    SourcesClickEvents, SourcesFocusEvents, HasFocus, SourcesKeyboardEvents {

  /**
   * Creates a SimpleCheckBox widget that wraps an existing &lt;input
   * type='checkbox'&gt; element.
   * 
   * This element must already be attached to the document.
   * 
   * @param element the element to be wrapped
   */
  public static SimpleCheckBox wrap(Element element) {
    // Assert that the element is of the correct type and is attached.
    assert InputElement.as(element).getType().equalsIgnoreCase("checkbox");
    assert Document.get().getBody().isOrHasChild(element);

    SimpleCheckBox checkBox = new SimpleCheckBox(element);

    // Mark it attached and remember it for cleanup.
    checkBox.onAttach();
    RootPanel.detachOnWindowClose(checkBox);

    return checkBox;
  }

  private ClickListenerCollection clickListeners;
  private FocusListenerCollection focusListeners;
  private KeyboardListenerCollection keyboardListeners;

  /**
   * Creates a new simple checkbox.
   */
  public SimpleCheckBox() {
    setElement(Document.get().createCheckInputElement());
    setStyleName("gwt-SimpleCheckBox");
  }

  SimpleCheckBox(Element element) {
    setElement(element);
  }

  public void addClickListener(ClickListener listener) {
    if (clickListeners == null) {
      clickListeners = new ClickListenerCollection();
      sinkEvents(Event.ONCLICK);
    }
    clickListeners.add(listener);
  }

  public void addFocusListener(FocusListener listener) {
    if (focusListeners == null) {
      focusListeners = new FocusListenerCollection();
      sinkEvents(Event.FOCUSEVENTS);
    }
    focusListeners.add(listener);
  }

  public void addKeyboardListener(KeyboardListener listener) {
    if (keyboardListeners == null) {
      keyboardListeners = new KeyboardListenerCollection();
      sinkEvents(Event.KEYEVENTS);
    }
    keyboardListeners.add(listener);
  }

  public String getName() {
    return getInputElement().getName();
  }

  public int getTabIndex() {
    return getInputElement().getTabIndex();
  }

  /**
   * Determines whether this check box is currently checked.
   * 
   * @return <code>true</code> if the check box is checked
   */
  public boolean isChecked() {
    String propName = isAttached() ? "checked" : "defaultChecked";
    return getInputElement().getPropertyBoolean(propName);
  }

  /**
   * Gets whether this widget is enabled.
   * 
   * @return <code>true</code> if the widget is enabled
   */
  public boolean isEnabled() {
    return !getInputElement().isDisabled();
  }

  public void removeClickListener(ClickListener listener) {
    if (clickListeners != null) {
      clickListeners.remove(listener);
    }
  }

  public void removeFocusListener(FocusListener listener) {
    if (focusListeners != null) {
      focusListeners.remove(listener);
    }
  }

  public void removeKeyboardListener(KeyboardListener listener) {
    if (keyboardListeners != null) {
      keyboardListeners.remove(listener);
    }
  }

  public void setAccessKey(char key) {
    getInputElement().setAccessKey(Character.toString(key));
  }

  /**
   * Checks or unchecks this check box.
   * 
   * @param checked <code>true</code> to check the check box
   */
  public void setChecked(boolean checked) {
    getInputElement().setChecked(checked);
    getInputElement().setDefaultChecked(checked);
  }

  /**
   * Sets whether this widget is enabled.
   * 
   * @param enabled <code>true</code> to enable the widget, <code>false</code>
   *          to disable it
   */
  public void setEnabled(boolean enabled) {
    getInputElement().setDisabled(!enabled);
    if (enabled) {
      removeStyleDependentName("disabled");
    } else {
      addStyleDependentName("disabled");
    }
  }

  public void setFocus(boolean focused) {
    if (focused) {
      getInputElement().focus();
    } else {
      getInputElement().blur();
    }
  }

  public void setName(String name) {
    getInputElement().setName(name);
  }

  public void setTabIndex(int index) {
    getInputElement().setTabIndex(index);
  }

  /**
   * This method is called when a widget is detached from the browser's
   * document. Overridden because of IE bug that throws away checked state and
   * in order to clear the event listener off of the <code>inputElem</code>.
   */
  @Override
  protected void onUnload() {
    setChecked(isChecked());
  }

  private InputElement getInputElement() {
    return InputElement.as(getElement());
  }
}
