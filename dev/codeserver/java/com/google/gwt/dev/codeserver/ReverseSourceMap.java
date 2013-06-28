package com.google.gwt.dev.codeserver;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.util.Util;
import com.google.gwt.thirdparty.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gwt.thirdparty.debugging.sourcemap.SourceMapParseException;

/**
 * A mapping from Java lines to JavaScript.
 */
class ReverseSourceMap {
  private final SourceMapConsumerV3 consumer;

  ReverseSourceMap(SourceMapConsumerV3 consumer) {
    this.consumer = consumer;
  }

  /**
   * Reads a source map from disk and parses it into an in-memory representation.
   * If it can't be loaded, logs an error and returns null.
   */
  static ReverseSourceMap load(TreeLogger logger, ModuleState moduleState) {
    SourceMapConsumerV3 consumer = new SourceMapConsumerV3();
    String unparsed = Util.readFileAsString(moduleState.findSourceMap());
    try {
      consumer.parse(unparsed);
      return new ReverseSourceMap(consumer);
    } catch (SourceMapParseException e) {
      logger.log(TreeLogger.WARN, "can't parse source map", e);
      return null;
    }
  }

  /**
   * Returns true if the given line in a Java file has any corresponding JavaScript in
   * the GWT compiler's output.
   */
  boolean appearsInJavaScript(String filename, int lineNumber) {
    // TODO: getReverseMapping() seems to be off by one (lines numbered from zero). Why?
    return !consumer.getReverseMapping(filename, lineNumber - 1, -1).isEmpty();
  }
}
