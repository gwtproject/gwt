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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Java initialized local variable statement.
 */
public class JDeclarationStatement extends JStatement {

  public JExpression initializer;
  private JVariableRef variableRef;

  public JDeclarationStatement(SourceInfo info, JVariableRef variableRef, JExpression intializer) {
    super(info);
    this.variableRef = variableRef;
    this.initializer = intializer;
    CanHaveInitializer variable = variableRef.getTarget();
    variable.setInitializer(this);
  }

  public JExpression getInitializer() {
    return initializer;
  }

  public JVariableRef getVariableRef() {
    return variableRef;
  }

  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
      variableRef = (JVariableRef) visitor.acceptLvalue(variableRef);
      if (initializer != null) {
        initializer = visitor.accept(initializer);
      }
    }
    visitor.endVisit(this, ctx);
  }

  public JDeclarationStatement() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeObject(initializer);
    out.writeObject(variableRef);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    initializer = (JExpression) in.readObject();
    variableRef = (JVariableRef) in.readObject();
  }

}
