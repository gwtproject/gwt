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
package com.google.web.bindery.requestfactory.gwt.client;

import com.google.web.bindery.event.shared.UmbrellaException;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.google.web.bindery.requestfactory.shared.SimpleFooProxy;
import com.google.web.bindery.requestfactory.shared.SimpleFooRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * Tests that an exception thrown by a {@link Receiver} does not prevent other
 * {@link Receiver}s from being called.
 */
public class RequestFactoryExceptionPropagationTest extends
    RequestFactoryTestBase {
  /*
   * DO NOT USE finishTest(). Instead, call finishTestAndReset();
   */

  private class CountingReceiver extends Receiver<Object> {
    int constraintViolationCallCount;
    int failureCallCount;
    int successCallCount;
    int violationCallCount;

    public void assertCounts(int expectedFailureCallCount,
        int expectedSuccessCallCount, int expectedViolationCallCount) {
      assertEquals(expectedFailureCallCount, failureCallCount);
      assertEquals(expectedSuccessCallCount, successCallCount);
      assertEquals(expectedViolationCallCount, constraintViolationCallCount);
      assertEquals(expectedViolationCallCount, violationCallCount);
    }
    
    @Override
    public void onConstraintViolation(Set<ConstraintViolation<?>> errors) {
      constraintViolationCallCount++;
      // Forward to onViolation
      super.onConstraintViolation(errors);
    }

    @Override
    public void onFailure(ServerFailure error) {
      failureCallCount++;
    }

    @Override
    public void onSuccess(Object response) {
      successCallCount++;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onViolation(Set<com.google.web.bindery.requestfactory.shared.Violation> errors) {
      violationCallCount++;
    }
  }

  private class ThrowingReceiver<T> extends Receiver<T> {
    private final RuntimeException e;

    public ThrowingReceiver(RuntimeException e) {
      this.e = e;
    }

    @Override
    public void onFailure(ServerFailure error) {
      throw e;
    }

    @Override
    public void onSuccess(T response) {
      throw e;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onViolation(Set<com.google.web.bindery.requestfactory.shared.Violation> errors) {
      throw e;
    }
  }

  /** An abstraction to verify an uncaught exception */
  public interface ExceptionVerifier {
    void verify(Throwable ex);
  }

  private static final int DELAY_TEST_FINISH = 10 * 1000;

  @Override
  public String getModuleName() {
    return "com.google.web.bindery.requestfactory.gwt.RequestFactorySuite";
  }

  private ExceptionVerifier exceptionVerifier;

  @Override
  protected void gwtTearDown() throws Exception {
    super.gwtTearDown();
    exceptionVerifier = null;
  }

  @Override
  protected void reportUncaughtException(Throwable ex) {
    try {
      exceptionVerifier.verify(ex);
    } catch (Throwable e) {
      super.reportUncaughtException(ex);
    }
  }

  /**
   * Test mixed invocation failure and success. One receiver will throw from
   * onSuccess and another from onFailure. Other receivers onSuccess and
   * onFailure will corectly be called.
   */
  public void testMixedSuccessAndFailureThrow() {
    delayTestFinish(DELAY_TEST_FINISH);

    final RuntimeException exception1 = new RuntimeException("first exception");
    final RuntimeException exception2 = new RuntimeException("second exception");
    final CountingReceiver count = new CountingReceiver();

    SimpleFooRequest context = req.simpleFooRequest();
    // 42 is the crash causing magic number for a runtime exception
    context.pleaseCrash(42).to(count);
    context.returnNullString().to(new ThrowingReceiver<String>(exception1));
    context.returnNullString().to(count);

    fireContextAndCatch(context, new ThrowingReceiver<Void>(exception2), new ExceptionVerifier() {
      @Override
      public void verify(Throwable e) {
        assertUmbrellaException(e, exception1, exception2);
        count.assertCounts(1, 1, 0);
        finishTestAndReset();
      }
    });
  }

  /**
   * Test invocation failure. Other invocations' onSuccess should correctly be
   * called.
   */
  public void testOnFailureThrow() {
    delayTestFinish(DELAY_TEST_FINISH);

    final RuntimeException exception1 = new RuntimeException("first exception");
    final RuntimeException exception2 = new RuntimeException("second exception");
    final CountingReceiver count = new CountingReceiver();

    SimpleFooRequest context = req.simpleFooRequest();
    context.returnNullString().to(count);
    // 42 is the crash causing magic number for a runtime exception
    context.pleaseCrash(42).to(new ThrowingReceiver<Void>(exception1));
    context.returnNullString().to(count);

    fireContextAndCatch(context, new ThrowingReceiver<Void>(exception2), new ExceptionVerifier() {
      @Override
      public void verify(Throwable e) {
        assertUmbrellaException(e, exception1, exception2);
        count.assertCounts(0, 2, 0);
        finishTestAndReset();
      }
    });
  }

  /**
   * Test global failure. All receivers will have their onFailure called, and
   * some of them will throw.
   */
  public void testOnGlobalFailureThrow() {
    delayTestFinish(DELAY_TEST_FINISH);

    final RuntimeException exception1 = new RuntimeException("first exception");
    final RuntimeException exception2 = new RuntimeException("second exception");
    final CountingReceiver count = new CountingReceiver();

    SimpleFooRequest context = req.simpleFooRequest();
    SimpleFooProxy newFoo = context.create(SimpleFooProxy.class);

    context.returnNullString().to(count);
    context.persist().using(newFoo).to(new ThrowingReceiver<Void>(exception1));
    context.returnNullString().to(count);

    final SimpleFooProxy mutableFoo = context.edit(newFoo);
    // 42 is the crash causing magic number for a runtime exception
    mutableFoo.setPleaseCrash(42);

    fireContextAndCatch(context, new ThrowingReceiver<Void>(exception2), new ExceptionVerifier() {
      @Override
      public void verify(Throwable e) {
        assertUmbrellaException(e, exception1, exception2);
        count.assertCounts(2, 0, 0);
        finishTestAndReset();
      }
    });
  }

  /**
   * All receivers will have their onSuccess called, and some of them will
   * throw.
   */
  public void testOnSuccessThrow() {
    delayTestFinish(DELAY_TEST_FINISH);

    final RuntimeException exception1 = new RuntimeException("first exception");
    final RuntimeException exception2 = new RuntimeException("second exception");
    final CountingReceiver count = new CountingReceiver();

    SimpleFooRequest context = req.simpleFooRequest();
    context.returnNullString().to(count);
    context.returnNullString().to(new ThrowingReceiver<String>(exception1));
    context.returnNullString().to(count);

    fireContextAndCatch(context, new ThrowingReceiver<Void>(exception2), new ExceptionVerifier() {
      @Override
      public void verify(Throwable e) {
        assertUmbrellaException(e, exception1, exception2);
        count.assertCounts(0, 2, 0);
        finishTestAndReset();
      }
    });
  }

  /**
   * Test violations. All receivers will have their onViolation called, and some
   * of them will throw.
   */
  public void testOnViolationThrow() {
    delayTestFinish(DELAY_TEST_FINISH);

    final RuntimeException exception1 = new RuntimeException("first exception");
    final RuntimeException exception2 = new RuntimeException("second exception");
    final CountingReceiver count = new CountingReceiver();

    SimpleFooRequest context = req.simpleFooRequest();
    SimpleFooProxy newFoo = context.create(SimpleFooProxy.class);
    newFoo.setUserName("a"); // too short

    context.returnNullString().to(count);
    context.persist().using(newFoo).to(new ThrowingReceiver<Void>(exception1));
    context.returnNullString().to(count);

    fireContextAndCatch(context, new ThrowingReceiver<Void>(exception2), new ExceptionVerifier() {
      @Override
      public void verify(Throwable e) {
        assertUmbrellaException(e, exception1, exception2);
        count.assertCounts(0, 0, 2);
        finishTestAndReset();
      }
    });
  }

  protected void fireContextAndCatch(RequestContext context,
      Receiver<Void> receiver, ExceptionVerifier exceptionVerifier) {
    this.exceptionVerifier = exceptionVerifier;
    if (receiver == null) {
      context.fire();
    } else {
      context.fire(receiver);
    }
  }

  private static void assertUmbrellaException(Throwable e, Throwable... expectedCauses) {
    assertTrue(e instanceof UmbrellaException);
    Set<Throwable> causes = new HashSet<Throwable>(Arrays.asList(expectedCauses));
    assertEquals(causes, ((UmbrellaException) e).getCauses());
  }
}
