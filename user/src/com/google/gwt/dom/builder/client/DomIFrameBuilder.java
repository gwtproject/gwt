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

import com.google.gwt.dom.builder.shared.HtmlIFrameBuilder;
import com.google.gwt.dom.builder.shared.IFrameBuilder;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.annotations.IsSafeUri;

/**
 * DOM-based implementation of {@link IFrameBuilder}.
 */
public class DomIFrameBuilder extends DomElementBuilderBase<IFrameBuilder, IFrameElement> implements
    IFrameBuilder {

  DomIFrameBuilder(DomBuilderImpl delegate) {
    super(delegate);
  }

  @Override
  public IFrameBuilder frameBorder(int frameBorder) {
    assertCanAddAttribute().setFrameBorder(frameBorder);
    return this;
  }

  @Override
  public HtmlIFrameBuilder html(SafeHtml html) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isChildElementSupported() {
    return false;
  }

  @Override
  public IFrameBuilder marginHeight(int marginHeight) {
    assertCanAddAttribute().setMarginHeight(marginHeight);
    return this;
  }

  @Override
  public IFrameBuilder marginWidth(int marginWidth) {
    assertCanAddAttribute().setMarginWidth(marginWidth);
    return this;
  }

  @Override
  public IFrameBuilder name(String name) {
    assertCanAddAttribute().setName(name);
    return this;
  }

  @Override
  public IFrameBuilder noResize() {
    assertCanAddAttribute().setNoResize(true);
    return this;
  }

  @Override
  public IFrameBuilder scrolling(String scrolling) {
    assertCanAddAttribute().setScrolling(scrolling);
    return this;
  }

  @Override
  public IFrameBuilder src(SafeUri src) {
    assertCanAddAttribute().setSrc(src);
    return this;
  }

  @Override
  public IFrameBuilder src(@IsSafeUri String src) {
    assertCanAddAttribute().setSrc(src);
    return this;
  }

  @Override
  public HtmlIFrameBuilder text(String text) {
    throw new UnsupportedOperationException();
  }
}
