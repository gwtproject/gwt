/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.user.client.impl;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * IE8 implementation of {@link com.google.gwt.user.client.impl.WindowImpl}.
 */
public class WindowImplIE8 extends WindowImplIE {

  @Override
  public native void addEventListener(String event, JavaScriptObject handler) /*-{
    $wnd.attachEvent('on' + event, handler);
  }-*/;

  @Override
  public native void removeEventListener(String event, JavaScriptObject handler) /*-{
    $wnd.detachEvent('on' + event, handler);
  }-*/;
}
