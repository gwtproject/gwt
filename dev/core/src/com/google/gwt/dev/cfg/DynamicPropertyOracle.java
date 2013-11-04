/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.dev.cfg;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.DefaultConfigurationProperty;
import com.google.gwt.core.ext.DefaultSelectionProperty;
import com.google.gwt.core.ext.LimitablePropertyOracle;
import com.google.gwt.core.ext.SelectionProperty;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableSet;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * An implementation of {@link LimitablePropertyOracle} that helps discover the property
 * values associated with specific rebind results for generators.<br />
 *
 * It does so by recording the properties that are queried, providing a first legal answer for
 * properties not previously queried and allowing an external driver to prescribe values for
 * properties that have been discovered as dependencies.
 */
public class DynamicPropertyOracle implements LimitablePropertyOracle {

  private static SelectionProperty createSelectionProperty(
      String value, BindingProperty bindingProperty) {
    SortedSet<String> possibleValues =
        Sets.newTreeSet(Arrays.asList(bindingProperty.getDefinedValues()));
    return new DefaultSelectionProperty(value, bindingProperty.getFallback(),
        bindingProperty.getName(), possibleValues, bindingProperty.getFallbackValuesMap());
  }

  private final Set<BindingProperty> accessedProperties = Sets.newHashSet();
  private boolean accessedPropertiesChanged;
  private ImmutableSet<String> accessiblePropertyNames;
  private final Map<String, String> prescribedPropertyValuesByName = Maps.newHashMap();
  private final Properties properties;

  public DynamicPropertyOracle(Properties properties) {
    this.properties = properties;
  }

  public Set<BindingProperty> getAccessedProperties() {
    return accessedProperties;
  }

  @Override
  public ConfigurationProperty getConfigurationProperty(String propertyName)
      throws BadPropertyValueException {
    PropertyOracles.checkPropertyAccess(accessiblePropertyNames, propertyName);

    Property property = properties.find(propertyName);
    if (property instanceof ConfigurationProperty) {
      ConfigurationProperty configurationProperty = (ConfigurationProperty) property;
      return new DefaultConfigurationProperty(
          configurationProperty.getName(), configurationProperty.getValues());
    }
    throw new BadPropertyValueException(propertyName);
  }

  /**
   * Returns the mapping from property names to its currently prescribed value. The internal mapping
   * changes between runs so the returned value is a stable copy.
   */
  public Map<String, String> getPrescribedPropertyValuesByName() {
    return Maps.newHashMap(prescribedPropertyValuesByName);
  }

  @Override
  public SelectionProperty getSelectionProperty(TreeLogger logger, String propertyName)
      throws BadPropertyValueException {
    PropertyOracles.checkPropertyAccess(accessiblePropertyNames, propertyName);

    BindingProperty bindingProperty = getBindingProperty(propertyName);
    accessedPropertiesChanged |= accessedProperties.add(bindingProperty);

    String propertyValue = prescribedPropertyValuesByName.isEmpty()
        ? bindingProperty.getFirstLegalValue() : prescribedPropertyValuesByName.get(propertyName);
    return createSelectionProperty(propertyValue, bindingProperty);
  }

  public boolean haveAccessedPropertiesChanged() {
    return accessedPropertiesChanged;
  }

  public void prescribePropertyValue(String propertyName, String propertyValue) {
    prescribedPropertyValuesByName.put(propertyName, propertyValue);
  }

  /**
   * Clears state in preparation for another round of rebind analysis on the same generator. Since
   * some per generator state is not cleared it is necessary to create a new instance per generator.
   */
  public void reset() {
    accessedPropertiesChanged = false;
    prescribedPropertyValuesByName.clear();
  }

  @Override
  public void setAccessiblePropertyNames(ImmutableSet<String> accessiblePropertyNames) {
    this.accessiblePropertyNames = accessiblePropertyNames;
  }

  private BindingProperty getBindingProperty(String propertyName) throws BadPropertyValueException {
    Property property = properties.find(propertyName);
    if (property instanceof BindingProperty) {
      return (BindingProperty) property;
    }
    throw new BadPropertyValueException(propertyName);
  }
}
