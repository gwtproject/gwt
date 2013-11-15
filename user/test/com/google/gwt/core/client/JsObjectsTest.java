/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.core.client;

import com.google.gwt.junit.client.GWTTestCase;

import junit.framework.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * Tests JsArray variants.
 */
public class JsObjectsTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.core.CoreWithUserAgent";
  }

  public void testGetKeys() {
    JsArrayString keys = JsObjects.getKeys(createSimpleObject());

    Set<String> set = toSet(keys);

    Assert.assertEquals(3, set.size());
    Assert.assertTrue(set.contains("a"));
    Assert.assertTrue(set.contains("b"));
    Assert.assertTrue(set.contains("c"));
  }

  public void testPrototypeProperties() {
    JsArrayString keys = JsObjects.getKeys(createObjectWithPropertiesOnProto());

    Set<String> set = toSet(keys);

    Assert.assertEquals(3, set.size());
    Assert.assertTrue(set.contains("childA"));
    Assert.assertTrue(set.contains("childB"));
    Assert.assertTrue(set.contains("childC"));
  }

  public void testEnumBug() {
    JsArrayString keys = JsObjects.getKeys(createObjectWithEnumBug());

    Set<String> set = toSet(keys);

    Assert.assertEquals(4, set.size());
    Assert.assertTrue(set.contains("a"));
    Assert.assertTrue(set.contains("b"));
    Assert.assertTrue(set.contains("c"));
    Assert.assertTrue(set.contains("toString"));
  }

  private Set<String> toSet(JsArrayString keys) {
    Set<String> set = new HashSet<String>();
    for (int i = 0; i < keys.length(); i++) {
      set.add(keys.get(i));
    }
    return set;
  }

  private native JavaScriptObject createObjectWithPropertiesOnProto() /*-{
    var parent = {parentA:1, parentB:2, parentC:3};

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
    // create bug
    o.toString = null;
    return o;
  }-*/;

  private native JavaScriptObject createSimpleObject() /*-{
    return {a : "1", b : "2", c : "3"};
  }-*/;
}
