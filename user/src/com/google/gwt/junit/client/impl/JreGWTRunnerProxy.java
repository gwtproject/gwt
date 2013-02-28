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
package com.google.gwt.junit.client.impl;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

/**
 * Java reflection based {@link GWTRunnerProxy} implementation.
 */
public class JreGWTRunnerProxy implements GWTRunnerProxy {

  @Override
  public TestAccessor createTestAccessor() {
    return new TestAccessor() {

      @Override
      public GWTTestCase newInstance(String className) throws Throwable {
        return (GWTTestCase) ReflectionHelper.newInstance(ReflectionHelper.loadClass(className));
      }

      @Override
      public Object invoke(GWTTestCase test, String className, String methodName) throws Throwable {
        return ReflectionHelper.invoke(test.getClass(), test, methodName);
      }
    };
  }

  @Override
  public String getUserAgentProperty() {
    return "HostedMode"; // ???
  }
}
