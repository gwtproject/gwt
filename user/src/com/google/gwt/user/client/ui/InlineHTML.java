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
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.shared.DirectionEstimator;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * A widget that can contain arbitrary HTML.
 *
 * This widget uses a &lt;span&gt; element, causing it to be displayed with
 * inline layout.
 *
 * <p>
 * If you only need a simple label (text, but not HTML), then the
 * {@link com.google.gwt.user.client.ui.Label} widget is more appropriate, as it
 * disallows the use of HTML, which can lead to potential security issues if not
 * used properly.
 * </p>
 *
 * <p>
 * <h3>Built-in Bidi Text Support</h3>
 * This widget is capable of automatically adjusting its direction according to
 * its content. This feature is controlled by {@link #setDirectionEstimator} or
 * passing a DirectionEstimator parameter to the constructor, and is off by
 * default.
 * </p>
 *
 * <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-InlineHTML { }</li>
 * </ul>
 */
public class InlineHTML extends HTML {

  /**
   * Creates an InlineHTML widget that wraps an existing &lt;div&gt; or
   * &lt;span&gt; element.
   *
   * This element must already be attached to the document. If the element is
   * removed from the document, you must call
   * {@link RootPanel#detachNow(Widget)}.
   *
   * @param element the element to be wrapped
   */
  public static InlineHTML wrap(Element element) {
    // Assert that the element is attached.
    assert Document.get().getBody().isOrHasChild(element);

    InlineHTML html = new InlineHTML(element);

    // Mark it attached and remember it for cleanup.
    html.onAttach();
    RootPanel.detachOnWindowClose(html);

    return html;
  }

  /**
   * Creates an empty HTML widget.
   */
  public InlineHTML() {
    super(Document.get().createSpanElement());
    setStyleName("gwt-InlineHTML");
  }

  /**
   * Initializes the widget's HTML from a given {@link SafeHtml} object.
   *
   * @param html the new widget's HTML contents
   */
  public InlineHTML(SafeHtml html) {
    this(html.asString());
  }

  /**
   * Creates an HTML widget with the specified contents and with the
   * specified direction.
   *
   * @param html the new widget's SafeHtml contents
   * @param dir the content's direction. Note: {@code Direction.DEFAULT} means
   *        direction should be inherited from the widget's parent element.
   */
  public InlineHTML(SafeHtml html, Direction dir) {
    this(html.asString(), dir);
  }

  /**
   * Creates an HTML widget with the specified HTML contents and with a default
   * direction estimator.
   *
   * @param html the new widget's SafeHtml contents
   * @param directionEstimator A DirectionEstimator object used for automatic
   *          direction adjustment. For convenience,
   *          {@link Label#DEFAULT_DIRECTION_ESTIMATOR} can be used.
   */
  public InlineHTML(SafeHtml html, DirectionEstimator directionEstimator) {
    this();
    setDirectionEstimator(directionEstimator);
    setHTML(html);
  }

  /**
   * Creates an HTML widget with the specified HTML contents.
   *
   * @param html the new widget's HTML contents
   */
  public InlineHTML(String html) {
    this();
    setHTML(html);
  }

  /**
   * Creates an HTML widget with the specified HTML contents and with the
   * specified direction.
   *
   * @param html the new widget's HTML contents
   * @param dir the content's direction. Note: {@code Direction.DEFAULT} means
   *        direction should be inherited from the widget's parent element.
   */
  public InlineHTML(String html, Direction dir) {
    this();
    setHTML(html, dir);
  }

  /**
   * This constructor may be used by subclasses to explicitly use an existing
   * element. This element must be either a &lt;div&gt; &lt;span&gt; element.
   *
   * @param element the element to be used
   */
  protected InlineHTML(Element element) {
    // super(element) also asserts that element is either a &lt;div&gt; or
    // &lt;span&gt;.
    super(element);
  }
}
