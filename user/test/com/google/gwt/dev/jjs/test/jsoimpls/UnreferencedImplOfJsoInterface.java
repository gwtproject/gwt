/*
 * Copyright 2009 Google Inc.
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
package com.google.gwt.dev.jjs.test.jsoimpls;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dev.jjs.test.jsointfs.JsoInterfaceWithUnreferencedImpl;

/**
 * This class exists for the purpose of testing JSO implementation types that
 * aren't specifically referenced in any Java source.
 */
public final class UnreferencedImplOfJsoInterface extends JavaScriptObject implements
    JsoInterfaceWithUnreferencedImpl {
  protected UnreferencedImplOfJsoInterface() {
  }

  @Override
  public boolean isOk() {
    if (2 + 2 == 4) {
      return true;
    } else {
      return false;
    }
  }
}
