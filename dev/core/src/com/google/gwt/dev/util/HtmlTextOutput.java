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
package com.google.gwt.dev.util;

import java.io.PrintWriter;
import java.util.regex.Pattern;

/**
 * An implementation of TextOutput that will produce HTML-escaped output.
 */
public class HtmlTextOutput extends AbstractTextOutput {
  private static final Pattern ESCAPE_PATTERN = Pattern.compile("[&<>'\"]");

  /**
   * Escapes any characters that need to be escaped for HTML output - &, <>, ', and ".
   */
  private static String escapeHtml(String s) {
    if (s == null) {
      return null;
    }
    return ESCAPE_PATTERN.matcher(s).replaceAll(match -> {
      switch (match.group()) {
        case "&":
          return "&amp;";
        case "<":
          return "&lt;";
        case ">":
          return "&gt;";
        case "'":
          return "&apos;";
        case "\"":
          return "&quot;";
        default:
          throw new IllegalStateException("Unexpected match: " + match.group());
      }
    });
  }

  public HtmlTextOutput(PrintWriter out, boolean compact) {
    super(compact);
    setPrintWriter(out);
  }

  @Override
  public void print(char c) {
    print(String.valueOf(c));
  }

  @Override
  public void print(char[] s) {
    print(String.valueOf(s));
  }

  @Override
  public void print(String s) {
    super.print(escapeHtml(s));
  }

  @Override
  public void printOpt(char c) {
    printOpt(String.valueOf(c));
  }

  @Override
  public void printOpt(char[] s) {
    printOpt(String.valueOf(s));
  }

  @Override
  public void printOpt(String s) {
    super.printOpt(escapeHtml(s));
  }

  /**
   * Print unescaped data into the output.
   */
  public void printRaw(String s) {
    super.print(s);
  }
}