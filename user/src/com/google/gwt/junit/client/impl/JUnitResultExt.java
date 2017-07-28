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
package com.google.gwt.junit.client.impl;

import com.google.gwt.core.shared.GwtIncompatible;
import com.google.gwt.core.shared.SerializableThrowable;
import com.google.gwt.junit.client.CspViolation;

import java.util.List;

/**
 * Server-side JUnit result container. Holds a JUnitResult along with CSP violation status.
 */
public class JUnitResultExt {
  JUnitResult result;
  boolean cspCheckpointReceived = false;
  String cspViolations;

  public void setResult(JUnitResult result) {
    this.result = result;
  }

  public JUnitResult getResult() {
    return result;
  }

  public void cspCheckpoint() {
    cspCheckpointReceived = true;
  }

  public boolean hasCompleted() {
    if (result == null) {
      return false;
    }
    return !result.cspTestingEnabled || cspCheckpointReceived;
  }

  public void setCspViolations(List<String> violations) {
    this.cspViolations = violations.toString();
  }

  public boolean isAnyException() {
    return cspViolations != null || result.isAnyException();
  }

  @GwtIncompatible
  public boolean isExceptionOf(Class<?> expectedException) {
    return result.isExceptionOf(expectedException);
  }

  public SerializableThrowable getException() {
    SerializableThrowable exception = result.getException();

    if (cspViolations != null) {
      SerializableThrowable cspException = 
          SerializableThrowable.fromThrowable(new CspViolation(cspViolations));
      if (exception == null) {
        exception = cspException;
      } else {
        exception.addSuppressed(exception);
      }
    }

    return exception;
  }

  @Override
  public String toString() {
    return "TestResultExt {result: " + result + ", cspCheckpointReceived: " +
        cspCheckpointReceived + ", cspViolations: " + cspViolations + "}";
  }
}
