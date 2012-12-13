/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.dev.js.ast;

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.util.Util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * A member/case in a JavaScript switch object.
 */
public abstract class JsSwitchMember extends JsNode {

  protected List<JsStatement> stmts;

  protected JsSwitchMember(SourceInfo sourceInfo) {
    super(sourceInfo);
    stmts = new ArrayList<JsStatement>();
  }

  public List<JsStatement> getStmts() {
    return stmts;
  }

  /*
  * Used for externalization only.
  */
  protected JsSwitchMember() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    Util.serializeCollection(stmts, out);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    stmts = Util.deserializeObjectList(in);
  }
}
