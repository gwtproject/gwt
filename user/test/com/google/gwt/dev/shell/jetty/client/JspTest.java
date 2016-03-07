package com.google.gwt.dev.shell.jetty.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Checks that JSPs are supported in JettyLauncher (through JUnitShell)
 */
public class JspTest extends GWTTestCase {
  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.shell.jetty.Jsp";
  }

  public void testJsp() throws Exception {
    delayTestFinish(5000);
    new RequestBuilder(RequestBuilder.GET, GWT.getModuleBaseForStaticFiles() + "java7.jsp")
        .sendRequest("", new RequestCallback() {
          @Override
          public void onResponseReceived(Request request, Response response) {
            assertEquals(200, response.getStatusCode());
            assertEquals("OK", response.getText().trim());
            finishTest();
          }

          @Override
          public void onError(Request request, Throwable exception) {
            fail();
          }
        });
  }
}
