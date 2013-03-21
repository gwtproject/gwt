/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.logging.impl;

import java.io.FilterOutputStream;
import java.io.PrintStream;

/**
 * A {@link PrintStream} implementation that implements only a subset of methods that is enough to
 * be used with {@link Throwable#printStackTrace(PrintStream)}.
 */
public class StackTracePrintStream extends PrintStream {

  private final StringBuilder builder;

  public StackTracePrintStream(StringBuilder builder) {
    super(new FilterOutputStream(null));
    this.builder = builder;
  }

  @Override
  public void print(Object obj) {
    print(String.valueOf(obj));
  }

  @Override
  public void println(Object obj) {
    print(obj);
    println();
  }

  @Override
  public void print(String str) {
    builder.append(str);
  }

  @Override
  public void println() {
    print("\n");
  }

  @Override
  public void println(String str) {
    print(str);
    println();
  }
}