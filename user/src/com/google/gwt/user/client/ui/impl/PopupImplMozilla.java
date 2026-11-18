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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;

/**
 * Implementation class used by {@link com.google.gwt.user.client.ui.PopupPanel}.
 * This implementation is identical to the implementation provided by
 * {@link com.google.gwt.user.client.ui.impl.PopupImpl} in the case where
 * Mozilla is NOT running on the Mac.
 * <p>
 * A different implementation is provided for the Mac in order to prevent
 * scrollbars underneath the PopupPanel from being rendered on top of the
 * PopupPanel (issue #410). Unfortunately, the solution that fixes this problem
 * for the Mac causes a problem with dragging a
 * {@link com.google.gwt.user.client.ui.DialogBox} on Linux. While dragging the
 * DialogBox (especially diagonally), it jitters significantly.
 * </p>
 * <p>
 * We did not introduce a deferred binding rule for Mozilla on the Mac because
 * this is the first instance in which we have a Mozilla-related bug fix which
 * does not work on all platforms.
 * </p>
 * <p>
 * This implementation can be simplified in the event that the jittering problem
 * on Linux is fixed, or the scrollbar rendering problem on the Mac is fixed.
 * </p>
 */
public class PopupImplMozilla extends PopupImpl {

  @Override
  public void setClip(Element popup, String rect) {
    super.setClip(popup, rect);
    popup.getStyle().setDisplay(Display.NONE);
    popup.getStyle().clearDisplay();
  }
}
