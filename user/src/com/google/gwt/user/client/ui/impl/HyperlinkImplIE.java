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

package com.google.gwt.user.client.ui.impl;

import com.google.gwt.user.client.Event;

/**
 * IE version of HyperlinkImpl.
 */
public class HyperlinkImplIE extends HyperlinkImpl {

  @Override
  public boolean handleAsClick(Event event) {
    int mouseButtons = event.getButton();
    boolean ctrl = event.getCtrlKey();
    boolean shift = event.getShiftKey();
    boolean middle = mouseButtons == Event.BUTTON_MIDDLE;
    boolean right = mouseButtons == Event.BUTTON_RIGHT;
    boolean modifiers = shift || ctrl;
    return !modifiers && !middle && !right;
  }
}
