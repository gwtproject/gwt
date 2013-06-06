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

/**
 * An interface for {@link FormPanel}.
 */
@SuppressWarnings("deprecation")
public interface IsFormPanel extends IsSimplePanel, FormPanelImplHost, FiresFormEvents {

  /**
   * See {@link FormPanel#addSubmitCompleteHandler(com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler)}.
   */
  HandlerRegistration addSubmitCompleteHandler(SubmitCompleteHandler handler);

  /**
   * See {@link FormPanel#addSubmitHandler(com.google.gwt.user.client.ui.FormPanel.SubmitHandler)}.
   */
  HandlerRegistration addSubmitHandler(SubmitHandler handler);

  /**
   * See {@link FormPanel#getAction()}.
   */
  String getAction();

  /**
   * See {@link FormPanel#getEncoding()}.
   */
  String getEncoding();

  /**
   * See {@link FormPanel#getMethod()}.
   */
  String getMethod();

  /**
   * See {@link FormPanel#getTarget()}.
   */
  String getTarget();

  /**
   * See {@link FormPanel#reset()}.
   */
  void reset();

  /**
   * See {@link FormPanel#setAction(java.lang.String)}.
   */
  void setAction(String action);

  /**
   * See {@link FormPanel#setAction(com.google.gwt.safehtml.shared.SafeUri)}.
   */
  void setAction(SafeUri url);

  /**
   * See {@link FormPanel#setEncoding(java.lang.String)}.
   */
  void setEncoding(String encoding);

  /**
   * See {@link FormPanel#setMethod(java.lang.String)}.
   */
  void setMethod(String method);

  /**
   * See {@link FormPanel#submit()}.
   */
  void submit();

}
