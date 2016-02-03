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
package com.google.gwt.dev.util.arg;

import com.google.gwt.util.tools.ArgHandlerString;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Argument handler for processing a bind address flags that takes an ip address or host name as its parameter.
 * If the specified address is a wildcard, the canonical address for localhost will be returned instead.
 */
public class ArgHandlerBindAddress extends ArgHandlerString {

  public static InetAddress getDefaultBindAddress() {
    try {
      return InetAddress.getLocalHost();
    } catch(UnknownHostException e) {
      return null;
    }
  }
  private final OptionBindAddress options;

  public ArgHandlerBindAddress(OptionBindAddress options) {
    this.options = options;
  }

  @Override
  public String[] getDefaultArgs() {
    return new String[] { getTag(), getDefaultBindAddress().getHostAddress() };
  }

  @Override
  public String getPurpose() {
    return "Specifies the bind address to be used (defaults to " + getDefaultBindAddress().getHostAddress() + ")";
  }

  @Override
  public String getTag() {
    return "-bindAddress";
  }

  @Override
  public String[] getTagArgs() {
    return new String[]{"ip-or-host"};
  }

  public boolean setBindAddress(String bindAddress) {
    try {
      options.setBindAddressAsEntered(bindAddress);
      options.setBindAddress(InetAddress.getByName(bindAddress));

      if (options.getBindAddress().isAnyLocalAddress()) {
        // wildcards should use the canonical form of localhost
        options.setBindAddress(InetAddress.getByName(
            InetAddress.getLocalHost().getCanonicalHostName()));
      }

      return true;
    } catch (UnknownHostException e) {
      System.err.println(getTag() + " \"" + bindAddress + "\" could not be resolved");
      return false;
    }
  }

  public boolean setString(String str) {
    return setBindAddress(str);
  }

}
