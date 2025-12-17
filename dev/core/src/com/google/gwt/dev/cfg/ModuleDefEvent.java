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
package com.google.gwt.dev.cfg;

import com.google.gwt.dev.util.log.perf.AbstractJfrEvent;

import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("gwt.compiler.ModuleDefEvent")
public class ModuleDefEvent extends AbstractJfrEvent {
  @Label("Phase")
  @Description("The phase of module loading")
  final String phase;

  @Label("Module Name")
  @Description("Fully qualified name of the GWT Module")
  final String moduleName;

  public ModuleDefEvent(String phase, String moduleName) {
    this.phase = phase;
    this.moduleName = moduleName;
  }
}