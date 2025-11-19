package com.google.gwt.dev.cfg;

import com.google.gwt.dev.util.log.perf.AbstractJfrEvent;
import com.google.gwt.dev.util.log.perf.BooleanSettingControl;
import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.SettingDefinition;

public class ModuleDefEvent extends AbstractJfrEvent {
  @Label("Phase")
  @Description("The phase of module loading")
  final String phase;

  @Label("Module Name")
  @Description("Fully qualified name of the GWT Module")
  final String moduleName;

  public ModuleDefEvent(String phase, String moduleName) {
    this.phase = phase;
    this.moduleName = moduleName;
  }

  @SettingDefinition
  @Label("Enable recording Module events")
  @Description("Enables recording Module events, which will include package names")
  public boolean filter(BooleanSettingControl enabled) {
    return enabled.get();
  }
}