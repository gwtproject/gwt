/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.resources.converter;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.thirdparty.common.css.SourceCode;
import com.google.gwt.thirdparty.common.css.compiler.ast.GssParser;
import com.google.gwt.thirdparty.common.css.compiler.ast.GssParserException;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Integration tests for Css2Gss.
 */
public class Css2GssTest extends TestCase {

  public void testInlineBlockCssEscaping() throws Exception {
    assertFileContentEqualsAfterConversion("inline-block.css", "inline-block.gss");
  }

  public void testMultipleDeclarationOfSameProperty() throws Exception {
    assertFileContentEqualsAfterConversion("multiple_declarations.css",
        "multiple_declarations.gss");
  }

  public void testCssConditional() throws Exception {
    assertFileContentEqualsAfterConversion("conditional.css", "conditional.gss");
  }

  public void testLenientFlag() throws Exception {
    assertFileContentEqualsAfterConversionAndIsGssCompatible("badRule.css", "badRule.gss", true);
  }

  public void testExternalMissingComma() throws Exception {
    assertFileContentEqualsAfterConversionAndIsGssCompatible("external-bug.css", "external-bug.gss",
        true);
  }

  public void testSprite() throws Exception {
    assertFileContentEqualsAfterConversion("sprite.css", "sprite.gss");
  }

  public void testFontFamily() throws Exception {
    assertFileContentEqualsAfterConversion("font-bug.css", "font-bug.gss");
  }

  public void testExternalBug() throws Exception {
    assertFileContentEqualsAfterConversionAndIsGssCompatible("external-bug.css", "external-bug.gss",
        true);
  }

  public void testUndefinedConstant() throws Exception {
    assertFileContentEqualsAfterConversionAndIsGssCompatible(
        "undefined-constants.css", "undefined-constants.gss", true);
  }

  public void testRemoveExternalEscaping() throws Exception {
    assertFileContentEqualsAfterConversion(
        "external-escaping.css", "external-escaping.gss");
  }

  public void testNestedConditional() throws Exception {
    assertFileContentEqualsAfterConversion(
        "nestedElseIf.css", "nestedElseIf.gss");
  }

  public void testConstants() throws Exception {
    assertFileContentEqualsAfterConversionAndIsGssCompatible(
        "constants.css", "constants.gss", true);
  }

  public void testInvalidConstantName() throws IOException, UnableToCompleteException {
    assertFileContentEqualsAfterConversionAndIsGssCompatible(
        "invalidConstantName.css", "invalidConstantName.gss", true);
  }

  public void testEscaping() throws IOException, UnableToCompleteException {
    assertFileContentEqualsAfterConversionAndIsGssCompatible("escape.css", "escape.gss", true);
  }

  private void assertFileContentEqualsAfterConversion(String inputCssFile, String expectedGssFile)
      throws IOException, UnableToCompleteException {
    assertFileContentEqualsAfterConversionAndIsGssCompatible(inputCssFile, expectedGssFile, false);
  }

  private void assertFileContentEqualsAfterConversionAndIsGssCompatible(String inputCssFile,
      String expectedGssFile, boolean lenient) throws IOException, UnableToCompleteException {
    URL resource = Css2GssTest.class.getResource(inputCssFile);
    InputStream stream = Css2GssTest.class.getResourceAsStream(expectedGssFile);
    String convertedGss = new Css2Gss(resource, lenient).toGss();
    String gss = IOUtils.toString(stream, "UTF-8");
    Assert.assertEquals(gss, convertedGss);

    // assert the convertedGss is compatible with GSS
    try {
      new GssParser(new SourceCode("[conversion of " + inputCssFile + "]", convertedGss)).parse();
    } catch (GssParserException e) {
      e.printStackTrace();
      fail("The conversion produces invalid GSS code.");
    }
  }
}
