/*
 * Copyright 2013 Google Inc.
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

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the removal of GwtIncompatible annotated classes and members.
 */
public class GwtIncompatiblePreprocessor {
  /**
   * Checks whether GwtIncompatible is in the array of {@code Annotation}.
   *
   * @param annotations an (possible null) array of {@code Annotation}
   * @return {@code true} if there is an annotation of class {@code *.GwtIncompatible} in
   *         array. {@code false} otherwise.
   */
  private static boolean hasGwtIncompatibleAnnotation(Annotation[] annotations) {
    if (annotations == null) {
      return false;
    }
    for (Annotation ann : annotations) {
      String typeName = new String(ann.type.getLastToken());
      if (typeName.equals("GwtIncompatible")) {
        return true;
      }
    }
    return false;
  }

  /**
   * Modifies the methods array of type {@code tyDecl} to remove any GwtIncompatible methods.
   */
  private static void stripGwtIncompatibleMethods(TypeDeclaration tyDecl) {
    if (tyDecl.methods == null) {
      return;
    }

    List<AbstractMethodDeclaration> newMethods = new ArrayList<AbstractMethodDeclaration>();
    for (AbstractMethodDeclaration methodDecl : tyDecl.methods) {
       if (!hasGwtIncompatibleAnnotation(methodDecl.annotations)) {
        newMethods.add(methodDecl);
       }
    }

    if (newMethods.size() != tyDecl.methods.length) {
      tyDecl.methods = newMethods.toArray(new AbstractMethodDeclaration[newMethods.size()]);
    }
  }

  /**
   * Modifies the fields array of type {@code tyDecl} to remove any GwtIncompatible fields.
   */
  private static void stripGwtIncompatibleFields(TypeDeclaration tyDecl) {
    if (tyDecl.fields == null) {
      return;
    }

    List<FieldDeclaration> newFields = new ArrayList<FieldDeclaration>();
    for (FieldDeclaration fieldDecl : tyDecl.fields) {
      if (!hasGwtIncompatibleAnnotation(fieldDecl.annotations)) {
        newFields.add(fieldDecl);
      }
    }

    if (newFields.size() != tyDecl.fields.length) {
      tyDecl.fields = newFields.toArray(new FieldDeclaration[newFields.size()]);
    }
  }

  /**
   * Removes inner classes, methods and fields that are @GwtIncompatible from an anonymous
   * inner class.
   *
   * @return The set of types with every element that was annotated by {@code GwtIncompatible}
   *         removed.
   */
  static void stripGwtIncompatibleAnonymousInnerClasses(
      CompilationUnitDeclaration cud) {
    ASTVisitor visitor = new ASTVisitor() {
      @Override
      public void endVisit(QualifiedAllocationExpression qualifiedAllocationExpression,
          BlockScope scope) {
        if (qualifiedAllocationExpression.anonymousType != null) {
          stripGwtIncompatible(
              new TypeDeclaration[]{qualifiedAllocationExpression.anonymousType}, false);
        }
      }
    };
    cud.traverse(visitor, cud.scope);
  }

  /**
  * Removes classes, inner classes, methods and fields that are @GwtIncompatible.
  *
  * @return The set of types with every element that was annotated by {@code GwtIncompatible}
  *         removed.
  */
  static TypeDeclaration[] stripGwtIncompatible(TypeDeclaration[] types, boolean leaveEmptyClass) {
    if (types == null) {
      return types;
    }

    List<TypeDeclaration> newTypeDecls = new ArrayList<TypeDeclaration>();
    for (TypeDeclaration tyDecl : types) {
      if (!hasGwtIncompatibleAnnotation(tyDecl.annotations)) {
        newTypeDecls.add(tyDecl);
        tyDecl.memberTypes = stripGwtIncompatible(tyDecl.memberTypes, false);
        stripGwtIncompatibleMethods(tyDecl);
        stripGwtIncompatibleFields(tyDecl);
      } else if (leaveEmptyClass) {
        // Leave the empty class.
        newTypeDecls.add(tyDecl);
        tyDecl.superclass = null;
        tyDecl.superInterfaces = new TypeReference[0];
        tyDecl.annotations = new Annotation[0];
        tyDecl.methods = new AbstractMethodDeclaration[0];
        tyDecl.memberTypes = new TypeDeclaration[0];
        tyDecl.fields = new FieldDeclaration[0];
      }
    }

    if (newTypeDecls.size() != types.length) {
      return newTypeDecls.toArray(new TypeDeclaration[newTypeDecls.size()]);
    } else {
      return types;
    }
  }
}
