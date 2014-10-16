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
package com.google.gwt.user;

import com.google.gwt.junit.tools.GWTTestSuite;
import com.google.gwt.user.client.rpc.FinalFieldsTest;

import junit.framework.Test;

/**
 * A collection of TestCases for the RPC system with final field serialization enabled.
 */
public class RPCWithSerializeFinalFieldsSuite {

  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite(
        "Test for com.google.gwt.user.client.rpc");

    suite.addTestSuite(FinalFieldsTest.class);

    return suite;
  }
}
