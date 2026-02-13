/*
 * Copyright 2025 GWT Project Authors
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
package com.google.gwt.user.server.rpc.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link RpcLoggerProvider} that delegates to {@link java.util.logging.Logger}.
 */
public class JulLoggerProvider implements RpcLoggerProvider {

  public JulLoggerProvider() {
  }

  @Override
  public RpcLoggerDelegate createLogger(String name) {
    return new JulLogger(Logger.getLogger(name));
  }

  private static final class JulLogger implements RpcLoggerDelegate {
    private final Logger logger;

    JulLogger(Logger logger) {
      this.logger = logger;
    }

    @Override
    public void info(String message) {
      logger.log(Level.INFO, message);
    }

    @Override
    public void warn(String message) {
      logger.log(Level.WARNING, message);
    }

    @Override
    public void error(String message) {
      logger.log(Level.SEVERE, message);
    }

    @Override
    public void error(String message, Throwable throwable) {
      logger.log(Level.SEVERE, message, throwable);
    }
  }

}
