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

import com.google.gwt.thirdparty.guava.common.base.Function;
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
    StringBuilder result = new StringBuilder();
    if (name.length > 0) {
      result.append(name[0]);
    }

    for (int i = 1; i < name.length; ++i) {
      result.append('.');
      result.append(name[i]);
    }
    return result.toString();
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
    String methodName = String.valueOf(methodBinding.selector);
    final List<TypeBinding> parameterTypeBindings = Lists.newArrayList();
    final Function<SyntheticArgumentBinding, Void> addSyntheticParameterSignatureFunction =
        new Function<SyntheticArgumentBinding, Void>() {
          @Override
          public Void apply(SyntheticArgumentBinding arg) {
            parameterTypeBindings.add(arg.type);
            return null;
          }
        };

    final boolean isConstructor = methodName.equals("<init>");
    if (isConstructor) {
      // It is a constructor.
      // (1) use the JSNI methodName instead of <init>.
      methodName = "new";
      // (2) add the implicit constructor parameters types for non static inner classes.
      processEnclosingInstanceSyntheticArgumentBindings(declaringClassBinding,
          addSyntheticParameterSignatureFunction);
    }

    parameterTypeBindings.addAll(Arrays.asList(methodBinding.parameters));

    if (isConstructor) {
      processCaptureVariableSyntheticArgumentBindings(declaringClassBinding,
          addSyntheticParameterSignatureFunction);
    }


    StringBuilder methodNameWithSignature = new StringBuilder();
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
      AnnotationBinding a, String paramName) {
    if (a != null) {
      for (ElementValuePair maybeValue : a.getElementValuePairs()) {
        Object value = maybeValue.getValue();
        if (value instanceof Object[] &&
            paramName.equals(String.valueOf(maybeValue.getName()))) {
          Object[] values = (Object[]) value;
          TypeBinding b[] = new TypeBinding[values.length];
          for (int i = 0; i < values.length; i++) {
            b[i] = (TypeBinding) values[i];
          }
          return b;
        }
      }
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

  /**
   * Applies {@code process} to each SyntheticArgumentBinding of a nested class.
   */
  public static void processSyntheticArgumentBindings(ReferenceBinding declaringClass,
      Function<SyntheticArgumentBinding, Void> process) {
    processEnclosingInstanceSyntheticArgumentBindings(declaringClass, process);
    processCaptureVariableSyntheticArgumentBindings(declaringClass, process);
  }

  /**
   * Applies {@code process} to each SyntheticArgumentBinding corresponding to a closure-captured
   * variable of a nested class.
   */
  public static void processCaptureVariableSyntheticArgumentBindings(
      ReferenceBinding declaringClass, Function<SyntheticArgumentBinding, Void> process) {
    if (!isInnerClass(declaringClass)) {
      return;
    }

    NestedTypeBinding nestedBinding = (NestedTypeBinding) declaringClass;
    if (nestedBinding.outerLocalVariables != null) {
      // Add Synthetic arguments for captured locals.
      for (SyntheticArgumentBinding arg : nestedBinding.outerLocalVariables) {
        process.apply(arg);
      }
    }
  }

  /**
   * Applies {@code process} to each SyntheticArgumentBinding corresponding to an enclosing instance
   * of a nested class.
   */
  public static void processEnclosingInstanceSyntheticArgumentBindings(
      ReferenceBinding declaringClass, Function<SyntheticArgumentBinding, Void> process) {
    if (!isInnerClass(declaringClass)) {
      return;
    }

    NestedTypeBinding nestedBinding = (NestedTypeBinding) declaringClass;
    if (nestedBinding.enclosingInstances != null) {
      // Add Synthetic arguments for outer classes.
      for (SyntheticArgumentBinding arg : nestedBinding.enclosingInstances) {
        process.apply(arg);
      }
    }
  }
}
