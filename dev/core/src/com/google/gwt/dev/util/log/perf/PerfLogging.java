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
    peek().ifPresent(parent -> {;
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
