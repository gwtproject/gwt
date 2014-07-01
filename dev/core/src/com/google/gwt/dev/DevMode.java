/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.dev;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.EmittedArtifact.Visibility;
import com.google.gwt.core.ext.linker.impl.StandardLinkerContext;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.shell.BrowserListener;
import com.google.gwt.dev.shell.BrowserWidgetHost;
import com.google.gwt.dev.shell.OophmSessionHandler;
import com.google.gwt.dev.ui.RestartServerCallback;
import com.google.gwt.dev.util.NullOutputFileSet;
import com.google.gwt.dev.util.OutputFileSet;
import com.google.gwt.dev.util.OutputFileSetOnDirectory;

/**
 * The main executable class for the hosted mode shell.
 */
public class DevMode extends DevModeCommon implements RestartServerCallback {

  /**
   * Startup development mode.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    /*
     * NOTE: main always exits with a call to System.exit to terminate any non-daemon threads that
     * were started in Generators. Typically, this is to shutdown AWT related threads, since the
     * contract for their termination is still implementation-dependent.
     */
    DevMode hostedMode = new DevMode();
    if (new ArgProcessor(hostedMode.options).processArgs(args)) {
      hostedMode.run();
      // Exit w/ success code.
      System.exit(0);
    }
    // Exit w/ non-success code.
    System.exit(-1);
  }
  private BrowserWidgetHost browserHost = new UiBrowserWidgetHostImpl();

  protected int codeServerPort;

  protected void ensureCodeServerListener() {
    codeServerPort = options.getCodeServerPort();
    BrowserListener listener =
        new BrowserListener(getTopLogger(), bindAddress, codeServerPort, new OophmSessionHandler(
            getTopLogger(), browserHost));
    listener.start();

    try {
      // save the port we actually used if it was auto
      codeServerPort = listener.getSocketPort();
    } catch (UnableToCompleteException e) {
      // ignore errors listening, we will catch them later
    }
  }

  protected URL processUrl(String url) throws UnableToCompleteException {
    /*
     * TODO(jat): properly support launching arbitrary browsers -- need some UI
     * API tweaks to support that.
     */
    URL parsedUrl = null;
    try {
      parsedUrl = new URL(url);
      String path = parsedUrl.getPath();
      String query = parsedUrl.getQuery();
      String hash = parsedUrl.getRef();
      String hostedParam =
          BrowserListener.getDevModeURLParams(connectAddress, codeServerPort);
      if (query == null) {
        query = hostedParam;
      } else {
        query += '&' + hostedParam;
      }
      path += '?' + query;
      if (hash != null) {
        path += '#' + hash;
      }
      parsedUrl = new URL(parsedUrl.getProtocol(), parsedUrl.getHost(), parsedUrl.getPort(), path);
      url = parsedUrl.toExternalForm();
    } catch (MalformedURLException e) {
      getTopLogger().log(TreeLogger.ERROR, "Invalid URL " + url, e);
      throw new UnableToCompleteException();
    }
    return parsedUrl;
  }

  @Override
  protected synchronized void produceOutput(TreeLogger logger, StandardLinkerContext linkerStack,
      ArtifactSet artifacts, ModuleDef module, boolean isRelink) throws UnableToCompleteException {
    TreeLogger linkLogger =
        logger.branch(TreeLogger.DEBUG, "Linking module '" + module.getName() + "'");

    OutputFileSetOnDirectory outFileSet =
        new OutputFileSetOnDirectory(options.getWarDir(), module.getName() + "/");
    OutputFileSetOnDirectory deployFileSet =
        new OutputFileSetOnDirectory(options.getDeployDir(), module.getName() + "/");
    OutputFileSet extraFileSet = new NullOutputFileSet();
    if (options.getExtraDir() != null) {
      extraFileSet = new OutputFileSetOnDirectory(options.getExtraDir(), module.getName() + "/");
    }

    linkerStack.produceOutput(linkLogger, artifacts, Visibility.Public, outFileSet);
    linkerStack.produceOutput(linkLogger, artifacts, Visibility.Deploy, deployFileSet);
    linkerStack.produceOutput(linkLogger, artifacts, Visibility.Private, extraFileSet);

    outFileSet.close();
    deployFileSet.close();
    try {
      extraFileSet.close();
    } catch (IOException e) {
      linkLogger.log(TreeLogger.ERROR, "Error emiting extra files", e);
      throw new UnableToCompleteException();
    }

    // The 'nocache' and 'clear.cache' files are produced with the same time-stamp
    // than the startup html file.
    // We change them to prevent cache issues in the browser if super-devmode
    // was previously run.
    new File(options.getWarDir() + "/" + module.getName() + "/" + module.getName() + ".nocache.js")
        .setLastModified(System.currentTimeMillis());
    new File(options.getWarDir() + "/" + module.getName() + "/clear.cache.gif")
        .setLastModified(System.currentTimeMillis());
  }
}
