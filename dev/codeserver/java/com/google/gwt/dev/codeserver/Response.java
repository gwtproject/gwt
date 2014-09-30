package com.google.gwt.dev.codeserver;

import com.google.gwt.core.ext.TreeLogger;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Encapsulates code that can send an HTTP response.
 */
interface Response {
  void send(HttpServletRequest request, HttpServletResponse response, TreeLogger logger)
      throws IOException;
}
