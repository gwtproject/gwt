package com.google.gwt.core.client.interop;

class FooImpl extends CollectionBase implements Collection {
  @Override
  public void add(Object o) {
    super.add(o);
    x = x.toString() + "FooImpl";
  }
}
