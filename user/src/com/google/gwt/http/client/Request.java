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
package com.google.gwt.http.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * An HTTP request that is waiting for a response. Requests can be queried for
 * their pending status or they can be canceled.
 * 
 * <h3>Required Module</h3> Modules that use this class should inherit
 * <code>com.google.gwt.http.HTTP</code>.
 * 
 * {@gwt.include
 * com/google/gwt/examples/http/InheritsExample.gwt.xml}
 * 
 */
public class Request {

  /**
   * Native implementation associated with {@link Request}. User classes should not use this class
   * directly.
   */
  static class RequestImpl {

    /**
     * Creates a {@link Response} instance for the given JavaScript XmlHttpRequest object.
     *
     * @param xmlHttpRequest xmlHttpRequest object for which we need a response
     * @return a {@link Response} object instance
     */
    Response createResponse(final XMLHttpRequest xmlHttpRequest) {
      return new ResponseImpl(xmlHttpRequest);
    }
  }

  /**
   * Special {@link RequestImpl} for IE8, IE9 to work around some IE specialities.
   */
  static class RequestImplIE8And9 extends RequestImpl {

    @Override
    Response createResponse(XMLHttpRequest xmlHttpRequest) {
      return new ResponseImpl(xmlHttpRequest) {

        @Override
        public int getStatusCode() {
          /*
           * http://code.google.com/p/google-web-toolkit/issues/detail?id=5031
           *
           * The XMLHTTPRequest object in IE will return a status code of 1223 and drop some
           * response headers if the server returns a HTTP/204.
           *
           * This issue is fixed in IE10.
           */
          int statusCode = super.getStatusCode();
          return (statusCode == 1223) ? SC_NO_CONTENT : statusCode;
        }
      };
    }
  }

  /*
   * Although Request is a client-side class, it's a transitive dependency of
   * some GWT servlet code.  Because GWT.create() isn't safe to call on the
   * server, we use the "Initialization On Demand Holder" idiom to lazily
   * initialize the RequestImpl.
   */
  private static class ImplHolder {
    private static final RequestImpl impl = GWT.create(RequestImpl.class);

    public static RequestImpl get() {
      return impl;
    }
  }

  /**
   * Creates a {@link Response} instance for the given JavaScript XmlHttpRequest
   * object.
   * 
   * @param xmlHttpRequest xmlHttpRequest object for which we need a response
   * @return a {@link Response} object instance
   */
  private static Response createResponse(final XMLHttpRequest xmlHttpRequest) {
    return ImplHolder.get().createResponse(xmlHttpRequest);
  }

  private final RequestCallback callback;

  /**
   * The number of milliseconds to wait for this HTTP request to complete.
   */
  private final int timeoutMillis;

  /**
   * Timer used to force HTTPRequest timeouts. If the user has not requested a
   * timeout then this field is null.
   */
  private final Timer timer = new Timer() {
    @Override
    public void run() {
      fireOnTimeout();
    }
  };

  /**
   * JavaScript XmlHttpRequest object that this Java class wraps. This field is
   * not final because we transfer ownership of it to the HTTPResponse object
   * and set this field to null.
   */
  private XMLHttpRequest xmlHttpRequest;

  /**
   * Only used for building a
   * {@link com.google.gwt.user.client.rpc.impl.FailedRequest}.
   */
  protected Request() {
    callback = null;
    timeoutMillis = 0;
    xmlHttpRequest = null;
  }

  /**
   * Constructs an instance of the Request object.
   * 
   * @param xmlHttpRequest JavaScript XmlHttpRequest object instance
   * @param timeoutMillis number of milliseconds to wait for a response
   * @param callback callback interface to use for notification
   * 
   * @throws IllegalArgumentException if timeoutMillis &lt; 0
   * @throws NullPointerException if xmlHttpRequest, or callback are null
   */
  Request(XMLHttpRequest xmlHttpRequest, int timeoutMillis, RequestCallback callback) {
    if (xmlHttpRequest == null) {
      throw new NullPointerException();
    }

    if (callback == null) {
      throw new NullPointerException();
    }

    if (timeoutMillis < 0) {
      throw new IllegalArgumentException();
    }

    this.callback = callback;
    this.timeoutMillis = timeoutMillis;
    this.xmlHttpRequest = xmlHttpRequest;

    if (timeoutMillis > 0) {
      timer.schedule(timeoutMillis);
    }
  }

  /**
   * Cancels a pending request. If the request has already been canceled or if
   * it has timed out no action is taken.
   */
  public void cancel() {
    if (xmlHttpRequest == null) {
      return;
    }

    timer.cancel();

    /*
     * There is a strange race condition that occurs on Mozilla when you cancel
     * a request while the response is coming in. It appears that in some cases
     * the onreadystatechange handler is still called after the handler function
     * has been deleted and during the call to XmlHttpRequest.abort(). So we
     * null the xmlHttpRequest here and that will prevent the
     * fireOnResponseReceived method from calling the callback function.
     * 
     * Setting the onreadystatechange handler to null gives us the correct
     * behavior in Mozilla but crashes IE. That is why we have chosen to fixed
     * this in Java by nulling out our reference to the XmlHttpRequest object.
     */
    final XMLHttpRequest xhr = xmlHttpRequest;
    xmlHttpRequest = null;

    xhr.clearOnReadyStateChange();
    xhr.abort();
  }

  /**
   * Returns true if this request is waiting for a response.
   * 
   * @return true if this request is waiting for a response
   */
  public boolean isPending() {
    if (xmlHttpRequest == null) {
      return false;
    }

    int readyState = xmlHttpRequest.getReadyState();

    /*
     * Because we are doing asynchronous requests it is possible that we can
     * call XmlHttpRequest.send and still have the XmlHttpRequest.getReadyState
     * method return the state as XmlHttpRequest.OPEN. That is why we include
     * open although it is nottechnically true since open implies that the
     * request has not been sent.
     */
    switch (readyState) {
      case XMLHttpRequest.OPENED:
      case XMLHttpRequest.HEADERS_RECEIVED:
      case XMLHttpRequest.LOADING:
        return true;
    }

    return false;
  }

  /*
   * Method called when the JavaScript XmlHttpRequest object's readyState
   * reaches 4 (LOADED).
   */
  void fireOnResponseReceived(RequestCallback callback) {
    if (xmlHttpRequest == null) {
      // the request has timed out at this point
      return;
    }

    timer.cancel();

    /*
     * We cannot use cancel here because it would clear the contents of the
     * JavaScript XmlHttpRequest object so we manually null out our reference to
     * the JavaScriptObject
     */
    final XMLHttpRequest xhr = xmlHttpRequest;
    xmlHttpRequest = null;

    Response response = createResponse(xhr);
    callback.onResponseReceived(this, response);
  }

  /*
   * Method called when this request times out.
   */
  private void fireOnTimeout() {
    if (xmlHttpRequest == null) {
      // the request has been received at this point
      return;
    }

    cancel();

    callback.onError(this, new RequestTimeoutException(this, timeoutMillis));
  }
}
