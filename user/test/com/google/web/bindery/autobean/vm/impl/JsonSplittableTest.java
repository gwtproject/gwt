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
package com.google.web.bindery.autobean.vm.impl;

import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.vm.SplittableJreTest;

public class JsonSplittableTest extends SplittableJreTest {
  public void testSize() {
	// new one should be empty
    JsonSplittable splittable = (JsonSplittable) JsonSplittable.createIndexed();
    assertEquals(0, splittable.size());

    // add two elements: should auto-size
    Splittable one = JsonSplittable.create("\"one\"");
    Splittable two = JsonSplittable.create("\"two\""); 
    one.assign(splittable, 0);
    two.assign(splittable, 1);
    assertEquals(2, splittable.size());

    // resize: should retain one and two, and add 8 more undefineds.
    splittable.setSize(10);
    assertEquals(10, splittable.size());
    assertEquals(one.asString(), splittable.get(0).asString());
    assertEquals(two.asString(), splittable.get(1).asString());
    for (int i = 2; i < 10; i++) {
      assertNull(splittable.get(i));
    }
  }
}
