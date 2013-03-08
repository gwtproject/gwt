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
package com.google.gwt.user.client.rpc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.shared.SerializableThrowable;
import com.google.gwt.event.shared.UmbrellaException;

/**
 * Tests serialization of various GWT Exception classes for RPC.
 */
public class ExceptionsTest extends RpcTestBase {

  private ExceptionsTestServiceAsync exceptionsTestService;

  public void testUmbrellaException() {
    final UmbrellaException expected = TestSetFactory.createUmbrellaException();
    checkException(expected, new AsyncCallback<UmbrellaException>() {
      public void onFailure(Throwable caught) {
        TestSetValidator.rethrowException(caught);
      }

      public void onSuccess(UmbrellaException result) {
        assertNotNull(result);
        assertTrue(TestSetValidator.isValid(expected, result));
        finishTest();
      }
    });
  }

  public void testSerializableThrowable() {
    SerializableThrowable expected = new SerializableThrowable(null, "msg");
    expected.setDesignatedType("x", true);
    expected.setStackTrace(new StackTraceElement[] {new StackTraceElement("c", "m", "f", 42)});
    expected.initCause(new SerializableThrowable(null, "cause"));

    checkException(expected, new AsyncCallback<SerializableThrowable>() {
      public void onFailure(Throwable caught) {
        TestSetValidator.rethrowException(caught);
      }

      public void onSuccess(SerializableThrowable result) {
        assertNotNull(result);
        assertEquals("msg", result.getMessage());
        assertEquals("x", result.getDesignatedType());
        assertTrue(result.isExactDesignatedTypeKnown());
        assertEquals("c.m(f:42)", result.getStackTrace()[0].toString());
        assertEquals("cause", ((SerializableThrowable) result.getCause()).getMessage());
        finishTest();
      }
    });
  }

  private <T extends Throwable> void checkException(T expected, AsyncCallback<T> callback) {
    delayTestFinishForRpc();
    getServiceAsync().echo(expected, callback);
  }

  private ExceptionsTestServiceAsync getServiceAsync() {
    if (exceptionsTestService == null) {
      exceptionsTestService =
          (ExceptionsTestServiceAsync) GWT.create(ExceptionsTestService.class);
    }
    return exceptionsTestService;
  }
}
