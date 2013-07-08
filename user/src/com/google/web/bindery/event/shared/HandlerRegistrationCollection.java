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
package com.google.web.bindery.event.shared;

import java.util.LinkedList;

/**
 * HandlerCollection allows to group many {@link HandlerRegistration}s together 
 * into one object, that will cancel all event registrations on each individual
 * {@link HandlerRegistration} once {@link HandlerRegistrationCollection#removeHandler()}
 * is called.
 *
 * Note: {@link HandlerRegistration}s are removed once
 * {@link HandlerRegistrationCollection#removeHandler()} is called.
 *
 * A simple example:
 * <code><pre>
 * HandlerRegistration hr1 = ...
 * HandlerRegistration hr2 = ...
 * return HandlerRegistrationCollection.create(hr1, hr2);
 * </pre></code>
 */
public class HandlerRegistrationCollection implements HandlerRegistration {

  /**
   * Create and return a {@link HandlerRegistrationCollection} with the given
   * {@link HandlerRegistration}s
   * @param handlers the {@link HandlerRegistration}s to add to the collection
   * @return the {@link HandlerRegistrationCollection}
   */
  public static HandlerRegistrationCollection create(HandlerRegistration... handlers) {
    HandlerRegistrationCollection collection = new HandlerRegistrationCollection();
    for (HandlerRegistration hr : handlers) {
      collection.addHandlerRegistration(hr);
    }
    return collection;
  }

  private LinkedList<HandlerRegistration> handlers = new LinkedList<HandlerRegistration>();

  /**
   * Add a {@link HandlerRegistration} to the collection
   * @param hr the {@link HandlerRegistration} to add
   */
  public void addHandlerRegistration(HandlerRegistration hr) {
    handlers.add(hr);
  }

  @Override
  public void removeHandler() {
    for (HandlerRegistration hr : handlers) {
      hr.removeHandler();
    }
    handlers.clear();
  }
}
