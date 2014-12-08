/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev;

import java.util.Collection;

/**
 * Test for {@ArgHandlerRestrictBindingProperties}.
 */
public class ArgHandlerRestrictPropertiesTest extends ArgProcessorTestBase {
  private final Compiler.ArgProcessor argProcessor;
  private final CompilerOptionsImpl options = new CompilerOptionsImpl();

  public ArgHandlerRestrictPropertiesTest() {
    argProcessor = new Compiler.ArgProcessor(options);
  }

  public void testSinglePropertySingleValue() {
    assertProcessSuccess(argProcessor,
        new String[] {"-properties", "locale=zh", "my.Module"});
    assertEquals("{locale=[zh]}", options.getRestrictedProperties().toString());
  }

  public void testMultiplePropertiesSingleValue() {
    assertProcessSuccess(argProcessor, new String[] {
        "-properties", "locale=zh,user.agent=safari,stackTraces=false", "my.Module"});
    assertEquals(3, options.getRestrictedProperties().keySet().size());
    assertEquals("[zh]", options.getRestrictedProperties().get("locale").toString());
    assertEquals("[safari]", options.getRestrictedProperties().get("user.agent").toString());
    assertEquals("[false]", options.getRestrictedProperties().get("stackTraces").toString());
  }

  public void testSinglePropertyMultipleValues() {
    assertProcessSuccess(argProcessor,
        new String[] {"-properties", "locale=zh,locale=en", "my.Module"});
    assertEquals(1, options.getRestrictedProperties().keySet().size());
    Collection<String> locales = options.getRestrictedProperties().get("locale");
    assertEquals(2, locales.size());
    assertTrue(locales.contains("zh") && locales.contains("en"));
  }

  public void testMultiplePropertiesMultipleValues() {
    assertProcessSuccess(argProcessor, new String[] {
        "-properties",
        "locale=zh,user.agent=safari,locale=en,user.agent=opera,stackTraces=true,stackTraces=false",
        "my.Module"});
    assertEquals(3, options.getRestrictedProperties().keySet().size());

    Collection<String> locales = options.getRestrictedProperties().get("locale");
    Collection<String> userAgents = options.getRestrictedProperties().get("user.agent");
    Collection<String> stackTraces = options.getRestrictedProperties().get("stackTraces");
    assertEquals(2, locales.size());
    assertTrue(locales.contains("zh") && locales.contains("en"));
    assertEquals(2, userAgents.size());
    assertTrue(userAgents.contains("opera") && userAgents.contains("safari"));
    assertEquals(2, stackTraces.size());
    assertTrue(stackTraces.contains("false") && stackTraces.contains("true"));
  }
}
