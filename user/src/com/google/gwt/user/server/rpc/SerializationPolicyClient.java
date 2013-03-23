package com.google.gwt.user.server.rpc;

/**
 * A client that downloads serialization policies from a URL, for use with
 * Super Dev Mode.
 * @see RemoteServiceServlet#getCodeServerClient
 */
public interface SerializationPolicyClient {

  /**
   * Attempts to read a policy from a given URL.
   * Logs any errors to the given interface.
   * @return the policy or null if unavailable.
   */
  SerializationPolicy loadPolicy(String url, Logger logger);

  /**
   * Destination for the loader's log messages.
   */
  interface Logger {
    void log(String message);
    void log(String message, Throwable throwable);
  }
}
