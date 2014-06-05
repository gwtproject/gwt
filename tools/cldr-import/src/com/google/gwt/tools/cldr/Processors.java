/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.tools.cldr;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Tracks all processors running during a generation, and information shared by all.
 */
public class Processors implements Iterable<Processor> {

  private final CldrData cldrData;
  private final File outputDir;
  private final Map<Class<? extends Processor>, Processor> instances;
  private final Set<Processor> processors;
  private final Stack<Processor> currentProcessor;
  private final Map<Processor, Set<Processor>> dependencies;

  private List<Processor> ordered;

  public Processors(CldrData cldrData, File outputDir) {
    this.cldrData = cldrData;
    this.outputDir = outputDir;
    instances = new HashMap<Class<? extends Processor>, Processor>();
    processors = new HashSet<Processor>();
    currentProcessor = new Stack<Processor>();
    dependencies = new HashMap<Processor, Set<Processor>>();
  }

  public void requireProcessor(Class<? extends Processor> processorClass) {
    // get the existing instance or create it
    Processor processor = instances.get(processorClass);
    if (processor == null) {
      try {
        Constructor<? extends Processor> ctor =
            processorClass.getConstructor(Processors.class);
        processor = ctor.newInstance(this);
      } catch (ReflectiveOperationException e) {
        throw new RuntimeException("Unable to instantiate " + processorClass, e);
      }
      processors.add(processor);
      instances.put(processorClass, processor);
    }

    // add the dependency to the currently running processor
    Processor cur = null;
    if (!currentProcessor.isEmpty()) {
      cur = currentProcessor.peek();
    }
    Set<Processor> depList = dependencies.get(cur);
    if (depList == null) {
      depList = new HashSet<Processor>();
      dependencies.put(cur, depList);
    }
    depList.add(processor);

    // find the dependencies of this new processor
    currentProcessor.add(processor);
    processor.addDependencies();
    currentProcessor.pop();
  }

  public CldrData getCldrData() {
    return cldrData;
  }

  public File getOutputDir() {
    return outputDir;
  }

  @Override
  public Iterator<Processor> iterator() {
    ensureOrder();
    return ordered.iterator();
  }

  private void ensureOrder() {
    if (ordered != null) {
      return;
    }
    ordered = new ArrayList<Processor>();
    Set<Processor> seen = new HashSet<Processor>();
    depthFirstSearch(null, seen);
  }

  private void depthFirstSearch(Processor proc, Set<Processor> seen) {
    if (seen.contains(proc)) {
      return;
    }
    seen.add(proc);
    Set<Processor> deps = dependencies.get(proc);
    if (deps != null) {
      for (Processor dep : deps) {
        depthFirstSearch(dep, seen);
      }
    }
    // null is used for top-level dependencies, so don't add it here
    if (proc != null) {
      ordered.add(proc);
    }
  }
}
