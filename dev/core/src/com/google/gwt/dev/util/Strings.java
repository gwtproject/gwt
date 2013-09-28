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
package com.google.gwt.dev.util;

import com.google.gwt.thirdparty.guava.common.base.Joiner;

/**
 * String manipulation utilities.
 */
public class Strings {

  /**
   * Join strings inserting separator between them.
   */
  public static String join(String[] strings, String separator) {
    return Joiner.on(separator).join(strings);
  }

  /**
   * @return the path components, result of splitting by "/".
   */
  public static String[] splitPath(String path) {
    return path.split("/");
  }
}
