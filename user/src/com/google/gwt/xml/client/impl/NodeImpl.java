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

import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.impl.AttrImpl.NativeAttrImpl;
import com.google.gwt.xml.client.impl.CDATASectionImpl.NativeCDATASectionImpl;
import com.google.gwt.xml.client.impl.CommentImpl.NativeCommentImpl;
import com.google.gwt.xml.client.impl.DocumentFragmentImpl.NativeDocumentFragmentImpl;
import com.google.gwt.xml.client.impl.DocumentImpl.NativeDocumentImpl;
import com.google.gwt.xml.client.impl.ElementImpl.NativeElementImpl;
import com.google.gwt.xml.client.impl.NamedNodeMapImpl.NativeNamedNodeMapImpl;
import com.google.gwt.xml.client.impl.NodeListImpl.NativeNodeListImpl;
import com.google.gwt.xml.client.impl.ProcessingInstructionImpl.NativeProcessingInstructionImpl;
import com.google.gwt.xml.client.impl.TextImpl.NativeTextImpl;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * This class wraps the native Node object.
 */
class NodeImpl extends DOMItem implements Node {

  @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
  static class NativeNodeImpl extends NativeDomItem {
    NativeNamedNodeMapImpl attributes;
    String nodeName;
    String nodeValue;
    NativeDocumentImpl ownerDocument;
    NativeNodeImpl nextSibling;
    String namespaceURI;
    NativeElementImpl parentNode;
    String prefix;
    NativeNodeImpl previousSibling;
    NativeNodeListImpl childNodes;
    String xml;
    String innerHTML;
    Object nodeType;

    @JsOverlay
    final short getNodeType() {
      if (nodeType == null) {
        return -1;
      }
      return nodeTypeAsShort();
    }

    @JsProperty(name = "nodeType")
    native short nodeTypeAsShort();

    native NativeNodeListImpl selectNodes(String selector);
    native NativeNodeListImpl getElementsByTagName(String selector);
    native NativeNodeListImpl getElementsByTagNameNS(String ns ,String tagName);
    native NativeNodeImpl appendChild(NativeNodeImpl child);
    native NativeNodeImpl cloneNode(boolean deep);
    native boolean hasChildNodes();
    native NativeNodeImpl insertBefore(NativeNodeImpl newChildJs, NativeNodeImpl refChildJs);
    native void normalize();
    native NativeNodeImpl removeChild(NativeNodeImpl child);
    native NativeNodeImpl replaceChild(NativeNodeImpl newChild, NativeNodeImpl oldChild);
  }
  
  /**
   * This method creates a new node of the correct type.
   * 
   * @param node - the supplied DOM JavaScript object
   * @return a Node object that corresponds to the DOM object
   */
  static Node build(NativeNodeImpl node) {
    if (node == null) {
      return null;
    }

    switch (node.getNodeType()) {
      case Node.ATTRIBUTE_NODE:
        return new AttrImpl((NativeAttrImpl) node);
      case Node.CDATA_SECTION_NODE:
        return new CDATASectionImpl((NativeCDATASectionImpl) node);
      case Node.COMMENT_NODE:
        return new CommentImpl((NativeCommentImpl) node);
      case Node.DOCUMENT_FRAGMENT_NODE:
        return new DocumentFragmentImpl((NativeDocumentFragmentImpl) node);
      case Node.DOCUMENT_NODE:
        return new DocumentImpl((NativeDocumentImpl) node);
      case Node.ELEMENT_NODE:
        return new ElementImpl((NativeElementImpl) node);
      case Node.PROCESSING_INSTRUCTION_NODE:
        return new ProcessingInstructionImpl((NativeProcessingInstructionImpl) node);
      case Node.TEXT_NODE:
        return new TextImpl((NativeTextImpl) node);
      default:
        return new NodeImpl(node);
    }
  }

  final NativeNodeImpl node;

  /**
   * creates a new NodeImpl from the supplied JavaScriptObject.
   * 
   * @param jso - the DOM node JavaScriptObject
   */
  protected NodeImpl(NativeNodeImpl jso) {
    super(jso);
    this.node = jso;
  }

  /**
   * This function delegates to the native method <code>appendChild</code> in
   * XMLParserImpl.
   */
  @Override
  public Node appendChild(Node newChild) {
    NodeImpl c = (NodeImpl) newChild;
    try {
      final NativeNodeImpl appendChildResults = node.appendChild(c.node);
      return NodeImpl.build(appendChildResults);
    } catch (Exception e) {
      throw new DOMNodeException(DOMException.INVALID_MODIFICATION_ERR, e, this);
    }
  }

