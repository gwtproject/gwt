package com.google.gwt.dev.codeserver;

import static com.google.gwt.dev.codeserver.SourceHandler.SOURCEMAP_PATH;
import static com.google.gwt.dev.codeserver.SourceHandler.SOURCEMAP_SUFFIX;

import com.google.gwt.dev.util.Util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for SourceHandler class.
 */
public class SourceHandlerTest {

  /**
   * Test source map paths.
   */
  @Test
  public void testSourceMapPathInfo() {
    String moduleName = "myModule";
    String strongName = Util.computeStrongName("foo-bar".getBytes());
    SourceHandler srcHand = new SourceHandler(null, null);

    assertTrue(srcHand.isSourceMapRequest(SOURCEMAP_PATH + moduleName + "/"));
    assertTrue(srcHand.isSourceMapRequest(SOURCEMAP_PATH + moduleName + "/whatever"));
    assertTrue(srcHand.isSourceMapRequest(SOURCEMAP_PATH + moduleName + "/folder/"));
    assertTrue(srcHand.isSourceMapRequest(SOURCEMAP_PATH + moduleName + "/folder/file.ext"));
    assertTrue(srcHand.isSourceMapRequest(
        SOURCEMAP_PATH + moduleName + "/" + strongName + SOURCEMAP_SUFFIX));

    assertFalse(srcHand.isSourceMapRequest(SOURCEMAP_PATH + moduleName));
    assertFalse(srcHand.isSourceMapRequest("whatever" + SOURCEMAP_PATH + moduleName + "/"));

    assertEquals(moduleName, srcHand.getModuleNameFromRequest(SOURCEMAP_PATH + moduleName + "/"));
    assertEquals(moduleName, srcHand.getModuleNameFromRequest(
        SOURCEMAP_PATH + moduleName + "/" + strongName + SOURCEMAP_SUFFIX));

    assertEquals(strongName, srcHand.getStrongNameFromRequest(
        SOURCEMAP_PATH + moduleName + "/" + strongName + SOURCEMAP_SUFFIX));
    assertNull(srcHand.getStrongNameFromRequest(
        SOURCEMAP_PATH + moduleName + "/invalid_hash" + SOURCEMAP_SUFFIX));
  }
}
