/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.dev.cfg;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableSet;

import junit.framework.TestCase;

/**
 * Tests for DynamicPropertyOracle.
 */
public class DynamicPropertyOracleTest extends TestCase {

  public void testProcess() throws BadPropertyValueException {
    // Setups up.
    Properties properties = new Properties();
    BindingProperty userAgentProperty = properties.createBinding("user.agent");
    BindingProperty localeProperty = properties.createBinding("locale");
    DynamicPropertyOracle dynamicPropertyOracle = new DynamicPropertyOracle(properties);

    // Verifies baseline state.
    assertFalse(dynamicPropertyOracle.haveAccessedPropertiesChanged());
    assertTrue(dynamicPropertyOracle.getAccessedProperties().isEmpty());

    // Finds XML Fallback.
    userAgentProperty.setFallback("mozilla");
    assertEquals("mozilla",
        dynamicPropertyOracle.getSelectionProperty(null, "user.agent").getCurrentValue());
    assertTrue(dynamicPropertyOracle.haveAccessedPropertiesChanged());
    assertEquals(1, dynamicPropertyOracle.getAccessedProperties().size());

    // Finds XML Constrained.
    dynamicPropertyOracle.reset();
    userAgentProperty.addDefinedValue(new ConditionWhenLinkerAdded("foo"), "webkit");
    userAgentProperty.addDefinedValue(new ConditionWhenLinkerAdded("bar"), "webkit");
    userAgentProperty.addDefinedValue(new ConditionWhenLinkerAdded("baz"), "webkit");
    assertEquals(
        "webkit", dynamicPropertyOracle.getSelectionProperty(null, "user.agent").getCurrentValue());
    assertFalse(dynamicPropertyOracle.haveAccessedPropertiesChanged());
    assertEquals(1, dynamicPropertyOracle.getAccessedProperties().size());

    // Finds first defined.
    dynamicPropertyOracle.reset();
    localeProperty.addDefinedValue(new ConditionWhenLinkerAdded("qwer"), "en");
    localeProperty.addDefinedValue(new ConditionWhenLinkerAdded("asdf"), "fr");
    localeProperty.addDefinedValue(new ConditionWhenLinkerAdded("zxcv"), "ru");
    assertEquals(
        "en", dynamicPropertyOracle.getSelectionProperty(null, "locale").getCurrentValue());
    assertTrue(dynamicPropertyOracle.haveAccessedPropertiesChanged());
    assertEquals(2, dynamicPropertyOracle.getAccessedProperties().size());

    // Finds permutation prescribed.
    dynamicPropertyOracle.reset();
    dynamicPropertyOracle.prescribePropertyValue("user.agent", "redbull");
    assertEquals("redbull",
        dynamicPropertyOracle.getSelectionProperty(null, "user.agent").getCurrentValue());
    assertFalse(dynamicPropertyOracle.haveAccessedPropertiesChanged());
    assertEquals(2, dynamicPropertyOracle.getAccessedProperties().size());

    // Reset clears prescription.
    dynamicPropertyOracle.reset();
    assertEquals(
        "webkit", dynamicPropertyOracle.getSelectionProperty(null, "user.agent").getCurrentValue());
    assertFalse(dynamicPropertyOracle.haveAccessedPropertiesChanged());
    assertEquals(2, dynamicPropertyOracle.getAccessedProperties().size());
  }

  public void testRejectsIllegalPropertyAccesses() throws BadPropertyValueException {
    String propertyName = "user.agent";
    String expectedPropertyValue = "ie6";

    // Setups up a property oracle that knows about user.agent.
    Properties properties = new Properties();
    properties.createBinding(propertyName);
    DynamicPropertyOracle dynamicPropertyOracle = new DynamicPropertyOracle(properties);
    dynamicPropertyOracle.prescribePropertyValue(propertyName, expectedPropertyValue);

    // Makes it specifically legal to access user.agent
    dynamicPropertyOracle.setAccessiblePropertyNames(ImmutableSet.of(propertyName));

    // Shows a successful access.
    assertEquals(expectedPropertyValue,
        dynamicPropertyOracle.getSelectionProperty(null, propertyName).getCurrentValue());

    // Makes it illegal to access user.agent
    dynamicPropertyOracle.setAccessiblePropertyNames(ImmutableSet.of("something else"));

    // Shows access now fails as expected.
    try {
      dynamicPropertyOracle.getSelectionProperty(null, propertyName);
      fail("user.agent property access should have failed");
    } catch (IllegalStateException e) {
      // expected behavior
    }

    // Makes it legal to access anything
    dynamicPropertyOracle.setAccessiblePropertyNames(null);

    // Shows a successful access.
    assertEquals(expectedPropertyValue,
        dynamicPropertyOracle.getSelectionProperty(null, propertyName).getCurrentValue());
  }
}
