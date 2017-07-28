/*
 * Copyright 2017 Google Inc.
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

import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.CspViolation;
import com.google.gwt.junit.client.ExpectedFailure;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * This test case checks that the test runner can correctly catch CSP violations initiated from 
 * unit tests.
 */
public class CspTest extends GWTTestCase {
  
  @Override
  public String getModuleName() {
    return "com.google.gwt.dom.DOMTest";
  }
  
  @DoNotRunWith(Platform.HtmlUnitLayout)
  @ExpectedFailure(withType = CspViolation.class)
  public void testCsp() {
    ButtonElement btn = Document.get().createPushButtonElement();
    btn.setAttribute("onclick", "1");
    btn.click();
  }
  
}
