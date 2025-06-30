/*
 * Copyright 2025 GWT Project Authors
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
package com.google.gwt.emultest;

import com.google.gwt.emultest.java17.lang.CharSequenceTest;
import com.google.gwt.emultest.java17.lang.StringTest;
import com.google.gwt.emultest.java17.util.stream.CollectorsTest;
import com.google.gwt.emultest.java17.util.stream.DoubleStreamTest;
import com.google.gwt.emultest.java17.util.stream.IntStreamTest;
import com.google.gwt.emultest.java17.util.stream.LongStreamTest;
import com.google.gwt.emultest.java17.util.stream.StreamTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/** Test JRE emulations. */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    CharSequenceTest.class,
    StringTest.class,
    CollectorsTest.class,
    StreamTest.class,
    DoubleStreamTest.class,
    IntStreamTest.class,
    LongStreamTest.class
})
public class EmulJava17Suite {
}
