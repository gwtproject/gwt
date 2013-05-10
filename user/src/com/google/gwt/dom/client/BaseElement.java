/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.dom.client;

import com.google.gwt.safehtml.shared.SafeUri;

/**
 * Document base URI.
 * 
 * @see <a href="http://www.w3.org/TR/1999/REC-html401-19991224/struct/links.html#edef-BASE">W3C HTML Specification</a>
 */
@TagName(BaseElement.TAG)
public class BaseElement extends Element {

  public static final String TAG = "base";

  /**
   * Assert that the given {@link Element} is compatible with this class and
   * automatically typecast it.
   */
  public static BaseElement as(Element elem) {
    assert elem.getTagName().equalsIgnoreCase(TAG);
    return (BaseElement) elem;
  }

  protected BaseElement() {
  }

  /**
   * The base URI. See the href attribute definition in HTML
   * 4.01.
   * 
   * @see <a href="http://www.w3.org/TR/1999/REC-html401-19991224/struct/links.html#adef-href-BASE">W3C HTML Specification</a>
   */
  public final native String getHref() /*-{
     return this.href;
   }-*/;

  /**
   * The default target frame.
   * 
   * @see <a href="http://www.w3.org/TR/1999/REC-html401-19991224/present/frames.html#adef-target">W3C HTML Specification</a>
   */ 
  public final native String getTarget() /*-{
     return this.target;
   }-*/;

  /**
   * The base URI. See the href attribute definition in HTML
   * 4.01.
   * 
   * @see <a href="http://www.w3.org/TR/1999/REC-html401-19991224/struct/links.html#adef-href-BASE">W3C HTML Specification</a>
   */
  public final native void setHref(String href) /*-{
     this.href = href;
   }-*/;

  /**
   * The base URI. See the href attribute definition in HTML
   * 4.01.
   * 
   * @see <a href="http://www.w3.org/TR/1999/REC-html401-19991224/struct/links.html#adef-href-BASE">W3C HTML Specification</a>
   */
  public final void setSafeHref(SafeUri href) {
    setHref(href.asString());
  }

  /**
   * The default target frame.
   * 
   * @see <a href="http://www.w3.org/TR/1999/REC-html401-19991224/present/frames.html#adef-target">W3C HTML Specification</a>
   */ 
  public final native void setTarget(String target) /*-{
     this.target = target;
   }-*/;
}
