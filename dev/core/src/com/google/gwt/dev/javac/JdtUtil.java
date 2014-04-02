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
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
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
    return Joiner.on(".").skipNulls().join(new String[]{
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
            formatMethodSignature(methodBinding));
  }

  private JdtUtil() {
  }
}
