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
package com.google.gwt.dev.jjs.ast;

import com.google.gwt.dev.jjs.SourceInfo;

import java.util.List;

/**
 * Java try statement.
 */
public class JTryStatement extends JStatement {

  private final List<List<JType>> catchTypes;
  private final List<JLocalRef> catchArgs;
  private final List<JBlock> catchBlocks;
  private final JBlock finallyBlock;
  private final JBlock tryBlock;

  /**
   * Construct a Java try statement.
   *
   * Parameters catchTypes, catchArgs and catchBlocks must agree on size. Each element of each
   * of these lists corresponds to a catch statement.
   *
   * @param info the source information.
   * @param tryBlock the statement block inside the try construct.
   * @param catchTypes  each element of this list contains the catch types the corresponding catch
   *                    block has. (Each catch block might have multiple Exception types associated
   *                    in Java 7).
   * @param catchArgs each element of this list correspond to the exception variable declared in
   *                  corresponding catch statement.
   * @param catchBlocks each element of this list contains the statement block for the corresponding
   *                    catch block.
   * @param finallyBlock the statement block corresponding to the finally construct.
   */
  public JTryStatement(SourceInfo info, JBlock tryBlock, List<List<JType>> catchTypes,
      List<JLocalRef> catchArgs, List<JBlock> catchBlocks, JBlock finallyBlock) {
    super(info);
    assert (catchArgs.size() == catchBlocks.size());
    assert (catchArgs.size() == catchTypes.size());
    this.tryBlock = tryBlock;
    this.catchArgs = catchArgs;
    this.catchTypes = catchTypes;
    this.catchBlocks = catchBlocks;
    this.finallyBlock = finallyBlock;
  }

  public List<List<JType>> getCatchTypes() {
    return catchTypes;
  }

  public List<JLocalRef> getCatchArgs() {
    return catchArgs;
  }

  public List<JBlock> getCatchBlocks() {
    return catchBlocks;
  }

  public JBlock getFinallyBlock() {
    return finallyBlock;
  }

  public JBlock getTryBlock() {
    return tryBlock;
  }


  /**
   * Resolve an external reference during AST stitching.
   */
  public void resolve(int i, int j, JReferenceType newType) {
    assert newType.replaces(catchTypes.get(i).get(j));
    catchTypes.get(i).set(j, newType);
  }

  public void traverse(JVisitor visitor, Context ctx) {
    if (visitor.visit(this, ctx)) {
      visitor.accept(tryBlock);
      visitor.accept(catchArgs);
      visitor.accept(catchBlocks);
      // TODO: normalize this so it's never null?
      if (finallyBlock != null) {
        visitor.accept(finallyBlock);
      }
    }
    visitor.endVisit(this, ctx);
  }
}
