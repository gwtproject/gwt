/*
 * Copyright 2012 Google Inc.
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

import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * Special {@link RequestImpl} for IE6-9 to work around some IE specialities.
 * 
 * @author <a href="mailto:jb@barop.de">Johannes Barop</a>
 * 
 */
class RequestImplIE extends RequestImpl {

  @Override
  Response createResponse(XMLHttpRequest xmlHttpRequest) {
    return new XMLHttpRequestResponse(xmlHttpRequest) {

      @Override
      public int getStatusCode() {
        /*
         * http://code.google.com/p/google-web-toolkit/issues/detail?id=5031
         * 
         * The XMLHTTPRequest object in IE will return a status code of 1223 and drop some response
         * headers if the server returns a HTTP/204.
         * 
         * This issue is fixed in IE10.
         */
        int statusCode = super.getStatusCode();
        return (statusCode == 1223) ? SC_NO_CONTENT : statusCode;
      }

    };
  }

}
