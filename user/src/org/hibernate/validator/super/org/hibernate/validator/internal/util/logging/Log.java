/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.hibernate.validator.internal.util.logging;

/**
 * A stub replacement for logging in hibernate.
 */
public class Log {

  public static final Log INSTANCE = new Log();

  public boolean isDebugEnabled() {
    return false;
  }

  public void debugf(Object... params) {
  }

  public IllegalArgumentException getIllegalArgumentException(String message) {
    return new IllegalArgumentException(message);
  }

  public IllegalArgumentException getInvalidLengthForIntegerPartException() {
    return new IllegalArgumentException("The length of the integer part cannot be negative.");
  }

  public IllegalArgumentException getInvalidLengthForFractionPartException() {
    return new IllegalArgumentException("The length of the fraction part cannot be negative.");
  }

  public IllegalArgumentException getInvalidJavaIdentifierException(String value) {
    return new IllegalArgumentException(value + " is not a valid Java Identifier.");
  }

  public IllegalArgumentException getMinCannotBeNegativeException() {
    return new IllegalArgumentException("The min parameter cannot be negative.");
  }

  public IllegalArgumentException getMaxCannotBeNegativeException() {
    return new IllegalArgumentException("The max parameter cannot be negative.");
  }

  public IllegalArgumentException getLengthCannotBeNegativeException() {
    return new IllegalArgumentException("The length cannot be negative.");
  }

  public IllegalArgumentException getInvalidBigDecimalFormatException(String val,
      NumberFormatException e) {
    return new IllegalArgumentException(val + " does not represent a valid BigDecimal format.", e);
  }

  public IllegalArgumentException getUnableToParsePropertyPathException(String value) {
    return new IllegalArgumentException("Unable to parse property path " + value + ".");
  }

}