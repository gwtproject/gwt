/*
 * Copyright 2010 Google Inc.
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
package com.google.gwt.validation.client.constraints;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.validation.ConstraintValidatorContext;

/**
 * {@link javax.validation.constraints.DecimalMax} constraint validator
 * implementation for a {@link Number}.
 */
public class DecimalMaxValidatorForNumber extends
    AbstractDecimalMaxValidator<Number> {

  @Override
  public final boolean isValid(Number value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    if (value instanceof BigDecimal) {
      return isValid((BigDecimal) value);
    }
    if (value instanceof BigInteger) {
      return isValid(new BigDecimal((BigInteger) value));
    }
    return isValid(BigDecimal.valueOf(value.doubleValue()));
  }
}
