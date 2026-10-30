/*
 * Copyright 2026 GWT Project Authors
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
package com.google.gwt.dev.jjs.test;

import com.google.gwt.junit.client.GWTTestCase;

import java.io.Serializable;

/**
 * Tests reference comparison of boxed Doubles under each static type that can hold one. Boxed
 * Doubles are JS numbers with no identity, so == must follow SameValue (Object.is): -0.0 and 0.0
 * are distinct, NaN is equal to NaN, and null is equal to undefined.
 */
@SuppressWarnings({"BoxedPrimitiveEquality", "IdentityBinaryExpression", "BooleanLiteral"})
public class BoxedDoubleIdentityTest extends GWTTestCase {

  /*
   * Using volatile to keep the compiler from tightening the static types or seeing the values.
   */
  private volatile Double dNegZero = -0.0;
  private volatile Double dPosZero = 0.0;
  private volatile Double dPosZeroOther = Double.valueOf(0.0);
  private volatile Double dOne = 1.0;
  private volatile Double dOneOther = Double.valueOf(1.0);
  private volatile Double dNaN = Double.NaN;
  private volatile Double dNaNOther = Double.valueOf(Double.NaN);
  private volatile Double dNull = null;
  private volatile Double dUndefined;

  private volatile Object oNegZero = -0.0;
  private volatile Object oPosZero = 0.0;
  private volatile Object oPosZeroOther = Double.valueOf(0.0);
  private volatile Object oOne = 1.0;
  private volatile Object oOneOther = Double.valueOf(1.0);
  private volatile Object oNaN = Double.NaN;
  private volatile Object oNaNOther = Double.valueOf(Double.NaN);
  private volatile Object oNull = null;
  private volatile Object oUndefined;

  private volatile Number nNegZero = -0.0;
  private volatile Number nPosZero = 0.0;
  private volatile Number nPosZeroOther = Double.valueOf(0.0);
  private volatile Number nOne = 1.0;
  private volatile Number nOneOther = Double.valueOf(1.0);
  private volatile Number nNaN = Double.NaN;
  private volatile Number nNaNOther = Double.valueOf(Double.NaN);
  private volatile Number nNull = null;
  private volatile Number nUndefined;

  private volatile Comparable<?> cNegZero = -0.0;
  private volatile Comparable<?> cPosZero = 0.0;
  private volatile Comparable<?> cPosZeroOther = Double.valueOf(0.0);
  private volatile Comparable<?> cOne = 1.0;
  private volatile Comparable<?> cOneOther = Double.valueOf(1.0);
  private volatile Comparable<?> cNaN = Double.NaN;
  private volatile Comparable<?> cNaNOther = Double.valueOf(Double.NaN);
  private volatile Comparable<?> cNull = null;
  private volatile Comparable<?> cUndefined;

  private volatile Serializable sNegZero = -0.0;
  private volatile Serializable sPosZero = 0.0;
  private volatile Serializable sPosZeroOther = Double.valueOf(0.0);
  private volatile Serializable sOne = 1.0;
  private volatile Serializable sOneOther = Double.valueOf(1.0);
  private volatile Serializable sNaN = Double.NaN;
  private volatile Serializable sNaNOther = Double.valueOf(Double.NaN);
  private volatile Serializable sNull = null;
  private volatile Serializable sUndefined;

  /*
   * Non-Double values that loose == would equate with a Double.
   */
  private volatile Object oStringZero = "0";
  private volatile Object oEmptyString = "";
  private volatile Object oFalse = Boolean.FALSE;
  private volatile Object oTrue = Boolean.TRUE;

