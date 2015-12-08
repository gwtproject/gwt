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

import com.google.gwt.dom.builder.shared.LinkBuilder;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.safehtml.shared.annotations.IsTrustedResourceUri;

/**
 * DOM-based implementation of {@link LinkBuilder}.
 */
public class DomLinkBuilder extends DomElementBuilderBase<LinkBuilder, LinkElement> implements
    LinkBuilder {

  DomLinkBuilder(DomBuilderImpl delegate) {
    super(delegate, true);
  }

  @Override
  public LinkBuilder disabled() {
    assertCanAddAttribute().setDisabled(true);
    return this;
  }

  @Override
  public LinkBuilder href(@IsTrustedResourceUri String href) {
    assertCanAddAttribute().setHref(href);
    return this;
  }

  @Override
  public LinkBuilder hreflang(String hreflang) {
    assertCanAddAttribute().setHreflang(hreflang);
    return this;
  }

  @Override
  public LinkBuilder media(String media) {
    assertCanAddAttribute().setMedia(media);
    return this;
  }

  @Override
  public LinkBuilder rel(String rel) {
    assertCanAddAttribute().setRel(rel);
    return this;
  }

  @Override
  public LinkBuilder target(String target) {
    assertCanAddAttribute().setTarget(target);
    return this;
  }

  @Override
  public LinkBuilder type(String type) {
    assertCanAddAttribute().setType(type);
    return this;
  }
}
