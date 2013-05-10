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
package com.google.gwt.dom.builder.shared;

import com.google.gwt.safehtml.shared.SafeUri;

/**
 * Builds an mod element.
 */
public interface ModBuilder extends ElementBuilderBase<ModBuilder> {

  /**
   * A URI designating a document that describes the reason for the change.
   *
   * @see <a href="http://www.w3.org/TR/1999/REC-html401-19991224/">W3C HTML Specification</a>
   */
  ModBuilder cite(SafeUri cite);

  /**
   * A URI designating a document that describes the reason for the change.
   *
   * @see <a href="http://www.w3.org/TR/1999/REC-html401-19991224/">W3C HTML Specification</a>
   */
  ModBuilder cite(String cite);

  /**
   * The date and time of the change.
   * 
   * @see <a href="http://www.w3.org/TR/1999/REC-html401-19991224/struct/text.html#adef-datetime">W3C HTML Specification</a>
   */
  ModBuilder dateTime(String dateTime);
}
