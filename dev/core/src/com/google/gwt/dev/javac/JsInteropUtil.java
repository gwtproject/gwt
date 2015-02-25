/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.dev.javac;

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JField;
import com.google.gwt.dev.jjs.ast.JMember;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.js.ast.JsNameRef;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;

/**
 * Utility functions to interact with JDT classes for JsInterop.
 */
public final class JsInteropUtil {

  public static final String JSEXPORT_CLASS = "com.google.gwt.core.client.js.JsExport";
  public static final String JSNAMESPACE_CLASS = "com.google.gwt.core.client.js.JsNamespace";
  public static final String JSNOEXPORT_CLASS = "com.google.gwt.core.client.js.JsNoExport";
  public static final String JSPROPERTY_CLASS = "com.google.gwt.core.client.js.JsProperty";
  public static final String JSTYPE_CLASS = "com.google.gwt.core.client.js.JsType";
  public static final String JSTYPEPROTOTYPE_CLASS =
      "com.google.gwt.core.client.js.impl.PrototypeOfJsType";

  public static void maybeSetJsinteropMethodProperties(AbstractMethodDeclaration x, JMethod method,
      boolean isClassWideExport, boolean isJsType) {
    maybeSetJsinteropProperties(method, x.annotations, isClassWideExport, isJsType);
    method.setJsProperty(JdtUtil.getAnnotation(x.annotations, JSPROPERTY_CLASS) != null);
  }

  public static void maybeSetExportedField(FieldDeclaration x, JField field,
      boolean isClassWideExport, boolean isJsType) {
    maybeSetJsinteropProperties(field, x.annotations, isClassWideExport, isJsType);
  }

  private static void maybeSetJsinteropProperties(JMember member, Annotation[] annotations,
      boolean isClassWideExport, boolean isJsType) {
    AnnotationBinding jsExport = JdtUtil.getAnnotation(annotations, JSEXPORT_CLASS);
    if (jsExport != null) {
      String value = JdtUtil.getAnnotationParameterString(jsExport, "value");
      setExportInfo(member, value == null ? "" : value);
    }

    /* Apply class wide JsInterop annotations */

    boolean ignore = JdtUtil.getAnnotation(annotations, JSNOEXPORT_CLASS) != null;
    if (ignore || !member.isPublic()) {
      return;
    }

    if (isJsType && member.needsVtable()) {
      member.setJsTypeName("");
    }

    if (isClassWideExport && !member.needsVtable() && jsExport == null) {
      setExportInfo(member, "");
    }
  }

  private static void setExportInfo(JMember memeber, String exportName) {
    if (exportName.isEmpty()) {
      memeber.setExportInfo(null, memeber.getName());
    } else {
      int split = exportName.lastIndexOf('.');
      if (split == -1) {
        memeber.setExportInfo("", exportName);
      } else {
        memeber.setExportInfo(exportName.substring(0, split), exportName.substring(split + 1));
      }
    }
  }

  public static boolean isJsType(TypeDeclaration x) {
    if (x.annotations != null) {
      return JdtUtil.getAnnotation(x.binding, JSTYPE_CLASS) != null;
    }
    return false;
  }

  public static String maybeGetJsTypePrototype(TypeDeclaration x) {
    if (x.annotations != null) {
      AnnotationBinding jsType = JdtUtil.getAnnotation(x.binding, JSTYPE_CLASS);
      if (jsType != null) {
        String value = JdtUtil.getAnnotationParameterString(jsType, "prototype");
        if (value != null) {
          return value;
        }
      }
    }
    return "";
  }

  public static void maybeSetJsPrototypeFlag(TypeDeclaration x, JClassType type) {
    if (JdtUtil.getAnnotation(x.binding, JSTYPEPROTOTYPE_CLASS) != null) {
      type.setJsPrototypeStub(true);
    }
  }

  public static boolean isClassWideJsExport(TypeDeclaration x) {
    if (x.annotations != null) {
      AnnotationBinding jsExport = JdtUtil.getAnnotation(x.binding, JSEXPORT_CLASS);
      if (jsExport != null) {
        return true;
      }
    }
    return false;
  }

  public static String maybeGetJsNamespace(TypeDeclaration x) {
    if (x.annotations != null) {
      AnnotationBinding jsNamespace = JdtUtil.getAnnotation(x.binding, JSNAMESPACE_CLASS);
      return JdtUtil.getAnnotationParameterString(jsNamespace, "value");
    }
    return "";
  }

  public static JsNameRef convertQualifiedPrototypeToNameRef(SourceInfo sourceInfo, String jsPrototype) {
    JsNameRef ref = new JsNameRef(sourceInfo, "$wnd");
    for (String part : jsPrototype.split("\\.")) {
      JsNameRef newRef = new JsNameRef(sourceInfo, part);
      newRef.setQualifier(ref);
      ref = newRef;
    }
    return ref;
  }
}
