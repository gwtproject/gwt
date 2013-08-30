/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.InternalCompilerException;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;

import java.lang.reflect.Constructor;

/**
 * CompilerPass represents a compiler pass over a compiler state.
 *
 * <p>All compiler passes should implement this interface
 */
public abstract class  CompilerPass {

  private CompilerContext compilerContext;
  /**
   * Runs the compiler pass on the compiler state.
   */
  public final boolean exec() {
    Event event = startEvent();

    boolean result = run();

    endEvent(event);
    return result;
  }

  /**
   * Run the pass.
   *
   * <p>Must be implemented by subclasses.
   */
  protected abstract boolean run();

  /**
   * Returns the name of the compiler pass
   */
  protected String getName() {
    return this.getClass().getSimpleName();
  }

  /**
   * Get SpeedTracer event.
   */
  protected SpeedTracerLogger.Event startEvent() {
    return SpeedTracerLogger.start(CompilerEventType.COMPILER_PASS, "Pass", getName());
  }

  private void endEvent(Event event) {
    if (event == null) {
      return;
    }
    event.end();
  }

  /**
   * Instantiate and run a compiler pass.
   */
  public static <T extends CompilerPass> boolean exec(Class<T> compilerPassClass,
      CompilerContext compilerContext) {
    T pass;
    try {
      Constructor<T> constructor;
      try {
        constructor = compilerPassClass.getConstructor(CompilerContext.class);
        pass = constructor.newInstance(compilerContext);
      } catch (NoSuchMethodException e) {
        pass = compilerPassClass.newInstance();
      }
      pass.setCompilerContext(compilerContext);
    } catch (Exception e) {
      throw new InternalCompilerException("Could not instantiate compiler pass " +
          compilerPassClass.getName(), e);
    }
    return pass.exec();
  }

  /**
   * Run a set of compiler passes.
   */
  public static boolean exec(CompilerContext compilerContext,
      Class... compilerPassClasses) {
    boolean didChange = false;
    for (Class compilerPassClass : compilerPassClasses) {
      didChange |= exec(compilerPassClass, compilerContext);
    }
    return didChange;
  }

  protected CompilerContext getCompilerContext() {
    return compilerContext;
  }

  protected void setCompilerContext(CompilerContext compilerContext) {
    this.compilerContext = compilerContext;
  }
}