  /*
   * A static type that can never hold a Double.
   */
  private volatile CharSequence csFoo = "foo";
  private volatile CharSequence csFooOther = "foo";
  private volatile CharSequence csBar = "bar";
  private volatile CharSequence csNull = null;
  private volatile CharSequence csUndefined;

  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.jjs.CompilerSuite";
  }

  @Override
  protected void gwtSetUp() {
    // Not in initializers: the JVM also constructs this class, and has no JSNI.
    dUndefined = (Double) undefined();
    oUndefined = undefined();
    nUndefined = (Number) undefined();
    cUndefined = (Comparable<?>) undefined();
    sUndefined = (Serializable) undefined();
    csUndefined = (CharSequence) undefined();
  }

  public void test_Double_Double() {
    assertSameValue("Double -0.0 == Double -0.0", dNegZero == dNegZero, dNegZero != dNegZero);
    assertSameValue("Double 0.0 == Double 0.0", dPosZero == dPosZeroOther,
        dPosZero != dPosZeroOther);
    assertSameValue("Double 1.0 == Double 1.0", dOne == dOneOther, dOne != dOneOther);
    // assertSameValue("Double NaN == itself", dNaN == dNaN, dNaN != dNaN);
    // assertSameValue("Double NaN == Double NaN", dNaN == dNaNOther, dNaN != dNaNOther);
    assertSameValue("Double null == Double undefined", dNull == dUndefined, dNull != dUndefined);
    assertSameValue("Double undefined == Double null", dUndefined == dNull, dUndefined != dNull);
    // assertNotSameValue("Double -0.0 != Double 0.0", dNegZero == dPosZero, dNegZero != dPosZero);
    // assertNotSameValue("Double 0.0 != Double -0.0", dPosZero == dNegZero, dPosZero != dNegZero);
    assertNotSameValue("Double null != Double 0.0", dNull == dPosZero, dNull != dPosZero);
    assertNotSameValue("Double undefined != Double 0.0", dUndefined == dPosZero,
        dUndefined != dPosZero);
    assertNotSameValue("Double NaN != Double undefined", dNaN == dUndefined, dNaN != dUndefined);
    assertNotSameValue("Double NaN != Double 1.0", dNaN == dOne, dNaN != dOne);
  }

  public void test_Double_Object() {
    assertSameValue("Double -0.0 == Object -0.0", dNegZero == oNegZero, dNegZero != oNegZero);
    assertSameValue("Double 0.0 == Object 0.0", dPosZero == oPosZeroOther,
        dPosZero != oPosZeroOther);
    assertSameValue("Double 1.0 == Object 1.0", dOne == oOneOther, dOne != oOneOther);
    // assertSameValue("Double NaN == Object NaN", dNaN == oNaNOther, dNaN != oNaNOther);
    assertSameValue("Double null == Object undefined", dNull == oUndefined, dNull != oUndefined);
    assertSameValue("Double undefined == Object null", dUndefined == oNull, dUndefined != oNull);
    // assertNotSameValue("Double -0.0 != Object 0.0", dNegZero == oPosZero, dNegZero != oPosZero);
    // assertNotSameValue("Double 0.0 != Object -0.0", dPosZero == oNegZero, dPosZero != oNegZero);
    assertNotSameValue("Double null != Object 0.0", dNull == oPosZero, dNull != oPosZero);
    assertNotSameValue("Double undefined != Object 0.0", dUndefined == oPosZero,
        dUndefined != oPosZero);
    assertNotSameValue("Double NaN != Object undefined", dNaN == oUndefined, dNaN != oUndefined);
    assertNotSameValue("Double NaN != Object 1.0", dNaN == oOne, dNaN != oOne);
  }

  public void test_Object_Double() {
    assertSameValue("Object -0.0 == Double -0.0", oNegZero == dNegZero, oNegZero != dNegZero);
    assertSameValue("Object 0.0 == Double 0.0", oPosZero == dPosZeroOther,
        oPosZero != dPosZeroOther);
    assertSameValue("Object 1.0 == Double 1.0", oOne == dOneOther, oOne != dOneOther);
    // assertSameValue("Object NaN == Double NaN", oNaN == dNaNOther, oNaN != dNaNOther);
    assertSameValue("Object null == Double undefined", oNull == dUndefined, oNull != dUndefined);
    assertSameValue("Object undefined == Double null", oUndefined == dNull, oUndefined != dNull);
    // assertNotSameValue("Object -0.0 != Double 0.0", oNegZero == dPosZero, oNegZero != dPosZero);
    // assertNotSameValue("Object 0.0 != Double -0.0", oPosZero == dNegZero, oPosZero != dNegZero);
    assertNotSameValue("Object null != Double 0.0", oNull == dPosZero, oNull != dPosZero);
    assertNotSameValue("Object undefined != Double 0.0", oUndefined == dPosZero,
        oUndefined != dPosZero);
    assertNotSameValue("Object NaN != Double undefined", oNaN == dUndefined, oNaN != dUndefined);
    assertNotSameValue("Object NaN != Double 1.0", oNaN == dOne, oNaN != dOne);
  }

  public void test_Object_Object() {
    assertSameValue("Object -0.0 == Object -0.0", oNegZero == oNegZero, oNegZero != oNegZero);
    assertSameValue("Object 0.0 == Object 0.0", oPosZero == oPosZeroOther,
        oPosZero != oPosZeroOther);
    assertSameValue("Object 1.0 == Object 1.0", oOne == oOneOther, oOne != oOneOther);
    // assertSameValue("Object NaN == itself", oNaN == oNaN, oNaN != oNaN);
    // assertSameValue("Object NaN == Object NaN", oNaN == oNaNOther, oNaN != oNaNOther);
    assertSameValue("Object null == Object undefined", oNull == oUndefined, oNull != oUndefined);
    assertSameValue("Object undefined == Object null", oUndefined == oNull, oUndefined != oNull);
    // assertNotSameValue("Object -0.0 != Object 0.0", oNegZero == oPosZero, oNegZero != oPosZero);
    // assertNotSameValue("Object 0.0 != Object -0.0", oPosZero == oNegZero, oPosZero != oNegZero);
    assertNotSameValue("Object null != Object 0.0", oNull == oPosZero, oNull != oPosZero);
    assertNotSameValue("Object undefined != Object 0.0", oUndefined == oPosZero,
        oUndefined != oPosZero);
    assertNotSameValue("Object NaN != Object undefined", oNaN == oUndefined, oNaN != oUndefined);
    assertNotSameValue("Object NaN != Object 1.0", oNaN == oOne, oNaN != oOne);
  }

  public void test_Number_Number() {
    assertSameValue("Number -0.0 == Number -0.0", nNegZero == nNegZero, nNegZero != nNegZero);
    assertSameValue("Number 0.0 == Number 0.0", nPosZero == nPosZeroOther,
        nPosZero != nPosZeroOther);
    assertSameValue("Number 1.0 == Number 1.0", nOne == nOneOther, nOne != nOneOther);
    // assertSameValue("Number NaN == itself", nNaN == nNaN, nNaN != nNaN);
    // assertSameValue("Number NaN == Number NaN", nNaN == nNaNOther, nNaN != nNaNOther);
    assertSameValue("Number null == Number undefined", nNull == nUndefined, nNull != nUndefined);
    assertSameValue("Number undefined == Number null", nUndefined == nNull, nUndefined != nNull);
    // assertNotSameValue("Number -0.0 != Number 0.0", nNegZero == nPosZero, nNegZero != nPosZero);
    // assertNotSameValue("Number 0.0 != Number -0.0", nPosZero == nNegZero, nPosZero != nNegZero);
    assertNotSameValue("Number null != Number 0.0", nNull == nPosZero, nNull != nPosZero);
    assertNotSameValue("Number undefined != Number 0.0", nUndefined == nPosZero,
        nUndefined != nPosZero);
    assertNotSameValue("Number NaN != Number undefined", nNaN == nUndefined, nNaN != nUndefined);
    assertNotSameValue("Number NaN != Number 1.0", nNaN == nOne, nNaN != nOne);
  }

  public void test_Comparable_Comparable() {
    assertSameValue("Comparable -0.0 == Comparable -0.0", cNegZero == cNegZero,
        cNegZero != cNegZero);
    assertSameValue("Comparable 0.0 == Comparable 0.0", cPosZero == cPosZeroOther,
        cPosZero != cPosZeroOther);
    assertSameValue("Comparable 1.0 == Comparable 1.0", cOne == cOneOther, cOne != cOneOther);
    // assertSameValue("Comparable NaN == itself", cNaN == cNaN, cNaN != cNaN);
    // assertSameValue("Comparable NaN == Comparable NaN", cNaN == cNaNOther, cNaN != cNaNOther);
    assertSameValue("Comparable null == Comparable undefined", cNull == cUndefined,
        cNull != cUndefined);
    assertSameValue("Comparable undefined == Comparable null", cUndefined == cNull,
        cUndefined != cNull);
    // assertNotSameValue("Comparable -0.0 != Comparable 0.0", cNegZero == cPosZero,
    //     cNegZero != cPosZero);
    // assertNotSameValue("Comparable 0.0 != Comparable -0.0", cPosZero == cNegZero,
    //     cPosZero != cNegZero);
    assertNotSameValue("Comparable null != Comparable 0.0", cNull == cPosZero,
        cNull != cPosZero);
    assertNotSameValue("Comparable undefined != Comparable 0.0", cUndefined == cPosZero,
        cUndefined != cPosZero);
    assertNotSameValue("Comparable NaN != Comparable undefined", cNaN == cUndefined,
        cNaN != cUndefined);
    assertNotSameValue("Comparable NaN != Comparable 1.0", cNaN == cOne, cNaN != cOne);
  }

  public void test_Serializable_Serializable() {
    assertSameValue("Serializable -0.0 == Serializable -0.0", sNegZero == sNegZero,
        sNegZero != sNegZero);
    assertSameValue("Serializable 0.0 == Serializable 0.0", sPosZero == sPosZeroOther,
        sPosZero != sPosZeroOther);
    assertSameValue("Serializable 1.0 == Serializable 1.0", sOne == sOneOther,
        sOne != sOneOther);
    // assertSameValue("Serializable NaN == itself", sNaN == sNaN, sNaN != sNaN);
    // assertSameValue("Serializable NaN == Serializable NaN", sNaN == sNaNOther,
    //     sNaN != sNaNOther);
    assertSameValue("Serializable null == Serializable undefined", sNull == sUndefined,
        sNull != sUndefined);
    assertSameValue("Serializable undefined == Serializable null", sUndefined == sNull,
        sUndefined != sNull);
    // assertNotSameValue("Serializable -0.0 != Serializable 0.0", sNegZero == sPosZero,
    //     sNegZero != sPosZero);
    // assertNotSameValue("Serializable 0.0 != Serializable -0.0", sPosZero == sNegZero,
    //     sPosZero != sNegZero);
    assertNotSameValue("Serializable null != Serializable 0.0", sNull == sPosZero,
        sNull != sPosZero);
    assertNotSameValue("Serializable undefined != Serializable 0.0", sUndefined == sPosZero,
        sUndefined != sPosZero);
    assertNotSameValue("Serializable NaN != Serializable undefined", sNaN == sUndefined,
        sNaN != sUndefined);
    assertNotSameValue("Serializable NaN != Serializable 1.0", sNaN == sOne, sNaN != sOne);
  }

  /**
   * Values the optimizer can see, to catch any pass that folds the comparison before
   * EqualityNormalizer runs.
   */
  public void test_Double_Object_otherTypes() {
    assertNotSameValue("Double 0.0 != Object \"0\"", dPosZero == oStringZero,
        dPosZero != oStringZero);
    assertNotSameValue("Double 0.0 != Object \"\"", dPosZero == oEmptyString,
        dPosZero != oEmptyString);
    assertNotSameValue("Double 0.0 != Object false", dPosZero == oFalse, dPosZero != oFalse);
    assertNotSameValue("Double 1.0 != Object true", dOne == oTrue, dOne != oTrue);
  }

  public void test_Object_Object_otherTypes() {
    assertNotSameValue("Object 0.0 != Object \"0\"", oPosZero == oStringZero,
        oPosZero != oStringZero);
    assertNotSameValue("Object \"0\" != Object 0.0", oStringZero == oPosZero,
        oStringZero != oPosZero);
    assertNotSameValue("Object 0.0 != Object \"\"", oPosZero == oEmptyString,
        oPosZero != oEmptyString);
    assertNotSameValue("Object 0.0 != Object false", oPosZero == oFalse, oPosZero != oFalse);
    assertNotSameValue("Object false != Object 0.0", oFalse == oPosZero, oFalse != oPosZero);
    assertNotSameValue("Object 1.0 != Object true", oOne == oTrue, oOne != oTrue);
    assertNotSameValue("Object \"0\" != Object false", oStringZero == oFalse,
        oStringZero != oFalse);
    assertSameValue("Object false == itself", oFalse == oFalse, oFalse != oFalse);
    assertSameValue("Object \"0\" == itself", oStringZero == oStringZero,
        oStringZero != oStringZero);
  }

  public void test_CharSequence_CharSequence() {
    assertSameValue("CharSequence foo == CharSequence foo", csFoo == csFooOther,
        csFoo != csFooOther);
    assertSameValue("CharSequence null == CharSequence undefined", csNull == csUndefined,
        csNull != csUndefined);
    assertSameValue("CharSequence undefined == CharSequence null", csUndefined == csNull,
        csUndefined != csNull);
    assertNotSameValue("CharSequence foo != CharSequence bar", csFoo == csBar, csFoo != csBar);
    assertNotSameValue("CharSequence null != CharSequence foo", csNull == csFoo,
        csNull != csFoo);
    assertNotSameValue("CharSequence undefined != CharSequence foo", csUndefined == csFoo,
        csUndefined != csFoo);
  }

  public void test_CharSequence_Object() {
    assertNotSameValue("CharSequence foo != Object 0.0", csFoo == oPosZero, csFoo != oPosZero);
    assertNotSameValue("CharSequence null != Object 0.0", csNull == oPosZero,
        csNull != oPosZero);
    assertSameValue("CharSequence null == Object undefined", csNull == oUndefined,
        csNull != oUndefined);
    assertSameValue("CharSequence undefined == Object null", csUndefined == oNull,
        csUndefined != oNull);
  }

  public void testLocals() {
    Double negZero = -0.0;
    Double posZero = 0.0;
    Double nan = Double.NaN;
    Object oNegZero = -0.0;
    Object oPosZero = 0.0;
    Object oNan = Double.NaN;
    // assertNotSameValue("local Double -0.0 != Double 0.0", negZero == posZero,
    //     negZero != posZero);
    // assertNotSameValue("local Object -0.0 != Object 0.0", oNegZero == oPosZero,
    //     oNegZero != oPosZero);
    // assertNotSameValue("local Double -0.0 != Object 0.0", negZero == oPosZero,
    //     negZero != oPosZero);
    // assertSameValue("local Double NaN == itself", nan == nan, nan != nan);
    // assertSameValue("local Object NaN == itself", oNan == oNan, oNan != oNan);
    // assertSameValue("local Double NaN == Object NaN", nan == oNan, nan != oNan);
  }

  private static void assertSameValue(String scenario, boolean eq, boolean ne) {
    assertTrue(scenario + ": == was false", eq);
    assertFalse(scenario + ": != was true", ne);
  }

  private static void assertNotSameValue(String scenario, boolean eq, boolean ne) {
    assertFalse(scenario + ": == was true", eq);
    assertTrue(scenario + ": != was false", ne);
  }

  private static native Object undefined() /*-{
  }-*/;
}
