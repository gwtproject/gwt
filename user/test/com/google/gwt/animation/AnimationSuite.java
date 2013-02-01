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
package com.google.gwt.animation;

import com.google.gwt.animation.client.AnimationApiUsageTest;
import com.google.gwt.animation.client.AnimationSchedulerImplTimerTest;
import com.google.gwt.animation.client.AnimationTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests of the animation package.
 */
public class AnimationSuite {
  public static Test suite() {
    TestSuite suite = new TestSuite("Tests of the animation package");

    suite.addTestSuite(AnimationApiUsageTest.class);
    suite.addTestSuite(AnimationSchedulerImplTimerTest.class);
    suite.addTestSuite(AnimationTest.class);

    return suite;
  }
}
