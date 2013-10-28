package com.google.gwt.dev.util.arg;

import com.google.gwt.util.tools.ArgHandlerFlag;

/**
 * Emits detailed compile-report information in the "Story Of Your Compile" in the new json format.
 */
public class ArgHandlerJsonSoyc extends ArgHandlerFlag {

  private final OptionJsonSoyc optionJsonSoyc;

  public ArgHandlerJsonSoyc(OptionJsonSoyc options) {
    optionJsonSoyc = options;

    addTagValue("-XjsonSoyc", true);
  }

  @Override
  public String getPurposeSnippet() {
    return "Emit detailed compile-report information in the \"Story Of Your Compile\" "
        + " in the new json format.";
  }

  @Override
  public String getLabel() {
    return "jsonSoyc";
  }

  @Override
  public boolean isUndocumented() {
    return true;
  }

  @Override
  public boolean setFlag(boolean value) {
    optionJsonSoyc.setJsonSoycEnabled(value);
    return true;
  }

  @Override
  public boolean isExperimental() {
    return true;
  }

  @Override
  public boolean getDefaultValue() {
    return optionJsonSoyc.isJsonSoycEnabled();
  }
}
