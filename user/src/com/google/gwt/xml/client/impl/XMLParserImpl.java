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
package com.google.gwt.xml.client.impl;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.impl.DocumentImpl.NativeDocumentImpl;
import com.google.gwt.xml.client.impl.ElementImpl.NativeElementImpl;
import com.google.gwt.xml.client.impl.NodeImpl.NativeNodeImpl;
import com.google.gwt.xml.client.impl.NodeListImpl.NativeNodeListImpl;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Native implementation associated with
 * {@link com.google.gwt.xml.client.XMLParser}.
 */
public abstract class XMLParserImpl {

  static class XMLParserImplIE8And9 extends XMLParserImpl {

    @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
    static class NativeInternalDocumentImpl extends NativeDocumentImpl {
      boolean preserveWhiteSpace;
      native void setProperty(String name , String value);
      native boolean loadXML(String content);
      ParseError parseError;
    }
    
    @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
    static class ParseError {
      int line;
      int linepos;
      String reason;
    }

    @Override
    protected NativeInternalDocumentImpl createDocumentImpl() {
      NativeInternalDocumentImpl doc = JsHelper.selectDOMDocumentVersion();
      // preserveWhiteSpace is set to true here to prevent IE from throwing away
      // text nodes that consist of only whitespace characters. This makes it
      // act more like other browsers.
      doc.preserveWhiteSpace = true;
      doc.setProperty("SelectionNamespaces", "xmlns:xsl='http://www.w3.org/1999/XSL/Transform'");
      doc.setProperty("SelectionLanguage", "XPath");
      return doc;
    }

    @Override
    protected NativeElementImpl getElementByIdImpl(NativeDocumentImpl o, String elementId) {
      return o.nodeFromID(elementId);
    }

    @Override
    protected NativeNodeListImpl getElementsByTagNameImpl(NativeNodeImpl o,
        String tagName) {
      return o.selectNodes(".//*[local-name()='" + tagName + "']");
    }

    @Override
    protected String getPrefixImpl(NativeNodeImpl node) {
      return node.prefix;
    }

    @Override
    protected NativeNodeImpl importNodeImpl(
        NativeDocumentImpl o, NativeNodeImpl importedNode, boolean deep) {
      // IE6 does not seem to need or want nodes to be imported
      // as appends from different docs work perfectly
      // and this method is not supplied until MSXML5.0
      return importedNode;
    }
    
    @Override
    protected NativeDocumentImpl parseImpl(String contents) {
      NativeInternalDocumentImpl doc = createDocumentImpl();
      
      if (!doc.loadXML(contents)) {
        ParseError err = doc.parseError;
        throw new RuntimeException("line " + err.line + ", char " + err.linepos + ":" + err.reason);
      }
      return doc;
    }

    @Override
    protected String toStringImpl(ProcessingInstructionImpl node) {
      return toStringImpl((NodeImpl) node);
    }
    
    @Override
    protected String toStringImpl(NodeImpl node) {
      return node.node.xml;
    }
  }
  
  /**
   * This class implements the methods for standard browsers that use the
   * DOMParser model of XML parsing.
   */
  private static class XMLParserImplStandard extends XMLParserImpl {
    
    @JsType(isNative = true, name = "DOMParser", namespace = JsPackage.GLOBAL)
    static class DOMParser {
      native NativeDocumentImpl parseFromString(String contents, String mimeType);
    }

    protected final DOMParser domParser = new DOMParser();

    @Override
    protected NativeDocumentImpl createDocumentImpl() {
      return JsHelper.createDocumentImpl();
    }

    @Override
    protected NativeElementImpl getElementByIdImpl(NativeDocumentImpl document, String id) {
      return document.getElementById(id);
    }

    @Override
    protected NativeNodeListImpl getElementsByTagNameImpl(NativeNodeImpl o, String tagName) {
      return o.getElementsByTagNameNS("*", tagName);
    }

    @Override
    protected String getPrefixImpl(NativeNodeImpl node) {
      String fullName = node.nodeName;
      if (fullName != null && fullName.indexOf(":") != -1) {
        return fullName.split(":", 2)[0];
      }
      return null;
    }

    @Override
    protected NativeNodeImpl importNodeImpl(
        NativeDocumentImpl document, NativeNodeImpl importedNode, boolean deep) {
      return document.importNode(importedNode, deep);
    }

