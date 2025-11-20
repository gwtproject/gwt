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

/**
 * Simple JFR event impl that logs a name and timestamp/duration.
 */
@Description("General event for measuring time taken by a named task")
public class SimpleEvent extends AbstractJfrEvent {
  @Description("Name of the task being measured")
  public final String name;

  public SimpleEvent(String name) {
    this.name = name;
  }
}
