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

import com.google.gwt.codegen.rebind.GwtCodeGenContext;
import com.google.gwt.codegen.server.JavaSourceWriterBuilder;
import com.google.gwt.codegen.server.SourceWriter;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.IncrementalGenerator;
import com.google.gwt.core.ext.RebindMode;
import com.google.gwt.core.ext.RebindResult;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

public class TestRebinder extends IncrementalGenerator {

  @Override
  public RebindResult generateIncrementally(TreeLogger logger, GeneratorContext context,
      String typeName) throws UnableToCompleteException {

    TypeOracle oracle = context.getTypeOracle();
    JClassType existing = oracle.findType(typeName);
    String pkg = existing.getPackage().getName();
    String generatedName = typeName.replace(pkg + ".", "").replace('.', '_') + "_Impl";
    String qualifiedName = (pkg.length() == 0 ? "" : pkg + ".") + generatedName;
    if (context.tryReuseTypeFromCache(qualifiedName))
      return new RebindResult(RebindMode.USE_ALL_CACHED, qualifiedName);

    GwtCodeGenContext ctx = new GwtCodeGenContext(logger, context);
    JavaSourceWriterBuilder builder = ctx.addClass(pkg, generatedName);
    builder.addImplementedInterface(typeName);

    SourceWriter out = builder.createSourceWriter();
    out.println("public boolean test() { return true; }");
    out.close();

    return new RebindResult(RebindMode.USE_ALL_NEW, qualifiedName);
  }

  @Override
  public long getVersionId() {
    return 0;
  }

}
