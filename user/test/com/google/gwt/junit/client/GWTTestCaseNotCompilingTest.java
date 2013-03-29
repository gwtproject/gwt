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
package com.google.gwt.junit.client;

import com.google.gwt.junit.JUnitFatalLaunchException;


/**
 * This class tests if the rest of testing continues even one class in the module doesn't compile.
 * This behavior is "good to have" but also important with the current implementation of compiler as
 * it may hide the compilation errors.
 */
public class GWTTestCaseNotCompilingTest extends GWTTestCaseTestBase {

  @ExpectedFailure(withType = JUnitFatalLaunchException.class)
  public void testThrowsException() throws Exception {
    Class.forName("Yes this will not compile!");
  }
}
