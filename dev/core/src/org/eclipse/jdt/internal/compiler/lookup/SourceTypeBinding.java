/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann <stephan@cs.tu-berlin.de> - Contributions for
 *								bug 328281 - visibility leaks not detected when analyzing unused field in private class
 *								bug 349326 - [1.7] new warning for missing try-with-resources
 *								bug 186342 - [compiler][null] Using annotations for null checking
 *								bug 365836 - [compiler][null] Incomplete propagation of null defaults.
 *								bug 365519 - editorial cleanup after bug 186342 and bug 365387
 *								bug 365662 - [compiler][null] warn on contradictory and redundant null annotations
 *								bug 365531 - [compiler][null] investigate alternative strategy for internally encoding nullness defaults
 *								bug 366063 - Compiler should not add synthetic @NonNull annotations
 *								bug 384663 - Package Based Annotation Compilation Error in JDT 3.8/4.2 (works in 3.7.2)
 *								bug 386356 - Type mismatch error with annotations and generics
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

import com.google.gwt.dev.util.collect.HashMap;
import java.util.Iterator;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.compiler.util.Util;

public class SourceTypeBinding extends ReferenceBinding {
  public ReferenceBinding superclass;
  public ReferenceBinding[] superInterfaces;
  private FieldBinding[] fields;
  private MethodBinding[] methods;
  public ReferenceBinding[] memberTypes;
  public TypeVariableBinding[] typeVariables;

  public ClassScope scope;

  // Synthetics are separated into 4 categories: methods, super methods, fields, class literals and bridge methods
  // if a new category is added, also increment MAX_SYNTHETICS
  private final static int METHOD_EMUL = 0;
  private final static int FIELD_EMUL = 1;
  private final static int CLASS_LITERAL_EMUL = 2;

  private final static int MAX_SYNTHETICS = 3;

  HashMap[] synthetics;
  char[] genericReferenceTypeSignature;

  private SimpleLookupTable storedAnnotations = null; // keys are this ReferenceBinding & its fields and methods, value is an AnnotationHolder

  private int defaultNullness;
  private int nullnessDefaultInitialized = 0; // 0: nothing; 1: type; 2: package

  public SourceTypeBinding(char[][] compoundName, PackageBinding fPackage, ClassScope scope) {
    this.compoundName = compoundName;
    this.fPackage = fPackage;
    this.fileName = scope.referenceCompilationUnit().getFileName();
    this.modifiers = scope.referenceContext.modifiers;
    this.sourceName = scope.referenceContext.name;
    this.scope = scope;

    // expect the fields & methods to be initialized correctly later
    this.fields = Binding.UNINITIALIZED_FIELDS;
    this.methods = Binding.UNINITIALIZED_METHODS;

    computeId();
  }

