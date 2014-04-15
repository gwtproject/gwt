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
package com.google.web.bindery.requestfactory.gwt.client;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.google.web.bindery.requestfactory.shared.SimpleFooProxy;

import java.util.Set;

/**
 * A helper Receiver to test onFailure callbacks.
 */
public class FooReceiver extends Receiver<SimpleFooProxy> {
  private RequestFactoryTestBase test;
  private SimpleFooProxy mutableFoo;
  private Request<SimpleFooProxy> persistRequest;
  private String expectedException;

  public FooReceiver(RequestFactoryTestBase test, SimpleFooProxy mutableFoo,
      Request<SimpleFooProxy> persistRequest, String exception) {
    this.test = test;
    this.mutableFoo = mutableFoo;
    this.persistRequest = persistRequest;
    this.expectedException = exception;
  }

  @Override
  public void onFailure(ServerFailure error) {
    assertSame(persistRequest.getRequestContext(), error.getRequestContext());
    assertEquals(expectedException, error.getExceptionType());
    if (expectedException != null) {
      assertFalse(error.getStackTraceString().length() == 0);
      assertEquals("THIS EXCEPTION IS EXPECTED BY A TEST", error.getMessage());
    } else {
      assertEquals(null, error.getStackTraceString());
      assertEquals("Server Error: THIS EXCEPTION IS EXPECTED BY A TEST", error.getMessage());
    }

    // Now show that we can fix the error and try again with the same
    // request

    mutableFoo.setPleaseCrash(24); // Only 42 and 43 crash
    persistRequest.fire(new Receiver<SimpleFooProxy>() {
      @Override
      public void onSuccess(SimpleFooProxy response) {
        response = test.checkSerialization(response);
        test.finishTestAndReset();
      }
    });
  }

  @Override
  public void onSuccess(SimpleFooProxy response) {
    fail("Failure expected but onSuccess() was called");
  }

  @SuppressWarnings("deprecation")
  @Override
  public void onViolation(Set<com.google.web.bindery.requestfactory.shared.Violation> errors) {
    fail("Failure expected but onViolation() was called");
  }
}
