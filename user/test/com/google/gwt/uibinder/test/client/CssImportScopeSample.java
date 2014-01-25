/*
 * Copyright 2009 Google Inc.
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
package com.google.gwt.uibinder.test.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.ImportedWithPrefix;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

/**
 * Odd widget demonstrates UiBinder's integration with CssResource's obscure but
 * crucial imported scopes feature.
 */
public class CssImportScopeSample extends Widget implements HasText {
  interface Binder extends UiBinder<DivElement, CssImportScopeSample> {
  }
  private static final Binder binder = GWT.create(Binder.class);

  interface Bundle extends ClientBundle {
    @Source("CssImportScopeSample.css")
    InnerStyle innerStyle();

    @Source("CssImportScopeSample.css")
    OuterStyle style();
  }

  @ImportedWithPrefix("inner")
  interface InnerStyle extends Style {
  }

  @ImportedWithPrefix("outer")
  interface OuterStyle extends Style {
  }

  interface Style extends CssResource {
    String body();
  }

  @UiField(provided = true)
  Bundle bundle = GWT.create(Bundle.class);
  @UiField
  Element inner;

  @UiField
  Element outer;

  CssImportScopeSample() {
    bundle.style().ensureInjected();
    bundle.innerStyle().ensureInjected();
    setElement(binder.createAndBindUi(this));
  }

  @Override
  public String getText() {
    return outer.getInnerText();
  }

  @Override
  public void setText(String text) {
    outer.setInnerText(text);
  }

  public void setWrappedText(String text) {
    inner.setInnerText(text);
  }
}
