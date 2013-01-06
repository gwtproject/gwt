package com.google.gwt.tools.cldr;

import com.google.gwt.tools.cldr.PluralRuleParser.BinaryExpr;
import com.google.gwt.tools.cldr.PluralRuleParser.Range;
import com.google.gwt.tools.cldr.PluralRuleParser.Relation;
import com.google.gwt.tools.cldr.PluralRuleParser.Token;
import com.google.gwt.tools.cldr.PluralRuleParser.Tree;
import com.google.gwt.tools.cldr.PluralRuleParser.Variable;

import junit.framework.TestCase;

public class PluralRuleParserTest extends TestCase {

  public void testBasic() {
    PluralRuleParser parser = new PluralRuleParser();
    Tree cond = parser.parse("n is 1");
    Relation rel = (Relation) cond;
    assertEquals(Token.Kind.IS, rel.op);
    assertFalse(rel.negate);
    Variable var = rel.left;
    assertEquals(0, var.mod);
    Range range = rel.right;
    assertEquals(2, range.rangeValues.length);
    assertEquals(1, range.rangeValues[0]);
    assertEquals(1, range.rangeValues[1]);
    cond = parser.parse("n not in 4..12");
    rel = (Relation) cond;
    assertEquals(Token.Kind.IN, rel.op);
    assertTrue(rel.negate);
    var = rel.left;
    assertEquals(0, var.mod);
    range = rel.right;
    assertEquals(2, range.rangeValues.length);
    assertEquals(4, range.rangeValues[0]);
    assertEquals(12, range.rangeValues[1]);
  }

  public void testAndOr() {
    PluralRuleParser parser = new PluralRuleParser();
    Tree cond = parser.parse("n is 1 or n mod 10 is 1 and n mod 100 not in 11..19");
    BinaryExpr orExpr = (BinaryExpr) cond;
    assertEquals(Token.Kind.OR, orExpr.op);
    Relation rel = (Relation) orExpr.left;
    assertEquals(Token.Kind.IS, rel.op);
    assertFalse(rel.negate);
    Variable var = rel.left;
    assertEquals(0, var.mod);
    Range range = rel.right;
    assertEquals(2, range.rangeValues.length);
    assertEquals(1, range.rangeValues[0]);
    assertEquals(1, range.rangeValues[1]);
    BinaryExpr andExpr = (BinaryExpr) orExpr.right;
    assertEquals(Token.Kind.AND, andExpr.op);
    rel = (Relation) andExpr.left;
    assertEquals(Token.Kind.IS, rel.op);
    assertFalse(rel.negate);
    var = rel.left;
    assertEquals(10, var.mod);
    range = rel.right;
    assertEquals(2, range.rangeValues.length);
    assertEquals(1, range.rangeValues[0]);
    assertEquals(1, range.rangeValues[1]);
    rel = (Relation) andExpr.right;
    assertEquals(Token.Kind.IN, rel.op);
    assertTrue(rel.negate);
    var = rel.left;
    assertEquals(100, var.mod);
    range = rel.right;
    assertEquals(2, range.rangeValues.length);
    assertEquals(11, range.rangeValues[0]);
    assertEquals(19, range.rangeValues[1]);
  }

  public void testOr() {
    PluralRuleParser parser = new PluralRuleParser();
    Tree cond = parser.parse("n is 1 or n in 3,4");
    BinaryExpr orExpr = (BinaryExpr) cond;
    assertEquals(Token.Kind.OR, orExpr.op);
    Relation rel = (Relation) orExpr.left;
    assertEquals(Token.Kind.IS, rel.op);
    assertFalse(rel.negate);
    Range range = rel.right;
    assertEquals(2, range.rangeValues.length);
    assertEquals(1, range.rangeValues[0]);
    assertEquals(1, range.rangeValues[1]);
    rel = (Relation) orExpr.right;
    assertEquals(Token.Kind.IN, rel.op);
    assertFalse(rel.negate);
    range = rel.right;
    assertEquals(4, range.rangeValues.length);
    assertEquals(3, range.rangeValues[0]);
    assertEquals(3, range.rangeValues[1]);
    assertEquals(4, range.rangeValues[2]);
    assertEquals(4, range.rangeValues[3]);
  }
}
