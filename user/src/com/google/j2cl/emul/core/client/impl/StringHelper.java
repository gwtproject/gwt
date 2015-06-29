/*
 * Copyright 2015 Google Inc.
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
package com.google.j2cl.emul.core.client.impl;

/**
 * Common methods that used to be on super sourced String class, but are both needed by GWT
 * and j2cl (in other emul classses).
 */
public class StringHelper {

  public static native String valueOf(char x[], int start, int end) /*-{
    // Work around function.prototype.apply call stack size limits:
    // https://code.google.com/p/v8/issues/detail?id=2896
    // Performance: http://jsperf.com/string-fromcharcode-test/13
    var batchSize = @com.google.gwt.lang.Array::ARRAY_PROCESS_BATCH_SIZE;
    var s = "";
    for (var batchStart = start; batchStart < end;) { // increment in block
      var batchEnd = Math.min(batchStart + batchSize, end);
      s += String.fromCharCode.apply(null, x.slice(batchStart, batchEnd));
      batchStart = batchEnd;
    }
    return s;
  }-*/;
}

