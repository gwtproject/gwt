/*
 * Copyright 2017 Google Inc.
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
package com.google.gwt.core.client;

import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;

/** Test for window.onerror reporting to {@link UncaughtExceptionHandler} */
public class GWTTestCaseUncaughtWindowErrorTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.WindowOnError";
  }

  // Disables special test UAE in this test case
  @Override
  public boolean catchExceptions() {
    return false;
  }

  // Does not work in dev mode, since JNSI code for setting up window.onerror needs Throwable.of
  // from super sourced code.
  @DoNotRunWith({Platform.Devel})
  public void testFailViaWindowOnError() {
    delayTestFinish(1000);
    GWT.setUncaughtExceptionHandler(
        new UncaughtExceptionHandler() {
          @Override
          public void onUncaughtException(Throwable e) {
            assertTrue(e.getMessage().contains("from_js"));
            finishTest();
          }
        });
    throwInNonEntryMethod();
  }

  private native void throwInNonEntryMethod() /*-{
    $wnd.setTimeout(function() {
      throw new Error("from_js");
    }, 0);
  }-*/;
}
