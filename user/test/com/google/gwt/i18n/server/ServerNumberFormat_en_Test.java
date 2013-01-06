/*
 * Copyright 2012 Google Inc.
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
package com.google.gwt.i18n.server;

import com.google.gwt.core.server.ServerGwtBridge;
import com.google.gwt.i18n.shared.NumberFormat;
import com.google.gwt.i18n.shared.NumberFormat_en_Test;

/**
 * Test {@link NumberFormat} in the {@code en} locale.
 */
public class ServerNumberFormat_en_Test extends NumberFormat_en_Test {

  public ServerNumberFormat_en_Test() {
    ServerGwtBridge.getInstance().setGlobalProperty("locale", "en");
  }
}
