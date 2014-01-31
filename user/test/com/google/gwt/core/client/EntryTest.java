package com.google.gwt.core.client;

import com.google.gwt.junit.client.GWTTestCase;

public class EntryTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.Core";
  }

  /**
   * Tests that methods with primitive return types correctly return JS values
   * when wrapped with {@code $entry} (rather than JS objects).
   * <p>
   * We test this with a boolean {@code false} that we coerce to a boolean. If the
   * $entry-wrapped function returns it as a JS Boolean object, it'll coerce to
   * {@code true} because it's non-null.
   * 
   * @see <a href="https://code.google.com/p/google-web-toolkit/issues/detail?id=8548">issue 8548</a>
   */
  public native void testPrimitiveReturnType() /*-{
    var assertIsBooleanValueFalse = function(shouldBeBooleanValueFalse) {
      @junit.framework.Assert::assertEquals(Ljava/lang/String;Ljava/lang/String;)("boolean", typeof shouldBeBooleanValueFalse);
      @junit.framework.Assert::assertFalse(Z)(!!shouldBeBooleanValueFalse);
    };
    var assertIsBooleanObjectFalse = function(shouldBeBooleanObjectFalse) {
      @junit.framework.Assert::assertEquals(Ljava/lang/String;Ljava/lang/String;)("object", typeof shouldBeBooleanObjectFalse);
      @junit.framework.Assert::assertTrue(Z)(shouldBeBooleanObjectFalse instanceof Boolean);
      @junit.framework.Assert::assertFalse(Z)(shouldBeBooleanObjectFalse.valueOf());
      // that was the failing code in issue 8548, so test it explicitly:
      @junit.framework.Assert::assertTrue(Z)(!!shouldBeBooleanObjectFalse);
    }

    // Make sure we don't erroneously wrap values
    var returnsBooleanValueFalse = $entry(function() { return false; });
    assertIsBooleanValueFalse(returnsBooleanValueFalse());
    // try if with a Java method returning a Java primitive boolean (issue 8548)
    var returnsJavaPrimitiveBooleanFalse = $entry(@com.google.gwt.core.client.EntryTest::returnsFalse());
    assertIsBooleanValueFalse(returnsJavaPrimitiveBooleanFalse());

    // Make sure we don't erroneously unwrap objects
    var returnsBooleanObjectFalse = $entry(function() { return new Boolean(false); });
    assertIsBooleanObjectFalse(returnsBooleanObjectFalse());

    // Just to be sure, make sure we round-trip values correctly:
    var returnsFirstArgument = $entry(function(a) { return a; });
    assertIsBooleanValueFalse(returnsFirstArgument(false));
    assertIsBooleanObjectFalse(returnsFirstArgument(new Boolean(false)));
  }-*/;

  private static boolean returnsFalse() {
    return false;
  }
}
