package com.google.gwt.user.rebind.rpc;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.javac.TypeOracleTestingUtils;
import com.google.gwt.dev.javac.testing.impl.StaticJavaResource;

import junit.framework.TestCase;

public class SerializationUtilsTest extends TestCase {

  public void testGetSerializationSignatureUseEnumConstants() throws Throwable {
    assertEquals("Identical enums have different signature",
        getEnumSerializationSignature("FOO, BAR, BAZ"),
        getEnumSerializationSignature("FOO, BAR, BAZ"));

    assertFalse("Enums w/ renamed constant have same signature",
        getEnumSerializationSignature("FOO, BAR, BAZ").equals(
            getEnumSerializationSignature("FOO, BAZ, BAR")));
    // reordering is equivalent to renaming, but let's test it anyway
    assertFalse("Enums w/ reordered constants have same signature",
        getEnumSerializationSignature("FOO, BAR, BAZ").equals(
            getEnumSerializationSignature("FOO, BAZ, BAR")));

    assertFalse("Enums w/ added constant have same signature",
        getEnumSerializationSignature("FOO, BAR, BAZ").equals(
            getEnumSerializationSignature("FOO, BAR, BAZ, QUUX")));
    assertFalse("Enums w/ removed constant have same signature",
        getEnumSerializationSignature("FOO, BAR, BAZ").equals(
            getEnumSerializationSignature("FOO, BAR")));

    assertEquals("Enums w/ changed implementation have different signature",
        getEnumSerializationSignature("FOO, BAR, BAZ"),
        getEnumSerializationSignature("FOO, BAR { @Override public String toString() { return \"QUUX\"; } }, BAZ"));
  }

  protected String getEnumSerializationSignature(String constants) throws NotFoundException {
    TypeOracle to = TypeOracleTestingUtils.buildStandardTypeOracleWith(TreeLogger.NULL,
        new StaticJavaResource("TestEnum", "public enum TestEnum { " + constants + " }"));
    JClassType enumType = to.getType("TestEnum");
    return SerializationUtils.getSerializationSignature(to, enumType);
  }
}
