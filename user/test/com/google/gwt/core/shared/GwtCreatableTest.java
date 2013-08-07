/*
 * Copyright 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.core.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.rebind.TestRebinder;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.junit.client.GWTTestCase;

@SuppressWarnings({"rawtypes", "unchecked"})
public class GwtCreatableTest extends GWTTestCase {

  public static interface TestInterface {
    boolean test();
  }

  private static final Class<? extends List> CLAZZ = ArrayList.class;

  @GwtCreate(generator = TestRebinder.class)
  public static GwtCreatable<TestInterface> create() {
    return GwtCreatable.create(TestInterface.class);
  }

  private static Class<? extends List> getTestClass() {
    return CLAZZ;
  }

  public void testGwtCreatable() {
    GwtCreatable<List<String>> creator = GwtCreatable.create(ArrayList.class);
    List<String> created = creator.create();
    assertNotNull(created);
    assertTrue(created instanceof ArrayList);
  }

  public void testGwtCreatableGenerator() {
    if (GWT.isScript()) {
      // doesn't work in dev mode yet
      GwtCreatable<TestInterface> creator = create();
      assertTrue(creator.create().test());
    }
  }

  public void testClassLiteralFinder() {
    final Class<? extends List> cls = getTestClass();
    GwtCreatable<List> creator = GwtCreatable.create(cls);
    List created = creator.create();
    assertNotNull(created);
    assertTrue(created instanceof ArrayList);
  }

  @Override
  public String getModuleName() {
    return "com.google.gwt.test.GwtCreatableTest";
  }

}
