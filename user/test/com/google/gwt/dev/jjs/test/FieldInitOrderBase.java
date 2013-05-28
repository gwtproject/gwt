package com.google.gwt.dev.jjs.test;

import java.util.ArrayList;

/**
 * A superclass that invokes a method in its cstr, so that subclasses can see their state before
 * their own cstr has run.
 * 
 * See {@link CompilerTest#testFieldInitializationOrder()}.
 */
class FieldInitOrderBase {
  FieldInitOrderBase(ArrayList<String> seenValues, int x) {
    method(seenValues, x);
  }

  void method(ArrayList<String> seenValues, int x) {
  }
}