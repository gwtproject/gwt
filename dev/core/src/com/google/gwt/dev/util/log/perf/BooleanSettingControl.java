package com.google.gwt.dev.util.log.perf;

import jdk.jfr.SettingControl;

import java.util.Set;

/**
 * A SettingControl for boolean settings. Defaults to true.
 */
public class BooleanSettingControl extends SettingControl {
  private boolean enabled;
  @Override
  public String combine(Set<String> set) {
    if (set.contains("true")) {
      return "true";
    }
    if (set.contains("false")) {
      return "false";
    }
    return "true";
  }

  @Override
  public void setValue(String s) {
    enabled = "true".equals(s);
  }

  @Override
  public String getValue() {
    return Boolean.toString(enabled);
  }

  public boolean get() {
    return enabled;
  }
}
