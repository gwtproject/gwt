package com.google.gwt.dev.jjs.ast.js;

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JStatement;
import com.google.gwt.dev.jjs.ast.JVisitor;

/**
 * Represents a JavaScript debugger statement.
 */
public class JDebuggerStatement extends JStatement {

  public JDebuggerStatement(SourceInfo info) {
    super(info);
  }

  @Override
  public void traverse(JVisitor visitor, Context ctx) {
    visitor.visit(this, ctx);
    visitor.endVisit(this, ctx);
  }
}
