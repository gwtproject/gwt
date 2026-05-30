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
package com.google.web.bindery.requestfactory.vm.testing;

import com.google.web.bindery.requestfactory.shared.RequestTransport.TransportReceiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * JRE tests for {@link UrlRequestTransport}.
 */
public class UrlRequestTransportTest extends TestCase {

  private static class CloseTrackingInputStream extends ByteArrayInputStream {
    private boolean closed;

    CloseTrackingInputStream() {
      super("{}".getBytes());
    }

    @Override
    public void close() throws IOException {
      closed = true;
      super.close();
    }
  }

  private static class FakeHttpURLConnection extends HttpURLConnection {
    private final CloseTrackingInputStream input = new CloseTrackingInputStream();

    FakeHttpURLConnection(URL url) {
      super(url);
    }

    @Override
    public void connect() {
    }

    @Override
    public void disconnect() {
    }

    @Override
    public String getContentEncoding() {
      return "br";
    }

    @Override
    public Map<String, List<String>> getHeaderFields() {
      return Collections.emptyMap();
    }

    @Override
    public InputStream getInputStream() {
      return input;
    }

    @Override
    public OutputStream getOutputStream() {
      return new ByteArrayOutputStream();
    }

    @Override
    public int getResponseCode() {
      return HTTP_OK;
    }

    @Override
    public boolean usingProxy() {
      return false;
    }
  }

  public void testUnsupportedEncodingClosesInputStream() throws Exception {
    final FakeHttpURLConnection[] connection = new FakeHttpURLConnection[1];
    URL url = new URL(null, "http://example.test/requestfactory", new URLStreamHandler() {
      @Override
      protected URLConnection openConnection(URL url) {
        connection[0] = new FakeHttpURLConnection(url);
        return connection[0];
      }
    });

    new UrlRequestTransport(url).send("{}", new TransportReceiver() {
      @Override
      public void onTransportFailure(ServerFailure failure) {
        assertEquals("Unknown server encoding br", failure.getMessage());
      }

      @Override
      public void onTransportSuccess(String payload) {
        fail("Expected unsupported encoding to fail");
      }
    });

    assertTrue(connection[0].input.closed);
  }
}
