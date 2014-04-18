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
package com.google.gwt.dev.cfg;

import java.io.Serializable;

/**
 * Represents an abstract module property.
 */
public abstract class Property implements Comparable<Property>, Serializable {

  protected final String name;

  protected Property(String name) {
    this.name = name;
  }

  @Override
  public int compareTo(Property o) {
    return name.compareTo(o.name);
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
