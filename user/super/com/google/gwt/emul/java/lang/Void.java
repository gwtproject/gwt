/*
 * Copyright 2007 Google Inc.
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
package java.lang;

/**
 * For JRE compatibility.
 */
public final class Void {

  public static final Class<Void> TYPE = void.class;

  /**
   * Not instantiable.
   */
  private Void() {
  }

  // CHECKSTYLE_OFF: Utility Methods for unboxed Void.
  protected static Void $create() {
    throw new AssertionError();
  }
}
