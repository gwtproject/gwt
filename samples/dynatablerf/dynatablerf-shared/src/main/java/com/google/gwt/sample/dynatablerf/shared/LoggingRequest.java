/*
 * Copyright 2026 GWT Project Authors
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
package com.google.gwt.sample.dynatablerf.shared;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

/**
 * Copy of LoggingRequest for use in this project without including the server jar in
 * the client classpath.
 */
@ServiceName("com.google.web.bindery.requestfactory.server.jakarta.Logging")
public interface LoggingRequest extends RequestContext {

  // TODO(unnurg): Pass a SerializableLogRecord here rather than it's
  // serialized string.
  /**
   * Log a message on the server.
   *
   * @param serializedLogRecordString a json serialized LogRecord, as provided
   *          by
   *          {@link com.google.gwt.logging.client.JsonLogRecordClientUtil#logRecordAsJsonObject(LogRecord)}
   * @return a Void {@link Request}
   */
  Request<Void> logMessage(String serializedLogRecordString);
}