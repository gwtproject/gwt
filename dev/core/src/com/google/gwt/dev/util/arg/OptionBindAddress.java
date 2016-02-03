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

import java.net.InetAddress;

/**
 * Option to set the bind address.
 */
public interface OptionBindAddress {

  /**
   * Returns the resolved bind address.
   * @return the resolved bind address to use
   */
  InetAddress getBindAddress();

  /**
   * Returns the bind address as originally provided.
   * @return the original bind address provided
   */
  String getBindAddressAsEntered();

  /**
   * Sets the resolved bind address.
   * @param bindAddress the resolved bind address to be set
   */
  void setBindAddress(InetAddress bindAddress);

  /**
   * Sets the bind address originally provided.
   * @param bindAddress the original bind address provided
   */
  void setBindAddressAsEntered(String bindAddress);
}
