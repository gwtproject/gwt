/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.junit.server;

import com.google.gwt.core.server.StackTraceDeobfuscator;
import com.google.gwt.junit.JUnitFatalLaunchException;
import com.google.gwt.junit.JUnitMessageQueue;
import com.google.gwt.junit.JUnitShell;
import com.google.gwt.junit.client.TimeoutException;
import com.google.gwt.junit.client.impl.JUnitHost;
import com.google.gwt.junit.client.impl.JUnitResult;
import com.google.gwt.junit.linker.JUnitSymbolMapsLinker;
import com.google.gwt.logging.shared.RemoteLoggingService;
import com.google.gwt.user.server.rpc.RPCServletUtils;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An RPC servlet that serves as a proxy to JUnitTestShell. Enables
 * communication between the unit test code running in a browser and the real
 * test process.
 */
public class JUnitHostImpl extends RemoteServiceServlet implements JUnitHost, RemoteLoggingService {

  /**
   * A maximum timeout to wait for the test system to respond with the next
   * test. The test system should respond nearly instantly if there are further
   * tests to run, unless the tests have not yet been compiled.
   */
  private static final int TIME_TO_WAIT_FOR_TESTNAME = 300000;

  private StackTraceDeobfuscator deobfuscator;

  public TestInfo[] reportResultsAndGetTestBlock(
      HashMap<TestInfo, JUnitResult> results) throws TimeoutException {
    JUnitMessageQueue host = JUnitShell.getMessageQueue();
    if (results.size() > 0) {
      for (JUnitResult result : results.values()) {
        initResult(result);
      }
      host.reportResults(results);
    }
    return host.getTestBlock(TIME_TO_WAIT_FOR_TESTNAME);
  }

  @Override
  protected void service(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    String requestURI = request.getRequestURI();
    if (requestURI.endsWith("/junithost/error")) {
      String msg = RPCServletUtils.readContentAsGwtRpc(request);
      System.err.println("Warning: " + msg);
    } else if (requestURI.endsWith("/junithost/error/fatal")) {
      String msg = RPCServletUtils.readContentAsGwtRpc(request);
      System.err.println("Fatal error: " + msg);
      System.exit(1);
    } else if (requestURI.endsWith("/junithost/error/launch")) {
      String requestPayload = RPCServletUtils.readContentAsGwtRpc(request);
      JUnitResult result = new JUnitResult();
      initResult(result);
      result.setException(new JUnitFatalLaunchException(requestPayload));
      JUnitShell.getMessageQueue().reportFatalLaunch(result);
    } else {
      super.service(request, response);
    }
  }

  private void initResult(JUnitResult result) {
    HttpServletRequest request = getThreadLocalRequest();
    result.setAgent(request.getHeader("User-Agent"));
    result.setHost(request.getRemoteHost());
    Throwable throwable = result.getException();
    if (throwable != null) {
      deobfuscateStackTrace(throwable);
    }
  }

  @Override
  public String logOnServer(LogRecord lr) {
    lr.setMessage("<BROWSER> " + lr.getMessage());
    if (lr.getThrown() != null) {
      deobfuscateStackTrace(lr.getThrown());
    }
    Logger.getLogger(lr.getLoggerName()).log(lr);
    return null;
  }

  private void deobfuscateStackTrace(Throwable throwable) {
    try {
      getDeobfuscator().deobfuscateStackTrace(throwable, getPermutationStrongName());
    } catch (IOException e) {
      System.err.println("Unable to deobfuscate a stack trace due to an error:");
      e.printStackTrace();
    }
  }

  private StackTraceDeobfuscator getDeobfuscator() throws IOException {
    if (deobfuscator == null) {
      String path = getRequestModuleBasePath() + "/" + JUnitSymbolMapsLinker.SYMBOL_MAP_DIR;
      deobfuscator = StackTraceDeobfuscator.fromUrl(getServletContext().getResource(path));
    }
    return deobfuscator;
  }
}

