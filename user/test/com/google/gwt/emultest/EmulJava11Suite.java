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

import com.google.gwt.emultest.java11.util.OptionalDoubleTest;
import com.google.gwt.emultest.java11.util.OptionalIntTest;
import com.google.gwt.emultest.java11.util.OptionalLongTest;
import com.google.gwt.emultest.java11.util.OptionalTest;
import com.google.gwt.emultest.java11.util.function.PredicateTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/** Test JRE emulations. */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        OptionalDoubleTest.class,
        OptionalIntTest.class,
        OptionalLongTest.class,
        OptionalTest.class,
        PredicateTest.class,
})
public class EmulJava11Suite {
}
