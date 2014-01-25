/*
 * Copyright 2007 Google Inc.
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
package com.google.gwt.http.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * TODO: document me.
 */
public class RequestTest extends RequestTestBase {

  private static String getTestBaseURL() {
    return GWT.getModuleBaseURL() + "testRequest/";
  }

  @Override
  public String getModuleName() {
    return "com.google.gwt.http.RequestTest";
  }

  /**
   * Test method for {@link com.google.gwt.http.client.Request#cancel()}.
   */
  public void testCancel() {
    delayTestFinishForRequest();

    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
        getTestBaseURL() + "/cancel");
    try {
      Request request = builder.sendRequest(null, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
          fail("Request was canceled - no response should be received");
        }

        @Override
        public void onError(Request request, Throwable exception) {
          fail("Request was canceled - no timeout should occur");
        }
      });

      assertTrue(request.isPending());
      request.cancel();
      assertFalse(request.isPending());

      finishTest();
    } catch (RequestException e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.google.gwt.http.client.Request#Request(XMLHttpRequest, int, RequestCallback)}.
   */
  public void testRequest() {
    RequestCallback callback = new RequestCallback() {
      @Override
      public void onResponseReceived(Request request, Response response) {
      }

      @Override
      public void onError(Request request, Throwable exception) {
      }
    };

    try {
      new Request(null, 0, callback);
      fail();
    } catch (NullPointerException ex) {
      // Success (The Request ctor explicitly throws an NPE).
    }

    try {
      new Request(XMLHttpRequest.create(), -1, callback);
      fail();
    } catch (IllegalArgumentException ex) {
      // Success.
    }

    try {
      new Request(XMLHttpRequest.create(), -1, null);
      fail();
    } catch (NullPointerException ex) {
      // Success (The Request ctor explicitly throws an NPE).
    }

    try {
      new Request(XMLHttpRequest.create(), 0, callback);
    } catch (Throwable ex) {
      fail(ex.getMessage());
    }
  }

  /**
   * Test method for {@link com.google.gwt.http.client.Request#isPending()}.
   */
  public void testIsPending() {
    // delayTestFinishForRequest();

    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
        getTestBaseURL() + "isPending");
    try {
      Request request = builder.sendRequest(null, new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
          finishTest();
        }

        @Override
        public void onError(Request request, Throwable exception) {
          finishTest();
        }
      });

      assertTrue(request.isPending());
      // finishTest();
    } catch (RequestException e) {
      fail(e.getMessage());
    }
  }

  /*
   * Checks that the status code is correct when receiving a 204-No-Content. This needs special
   * handling in IE6-9. See http://code.google.com/p/google-web-toolkit/issues/detail?id=5031
   */
  public void test204NoContent() {
    delayTestFinishForRequest();

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, getTestBaseURL() + "204NoContent");
    try {
      builder.sendRequest(null, new RequestCallback() {

        @Override
        public void onResponseReceived(Request request, Response response) {
          assertEquals(204, response.getStatusCode());
          finishTest();
        }

        @Override
        public void onError(Request request, Throwable exception) {
          fail(exception.getMessage());
        }
      });
    } catch (RequestException e) {
      fail(e.getMessage());
    }
  }
}
