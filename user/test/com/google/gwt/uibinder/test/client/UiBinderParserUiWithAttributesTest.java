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
package com.google.gwt.uibinder.test.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;

/**
 * Tests &lt;ui:attribute>.
 */
public class UiBinderParserUiWithAttributesTest extends GWTTestCase {

  static class TestBeanA {
  }

  static class TestBeanB {
    TestBeanA beanA;

    public void setBeanA(TestBeanA beanA) {
      this.beanA = beanA;
    }
  }

  static class TestBeanC {
    TestBeanA beanA;
    TestBeanB beanB;
    
    @UiConstructor
    public TestBeanC(TestBeanA beanA) {
      this.beanA = beanA;
    }
    
    public void setBeanB(TestBeanB beanB) {
      this.beanB = beanB;
    }
  }

  static class TestBeanD {
    String value1;
    String value2;

    @UiConstructor
    public TestBeanD(String value1) {
      this.value1 = value1;
    }

    public void setValue2(String value2) {
      this.value2 = value2;
    }
  }

  static class TestBeanE {
    String value1;

    @UiConstructor
    public TestBeanE() {
    }

    public void setValue1(String value1) {
      this.value1 = value1;
    }
  }

  static class Ui {
    interface Binder extends UiBinder<Element, Ui> {
    }
    
    static final Binder binder = GWT.create(Binder.class);
    
    @UiField(provided = true)
    TestBeanA test1 = new TestBeanA();
    
    @UiField
    TestBeanA test2;
    
    @UiField
    TestBeanB test3;
    
    @UiField
    TestBeanC test4;
    
    @UiField
    TestBeanC test5;
    
    @UiField
    TestBeanD test6;

    @UiField
    TestBeanD test7;

    @UiField
    TestBeanE test8;

    @UiField
    TestBeanE test9;

    @UiFactory
    protected TestBeanD createTestBeanD(String value1) {
      return new TestBeanD(value1);
    }

    @UiFactory
    protected TestBeanE createTestBeanE() {
      return new TestBeanE();
    }

    Ui() {
      binder.createAndBindUi(this);
    }
  }

  static class UiMissingParameter {
    interface Binder extends UiBinder<Element, UiMissingParameter> {
    }

    static final Binder binder = GWT.create(Binder.class);

    @UiField
    TestBeanD test1;

    @UiFactory
    protected TestBeanD createTestBeanD(String value1) {
      return new TestBeanD(value1);
    }

    UiMissingParameter() {
      binder.createAndBindUi(this);
    }
  }

  static class UiIncompleteParameters {
    interface Binder extends UiBinder<Element, UiIncompleteParameters> {
    }

    static final Binder binder = GWT.create(Binder.class);

    @UiField
    TestBeanD test1;

    @UiFactory
    protected TestBeanD createTestBeanD(String value1, String value2) {
      return new TestBeanD(value1 + value2);
    }

    UiIncompleteParameters() {
      binder.createAndBindUi(this);
    }
  }

  @Override
  public String getModuleName() {
    return "com.google.gwt.uibinder.test.LazyWidgetBuilderSuite";
  }
  
  public void testUiWith() {
    Ui ui = new Ui();
    
    assertNotNull(ui.test1);
    assertNotNull(ui.test2);
    
    assertNotNull(ui.test3);
    assertNotNull(ui.test3.beanA);
    assertSame(ui.test1, ui.test3.beanA);
    
    assertNotNull(ui.test4);
    assertSame(ui.test1, ui.test4.beanA);
    assertNull(ui.test4.beanB);
    
    assertNotNull(ui.test5);
    assertSame(ui.test1, ui.test5.beanA);
    assertSame(ui.test3, ui.test5.beanB);

    assertNotNull(ui.test6);
    assertNotNull(ui.test6.value1);
    assertEquals("myValue1", ui.test6.value1);
    assertNull(ui.test6.value2);

    assertNotNull(ui.test7);
    assertNotNull(ui.test7.value1);
    assertEquals("myValue1", ui.test7.value1);
    assertNotNull(ui.test7.value2);
    assertEquals("myValue2", ui.test7.value2);

    assertNotNull(ui.test8);
    assertNull(ui.test8.value1);

    assertNotNull(ui.test9);
    assertNotNull(ui.test9.value1);
    assertEquals("myValue1", ui.test9.value1);
  }

  public void testMissingConstructorParameter() {
    try {
      UiMissingParameter ui = new UiMissingParameter();
      fail("missing parameter not found");
    } catch (Throwable e) {
    }
  }

  public void testIncompleteConstructorParameter() {
    try {
      UiIncompleteParameters ui = new UiIncompleteParameters();
      fail("missing parameters not founds");
    } catch (Throwable e) {
    }
  }
}
