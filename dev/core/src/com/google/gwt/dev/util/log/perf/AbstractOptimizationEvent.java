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

public class AbstractOptimizationEvent extends AbstractJfrEvent {
  @Label("Number of Modifications")
  public int numMods;

  @Label("Node Count")
  @Description("Number of AST nodes in the program at the start of the optimization loop. Unset" +
      "in cases where the compiler hasn't taken the time to measure the nodes, such as where the" +
      "pass isn't part of a loop.")
  public int nodeCount;
}
