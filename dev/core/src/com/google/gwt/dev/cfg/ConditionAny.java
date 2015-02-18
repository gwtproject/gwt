/*
 * Copyright 2006 Google Inc.
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

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.thirdparty.guava.common.base.Objects;

import java.util.Iterator;

/**
 * A compound condition that is satisfied if any of its children are satisfied.
 */
public class ConditionAny extends CompoundCondition {

  public ConditionAny(Condition... conditions) {
    super(conditions);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof ConditionAny) {
      ConditionAny that = (ConditionAny) object;
      return Objects.equal(this.conditions, that.conditions);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(conditions);
  }

  @Override
  protected boolean doEval(TreeLogger logger, DeferredBindingQuery query)
      throws UnableToCompleteException {
    for (Iterator<Condition> iter = getConditions().iterator(); iter.hasNext();) {
      Condition condition = iter.next();
      if (condition.isTrue(logger, query)) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected String getEvalAfterMessage(String testType, boolean result) {
    if (result) {
      return "Yes: One or more subconditions was true";
    } else {
      return "No: All subconditions were false";
    }
  }

  @Override
  protected String getEvalBeforeMessage(String testType) {
    return "Checking if any subcondition is true (<any>)";
  }
}