  private void addDefaultAbstractMethods() {
    if ((this.tagBits & TagBits.KnowsDefaultAbstractMethods) != 0) return;

    this.tagBits |= TagBits.KnowsDefaultAbstractMethods;
    if (isClass() && isAbstract()) {
      if (this.scope.compilerOptions().targetJDK >= ClassFileConstants.JDK1_2)
        return; // no longer added for post 1.2 targets

      ReferenceBinding[] itsInterfaces = superInterfaces();
      if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
        MethodBinding[] defaultAbstracts = null;
        int defaultAbstractsCount = 0;
        ReferenceBinding[] interfacesToVisit = itsInterfaces;
        int nextPosition = interfacesToVisit.length;
        for (int i = 0; i < nextPosition; i++) {
          ReferenceBinding superType = interfacesToVisit[i];
          if (superType.isValidBinding()) {
            MethodBinding[] superMethods = superType.methods();
            nextAbstractMethod: for (int m = superMethods.length; --m >= 0;) {
              MethodBinding method = superMethods[m];
              // explicitly implemented ?
              if (implementsMethod(method))
                continue nextAbstractMethod;
              if (defaultAbstractsCount == 0) {
                defaultAbstracts = new MethodBinding[5];
              } else {
                // already added as default abstract ?
                for (int k = 0; k < defaultAbstractsCount; k++) {
                  MethodBinding alreadyAdded = defaultAbstracts[k];
                  if (CharOperation.equals(alreadyAdded.selector, method.selector) && alreadyAdded.areParametersEqual(method))
                    continue nextAbstractMethod;
                }
              }
              MethodBinding defaultAbstract = new MethodBinding(
                  method.modifiers | ExtraCompilerModifiers.AccDefaultAbstract | ClassFileConstants.AccSynthetic,
                  method.selector,
                  method.returnType,
                  method.parameters,
                  method.thrownExceptions,
                  this);
              if (defaultAbstractsCount == defaultAbstracts.length)
                System.arraycopy(defaultAbstracts, 0, defaultAbstracts = new MethodBinding[2 * defaultAbstractsCount], 0, defaultAbstractsCount);
              defaultAbstracts[defaultAbstractsCount++] = defaultAbstract;
            }

            if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
              int itsLength = itsInterfaces.length;
              if (nextPosition + itsLength >= interfacesToVisit.length)
                System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
              nextInterface : for (int a = 0; a < itsLength; a++) {
                ReferenceBinding next = itsInterfaces[a];
                for (int b = 0; b < nextPosition; b++)
                  if (next == interfacesToVisit[b]) continue nextInterface;
                interfacesToVisit[nextPosition++] = next;
              }
            }
          }
        }
        if (defaultAbstractsCount > 0) {
          int length = this.methods.length;
          System.arraycopy(this.methods, 0, this.methods = new MethodBinding[length + defaultAbstractsCount], 0, length);
          System.arraycopy(defaultAbstracts, 0, this.methods, length, defaultAbstractsCount);
          // re-sort methods
          length = length + defaultAbstractsCount;
          if (length > 1)
            ReferenceBinding.sortMethods(this.methods, 0, length);
          // this.tagBits |= TagBits.AreMethodsSorted; -- already set in #methods()
        }
      }
    }
  }
  /* Add a new synthetic field for <actualOuterLocalVariable>.
  *	Answer the new field or the existing field if one already existed.
  */
  public FieldBinding addSyntheticFieldForInnerclass(LocalVariableBinding actualOuterLocalVariable) {
    if (this.synthetics == null)
      this.synthetics = new HashMap[MAX_SYNTHETICS];
    if (this.synthetics[SourceTypeBinding.FIELD_EMUL] == null)
      this.synthetics[SourceTypeBinding.FIELD_EMUL] = new HashMap();

    FieldBinding synthField = (FieldBinding) this.synthetics[SourceTypeBinding.FIELD_EMUL].get(actualOuterLocalVariable);
    if (synthField == null) {
      synthField = new SyntheticFieldBinding(
          CharOperation.concat(TypeConstants.SYNTHETIC_OUTER_LOCAL_PREFIX, actualOuterLocalVariable.name),
          actualOuterLocalVariable.type,
          ClassFileConstants.AccPrivate | ClassFileConstants.AccFinal | ClassFileConstants.AccSynthetic,
          this,
          Constant.NotAConstant,
          this.synthetics[SourceTypeBinding.FIELD_EMUL].size());
      this.synthetics[SourceTypeBinding.FIELD_EMUL].put(actualOuterLocalVariable, synthField);
    }

    // ensure there is not already such a field defined by the user
    boolean needRecheck;
    int index = 1;
    do {
      needRecheck = false;
      FieldBinding existingField;
      if ((existingField = getField(synthField.name, true /*resolve*/)) != null) {
        TypeDeclaration typeDecl = this.scope.referenceContext;
        FieldDeclaration[] fieldDeclarations = typeDecl.fields;
        int max = fieldDeclarations == null ? 0 : fieldDeclarations.length;
        for (int i = 0; i < max; i++) {
          FieldDeclaration fieldDecl = fieldDeclarations[i];
          if (fieldDecl.binding == existingField) {
            synthField.name = CharOperation.concat(
                TypeConstants.SYNTHETIC_OUTER_LOCAL_PREFIX,
                actualOuterLocalVariable.name,
                ("$" + String.valueOf(index++)).toCharArray()); //$NON-NLS-1$
            needRecheck = true;
            break;
          }
        }
      }
    } while (needRecheck);
    return synthField;
  }
  /* Add a new synthetic field for <enclosingType>.
  *	Answer the new field or the existing field if one already existed.
  */
  public FieldBinding addSyntheticFieldForInnerclass(ReferenceBinding enclosingType) {
    if (this.synthetics == null)
      this.synthetics = new HashMap[MAX_SYNTHETICS];
    if (this.synthetics[SourceTypeBinding.FIELD_EMUL] == null)
      this.synthetics[SourceTypeBinding.FIELD_EMUL] = new HashMap();

    FieldBinding synthField = (FieldBinding) this.synthetics[SourceTypeBinding.FIELD_EMUL].get(enclosingType);
    if (synthField == null) {
      synthField = new SyntheticFieldBinding(
          CharOperation.concat(
              TypeConstants.SYNTHETIC_ENCLOSING_INSTANCE_PREFIX,
              String.valueOf(enclosingType.depth()).toCharArray()),
          enclosingType,
          ClassFileConstants.AccDefault | ClassFileConstants.AccFinal | ClassFileConstants.AccSynthetic,
          this,
          Constant.NotAConstant,
          this.synthetics[SourceTypeBinding.FIELD_EMUL].size());
      this.synthetics[SourceTypeBinding.FIELD_EMUL].put(enclosingType, synthField);
    }
    // ensure there is not already such a field defined by the user
    boolean needRecheck;
    do {
      needRecheck = false;
      FieldBinding existingField;
      if ((existingField = getField(synthField.name, true /*resolve*/)) != null) {
        TypeDeclaration typeDecl = this.scope.referenceContext;
        FieldDeclaration[] fieldDeclarations = typeDecl.fields;
        int max = fieldDeclarations == null ? 0 : fieldDeclarations.length;
        for (int i = 0; i < max; i++) {
          FieldDeclaration fieldDecl = fieldDeclarations[i];
          if (fieldDecl.binding == existingField) {
            if (this.scope.compilerOptions().complianceLevel >= ClassFileConstants.JDK1_5) {
              synthField.name = CharOperation.concat(
                  synthField.name,
                  "$".toCharArray()); //$NON-NLS-1$
              needRecheck = true;
            } else {
              this.scope.problemReporter().duplicateFieldInType(this, fieldDecl);
            }
            break;
          }
        }
      }
    } while (needRecheck);
    return synthField;
  }
  /* Add a new synthetic field for a class literal access.
  *	Answer the new field or the existing field if one already existed.
  */
  public FieldBinding addSyntheticFieldForClassLiteral(TypeBinding targetType, BlockScope blockScope) {
    if (this.synthetics == null)
      this.synthetics = new HashMap[MAX_SYNTHETICS];
    if (this.synthetics[SourceTypeBinding.CLASS_LITERAL_EMUL] == null)
      this.synthetics[SourceTypeBinding.CLASS_LITERAL_EMUL] = new HashMap();

    // use a different table than FIELDS, given there might be a collision between emulation of X.this$0 and X.class.
    FieldBinding synthField = (FieldBinding) this.synthetics[SourceTypeBinding.CLASS_LITERAL_EMUL].get(targetType);
    if (synthField == null) {
      synthField = new SyntheticFieldBinding(
          CharOperation.concat(
              TypeConstants.SYNTHETIC_CLASS,
              String.valueOf(this.synthetics[SourceTypeBinding.CLASS_LITERAL_EMUL].size()).toCharArray()),
          blockScope.getJavaLangClass(),
          ClassFileConstants.AccDefault | ClassFileConstants.AccStatic | ClassFileConstants.AccSynthetic,
          this,
          Constant.NotAConstant,
          this.synthetics[SourceTypeBinding.CLASS_LITERAL_EMUL].size());
      this.synthetics[SourceTypeBinding.CLASS_LITERAL_EMUL].put(targetType, synthField);
    }
    // ensure there is not already such a field defined by the user
    FieldBinding existingField;
    if ((existingField = getField(synthField.name, true /*resolve*/)) != null) {
      TypeDeclaration typeDecl = blockScope.referenceType();
      FieldDeclaration[] typeDeclarationFields = typeDecl.fields;
      int max = typeDeclarationFields == null ? 0 : typeDeclarationFields.length;
      for (int i = 0; i < max; i++) {
        FieldDeclaration fieldDecl = typeDeclarationFields[i];
        if (fieldDecl.binding == existingField) {
          blockScope.problemReporter().duplicateFieldInType(this, fieldDecl);
          break;
        }
      }
    }
    return synthField;
  }
  /* Add a new synthetic field for the emulation of the assert statement.
  *	Answer the new field or the existing field if one already existed.
  */
  public FieldBinding addSyntheticFieldForAssert(BlockScope blockScope) {
    if (this.synthetics == null)
      this.synthetics = new HashMap[MAX_SYNTHETICS];
    if (this.synthetics[SourceTypeBinding.FIELD_EMUL] == null)
      this.synthetics[SourceTypeBinding.FIELD_EMUL] = new HashMap();

    FieldBinding synthField = (FieldBinding) this.synthetics[SourceTypeBinding.FIELD_EMUL].get("assertionEmulation"); //$NON-NLS-1$
    if (synthField == null) {
      synthField = new SyntheticFieldBinding(
          TypeConstants.SYNTHETIC_ASSERT_DISABLED,
          TypeBinding.BOOLEAN,
          ClassFileConstants.AccDefault | ClassFileConstants.AccStatic | ClassFileConstants.AccSynthetic | ClassFileConstants.AccFinal,
          this,
          Constant.NotAConstant,
          this.synthetics[SourceTypeBinding.FIELD_EMUL].size());
      this.synthetics[SourceTypeBinding.FIELD_EMUL].put("assertionEmulation", synthField); //$NON-NLS-1$
    }
    // ensure there is not already such a field defined by the user
    // ensure there is not already such a field defined by the user
    boolean needRecheck;
    int index = 0;
    do {
      needRecheck = false;
      FieldBinding existingField;
      if ((existingField = getField(synthField.name, true /*resolve*/)) != null) {
        TypeDeclaration typeDecl = this.scope.referenceContext;
        int max = (typeDecl.fields == null) ? 0 : typeDecl.fields.length;
        for (int i = 0; i < max; i++) {
          FieldDeclaration fieldDecl = typeDecl.fields[i];
          if (fieldDecl.binding == existingField) {
            synthField.name = CharOperation.concat(
                TypeConstants.SYNTHETIC_ASSERT_DISABLED,
                ("_" + String.valueOf(index++)).toCharArray()); //$NON-NLS-1$
            needRecheck = true;
            break;
          }
        }
      }
    } while (needRecheck);
    return synthField;
  }
  /* Add a new synthetic field for recording all enum constant values
  *	Answer the new field or the existing field if one already existed.
  */
  public FieldBinding addSyntheticFieldForEnumValues() {
    if (this.synthetics == null)
      this.synthetics = new HashMap[MAX_SYNTHETICS];
    if (this.synthetics[SourceTypeBinding.FIELD_EMUL] == null)
      this.synthetics[SourceTypeBinding.FIELD_EMUL] = new HashMap();

    FieldBinding synthField = (FieldBinding) this.synthetics[SourceTypeBinding.FIELD_EMUL].get("enumConstantValues"); //$NON-NLS-1$
    if (synthField == null) {
      synthField = new SyntheticFieldBinding(
          TypeConstants.SYNTHETIC_ENUM_VALUES,
          this.scope.createArrayType(this,1),
          ClassFileConstants.AccPrivate | ClassFileConstants.AccStatic | ClassFileConstants.AccSynthetic | ClassFileConstants.AccFinal,
          this,
          Constant.NotAConstant,
          this.synthetics[SourceTypeBinding.FIELD_EMUL].size());
      this.synthetics[SourceTypeBinding.FIELD_EMUL].put("enumConstantValues", synthField); //$NON-NLS-1$
    }
    // ensure there is not already such a field defined by the user
    // ensure there is not already such a field defined by the user
    boolean needRecheck;
    int index = 0;
    do {
      needRecheck = false;
      FieldBinding existingField;
      if ((existingField = getField(synthField.name, true /*resolve*/)) != null) {
        TypeDeclaration typeDecl = this.scope.referenceContext;
        FieldDeclaration[] fieldDeclarations = typeDecl.fields;
        int max = fieldDeclarations == null ? 0 : fieldDeclarations.length;
        for (int i = 0; i < max; i++) {
          FieldDeclaration fieldDecl = fieldDeclarations[i];
          if (fieldDecl.binding == existingField) {
            synthField.name = CharOperation.concat(
                TypeConstants.SYNTHETIC_ENUM_VALUES,
                ("_" + String.valueOf(index++)).toCharArray()); //$NON-NLS-1$
            needRecheck = true;
            break;
          }
        }
      }
    } while (needRecheck);
    return synthField;
  }
  /* Add a new synthetic access method for read/write access to <targetField>.
    Answer the new method or the existing method if one already existed.
  */
  public SyntheticMethodBinding addSyntheticMethod(FieldBinding targetField, boolean isReadAccess, boolean isSuperAccess) {
    if (this.synthetics == null)
      this.synthetics = new HashMap[MAX_SYNTHETICS];
    if (this.synthetics[SourceTypeBinding.METHOD_EMUL] == null)
      this.synthetics[SourceTypeBinding.METHOD_EMUL] = new HashMap();

    SyntheticMethodBinding accessMethod = null;
    SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[]) this.synthetics[SourceTypeBinding.METHOD_EMUL].get(targetField);
    if (accessors == null) {
      accessMethod = new SyntheticMethodBinding(targetField, isReadAccess, isSuperAccess, this);
      this.synthetics[SourceTypeBinding.METHOD_EMUL].put(targetField, accessors = new SyntheticMethodBinding[2]);
      accessors[isReadAccess ? 0 : 1] = accessMethod;
    } else {
      if ((accessMethod = accessors[isReadAccess ? 0 : 1]) == null) {
        accessMethod = new SyntheticMethodBinding(targetField, isReadAccess, isSuperAccess, this);
        accessors[isReadAccess ? 0 : 1] = accessMethod;
      }
    }
    return accessMethod;
  }
  /* Add a new synthetic method the enum type. Selector can either be 'values' or 'valueOf'.
   * char[] constants from TypeConstants must be used: TypeConstants.VALUES/VALUEOF
  */
  public SyntheticMethodBinding addSyntheticEnumMethod(char[] selector) {
    if (this.synthetics == null)
      this.synthetics = new HashMap[MAX_SYNTHETICS];
    if (this.synthetics[SourceTypeBinding.METHOD_EMUL] == null)
      this.synthetics[SourceTypeBinding.METHOD_EMUL] = new HashMap();

    SyntheticMethodBinding accessMethod = null;
    SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[]) this.synthetics[SourceTypeBinding.METHOD_EMUL].get(selector);
    if (accessors == null) {
      accessMethod = new SyntheticMethodBinding(this, selector);
      this.synthetics[SourceTypeBinding.METHOD_EMUL].put(selector, accessors = new SyntheticMethodBinding[2]);
      accessors[0] = accessMethod;
    } else {
      if ((accessMethod = accessors[0]) == null) {
        accessMethod = new SyntheticMethodBinding(this, selector);
        accessors[0] = accessMethod;
      }
    }
    return accessMethod;
  }
  /*
  * Add a synthetic field to handle the cache of the switch translation table for the corresponding enum type
  */
  public SyntheticFieldBinding addSyntheticFieldForSwitchEnum(char[] fieldName, String key) {
    if (this.synthetics == null)
      this.synthetics = new HashMap[MAX_SYNTHETICS];
    if (this.synthetics[SourceTypeBinding.FIELD_EMUL] == null)
      this.synthetics[SourceTypeBinding.FIELD_EMUL] = new HashMap();

    SyntheticFieldBinding synthField = (SyntheticFieldBinding) this.synthetics[SourceTypeBinding.FIELD_EMUL].get(key);
    if (synthField == null) {
      synthField = new SyntheticFieldBinding(
          fieldName,
          this.scope.createArrayType(TypeBinding.INT,1),
          ClassFileConstants.AccPrivate | ClassFileConstants.AccStatic | ClassFileConstants.AccSynthetic,
          this,
          Constant.NotAConstant,
          this.synthetics[SourceTypeBinding.FIELD_EMUL].size());
      this.synthetics[SourceTypeBinding.FIELD_EMUL].put(key, synthField);
    }
    // ensure there is not already such a field defined by the user
    boolean needRecheck;
    int index = 0;
    do {
      needRecheck = false;
      FieldBinding existingField;
      if ((existingField = getField(synthField.name, true /*resolve*/)) != null) {
        TypeDeclaration typeDecl = this.scope.referenceContext;
        FieldDeclaration[] fieldDeclarations = typeDecl.fields;
        int max = fieldDeclarations == null ? 0 : fieldDeclarations.length;
        for (int i = 0; i < max; i++) {
          FieldDeclaration fieldDecl = fieldDeclarations[i];
          if (fieldDecl.binding == existingField) {
            synthField.name = CharOperation.concat(
                fieldName,
                ("_" + String.valueOf(index++)).toCharArray()); //$NON-NLS-1$
            needRecheck = true;
            break;
          }
        }
      }
    } while (needRecheck);
    return synthField;
  }
  /* Add a new synthetic method the enum type. Selector can either be 'values' or 'valueOf'.
   * char[] constants from TypeConstants must be used: TypeConstants.VALUES/VALUEOF
  */
  public SyntheticMethodBinding addSyntheticMethodForSwitchEnum(TypeBinding enumBinding) {
    if (this.synthetics == null)
      this.synthetics = new HashMap[MAX_SYNTHETICS];
    if (this.synthetics[SourceTypeBinding.METHOD_EMUL] == null)
      this.synthetics[SourceTypeBinding.METHOD_EMUL] = new HashMap();

    SyntheticMethodBinding accessMethod = null;
    char[] selector = CharOperation.concat(TypeConstants.SYNTHETIC_SWITCH_ENUM_TABLE, enumBinding.constantPoolName());
    CharOperation.replace(selector, '/', '$');
    final String key = new String(selector);
    SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[]) this.synthetics[SourceTypeBinding.METHOD_EMUL].get(key);
    // first add the corresponding synthetic field
    if (accessors == null) {
      // then create the synthetic method
      final SyntheticFieldBinding fieldBinding = addSyntheticFieldForSwitchEnum(selector, key);
      accessMethod = new SyntheticMethodBinding(fieldBinding, this, enumBinding, selector);
      this.synthetics[SourceTypeBinding.METHOD_EMUL].put(key, accessors = new SyntheticMethodBinding[2]);
      accessors[0] = accessMethod;
    } else {
      if ((accessMethod = accessors[0]) == null) {
        final SyntheticFieldBinding fieldBinding = addSyntheticFieldForSwitchEnum(selector, key);
        accessMethod = new SyntheticMethodBinding(fieldBinding, this, enumBinding, selector);
        accessors[0] = accessMethod;
      }
    }
    return accessMethod;
  }
  public SyntheticMethodBinding addSyntheticMethodForEnumInitialization(int begin, int end) {
    if (this.synthetics == null)
      this.synthetics = new HashMap[MAX_SYNTHETICS];
    if (this.synthetics[SourceTypeBinding.METHOD_EMUL] == null)
      this.synthetics[SourceTypeBinding.METHOD_EMUL] = new HashMap();

    SyntheticMethodBinding accessMethod = new SyntheticMethodBinding(this, begin, end);
    SyntheticMethodBinding[] accessors = new SyntheticMethodBinding[2];
    this.synthetics[SourceTypeBinding.METHOD_EMUL].put(accessMethod.selector, accessors);
    accessors[0] = accessMethod;
    return accessMethod;
  }
  /* Add a new synthetic access method for access to <targetMethod>.
   * Must distinguish access method used for super access from others (need to use invokespecial bytecode)
    Answer the new method or the existing method if one already existed.
  */
  public SyntheticMethodBinding addSyntheticMethod(MethodBinding targetMethod, boolean isSuperAccess) {
    if (this.synthetics == null)
      this.synthetics = new HashMap[MAX_SYNTHETICS];
    if (this.synthetics[SourceTypeBinding.METHOD_EMUL] == null)
      this.synthetics[SourceTypeBinding.METHOD_EMUL] = new HashMap();

    SyntheticMethodBinding accessMethod = null;
    SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[]) this.synthetics[SourceTypeBinding.METHOD_EMUL].get(targetMethod);
    if (accessors == null) {
      accessMethod = new SyntheticMethodBinding(targetMethod, isSuperAccess, this);
      this.synthetics[SourceTypeBinding.METHOD_EMUL].put(targetMethod, accessors = new SyntheticMethodBinding[2]);
      accessors[isSuperAccess ? 0 : 1] = accessMethod;
    } else {
      if ((accessMethod = accessors[isSuperAccess ? 0 : 1]) == null) {
        accessMethod = new SyntheticMethodBinding(targetMethod, isSuperAccess, this);
        accessors[isSuperAccess ? 0 : 1] = accessMethod;
      }
    }
    if (targetMethod.declaringClass.isStatic()) {
      if ((targetMethod.isConstructor() && targetMethod.parameters.length >= 0xFE)
          || targetMethod.parameters.length >= 0xFF) {
        this.scope.problemReporter().tooManyParametersForSyntheticMethod(targetMethod.sourceMethod());
      }
    } else if ((targetMethod.isConstructor() && targetMethod.parameters.length >= 0xFD)
        || targetMethod.parameters.length >= 0xFE) {
      this.scope.problemReporter().tooManyParametersForSyntheticMethod(targetMethod.sourceMethod());
    }
    return accessMethod;
  }
  /*
  * Record the fact that bridge methods need to be generated to override certain inherited methods
  */
  public SyntheticMethodBinding addSyntheticBridgeMethod(MethodBinding inheritedMethodToBridge, MethodBinding targetMethod) {
    if (isInterface()) return null; // only classes & enums get bridge methods
    // targetMethod may be inherited
    if (inheritedMethodToBridge.returnType.erasure() == targetMethod.returnType.erasure()
        && inheritedMethodToBridge.areParameterErasuresEqual(targetMethod)) {
      return null; // do not need bridge method
    }
    if (this.synthetics == null)
      this.synthetics = new HashMap[MAX_SYNTHETICS];
    if (this.synthetics[SourceTypeBinding.METHOD_EMUL] == null) {
      this.synthetics[SourceTypeBinding.METHOD_EMUL] = new HashMap();
    } else {
      // check to see if there is another equivalent inheritedMethod already added
      Iterator synthMethods = this.synthetics[SourceTypeBinding.METHOD_EMUL].keySet().iterator();
      while (synthMethods.hasNext()) {
        Object synthetic = synthMethods.next();
        if (synthetic instanceof MethodBinding) {
          MethodBinding method = (MethodBinding) synthetic;
          if (CharOperation.equals(inheritedMethodToBridge.selector, method.selector)
              && inheritedMethodToBridge.returnType.erasure() == method.returnType.erasure()
              && inheritedMethodToBridge.areParameterErasuresEqual(method)) {
            return null;
          }
        }
      }
    }

    SyntheticMethodBinding accessMethod = null;
    SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[]) this.synthetics[SourceTypeBinding.METHOD_EMUL].get(inheritedMethodToBridge);
    if (accessors == null) {
      accessMethod = new SyntheticMethodBinding(inheritedMethodToBridge, targetMethod, this);
      this.synthetics[SourceTypeBinding.METHOD_EMUL].put(inheritedMethodToBridge, accessors = new SyntheticMethodBinding[2]);
      accessors[1] = accessMethod;
    } else {
      if ((accessMethod = accessors[1]) == null) {
        accessMethod = new SyntheticMethodBinding(inheritedMethodToBridge, targetMethod, this);
        accessors[1] = accessMethod;
      }
    }
    return accessMethod;
  }
  /*
  * https://bugs.eclipse.org/bugs/show_bug.cgi?id=288658. Generate a bridge method if a public method is inherited
  * from a non-public class into a public class (only in 1.6 or greater)
  */
  public SyntheticMethodBinding addSyntheticBridgeMethod(MethodBinding inheritedMethodToBridge) {
    if (this.scope.compilerOptions().complianceLevel <= ClassFileConstants.JDK1_5) {
      return null;
    }
    if (isInterface()) return null;
    if (inheritedMethodToBridge.isAbstract() || inheritedMethodToBridge.isFinal() || inheritedMethodToBridge.isStatic()) {
      return null;
    }
    if (this.synthetics == null)
      this.synthetics = new HashMap[MAX_SYNTHETICS];
    if (this.synthetics[SourceTypeBinding.METHOD_EMUL] == null) {
      this.synthetics[SourceTypeBinding.METHOD_EMUL] = new HashMap();
    } else {
      // check to see if there is another equivalent inheritedMethod already added
      Iterator synthMethods = this.synthetics[SourceTypeBinding.METHOD_EMUL].keySet().iterator();
      while (synthMethods.hasNext()) {
        Object synthetic = synthMethods.next();
        if (synthetic instanceof MethodBinding) {
          MethodBinding method = (MethodBinding) synthetic;
          if (CharOperation.equals(inheritedMethodToBridge.selector, method.selector)
              && inheritedMethodToBridge.returnType.erasure() == method.returnType.erasure()
              && inheritedMethodToBridge.areParameterErasuresEqual(method)) {
            return null;
          }
        }
      }
    }

    SyntheticMethodBinding accessMethod = null;
    SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[]) this.synthetics[SourceTypeBinding.METHOD_EMUL].get(inheritedMethodToBridge);
    if (accessors == null) {
      accessMethod = new SyntheticMethodBinding(inheritedMethodToBridge, this);
      this.synthetics[SourceTypeBinding.METHOD_EMUL].put(inheritedMethodToBridge, accessors = new SyntheticMethodBinding[2]);
      accessors[0] = accessMethod;
    } else {
      if ((accessMethod = accessors[0]) == null) {
        accessMethod = new SyntheticMethodBinding(inheritedMethodToBridge, this);
        accessors[0] = accessMethod;
      }
    }
    return accessMethod;
  }
  boolean areFieldsInitialized() {
    return this.fields != Binding.UNINITIALIZED_FIELDS;
  }
  boolean areMethodsInitialized() {
    return this.methods != Binding.UNINITIALIZED_METHODS;
  }
  public int kind() {
    if (this.typeVariables != Binding.NO_TYPE_VARIABLES) return Binding.GENERIC_TYPE;
    return Binding.TYPE;
  }

  public char[] computeUniqueKey(boolean isLeaf) {
    char[] uniqueKey = super.computeUniqueKey(isLeaf);
    if (uniqueKey.length == 2) return uniqueKey; // problem type's unique key is "L;"
    if (Util.isClassFileName(this.fileName)) return uniqueKey; // no need to insert compilation unit name for a .class file

    // insert compilation unit name if the type name is not the main type name
    int end = CharOperation.lastIndexOf('.', this.fileName);
    if (end != -1) {
      int start = CharOperation.lastIndexOf('/', this.fileName) + 1;
      char[] mainTypeName = CharOperation.subarray(this.fileName, start, end);
      start = CharOperation.lastIndexOf('/', uniqueKey) + 1;
      if (start == 0)
        start = 1; // start after L
      if (this.isMemberType()) {
        end = CharOperation.indexOf('$', uniqueKey, start);
      } else {
        // '$' is part of the type name
        end = -1;
      }
      if (end == -1)
        end = CharOperation.indexOf('<', uniqueKey, start);
      if (end == -1)
        end = CharOperation.indexOf(';', uniqueKey, start);
      char[] topLevelType = CharOperation.subarray(uniqueKey, start, end);
      if (!CharOperation.equals(topLevelType, mainTypeName)) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(uniqueKey, 0, start);
        buffer.append(mainTypeName);
        buffer.append('~');
        buffer.append(topLevelType);
        buffer.append(uniqueKey, end, uniqueKey.length - end);
        int length = buffer.length();
        uniqueKey = new char[length];
        buffer.getChars(0, length, uniqueKey, 0);
        return uniqueKey;
      }
    }
    return uniqueKey;
  }

  void faultInTypesForFieldsAndMethods() {
    // check @Deprecated annotation
    getAnnotationTagBits(); // marks as deprecated by side effect
    ReferenceBinding enclosingType = enclosingType();
    if (enclosingType != null && enclosingType.isViewedAsDeprecated() && !isDeprecated())
      this.modifiers |= ExtraCompilerModifiers.AccDeprecatedImplicitly;
    fields();
    methods();

    for (int i = 0, length = this.memberTypes.length; i < length; i++)
      ((SourceTypeBinding) this.memberTypes[i]).faultInTypesForFieldsAndMethods();
  }
  // NOTE: the type of each field of a source type is resolved when needed
  public FieldBinding[] fields() {
    if ((this.tagBits & TagBits.AreFieldsComplete) != 0)
      return this.fields;

    int failed = 0;
    FieldBinding[] resolvedFields = this.fields;
    try {
      // lazily sort fields
      if ((this.tagBits & TagBits.AreFieldsSorted) == 0) {
        int length = this.fields.length;
        if (length > 1)
          ReferenceBinding.sortFields(this.fields, 0, length);
        this.tagBits |= TagBits.AreFieldsSorted;
      }
      for (int i = 0, length = this.fields.length; i < length; i++) {
        if (resolveTypeFor(this.fields[i]) == null) {
          // do not alter original field array until resolution is over, due to reentrance (143259)
          if (resolvedFields == this.fields) {
            System.arraycopy(this.fields, 0, resolvedFields = new FieldBinding[length], 0, length);
          }
          resolvedFields[i] = null;
          failed++;
        }
      }
    } finally {
      if (failed > 0) {
        // ensure fields are consistent reqardless of the error
        int newSize = resolvedFields.length - failed;
        if (newSize == 0)
          return this.fields = Binding.NO_FIELDS;

        FieldBinding[] newFields = new FieldBinding[newSize];
        for (int i = 0, j = 0, length = resolvedFields.length; i < length; i++) {
          if (resolvedFields[i] != null)
            newFields[j++] = resolvedFields[i];
        }
        this.fields = newFields;
      }
    }
    this.tagBits |= TagBits.AreFieldsComplete;
    return this.fields;
  }
  /**
   * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#genericTypeSignature()
   */
  public char[] genericTypeSignature() {
    if (this.genericReferenceTypeSignature == null)
      this.genericReferenceTypeSignature = computeGenericTypeSignature(this.typeVariables);
    return this.genericReferenceTypeSignature;
  }
  /**
   * <param1 ... paramN>superclass superinterface1 ... superinterfaceN
   * <T:LY<TT;>;U:Ljava/lang/Object;V::Ljava/lang/Runnable;:Ljava/lang/Cloneable;:Ljava/util/Map;>Ljava/lang/Exception;Ljava/lang/Runnable;
   */
  public char[] genericSignature() {
    StringBuffer sig = null;
    if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
      sig = new StringBuffer(10);
      sig.append('<');
      for (int i = 0, length = this.typeVariables.length; i < length; i++)
        sig.append(this.typeVariables[i].genericSignature());
      sig.append('>');
    } else {
      // could still need a signature if any of supertypes is parameterized
      noSignature: if (this.superclass == null || !this.superclass.isParameterizedType()) {
        for (int i = 0, length = this.superInterfaces.length; i < length; i++)
          if (this.superInterfaces[i].isParameterizedType())
            break noSignature;
        return null;
      }
      sig = new StringBuffer(10);
    }
    if (this.superclass != null)
      sig.append(this.superclass.genericTypeSignature());
    else // interface scenario only (as Object cannot be generic) - 65953
      sig.append(this.scope.getJavaLangObject().genericTypeSignature());
    for (int i = 0, length = this.superInterfaces.length; i < length; i++)
      sig.append(this.superInterfaces[i].genericTypeSignature());
    return sig.toString().toCharArray();
  }

  /**
   * Compute the tagbits for standard annotations. For source types, these could require
   * lazily resolving corresponding annotation nodes, in case of forward references.
   * @see org.eclipse.jdt.internal.compiler.lookup.Binding#getAnnotationTagBits()
   */
  public long getAnnotationTagBits() {
    if ((this.tagBits & TagBits.AnnotationResolved) == 0 && this.scope != null) {
      TypeDeclaration typeDecl = this.scope.referenceContext;
      boolean old = typeDecl.staticInitializerScope.insideTypeAnnotation;
      try {
        typeDecl.staticInitializerScope.insideTypeAnnotation = true;
        ASTNode.resolveAnnotations(typeDecl.staticInitializerScope, typeDecl.annotations, this);
      } finally {
        typeDecl.staticInitializerScope.insideTypeAnnotation = old;
      }
      if ((this.tagBits & TagBits.AnnotationDeprecated) != 0)
        this.modifiers |= ClassFileConstants.AccDeprecated;
      evaluateNullAnnotations(this.tagBits);
    }
    return this.tagBits;
  }
  public MethodBinding[] getDefaultAbstractMethods() {
    int count = 0;
    for (int i = this.methods.length; --i >= 0;)
      if (this.methods[i].isDefaultAbstract())
        count++;
    if (count == 0) return Binding.NO_METHODS;

    MethodBinding[] result = new MethodBinding[count];
    count = 0;
    for (int i = this.methods.length; --i >= 0;)
      if (this.methods[i].isDefaultAbstract())
        result[count++] = this.methods[i];
    return result;
  }
  // NOTE: the return type, arg & exception types of each method of a source type are resolved when needed
  public MethodBinding getExactConstructor(TypeBinding[] argumentTypes) {
    int argCount = argumentTypes.length;
    if ((this.tagBits & TagBits.AreMethodsComplete) != 0) { // have resolved all arg types & return type of the methods
      long range;
      if ((range = ReferenceBinding.binarySearch(TypeConstants.INIT, this.methods)) >= 0) {
        nextMethod: for (int imethod = (int)range, end = (int)(range >> 32); imethod <= end; imethod++) {
          MethodBinding method = this.methods[imethod];
          if (method.parameters.length == argCount) {
            TypeBinding[] toMatch = method.parameters;
            for (int iarg = 0; iarg < argCount; iarg++)
              if (toMatch[iarg] != argumentTypes[iarg])
                continue nextMethod;
            return method;
          }
        }
      }
    } else {
      // lazily sort methods
      if ((this.tagBits & TagBits.AreMethodsSorted) == 0) {
        int length = this.methods.length;
        if (length > 1)
          ReferenceBinding.sortMethods(this.methods, 0, length);
        this.tagBits |= TagBits.AreMethodsSorted;
      }
      long range;
      if ((range = ReferenceBinding.binarySearch(TypeConstants.INIT, this.methods)) >= 0) {
        nextMethod: for (int imethod = (int)range, end = (int)(range >> 32); imethod <= end; imethod++) {
          MethodBinding method = this.methods[imethod];
          if (resolveTypesFor(method) == null || method.returnType == null) {
            methods();
            return getExactConstructor(argumentTypes);  // try again since the problem methods have been removed
          }
          if (method.parameters.length == argCount) {
            TypeBinding[] toMatch = method.parameters;
            for (int iarg = 0; iarg < argCount; iarg++)
              if (toMatch[iarg] != argumentTypes[iarg])
                continue nextMethod;
            return method;
          }
        }
      }
    }
    return null;
  }

  //NOTE: the return type, arg & exception types of each method of a source type are resolved when needed
