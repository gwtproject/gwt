/*
 * Copyright 2010 Google Inc.
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

package java.util.logging;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.logging.impl.LevelImpl;
import com.google.gwt.logging.impl.LevelImplNull;

import java.io.Serializable;

/**
 *  An emulation of the java.util.logging.Level class. See 
 *  <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/util/logging/Level.html"> 
 *  The Java API doc for details</a>
 */
public class Level implements Serializable {
  private static final LevelImpl IMPL = GWT.create(LevelImplNull.class);

  public static final Level ALL = new Level("ALL", Integer.MIN_VALUE);
  public static final Level CONFIG = new Level("CONFIG", 700);
  public static final Level FINE = new Level("FINE", 500);
  public static final Level FINER = new Level("FINER", 400);
  public static final Level FINEST = new Level("FINEST", 300);
  public static final Level INFO = new Level("INFO", 800);
  public static final Level OFF = new Level("OFF", Integer.MAX_VALUE);
  public static final Level SEVERE = new Level("SEVERE", 1000);
  public static final Level WARNING = new Level("WARNING", 900);

  public static Level parse(String name) {
    return IMPL.parse(name);
  } 

  private final String name;
  private final int value;

  protected Level(String name, int value) {
    if (name == null) {
      throw new NullPointerException();
    }

    this.name = name;
    this.value = value;
  }
  
  public String getName() {
    return name;
  }
  
  public int intValue() {
    return value;
  }
    
  @Override
  public String toString() {
    return name;
  }
  
  /* Not Implemented */
  // public boolean equals(Object ox) {} 
  // protected Level(String name, int value, String resourceBundleName) {} 
  // public String getLocalizedName() {}
  // public String getResourceBundleName() {} 
  // public int  hashCode() {}
}
