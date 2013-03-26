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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple and relatively naive client for downloading serialization policies from a URL.
 * (Intended only for development.)
 */
public class DefaultSerializationPolicyClient implements SerializationPolicyClient {

  @Override
  public SerializationPolicy loadPolicy(String url, Logger logger) {
    URL urlObj;
    try {
      urlObj = new URL(url);
    } catch (MalformedURLException e) {
      logger.log("can't parse serialization policy URL: " + url, e);
      return null;
    }

    URLConnection conn;
    InputStream in;
    try {
      conn = urlObj.openConnection();
      conn.setConnectTimeout(5000);
      conn.setReadTimeout(5000);
      if (conn instanceof HttpURLConnection) {
        ((HttpURLConnection)conn).setInstanceFollowRedirects(false);
      }
      conn.connect();
      in = conn.getInputStream();
    } catch (IOException e) {
      logger.log("can't open serialization policy URL: " + url, e);
      return null;
    }

    return readPolicy(in, url, logger);
  }

  /**
   * Attempts to read a policy from a given InputStream and logs any errors.
   *
   * @param sourceName names the source of the input stream for log messages.
   * @return the policy or null if unavailable.
   */
  private static SerializationPolicy readPolicy(InputStream in, String sourceName,
      Logger logger) {
    try {
      List<ClassNotFoundException> errs = new ArrayList<ClassNotFoundException>();
      SerializationPolicy policy = SerializationPolicyLoader.loadFromStream(in, errs);
      logger.log("downloaded serialization policy from " + sourceName);

      if (!errs.isEmpty()) {
        logMissingClasses(logger, errs);
      }
      return policy;

    } catch (ParseException e) {
      logger.log("can't parse serialization policy from " + sourceName, e);
      return null;
    } catch (IOException e) {
      logger.log("can't read serialization policy from " + sourceName, e);
      return null;
    } finally {
      try {
        in.close();
      } catch (IOException e) {
        logger.log("can't close serialization policy url: " + sourceName, e);
      }
    }
  }

  private static void logMissingClasses(Logger logger, List<ClassNotFoundException> errs) {
    logger.log("unable to load server-side classes used by policy:");

    int limit = Math.min(10, errs.size());
    for (int i = 0; i < limit; i++) {
      logger.log(errs.get(i).getMessage());
    }
    int omitted = errs.size() - limit;
    if (omitted > 0) {
      logger.log("(omitted " + omitted + " more classes)");
    }
  }
}
