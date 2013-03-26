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
package com.google.gwt.user.server.rpc;

/**
 * A client that downloads serialization policies from a URL, for use with
 * Super Dev Mode.
 * @see RemoteServiceServlet#getCodeServerClient
 */
public interface SerializationPolicyClient {

  /**
   * Attempts to read a policy from a given URL.
   * Logs any errors to the given interface.
   * @return the policy or null if unavailable.
   */
  SerializationPolicy loadPolicy(String url, Logger logger);

  /**
   * Destination for the loader's log messages.
   */
  interface Logger {
    void log(String message);
    void log(String message, Throwable throwable);
  }
}
