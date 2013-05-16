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

import com.google.gwt.dom.builder.shared.HtmlBuilderFactory;
import com.google.gwt.dom.builder.shared.HtmlSpanBuilder;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

/**
 * A type of widget that can wrap another widget, hiding the wrapped widget's
 * methods. When added to a panel, a composite behaves exactly as if the widget
 * it wraps had been added.
 *
 * <p>
 * <h3>Example</h3>
 * {@example com.google.gwt.examples.CompositeExample}
 * </p>
 *
 * @param <T> type of the widget wrapped
 */
public abstract class Composite<T extends Widget> extends Widget implements IsRenderable {

  private Widget widget;

  private T delegate;

  private IsRenderable renderable;

  private Element elementToWrap;

  @Override
  public void claimElement(Element element) {
    if (renderable != null) {
      renderable.claimElement(element);
      setElement(widget.getElement());
    } else {
      this.elementToWrap = element;
    }
  }

  @Override
  public void initializeClaimedElement() {
    if (renderable != null) {
      renderable.initializeClaimedElement();
    } else {
      elementToWrap.getParentNode().replaceChild(widget.getElement(), elementToWrap);
    }
  }

  @Override
  public boolean isAttached() {
    if (widget != null) {
      return widget.isAttached();
    }
    return false;
  }

  @Override
  public void onBrowserEvent(Event event) {
    // Fire any handler added to the composite itself.
    super.onBrowserEvent(event);

    // Delegate events to the widget.
    widget.onBrowserEvent(event);
  }

  @Override
  public SafeHtml render(RenderableStamper stamper) {
    if (renderable != null) {
      return renderable.render(stamper);
    } else {
      checkInit();

      HtmlSpanBuilder spanBuilder = HtmlBuilderFactory.get()
          .createSpanBuilder();
      stamper.stamp(spanBuilder).end();
      return spanBuilder.asSafeHtml();
    }
  }

  @Override
  public void render(RenderableStamper stamper, SafeHtmlBuilder builder) {
    if (renderable != null) {
      renderable.render(stamper, builder);
    } else {
      builder.append(render(stamper));
    }
  }

  /**
   * Check if the composite is initialized.
   */
  protected void checkInit() {
    if (widget == null) {
      throw new IllegalStateException("initWidget() is not called yet");
    }
  }

  /**
   * Provides subclasses access to the topmost widget that defines this
   * composite.
   *
   * @return the widget
   */
  protected Widget getWidget() {
    return widget;
  }

  public T getDelegate() {
    checkInit();

    return delegate;
  }

  /**
   * Sets the widget to be wrapped by the composite. The wrapped widget must be
   * set before calling any {@link Widget} methods on this object, or adding it
   * to a panel. initWidget may only be called once for a given composite.
   *
   * @param widget the widget to be wrapped
   */
  protected void initWidget(T widget) {
    initWidget(widget, widget);
  }

  /**
   * Sets the widget to be wrapped by the composite. The wrapped widget must be
   * set before calling any {@link Widget} methods on this object, or adding it
   * to a panel. initWidget may only be called once for a given composite.
   *
   * @param widget the widget to be wrapped
   * @param delegate the child widget that the higher level API calls (if any)
   *        will be delegated (e.g. HasWidget#add)
   */
  protected void initWidget(Widget widget, T delegate) {
    // Validate. Make sure the widget is not being set twice.
    if (this.widget != null) {
      throw new IllegalStateException("Composite.initWidget() may only be "
          + "called once.");
    }

    if (widget == null) {
      throw new NullPointerException("widget cannot be null");
    }

    if (delegate == null) {
      throw new NullPointerException("delegate cannot be null");
    }

    if (widget instanceof IsRenderable) {
      // In case the Widget being wrapped is an IsRenderable, we save that fact.
      this.renderable = (IsRenderable) widget;
    }

    // Detach the new child.
    widget.removeFromParent();

    // Use the contained widget's element as the composite's element,
    // effectively merging them within the DOM.
    Element elem = widget.getElement();
    setElement(elem);

    if (PotentialElement.isPotential(elem)) {
      PotentialElement.as(elem).setResolver(this);
    }

    // Logical attach.
    this.widget = widget;

    // Adopt.
    widget.setParent(this);

    this.delegate = delegate;
  }

  @Override
  protected void onAttach() {
    checkInit();

    if (!isOrWasAttached()) {
      widget.sinkEvents(eventsToSink);
      eventsToSink = -1;
    }

    widget.onAttach();

    // Clobber the widget's call to setEventListener(), causing all events to
    // be routed to this composite, which will delegate back to the widget by
    // default (note: it's not necessary to clear this in onDetach(), because
    // the widget's onDetach will do so).
    DOM.setEventListener(getElement(), this);

    // Call onLoad() directly, because we're not calling super.onAttach().
    onLoad();
    AttachEvent.fire(this, true);
  }

  @Override
  protected void onDetach() {
    try {
      onUnload();
      AttachEvent.fire(this, false);
    } finally {
      // We don't want an exception in user code to keep us from calling the
      // super implementation (or event listeners won't get cleaned up and
      // the attached flag will be wrong).
      widget.onDetach();
    }
  }

  @Override
  protected Element resolvePotentialElement() {
    setElement(widget.resolvePotentialElement());
    return getElement();
  }

  /**
   * This method was for initializing the Widget to be wrapped by this
   * Composite, but has been deprecated in favor of {@link #initWidget(Widget)}.
   * 
   * @deprecated Use {@link #initWidget(Widget)} instead
   */
  @Deprecated
  protected void setWidget(T widget) {
    initWidget(widget);
  }
}