  /**
   * This function delegates to the native method <code>cloneNode</code> in
   * XMLParserImpl.
   */
  @Override
  public Node cloneNode(boolean deep) {
    return NodeImpl.build(node.cloneNode(deep));
  }

  @Override
  public NamedNodeMap getAttributes() {
    return new NamedNodeMapImpl(node.attributes);
  }

  @Override
  public NodeList getChildNodes() {
    return new NodeListImpl(node.childNodes);
  }

  @Override
  public Node getFirstChild() {
    return getChildNodes().item(0);
  }

  @Override
  public Node getLastChild() {
    return getChildNodes().item(getChildNodes().getLength() - 1);
  }

  /**
   * This function delegates to the native method <code>getNamespaceURI</code>
   * in XMLParserImpl.
   */
  @Override
  public String getNamespaceURI() {
    return node.namespaceURI;
  }

  @Override
  public Node getNextSibling() {
    return NodeImpl.build(node.nextSibling);
  }

  @Override
  public String getNodeName() {
    return node.nodeName;
  }

  @Override
  public short getNodeType() {
    return node.getNodeType();
  }

  @Override
  public String getNodeValue() {
    return node.nodeValue;
  }

  @Override
  public Document getOwnerDocument() {
    return (Document) NodeImpl.build(node.ownerDocument);
  }

  @Override
  public Node getParentNode() {
    return NodeImpl.build(node.parentNode);
  }

  /**
   * This function delegates to the native method <code>getPrefix</code> in
   * XMLParserImpl.
   */
  @Override
  public String getPrefix() {
    return XMLParserImpl.getPrefix(node);
  }

  @Override
  public Node getPreviousSibling() {
    return NodeImpl.build(node.previousSibling);
  }

  /**
   * This function delegates to the native method <code>hasAttributes</code>
   * in XMLParserImpl.
   */
  @Override
  public boolean hasAttributes() {
    return node.attributes.length > 0;
  }

  /**
   * This function delegates to the native method <code>hasChildNodes</code>
   * in XMLParserImpl.
   */
  @Override
  public boolean hasChildNodes() {
    return node.hasChildNodes();
  }

  /**
   * This function delegates to the native method <code>insertBefore</code> in
   * XMLParserImpl.
   */
  @Override
  public Node insertBefore(Node newChild, Node refChild) {
    try {
      final NativeNodeImpl newChildJs = ((NodeImpl) newChild).node;
      final NativeNodeImpl refChildJs;
      if (refChild != null) {
        refChildJs = ((NodeImpl) refChild).node;
      } else {
        refChildJs = null;
      }
      NativeNodeImpl insertBeforeResults = node.insertBefore(newChildJs, refChildJs);
      return NodeImpl.build(insertBeforeResults);
    } catch (Exception e) {
      throw new DOMNodeException(DOMException.INVALID_MODIFICATION_ERR, e, this);
    }
  }

  /**
   * This function delegates to the native method <code>normalize</code> in
   * XMLParserImpl.
   */
  @Override
  public void normalize() {
    node.normalize();
  }

  /**
   * This function delegates to the native method <code>removeChild</code> in
   * XMLParserImpl.
   */
  @Override
  public Node removeChild(Node oldChild) {
    try {
      NativeNodeImpl oldChildJs = ((NodeImpl) oldChild).node;
      NativeNodeImpl removeChildResults = node.removeChild(oldChildJs);
      return NodeImpl.build(removeChildResults);
    } catch (Exception e) {
      throw new DOMNodeException(DOMException.INVALID_MODIFICATION_ERR, e, this);
    }
  }

  /**
   * This function delegates to the native method <code>replaceChild</code> in XMLParserImpl.
   */
  @Override
  public Node replaceChild(Node newChild, Node oldChild) {
    try {
      final NativeNodeImpl newChildJs = ((NodeImpl) newChild).node;
      final NativeNodeImpl oldChildJs = ((NodeImpl) oldChild).node;
      final NativeNodeImpl replaceChildResults = node.replaceChild(newChildJs, oldChildJs);
      return NodeImpl.build(replaceChildResults);
    } catch (Exception e) {
      throw new DOMNodeException(DOMException.INVALID_MODIFICATION_ERR, e, this);
    }
  }

  /**
   * This function delegates to the native method <code>setNodeValue</code> in
   * XMLParserImpl.
   */
  @Override
  public void setNodeValue(String nodeValue) {
    try {
      node.nodeValue = nodeValue;
    } catch (Exception e) {
      throw new DOMNodeException(DOMException.INVALID_MODIFICATION_ERR, e, this);
    }
  }

  @Override
  public String toString() {
    return XMLParserImpl.getInstance().toStringImpl(this);
  }
}
