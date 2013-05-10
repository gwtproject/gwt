/*
 * Copyright 2011 Google Inc.
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
package com.google.gwt.dom.builder.client;

import com.google.gwt.dom.builder.shared.BaseBuilder;
import com.google.gwt.dom.client.BaseElement;
import com.google.gwt.safehtml.shared.SafeUri;

/**
 * DOM-based implementation of {@link BaseBuilder}.
 */
public class DomBaseBuilder extends DomElementBuilderBase<BaseBuilder, BaseElement> implements
    BaseBuilder {

  DomBaseBuilder(DomBuilderImpl delegate) {
    super(delegate, true);
  }

  @Override
  public BaseBuilder href(SafeUri href) {
    assertCanAddAttribute().setSafeHref(href);
    return this;
  }

  @Override
  public BaseBuilder href(String href) {
    assertCanAddAttribute().setHref(href);
    return this;
  }

  @Override
  public BaseBuilder target(String target) {
    assertCanAddAttribute().setTarget(target);
    return this;
  }
}
