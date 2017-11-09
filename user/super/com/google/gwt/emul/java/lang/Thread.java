/*
 * Copyright 2017 Google Inc.
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
 * Minimal implementation of Thread.
 * <p>Since there are no compatible Thread implementations in any JavaScript engine, this class
 * misses most of the regular Java methods.
 */
public final class Thread {

  public static boolean interrupted() {
    return false;
  }

  // We don't allow for instances of Thread since we can not emulate them in JS.
  private Thread() {
  }
}
