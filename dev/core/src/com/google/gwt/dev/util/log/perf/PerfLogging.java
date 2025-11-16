/*
 * Copyright 2025 GWT Project Authors
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
package com.google.gwt.dev.util.log.perf;

import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;

import java.util.Optional;
import java.util.Stack;

public final class PerfLogging {
  private PerfLogging() {
    // Not instantiable
  }

  private static final ThreadLocal<Stack<GwtJfrEvent>> eventStack = ThreadLocal.withInitial(Stack::new);

  public static GwtJfrEvent start(SpeedTracerLogger.EventType eventType) {
    GwtJfrEvent event = new GwtJfrEvent();
    if (event.eventId < 4) {
      new RuntimeException("eventId= " + event.eventId + ", eventType= " + event.eventType).printStackTrace();
    }
    event.eventType = eventType.getName();
    peek().ifPresent(parent -> {
      event.parentId = parent.eventId;
    });
    push(event);
    event.begin();
    return event;
  }

  public static void end(GwtJfrEvent event) {
    GwtJfrEvent top = pop().orElseThrow(() -> new IllegalStateException("No event on stack"));
    if (top != event) {
      throw new IllegalStateException("Mismatched events, expected " + top.eventType + " but got " + event.eventType);
    }
    event.end();
  }

  private static Optional<GwtJfrEvent> pop() {
    Stack<GwtJfrEvent> gwtJfrEvents = eventStack.get();
    if (gwtJfrEvents.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(gwtJfrEvents.pop());
  }

  private static Optional<GwtJfrEvent> peek() {
    Stack<GwtJfrEvent> gwtJfrEvents = eventStack.get();
    if (gwtJfrEvents.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(gwtJfrEvents.peek());
  }
  private static void push(GwtJfrEvent event) {
    Stack<GwtJfrEvent> gwtJfrEvents = eventStack.get();
    gwtJfrEvents.push(event);
  }
}
