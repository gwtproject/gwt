/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.core.client;

import com.google.gwt.core.client.impl.IterateAsArray;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;

import java.util.Iterator;

/**
 * Tests JsArray variants.
 */
public class IterateAsArrayTest extends GWTTestCase {

  interface NonIterateAsArray<T> extends Iterable<T> {
  }

  @IterateAsArray(getter = "fetch", length = "howBig")
  interface MyList<T> extends NonIterateAsArray<T> {
    int howBig();
    T fetch(int i);
  }

  static final class MyListImpl extends JavaScriptObject implements MyList<String> {

    protected MyListImpl() {}
    public static native MyListImpl make() /*-{
      return ['Apples', 'Oranges', 'Grapes'];
    }-*/;

    @Override public native int howBig() /*-{
      return this.length;
    }-*/;

    @Override public native String fetch(int i) /*-{
      return this[i];
    }-*/;

    @Override public Iterator<String> iterator() {
      fail("Should not have called iterator");
      return null;
    }
  }

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.Core";
  }

  public void testIterateAsArray() {
    MyList<String> x = MyListImpl.<String>make();
    MyListImpl impl = (MyListImpl) x;

    int c = 0;
    String[] strings = { "Apples", "Oranges", "Grapes" };

    // against interface
    for (String s : x) {
      assertEquals(strings[c++], s);
    }

    // against implementor
    c = 0;
    for (String s : impl) {
      assertEquals(strings[c++], s);
    }
  }

  public void testIterateAsIterator() {
    MyList<String> x = MyListImpl.<String>make();
    NonIterateAsArray<String> y = x;

    try {
      for (String s : y) {
        fail("Should through exception because iterator should be called");
      }
    } catch (AssertionError ae) {
      // this is expected, so we pass if we get here
    }
  }
}
