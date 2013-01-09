/*
 * Copyright 2010 Google Inc.
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

package com.google.gwt.event.dom.client;

/**
 * Receiver used to handle all touch events at once.
 *
 * WARNING, PLEASE READ: As this class is intended for developers who wish to
 * handle all touch events in GWT, new touch handler interfaces will be added to
 * it. Therefore, updates to GWT could cause breaking API changes.
 *
 */
public abstract class HandlesAllTouchEvents implements TouchStartHandler,
    TouchMoveHandler, TouchEndHandler, TouchCancelHandler {

  /**
   * Convenience method used to handle all touch events from an event source.
   *
   * @param <H> receiver type, must implement all touch handlers
   * @param source the event source
   * @param reciever the receiver implementing all touch handlers
   */
  public static <H extends TouchStartHandler & TouchMoveHandler
      & TouchEndHandler & TouchCancelHandler>
      void handle(HasAllTouchHandlers source, H reciever) {
    source.addTouchStartHandler(reciever);
    source.addTouchMoveHandler(reciever);
    source.addTouchEndHandler(reciever);
    source.addTouchCancelHandler(reciever);
  }

  /**
   * Constructor.
   */
  public HandlesAllTouchEvents() {
  }

  /**
   * Convenience method to handle all touch events from an event source.
   *
   * @param eventSource the event source
   */
  public void handle(HasAllTouchHandlers eventSource) {
    handle(eventSource, this);
  }
}