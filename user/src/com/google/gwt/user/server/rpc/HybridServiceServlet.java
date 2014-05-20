/*
 * Copyright 2009 Google Inc.
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

import static com.google.gwt.user.client.rpc.RpcRequestBuilder.MODULE_BASE_HEADER;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.user.client.rpc.SerializationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * EXPERIMENTAL and subject to change. Do not use this in production code.
 * <p>
 * This RemoteServiceServlet provides support for GWT-RPC clients.
 */
public class HybridServiceServlet extends AbstractRemoteServiceServlet implements
    SerializationPolicyProvider {

  private static final boolean DUMP_PAYLOAD = Boolean.getBoolean("gwt.rpc.dumpPayload");
  /**
   * A cache of moduleBaseURL and serialization policy strong name to
   * {@link SerializationPolicy}.
   */
  private final Map<String, SerializationPolicy> serializationPolicyCache = new HashMap<String, SerializationPolicy>();

  @Override
  public final SerializationPolicy getSerializationPolicy(String moduleBaseURL,
      String strongName) {

    SerializationPolicy serializationPolicy = getCachedSerializationPolicy(
        moduleBaseURL, strongName);
    if (serializationPolicy != null) {
      return serializationPolicy;
    }

    serializationPolicy = doGetSerializationPolicy(getThreadLocalRequest(),
        moduleBaseURL, strongName);

    if (serializationPolicy == null) {
      // Failed to get the requested serialization policy; use the default
      log(
          "WARNING: Failed to get the SerializationPolicy '"
              + strongName
              + "' for module '"
              + moduleBaseURL
              + "'; a legacy, 1.3.3 compatible, serialization policy will be used."
              + "  You may experience SerializationExceptions as a result.");
      serializationPolicy = RPC.getDefaultSerializationPolicy();
    }

    // This could cache null or an actual instance. Either way we will not
    // attempt to lookup the policy again.
    putCachedSerializationPolicy(moduleBaseURL, strongName, serializationPolicy);

    return serializationPolicy;
  }

  /**
   * Extract the module's base path from the current request.
   *
   * @return the module's base path, modulo protocol and host, as reported by
   *         {@link com.google.gwt.core.client.GWT#getModuleBaseURL()} or
   *         <code>null</code> if the request did not contain the
   *         {@value com.google.gwt.user.client.rpc.RpcRequestBuilder#MODULE_BASE_HEADER} header
   */
  protected final String getRequestModuleBasePath() {
    try {
      String header = getThreadLocalRequest().getHeader(MODULE_BASE_HEADER);
      if (header == null) {
        return null;
      }
      String path = new URL(header).getPath();
      String contextPath = getThreadLocalRequest().getContextPath();
      if (!path.startsWith(contextPath)) {
        return null;
      }
      return path.substring(contextPath.length());
    } catch (MalformedURLException e) {
      return null;
    }
  }
  public void processCall(String payload,
      OutputStream stream) throws SerializationException {

    String toReturn = processCall(payload);
    onAfterResponseSerialized(toReturn);

    try {
      stream.write(toReturn.getBytes(RPCServletUtils.CHARSET_UTF8));
    } catch (IOException e) {
      throw new SerializationException("Unable to commit bytes", e);
    }
  }

  public String processCall(String payload) throws SerializationException {
    try {
      RPCRequest rpcRequest = RPC.decodeRequest(payload, this.getClass(), this);
      onAfterRequestDeserialized(rpcRequest);
      return RPC.invokeAndEncodeResponse(this, rpcRequest.getMethod(),
          rpcRequest.getParameters(), rpcRequest.getSerializationPolicy(),
          rpcRequest.getFlags());
    } catch (IncompatibleRemoteServiceException ex) {
      log(
          "An IncompatibleRemoteServiceException was thrown while processing this call.",
          ex);
      return RPC.encodeResponseForFailure(null, ex);
    } catch (RpcTokenException tokenException) {
      log("An RpcTokenException was thrown while processing this call.",
          tokenException);
      return RPC.encodeResponseForFailure(null, tokenException);
    }
  }

  /**
   * Gets the {@link SerializationPolicy} for given module base URL and strong
   * name if there is one.
   * 
   * Override this method to provide a {@link SerializationPolicy} using an
   * alternative approach.
   * 
   * @param request the HTTP request being serviced
   * @param moduleBaseURL as specified in the incoming payload
   * @param strongName a strong name that uniquely identifies a serialization
   *          policy file
   * @return a {@link SerializationPolicy} for the given module base URL and
   *         strong name, or <code>null</code> if there is none
   */
  protected SerializationPolicy doGetSerializationPolicy(
      HttpServletRequest request, String moduleBaseURL, String strongName) {
    return RemoteServiceServlet.loadSerializationPolicy(this, request,
        moduleBaseURL, strongName);
  }

  /**
   * @param serializedResponse
   */
  protected void onAfterResponseSerialized(String serializedResponse) {
  }

  private SerializationPolicy getCachedSerializationPolicy(
      String moduleBaseURL, String strongName) {
    synchronized (serializationPolicyCache) {
      return serializationPolicyCache.get(moduleBaseURL + strongName);
    }
  }

  private void putCachedSerializationPolicy(String moduleBaseURL,
      String strongName, SerializationPolicy serializationPolicy) {
    synchronized (serializationPolicyCache) {
      serializationPolicyCache.put(moduleBaseURL + strongName,
          serializationPolicy);
    }
  }

  @Override
  public final void processPost(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException,
      SerializationException {

    // Read the request fully.
    String requestPayload = readContent(request);
    if (DUMP_PAYLOAD) {
      System.out.println(requestPayload);
    }

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // Configure the OutputStream based on configuration and capabilities
    boolean canCompress = RPCServletUtils.acceptsGzipEncoding(request)
        && shouldCompressResponse(request, response);

    OutputStream out;
    if (DUMP_PAYLOAD) {
      out = new ByteArrayOutputStream();

    } else if (canCompress) {
      RPCServletUtils.setGzipEncodingHeader(response);
      out = new GZIPOutputStream(response.getOutputStream());

    } else {
      out = response.getOutputStream();
    }

    // Invoke the core dispatching logic, which returns the serialized result.
    processCall(requestPayload, out);

    if (DUMP_PAYLOAD) {
      byte[] bytes = ((ByteArrayOutputStream) out).toByteArray();
      System.out.println(new String(bytes, "UTF-8"));
      response.getOutputStream().write(bytes);
    } else if (canCompress) {
      /*
       * We want to write the end of the gzip data, but not close the underlying
       * OutputStream in case there are servlet filters that want to write
       * headers after processPost().
       */
      ((GZIPOutputStream) out).finish();
    }
  }

  /**
   * Determines whether the response to a given servlet request should or should
   * not be GZIP compressed. This method is only called in cases where the
   * requester accepts GZIP encoding.
   * <p>
   * This implementation currently returns <code>true</code> if the request
   * originates from a non-local address. Subclasses can override this logic.
   * </p>
   *
   * @param request the request being served
   * @param response the response that will be written into
   * @return <code>true</code> if responsePayload should be GZIP compressed,
   *         otherwise <code>false</code>.
   */
  protected boolean shouldCompressResponse(HttpServletRequest request,
      HttpServletResponse response) {
    return !isRequestFromLocalAddress();
  }

  /**
   * Utility function to determine if the thread-local request originates from a
   * local address.
   */
  private boolean isRequestFromLocalAddress() {
    try {
      InetAddress addr = InetAddress.getByName(getThreadLocalRequest().getRemoteAddr());

      return InetAddress.getLocalHost().equals(addr)
          || addr.isLoopbackAddress() || addr.isSiteLocalAddress()
          || addr.isLinkLocalAddress();
    } catch (UnknownHostException e) {
      return false;
    }
  }

}
