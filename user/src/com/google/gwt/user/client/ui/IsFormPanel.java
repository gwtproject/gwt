/*
 * Copyright 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.user.client.ui;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.impl.FormPanelImplHost;

@SuppressWarnings("deprecation")
public interface IsFormPanel extends IsSimplePanel, FormPanelImplHost, FiresFormEvents {

  HandlerRegistration addSubmitCompleteHandler(SubmitCompleteHandler handler);

  HandlerRegistration addSubmitHandler(SubmitHandler handler);

  String getAction();

  String getEncoding();

  String getMethod();

  String getTarget();

  void reset();

  void setAction(String action);

  void setAction(SafeUri url);

  void setEncoding(String encoding);

  void setMethod(String method);

  void submit();

}
