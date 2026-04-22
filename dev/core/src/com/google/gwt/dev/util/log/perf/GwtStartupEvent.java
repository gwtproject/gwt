/*
 * Copyright 2026 GWT Project Authors
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

import com.google.gwt.dev.About;
import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;

/**
 * Logs the class that was used to run GWT and the GWT build in use.
 */
@Name("gwt.Startup")
@Description("Basic startup info about the current GWT process")
@Category("GWT")
@StackTrace(false)
public class GwtStartupEvent extends Event {
  @Description("GWT compiler version")
  final String gwtVersion = About.getGwtVersion();

  @Description("Git revision of the GWT compiler build")
  final String gwtCommit = About.getGwtGitRev();

  @Description("Class being invoked to start the compiler")
  final Class<?> main;

  public GwtStartupEvent(Class<?> main) {
    this.main = main;
    commit();
  }
}
