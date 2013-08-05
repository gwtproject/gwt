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
package com.google.gwt.sample.helloselfgen.selfgen.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.IncrementalGenerator;
import com.google.gwt.core.ext.RebindMode;
import com.google.gwt.core.ext.RebindResult;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.sample.helloselfgen.selfgen.client.UiBinderOwner;
import com.google.gwt.sample.helloselfgen.selfgen.client.impl.UiBinderWrapper;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;

public class UiBinderOwnerGenerator extends IncrementalGenerator {

  private static final String INTERNAL_BINDER_INTF = "InternalBinder";

  @Override
  public RebindResult generateIncrementally(TreeLogger logger, GeneratorContext context,
      String typeName) throws UnableToCompleteException {

    TypeOracle typeOracle = context.getTypeOracle();

    JClassType uiBinderOwnerType = typeOracle.findType(UiBinderOwner.class.getCanonicalName());
    
    JClassType ownerType = typeOracle.findType(typeName);
    String implName = ownerType.getName().replace(".", "$") + "_UiBinderOwnerImpl";

    String packageName = ownerType.getPackage().getName();
    String qualifiedImplName = ownerType.getPackage().getName() + "." + implName;

    if (context.getTypeOracle().findType(packageName, implName) != null) {
      return new RebindResult(RebindMode.USE_EXISTING, qualifiedImplName);
    }

    PrintWriter printWriter = context.tryCreate(logger, packageName, implName);
    if (printWriter == null) {
      return new RebindResult(RebindMode.USE_EXISTING, qualifiedImplName);
    }
    
    JParameterizedType parameterizedOwnerType = ownerType.asParameterizationOf(uiBinderOwnerType.isGenericType());
    
    JClassType rootType = parameterizedOwnerType.getTypeArgs()[0];
    String rootTypeName = rootType.getQualifiedSourceName();
    
    ClassSourceFileComposerFactory composerFactory =
        new ClassSourceFileComposerFactory(packageName, implName);

    String ownerTypeName = ownerType.getQualifiedSourceName();
    String uiBinderIntf = UiBinder.class.getCanonicalName();


    String gwtClassName = GWT.class.getCanonicalName();

    composerFactory.setSuperclass(UiBinderWrapper.class.getCanonicalName() + "<" + rootTypeName
        + ", " + ownerTypeName + ">");
    SourceWriter writer = composerFactory.createSourceWriter(context, printWriter);

    UiTemplate uiTemplate = ownerType.getAnnotation(UiTemplate.class);

    String template =
        uiTemplate != null ? uiTemplate.value() : ownerType.getSimpleSourceName() + ".ui.xml";

    writer.println();
    writer.println("@%s(\"%s\")", UiTemplate.class.getCanonicalName(), template);
    writer.println("static interface %s extends %s<%s, %s> { }", INTERNAL_BINDER_INTF, uiBinderIntf,
        rootTypeName, ownerTypeName);
    writer.println();

    writer.println("public %s() {", implName);
    writer.indentln("super(%s.<%s>create(%s.class));", gwtClassName, INTERNAL_BINDER_INTF,
        INTERNAL_BINDER_INTF, ownerTypeName);
    writer.println("}");

    writer.commit(logger);

    return new RebindResult(RebindMode.USE_ALL_NEW, qualifiedImplName);
  }

  @Override
  public long getVersionId() {
    return 0;
  }
}
