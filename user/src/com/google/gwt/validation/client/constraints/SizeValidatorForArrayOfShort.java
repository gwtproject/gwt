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

import javax.validation.ConstraintValidatorContext;

/**
 * {@link javax.validation.constraints.Size} constraint validator implementation
 * for a array of {@code short}s.
 */
public class SizeValidatorForArrayOfShort extends
    AbstractSizeValidator<short[]> {

  @Override
  public final boolean isValid(short[] value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    return isLengthValid(value.length);
  }
}
