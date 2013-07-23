package com.google.web.bindery.requestfactory.server;

import com.google.web.bindery.requestfactory.gwt.client.RequestFactoryGenericsTest;

public class RequestFactoryGenericsJreTest extends RequestFactoryGenericsTest {

  @Override
  public String getModuleName() {
    return null;
  }

  @Override
  protected Factory createFactory() {
    return RequestFactoryJreTest.createInProcess(Factory.class);
  }
}
