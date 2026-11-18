/*
 * Copyright 2010 Google Inc.
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
package com.google.gwt.uibinder.rebind;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Empty implementation of {@link DesignTimeUtils}.
 */
public class DesignTimeUtilsStub implements DesignTimeUtils {
  public static final DesignTimeUtils EMPTY = new DesignTimeUtilsStub();

  @Override
  public void addDeclarations(IndentedWriter w) {
  }

  @Override
  public String getImplName(String implName) {
    return implName;
  }

  @Override
  public String getPath(Element element) {
    return null;
  }

  @Override
  public String getProvidedFactory(String typeName, String methodName,
      String args) {
    return null;
  }

  @Override
  public String getProvidedField(String typeName, String fieldName) {
    return null;
  }

  @Override
  public String getTemplateContent(String path) {
    return null;
  }

  @Override
  public void handleUIObject(Statements writer, XMLElement elem,
      String fieldName) {
  }

  @Override
  public boolean isDesignTime() {
    return false;
  }

  @Override
  public void putAttribute(XMLElement elem, String name, String value) {
  }

  @Override
  public void putAttribute(XMLElement elem, String name, String[] values) {
  }

  @Override
  public void rememberPathForElements(Document doc) {
  }

  @Override
  public void writeAttributes(Statements writer) {
  }
}