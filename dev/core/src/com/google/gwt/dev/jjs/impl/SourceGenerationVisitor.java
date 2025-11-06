/*
 * Copyright 2007 Google Inc.
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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JEnumType;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JInterfaceType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JMethodBody;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JRecordType;
import com.google.gwt.dev.util.TextOutput;

/**
 * Generates Java source from our AST. ToStringGenerationVisitor is for
 * relatively short toString() results, for easy viewing in a debugger. This
 * subclass delves into the bodies of classes, interfaces, and methods to
 * produce the whole source tree.
 * <p>
 * The goal is not to generate the input source tree, or even properly compilable sources.
 * Instead, this provides a way to log the AST in a human-readable way.
 */
public class SourceGenerationVisitor extends ToStringGenerationVisitor {

  public SourceGenerationVisitor(TextOutput textOutput) {
    super(textOutput);
  }

  @Override
  public boolean visit(JClassType x, Context ctx) {
    printAbstractFlag(x);
    printFinalFlag(x);
    if (x instanceof JEnumType) {
      print(CHARS_ENUM);
    } else if (x instanceof JRecordType) {
      print(CHARS_RECORD);
    } else {
      print(CHARS_CLASS);
    }
    printTypeName(x);
    space();
    if (x.getSuperClass() != null) {
      print(CHARS_EXTENDS);
      printTypeName(x.getSuperClass());
      space();
    }

    if (x.getImplements().size() > 0) {
      print(CHARS_IMPLEMENTS);
      for (int i = 0, c = x.getImplements().size(); i < c; ++i) {
        if (i > 0) {
          print(CHARS_COMMA);
        }
        printTypeName(x.getImplements().get(i));
      }
      space();
    }
    openBlock();

    return true;
  }

  @Override
  public void endVisit(JDeclaredType x, Context ctx) {
    closeBlock();

    newline();
    newline();
  }

  @Override
  public void endVisit(JField x, Context ctx) {
    semi();
    newline();
    newline();
  }

  @Override
  public boolean visit(JMethod x, Context ctx) {
    if (JProgram.isClinit(x)) {
      // Suppress empty clinit.
      JMethodBody body = (JMethodBody) x.getBody();
      if (body.getBlock().getStatements().isEmpty()) {
        return false;
      }
    }
    return super.visit(x, ctx);
  }

  @Override
  public void endVisit(JMethod x, Context ctx) {
    newline();
    newline();
  }

  @Override
  public boolean visit(JInterfaceType x, Context ctx) {
    print(CHARS_INTERFACE);
    printTypeName(x);
    space();

    if (x.getImplements().size() > 0) {
      print(CHARS_EXTENDS);
      for (int i = 0, c = x.getImplements().size(); i < c; ++i) {
        if (i > 0) {
          print(CHARS_COMMA);
        }
        printTypeName(x.getImplements().get(i));
      }
      space();
    }

    openBlock();

    return true;
  }

  @Override
  public boolean visit(JProgram x, Context ctx) {
    for (int i = 0; i < x.getDeclaredTypes().size(); ++i) {
      JDeclaredType type = x.getDeclaredTypes().get(i);
      accept(type);
    }
    return false;
  }

  @Override
  protected boolean shouldPrintMethodBody() {
    return true;
  }

}
