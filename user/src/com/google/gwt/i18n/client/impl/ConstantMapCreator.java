package com.google.gwt.i18n.client.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to create <code>Constants</code> maps
 * and is to be used only by the GWT code. The map is immediately wrapped in
 * Collections.unmodifiableMap(..) preventing any changes after construction.
 */
public final class ConstantMapCreator {
  private ConstantMapCreator() {}

  public static Map<String, String> create(String keys[], String values[]) {
    HashMap<String, String> map = new HashMap<String, String>();
    for (int i = 0; i < keys.length; ++i) {
      assert keys[i] != null;
      assert values[i] != null;
      map.put(keys[i], values[i]);
    }
    return Collections.unmodifiableMap(map);
  }
}
