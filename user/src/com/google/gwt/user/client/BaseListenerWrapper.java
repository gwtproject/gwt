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

package com.google.gwt.user.client;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.GwtEvent.Type;

/**
 * Legacy listener support hierarchy root.
 * 
 * Note, this class and its subtypes all assume that the handlers are stored in
 * handler managers.
 * 
 * This class, and its children are used to gather the bulk of the legacy glue
 * code in one place, for easy deletion when Listener methods are deleted.
 * 
 * @param <T> listener type to be wrapped
 * @deprecated will be removed in GWT 2.7 with the handler listeners themselves
 */
@Deprecated
public abstract class BaseListenerWrapper<T> implements EventHandler {

  static class WrapWindowClose extends BaseListenerWrapper<WindowCloseListener>
      implements Window.ClosingHandler, CloseHandler<Window> {
    @Deprecated
    public static void add(WindowCloseListener listener) {
      WrapWindowClose handler = new WrapWindowClose(listener);
      Window.addWindowClosingHandler(handler);
      Window.addCloseHandler(handler);
    }

    public static void remove(HandlerManager manager,
        WindowCloseListener listener) {
      baseRemove(manager, listener, Window.ClosingEvent.getType(),
          CloseEvent.getType());
    }

    private WrapWindowClose(WindowCloseListener listener) {
      super(listener);
    }

    public void onClose(CloseEvent<Window> event) {
      listener.onWindowClosed();
    }

    public void onWindowClosing(Window.ClosingEvent event) {
      String message = listener.onWindowClosing();
      if (event.getMessage() == null) {
        event.setMessage(message);
      }
    }
  }

  static class WrapWindowResize extends
      BaseListenerWrapper<WindowResizeListener> implements ResizeHandler {
    @Deprecated
    public static void add(WindowResizeListener listener) {
      Window.addResizeHandler(new WrapWindowResize(listener));
    }

    public static void remove(HandlerManager manager,
        WindowResizeListener listener) {
      baseRemove(manager, listener, ResizeEvent.getType());
    }

    private WrapWindowResize(WindowResizeListener listener) {
      super(listener);
    }

    public void onResize(ResizeEvent event) {
      listener.onWindowResized(event.getWidth(), event.getHeight());
    }
  }

  static class WrapWindowScroll extends
      BaseListenerWrapper<WindowScrollListener> implements Window.ScrollHandler {
    @Deprecated
    public static void add(WindowScrollListener listener) {
      Window.addWindowScrollHandler(new WrapWindowScroll(listener));
    }

    public static void remove(HandlerManager manager,
        WindowScrollListener listener) {
      baseRemove(manager, listener, Window.ScrollEvent.getType());
    }

    private WrapWindowScroll(WindowScrollListener listener) {
      super(listener);
    }

    public void onWindowScroll(Window.ScrollEvent event) {
      listener.onWindowScrolled(event.getScrollLeft(), event.getScrollTop());
    }
  }

  /**
   * Helper method to remove all wrapped listeners from the given event types.
   * 
   * @param manager the manager to remove the listener from
   * @param listener the listener
   * @param types the event types to remove the listener from
   * @param <H>
   */
  // This is an internal helper method with the current formulation, we have
  // lost the info needed to make it safe by this point.
  @SuppressWarnings("rawtypes")
  protected static <H extends EventHandler> void baseRemove(
      HandlerManager manager, Object listener, Type... types) {
    if (manager != null) {
      for (Type<H> key : types) {
        int handlerCount = manager.getHandlerCount(key);
        // We are removing things as we traverse, have to go backward
        for (int i = handlerCount - 1; i >= 0; i--) {
          H handler = manager.getHandler(key, i);
          if (handler instanceof BaseListenerWrapper
              && ((BaseListenerWrapper) handler).listener.equals(listener)) {
            manager.removeHandler(key, handler);
          }
        }
      }
    }
  }

  /**
   * Listener being wrapped.
   */
  final T listener;

  /**
   * Creates a new listener wrapper.
   * 
   * @param listener the listener to wrap
   */
  protected BaseListenerWrapper(T listener) {
    this.listener = listener;
  }

  /**
   * Gets the listener being wrapped.
   * 
   * @return the wrapped listener
   */
  protected T getListener() {
    return listener;
  }
}
