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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * A JavaScript unary operator.
 */
public enum JsUnaryOperator implements JsOperator {
  /*
   * Precedence indices from "JavaScript - The Definitive Guide" 4th Edition (page 57)
   */

  BIT_NOT("~", 14, PREFIX), DEC("--", 14, POSTFIX | PREFIX), DELETE("delete", 14, PREFIX),
      INC("++", 14, POSTFIX | PREFIX), NEG("-", 14, PREFIX), POS("+", 14, PREFIX),
      NOT("!", 14, PREFIX), TYPEOF("typeof", 14, PREFIX), VOID("void", 14, PREFIX);

  private int mask;

  private int precedence;

  private String symbol;

  private JsUnaryOperator(String symbol, int precedence, int mask) {
    this.symbol = symbol;
    this.precedence = precedence;
    this.mask = mask;
  }

  @Override
  public int getPrecedence() {
    return precedence;
  }

  @Override
  public String getSymbol() {
    return symbol;
  }

  @Override
  public boolean isKeyword() {
    return this == DELETE || this == TYPEOF || this == VOID;
  }

  @Override
  public boolean isLeftAssociative() {
    return (mask & LEFT) != 0;
  }

  public boolean isModifying() {
    return this == DEC || this == INC || this == DELETE;
  }

  @Override
  public boolean isPrecedenceLessThan(JsOperator other) {
    return precedence < other.getPrecedence();
  }

  @Override
  public boolean isValidInfix() {
    return (mask & INFIX) != 0;
  }

  @Override
  public boolean isValidPostfix() {
    return (mask & POSTFIX) != 0;
  }

  @Override
  public boolean isValidPrefix() {
    return (mask & PREFIX) != 0;
  }

  @Override
  public String toString() {
    return symbol;
  }

  /*
  * Used for externalization only.
  */
  public void writeOperator(ObjectOutput out) throws IOException {
    out.writeInt(ordinal());
  }

  public static JsUnaryOperator readOperator(ObjectInput in)
      throws IOException, ClassNotFoundException {
    int ordinal = in.readInt();
    return values()[ordinal];
  }
}
