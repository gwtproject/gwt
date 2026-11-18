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

import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.Name;

/**
 * Simple JFR event wrapper that logs a name and timestamp/duration. Wraps the actual jfr event to
 * let this class be used as in code compiled with javac --release=8.
 */
public class SimpleEvent implements AutoCloseable {
  @Description("General event for measuring time taken by a named task")
  @Name("gwt.SimpleEvent")
  private static class SimpleEventInternal extends AbstractJfrEvent {
    @Description("Name of the task being measured")
    @Label("Name")
    public final String name;

    SimpleEventInternal(String name) {
      this.name = name;
    }
  }
  private final SimpleEventInternal event;

  public SimpleEvent(String name) {
    this.event = new SimpleEventInternal(name);
  }

  @Override
  public void close() {
    event.close();
  }
}
