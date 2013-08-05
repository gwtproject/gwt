/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.sample.helloselfgen.selfgen.client.impl;

import com.google.gwt.sample.helloselfgen.selfgen.client.UiBinderOwner;
import com.google.gwt.uibinder.client.UiBinder;

public abstract class UiBinderWrapper<U, O extends UiBinderOwner<U>> implements UiBinder<U, O> {

  private final UiBinder<U, O> internal;


  protected UiBinderWrapper(UiBinder<U, O> internal) {
    this.internal = internal;
  }

  @Override
  public U createAndBindUi(O owner) {
    return internal.createAndBindUi(owner);
  }
}
