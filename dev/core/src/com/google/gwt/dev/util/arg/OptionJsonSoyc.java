package com.google.gwt.dev.util.arg;

/**
 * Option to request the SOYC Compile Report output in the new json format.
 */
public interface OptionJsonSoyc extends OptionCompilerMetricsEnabled{

  /**
   * Returns true if the compiler should record and emit Compile Report information in json format.
   */
  boolean isJsonSoycEnabled();

  /**
   * Sets whether or not the compiler should record and emit Compile Report information in json
   * format.
   */
  void setJsonSoycEnabled(boolean value);


}
