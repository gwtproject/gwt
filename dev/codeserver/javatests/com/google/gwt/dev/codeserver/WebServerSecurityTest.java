/*
 * Copyright 2026 Google Inc.
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
package com.google.gwt.dev.codeserver;

import junit.framework.TestCase;

/**
 * Security-focused tests for request header parsing in {@link WebServer}.
 */
public class WebServerSecurityTest extends TestCase {

  public void testAcceptsGzipEncodingRejectsNullOrEmptyHeader() {
    assertFalse(WebServer.acceptsGzipEncoding(null));
    assertFalse(WebServer.acceptsGzipEncoding(""));
    assertFalse(WebServer.acceptsGzipEncoding("   "));
  }

  public void testAcceptsGzipEncodingAcceptsSimpleGzip() {
    assertTrue(WebServer.acceptsGzipEncoding("gzip"));
    assertTrue(WebServer.acceptsGzipEncoding("deflate, gzip"));
    assertTrue(WebServer.acceptsGzipEncoding("GZIP"));
  }

  public void testAcceptsGzipEncodingRejectsExplicitGzipZeroQValue() {
    assertFalse(WebServer.acceptsGzipEncoding("gzip;q=0"));
    assertFalse(WebServer.acceptsGzipEncoding("deflate, gzip; q=0.0"));
  }

  public void testAcceptsGzipEncodingHonorsWildcardWhenGzipAbsent() {
    assertTrue(WebServer.acceptsGzipEncoding("*"));
    assertTrue(WebServer.acceptsGzipEncoding("br;q=0.2, *;q=0.7"));
    assertFalse(WebServer.acceptsGzipEncoding("*;q=0"));
  }

  public void testAcceptsGzipEncodingPrefersExplicitGzipOverWildcard() {
    assertFalse(WebServer.acceptsGzipEncoding("gzip;q=0, *;q=1"));
    assertTrue(WebServer.acceptsGzipEncoding("gzip;q=1, *;q=0"));
  }

  public void testAcceptsGzipEncodingRejectsMalformedQValue() {
    assertFalse(WebServer.acceptsGzipEncoding("gzip;q=not-a-number"));
  }
}
