/*
 * Copyright 2011 Google Inc.
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
package com.google.gwt.dev;

import com.google.gwt.dev.util.log.perf.SimpleEvent;

import java.awt.GraphicsEnvironment;

/**
 * Creates a Graphics2D context in a thread in order to go ahead and get first
 * time initialization out of the way. Delays ranging from 200ms to 6s have
 * been observed when initializing the library.
 */
class GraphicsInitThread extends Thread {
  public GraphicsInitThread() {
    // We don't care if the program finishes before the initialization ends.
    setDaemon(true);
  }

  @Override
  public void run() {
    try (SimpleEvent ignored = new SimpleEvent("Graphics2d")) {
      GraphicsEnvironment.getLocalGraphicsEnvironment();
    }
  }
}