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
package com.google.gwt.user;

import com.google.gwt.junit.tools.GWTTestSuite;
import com.google.gwt.user.client.ui.AbsolutePanelTest;
import com.google.gwt.user.client.ui.AnchorTest;
import com.google.gwt.user.client.ui.ButtonTest;
import com.google.gwt.user.client.ui.CaptionPanelTest;
import com.google.gwt.user.client.ui.CheckBoxTest;
import com.google.gwt.user.client.ui.CompositeTest;
import com.google.gwt.user.client.ui.CreateEventTest;
import com.google.gwt.user.client.ui.CustomButtonTest;
import com.google.gwt.user.client.ui.CustomScrollPanelTest;
import com.google.gwt.user.client.ui.DOMRtlTest;
import com.google.gwt.user.client.ui.DOMTest;
import com.google.gwt.user.client.ui.DateBoxTest;
import com.google.gwt.user.client.ui.DatePickerTest;
import com.google.gwt.user.client.ui.DeckLayoutPanelTest;
import com.google.gwt.user.client.ui.DeckPanelTest;
import com.google.gwt.user.client.ui.DecoratedPopupTest;
import com.google.gwt.user.client.ui.DecoratedStackPanelTest;
import com.google.gwt.user.client.ui.DecoratedTabBarTest;
import com.google.gwt.user.client.ui.DecoratedTabPanelTest;
import com.google.gwt.user.client.ui.DecoratorPanelTest;
import com.google.gwt.user.client.ui.DefaultSuggestionDisplayTest;
import com.google.gwt.user.client.ui.DelegatingKeyboardListenerCollectionTest;
import com.google.gwt.user.client.ui.DialogBoxTest;
import com.google.gwt.user.client.ui.DirectionalTextHelperTest;
import com.google.gwt.user.client.ui.DisclosurePanelTest;
import com.google.gwt.user.client.ui.DockLayoutPanelRtlTest;
import com.google.gwt.user.client.ui.DockLayoutPanelTest;
import com.google.gwt.user.client.ui.DockPanelTest;
import com.google.gwt.user.client.ui.ElementWrappingTest;
import com.google.gwt.user.client.ui.FastStringMapTest;
import com.google.gwt.user.client.ui.FileUploadTest;
import com.google.gwt.user.client.ui.FiniteWidgetIteratorTest;
import com.google.gwt.user.client.ui.FlexTableTest;
import com.google.gwt.user.client.ui.FlowPanelTest;
import com.google.gwt.user.client.ui.FocusPanelTest;
import com.google.gwt.user.client.ui.FormPanelTest;
import com.google.gwt.user.client.ui.GridTest;
import com.google.gwt.user.client.ui.impl.ClippedImagePrototypeTest;

import junit.framework.Test;

/**
 * Tests in the user.client.ui package that start with A-G.
 * @see UiPart2Suite
 */
public class UiPart1Suite {
  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("Test for suite for all user widgets");

    suite.addTestSuite(AbsolutePanelTest.class);
    suite.addTestSuite(AnchorTest.class);
    suite.addTestSuite(ButtonTest.class);
    suite.addTestSuite(CaptionPanelTest.class);
    suite.addTestSuite(CheckBoxTest.class);
    suite.addTestSuite(ClassInitTest.class);
    suite.addTestSuite(ClippedImagePrototypeTest.class);
    suite.addTestSuite(CompositeTest.class);
    suite.addTestSuite(CreateEventTest.class);
    suite.addTestSuite(CustomButtonTest.class);
    suite.addTestSuite(CustomScrollPanelTest.class);
    suite.addTestSuite(DateBoxTest.class);
    suite.addTestSuite(DatePickerTest.class);
    suite.addTestSuite(DeckLayoutPanelTest.class);
    suite.addTestSuite(DeckPanelTest.class);
    suite.addTestSuite(DecoratedPopupTest.class);
    suite.addTestSuite(DecoratedStackPanelTest.class);
    suite.addTestSuite(DecoratedTabBarTest.class);
    suite.addTestSuite(DecoratedTabPanelTest.class);
    suite.addTestSuite(DecoratorPanelTest.class);
    suite.addTestSuite(DefaultSuggestionDisplayTest.class);
    suite.addTestSuite(DelegatingKeyboardListenerCollectionTest.class);
    suite.addTestSuite(DialogBoxTest.class);
    suite.addTestSuite(DirectionalTextHelperTest.class);
    suite.addTestSuite(DisclosurePanelTest.class);
    suite.addTestSuite(DockLayoutPanelRtlTest.class);
    suite.addTestSuite(DockLayoutPanelTest.class);
    suite.addTestSuite(DockPanelTest.class);
    suite.addTestSuite(DOMTest.class);
    suite.addTestSuite(DOMRtlTest.class);
    suite.addTestSuite(ElementWrappingTest.class);
    suite.addTestSuite(FastStringMapTest.class);
    suite.addTestSuite(FileUploadTest.class);
    suite.addTestSuite(FiniteWidgetIteratorTest.class);
    suite.addTestSuite(FlexTableTest.class);
    suite.addTestSuite(FlowPanelTest.class);
    suite.addTestSuite(FocusPanelTest.class);
    suite.addTestSuite(FormPanelTest.class);
    suite.addTestSuite(GridTest.class);

    return suite;
  }
}
