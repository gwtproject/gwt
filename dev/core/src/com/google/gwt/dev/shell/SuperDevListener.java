/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.dev.shell;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.impl.StandardLinkerContext;
import com.google.gwt.dev.DevMode.HostedModeOptions;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.json.JsonException;
import com.google.gwt.dev.json.JsonObject;
import com.google.gwt.dev.json.JsonString;
import com.google.gwt.dev.util.arg.OptionJsInteropMode;
import com.google.gwt.dev.util.arg.OptionMethodNameDisplayMode;
import com.google.gwt.thirdparty.guava.common.base.Charsets;
import com.google.gwt.thirdparty.guava.common.io.ByteStreams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Starts a superdev-mode codeserver.
 */
public class SuperDevListener implements CodeServerListener {

  private final Thread listenThread;
  private final TreeLogger logger;
  private final HostedModeOptions options;
  private final int codeServerPort;

  /**
   * Listens for new connections from browsers.
   */
  public SuperDevListener(TreeLogger treeLogger, HostedModeOptions options) {
    this.logger = treeLogger;
    this.options = options;
    this.codeServerPort = chooseCodeServerPort(treeLogger, options);

    // This directory must exist when the Code Server starts.
    ensureModuleBaseDir(options);

    List<String> args = makeCodeServerArgs(options, codeServerPort);

    final String[] codeServerArgs = args.toArray(new String[0]);

    logger.log(Type.INFO, "Runing CodeServer with parameters: " + args);

    // Using reflection so as we don't create a circular dependency between
    // dev.jar && codeserver.jar
    final Method mainMethod;
    try {
      Class<?> clazz = Class.forName("com.google.gwt.dev.codeserver.CodeServer");
      mainMethod = clazz.getMethod("main", String[].class);
    } catch (ClassNotFoundException e) {
      logger.log(TreeLogger.ERROR, "Unable to find main() method for Super Dev Mode "
          + "code server. Hint: verify that gwt-codeserver.jar is in your classpath.");
      throw new RuntimeException(e);
    } catch (NoSuchMethodException e) {
      logger.log(TreeLogger.ERROR, "Unable to run superdev codeServer.", e);
      throw new RuntimeException(e);
    }

    listenThread = new Thread() {
      public void run() {
        try {
          mainMethod.invoke(null, new Object[] {codeServerArgs});
        } catch (Exception e) {
          logger.log(TreeLogger.ERROR, "Unable to run superdev codeServer.", e);
        }
      }
    };
    listenThread.setName("SuperDevMode code server listener");
    listenThread.setDaemon(true);
  }

  @Override
  public int getSocketPort() {
    return codeServerPort;
  }

  @Override
  public URL makeStartupUrl(String url) throws UnableToCompleteException {
    try {
      return new URL(url);
    } catch (MalformedURLException e) {
      logger.log(TreeLogger.ERROR, "Invalid URL " + url, e);
      throw new UnableToCompleteException();
    }
  }

  @Override
  public void writeCompilerOutput(StandardLinkerContext linkerStack, ArtifactSet artifacts,
      ModuleDef module, boolean isRelink) throws UnableToCompleteException {
    // The code server will do this.
  }

  @Override
  public void setIgnoreRemoteDeath(boolean b) {
  }

  @Override
  public void start() {
    listenThread.start();
  }