    @Override
    protected NativeDocumentImpl parseImpl(String contents) {
      NativeDocumentImpl result = domParser.parseFromString(contents, "text/xml");

      NativeElementImpl rootTag = result.documentElement;

      if ("parsererror".equals(rootTag.tagName)
          && "http://www.mozilla.org/newlayout/xml/parsererror.xml".equals(rootTag.namespaceURI)) {
        throw new RuntimeException(rootTag.firstChild.data);
      }

      return result;
    }

    @Override
    protected String toStringImpl(ProcessingInstructionImpl node) {
      return toStringImpl((NodeImpl) node);
    }

    @Override
    protected String toStringImpl(NodeImpl node) {
      return new XMLSerializer().serializeToString(node.node);
    }

    @JsType(isNative = true, name = "XMLSerializer", namespace = JsPackage.GLOBAL)
    private static class XMLSerializer {
      native String serializeToString(NativeNodeImpl node);
    }
  }

  /**
   * This class is Safari implementation of the XMLParser interface.
   */
  private static class XMLParserImplSafari extends XMLParserImplStandard {

    @Override
    protected NativeNodeListImpl getElementsByTagNameImpl(NativeNodeImpl o, String tagName) {
      return o.getElementsByTagName(tagName);
    }

    /**
     * <html><body><parsererror style="white-space: pre; border: 2px solid #c77; padding: 0 1em 0 1em;
     * margin: 1em; background-color: #fdd; color: black" >
     *
     * <h3>This page contains the following errors:</h3>
     *
     * <div style="font-family:monospace;font-size:12px" >error on line 1 at column 2:
     * xmlParseStartTag: invalid element name </div>
     *
     * <h3>Below is a rendering of the page up to the first error.</h3>
     *
     * </parsererror></body></html> is all you get from Safari. Hope that nobody wants to send one of
     * those error reports over the wire to be parsed by safari...
     *
     * @param contents contents
     * @return parsed JavaScript object
     * @see com.google.gwt.xml.client.impl.XMLParserImpl#parseImpl(java.lang.String)
     */
    @Override
    protected NativeDocumentImpl parseImpl(String contents) {
      NativeDocumentImpl result = domParser.parseFromString(contents, "text/xml");

      NativeNodeListImpl parseErrors = result.getElementsByTagName("parsererror");
      if (parseErrors.length > 0) {
        NativeNodeImpl error = parseErrors.item(0);
        if ("body".equals(error.parentNode.tagName)) {
          throw new RuntimeException(error.childNodes.item(1).innerHTML);
        }
      }

      return result;
    }
  }

  private static XMLParserImpl impl;

  public static XMLParserImpl getInstance() {
    if (impl == null) {
      impl = createImpl();
    }

    return impl;
  }

  private static XMLParserImpl createImpl() {
    String userAgent = System.getProperty("user.agent", "safari");

    if ("ie".equals(userAgent) || "ie9".equals(userAgent)) {
      return new XMLParserImplIE8And9();
    } else if ("safari".equals(userAgent)) {
      return new XMLParserImplSafari();
    } else {
      return new XMLParserImplStandard();
    }
  }

  static NativeElementImpl getElementById(NativeDocumentImpl document, String id) {
    return impl.getElementByIdImpl(document, id);
  }

  static NativeNodeListImpl getElementsByTagName(NativeNodeImpl o, String tagName) {
    return impl.getElementsByTagNameImpl(o, tagName);
  }

  static String getPrefix(NativeNodeImpl node) {
    return impl.getPrefixImpl(node);
  }

  static NativeNodeImpl importNode(NativeDocumentImpl document,
      NativeNodeImpl importedNode, boolean deep) {
    return impl.importNodeImpl(document, importedNode, deep);
  }

  /**
   * Not globally instantable.
   */
  XMLParserImpl() {
  }

  public final Document createDocument() {
    return (Document) NodeImpl.build(createDocumentImpl());
  }

  public final Document parse(String contents) {
    try {
      return (Document) NodeImpl.build(parseImpl(contents));
    } catch (Exception e) {
      throw new DOMParseException(contents, e);
    }
  }

  protected abstract NativeDocumentImpl createDocumentImpl();

  protected abstract NativeElementImpl getElementByIdImpl(NativeDocumentImpl document, String id);

  protected abstract NativeNodeListImpl getElementsByTagNameImpl(
      NativeNodeImpl node, String tagName);

  protected abstract String getPrefixImpl(NativeNodeImpl node);

  protected abstract NativeNodeImpl importNodeImpl(
      NativeDocumentImpl document, NativeNodeImpl importedNode, boolean deep);

  protected abstract NativeDocumentImpl parseImpl(String contents);
  
  abstract String toStringImpl(ProcessingInstructionImpl node);
  
  abstract String toStringImpl(NodeImpl node);
}
