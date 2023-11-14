/*
 * Copyright 2023 Google Inc.
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

import com.google.gwt.emultest.java9.util.stream.CollectorsTest;
import com.google.gwt.emultest.java9.util.stream.DoubleStreamTest;
import com.google.gwt.emultest.java9.util.stream.IntStreamTest;
import com.google.gwt.emultest.java9.util.stream.LongStreamTest;
import com.google.gwt.emultest.java9.util.stream.StreamTest;
import com.google.gwt.emultest.java9.util.ListTest;
import com.google.gwt.emultest.java9.util.MapTest;
import com.google.gwt.emultest.java9.util.OptionalDoubleTest;
import com.google.gwt.emultest.java9.util.OptionalIntTest;
import com.google.gwt.emultest.java9.util.OptionalLongTest;
import com.google.gwt.emultest.java9.util.OptionalTest;
import com.google.gwt.emultest.java9.util.SetTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/** Test JRE emulations. */
@RunWith(Suite.class)
@SuiteClasses({
        CollectorsTest.class,
        DoubleStreamTest.class,
        IntStreamTest.class,
        LongStreamTest.class,
        StreamTest.class,
        ListTest.class,
        MapTest.class,
        OptionalDoubleTest.class,
        OptionalIntTest.class,
        OptionalLongTest.class,
        OptionalTest.class,
        SetTest.class
})
public class EmulJava9Suite {
}
