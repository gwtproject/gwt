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

import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.google.gwt.thirdparty.guava.common.base.Strings;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.internal.compiler.impl.StringConstant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Utility functions to interact with JDT classes.
 */
public final class JdtUtil {
  /**
   * Returns a source name from an array of names.
   */
  public static String asDottedString(char[][] name) {
    return join(name, ".");
  }

  /**
   * Returns a string name from an array of names using {@code separator}.
   */
  public static String join(char[][] name, String separator) {
    StringBuilder result = new StringBuilder();
    if (name.length > 0) {
      result.append(name[0]);
    }

    for (int i = 1; i < name.length; ++i) {
      result.append(separator);
      result.append(name[i]);
    }
    return result.toString();
  }

  /**
   * Returns the relative file path for the resource of the compilation unit that defines
   * {@code binding}.
   */
  public static String bindingToResourcePath(ReferenceBinding binding) {
    String packagePathPrefix =
        join(binding.getPackage().compoundName, File.separator);
    String bindingFileName = CharOperation.charToString(binding.getFileName());
    int relativePathPosition = bindingFileName.lastIndexOf(packagePathPrefix);
    if (relativePathPosition == -1) {
      return null;
    }
    return bindingFileName.substring(relativePathPosition);
  }

  /**
   * Returns the top type of the compilation unit that defines
   * {@code binding}.
   */
  public static String getDefiningCompilationUnitType(ReferenceBinding binding) {
    // Get the compilation unit type name.
    // TODO(rluble): check that this is valid for classes declared in the same compilation unit
    // top scope.
    return asDottedString(binding.outermostEnclosingType().compoundName);
  }

  public static String getSourceName(TypeBinding classBinding) {
    return Joiner.on(".").skipNulls().join(new String[] {
        Strings.emptyToNull(CharOperation.charToString(classBinding.qualifiedPackageName())),
        CharOperation.charToString(classBinding.qualifiedSourceName())});
  }

  public static boolean isInnerClass(ReferenceBinding binding) {
    return binding.isNestedType() && !binding.isStatic();
  }

  /**
   * Get a readable method description from {@code methodBinding} conforming with JSNI formatting.
   * <p>
   * See examples:
   * <ul>
   * <li>a constructor of class A with a java.lang.String parameter will be formatted as
   * "new(Ljava/lang/String;).</li>
   * <li>a method with name m with an parameter of class java.lang.Object and return type boolean
   * will be formatted as "m(Ljava/lang/Object;</li>
   * </ul>,
   */
  public static String formatMethodSignature(MethodBinding methodBinding) {
    ReferenceBinding declaringClassBinding = methodBinding.declaringClass;
    StringBuilder methodNameWithSignature = new StringBuilder();
    String methodName = String.valueOf(methodBinding.selector);
    List<TypeBinding> parameterTypeBindings = Lists.newArrayList();
    if (methodName.equals("<init>")) {
      // It is a constructor.
      // (1) use the JSNI methodName instead of <init>.
      methodName = "new";
      // (2) add the implicit constructor parameters types for non static inner classes.
      if (isInnerClass(declaringClassBinding)) {
        NestedTypeBinding nestedBinding = (NestedTypeBinding) declaringClassBinding;
        if (nestedBinding.enclosingInstances != null) {
          for (SyntheticArgumentBinding argumentBinding : nestedBinding.enclosingInstances) {
            parameterTypeBindings.add(argumentBinding.type);
          }
        }
      }
    }

    parameterTypeBindings.addAll(Arrays.asList(methodBinding.parameters));
    methodNameWithSignature.append(methodName);
    methodNameWithSignature.append("(");
    for (TypeBinding parameterTypeBinding : parameterTypeBindings) {
      methodNameWithSignature.append(parameterTypeBinding.signature());
    }
    methodNameWithSignature.append(")");
    return methodNameWithSignature.toString();
  }

  public static String formatBinding(MethodBinding methodBinding) {
    String accessModifier = null;
    if (methodBinding.isProtected()) {
      accessModifier = "protected";
    } else if (methodBinding.isPrivate()) {
      accessModifier = "private";
    } else if (methodBinding.isPublic()) {
      accessModifier = "public";
    }
    return Joiner.on(" ").skipNulls().join(
        accessModifier,
        methodBinding.isStatic() ? "static" : null,
        getSourceName(methodBinding.declaringClass) + "." +
            formatMethodSignature(methodBinding)
    );
  }

