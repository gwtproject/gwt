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

package com.google.gwt.useragent.rebind;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.user.rebind.SourceWriter;

import java.util.Set;

/**
 * A utility that writes javascript to determine the value of {@code user.agent} based on
 * configuration properties.
 * <p>
 * This utility assumes there exists a {@code user.agent.$X.predicate} for each '$X' value that
 * {@code user.agent} property could take. The value of each predicate should be a javascript code
 * snippet that processes {@code navigator.userAgent} available to access via local variable
 * {@code ua} and returns {@code true} if it matches to corresponding user agent. E.g.
 * <pre>
 *   {@literal <}set-configuration-property
 *     name="user.agent.gecko1_8.predicate"
 *     value="return (ua.indexOf('gecko') != -1);" /{@literal >}
 * </pre>
 */
class UserAgentSelectorWriter {

  interface ConfigPropertyAccessor {
    String getValue(String name) throws BadPropertyValueException;
  }

  private final TreeLogger logger;
  private final ConfigPropertyAccessor accessor;

  public UserAgentSelectorWriter(TreeLogger logger, ConfigPropertyAccessor accessor) {
    this.logger = logger;
    this.accessor = accessor;
  }

  /**
   * Writes out the JavaScript function body for determining the value of the {@code user.agent}
   * selection property.
   */
  public void write(SourceWriter body, Set<String> agents) throws UnableToCompleteException {
    // write preamble
    body.println("var ua = navigator.userAgent.toLowerCase();");

    for (String userAgent : agents) {
      body.println("if ((function() { ");
      body.indentln(getPredicate(userAgent));
      body.println("})()) return '%s';", userAgent);
    }

    // default return
    body.println("return 'unknown';");
  }

  private String getPredicate(String agent) throws UnableToCompleteException {
    String predicateKey = String.format("user.agent.%s.predicate", agent);
    try {
      return accessor.getValue(predicateKey);
    } catch (BadPropertyValueException e) {
      logger.log(TreeLogger.ERROR, "Predicate uknown for user agent:" + agent);
      throw new UnableToCompleteException();
    }
  }
}