//searches up the hierarchy as long as no potential (but not exact) match was found.
  public MethodBinding getExactMethod(char[] selector, TypeBinding[] argumentTypes, CompilationUnitScope refScope) {
    // sender from refScope calls recordTypeReference(this)
    int argCount = argumentTypes.length;
    boolean foundNothing = true;

    if ((this.tagBits & TagBits.AreMethodsComplete) != 0) { // have resolved all arg types & return type of the methods
      long range;
      if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0) {
        nextMethod: for (int imethod = (int)range, end = (int)(range >> 32); imethod <= end; imethod++) {
          MethodBinding method = this.methods[imethod];
          foundNothing = false; // inner type lookups must know that a method with this name exists
          if (method.parameters.length == argCount) {
            TypeBinding[] toMatch = method.parameters;
            for (int iarg = 0; iarg < argCount; iarg++)
              if (toMatch[iarg] != argumentTypes[iarg])
                continue nextMethod;
            return method;
          }
        }
      }
    } else {
      // lazily sort methods
      if ((this.tagBits & TagBits.AreMethodsSorted) == 0) {
        int length = this.methods.length;
        if (length > 1)
          ReferenceBinding.sortMethods(this.methods, 0, length);
        this.tagBits |= TagBits.AreMethodsSorted;
      }

      long range;
      if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0) {
        // check unresolved method
        int start = (int) range, end = (int) (range >> 32);
        for (int imethod = start; imethod <= end; imethod++) {
          MethodBinding method = this.methods[imethod];
          if (resolveTypesFor(method) == null || method.returnType == null) {
            methods();
            return getExactMethod(selector, argumentTypes, refScope); // try again since the problem methods have been removed
          }
        }
        // check dup collisions
        boolean isSource15 = this.scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
        for (int i = start; i <= end; i++) {
          MethodBinding method1 = this.methods[i];
          for (int j = end; j > i; j--) {
            MethodBinding method2 = this.methods[j];
            boolean paramsMatch = isSource15
                ? method1.areParameterErasuresEqual(method2)
                : method1.areParametersEqual(method2);
            if (paramsMatch) {
              methods();
              return getExactMethod(selector, argumentTypes, refScope); // try again since the problem methods have been removed
            }
          }
        }
        nextMethod: for (int imethod = start; imethod <= end; imethod++) {
          MethodBinding method = this.methods[imethod];
          TypeBinding[] toMatch = method.parameters;
          if (toMatch.length == argCount) {
            for (int iarg = 0; iarg < argCount; iarg++)
              if (toMatch[iarg] != argumentTypes[iarg])
                continue nextMethod;
            return method;
          }
        }
      }
    }

    if (foundNothing) {
      if (isInterface()) {
        if (this.superInterfaces.length == 1) {
          if (refScope != null)
            refScope.recordTypeReference(this.superInterfaces[0]);
          return this.superInterfaces[0].getExactMethod(selector, argumentTypes, refScope);
        }
      } else if (this.superclass != null) {
        if (refScope != null)
          refScope.recordTypeReference(this.superclass);
        return this.superclass.getExactMethod(selector, argumentTypes, refScope);
      }
    }
    return null;
  }

  //NOTE: the type of a field of a source type is resolved when needed
  public FieldBinding getField(char[] fieldName, boolean needResolve) {

    if ((this.tagBits & TagBits.AreFieldsComplete) != 0)
      return ReferenceBinding.binarySearch(fieldName, this.fields);

    // lazily sort fields
    if ((this.tagBits & TagBits.AreFieldsSorted) == 0) {
      int length = this.fields.length;
      if (length > 1)
        ReferenceBinding.sortFields(this.fields, 0, length);
      this.tagBits |= TagBits.AreFieldsSorted;
    }
    // always resolve anyway on source types
    FieldBinding field = ReferenceBinding.binarySearch(fieldName, this.fields);
    if (field != null) {
      FieldBinding result = null;
      try {
        result = resolveTypeFor(field);
        return result;
      } finally {
        if (result == null) {
          // ensure fields are consistent reqardless of the error
          int newSize = this.fields.length - 1;
          if (newSize == 0) {
            this.fields = Binding.NO_FIELDS;
          } else {
            FieldBinding[] newFields = new FieldBinding[newSize];
            int index = 0;
            for (int i = 0, length = this.fields.length; i < length; i++) {
              FieldBinding f = this.fields[i];
              if (f == field) continue;
              newFields[index++] = f;
            }
            this.fields = newFields;
          }
        }
      }
    }
    return null;
  }

  // NOTE: the return type, arg & exception types of each method of a source type are resolved when needed
  public MethodBinding[] getMethods(char[] selector) {
    if ((this.tagBits & TagBits.AreMethodsComplete) != 0) {
      long range;
      if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0) {
        int start = (int) range, end = (int) (range >> 32);
        int length = end - start + 1;
        MethodBinding[] result;
        System.arraycopy(this.methods, start, result = new MethodBinding[length], 0, length);
        return result;
      } else {
        return Binding.NO_METHODS;
      }
    }
    // lazily sort methods
    if ((this.tagBits & TagBits.AreMethodsSorted) == 0) {
      int length = this.methods.length;
      if (length > 1)
        ReferenceBinding.sortMethods(this.methods, 0, length);
      this.tagBits |= TagBits.AreMethodsSorted;
    }
    MethodBinding[] result;
    long range;
    if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0) {
      int start = (int) range, end = (int) (range >> 32);
      for (int i = start; i <= end; i++) {
        MethodBinding method = this.methods[i];
        if (resolveTypesFor(method) == null || method.returnType == null) {
          methods();
          return getMethods(selector); // try again since the problem methods have been removed
        }
      }
      int length = end - start + 1;
      System.arraycopy(this.methods, start, result = new MethodBinding[length], 0, length);
    } else {
      return Binding.NO_METHODS;
    }
    boolean isSource15 = this.scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
    for (int i = 0, length = result.length - 1; i < length; i++) {
      MethodBinding method = result[i];
      for (int j = length; j > i; j--) {
        boolean paramsMatch = isSource15
            ? method.areParameterErasuresEqual(result[j])
            : method.areParametersEqual(result[j]);
        if (paramsMatch) {
          methods();
          return getMethods(selector); // try again since the duplicate methods have been removed
        }
      }
    }
    return result;
  }
  /* Answer the synthetic field for <actualOuterLocalVariable>
  *	or null if one does not exist.
  */
  public FieldBinding getSyntheticField(LocalVariableBinding actualOuterLocalVariable) {
    if (this.synthetics == null || this.synthetics[SourceTypeBinding.FIELD_EMUL] == null) return null;
    return (FieldBinding) this.synthetics[SourceTypeBinding.FIELD_EMUL].get(actualOuterLocalVariable);
  }
  /* Answer the synthetic field for <targetEnclosingType>
  *	or null if one does not exist.
  */
  public FieldBinding getSyntheticField(ReferenceBinding targetEnclosingType, boolean onlyExactMatch) {

    if (this.synthetics == null || this.synthetics[SourceTypeBinding.FIELD_EMUL] == null) return null;
    FieldBinding field = (FieldBinding) this.synthetics[SourceTypeBinding.FIELD_EMUL].get(targetEnclosingType);
    if (field != null) return field;

    // type compatibility : to handle cases such as
    // class T { class M{}}
    // class S extends T { class N extends M {}} --> need to use S as a default enclosing instance for the super constructor call in N().
    if (!onlyExactMatch){
      Iterator accessFields = this.synthetics[SourceTypeBinding.FIELD_EMUL].values().iterator();
      while (accessFields.hasNext()) {
        field = (FieldBinding) accessFields.next();
        if (CharOperation.prefixEquals(TypeConstants.SYNTHETIC_ENCLOSING_INSTANCE_PREFIX, field.name)
            && field.type.findSuperTypeOriginatingFrom(targetEnclosingType) != null)
          return field;
      }
    }
    return null;
  }
  /*
  * Answer the bridge method associated for an  inherited methods or null if one does not exist
  */
  public SyntheticMethodBinding getSyntheticBridgeMethod(MethodBinding inheritedMethodToBridge) {
    if (this.synthetics == null) return null;
    if (this.synthetics[SourceTypeBinding.METHOD_EMUL] == null) return null;
    SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[]) this.synthetics[SourceTypeBinding.METHOD_EMUL].get(inheritedMethodToBridge);
    if (accessors == null) return null;
    return accessors[1];
  }

  public boolean hasTypeBit(int bit) {
    // source types initialize type bits during connectSuperclass/interfaces()
    return (this.typeBits & bit) != 0;
  }

  /**
   * @see org.eclipse.jdt.internal.compiler.lookup.Binding#initializeDeprecatedAnnotationTagBits()
   */
  public void initializeDeprecatedAnnotationTagBits() {
    if ((this.tagBits & TagBits.DeprecatedAnnotationResolved) == 0) {
      TypeDeclaration typeDecl = this.scope.referenceContext;
      boolean old = typeDecl.staticInitializerScope.insideTypeAnnotation;
      try {
        typeDecl.staticInitializerScope.insideTypeAnnotation = true;
        ASTNode.resolveDeprecatedAnnotations(typeDecl.staticInitializerScope, typeDecl.annotations, this);
        this.tagBits |= TagBits.DeprecatedAnnotationResolved;
      } finally {
        typeDecl.staticInitializerScope.insideTypeAnnotation = old;
      }
      if ((this.tagBits & TagBits.AnnotationDeprecated) != 0) {
        this.modifiers |= ClassFileConstants.AccDeprecated;
      }
    }
  }

  // ensure the receiver knows its hierarchy & fields/methods so static imports can be resolved correctly
