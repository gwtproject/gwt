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

@Name("gwt.compiler.OptimizationLoop")
@Label("Optimization Loop")
public class OptimizationLoopEvent extends AbstractOptimizationEvent {
  @Label("Optimization iteration")
  @Description("The iteration number of the optimization loop")
  final int optimizationIteration;

  @Label("Language")
  @Description("Language being optimized (Java or JavaScript)")
  final String language;

  public OptimizationLoopEvent(int loopCount, String language) {
    this.optimizationIteration = loopCount;
    this.language = language;
  }
}
