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
package com.google.gwt.junit;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

/**
 * Runs in Production Mode via browsers managed as an external process. This
 * feature is experimental and is not officially supported.
 */
class RunStyleExternalBrowser extends RunStyle {

  private static class ExternalBrowser {
    String browserPath;
    Process process;

    public ExternalBrowser(String browserPath) {
      this.browserPath = browserPath;
    }

    public String getPath() {
      return browserPath;
    }

    public Process getProcess() {
      return process;
    }

    public void setProcess(Process process) {
      this.process = process;
    }
  }

  /**
   * Registered as a shutdown hook to make sure that any browsers that were not
   * finished are killed.
   */
  private class ShutdownCb extends Thread {

    @Override
    public void run() {
      if (browser.getProcess() != null) {
        try {
          browser.getProcess().exitValue();
        } catch (IllegalThreadStateException e) {
          // The process is still active. Kill it.
          browser.getProcess().destroy();
        }
      }
    }
  }

  private ExternalBrowser browser;

  /**
   * @param shell the containing shell
   */
  public RunStyleExternalBrowser(JUnitShell shell) {
    super(shell);
  }

  @Override
  public boolean isBrowserAlive() {
    try {
      browser.getProcess().exitValue();
      return false;
    } catch (IllegalThreadStateException e) {
      // The process is still active.
      return true;
    }
  }

  @Override
  public int initialize(String args) {
    if (args == null || args.length() == 0) {
      getLogger().log(TreeLogger.ERROR, "ExternalBrowser runstyle requires an  argument listing an"
          + "executable of external browser to launch");
      return -1;
    }
    if (args.split(",").length > 1) {
      return 2; // Causes JUnitShell to log an error message about multi-browser deprecation
    }
    browser = new ExternalBrowser(args);
    Runtime.getRuntime().addShutdownHook(new ShutdownCb());
    return 1;
  }

  @Override
  public synchronized void launchModule(String moduleName) throws UnableToCompleteException {
    String commandArray[] = new String[2];
    commandArray[0] = browser.getPath();
    commandArray[1] = shell.getModuleUrl(moduleName);

    Process child = null;
    try {
      child = Runtime.getRuntime().exec(commandArray);
      if (child == null) {
        getLogger().log(TreeLogger.ERROR, "Problem exec()'ing " + commandArray[0]);
        throw new UnableToCompleteException();
      }
    } catch (Exception e) {
      getLogger().log(TreeLogger.ERROR, "Error launching external browser at " + browser.getPath(),
          e);
      throw new UnableToCompleteException();
    }
    browser.setProcess(child);
  }
}
