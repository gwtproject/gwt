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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * An enumeration of the available unary operators.
 */
public enum JUnaryOperator {

  INC("++"), DEC("--"), NEG("-"), NOT("!"), BIT_NOT("~");

  private final char[] symbol;

  private JUnaryOperator(String symbol) {
    this.symbol = symbol.toCharArray();
  }

  public char[] getSymbol() {
    return symbol;
  }

  public boolean isModifying() {
    return this == INC || this == DEC;
  }

  @Override
  public String toString() {
    return new String(getSymbol());
  }

  /*
  * Used for externalization only.
  */
  public void writeOperator(ObjectOutput out) throws IOException {
    out.writeInt(ordinal());
  }

  public static JUnaryOperator readOperator(ObjectInput in)
      throws IOException, ClassNotFoundException {
    int ordinal = in.readInt();
    return values()[ordinal];
  }
}
