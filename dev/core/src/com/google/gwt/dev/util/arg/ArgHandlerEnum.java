/*
 * Copyright 2013 Google Inc.
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

import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.google.gwt.util.tools.ArgHandlerString;

/**
 * A generic arg handler for options defined by enums..
 */
public abstract  class ArgHandlerEnum<T extends Enum<T>> extends ArgHandlerString {
  private final Class<T> optionsEnum;
  private final int defaultEnumIndex;

  public ArgHandlerEnum(Class<T> optionsEnum) {
    this(optionsEnum, 0);
  }

    public ArgHandlerEnum(Class<T> optionsEnum, int defaultEnumIndex) {
    this.optionsEnum = optionsEnum;
    this.defaultEnumIndex = defaultEnumIndex;
  }

  @Override
  public String[] getTagArgs() {
    return new String[]{"[" + Joiner.on(", ").skipNulls().join(
        optionsEnum.getEnumConstants()) + "]"};
  }

  @Override
  final public boolean setString(String value) {
    T mode = null;
    try {
      mode = Enum.valueOf(optionsEnum, value.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      System.err.println(value + " is not a valid option for " + getTag());
      System.err.println(getTag() + " value must be one of [" +
          Joiner.on(", ").skipNulls().join(optionsEnum.getEnumConstants()) + "].");
      return false;
    }

    setValue(mode);
    return true;
  }

  /**
   * Override to handle the setting of an enum value.
   */
  public abstract void setValue(T value);
}
