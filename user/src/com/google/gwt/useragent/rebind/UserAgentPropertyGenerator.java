/*
 * Copyright 2011 Google Inc.
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

package com.google.gwt.useragent.rebind;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.core.ext.linker.PropertyProviderGenerator;
import com.google.gwt.thirdparty.guava.common.base.Function;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.user.rebind.StringSourceWriter;
import com.google.gwt.useragent.rebind.UserAgentSelectorWriter.ConfigPropertyAccessor;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * Generator which writes out the JavaScript for determining the value of the {@code user.agent}
 * selection property.
 */
public class UserAgentPropertyGenerator implements PropertyProviderGenerator {

  @Override
  public String generate(TreeLogger logger, SortedSet<String> possibleValues, String fallback,
      final SortedSet<ConfigurationProperty> configProperties) throws UnableToCompleteException {

    StringSourceWriter body = new StringSourceWriter();
    body.println("{");
    body.indent();
    ConfigPropertyAccessor accessor = toConfigAccessor(configProperties);
    new UserAgentSelectorWriter(logger, accessor).write(body, possibleValues);
    body.outdent();
    body.println("}");

    return body.toString();
  }

  private ConfigPropertyAccessor toConfigAccessor(Set<ConfigurationProperty> configProperties) {
    final Map<String, ConfigurationProperty> configIndex =
        Maps.uniqueIndex(configProperties, getConfigNameExtractor());
    return new ConfigPropertyAccessor() {
      @Override
      public String getValue(String name) throws BadPropertyValueException {
        ConfigurationProperty config = configIndex.get(name);
        if (config == null) {
          throw new BadPropertyValueException(name);
        }
        return config.getValues().get(0);
      }
    };
  }

  private Function<ConfigurationProperty, String> getConfigNameExtractor() {
    return new Function<ConfigurationProperty, String>() {
      @Override
      public String apply(ConfigurationProperty config) {
        return config.getName();
      }
    };
  }
}
