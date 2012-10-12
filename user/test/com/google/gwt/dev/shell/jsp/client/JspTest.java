package com.google.gwt.dev.shell.jsp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.junit.client.GWTTestCase;

public class JspTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.dev.shell.jsp.JspTest";
  }

  /**
   * Makes a request to a JSP and checks the status code and response.
   */
  public void testJsp() throws RequestException {
    doTest("test.jsp");
  }

  /**
   * Tests Java 5 source support by using generics.
   * 
   * @see http://code.google.com/p/google-web-toolkit/issues/detail?id=3557
   */
  public void disabledTestJava5() throws RequestException {
    doTest("java5.jsp");
  }

  /**
   * Tests Java 6 source support by using @Override on a method coming from an
   * interface.
   * 
   * @see http://code.google.com/p/google-web-toolkit/issues/detail?id=3557
   */
  public void disabledTestJava6() throws RequestException {
    doTest("java6.jsp");
  }

  private void doTest(String page) throws RequestException {
    delayTestFinish(2000);

    RequestBuilder rb =
        new RequestBuilder(RequestBuilder.GET, GWT.getModuleBaseForStaticFiles() + page);
    rb.setCallback(new RequestCallback() {

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
    rb.send();
  }
}
