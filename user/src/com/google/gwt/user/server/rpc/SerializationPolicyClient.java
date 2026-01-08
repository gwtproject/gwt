/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.user.server.rpc;

import com.google.gwt.user.server.rpc.logging.RpcLogManager;
import com.google.gwt.user.server.rpc.logging.RpcLogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

/**
 * A simple and relatively naive client for downloading serialization policies from a URL.
 * (Intended only for development.)
 */
class SerializationPolicyClient {

  private static final RpcLogger logger = RpcLogManager.getLogger(SerializationPolicyClient.class);
  private final int connectTimeout;
  private final int readTimeout;

  /**
   * Creates a client with the given configuration.
   * @param connectTimeoutMs see {@link URLConnection#setConnectTimeout}
   * @param readTimeoutMs see {@link URLConnection#setReadTimeout}
   */
  SerializationPolicyClient(int connectTimeoutMs, int readTimeoutMs) {
    this.connectTimeout = connectTimeoutMs;
    this.readTimeout = readTimeoutMs;
  }

  SerializationPolicy loadPolicy(String url, ServletContext servletContext) {
    URL urlObj;
    try {
      urlObj = new URL(url);
    } catch (MalformedURLException e) {
      logger.error("Can't parse serialization policy URL: " + url, e, servletContext);
      return null;
    }

    URLConnection conn;
    InputStream in;
    try {
      conn = urlObj.openConnection();
      conn.setConnectTimeout(connectTimeout);
      conn.setReadTimeout(readTimeout);
      // The code server doesn't redirect. Fail fast if we get a redirect since
      // it's likely a configuration error.
      if (conn instanceof HttpURLConnection) {
        ((HttpURLConnection) conn).setInstanceFollowRedirects(false);
      }
      conn.connect();
      in = conn.getInputStream();
    } catch (IOException e) {
      logger.error("Can't open serialization policy URL: " + url, e, servletContext);
      return null;
    }

    return readPolicy(in, url, servletContext);
  }

  /**
   * Attempts to read a policy from a given InputStream and logs any errors.
   *
   * @param sourceName names the source of the input stream for log messages.
   * @return the policy or null if unavailable.
   */
  private static SerializationPolicy readPolicy(InputStream in, String sourceName,
      ServletContext servletContext) {
    try {
      List<ClassNotFoundException> errs = new ArrayList<ClassNotFoundException>();
      SerializationPolicy policy = SerializationPolicyLoader.loadFromStream(in, errs);
      logger.info("Downloaded serialization policy from " + sourceName, servletContext);

      if (!errs.isEmpty()) {
        logMissingClasses(errs, servletContext);
      }
      return policy;

    } catch (ParseException e) {
      logger.error("Can't parse serialization policy from " + sourceName, e, servletContext);
      return null;
    } catch (IOException e) {
      logger.error("Can't read serialization policy from " + sourceName, e, servletContext);
      return null;
    } finally {
      try {
        in.close();
      } catch (IOException e) {
        logger.error("Can't close serialization policy url: " + sourceName, e, servletContext);
      }
    }
  }

  private static void logMissingClasses(List<ClassNotFoundException> errs,
      ServletContext servletContext) {
    StringBuilder message = new StringBuilder();
    message.append("Unable to load server-side classes used by policy:\n");

    int limit = Math.min(10, errs.size());
    for (int i = 0; i < limit; i++) {
      message.append("  " + errs.get(i).getMessage() + "\n");
    }
    int omitted = errs.size() - limit;
    if (omitted > 0) {
      message.append("  (omitted " + omitted + " more classes)\n");
    }
    logger.info(message.toString(), servletContext);
  }

}
