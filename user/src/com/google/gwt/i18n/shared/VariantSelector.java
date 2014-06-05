/*
 * Copyright 2012 Google Inc.
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
package com.google.gwt.i18n.shared;

/**
 * Selects between variants based on a count, such as for plural or ordinal
 * messages.
 */
public interface VariantSelector {

  /**
   * Forms taken from ICU -- the names don't always match up well with the
   * usage, so there is a description available as well.
   */
  public enum VariantForm {
    OTHER,
    ZERO,
    ONE,
    TWO,
    FEW,
    MANY,
  }

  /**
   * Returns a list of variant forms used for this selector rule.
   * {@link VariantForm#OTHER} is always assumed present, so is not returned
   * in this list.
   */
  VariantForm[] getForms();

  /**
   * Returns a human-readable description of a variant form, intended for a
   * translator.
   * 
   * @param form
   */
  String getFormDescription(VariantForm form);


  /**
   * Choose the variant form given a count.  Exactly equivalent to {@link #select(double, int)}
   * with 0 decimal digits.
   *
   * @param n the number to choose
   * @return a variant form, which must always either be
   *     {@link VariantForm#OTHER} or a value returned by {@link #getForms()}
   *     and may never be null
   */
  // @NotNull
  VariantForm select(double n);

  /**
   * Choose the variant form given a count.
   * 
   * @param n the number to choose
   * @param decimalDigits the number of decimal digis to be displayed (0 for integers)
   * @return a variant form, which must always either be
   *     {@link VariantForm#OTHER} or a value returned by {@link #getForms()}
   *     and may never be null
   */
  // @NotNull
  VariantForm select(double n, int decimalDigits);
}
