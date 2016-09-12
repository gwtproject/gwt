/*
 * Copyright 2016 Google Inc.
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

import com.google.gwt.xml.client.impl.DocumentImpl.NativeDocumentImpl;
import com.google.gwt.xml.client.impl.XMLParserImpl.XMLParserImplIE8And9.NativeInternalDocumentImpl;

class JsHelper {
  /**
   * Called from JSNI to select a DOM document; this is necessary due to
   * different versions of IE and Windows having different available DOM
   * implementations.
   */
  public static native NativeInternalDocumentImpl selectDOMDocumentVersion() /*-{
    try { return new ActiveXObject("Msxml2.DOMDocument"); } catch (e) { }
    try { return new ActiveXObject("MSXML.DOMDocument"); } catch (e) { }
    try { return new ActiveXObject("MSXML3.DOMDocument"); } catch (e) { }
    try { return new ActiveXObject("Microsoft.XmlDom"); } catch (e) { }
    try { return new ActiveXObject("Microsoft.DOMDocument"); } catch (e) { }

    throw new Error("XMLParserImplIE6.createDocumentImpl: "
        + "Could not find appropriate version of DOMDocument.");
  }-*/;

  public static native NativeDocumentImpl createDocumentImpl() /*-{
    return document.implementation.createDocument("", "", null);
  }-*/;
}