  @Override
  public void waitUntilReady() throws UnableToCompleteException {
    logger.log(Type.INFO, "Waiting for CodeServer to start.");
    while (!isReady()) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        logger.log(Type.ERROR, "thread interrupted");
        throw new UnableToCompleteException();
      }
    }
    logger.log(Type.INFO, "CodeServer is ready.");
  }

  /**
   * Returns true if the code server is running and idle.
   * Returns false to wait and try again.
   * @throws UnableToCompleteException if there's no point in retrying.
   */
  private boolean isReady() throws UnableToCompleteException {
    String urlString = "http://" + options.getBindAddress() + ":" + codeServerPort + "/progress";

    URL url;
    try {
      url = new URL(urlString);
    } catch (MalformedURLException e) {
      logger.log(Type.ERROR, "bad URL: " + urlString, e);
      throw new UnableToCompleteException();
    }

    HttpURLConnection c;
    try {
      c = ((HttpURLConnection) url.openConnection());
    } catch (IOException e) {
      logger.log(Type.ERROR, "openConnection() failed for URL: " + urlString, e);
      return false;
    }

    byte[] bytes;
    try {
      int status = c.getResponseCode();
      if (status != 200) {
        logger.log(Type.ERROR, "unexpected response to GET " + urlString + " : " + status);
        return false;
      }

      InputStream in = c.getInputStream();
      try {
        bytes = ByteStreams.toByteArray(in);
      } finally {
        in.close();
      }
    } catch (ConnectException e) {
      // This is the normal when the port hasn't been opened yet. (Don't log it.)
      return false;
    } catch (IOException e) {
      logger.log(Type.ERROR, "can't read response to GET " + urlString, e);
      return false;
    } finally {
      c.disconnect();
    }

    JsonObject json;
    try {
      String input = new String(bytes, Charsets.UTF_8);
      json = JsonObject.parse(new StringReader(input));
    } catch (JsonException e) {
      logger.log(Type.ERROR, "/progress response is not JSON", e);
      return false;
    } catch (IOException e) {
      logger.log(Type.ERROR, "/progress response is not JSON", e);
      return false;
    }

    if (!json.isObject()) {
      logger.log(Type.ERROR, "/progress response is not a JSON object");
      return false;
    }

    JsonString value = json.get("status").asString();
    if (value == null) {
      logger.log(Type.ERROR, "can't read status from /progress response");
      return false;
    }

    return value.getString().equals("idle");
  }

  private static int chooseCodeServerPort(TreeLogger logger, HostedModeOptions options) {
    int port = options.getCodeServerPort();
    if (port == 0) {
      // Automatically choose an unused port.
      try {
        ServerSocket serverSocket = new ServerSocket(0);
        port = serverSocket.getLocalPort();
        serverSocket.close();
        return port;
      } catch (IOException e) {
        logger.log(TreeLogger.ERROR, "Unable to get an unnused port.");
        throw new RuntimeException(e);
      }
    } else if (port < 0 || port == 9997) {
      // 9997 is the default non-SuperDevMode port from DevModeBase. TODO: use constant.
      return 9876; // Default Super Dev Mode port
    } else {
      return port; // User-specified port
    }
  }

  private static void ensureModuleBaseDir(HostedModeOptions options) {
    File dir = options.getModuleBaseDir();
    if (!dir.isDirectory()) {
      dir.mkdirs();
      if (!dir.isDirectory()) {
        throw new RuntimeException("unable to create module base directory: " +
            dir.getAbsolutePath());
      }
    }
  }

  private static List<String> makeCodeServerArgs(HostedModeOptions options, int port) {
    List<String> args = new ArrayList<String>();
    args.add("-noprecompile");
    args.add("-port");
    args.add(String.valueOf(port));
    args.add("-sourceLevel");
    args.add(String.valueOf(options.getSourceLevel()));
    if (options.getBindAddress() != null) {
      args.add("-bindAddress");
      args.add(options.getBindAddress());
    }
    if (options.getWorkDir() != null) {
      args.add("-workDir");
      args.add(String.valueOf(options.getWorkDir()));
    }
    args.add("-launcherDir");
    args.add(options.getModuleBaseDir().getAbsolutePath());
    if (options.getLogLevel() != null) {
      args.add("-logLevel");
      args.add(String.valueOf(options.getLogLevel()));
    }
    if (options.getJsInteropMode() != OptionJsInteropMode.Mode.NONE) {
      args.add("-XjsInteropMode");
      args.add(options.getJsInteropMode().name());
    }
    if (!options.isIncrementalCompileEnabled()) {
      args.add("-noincremental");
    }
    if (options.getMethodNameDisplayMode() != OptionMethodNameDisplayMode.Mode.NONE) {
      args.add("-XmethodNameDisplayMode");
      args.add(options.getMethodNameDisplayMode().name());
    }
    for (String mod : options.getModuleNames()) {
      args.add(mod);
    }
    return args;
  }
}
