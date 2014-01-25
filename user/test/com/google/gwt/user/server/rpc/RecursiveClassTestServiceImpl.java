/*
 * Copyright 2010 Google Inc.
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
package com.google.gwt.user.server.rpc;

import com.google.gwt.user.client.rpc.RecursiveClassTestService;

/**
 * The server-side implementation of the RPC service.
 *
 */
public class RecursiveClassTestServiceImpl extends RemoteServiceServlet
    implements RecursiveClassTestService {

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.rpc.RecursiveClassTestService#greetServer(java.lang.String)
   */
  @Override
  public <T extends ResultNode<T>> ResultNode<T> greetServer(String name)
      throws IllegalArgumentException {
     return new ResultNode<T>();
  }
  
}