  private JdtUtil() {
  }

  public static String getAnnotationParameterString(AnnotationBinding a, String paramName) {
    if (a != null) {
      for (ElementValuePair maybeValue : a.getElementValuePairs()) {
        if (maybeValue.getValue() instanceof StringConstant &&
            paramName.equals(String.valueOf(maybeValue.getName()))) {
          return ((StringConstant) maybeValue.getValue()).stringValue();
        }
      }
    }
    return null;
  }

  public static boolean getAnnotationParameterBoolean(AnnotationBinding a, String paramName) {
    if (a != null) {
      for (ElementValuePair maybeValue : a.getElementValuePairs()) {
        if (maybeValue.getValue() instanceof BooleanConstant &&
            paramName.equals(String.valueOf(maybeValue.getName()))) {
          return ((BooleanConstant) maybeValue.getValue()).booleanValue();
        }
      }
    }
    return false;
  }

  static AnnotationBinding getAnnotation(AnnotationBinding[] annotations, String nameToFind) {
    if (annotations != null) {
      for (AnnotationBinding a : annotations) {
        ReferenceBinding annBinding = a.getAnnotationType();
        String annName = CharOperation.toString(annBinding.compoundName);
        if (nameToFind.equals(annName)) {
          return a;
        }
      }
    }
    return null;
  }

  static AnnotationBinding getAnnotation(Annotation[] annotations, String nameToFind) {
    if (annotations != null) {
      for (Annotation a : annotations) {
        AnnotationBinding annBinding = a.getCompilerAnnotation();
        if (annBinding != null) {
          String annName = CharOperation.toString(annBinding.getAnnotationType().compoundName);
          if (nameToFind.equals(annName)) {
            return annBinding;
          }
        }
      }
    }
    return null;
  }

  public static AnnotationBinding getAnnotation(Binding binding, String nameToFind) {
    if (binding instanceof SourceTypeBinding) {
      ClassScope scope = ((SourceTypeBinding) binding).scope;
      return scope != null ? getAnnotation(scope.referenceType().annotations, nameToFind) : null;
    } else if (binding instanceof ReferenceBinding) {
      return getAnnotation(((ReferenceBinding) binding).getAnnotations(), nameToFind);
    } else if (binding instanceof SyntheticMethodBinding) {
      return null;
    } else if (binding instanceof MethodBinding) {
      AbstractMethodDeclaration abMethod = safeSourceMethod((MethodBinding) binding);
      return abMethod != null ? getAnnotation(abMethod.annotations, nameToFind) : null;
    } else if (binding instanceof FieldBinding) {
      return getAnnotation(((FieldBinding) binding).sourceField().annotations, nameToFind);
    } else {
      return null;
    }
  }

  public static TypeBinding getAnnotationParameterTypeBinding(
      AnnotationBinding a, String paramName) {
    if (a != null) {
      for (ElementValuePair maybeValue : a.getElementValuePairs()) {
        if (maybeValue.getValue() instanceof Class &&
            paramName.equals(String.valueOf(maybeValue.getName()))) {
          return (TypeBinding)  maybeValue.getValue();
        }
      }
    }
    return null;
  }

  public static TypeBinding[] getAnnotationParameterTypeBindingArray(
      AnnotationBinding annotationBinding, String paramName) {
    if (annotationBinding == null) {
      return null;
    }

    for (ElementValuePair maybeValue : annotationBinding.getElementValuePairs()) {
      Object value = maybeValue.getValue();
      if (!paramName.equals(String.valueOf(maybeValue.getName()))) {
        continue;
      }
      if (value instanceof Object[]) {
        Object[] values = (Object[]) value;
        TypeBinding bindings[] = new TypeBinding[values.length];
        System.arraycopy(values, 0, bindings, 0, values.length);
        return bindings;
      }
      assert value instanceof TypeBinding;
      return new TypeBinding[] {(TypeBinding) value};
    }
    return null;
  }

  /**
   * Work around JDT bug.
   */
  public static AbstractMethodDeclaration safeSourceMethod(MethodBinding mb) {
    try {
      return mb.sourceMethod();
    } catch (Exception e) {
      return null;
    }
  }
}
