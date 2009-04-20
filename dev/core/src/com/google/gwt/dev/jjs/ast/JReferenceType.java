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
package com.google.gwt.dev.jjs.ast;

import com.google.gwt.dev.jjs.SourceInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for any reference type.
 */
public abstract class JReferenceType extends JType implements CanBeAbstract {

  public JClassType extnds;

  /**
   * Special serialization treatment.
   */
  public transient List<JField> fields = new ArrayList<JField>();

  public List<JInterfaceType> implments = new ArrayList<JInterfaceType>();

  /**
   * Special serialization treatment.
   */
  public transient List<JMethod> methods = new ArrayList<JMethod>();

  public JReferenceType(JProgram program, SourceInfo info, String name) {
    super(program, info, name, program.getLiteralNull());
  }

  @Override
  public String getJavahSignatureName() {
    return "L" + name.replaceAll("_", "_1").replace('.', '_') + "_2";
  }

  @Override
  public String getJsniSignatureName() {
    return "L" + name.replace('.', '/') + ';';
  }

  public JProgram getProgram() {
    return program;
  }

  public String getShortName() {
    int dotpos = name.lastIndexOf('.');
    return name.substring(dotpos + 1);
  }

  /**
   * See {@link #writeMembers(ObjectOutputStream)}.
   * 
   * @see #writeMembers(ObjectOutputStream)
   */
  @SuppressWarnings("unchecked")
  void readMembers(ObjectInputStream stream) throws IOException,
      ClassNotFoundException {
    fields = (List<JField>) stream.readObject();
    methods = (List<JMethod>) stream.readObject();
  }

  /**
   * See {@link #writeMethodBodies(ObjectOutputStream).
   * 
   * @see #writeMethodBodies(ObjectOutputStream)
   */
  void readMethodBodies(ObjectInputStream stream) throws IOException,
      ClassNotFoundException {
    for (JMethod method : methods) {
      method.readBody(stream);
    }
  }

  /**
   * After all types are written to the stream without transient members, this
   * method actually writes fields and methods to the stream, which establishes
   * type identity for them.
   * 
   * @see JProgram#writeObject(ObjectOutputStream)
   */
  void writeMembers(ObjectOutputStream stream) throws IOException {
    stream.writeObject(fields);
    stream.writeObject(methods);
  }

  /**
   * After all types, fields, and methods are written to the stream, this method
   * writes method bodies to the stream.
   * 
   * @see JProgram#writeObject(ObjectOutputStream)
   */
  void writeMethodBodies(ObjectOutputStream stream) throws IOException {
    for (JMethod method : methods) {
      method.writeBody(stream);
    }
  }
}
