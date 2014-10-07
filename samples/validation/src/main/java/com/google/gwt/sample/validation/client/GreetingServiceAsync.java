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
package com.google.gwt.sample.validation.client;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.sample.validation.shared.Person;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.hibernate.validator.internal.engine.ValidationSupport;

import javax.validation.ConstraintViolationException;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
  void greetServer(Person person, AsyncCallback<SafeHtml> callback)
      throws IllegalArgumentException, ConstraintViolationException;

  void dummy(AsyncCallback<ValidationSupport> callback);
}
