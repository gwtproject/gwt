package com.google.gwt.dev.jjs.test;

import com.google.gwt.dev.util.arg.SourceLevel;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.JUnitShell;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Dummy test case. Java9Test is super sourced so that GWT can be compiled by Java 8.
 *
 * NOTE: Make sure this class has the same test methods of its supersourced variant.
 */
@DoNotRunWith(Platform.Devel)
public class Java9Test extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "com.google.gwt.dev.jjs.Java9Test";
    }

    @Override
    public void runTest() throws Throwable {
        // Only run these tests if -sourceLevel 9 (or greater) is enabled.
        if (isGwtSourceLevel9()) {
            super.runTest();
        }
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

    private boolean isGwtSourceLevel9() {
        return JUnitShell.getCompilerOptions().getSourceLevel().compareTo(SourceLevel.JAVA9) >= 0;
    }
}
