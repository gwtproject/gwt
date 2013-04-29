/*
 * Copyright 2009 Google Inc.
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
package com.google.gwt.dev.util.arg;

/**
 * Option to request extra Compile Report (SOYC) output at the expense of more compile time.
 */
public interface OptionSoycDetailed {

  /**
   * Returns true if the compiler should record and emit extra Compile Report information.
   */
  boolean isSoycExtra();

  /**
   * Sets whether or not the compiler should record and emit Compile Report information
   * and build the dashboard.
   */
  void setSoycEnabled(boolean enabled);

  /**
   * Sets whether or not the compiler should record and emit extra Compile Report
   * information.
   */
  void setSoycExtra(boolean enabled);
}
