/*
 * Copyright 2016 Google Inc.
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
package com.google.gwt.safehtml.shared.annotations;

/**
 * Annotates methods that could not be automatically refactored to use {@link IsSafeHtml} correctly.
 * <p>
 * This annotation is only supposed to be added by automated refactoring tools to mark locations
 * where code cannot be automatically refactored to use {@link IsSafeHtml} correctly. These
 * should be manually reviewed and be changed to use either {@link SuppressIsSafeHtmlCastCheck} or
 * the {@link com.google.gwt.safehtml.shared.SafeHtml} type.
 * <p>
 * This annotation marks methods in which an expression without a
 * {@link com.google.gwt.safehtml.shared.annotations.IsSafeHtml} annotation is used in a context
 *  where such an annotation is required (e.g., the return statement of a method that
 * returns {@code @IsSafeHtml String}).
 * <p>
 * As such, use of this annotation marks code that is potentially prone to HTML-injection
 * vulnerabilities, and which hence needs to be carefully security reviewed or replaced with a safe
 * construct.
 */
public @interface SuppressIsSafeHtmlCastCheckLegacy {

}

