/*
 * Copyright 2023 Google Inc.
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
package com.google.gwt.dev.shell.jetty;

import com.google.gwt.core.ext.TreeLogger;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Configuration object to parse selected command line args, store SSL options for use by in the
 * server.
 */
public class SslConfiguration {
  private final ClientAuthType clientAuth;

  private final String keyStore;

  private final String keyStorePassword;

  private final boolean useSsl;

  public static Optional<SslConfiguration> parseArgs(String[] args, TreeLogger logger) {
    boolean useSsl = false;
    String keyStore = null;
    String keyStorePassword = null;
    ClientAuthType clientAuth = ClientAuthType.NONE;

    // TODO(jat): better parsing of the args
    for (String arg : args) {
      int equals = arg.indexOf('=');
      String tag;
      String value = null;
      if (equals < 0) {
        tag = arg;
      } else {
        tag = arg.substring(0, equals);
        value = arg.substring(equals + 1);
      }
      if ("ssl".equals(tag)) {
        useSsl = true;
        URL keyStoreUrl = JettyLauncher.class.getResource("localhost.keystore");
        if (keyStoreUrl == null) {
          logger.log(TreeLogger.ERROR, "Default GWT keystore not found");
          return Optional.empty();
        }
        keyStore = keyStoreUrl.toExternalForm();
        keyStorePassword = "localhost";
      } else if ("keystore".equals(tag)) {
        useSsl = true;
        keyStore = value;
      } else if ("password".equals(tag)) {
        useSsl = true;
        keyStorePassword = value;
      } else if ("pwfile".equals(tag)) {
        useSsl = true;
        try {
          keyStorePassword = Files.readString(Paths.get(value), StandardCharsets.UTF_8);
        } catch (IOException e) {
          logger.log(TreeLogger.ERROR,
                  "Unable to read keystore password from '" + value + "'");
          return Optional.empty();
        }
        keyStorePassword = keyStorePassword.trim();
      } else if ("clientAuth".equals(tag)) {
        useSsl = true;
        try {
          clientAuth = ClientAuthType.valueOf(value);
        } catch (IllegalArgumentException e) {
          logger.log(TreeLogger.WARN, "Ignoring invalid clientAuth of '"
                  + value + "'");
        }
      } else {
        logger.log(TreeLogger.ERROR, "Unexpected argument to "
                + JettyLauncher.class.getSimpleName() + ": " + arg);
        return Optional.empty();
      }
    }
    if (useSsl) {
      if (keyStore == null) {
        logger.log(TreeLogger.ERROR, "A keystore is required to use SSL");
        return Optional.empty();
      }
      if (keyStorePassword == null) {
        logger.log(TreeLogger.ERROR,
                "A keystore password is required to use SSL");
        return Optional.empty();
      }
    }
    return Optional.of(new SslConfiguration(clientAuth, keyStore, keyStorePassword, useSsl));
  }

  public SslConfiguration(ClientAuthType clientAuth, String keyStore, String keyStorePassword, boolean useSsl) {
    this.clientAuth = clientAuth;
    this.keyStore = keyStore;
    this.keyStorePassword = keyStorePassword;
    this.useSsl = useSsl;
  }

  public ClientAuthType getClientAuth() {
    return clientAuth;
  }

  public String getKeyStore() {
    return keyStore;
  }

  public String getKeyStorePassword() {
    return keyStorePassword;
  }

  public boolean isUseSsl() {
    return useSsl;
  }
}
