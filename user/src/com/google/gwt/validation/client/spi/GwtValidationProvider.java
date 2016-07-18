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
package com.google.gwt.validation.client.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.validation.client.AbstractGwtValidatorFactory;
import com.google.gwt.validation.client.impl.BaseGwtConfiguration;
import com.google.gwt.validation.client.impl.GwtConfiguration;

import javax.validation.Configuration;
import javax.validation.ValidatorFactory;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;

/**
 * GWT {@link ValidationProvider}.
 */
public final class GwtValidationProvider implements
    ValidationProvider<BaseGwtConfiguration> {

  @Override
  public ValidatorFactory buildValidatorFactory(ConfigurationState configurationState) {
    AbstractGwtValidatorFactory validatorFactory =
        (AbstractGwtValidatorFactory) GWT.create(ValidatorFactory.class);
    validatorFactory.init(configurationState);
    return validatorFactory;
  }

  @Override
  public Configuration<?> createGenericConfiguration(BootstrapState state) {
    return new GwtConfiguration(this, state);
  }

  @Override
  public GwtConfiguration createSpecializedConfiguration(BootstrapState state) {
    return new GwtConfiguration(this, state);
  }
}
