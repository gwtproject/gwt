package com.google.gwt.validation.client.impl;

import javax.validation.ConstraintViolation;

import junit.framework.TestCase;

public class ConstraintViolationImplTest extends TestCase {

  public <T> void testEquals() throws Exception {
    String constraintMessage = "May not be null";
    String path = "path";
    ConstraintViolation<T> a = createViolation(constraintMessage, path);
    ConstraintViolation<T> b = createViolation(constraintMessage, path);
    assertTrue(a.equals(b));
  }

  public <T> void testNotEquals() throws Exception {
    String constraintMessage = "May not be null";
    ConstraintViolation<T> a = createViolation(constraintMessage, "path 1");
    ConstraintViolation<T> b = createViolation(constraintMessage, "path 2");
    assertFalse(a.equals(b));
  }

  private <T> ConstraintViolation<T> createViolation(String msg, final String path) {
    return new ConstraintViolationImpl.Builder<T>()
        .setMessage(msg)
        .setRootBean(null)
        .setPropertyPath(new PathImpl().append(path))
        .build();
  }

}
