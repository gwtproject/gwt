/*
 * Copyright 2014 Google Inc.
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

import com.google.gwt.dev.util.arg.SourceLevel;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.JUnitShell;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Dummy test case. Java8Test is super sourced so that GWT can be compiled by Java 6.
 *
 * NOTE: Make sure this class has the same test methods of its supersourced variant.
 */
@DoNotRunWith(Platform.Devel)
public class Java8Test extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.jjs.Java8Test";
  }

  @Override
  public void runTest() throws Throwable {
    // Only run these tests if -sourceLevel 9 (or greater) is enabled.
    if (isGwtSourceLevel9()) {
      super.runTest();
    }
  }

  public void testLambdaNoCapture() {
    // Make sure we are using the right Java8Test if the source compatibility level is set to Java 8
    // or above.
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaCaptureLocal() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaCaptureLocalWithInnerClass() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaCaptureLocalAndField() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaCaptureLocalAndFieldWithInnerClass() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testCompileLambdaCaptureOuterInnerField() throws Exception {
    assertFalse(isGwtSourceLevel9());
  }

  public void testStaticReferenceBinding() throws Exception {
    assertFalse(isGwtSourceLevel9());
  }

  public void testInstanceReferenceBinding() throws Exception {
    assertFalse(isGwtSourceLevel9());
  }

  public void testImplicitQualifierReferenceBinding() throws Exception {
    assertFalse(isGwtSourceLevel9());
  }

  public void testConstructorReferenceBinding() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testStaticInterfaceMethod() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testArrayConstructorReference() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testArrayConstructorReferenceBoxed() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testVarArgsReferenceBinding() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testVarArgsPassthroughReferenceBinding() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testVarArgsPassthroughReferenceBindingProvidedArray() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testSuperReferenceExpression() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testQualifiedSuperReferenceExpression() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testSuperReferenceExpressionWithVarArgs() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testPrivateConstructorReference() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testDefaultInterfaceMethod() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testDefaultInterfaceMethodVirtualUpRef() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testInterfaceWithDefaultMethodsInitialization() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testDefaultInterfaceMethodMultiple() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testDefaultMethodReference() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testDefenderMethodByInterfaceInstance() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testDefaultMethod_staticInitializer() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testThisRefInDefenderMethod() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testClassImplementsTwoInterfacesWithSameDefenderMethod() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testAbstractClassImplementsInterface() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testSuperRefInDefenderMethod() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testSuperThisRefsInDefenderMethod() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testNestedInterfaceClass() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testBaseIntersectionCast() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testIntersectionCastWithLambdaExpr() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testIntersectionCastPolymorphism() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaCaptureParameter() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaNestingCaptureLocal() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaNestingInAnonymousCaptureLocal() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaNestingInMultipleMixedAnonymousCaptureLocal() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaNestingInMultipleMixedAnonymousCaptureLocal_withInterference() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaNestingInMultipleMixedAnonymousCaptureLocalAndField() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaNestingInMultipleAnonymousCaptureLocal() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaNestingCaptureField_InnerClassCapturingOuterClassVariable() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testInnerClassCaptureLocalFromOuterLambda() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaNestingCaptureField() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaMultipleNestingCaptureFieldAndLocal() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaMultipleNestingCaptureFieldAndLocalInnerClass() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testMethodRefWithSameName() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testMultipleDefaults_fromInterfaces_left() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testMultipleDefaults_fromInterfaces_right() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testMultipleDefaults_superclass_left() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testMultipleDefaults_superclass_right() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testMultipleDefaults_defaultShadowsOverSyntheticAbstractStub() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testMultipleDefaults_defaultShadowsOverDefaultOnSuperAbstract() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testInterfaceThis() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testMethodReference_generics() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testNativeJsTypeWithStaticInitializer() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testJsVarargsLambda() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testMethodReference_implementedInSuperclass() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testMethodReference_genericTypeParameters() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testMethodReference_autoboxing() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testMethodReference_varargs() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testNativeJsOverlay_lambda() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaCapturingThis_onDefaultMethod() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testJsFunction_withOverlay() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testFunctionalExpressionBridges() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testCorrectNaming() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testInterfaceWithOverlayAndNativeSubclass() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLocalClassConstructorReferenceInStaticMethod() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testDefaultMethodDevirtualizationOrder() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testVarargsFunctionalConversion() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testJSOLivenessSingleImplErasure() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaErasureCasts() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testLambdaBoxing() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testTryWithResourcesJava9() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testInterfacePrivateMethodsJava9() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testAnonymousDiamondJava9() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testImproperMethodResolution() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testIntersectionCastLambda() {
    assertFalse(isGwtSourceLevel9());
  }

  public void testIntersectionCastMethodReference() {
    assertFalse(isGwtSourceLevel9());
  }

  private boolean isGwtSourceLevel9() {
    return JUnitShell.getCompilerOptions().getSourceLevel().compareTo(SourceLevel.JAVA9) >= 0;
  }
}
