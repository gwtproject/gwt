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
package com.google.gwt.dev;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.jjs.PermutationResult;
import com.google.gwt.dev.jjs.UnifiedAst;
import com.google.gwt.dev.util.PersistenceBackedObject;
import com.google.gwt.dev.util.StringInterningObjectInputStream;
import com.google.gwt.dev.util.log.perf.SimpleEvent;
import com.google.gwt.util.tools.shared.StringUtils;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A PermutationWorkerFactory designed to launch instances of
 * {@link CompilePermsServer}. The system property
 * {@value #JAVA_COMMAND_PROPERTY} can be used to change the command used to
 * launch the JVM. The system property {@link #JVM_ARGS_PROPERTY} can be used to
 * override the JVM args passed to the subprocess.
 */
public class ExternalPermutationWorkerFactory extends PermutationWorkerFactory {

  /**
   * Allows accept() to be called a finite number of times on a ServerSocket
   * before closing the socket.
   */
  private static class CountedServerSocket {
    private final ServerSocket sock;
    private final Set<String> validCookies;
    private final TreeLogger logger;

    public CountedServerSocket(TreeLogger logger, ServerSocket sock, Set<String> validCookies) {
      assert sock != null;
      assert !validCookies.isEmpty();

      this.logger = logger;
      this.sock = sock;
      this.validCookies = validCookies;
    }

    /**
     * Returns an authenticated Socket connection.
     * @return the next connecting socket to present a valid unused cookie
     * @throws IOException if there is an error with the socket
     */
    public synchronized Socket accept() throws IOException {
      while (!validCookies.isEmpty()) {
        Socket s = sock.accept();
        byte[] bytes = s.getInputStream().readNBytes(32);
        String cookie = new String(bytes, StandardCharsets.US_ASCII);
        if (validCookies.remove(cookie)) {
          if (validCookies.isEmpty()) {
            // We've removed the final cookie, no further connections are expected
            sock.close();
          }
          return s;
        }
        s.close();
        logger.log(TreeLogger.WARN, "Rejected connection from "
            + s.getRemoteSocketAddress() + " with invalid cookie: " + cookie);
      }
      throw new IllegalStateException("No more cookies, too many calls to accept()");
    }
  }

  private static class ExternalPermutationWorker implements PermutationWorker {
    private final File astFile;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final CountedServerSocket serverSocket;
    private Socket workerSocket;

    public ExternalPermutationWorker(CountedServerSocket sock, File astFile) {
      this.astFile = astFile;
      this.serverSocket = sock;
    }

    @Override
    public void compile(TreeLogger logger, CompilerContext compilerContext, Permutation permutation,
        PersistenceBackedObject<PermutationResult> resultFile)
        throws TransientWorkerException, UnableToCompleteException {

      // If we've just started, we need to get a connection from a subprocess
      if (workerSocket == null) {
        try {
          /*
           * We've set SO_TIMEOUT, so this may fail if the remote process never
           * connects back.
           */
          workerSocket = serverSocket.accept();

          in = new StringInterningObjectInputStream(workerSocket.getInputStream());
          out = new ObjectOutputStream(workerSocket.getOutputStream());
          out.writeObject(astFile);

          // Get the remote worker's estimate of memory use
          long memoryUse = in.readLong();
          if (logger.isLoggable(TreeLogger.SPAM)) {
            logger.log(TreeLogger.SPAM, "Remote process indicates " + memoryUse
                + " bytes of memory used");
          }

        } catch (SocketTimeoutException e) {
          throw new TransientWorkerException(
              "Remote process did not connect within timeout period", e);
        } catch (IOException e) {
          throw new TransientWorkerException(
              "Unable to communicate with worker", e);
        }
      }

      try {
        out.writeBoolean(true);
        out.writeObject(resultFile);
        out.writeObject(permutation);
        out.flush();

        Throwable t = (Throwable) in.readObject();
        if (t != null) {
          logger.log(TreeLogger.ERROR, "Error from external worker", t);
          throw new UnableToCompleteException();
        }
      } catch (IOException e) {
        logger.log(TreeLogger.WARN, "Lost communication with remote process", e);
        throw new TransientWorkerException(
            "Lost communication with remote process", e);
      } catch (ClassNotFoundException e) {
        logger.log(TreeLogger.ERROR, "Unable to receive response", e);
        throw new UnableToCompleteException();
      }
    }

    @Override
    public String getName() {
      return "External worker "
          + (workerSocket != null ? workerSocket.getRemoteSocketAddress()
              : "unconnected");
    }

    @Override
    public void shutdown() {
      if (out != null) {
        try {
          out.writeBoolean(false);
          out.flush();
          out.close();
        } catch (IOException e) {
          // Not much to do here
        }
      }

      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          // Not much to do here
        }
      }

      if (workerSocket != null) {
        try {
          workerSocket.close();
        } catch (IOException e) {
          // Nothing to do
        }
      }
    }
  }

  /**
   * A system property that can be used to override the command used to invoke a
   * JVM instance.
   */
  public static final String JAVA_COMMAND_PROPERTY = "gwt.jjs.javaCommand";

  /**
   * A system property that can be used to override the JVM args passed to the
   * subprocess.
   */
  public static final String JVM_ARGS_PROPERTY = "gwt.jjs.javaArgs";

  /**
   * Random number generator used for keys to worker threads.
   */
  private static final SecureRandom random = new SecureRandom();

  /**
   * Launches an external worker, with a port to connect to and a cookie to authenticate with.
   */
  private static void launchExternalWorker(TreeLogger logger, String cookie, int port)
      throws UnableToCompleteException {

    String javaCommand = System.getProperty(JAVA_COMMAND_PROPERTY,
        System.getProperty("java.home") + File.separator + "bin"
            + File.separator + "java");
    if (logger.isLoggable(TreeLogger.TRACE)) {
      logger.log(TreeLogger.TRACE, "javaCommand = " + javaCommand);
    }

    // Construct the arguments
    List<String> args = new ArrayList<String>();
    args.add(javaCommand);

    // This will include -Xmx, -D, etc...
    String userJvmArgs = System.getProperty(JVM_ARGS_PROPERTY);
    if (userJvmArgs == null) {
      args.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
    } else {
      args.addAll(Arrays.asList(userJvmArgs.split(" ")));
    }

    // Determine the logLevel for the external program
    TreeLogger.Type logLevel = TreeLogger.ERROR;
    for (TreeLogger.Type t : TreeLogger.Type.values()) {
      if (logger.isLoggable(t)) {
        logLevel = t;
      } else {
        break;
      }
    }

    // Cook up the classpath, main class, and extra args
    args.addAll(Arrays.asList(
        CompilePermsServer.class.getName(), "-host", "localhost", "-port",
        String.valueOf(port), "-logLevel", logLevel.toString(), "-cookie",
        cookie));

    // Filter undesirable arguments
    for (Iterator<String> iter = args.iterator(); iter.hasNext();) {
      String arg = iter.next();
      if (arg.startsWith("-agentlib")) {
        iter.remove();
      }
    }

    ProcessBuilder builder = new ProcessBuilder();
    builder.environment().put("CLASSPATH", ManagementFactory.getRuntimeMXBean().getClassPath());
    builder.command(args);

    try {
      final Process proc = builder.start();
      final BufferedReader bin = new BufferedReader(new InputStreamReader(
          proc.getInputStream()));
      final BufferedReader berr = new BufferedReader(new InputStreamReader(
          proc.getErrorStream()));
      final TreeLogger procLogger = logger.branch(TreeLogger.DEBUG,
          "Process output");

      // Threads to copy stdout, stderr to the logger
      new Thread(new Runnable() {
        @Override
        public void run() {
          while (true) {
            try {
              String line = bin.readLine();
              if (line == null) {
                break;
              }
              procLogger.log(TreeLogger.INFO, line);
            } catch (EOFException e) {
              // Ignore
            } catch (IOException e) {
              procLogger.log(TreeLogger.ERROR,
                  "Unable to read from subprocess", e);
            }
          }
        }
      }).start();

      new Thread(new Runnable() {
        @Override
        public void run() {
          while (true) {
            try {
              String line = berr.readLine();
              if (line == null) {
                break;
              }
              procLogger.log(TreeLogger.ERROR, line);
            } catch (EOFException e) {
              // Ignore
            } catch (IOException e) {
              procLogger.log(TreeLogger.ERROR,
                  "Unable to read from subprocess", e);
            }
          }
        }
      }).start();

      // The child process should not outlive this JVM
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            proc.exitValue();
          } catch (IllegalThreadStateException e) {
            proc.destroy();
          }
        }
      }));

    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "Unable to start external process", e);
      throw new UnableToCompleteException();
    }
  }

  private ServerSocket sock = null;

  @Override
  public Collection<PermutationWorker> getWorkers(TreeLogger logger,
      UnifiedAst unifiedAst, int numWorkers) throws UnableToCompleteException {
    ensureSocket(logger);
    File astFile;
    try {
      astFile = File.createTempFile("externalPermutationWorkerFactory", ".ser");
      astFile.deleteOnExit();
      try (SimpleEvent ignored = new SimpleEvent("Writing UnifiedAst to disk");
           OutputStream stream = new FileOutputStream(astFile);
           ObjectOutputStream objectStream = new ObjectOutputStream(stream)) {
        objectStream.writeObject(unifiedAst);
      } catch (IOException e) {
        logger.log(TreeLogger.ERROR, "Unable to write file: "
            + astFile.getAbsolutePath(), e);
        throw new UnableToCompleteException();
      }
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "Unable to create temporary file", e);
      throw new UnableToCompleteException();
    }

    Set<String> cookies = Collections.synchronizedSet(new HashSet<String>(
        numWorkers));
    for (int i = 0; i < numWorkers; i++) {
      byte[] cookieBytes = new byte[16];
      random.nextBytes(cookieBytes);
      cookies.add(StringUtils.toHexString(cookieBytes));
    }
    CountedServerSocket countedSock = new CountedServerSocket(logger, sock, cookies);
    List<PermutationWorker> toReturn = new ArrayList<PermutationWorker>(
        numWorkers);

    // Copy the cookies to a list so we can guarantee we never have a race with removals
    List<String> cookieList = new ArrayList<>(cookies);

    // TODO(spoon): clean up already-launched processes if we get an exception?
    for (int i = 0; i < numWorkers; i++) {
      launchExternalWorker(logger, cookieList.get(i), sock.getLocalPort());
      toReturn.add(new ExternalPermutationWorker(countedSock, astFile));
    }

    return toReturn;
  }

  @Override
  public boolean isLocal() {
    return true;
  }

  private synchronized void ensureSocket(TreeLogger logger) throws UnableToCompleteException {
    if (sock != null) {
      return;
    }
    try {
      sock = new ServerSocket();
      /*
       * Have accept() wait no more than one minute for a connection. This
       * prevents dead-head behavior.
       */
      sock.setSoTimeout(60000);
      sock.bind(null);
      if (logger.isLoggable(TreeLogger.SPAM)) {
        logger.log(TreeLogger.SPAM, "Listening for external workers on port "
            + sock.getLocalPort());
      }
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "Unable to create socket", e);
      throw new UnableToCompleteException();
    }
  }
}
