/*
 * Copyright 2012 Google Inc.
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
package com.google.gwt.tools.cldr;

import com.google.gwt.tools.cldr.PluralRuleParser.Token.Kind;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for CLDR plural rules, described by (taken from ICU4J):
 * <pre>
 * condition :     or_condition
 *                 and_condition
 * or_condition :  and_condition 'or' condition
 * and_condition : relation
 *                 relation 'and' relation
 * relation :      is_relation
 *                 in_relation
 *                 within_relation
 *                 'n' EOL
 * is_relation :   expr 'is' value
 *                 expr 'is' 'not' value
 * in_relation :   expr 'in' range
 *                 expr 'not' 'in' range_list
 * within_relation : expr 'within' range_list
 *                   expr 'not' 'within' range_list
 * expr :          'n'
 *                 'n' 'mod' value
 * value :         digit+
 * digit :         0|1|2|3|4|5|6|7|8|9
 * range_list :    range { , range }*
 * range :         value'..'value
 * </pre>
 */
public class PluralRuleParser {

  public static class TreeVisitor {

    public void visit(Range range) {
    }

    public void visit(BinaryExpr binExpr) {
    }

    public void visit(Variable var) {
    }

    public void visit(Relation rel) {
    }
  }

  public static class BinaryExpr implements Tree {
    public final Token.Kind op;
    public final Tree left;
    public final Tree right;

    public BinaryExpr(Token.Kind op, Tree left, Tree right) {
      this.op = op;
      this.left = left;
      this.right = right;
    }

    @Override
    public void accept(TreeVisitor v) {
      left.accept(v);
      right.accept(v);
      v.visit(this);
    }
  }

  public static class Range implements Tree {
    public final int[] rangeValues;
    
    public Range(List<Integer> rangeValues) {
      int n = rangeValues.size();
      this.rangeValues = new int[n];
      for (int i = 0; i < n; i++) {
        this.rangeValues[i] = rangeValues.get(i);
      }
    }

    @Override
    public void accept(TreeVisitor v) {
      v.visit(this);
    }
  }

  public static class Relation implements Tree {
    public final boolean negate;
    public final Token.Kind op;
    public final Variable left;
    public final Range right;

    public Relation(Token.Kind op, Variable left, Range right, boolean negate) {
      this.op = op;
      this.left = left;
      this.right = right;
      this.negate = negate;
    }

    @Override
    public void accept(TreeVisitor v) {
      left.accept(v);
      right.accept(v);
      v.visit(this);
    }
  }
  
  public interface Tree {
    void accept(TreeVisitor v);    
  }

  public static class Variable implements Tree {
    public final int mod;

    public Variable(int mod) {
      this.mod = mod;
    }

    @Override
    public void accept(TreeVisitor v) {
      v.visit(this);
    }
  }

  protected static class Token {
    public enum Kind {
      OR,
      AND,
      MOD,
      IS,
      IN,
      WITHIN,
      NOT,
      IDENT,
      INTEGER,
      DOTDOT,
      COMMA,
    }

    final Kind kind;

    final String value;
    public Token(Kind kind, String value) {
      this.kind = kind;
      this.value = value;
    }
  }

  // @VisibleForTesting
  static class Lexer {

    private final String src;
    private int pos;
    private Token saved;
    
    public Lexer(String src) {
      this.src = src;
      pos = 0;
    }
    
    public Token expect(Kind kind) {
      Token tok = next();
      if (tok.kind != kind) {
        throw new RuntimeException("Expected " + kind + ", got " + tok.kind);
      }
      return tok;
    }

