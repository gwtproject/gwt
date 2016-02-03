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
 * Argument handler for processing flags that take a host name or ip address as their parameter.
 */
public abstract class ArgHandlerHost extends ArgHandlerString {

  public static final String DEFAULT_HOST_ADDRESS = "127.0.0.1";
  public static final String DEFAULT_HOST_NAME = "localhost";

  protected static final String HOST_TAG = "-host";

  private InetAddress preferredHost;
  private String rawHost;
  private InetAddress resolvedHost;

  @Override
  public String[] getDefaultArgs() {
    return new String[]{getTag(), DEFAULT_HOST_ADDRESS};
  }

  @Override
  public String getPurpose() {
    return "Specifies the host to be used (defaults to " + DEFAULT_HOST_ADDRESS + ")";
  }

  public InetAddress getPreferredHost() throws UnknownHostException {
    if (preferredHost == null) {
      if (getResolvedHost().isAnyLocalAddress()) {
        // make a guess as to the best host to use by looking up the canonical local host
        preferredHost = InetAddress.getByName(InetAddress.getLocalHost().getCanonicalHostName());
      } else {
        preferredHost = resolvedHost;
      }
    }
    return preferredHost;
  }

  public String getRawHost() {
    return rawHost;
  }

  public InetAddress getResolvedHost() throws UnknownHostException {
    if (resolvedHost == null) {
      resolvedHost = InetAddress.getByName(rawHost);
    }
    return resolvedHost;
  }

  @Override
  public String getTag() {
    return HOST_TAG;
  }

  @Override
  public String[] getTagArgs() {
    return new String[]{"host-name-or-address"};
  }

  @Override
  public int handle(String[] args, int startIndex) {
    if (startIndex + 1 < args.length) {
      // preserve the raw host value as entered
      rawHost = args[startIndex + 1];
      try {
        // use the preferred host address for wildcards, otherwise use the raw host value
        String host = getResolvedHost().isAnyLocalAddress()
            ? getPreferredHost().getHostAddress()
            : getRawHost();
        // set the host string
        if (!setString(host)) {
          return -1;
        }
      } catch (UnknownHostException e) {
        System.err.println(getTag() + " \"" + getRawHost() + "\" could not be resolved");
        return -1;
      }
      return 1;
    }

    System.err.println(getTag() + " must be followed by an argument for " + getTagArgs()[0]);
    return -1;
  }

}
