package com.google.gwt.dev.jjs.test;

import java.util.ArrayList;

/** A superclass that invokes a method in its cstr, used by @{link CompilerTest}. */
class FieldInitOrderBase {
  FieldInitOrderBase(ArrayList<String> seenValues, int x) {
    method(seenValues, x);
  }

  void method(ArrayList<String> seenValues, int x) {
  }
}