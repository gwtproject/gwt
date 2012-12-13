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
package com.google.gwt.dev.jjs.ast;

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.util.Util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

/**
 * Represents a rebound entry point before deferred binding decisions are
 * finalized. Replaced with the entry call for the appropriate rebind result in
 * that permutation.
 */
public class JReboundEntryPoint extends JStatement {

  private List<JExpression> entryCalls;
  private List<String> resultTypes;
  private String sourceType;

  public JReboundEntryPoint(SourceInfo info, JReferenceType sourceType,
      List<JClassType> resultTypes, List<JExpression> entryCalls) {
    super(info);
    this.sourceType = JGwtCreate.nameOf(sourceType);
    this.resultTypes = JGwtCreate.nameOf(resultTypes);
    this.entryCalls = entryCalls;
  }

  public List<JExpression> getEntryCalls() {
    return entryCalls;
  }

  public List<String> getResultTypes() {
    return resultTypes;
  }

  public String getSourceType() {
    return sourceType;
  }

  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
      visitor.accept(entryCalls);
    }
    visitor.endVisit(this, ctx);
  }
  public JReboundEntryPoint() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeObject(entryCalls);
    out.writeObject(resultTypes);
    Util.serializeString(sourceType, out);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    entryCalls = (List<JExpression>) in.readObject();
    resultTypes = (List<String>) in.readObject();
    sourceType = Util.deserializeString(in);
  }
}
