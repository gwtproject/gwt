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

    @Source("com/google/gwt/core/client/testdata/jsonFileNormalNoQuotes.json")
    TextResource jsonFileNormalNoQuotes();
  }

  private String jsonFileNormal;
  private String jsonFileNormalNoQuotes;
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
    jsonFileNormalNoQuotes = resources.jsonFileNormalNoQuotes().getText();
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
    assertSafeToEval(true, jsonFileNormalNoQuotes);
    assertSafeToEval(false, "{\"key\": value}");
  }

  public void testRemoveAllQuotedText() {
    // Some happy paths.
    assertRemoveAllQuotedText("", "");
    assertRemoveAllQuotedText(jsonFileNormalNoQuotes, jsonFileNormal);
    assertRemoveAllQuotedText(jsonFileNormalNoQuotes, jsonFileNormalNoQuotes);

    // If the double quoted string begins with an escape slash, we still remove it.
    assertRemoveAllQuotedText("\\", "\\\"blah\"");
    assertRemoveAllQuotedText("something\\", "something\\\"blah\"");

    // If the double quotes are not closed, we don't remove it.
    assertRemoveAllQuotedText("\"blah", "\"blah");

    // Edge cases for where the quoted text is.
    assertRemoveAllQuotedText("", "\"all is quoted\"");
    assertRemoveAllQuotedText("END", "\"begins with quoted\"END");
    assertRemoveAllQuotedText("BEGIN", "BEGIN\"ends with quoted\"");

    // Anything outside double quotes will stay. Also, escapes don't matter here.
    assertRemoveAllQuotedText("blah':,.\\a blah ", "blah':,.\\a blah \"quoted\\\"\"");

    // Empty quotes should be removed as well
    assertRemoveAllQuotedText("", "\"\"");
    assertRemoveAllQuotedText("blahblah", "blah\"\"blah");
  }

  public void testRemoveAllQuotedTextSuperLongLineIterativeWay() throws Exception {
    assertEquals(jsonFileWithSuperLongLineNoQuotes,
        JsonUtils.removeAllQuotedText(jsonFileWithSuperLongLine));
  }

  /**
   * Calls the method {@link JsonUtils#removeAllQuotedText(String)} and assert the result is as
   * expected. It also calls {@link #removeAllQuotedTextViaRegexp(String)} to ensure that both
   * methods behave the same way.
   */
  private void assertRemoveAllQuotedText(String expectedOutput, String input) {
    String realOutput = JsonUtils.removeAllQuotedText(input);
    String regexpOutput = removeAllQuotedTextViaRegexp(input);
    assertEquals(
        "The output from removeAllQuotedText should be the same as "
        + "removeAllQuotedTextViaRegexp for input: '" + input + "'", regexpOutput, realOutput);
    assertEquals("For input '" + input + "'", expectedOutput, realOutput);
  }

  /**
   * Does the same as {@link JsonUtils#removeAllQuotedText(String)} but using the regular expression
   * we want to imitate. This allow us to test that the function mentioned does exactly the same as
   * the regular expression suggested by <a href="http://www.ietf.org/rfc/rfc4627.txt">RFC 4627</a>.
   */
  private static native String removeAllQuotedTextViaRegexp(String input) /*-{
    // Remove quoted strings with a regexp:
    return input.replace(/"(\\.|[^"\\])*"/g, '');
  }-*/;
  
  /**
   * Calls both {@link JsonUtils#safeToEval(String)} and {@link #safeToEvalViaRegexp(String)} and
   * make sure they both provided the expected result.
   */
  private void assertSafeToEval(boolean expectedResult, String input) {
    assertEquals(input, expectedResult, safeToEvalViaRegexp(input));
    assertEquals(input, expectedResult, JsonUtils.safeToEval(input));
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
