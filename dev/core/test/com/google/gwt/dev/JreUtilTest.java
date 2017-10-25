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
package com.google.gwt.dev;

import junit.framework.TestCase;

public class JreUtilTest extends TestCase {

  public void testGetJDKVersion18() {
    int jdkVersion = JreUtil.getJDKVersion("18.3");
    assertEquals(18, jdkVersion);

    jdkVersion = JreUtil.getJDKVersion("18.9");
    assertEquals(18, jdkVersion);
  }

  public void testGetJDKVersion7() {
    int jdkVersion = JreUtil.getJDKVersion("1.7.0_155");
    assertEquals(7, jdkVersion);

    jdkVersion = JreUtil.getJDKVersion("1.7.0");
    assertEquals(7, jdkVersion);
  }

  public void testGetJDKVersion8() {
    int jdkVersion = JreUtil.getJDKVersion("1.8.0_152");
    assertEquals(8, jdkVersion);

    jdkVersion = JreUtil.getJDKVersion("1.8.0");
    assertEquals(8, jdkVersion);
  }

  public void testGetJDKVersion9() {
    int jdkVersion = JreUtil.getJDKVersion("9");
    assertEquals(9, jdkVersion);

    jdkVersion = JreUtil.getJDKVersion("9.0.1");
    assertEquals(9, jdkVersion);
  }

}
