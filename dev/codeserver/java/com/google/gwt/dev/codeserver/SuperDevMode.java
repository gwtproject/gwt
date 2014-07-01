/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.dev.codeserver;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.impl.StandardLinkerContext;
import com.google.gwt.dev.DevModeBase;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.util.tools.Utility;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The main executable class for the super-devmode shell.
 */
public class SuperDevMode extends DevModeBase {

  protected static class SuperDevModeOptions extends HostedModeBaseOptionsImpl {
    private static final long serialVersionUID = 1L;

    @Override
    public int getCodeServerPort() {
      int port = super.getCodeServerPort();
      if (port == 0) {
        // when codeServerPort is set to auto
        setCodeServerPort(new Random().nextInt(Character.MAX_VALUE - 1024) + 1024);
      } else if (port == 9997 || port < 0) {
        // when codeServerPort is not set or is default
        setCodeServerPort(9876);
      }
      return super.getCodeServerPort();
    }
  }

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
    SuperDevMode superDevHostedMode = new SuperDevMode();
    if (new ArgProcessor(superDevHostedMode.options).processArgs(args)) {
      superDevHostedMode.run();
      // Exit w/ success code.
      System.exit(0);
    }
    // Exit w/ non-success code.
    System.exit(-1);
  }

  protected final SuperDevModeOptions options = (SuperDevModeOptions) super.options;

  @Override
  protected HostedModeBaseOptions createOptions() {
    return new SuperDevModeOptions();
  }

  @Override
  protected void ensureCodeServerListener() {
    Thread listener = new Thread() {
      public void run() {
        try {
          String args = "-noprecompile";
          if (options.getBindAddress() != null) {
            args += " -bindAddress " + options.getBindAddress();
          }
          if (options.getWorkDir() != null) {
            args += " -workDir " + options.getWorkDir();
          }
          if (options.getLogLevel() != null) {
            args += " -logLevel " + options.getLogLevel();
          }
          args += " -port " + options.getCodeServerPort();
          args += " -sourceLevel " + options.getSourceLevel();
          for (String mod : options.getModuleNames()) {
            args += " " + mod;
          }
          getTopLogger().log(TreeLogger.INFO, "Running CodeServer with args: " + args);

          CodeServer.main(args.split(" "));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };
    listener.start();
  }

  @Override
  protected URL processUrl(String url) throws UnableToCompleteException {
    URL parsedUrl = null;
    try {
      parsedUrl = new URL(url);
    } catch (MalformedURLException e) {
      getTopLogger().log(TreeLogger.ERROR, "Invalid URL " + url, e);
      throw new UnableToCompleteException();
    }
    return parsedUrl;
  }

  @Override
  protected synchronized void produceOutput(TreeLogger logger, StandardLinkerContext linkerStack,
      ArtifactSet artifacts, ModuleDef module, boolean isRelink) throws UnableToCompleteException {
    try {
      String computeScriptBase =
          Utility.getFileFromClassPath("com/google/gwt/dev/codeserver/computeScriptBase.js");
      String contents =
          Utility.getFileFromClassPath("com/google/gwt/dev/codeserver/hosted.nocache.js");

      File file =
          new File(options.getWarDir() + "/" + module.getName() + "/" + module.getName()
              + ".nocache.js");

      file.deleteOnExit();

      file.getParentFile().mkdirs();

      Map<String, String> replacements = new HashMap<String, String>();
      replacements.put("__COMPUTE_SCRIPT_BASE__", computeScriptBase);
      // must go after script base
      replacements.put("__MODULE_NAME__", module.getName());
      replacements.put("__SUPERDEV_PORT__", "" + options.getCodeServerPort());

      Utility.writeTemplateFile(file, contents, replacements);

      getTopLogger().log(
          TreeLogger.INFO,
          "To compile, visit: http://127.0.0.1:"
              + options.getCodeServerPort() + "/recompile/" + module.getName()
              + "?user.agent=safari&_callback=foo\n");

    } catch (IOException e) {
      logger.log(Type.ERROR, "Unable to create nocache script ", e);
      throw new UnableToCompleteException();
    }
  }
}
