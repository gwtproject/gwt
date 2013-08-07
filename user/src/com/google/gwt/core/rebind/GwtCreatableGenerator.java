/*
 * Copyright 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.core.rebind;

import java.util.List;

import com.google.gwt.codegen.rebind.GwtCodeGenContext;
import com.google.gwt.codegen.server.JavaSourceWriterBuilder;
import com.google.gwt.codegen.server.SourceWriter;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.core.shared.GwtCreatable;
import com.google.gwt.core.shared.GwtCreate;
import com.google.gwt.dev.javac.StandardGeneratorContext;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JParameter;

public class GwtCreatableGenerator {

  private static final String NameSuffix = "_GwtC";

  public static String exec(TreeLogger logger, StandardGeneratorContext context, String typeName,
      GwtCreate gwtc) throws UnableToCompleteException {

    TypeOracle oracle = context.getTypeOracle();
    JClassType existing = oracle.findType(typeName);
    String pkg = existing.getPackage().getName();
    String generatedName = typeName.replace(pkg + ".", "").replace('.', '_') + NameSuffix;
    String qualifiedName = (pkg.length() == 0 ? "" : pkg + ".") + generatedName;
    if (oracle.findType(pkg, generatedName) != null)
      return qualifiedName;

    String simpleName = existing.getSimpleSourceName();
    String creator = "return GWT.create(" + simpleName + ".class);";
    String result = null;
    if (gwtc != null) {
      // The client has specified a generator. Let's go ahead and apply it for them.
      Class<? extends Generator> generator = gwtc.generator();
      try {
        logger.log(Type.INFO, "Generating " + typeName + " with generator " + generator.getName());
        result = context.runGenerator(logger, generator, typeName);
      } catch (UnableToCompleteException e) {
        logger.log(Type.ERROR, "Unable to rebind " + typeName + "with " + generator.getName(), e);
        return null;
      }
      creator = "return new " + result + "();";
    }

    GwtCodeGenContext ctx = new GwtCodeGenContext(logger, context);
    JavaSourceWriterBuilder writer = ctx.addClass(pkg, generatedName);
    writer.addImport(typeName);
    if (result == null)
      writer.addImport(GWT.class.getName());
    writer.setSuperclass(GwtCreatable.class.getName() + "<" + simpleName + ">");
    SourceWriter out = writer.createSourceWriter();

    out.println("public " + generatedName + "() {");
    out.indentln("super(" + simpleName + ".class);");
    out.println("}");

    out.println();

    out.println("public final " + simpleName + " create() {");
    out.indentln(creator);
    out.println("}");

    out.close();

    return qualifiedName;
  }

  public static GwtCreate findGwtCreate(TreeLogger logger, JMethod currentMethod, TypeOracle oracle) {
    JDeclaredType enclosingType = currentMethod.getEnclosingType();
    com.google.gwt.core.ext.typeinfo.JClassType typeInfo = oracle.findType(enclosingType.getName());
    com.google.gwt.core.ext.typeinfo.JMethod method;
    try {
      List<JParameter> params = currentMethod.getParams();
      int i = params.size();
      JType[] types = new JType[i];
      for (; i-- > 0;) {
        com.google.gwt.dev.jjs.ast.JType param = params.get(i).getType();
        // TODO worry about binary/source name translations.
        types[i] = oracle.findType(param.getName());
      }
      method = typeInfo.getMethod(currentMethod.getName(), types);
    } catch (NotFoundException e) {
      logger.log(Type.ERROR, "Error looking up " + currentMethod + " while resolve GwtCreatable; "
          + "expect subsequent errors.");
      return null;
    }
    return method.getAnnotation(GwtCreate.class);
  }

}
