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

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.MetadataDefinition;
import jdk.jfr.Name;
import jdk.jfr.Relational;
import jdk.jfr.StackTrace;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.atomic.AtomicInteger;

@Name("gwt.Event")
@Label("GWT Performance Event")
@Category({"GWT"})
@StackTrace(false)
@Deprecated
public class GwtJfrEvent extends Event implements AutoCloseable {
  @MetadataDefinition
  @Name("gwt.EventId")
  @Label("Event ID")
  @Relational
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ ElementType.FIELD })
  public @interface EventId {}

  private static final AtomicInteger EVENT_ID_GENERATOR = new AtomicInteger(0);

  @Label("Event ID")
  @EventId
  long eventId = EVENT_ID_GENERATOR.incrementAndGet();

  @Label("Parent Event ID")
  @EventId
  long parentId = -1;

  @Label("Module Name")
  public String moduleName;
  @Label("Compiler Phase")
  public String phase;
  @Label("Optimizer")
  public String optimizer;
  @Label("Class Name")
  public String clazz;
  @Label("Type")
  public String type;
  @Label("GWT.create type literal")
  public String argument;
  @Label("GWT.create caller")
  public String caller;
  @Label("Did Change")
  public boolean didChange;
  @Label("Number of Generated Units")
  public int generatedUnits;
  @Label("Permutation properties")
  public String properties;

//  @Label("start time (nanos)")
//  @Timestamp
//  long startTimeNanos;
//
//  @Label("duration (nanos)")
//  @Timespan
//  long durationNanos;

  @Label("event type")
  String eventType;

  @Override
  public void close() {
    commit();
    PerfLogging.end(this);
  }
  public void close(boolean didChange) {
    this.didChange = didChange;
    close();
  }
}
