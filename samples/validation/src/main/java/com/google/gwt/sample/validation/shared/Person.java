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
package com.google.gwt.sample.validation.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A bean interface to show validation on.
 */
public interface Person extends IsSerializable {

  @Valid
  Address getMainAddress();

  @Valid
  Map<String, Address> getMappedAddresses();

  @Valid
  Set<Address> getOtherAddresses();

  @Valid
  Group getGroup();

  @Size(min = 4, message = "{custom.name.size.message}")
  @NotNull
  String getName();

  void setMainAddress(Address address);

  void setName(String name);

  @Max(999999999)
  void setSsn(long ssn);

}
