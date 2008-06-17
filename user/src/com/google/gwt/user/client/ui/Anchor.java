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

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.BidiUtils;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.user.client.Event;

/**
 * A widget that represents a simple &lt;a&gt; element.
 * 
 * <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-Anchor { }</li>
 * </ul>
 */
public class Anchor extends Widget implements SourcesClickEvents,
    SourcesMouseEvents, HasHorizontalAlignment, HasText, HasHTML, HasWordWrap,
    HasDirection, HasFocus {

  /**
   * Creates an Anchor widget that wraps an existing &lt;div&gt; or &lt;span&gt;
   * element.
   * 
   * This element must already be attached to the document.
   * 
   * @param element the element to be wrapped
   */
  public static Anchor wrap(Element element) {
    // Assert that the element is of the correct type and is attached.
    AnchorElement.as(element);
    assert Document.get().getBody().isOrHasChild(element);

    Anchor anchor = new Anchor(element);

    // Mark it attached and remember it for cleanup.
    anchor.onAttach();
    RootPanel.detachOnWindowClose(anchor);

    return anchor;
  }

  private ClickListenerCollection clickListeners;
  private HorizontalAlignmentConstant horzAlign;
  private MouseListenerCollection mouseListeners;
  private FocusListenerCollection focusListeners;
  private KeyboardListenerCollection keyboardListeners;

  /**
   * Creates an empty anchor.
   */
  public Anchor() {
    setElement(Document.get().createAnchorElement());
    setStyleName("gwt-Anchor");
  }

  /**
   * Creates an anchor with its text and href (target URL) specified.
   * 
   * @param text the anchor's text
   * @param asHTML <code>true</code> to treat the specified text as html
   * @param href the url to which it will link
   */
  public Anchor(String text, boolean asHTML, String href) {
    this();
    if (asHTML) {
      setHTML(text);
    } else {
      setText(text);
    }
    setHref(href);
  }

  /**
   * Creates an anchor with its text and href (target URL) specified.
   * 
   * @param text the anchor's text
   * @param href the url to which it will link
   */
  public Anchor(String text, String href) {
    this();
    setText(text);
    setHref(href);
  }

  private Anchor(Element element) {
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

  public void addMouseListener(MouseListener listener) {
    if (mouseListeners == null) {
      mouseListeners = new MouseListenerCollection();
      sinkEvents(Event.MOUSEEVENTS);
    }
    mouseListeners.add(listener);
  }

  public Direction getDirection() {
    return BidiUtils.getDirectionOnElement(getElement());
  }

  public HorizontalAlignmentConstant getHorizontalAlignment() {
    return horzAlign;
  }

  /**
   * Gets the anchor's href (the url to which it links).
   * 
   * @return the anchor's href
   */
  public String getHref() {
    return getAnchorElement().getHref();
  }

  public String getHTML() {
    return getElement().getInnerHTML();
  }

  public int getTabIndex() {
    return getAnchorElement().getTabIndex();
  }

  public String getText() {
    return getElement().getInnerText();
  }

  public boolean getWordWrap() {
    return !getElement().getStyle().getProperty("whiteSpace").equals("nowrap");
  }

  @Override
  public void onBrowserEvent(Event event) {
    switch (event.getTypeInt()) {
      case Event.ONCLICK:
        if (clickListeners != null) {
          clickListeners.fireClick(this);
        }
        break;

      case Event.ONMOUSEDOWN:
      case Event.ONMOUSEUP:
      case Event.ONMOUSEMOVE:
      case Event.ONMOUSEOVER:
      case Event.ONMOUSEOUT:
        if (mouseListeners != null) {
          mouseListeners.fireMouseEvent(this, event);
        }
        break;

      case Event.ONBLUR:
      case Event.ONFOCUS:
        if (focusListeners != null) {
          focusListeners.fireFocusEvent(this, event);
        }
        break;

      case Event.ONKEYDOWN:
      case Event.ONKEYUP:
      case Event.ONKEYPRESS:
        if (keyboardListeners != null) {
          keyboardListeners.fireKeyboardEvent(this, event);
        }
        break;
    }
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

  public void removeMouseListener(MouseListener listener) {
    if (mouseListeners != null) {
      mouseListeners.remove(listener);
    }
  }

  public void setAccessKey(char key) {
    getAnchorElement().setAccessKey(Character.toString(key));
  }

  public void setDirection(Direction direction) {
    BidiUtils.setDirectionOnElement(getElement(), direction);
  }

  public void setFocus(boolean focused) {
    if (focused) {
      getAnchorElement().focus();
    } else {
      getAnchorElement().blur();
    }
  }

  public void setHorizontalAlignment(HorizontalAlignmentConstant align) {
    horzAlign = align;
    getElement().getStyle().setProperty("textAlign", align.getTextAlignString());
  }

  /**
   * Sets the anchor's href (the url to which it links).
   * 
   * @param href the anchor's href
   */
  public void setHref(String href) {
    getAnchorElement().setHref(href);
  }

  public void setHTML(String html) {
    getElement().setInnerHTML(html);
  }

  public void setTabIndex(int index) {
    getAnchorElement().setTabIndex(index);
  }

  public void setText(String text) {
    getElement().setInnerText(text);
  }

  public void setWordWrap(boolean wrap) {
    getElement().getStyle().setProperty("whiteSpace",
        wrap ? "normal" : "nowrap");
  }

  private AnchorElement getAnchorElement() {
    return AnchorElement.as(getElement());
  }
}