// see bug 230026
  void initializeForStaticImports() {
    if (this.scope == null) return; // already initialized

    if (this.superInterfaces == null)
      this.scope.connectTypeHierarchy();
    this.scope.buildFields();
    this.scope.buildMethods();
  }

  /**
   * Returns true if a type is identical to another one,
   * or for generic types, true if compared to its raw type.
   */
  public boolean isEquivalentTo(TypeBinding otherType) {

    if (this == otherType) return true;
    if (otherType == null) return false;
    switch(otherType.kind()) {

      case Binding.WILDCARD_TYPE :
      case Binding.INTERSECTION_TYPE:
        return ((WildcardBinding) otherType).boundCheck(this);

      case Binding.PARAMETERIZED_TYPE :
        if ((otherType.tagBits & TagBits.HasDirectWildcard) == 0 && (!isMemberType() || !otherType.isMemberType()))
          return false; // should have been identical
        ParameterizedTypeBinding otherParamType = (ParameterizedTypeBinding) otherType;
        if (this != otherParamType.genericType())
          return false;
        if (!isStatic()) { // static member types do not compare their enclosing
          ReferenceBinding enclosing = enclosingType();
          if (enclosing != null) {
            ReferenceBinding otherEnclosing = otherParamType.enclosingType();
            if (otherEnclosing == null) return false;
            if ((otherEnclosing.tagBits & TagBits.HasDirectWildcard) == 0) {
              if (enclosing != otherEnclosing) return false;
            } else {
              if (!enclosing.isEquivalentTo(otherParamType.enclosingType())) return false;
            }
          }
        }
        int length = this.typeVariables == null ? 0 : this.typeVariables.length;
        TypeBinding[] otherArguments = otherParamType.arguments;
        int otherLength = otherArguments == null ? 0 : otherArguments.length;
        if (otherLength != length)
          return false;
        for (int i = 0; i < length; i++)
          if (!this.typeVariables[i].isTypeArgumentContainedBy(otherArguments[i]))
            return false;
        return true;

      case Binding.RAW_TYPE :
        return otherType.erasure() == this;
    }
    return false;
  }
  public boolean isGenericType() {
    return this.typeVariables != Binding.NO_TYPE_VARIABLES;
  }
  public boolean isHierarchyConnected() {
    return (this.tagBits & TagBits.EndHierarchyCheck) != 0;
  }
  public ReferenceBinding[] memberTypes() {
    return this.memberTypes;
  }

  public boolean hasMemberTypes() {
    return this.memberTypes.length > 0;
  }

  // NOTE: the return type, arg & exception types of each method of a source type are resolved when needed
  public MethodBinding[] methods() {
    if ((this.tagBits & TagBits.AreMethodsComplete) != 0)
      return this.methods;

    if (!areMethodsInitialized()) { // https://bugs.eclipse.org/384663
      this.scope.buildMethods();
    }

    // lazily sort methods
    if ((this.tagBits & TagBits.AreMethodsSorted) == 0) {
      int length = this.methods.length;
      if (length > 1)
        ReferenceBinding.sortMethods(this.methods, 0, length);
      this.tagBits |= TagBits.AreMethodsSorted;
    }

    int failed = 0;
    MethodBinding[] resolvedMethods = this.methods;
    try {
      for (int i = 0, length = this.methods.length; i < length; i++) {
        if ((this.tagBits & TagBits.AreMethodsComplete) != 0) {
          // recursive call to methods() from resolveTypesFor(..) resolved the methods
          return this.methods;
        }

        if (resolveTypesFor(this.methods[i]) == null) {
          // do not alter original method array until resolution is over, due to reentrance (143259)
          if (resolvedMethods == this.methods) {
            System.arraycopy(this.methods, 0, resolvedMethods = new MethodBinding[length], 0, length);
          }
          resolvedMethods[i] = null; // unable to resolve parameters
          failed++;
        }
      }

      // find & report collision cases
      boolean complyTo15OrAbove = this.scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
      boolean compliance16 = this.scope.compilerOptions().complianceLevel == ClassFileConstants.JDK1_6;

      for (int i = 0, length = this.methods.length; i < length; i++) {
        int severity = ProblemSeverities.Error;
        MethodBinding method = resolvedMethods[i];
        if (method == null)
          continue;
        char[] selector = method.selector;
        AbstractMethodDeclaration methodDecl = null;
        nextSibling: for (int j = i + 1; j < length; j++) {
          MethodBinding method2 = resolvedMethods[j];
          if (method2 == null)
            continue nextSibling;
          if (!CharOperation.equals(selector, method2.selector))
            break nextSibling; // methods with same selector are contiguous

          if (complyTo15OrAbove) {
            if (method.areParameterErasuresEqual(method2)) {
              // we now ignore return types in 1.7 when detecting duplicates, just as we did before 1.5
              // Only in 1.6, we have to make sure even return types are different
              // https://bugs.eclipse.org/bugs/show_bug.cgi?id=317719
              if (compliance16 && method.returnType != null && method2.returnType != null) {
                if (method.returnType.erasure() != method2.returnType.erasure()) {
                  // check to see if the erasure of either method is equal to the other
                  // if not, then change severity to WARNING
                  TypeBinding[] params1 = method.parameters;
                  TypeBinding[] params2 = method2.parameters;
                  int pLength = params1.length;
                  TypeVariableBinding[] vars = method.typeVariables;
                  TypeVariableBinding[] vars2 = method2.typeVariables;
                  boolean equalTypeVars = vars == vars2;
                  MethodBinding subMethod = method2;
                  if (!equalTypeVars) {
                    MethodBinding temp = method.computeSubstitutedMethod(method2, this.scope.environment());
                    if (temp != null) {
                      equalTypeVars = true;
                      subMethod = temp;
                    }
                  }
                  boolean equalParams = method.areParametersEqual(subMethod);
                  if (equalParams && equalTypeVars) {
                    // duplicates regardless of return types
                  } else if (vars != Binding.NO_TYPE_VARIABLES && vars2 != Binding.NO_TYPE_VARIABLES) {
                    // both have type arguments. Erasure of signature of one cannot be equal to signature of other
                    severity = ProblemSeverities.Warning;
                  } else if (pLength > 0) {
                    int index = pLength;
                    // is erasure of signature of m2 same as signature of m1?
                    for (; --index >= 0;) {
                      if (params1[index] != params2[index].erasure()) {
                        // If one of them is a raw type
                        if (params1[index] instanceof RawTypeBinding) {
                          if (params2[index].erasure() != ((RawTypeBinding)params1[index]).actualType()) {
                            break;
                          }
                        } else  {
                          break;
                        }
                      }
                      if (params1[index] == params2[index]) {
                        TypeBinding type = params1[index].leafComponentType();
                        if (type instanceof SourceTypeBinding && type.typeVariables() != Binding.NO_TYPE_VARIABLES) {
                          index = pLength; // handle comparing identical source types like X<T>... its erasure is itself BUT we need to answer false
                          break;
                        }
                      }
                    }
                    if (index >= 0 && index < pLength) {
                      // is erasure of signature of m1 same as signature of m2?
                      for (index = pLength; --index >= 0;)
                        if (params1[index].erasure() != params2[index]) {
                          // If one of them is a raw type
                          if (params2[index] instanceof RawTypeBinding) {
                            if (params1[index].erasure() != ((RawTypeBinding)params2[index]).actualType()) {
                              break;
                            }
                          } else  {
                            break;
                          }
                        }

                    }
                    if (index >= 0) {
                      // erasure of neither is equal to signature of other
                      severity = ProblemSeverities.Warning;
                    }
                  } else if (pLength != 0){
                    severity = ProblemSeverities.Warning;
                  } // pLength = 0 automatically makes erasure of arguments one equal to arguments of other.
                }
                // else return types also equal. All conditions satisfied
                // to give error in 1.6 compliance as well.
              }
            } else {
              continue nextSibling;
            }
          } else if (!method.areParametersEqual(method2)) {
            // prior to 1.5, parameters identical meant a collision case
            continue nextSibling;
          }
          // otherwise duplicates / name clash
          boolean isEnumSpecialMethod = isEnum() && (CharOperation.equals(selector,TypeConstants.VALUEOF) || CharOperation.equals(selector,TypeConstants.VALUES));
          // report duplicate
          boolean removeMethod2 = (severity == ProblemSeverities.Error) ? true : false; // do not remove if in 1.6 and just a warning given
          if (methodDecl == null) {
            methodDecl = method.sourceMethod(); // cannot be retrieved after binding is lost & may still be null if method is special
            if (methodDecl != null && methodDecl.binding != null) { // ensure its a valid user defined method
              boolean removeMethod = method.returnType == null && method2.returnType != null;
              if (isEnumSpecialMethod) {
                this.scope.problemReporter().duplicateEnumSpecialMethod(this, methodDecl);
                // remove user defined methods & keep the synthetic
                removeMethod = true;
              } else {
                this.scope.problemReporter().duplicateMethodInType(this, methodDecl, method.areParametersEqual(method2), severity);
              }
              if (removeMethod) {
                removeMethod2 = false;
                methodDecl.binding = null;
                // do not alter original method array until resolution is over, due to reentrance (143259)
                if (resolvedMethods == this.methods)
                  System.arraycopy(this.methods, 0, resolvedMethods = new MethodBinding[length], 0, length);
                resolvedMethods[i] = null;
                failed++;
              }
            }
          }
          AbstractMethodDeclaration method2Decl = method2.sourceMethod();
          if (method2Decl != null && method2Decl.binding != null) { // ensure its a valid user defined method
            if (isEnumSpecialMethod) {
              this.scope.problemReporter().duplicateEnumSpecialMethod(this, method2Decl);
              removeMethod2 = true;
            } else {
              this.scope.problemReporter().duplicateMethodInType(this, method2Decl, method.areParametersEqual(method2), severity);
            }
            if (removeMethod2) {
              method2Decl.binding = null;
              // do not alter original method array until resolution is over, due to reentrance (143259)
              if (resolvedMethods == this.methods)
                System.arraycopy(this.methods, 0, resolvedMethods = new MethodBinding[length], 0, length);
              resolvedMethods[j] = null;
              failed++;
            }
          }
        }
        if (method.returnType == null && resolvedMethods[i] != null) { // forget method with invalid return type... was kept to detect possible collisions
          methodDecl = method.sourceMethod();
          if (methodDecl != null)
            methodDecl.binding = null;
          // do not alter original method array until resolution is over, due to reentrance (143259)
          if (resolvedMethods == this.methods)
            System.arraycopy(this.methods, 0, resolvedMethods = new MethodBinding[length], 0, length);
          resolvedMethods[i] = null;
          failed++;
        }
      }
    } finally {
      if ((this.tagBits & TagBits.AreMethodsComplete) != 0) {
        // recursive call to methods() from resolveTypesFor(..) resolved the methods
        return this.methods;
      }
      if (failed > 0) {
        int newSize = resolvedMethods.length - failed;
        if (newSize == 0) {
          this.methods = Binding.NO_METHODS;
        } else {
          MethodBinding[] newMethods = new MethodBinding[newSize];
          for (int i = 0, j = 0, length = resolvedMethods.length; i < length; i++)
            if (resolvedMethods[i] != null)
              newMethods[j++] = resolvedMethods[i];
          this.methods = newMethods;
        }
      }

      // handle forward references to potential default abstract methods
      addDefaultAbstractMethods();
      this.tagBits |= TagBits.AreMethodsComplete;
    }
    return this.methods;
  }
  public FieldBinding resolveTypeFor(FieldBinding field) {
    if ((field.modifiers & ExtraCompilerModifiers.AccUnresolved) == 0)
      return field;

    if (this.scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5) {
      if ((field.getAnnotationTagBits() & TagBits.AnnotationDeprecated) != 0)
        field.modifiers |= ClassFileConstants.AccDeprecated;
    }
    if (isViewedAsDeprecated() && !field.isDeprecated())
      field.modifiers |= ExtraCompilerModifiers.AccDeprecatedImplicitly;
    if (hasRestrictedAccess())
      field.modifiers |= ExtraCompilerModifiers.AccRestrictedAccess;
    FieldDeclaration[] fieldDecls = this.scope.referenceContext.fields;
    int length = fieldDecls == null ? 0 : fieldDecls.length;
    for (int f = 0; f < length; f++) {
      if (fieldDecls[f].binding != field)
        continue;

      MethodScope initializationScope = field.isStatic()
          ? this.scope.referenceContext.staticInitializerScope
          : this.scope.referenceContext.initializerScope;
      FieldBinding previousField = initializationScope.initializedField;
      try {
        initializationScope.initializedField = field;
        FieldDeclaration fieldDecl = fieldDecls[f];
        TypeBinding fieldType =
            fieldDecl.getKind() == AbstractVariableDeclaration.ENUM_CONSTANT
                ? initializationScope.environment().convertToRawType(this, false /*do not force conversion of enclosing types*/) // enum constant is implicitly of declaring enum type
                : fieldDecl.type.resolveType(initializationScope, true /* check bounds*/);
        field.type = fieldType;
        field.modifiers &= ~ExtraCompilerModifiers.AccUnresolved;
        if (fieldType == null) {
          fieldDecl.binding = null;
          return null;
        }
        if (fieldType == TypeBinding.VOID) {
          this.scope.problemReporter().variableTypeCannotBeVoid(fieldDecl);
          fieldDecl.binding = null;
          return null;
        }
        if (fieldType.isArrayType() && ((ArrayBinding) fieldType).leafComponentType == TypeBinding.VOID) {
          this.scope.problemReporter().variableTypeCannotBeVoidArray(fieldDecl);
          fieldDecl.binding = null;
          return null;
        }
        if ((fieldType.tagBits & TagBits.HasMissingType) != 0) {
          field.tagBits |= TagBits.HasMissingType;
        }
        TypeBinding leafType = fieldType.leafComponentType();
        if (leafType instanceof ReferenceBinding && (((ReferenceBinding)leafType).modifiers & ExtraCompilerModifiers.AccGenericSignature) != 0) {
          field.modifiers |= ExtraCompilerModifiers.AccGenericSignature;
        }
      } finally {
        initializationScope.initializedField = previousField;
      }
      return field;
    }
    return null; // should never reach this point
  }
  public MethodBinding resolveTypesFor(MethodBinding method) {
    if ((method.modifiers & ExtraCompilerModifiers.AccUnresolved) == 0)
      return method;

    if (this.scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5) {
      if ((method.getAnnotationTagBits() & TagBits.AnnotationDeprecated) != 0)
        method.modifiers |= ClassFileConstants.AccDeprecated;
    }
    if (isViewedAsDeprecated() && !method.isDeprecated())
      method.modifiers |= ExtraCompilerModifiers.AccDeprecatedImplicitly;
    if (hasRestrictedAccess())
      method.modifiers |= ExtraCompilerModifiers.AccRestrictedAccess;

    AbstractMethodDeclaration methodDecl = method.sourceMethod();
    if (methodDecl == null) return null; // method could not be resolved in previous iteration


    TypeParameter[] typeParameters = methodDecl.typeParameters();
    if (typeParameters != null) {
      methodDecl.scope.connectTypeVariables(typeParameters, true);
      // Perform deferred bound checks for type variables (only done after type variable hierarchy is connected)
      for (int i = 0, paramLength = typeParameters.length; i < paramLength; i++)
        typeParameters[i].checkBounds(methodDecl.scope);
    }
    TypeReference[] exceptionTypes = methodDecl.thrownExceptions;
    if (exceptionTypes != null) {
      int size = exceptionTypes.length;
      method.thrownExceptions = new ReferenceBinding[size];
      int count = 0;
      ReferenceBinding resolvedExceptionType;
      for (int i = 0; i < size; i++) {
        resolvedExceptionType = (ReferenceBinding) exceptionTypes[i].resolveType(methodDecl.scope, true /* check bounds*/);
        if (resolvedExceptionType == null)
          continue;
        if (resolvedExceptionType.isBoundParameterizedType()) {
          methodDecl.scope.problemReporter().invalidParameterizedExceptionType(resolvedExceptionType, exceptionTypes[i]);
          continue;
        }
        if (resolvedExceptionType.findSuperTypeOriginatingFrom(TypeIds.T_JavaLangThrowable, true) == null) {
          if (resolvedExceptionType.isValidBinding()) {
            methodDecl.scope.problemReporter().cannotThrowType(exceptionTypes[i], resolvedExceptionType);
            continue;
          }
        }
        if ((resolvedExceptionType.tagBits & TagBits.HasMissingType) != 0) {
          method.tagBits |= TagBits.HasMissingType;
        }
        method.modifiers |= (resolvedExceptionType.modifiers & ExtraCompilerModifiers.AccGenericSignature);
        method.thrownExceptions[count++] = resolvedExceptionType;
      }
      if (count < size)
        System.arraycopy(method.thrownExceptions, 0, method.thrownExceptions = new ReferenceBinding[count], 0, count);
    }
    final boolean reportUnavoidableGenericTypeProblems = this.scope.compilerOptions().reportUnavoidableGenericTypeProblems;
    boolean foundArgProblem = false;
    Argument[] arguments = methodDecl.arguments;
    if (arguments != null) {
      int size = arguments.length;
      method.parameters = Binding.NO_PARAMETERS;
      TypeBinding[] newParameters = new TypeBinding[size];
      for (int i = 0; i < size; i++) {
        Argument arg = arguments[i];
        if (arg.annotations != null) {
          method.tagBits |= TagBits.HasParameterAnnotations;
        }
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=322817
        boolean deferRawTypeCheck = !reportUnavoidableGenericTypeProblems && !method.isConstructor() && (arg.type.bits & ASTNode.IgnoreRawTypeCheck) == 0;
        TypeBinding parameterType;
        if (deferRawTypeCheck) {
          arg.type.bits |= ASTNode.IgnoreRawTypeCheck;
        }
        try {
          parameterType = arg.type.resolveType(methodDecl.scope, true /* check bounds*/);
        } finally {
          if (deferRawTypeCheck) {
            arg.type.bits &= ~ASTNode.IgnoreRawTypeCheck;
          }
        }

        if (parameterType == null) {
          foundArgProblem = true;
        } else if (parameterType == TypeBinding.VOID) {
          methodDecl.scope.problemReporter().argumentTypeCannotBeVoid(this, methodDecl, arg);
          foundArgProblem = true;
        } else {
          if ((parameterType.tagBits & TagBits.HasMissingType) != 0) {
            method.tagBits |= TagBits.HasMissingType;
          }
          TypeBinding leafType = parameterType.leafComponentType();
          if (leafType instanceof ReferenceBinding && (((ReferenceBinding) leafType).modifiers & ExtraCompilerModifiers.AccGenericSignature) != 0)
            method.modifiers |= ExtraCompilerModifiers.AccGenericSignature;
          newParameters[i] = parameterType;
          arg.binding = new LocalVariableBinding(arg, parameterType, arg.modifiers, true /*isArgument*/);
        }
      }
      // only assign parameters if no problems are found
      if (!foundArgProblem) {
        method.parameters = newParameters;
      }
    }

    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=337799
    if (this.scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_7) {
      if ((method.tagBits & TagBits.AnnotationSafeVarargs) != 0) {
        if (!method.isVarargs()) {
          methodDecl.scope.problemReporter().safeVarargsOnFixedArityMethod(method);
        } else if (!method.isStatic() && !method.isFinal() && !method.isConstructor()) {
          methodDecl.scope.problemReporter().safeVarargsOnNonFinalInstanceMethod(method);
        }
      } else if (method.parameters != null && method.parameters.length > 0 && method.isVarargs()) { // https://bugs.eclipse.org/bugs/show_bug.cgi?id=337795
        if (!method.parameters[method.parameters.length - 1].isReifiable()) {
          methodDecl.scope.problemReporter().possibleHeapPollutionFromVararg(methodDecl.arguments[methodDecl.arguments.length - 1]);
        }
      }
    }

    boolean foundReturnTypeProblem = false;
    if (!method.isConstructor()) {
      TypeReference returnType = methodDecl instanceof MethodDeclaration
          ? ((MethodDeclaration) methodDecl).returnType
          : null;
      if (returnType == null) {
        methodDecl.scope.problemReporter().missingReturnType(methodDecl);
        method.returnType = null;
        foundReturnTypeProblem = true;
      } else {
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=322817
        boolean deferRawTypeCheck = !reportUnavoidableGenericTypeProblems && (returnType.bits & ASTNode.IgnoreRawTypeCheck) == 0;
        TypeBinding methodType;
        if (deferRawTypeCheck) {
          returnType.bits |= ASTNode.IgnoreRawTypeCheck;
        }
        try {
          methodType = returnType.resolveType(methodDecl.scope, true /* check bounds*/);
        } finally {
          if (deferRawTypeCheck) {
            returnType.bits &= ~ASTNode.IgnoreRawTypeCheck;
          }
        }
        if (methodType == null) {
          foundReturnTypeProblem = true;
        } else if (methodType.isArrayType() && ((ArrayBinding) methodType).leafComponentType == TypeBinding.VOID) {
          methodDecl.scope.problemReporter().returnTypeCannotBeVoidArray((MethodDeclaration) methodDecl);
          foundReturnTypeProblem = true;
        } else {
          if ((methodType.tagBits & TagBits.HasMissingType) != 0) {
            method.tagBits |= TagBits.HasMissingType;
          }
          method.returnType = methodType;
          TypeBinding leafType = methodType.leafComponentType();
          if (leafType instanceof ReferenceBinding && (((ReferenceBinding) leafType).modifiers & ExtraCompilerModifiers.AccGenericSignature) != 0)
            method.modifiers |= ExtraCompilerModifiers.AccGenericSignature;
        }
      }
    }
    if (foundArgProblem) {
      methodDecl.binding = null;
      method.parameters = Binding.NO_PARAMETERS; // see 107004
      // nullify type parameter bindings as well as they have a backpointer to the method binding
      // (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=81134)
      if (typeParameters != null)
        for (int i = 0, length = typeParameters.length; i < length; i++)
          typeParameters[i].binding = null;
      return null;
    }
    if (this.scope.compilerOptions().isAnnotationBasedNullAnalysisEnabled)
      createArgumentBindings(method); // need annotations resolved already at this point
    if (foundReturnTypeProblem)
      return method; // but its still unresolved with a null return type & is still connected to its method declaration

    method.modifiers &= ~ExtraCompilerModifiers.AccUnresolved;
    return method;
  }
  private void createArgumentBindings(MethodBinding method) {
    // ensure nullness defaults are initialized at all enclosing levels:
    switch (this.nullnessDefaultInitialized) {
      case 0:
        getAnnotationTagBits(); // initialize
        //$FALL-THROUGH$
      case 1:
        getPackage().isViewedAsDeprecated(); // initialize annotations
        this.nullnessDefaultInitialized = 2;
    }
    AbstractMethodDeclaration methodDecl = method.sourceMethod();
    if (methodDecl != null) {
      if (method.parameters != Binding.NO_PARAMETERS)
        methodDecl.createArgumentBindings();
      if ((findNonNullDefault(methodDecl.scope, methodDecl.scope.environment()) == NONNULL_BY_DEFAULT)) {
        method.fillInDefaultNonNullness();
      }
    }
  }
  private void evaluateNullAnnotations(long annotationTagBits) {
    if (this.nullnessDefaultInitialized > 0 || !this.scope.compilerOptions().isAnnotationBasedNullAnalysisEnabled)
      return;
    boolean isPackageInfo = CharOperation.equals(this.sourceName, TypeConstants.PACKAGE_INFO_NAME);
    PackageBinding pkg = getPackage();
    boolean isInDefaultPkg = (pkg.compoundName == CharOperation.NO_CHAR_CHAR);
    if (!isPackageInfo) {
      boolean isInNullnessAnnotationPackage =
          pkg == this.scope.environment().nonnullAnnotationPackage
              || pkg == this.scope.environment().nullableAnnotationPackage
              || pkg == this.scope.environment().nonnullByDefaultAnnotationPackage;
      if (pkg.defaultNullness == NO_NULL_DEFAULT && !isInDefaultPkg && !isInNullnessAnnotationPackage && !(this instanceof NestedTypeBinding)) {
        ReferenceBinding packageInfo = pkg.getType(TypeConstants.PACKAGE_INFO_NAME);
        if (packageInfo == null) {
          // no pkgInfo - complain
          this.scope.problemReporter().missingNonNullByDefaultAnnotation(this.scope.referenceContext);
          pkg.defaultNullness = NULL_UNSPECIFIED_BY_DEFAULT;
        } else {
          // if pkgInfo has no default annot. - complain
          packageInfo.getAnnotationTagBits();
        }
      }
    }
    this.nullnessDefaultInitialized = 1;
    // transfer nullness info from tagBits to this.nullnessDefaultAnnotation
    int newDefaultNullness = NO_NULL_DEFAULT;
    if ((annotationTagBits & TagBits.AnnotationNullUnspecifiedByDefault) != 0)
      newDefaultNullness = NULL_UNSPECIFIED_BY_DEFAULT;
    else if ((annotationTagBits & TagBits.AnnotationNonNullByDefault) != 0)
      newDefaultNullness = NONNULL_BY_DEFAULT;
    if (newDefaultNullness != NO_NULL_DEFAULT) {
      if (isPackageInfo) {
        pkg.defaultNullness = newDefaultNullness;
      } else {
        this.defaultNullness = newDefaultNullness;
        TypeDeclaration typeDecl = this.scope.referenceContext;
        long nullDefaultBits = annotationTagBits & (TagBits.AnnotationNullUnspecifiedByDefault|TagBits.AnnotationNonNullByDefault);
        checkRedundantNullnessDefaultRecurse(typeDecl, typeDecl.annotations, nullDefaultBits);
      }
    } else if (isPackageInfo || (isInDefaultPkg && !(this instanceof NestedTypeBinding))) {
      this.scope.problemReporter().missingNonNullByDefaultAnnotation(this.scope.referenceContext);
      if (!isInDefaultPkg)
        pkg.defaultNullness = NULL_UNSPECIFIED_BY_DEFAULT;
    }
  }

  protected void checkRedundantNullnessDefaultRecurse(ASTNode location, Annotation[] annotations, long annotationTagBits) {
    if (this.fPackage.defaultNullness != NO_NULL_DEFAULT) {
      if ((this.fPackage.defaultNullness == NONNULL_BY_DEFAULT
          && ((annotationTagBits & TagBits.AnnotationNonNullByDefault) != 0))) {
        this.scope.problemReporter().nullDefaultAnnotationIsRedundant(location, annotations, this.fPackage);
      }
      return;
    }
  }

  // return: should caller continue searching?
  protected boolean checkRedundantNullnessDefaultOne(ASTNode location, Annotation[] annotations, long annotationTagBits) {
    int thisDefault = this.defaultNullness;
    if (thisDefault == NONNULL_BY_DEFAULT) {
      if ((annotationTagBits & TagBits.AnnotationNonNullByDefault) != 0) {
        this.scope.problemReporter().nullDefaultAnnotationIsRedundant(location, annotations, this);
      }
      return false; // different default means inner default is not redundant -> we're done
    }
    return true;
  }

  /**
   * Answer the nullness default applicable at the given method binding.
   * Possible values: {@link Binding#NO_NULL_DEFAULT}, {@link Binding#NULL_UNSPECIFIED_BY_DEFAULT}, {@link Binding#NONNULL_BY_DEFAULT}.
   * @param currentScope where to start search for lexically enclosing default
   * @param environment gateway to options
   */
  private int findNonNullDefault(Scope currentScope, LookupEnvironment environment) {
    // find the applicable default inside->out:

    SourceTypeBinding currentType = null;
    while (currentScope != null) {
      switch (currentScope.kind) {
        case Scope.METHOD_SCOPE:
          AbstractMethodDeclaration referenceMethod = ((MethodScope)currentScope).referenceMethod();
          if (referenceMethod != null && referenceMethod.binding != null) {
            long methodTagBits = referenceMethod.binding.tagBits;
            if ((methodTagBits & TagBits.AnnotationNonNullByDefault) != 0)
              return NONNULL_BY_DEFAULT;
            if ((methodTagBits & TagBits.AnnotationNullUnspecifiedByDefault) != 0)
              return NULL_UNSPECIFIED_BY_DEFAULT;
          }
          break;
        case Scope.CLASS_SCOPE:
          currentType = ((ClassScope)currentScope).referenceContext.binding;
          if (currentType != null) {
            int foundDefaultNullness = currentType.defaultNullness;
            if (foundDefaultNullness != NO_NULL_DEFAULT) {
              return foundDefaultNullness;
            }
          }
          break;
      }
      currentScope = currentScope.parent;
    }

    // package
    if (currentType != null) {
      int foundDefaultNullness = currentType.getPackage().defaultNullness;
      if (foundDefaultNullness != NO_NULL_DEFAULT) {
        return foundDefaultNullness;
      }
    }

    return NO_NULL_DEFAULT;
  }

  public AnnotationHolder retrieveAnnotationHolder(Binding binding, boolean forceInitialization) {
    if (forceInitialization)
      binding.getAnnotationTagBits(); // ensure annotations are up to date
    return super.retrieveAnnotationHolder(binding, false);
  }
  public void setFields(FieldBinding[] fields) {
    this.fields = fields;
  }
  public void setMethods(MethodBinding[] methods) {
    this.methods = methods;
  }
  public final int sourceEnd() {
    return this.scope.referenceContext.sourceEnd;
  }
  public final int sourceStart() {
    return this.scope.referenceContext.sourceStart;
  }
  SimpleLookupTable storedAnnotations(boolean forceInitialize) {
    if (forceInitialize && this.storedAnnotations == null && this.scope != null) { // scope null when no annotation cached, and type got processed fully (159631)
      this.scope.referenceCompilationUnit().compilationResult.hasAnnotations = true;
      if (!this.scope.environment().globalOptions.storeAnnotations)
        return null; // not supported during this compile
      this.storedAnnotations = new SimpleLookupTable(3);
    }
    return this.storedAnnotations;
  }
  public ReferenceBinding superclass() {
    return this.superclass;
  }
  public ReferenceBinding[] superInterfaces() {
    return this.superInterfaces;
  }
  public SyntheticMethodBinding[] syntheticMethods() {
    if (this.synthetics == null
        || this.synthetics[SourceTypeBinding.METHOD_EMUL] == null
        || this.synthetics[SourceTypeBinding.METHOD_EMUL].size() == 0) {
      return null;
    }
    // difficult to compute size up front because of the embedded arrays so assume there is only 1
    int index = 0;
    SyntheticMethodBinding[] bindings = new SyntheticMethodBinding[1];
    Iterator methodArrayIterator = this.synthetics[SourceTypeBinding.METHOD_EMUL].values().iterator();
    while (methodArrayIterator.hasNext()) {
      SyntheticMethodBinding[] methodAccessors = (SyntheticMethodBinding[]) methodArrayIterator.next();
      for (int i = 0, max = methodAccessors.length; i < max; i++) {
        if (methodAccessors[i] != null) {
          if (index+1 > bindings.length) {
            System.arraycopy(bindings, 0, (bindings = new SyntheticMethodBinding[index + 1]), 0, index);
          }
          bindings[index++] = methodAccessors[i];
        }
      }
    }
    // sort them in according to their own indexes
    int length;
    SyntheticMethodBinding[] sortedBindings = new SyntheticMethodBinding[length = bindings.length];
    for (int i = 0; i < length; i++){
      SyntheticMethodBinding binding = bindings[i];
      sortedBindings[binding.index] = binding;
    }
    return sortedBindings;
  }
  /**
   * Answer the collection of synthetic fields to append into the classfile
   */
  public FieldBinding[] syntheticFields() {
    if (this.synthetics == null) return null;
    int fieldSize = this.synthetics[SourceTypeBinding.FIELD_EMUL] == null ? 0 : this.synthetics[SourceTypeBinding.FIELD_EMUL].size();
    int literalSize = this.synthetics[SourceTypeBinding.CLASS_LITERAL_EMUL] == null ? 0 :this.synthetics[SourceTypeBinding.CLASS_LITERAL_EMUL].size();
    int totalSize = fieldSize + literalSize;
    if (totalSize == 0) return null;
    FieldBinding[] bindings = new FieldBinding[totalSize];

    // add innerclass synthetics
    if (this.synthetics[SourceTypeBinding.FIELD_EMUL] != null){
      Iterator elements = this.synthetics[SourceTypeBinding.FIELD_EMUL].values().iterator();
      for (int i = 0; i < fieldSize; i++) {
        SyntheticFieldBinding synthBinding = (SyntheticFieldBinding) elements.next();
        bindings[synthBinding.index] = synthBinding;
      }
    }
    // add class literal synthetics
    if (this.synthetics[SourceTypeBinding.CLASS_LITERAL_EMUL] != null){
      Iterator elements = this.synthetics[SourceTypeBinding.CLASS_LITERAL_EMUL].values().iterator();
      for (int i = 0; i < literalSize; i++) {
        SyntheticFieldBinding synthBinding = (SyntheticFieldBinding) elements.next();
        bindings[fieldSize+synthBinding.index] = synthBinding;
      }
    }
    return bindings;
  }
  public String toString() {
    StringBuffer buffer = new StringBuffer(30);
    buffer.append("(id="); //$NON-NLS-1$
    if (this.id == TypeIds.NoId)
      buffer.append("NoId"); //$NON-NLS-1$
    else
      buffer.append(this.id);
    buffer.append(")\n"); //$NON-NLS-1$
    if (isDeprecated()) buffer.append("deprecated "); //$NON-NLS-1$
    if (isPublic()) buffer.append("public "); //$NON-NLS-1$
    if (isProtected()) buffer.append("protected "); //$NON-NLS-1$
    if (isPrivate()) buffer.append("private "); //$NON-NLS-1$
    if (isAbstract() && isClass()) buffer.append("abstract "); //$NON-NLS-1$
    if (isStatic() && isNestedType()) buffer.append("static "); //$NON-NLS-1$
    if (isFinal()) buffer.append("final "); //$NON-NLS-1$

    if (isEnum()) buffer.append("enum "); //$NON-NLS-1$
    else if (isAnnotationType()) buffer.append("@interface "); //$NON-NLS-1$
    else if (isClass()) buffer.append("class "); //$NON-NLS-1$
    else buffer.append("interface "); //$NON-NLS-1$
    buffer.append((this.compoundName != null) ? CharOperation.toString(this.compoundName) : "UNNAMED TYPE"); //$NON-NLS-1$

    if (this.typeVariables == null) {
      buffer.append("<NULL TYPE VARIABLES>"); //$NON-NLS-1$
    } else if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
      buffer.append("<"); //$NON-NLS-1$
      for (int i = 0, length = this.typeVariables.length; i < length; i++) {
        if (i  > 0) buffer.append(", "); //$NON-NLS-1$
        if (this.typeVariables[i] == null) {
          buffer.append("NULL TYPE VARIABLE"); //$NON-NLS-1$
          continue;
        }
        char[] varChars = this.typeVariables[i].toString().toCharArray();
        buffer.append(varChars, 1, varChars.length - 2);
      }
      buffer.append(">"); //$NON-NLS-1$
    }
    buffer.append("\n\textends "); //$NON-NLS-1$
    buffer.append((this.superclass != null) ? this.superclass.debugName() : "NULL TYPE"); //$NON-NLS-1$

    if (this.superInterfaces != null) {
      if (this.superInterfaces != Binding.NO_SUPERINTERFACES) {
        buffer.append("\n\timplements : "); //$NON-NLS-1$
        for (int i = 0, length = this.superInterfaces.length; i < length; i++) {
          if (i  > 0)
            buffer.append(", "); //$NON-NLS-1$
          buffer.append((this.superInterfaces[i] != null) ? this.superInterfaces[i].debugName() : "NULL TYPE"); //$NON-NLS-1$
        }
      }
    } else {
      buffer.append("NULL SUPERINTERFACES"); //$NON-NLS-1$
    }

    if (enclosingType() != null) {
      buffer.append("\n\tenclosing type : "); //$NON-NLS-1$
      buffer.append(enclosingType().debugName());
    }

    if (this.fields != null) {
      if (this.fields != Binding.NO_FIELDS) {
        buffer.append("\n/*   fields   */"); //$NON-NLS-1$
        for (int i = 0, length = this.fields.length; i < length; i++)
          buffer.append('\n').append((this.fields[i] != null) ? this.fields[i].toString() : "NULL FIELD"); //$NON-NLS-1$
      }
    } else {
      buffer.append("NULL FIELDS"); //$NON-NLS-1$
    }

    if (this.methods != null) {
      if (this.methods != Binding.NO_METHODS) {
        buffer.append("\n/*   methods   */"); //$NON-NLS-1$
        for (int i = 0, length = this.methods.length; i < length; i++)
          buffer.append('\n').append((this.methods[i] != null) ? this.methods[i].toString() : "NULL METHOD"); //$NON-NLS-1$
      }
    } else {
      buffer.append("NULL METHODS"); //$NON-NLS-1$
    }

    if (this.memberTypes != null) {
      if (this.memberTypes != Binding.NO_MEMBER_TYPES) {
        buffer.append("\n/*   members   */"); //$NON-NLS-1$
        for (int i = 0, length = this.memberTypes.length; i < length; i++)
          buffer.append('\n').append((this.memberTypes[i] != null) ? this.memberTypes[i].toString() : "NULL TYPE"); //$NON-NLS-1$
      }
    } else {
      buffer.append("NULL MEMBER TYPES"); //$NON-NLS-1$
    }

    buffer.append("\n\n"); //$NON-NLS-1$
    return buffer.toString();
  }
  public TypeVariableBinding[] typeVariables() {
    return this.typeVariables != null ? this.typeVariables : Binding.NO_TYPE_VARIABLES;
  }
  void verifyMethods(MethodVerifier verifier) {
    verifier.verify(this);

    for (int i = this.memberTypes.length; --i >= 0;)
      ((SourceTypeBinding) this.memberTypes[i]).verifyMethods(verifier);
  }

  public FieldBinding[] unResolvedFields() {
    return this.fields;
  }

  public void tagIndirectlyAccessibleMembers() {
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=328281
    for (int i = 0; i < this.fields.length; i++) {
      if (!this.fields[i].isPrivate())
        this.fields[i].modifiers |= ExtraCompilerModifiers.AccLocallyUsed;
    }
    for (int i = 0; i < this.memberTypes.length; i++) {
      if (!this.memberTypes[i].isPrivate())
        this.memberTypes[i].modifiers |= ExtraCompilerModifiers.AccLocallyUsed;
    }
    if (this.superclass.isPrivate())
      if (this.superclass instanceof SourceTypeBinding)  // should always be true because private super type can only be accessed in same CU
        ((SourceTypeBinding) this.superclass).tagIndirectlyAccessibleMembers();
  }
}