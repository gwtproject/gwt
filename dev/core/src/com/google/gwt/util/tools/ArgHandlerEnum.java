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
package com.google.gwt.util.tools;

import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * A generic arg handler for options defined by enums.
 *
 * @param <T> enum type providing option values.
 */
public abstract  class ArgHandlerEnum<T extends Enum<T>> extends ArgHandler {
  private final Class<T> optionsEnumClass;

  public ArgHandlerEnum(Class<T> optionsEnumClass) {
    this.optionsEnumClass = optionsEnumClass;
  }

  @Override
  public String[] getTagArgs() {
    return new String[]{ getFormattedOptionNames(optionsEnumClass) };
  }

  @Override
  public int handle(String[] args, int startIndex) {
    if (startIndex + 1 < args.length) {
      String value = args[startIndex + 1].trim().toUpperCase(Locale.ENGLISH);
      T mode = null;
      try {
        mode = Enum.valueOf(optionsEnumClass, value);
      } catch (IllegalArgumentException e) {
        System.err.println(value + " is not a valid option for " + getTag());
        System.err.println(
            getTag() + " value must be one of " + getFormattedOptionNames(optionsEnumClass) + ".");
        return -1;
      }
      setValue(mode);
      return 1;
    }

    System.err.println("Missing argument for "  + getTag() + " must be followed by one of " +
        getFormattedOptionNames(optionsEnumClass) + ".");
    return -1;
  }

  /**
   * Override to handle the setting of an enum value.
   */
  public abstract void setValue(T value);

  private List<String> getEnumNames(Class<T> optionsEnumClass) {
    return Lists.transform(Arrays.asList(optionsEnumClass.getEnumConstants()),
        new Function<T, String>() {
          @Override
          public String apply(T t) {
            return t.name();
          }
        });
  }

  private String getFormattedOptionNames(Class<T> optionsEnumClass) {
    return "[" +
        Joiner.on(", ").skipNulls().join(getEnumNames(optionsEnumClass)) + "]";
  }
}
