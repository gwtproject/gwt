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
package com.google.gwt.dev.cfg;

import com.google.gwt.thirdparty.guava.common.base.Objects;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableList;

/**
 * The compiler's representation of the properties for one hard permutation.
 */
public class HardProps {
  private final ImmutableList<SoftProps> props;

  public HardProps(Iterable<SoftProps> softProps) {
    this.props = ImmutableList.copyOf(softProps);
    assert props.size() >= 1;
  }

  /**
   * Returns the permutation-independent properties.
   */
  public ConfigProps getConfigProps() {
    // They are all the same, so just take the first one.
    return props.get(0).getConfigProps();
  }

  /**
   * Returns the properties for each soft permutation, ordered by soft permutation id.
   *
   * <p>If soft permutations aren't turned on, the list will contain one item.
   */
  public ImmutableList<SoftProps> getSoftProps() {
    return props;
  }

  /**
   * Returns the value of a binding property as a string.
   *
   * @throws IllegalStateException if it doesn't exist or if the soft permutations
   * don't all have the same value.
   */
  public String mustGetString(String key) {
    if (!isEqualInEachPermutation(key)) {
      throw new IllegalStateException("The '" + key +
          "' binding property must be the same in each soft permutation");
    }
    String value = props.get(0).getString(key, null);
    if (value == null) {
      throw new IllegalStateException("The '" + key + "' binding property is not defined");
    }
    return value;
  }

  /**
   * Returns true if a binding property has the same value in every soft permutation.
   */
  public boolean isEqualInEachPermutation(String key) {
    String expected = props.get(0).getString(key, null);
    for (SoftProps prop : props.subList(1, props.size())) {
      String actual = prop.getString(key, null);
      if (!Objects.equal(expected, actual)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if a boolean binding property is set to true in any soft permutation.
   */
  public boolean isTrueInAnyPermutation(String name) {
    for (SoftProps softProps : props) {
      if (softProps.getBoolean(name, false)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Dumps the properties for this hard permuation, for logging and soyc.
   */
  public String prettyPrint() {
    StringBuilder out = new StringBuilder();
    for (SoftProps softProps : getSoftProps()) {
      if (out.length() > 0) {
        out.append("; ");
      }
      out.append(softProps.prettyPrint());
    }
    return out.toString();
  }
}
