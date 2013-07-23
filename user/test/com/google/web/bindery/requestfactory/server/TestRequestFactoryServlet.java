package com.google.web.bindery.requestfactory.server;

import com.google.web.bindery.requestfactory.shared.MethodProvidedByServiceLayerTest;
import com.google.web.bindery.requestfactory.vm.impl.OperationKey;

import java.lang.reflect.Method;

public class TestRequestFactoryServlet extends RequestFactoryServlet {

  public TestRequestFactoryServlet() {
    super(new DefaultExceptionHandler(), new MethodProvidedByServiceLayerJreTest.Decorator());
  }
}
