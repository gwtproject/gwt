package com.google.gwt.core.client;

import com.google.gwt.junit.client.GWTTestCase;

import java.util.HashSet;
import java.util.Set;

/**
 * Test our polyfill for Object.keys.
 */
public class ObjectKeysPolyfillTest extends GWTTestCase {

  private static native JsArrayString getKeys(JavaScriptObject o) /*-{
    return Object.keys(o);
  }-*/;

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.Core";
  }

  public void testGetKeys() {
    JsArrayString keys = getKeys(createSimpleObject());

    Set<String> set = toSet(keys);

    assertEquals(3, set.size());
    assertTrue(set.contains("a"));
    assertTrue(set.contains("b"));
    assertTrue(set.contains("c"));
  }

  public void testPrototypeProperties() {
    JsArrayString keys = getKeys(createObjectWithPropertiesOnProto());

    Set<String> set = toSet(keys);

    assertEquals(3, set.size());
    assertTrue(set.contains("childA"));
    assertTrue(set.contains("childB"));
    assertTrue(set.contains("childC"));
  }

  public void testEnumBug() {
    JsArrayString keys = getKeys(createObjectWithEnumBug());

    Set<String> set = toSet(keys);

    assertEquals(4, set.size());
    assertTrue(set.contains("a"));
    assertTrue(set.contains("b"));
    assertTrue(set.contains("c"));
    assertTrue(set.contains("toString"));
  }

  private Set<String> toSet(JsArrayString keys) {
    Set<String> set = new HashSet<String>();
    for (int i = 0; i < keys.length(); i++) {
      set.add(keys.get(i));
    }
    return set;
  }

  private native JavaScriptObject createObjectWithPropertiesOnProto() /*-{
    var parent = {parentA: 1, parentB: 2, parentC: 3};

    function Child() {
      this.childA = "1";
      this.childB = "2";
      this.childC = "3";
    }

    Child.prototype = parent;

    return new Child();
  }-*/;

  private native JavaScriptObject createObjectWithEnumBug() /*-{
    var o = {a : "1", b : "2", c : "3"};
    // create toString property with a value of null to trigger the enum bug
    // that is present in some browsers like IE8. Those browsers do not loop through
    // properties like toString if they are present on the object but null.
    o.toString = null;
    return o;
  }-*/;

  private native JavaScriptObject createSimpleObject() /*-{
    return {a : "1", b : "2", c : "3"};
  }-*/;
}
