/*
 * Copyright 2015 Google Inc.
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
package com.google.gwt.safehtml.shared;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

//import com.google.errorprone.annotations.LatticeType;
//import com.google.errorprone.annotations.LatticeType.AllowCast;
//import com.google.errorprone.annotations.TypeLattice;
//import com.google.errorprone.annotations.TypeLattice.MaturityLevel;
//import com.google.errorprone.annotations.TypeLattice.SeverityLevel;
//import com.google.errorprone.annotations.TypeLattice.Suppressibility;

//TODO: Move this to its own package once I figure out the build system
import java.lang.annotation.Target;

/**
 * Annotations to mark properly escaped HTML
 */
//@TypeLattice(
//  name = "HtmlAnnotation",
//  summary = "Make sure only safe HTML strings are passed to HTML Apis",
//  severity = SeverityLevel.WARNING,
//  maturity = MaturityLevel.EXPERIMENTAL,
//  suppressibility = Suppressibility.CUSTOM_ANNOTATION,
//  customSuppressionAnnotation = SuppressHTMLAnnotationErrors.class,
//  suppressOnPath = ".*(trunk/user/test/|trunk/samples/showcase/).*"
//)
public class Types {
  /**
   * A type annotation that represents values that are safe to use in a HTML context.
   *
   * The annotated type {@code @Html String} and the type {@code SafeHtml} are semantically 
   * equivalent.
   */
//  @LatticeType(
//    Unlabelled = AllowCast.TO,
//    AllowCastFrom = {},
//    SilentCastFromMatcher = "com.google.errorprone.matchers.SafeHtmlExpressionMatcher"
//  )
  @Target({PARAMETER, METHOD, FIELD, LOCAL_VARIABLE})
  public @interface IsSafeHtml {
  }
}
