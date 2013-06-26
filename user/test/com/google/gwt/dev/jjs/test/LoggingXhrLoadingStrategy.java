/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.dev.jjs.test;

import com.google.gwt.core.client.impl.XhrLoadingStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * An Xhr based fragment loading strategy that logs fragment index + fragment text pairs for later
 * introspection.
 */
public class LoggingXhrLoadingStrategy extends XhrLoadingStrategy {
  public static Map<Integer, String> sourceByFragmentIndex = new HashMap<Integer, String>();

  @Override
  protected void tryInstall(RequestData request, String responseText) {
    sourceByFragmentIndex.put(request.getFragment(), responseText);
    super.tryInstall(request, responseText);
  }
}