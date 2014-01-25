/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.lang;

/**
 * An exception thrown for a null pointer check that the GWT compiler will
 * remove in production. GWT uses this exception instead of
 * java.lang.NullPointerException to make it harder to accidentally write
 * code that works in development and fails in production.
 *
 * <p> Instead of attempting to catch this exception, we recommend adding
 * an explicit null check and throwing java.lang.NullPointerException,
 * so that the GWT compiler doesn't remove the null check you're depending on.
 */
class DevOnlyNullPointerException extends AssertionError {
    DevOnlyNullPointerException() {
    }
}
