package com.google.gwt.dev.util.arg;

/**
 * Option to determine whether the runtime shell should disable support
 * of old Emma versions.
 */
public interface OptionDisableOldEmmaSupport {

  /**
   * Returns true if the compiler should disable the old Emma support.
   */
  boolean shouldDisableOldEmmaSupport();

  /**
   * Sets whether or not the compiler should disable the old Emma support.
   */
  void setDisableOldEmmaSupport(boolean enabled);
}