    public Token next() {
      if (saved != null) {
        Token tok = saved;
        saved = null;
        return tok;
      }
      while (pos < src.length() && src.charAt(pos) == ' ') {
        ++pos;
      }
      if (pos >= src.length()) {
        return null;
      }
      char ch = src.charAt(pos);
      if (Character.isAlphabetic(ch)) {
        StringBuilder buf = new StringBuilder();
        do {
          buf.append(ch);
          ch = nextChar();
        } while (Character.isAlphabetic(ch));
        String ident = buf.toString();
        if ("or".equalsIgnoreCase(ident)) {
          return new Token(Token.Kind.OR, ident);
        } else if ("and".equalsIgnoreCase(ident)) {
          return new Token(Token.Kind.AND, ident);
        } else if ("is".equalsIgnoreCase(ident)) {
          return new Token(Token.Kind.IS, ident);
        } else if ("not".equalsIgnoreCase(ident)) {
          return new Token(Token.Kind.NOT, ident);
        } else if ("in".equalsIgnoreCase(ident)) {
          return new Token(Token.Kind.IN, ident);
        } else if ("within".equalsIgnoreCase(ident)) {
          return new Token(Token.Kind.WITHIN, ident);
        } else if ("mod".equalsIgnoreCase(ident)) {
          return new Token(Token.Kind.MOD, ident);
        } else {
          return new Token(Token.Kind.IDENT, ident);
        }
      }
      if (Character.isDigit(ch)) {
        StringBuilder buf = new StringBuilder();
        do {
          buf.append(ch);
          ch = nextChar();
        } while (Character.isDigit(ch));
        return new Token(Token.Kind.INTEGER, buf.toString());
      }
      if (ch == ',') {
        nextChar();
        return new Token(Token.Kind.COMMA, ",");
      }
      if (ch == '.') {
        if (nextChar() != '.') {
          throw new RuntimeException("Expected '..', found '.'");
        }
        nextChar();
        return new Token(Token.Kind.DOTDOT, "..");
      }
      throw new RuntimeException("Invalid character '" + ch + "'");
    }

    /**
     * Check to see if the next token matches -- if so, consume it and return
     * true.
     * 
     * @param kind
     * @return
     */
    public boolean optional(Kind kind) {
      Token tok = peek();
      if (tok != null && tok.kind == kind) {
        next();
        return true;
      }
      return false;
    }

    public Token peek() {
      if (saved == null) {
        saved = next();
      }
      return saved;
    }

    private char nextChar() {
      if (++pos < src.length()) {
        return src.charAt(pos);
      } else {
        return 0;
      }
    }
  }

  private Lexer lexer;

  public Tree parse(String src) {
    lexer = new Lexer(src);
    Tree left = parseAndCond();
    while (lexer.optional(Token.Kind.OR)) {
      Tree right = parseAndCond();
      left = new BinaryExpr(Token.Kind.OR, left, right);
    }
    assert lexer.peek() == null;
    return left;
  }

  public Tree parseAndCond() {
    Tree left = parseRelation();
    while (lexer.optional(Token.Kind.AND)) {
      Tree right = parseAndCond();
      left = new BinaryExpr(Token.Kind.AND, left, right);
    }
    return left;
  }

  public Variable parseExpr() {
    lexer.expect(Token.Kind.IDENT);
    int mod = 0;
    if (lexer.optional(Token.Kind.MOD)) {
      Token value = lexer.expect(Token.Kind.INTEGER);
      mod = Integer.parseInt(value.value, 10);
    }
    return new Variable(mod);
  }

  public Tree parseRelation() {
    Variable expr = parseExpr();
    boolean seenNot = false;
    if (lexer.optional(Token.Kind.NOT)) {
      seenNot = true;
    }
    Token op = lexer.next();
    List<Integer> ranges = new ArrayList<Integer>();
    switch (op.kind) {
      case IS:
        assert !seenNot;
        seenNot = lexer.optional(Token.Kind.NOT);
        Token value = lexer.expect(Token.Kind.INTEGER);
        int val = Integer.parseInt(value.value, 10);
        ranges.add(val);
        ranges.add(val);
        break;
      case IN:
      case WITHIN:
        do {
          Token low = lexer.expect(Token.Kind.INTEGER);
          Token high = low;
          if (lexer.optional(Token.Kind.DOTDOT)) {
            high = lexer.expect(Token.Kind.INTEGER);
          }
          ranges.add(Integer.parseInt(low.value, 10));
          ranges.add(Integer.parseInt(high.value, 10));
        } while (lexer.optional(Token.Kind.COMMA));
        break;
      default:
        throw new RuntimeException("Unexpected token " + op.kind);
    }
    return new Relation(op.kind, expr, new Range(ranges), seenNot);
  }
}
