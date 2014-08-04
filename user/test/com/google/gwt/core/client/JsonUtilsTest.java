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
package com.google.gwt.core.client;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * Tests {@link JsonUtils}.
 */
public class JsonUtilsTest extends GWTTestCase {
  interface TestResources extends ClientBundle {
    @Source("com/google/gwt/core/client/testdata/jsonFileNormal.json")
    TextResource jsonFileNormal();
  }

  private String jsonFileNormal;
  private String jsonFileWithSuperLongLine;
  private String jsonFileWithSuperLongLineNoQuotes;

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.Core";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    TestResources resources = GWT.create(TestResources.class);
    jsonFileNormal = resources.jsonFileNormal().getText();
    jsonFileWithSuperLongLine = getJsonWithSuperLongLines(true);
    jsonFileWithSuperLongLineNoQuotes = getJsonWithSuperLongLines(false);
  }

  public void testStringify() throws Exception {
    if (isFirefox40OrEarlier()) {
      return;
    }
    assertEquals("{\"a\":2}", JsonUtils.stringify(createJson()));
    assertEquals("{\n\t\"a\": 2\n}", JsonUtils.stringify(createJson(), "\t"));
    assertEquals("{\nXYZ\"a\": 2\n}", JsonUtils.stringify(createJson(), "XYZ"));
  }

  private native JavaScriptObject createJson() /*-{
    return { a: 2 };
  }-*/;

  private static native boolean isFirefox40OrEarlier() /*-{
    return @com.google.gwt.dom.client.DOMImplMozilla::isGecko2OrBefore()();
  }-*/;

  /**
   * This is not a thorough test, but it makes sure that we detect some safe and unsafe json
   * examples.
   */
  public void testSafeToEval() throws Exception {
    assertSafeToEval(true, jsonFileNormal);

    assertSafeToEval(true, "{\"key\": 23}");
    assertSafeToEval(true, "{\n\t\"key\": 23\n}");
    assertSafeToEval(true, "{\n\t\"key\": 23.4\r\n}");
    assertSafeToEval(false, "{\"key\": value}");

    // Completely invalid, but safe to eval.
    assertSafeToEval(true, ",:{}[]0123456789.-+Eaeflnrstu \n\r\t");

    // The backslash and the double quotes are special in the code because they
    // change state. So we test them specially.
    assertSafeToEval(false, "\\");
    assertSafeToEval(false, "\"");

    // Some illegal characters.
    assertSafeToEval(false, "()");
    assertSafeToEval(false, "call()");
    // Which are fine if between double quotes.
    assertSafeToEval(true, "\"()\"");
    assertSafeToEval(true, "\"call()\"");
    // Single quote strings are not allowed.
    assertSafeToEval(false, "'()'");
    assertSafeToEval(false, "'call()'");
    // Unless they are inside double quotes, of course.
    assertSafeToEval(true, "\"'()'\"");
    assertSafeToEval(true, "\"'call()'\"");

    assertSafeToEval(false, " \" unterminated double quote");
    assertSafeToEval(false, " \" unterminated double quote with escaped one at the end \\\"");
    assertSafeToEval(true,
        " \" properly terminated double quote with escaped one at the end \\\"\"");
  }

  public void testSafeToEvalWith() throws Exception {
    assertTrue("Unexpected safeToEval result for: '" + jsonFileWithSuperLongLine + "'",
        JsonUtils.safeToEval(jsonFileWithSuperLongLine));
    assertTrue("Unexpected safeToEval result for: '" + jsonFileWithSuperLongLineNoQuotes + "'",
        JsonUtils.safeToEval(jsonFileWithSuperLongLineNoQuotes));
  }

  /**
   * Calls both {@link JsonUtils#safeToEval(String)} and {@link #safeToEvalViaRegexp(String)} and
   * make sure they both provided the expected result.
   */
  private void assertSafeToEval(boolean expectedResult, String input) {
    assertEquals("Unexpected safeToEval result for: '" + input + "'",
        expectedResult, JsonUtils.safeToEval(input));
    assertEquals("Doesn't match the regexp: '" + input + "'",
        expectedResult, safeToEvalViaRegexp(input));
  }

  /**
   * Similar to {@link JsonUtils#safeToEval(String)} but using the regular expressions suggested by
   * <a href="http://www.ietf.org/rfc/rfc4627.txt">RFC 4627</a>. We keep this here to validate in
   * the test that doing it without regular expressions has the same effect.
   */
  private static native boolean safeToEvalViaRegexp(String text) /*-{
    // Remove quoted strings and disallow anything except:
    //
    // 1) symbols and brackets ,:{}[]
    // 2) numbers: digits 0-9, ., -, +, e, and E
    // 3) literal values: 'null', 'true' and 'false' = [aeflnr-u]
    // 4) whitespace: ' ', '\n', '\r', and '\t'
    return !(/[^,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]/.test(text.replace(/"(\\.|[^"\\])*"/g, '')));
  }-*/;

  /**
   * Dynamically creates a json file that, if withDoubleQuotesText is true, will contain a super
   * long string.
   *
   * @param withDoubleQuotesText determines if the json will have the keys and values or not (aka
   *        text bewteen double quotes)
   */
  private String getJsonWithSuperLongLines(boolean withDoubleQuotesText) {
    StringBuilder sb = new StringBuilder("{\n");
    sb.append("  ");
    if (withDoubleQuotesText) {
      sb.append("\"key\"");
    }
    sb.append(" : ");
    if (withDoubleQuotesText) {
      sb.append("\"");
      String pieceOfStringToBeRepeated =
          "\\\"Lorem ipsum dolor sit amet, consectetur adipisicing elit.\\\"";
      int desiredTextLength = 2500000;
      for (int i = 0; i < desiredTextLength / pieceOfStringToBeRepeated.length(); i++) {
        sb.append(pieceOfStringToBeRepeated);
      }
      sb.append("\"");
    }
    sb.append("\n}\n");
    return sb.toString();
  }
}
