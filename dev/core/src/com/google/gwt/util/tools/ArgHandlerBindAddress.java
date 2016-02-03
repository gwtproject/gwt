/*
 * Copyright 2016 Google Inc.
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
package com.google.gwt.util.tools;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Argument handler for processing a bind address flags that takes an ip address or host name as its parameter.
 */
public abstract class ArgHandlerBindAddress extends ArgHandlerString {

  public static final String LOCALHOST_ADDRESS = "127.0.0.1";
  public static final String LOCALHOST_NAME = "localhost";

  protected static final String BIND_TAG = "-bindAddress";

  private String argAddress;
  private InetAddress preferredAddress;
  private InetAddress resolvedAddress;

  @Override
  public String[] getDefaultArgs() {
    return new String[]{getTag(), LOCALHOST_ADDRESS};
  }

  @Override
  public String getPurpose() {
    return "Specifies the bind address to be used (defaults to " + LOCALHOST_ADDRESS + ")";
  }

  public String getArgAddress() {
    return argAddress;
  }

  public InetAddress getPreferredAddress() throws UnknownHostException {
    if (preferredAddress == null) {
      if (getResolvedAddress().isAnyLocalAddress()) {
        // make a guess as to the preferred binding to use by looking up the canonical form of localhost
        preferredAddress = InetAddress.getByName(InetAddress.getLocalHost().getCanonicalHostName());
      } else {
        preferredAddress = resolvedAddress;
      }
    }
    return preferredAddress;
  }

  public InetAddress getResolvedAddress() throws UnknownHostException {
    if (resolvedAddress == null) {
      resolvedAddress = InetAddress.getByName(argAddress);
    }
    return resolvedAddress;
  }

  @Override
  public String getTag() {
    return BIND_TAG;
  }

  @Override
  public String[] getTagArgs() {
    return new String[]{"ip-or-host"};
  }

  public abstract boolean setBindAddress(String argValue, String hostAddress, String hostName);

  public boolean setString(String str) {
    // preserve the arg address as entered
    argAddress = str;
    try {
      // use the preferred address for wildcards, otherwise use the resolved address
      if (getResolvedAddress().isAnyLocalAddress()) {
        return setBindAddress(argAddress, getPreferredAddress().getHostAddress(), getPreferredAddress().getHostName());
      } else {
        return setBindAddress(argAddress, getResolvedAddress().getHostAddress(), getResolvedAddress().getHostName());
      }
    } catch (UnknownHostException e) {
      System.err.println(getTag() + " \"" + getArgAddress() + "\" could not be resolved");
      return false;
    }
  }

}
