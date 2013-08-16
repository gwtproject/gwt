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
package com.google.gwt.dev.jjs.ast;

import com.google.gwt.dev.jjs.SourceInfo;

/**
 * Represents a @GwtCreate parameter.
 */
public class JGwtCreateParameter extends JParameter {

  private JParameter factoryParam;

  public JGwtCreateParameter(SourceInfo info, String name, JType type, boolean isFinal,
      boolean isThis, JMethod enclosingMethod) {
    super(info, name, type, isFinal, isThis, enclosingMethod);
  }

  public JParameter getFactoryParam() {
    return factoryParam;
  }

  public void setFactoryParam(JParameter factoryParam) {
    assert factoryParam.getEnclosingMethod().equals(getEnclosingMethod());
    this.factoryParam = factoryParam;
  }
}
