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
 * Native implementation associated with {@link Request}. User classes should not use this class
 * directly.
 * 
 * @author <a href="mailto:jb@barop.de">Johannes Barop</a>
 */
class RequestImpl {

  /**
   * Creates a {@link Response} instance for the given JavaScript XmlHttpRequest object.
   * 
   * @param xmlHttpRequest xmlHttpRequest object for which we need a response
   * @return a {@link Response} object instance
   */
  Response createResponse(final XMLHttpRequest xmlHttpRequest) {
    return new XMLHttpRequestResponse(xmlHttpRequest);
  }

}
