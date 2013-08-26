/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - Contributions for
 *								bug 186342 - [compiler][null] Using annotations for null checking
 *								bug 365519 - editorial cleanup after bug 186342 and bug 365387
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;


import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
import org.eclipse.jdt.internal.compiler.util.SimpleSet;

class MethodVerifier15 extends MethodVerifier {

MethodVerifier15(LookupEnvironment environment) {
	super(environment);
}
boolean areMethodsCompatible(MethodBinding one, MethodBinding two) {
	// use the original methods to test compatibility, but do not check visibility, etc
	one = one.original();
	two = one.findOriginalInheritedMethod(two);

	if (two == null)
		return false; // method's declaringClass does not inherit from inheritedMethod's

	return isParameterSubsignature(one, two);
}
boolean areParametersEqual(MethodBinding one, MethodBinding two) {
	TypeBinding[] oneArgs = one.parameters;
	TypeBinding[] twoArgs = two.parameters;
	if (oneArgs == twoArgs) return true;

	int length = oneArgs.length;
	if (length != twoArgs.length) return false;

	
	// methods with raw parameters are considered equal to inherited methods
	// with parameterized parameters for backwards compatibility, need a more complex check
	int i;
	foundRAW: for (i = 0; i < length; i++) {
		if (!areTypesEqual(oneArgs[i], twoArgs[i])) {
			if (oneArgs[i].leafComponentType().isRawType()) {
				if (oneArgs[i].dimensions() == twoArgs[i].dimensions() && oneArgs[i].leafComponentType().isEquivalentTo(twoArgs[i].leafComponentType())) {
					// raw mode does not apply if the method defines its own type variables
					if (one.typeVariables != Binding.NO_TYPE_VARIABLES)
						return false;
					// one parameter type is raw, hence all parameters types must be raw or non generic
					// otherwise we have a mismatch check backwards
					for (int j = 0; j < i; j++)
						if (oneArgs[j].leafComponentType().isParameterizedTypeWithActualArguments())
							return false;
					// switch to all raw mode
					break foundRAW;
				}
			}
			return false;
		}
	}
	// all raw mode for remaining parameters (if any)
	for (i++; i < length; i++) {
		if (!areTypesEqual(oneArgs[i], twoArgs[i])) {
			if (oneArgs[i].leafComponentType().isRawType())
				if (oneArgs[i].dimensions() == twoArgs[i].dimensions() && oneArgs[i].leafComponentType().isEquivalentTo(twoArgs[i].leafComponentType()))
					continue;
			return false;
		} else if (oneArgs[i].leafComponentType().isParameterizedTypeWithActualArguments()) {
			return false; // no remaining parameter can be a Parameterized type (if one has been converted then all RAW types must be converted)
		}
	}
	return true;
}
boolean areReturnTypesCompatible(MethodBinding one, MethodBinding two) {
	if (one.returnType == two.returnType) return true;
	if (this.type.scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5) {
		return areReturnTypesCompatible0(one, two);
	} else {
		return areTypesEqual(one.returnType.erasure(), two.returnType.erasure());
	}
}
boolean areTypesEqual(TypeBinding one, TypeBinding two) {
	if (one == two) return true;
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=329584
	switch(one.kind()) {
		case Binding.TYPE:
			switch (two.kind()) {
				case Binding.PARAMETERIZED_TYPE:
				case Binding.RAW_TYPE:
					if (one == two.erasure())
						return true;
			}
			break;
		case Binding.RAW_TYPE:
		case Binding.PARAMETERIZED_TYPE:
			switch(two.kind()) {
				case Binding.TYPE:
					if (one.erasure() == two)
						return true;
			}
	}

	// need to consider X<?> and X<? extends Object> as the same 'type'
	if (one.isParameterizedType() && two.isParameterizedType())
		return one.isEquivalentTo(two) && two.isEquivalentTo(one);

	// Can skip this since we resolved each method before comparing it, see computeSubstituteMethod()
	//	if (one instanceof UnresolvedReferenceBinding)
	//		return ((UnresolvedReferenceBinding) one).resolvedType == two;
	//	if (two instanceof UnresolvedReferenceBinding)
	//		return ((UnresolvedReferenceBinding) two).resolvedType == one;
	return false; // all other type bindings are identical
}
// Given `overridingMethod' which overrides `inheritedMethod' answer whether some subclass method that
// differs in erasure from overridingMethod could override `inheritedMethod'
protected boolean canOverridingMethodDifferInErasure(MethodBinding overridingMethod, MethodBinding inheritedMethod) {
	if (overridingMethod.areParameterErasuresEqual(inheritedMethod))
		return false;  // no further change in signature is possible due to parameterization.
	if (overridingMethod.declaringClass.isRawType())
		return false;  // no parameterization is happening anyways.
	return true;
}
boolean canSkipInheritedMethods() {
	if (this.type.superclass() != null)
		if (this.type.superclass().isAbstract() || this.type.superclass().isParameterizedType())
			return false;
	return this.type.superInterfaces() == Binding.NO_SUPERINTERFACES;
}
boolean canSkipInheritedMethods(MethodBinding one, MethodBinding two) {
	return two == null // already know one is not null
		|| (one.declaringClass == two.declaringClass && !one.declaringClass.isParameterizedType());
}
void checkConcreteInheritedMethod(MethodBinding concreteMethod, MethodBinding[] abstractMethods) {
	super.checkConcreteInheritedMethod(concreteMethod, abstractMethods);
	boolean analyseNullAnnotations = this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled;
	for (int i = 0, l = abstractMethods.length; i < l; i++) {
		MethodBinding abstractMethod = abstractMethods[i];
		if (concreteMethod.isVarargs() != abstractMethod.isVarargs())
			problemReporter().varargsConflict(concreteMethod, abstractMethod, this.type);

		// so the parameters are equal and the return type is compatible b/w the currentMethod & the substituted inheritedMethod
		MethodBinding originalInherited = abstractMethod.original();
		if (originalInherited.returnType != concreteMethod.returnType)
			if (!isAcceptableReturnTypeOverride(concreteMethod, abstractMethod))
				problemReporter().unsafeReturnTypeOverride(concreteMethod, originalInherited, this.type);

		// check whether bridge method is already defined above for interface methods
		// skip generation of bridge method for current class & method if an equivalent
		// bridge will be/would have been generated in the context of the super class since
		// the bridge itself will be inherited. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=298362
		if (originalInherited.declaringClass.isInterface()) {
			if ((concreteMethod.declaringClass == this.type.superclass && this.type.superclass.isParameterizedType() && !areMethodsCompatible(concreteMethod, originalInherited))
				|| this.type.superclass.erasure().findSuperTypeOriginatingFrom(originalInherited.declaringClass) == null)
					this.type.addSyntheticBridgeMethod(originalInherited, concreteMethod.original());
		}
		if (analyseNullAnnotations && !concreteMethod.isStatic() && !abstractMethod.isStatic())
			checkNullSpecInheritance(concreteMethod, abstractMethod);
	}
}
void checkForBridgeMethod(MethodBinding currentMethod, MethodBinding inheritedMethod, MethodBinding[] allInheritedMethods) {
	if (currentMethod.isVarargs() != inheritedMethod.isVarargs())
		problemReporter(currentMethod).varargsConflict(currentMethod, inheritedMethod, this.type);

	// so the parameters are equal and the return type is compatible b/w the currentMethod & the substituted inheritedMethod
	MethodBinding originalInherited = inheritedMethod.original();
	if (originalInherited.returnType != currentMethod.returnType)
		if (!isAcceptableReturnTypeOverride(currentMethod, inheritedMethod))
			problemReporter(currentMethod).unsafeReturnTypeOverride(currentMethod, originalInherited, this.type);

	MethodBinding bridge = this.type.addSyntheticBridgeMethod(originalInherited, currentMethod.original());
	if (bridge != null) {
		for (int i = 0, l = allInheritedMethods == null ? 0 : allInheritedMethods.length; i < l; i++) {
			if (allInheritedMethods[i] != null && detectInheritedNameClash(originalInherited, allInheritedMethods[i].original()))
				return;
		}
		// See if the new bridge clashes with any of the user methods of the class. For this check
		// we should check for "method descriptor clash" and not just "method signature clash". Really
		// what we are checking is whether there is a contention for the method dispatch table slot.
		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=293615.
		MethodBinding[] current = (MethodBinding[]) this.currentMethods.get(bridge.selector);
		for (int i = current.length - 1; i >= 0; --i) {
			final MethodBinding thisMethod = current[i];
			if (thisMethod.areParameterErasuresEqual(bridge) && thisMethod.returnType.erasure() == bridge.returnType.erasure()) {
				// use inherited method for problem reporting.
				problemReporter(thisMethod).methodNameClash(thisMethod, inheritedMethod.declaringClass.isRawType() ? inheritedMethod : inheritedMethod.original(), ProblemSeverities.Error);
				return;	
			}
		}
	}
}
void checkForNameClash(MethodBinding currentMethod, MethodBinding inheritedMethod) {
	// sent from checkMethods() to compare a current method and an inherited method that are not 'equal'

	// error cases:
	//		abstract class AA<E extends Comparable> { abstract void test(E element); }
	//		class A extends AA<Integer> { public void test(Integer i) {} }
	//		public class B extends A { public void test(Comparable i) {} }
	//		interface I<E extends Comparable> { void test(E element); }
	//		class A implements I<Integer> { public void test(Integer i) {} }
	//		public class B extends A { public void test(Comparable i) {} }

	//		abstract class Y implements EqualityComparable<Integer>, Equivalent<String> {
	//			public boolean equalTo(Integer other) { return true; }
	//		}
	//		interface Equivalent<T> { boolean equalTo(T other); }
	//		interface EqualityComparable<T> { boolean equalTo(T other); }

	//		class Y implements EqualityComparable, Equivalent<String>{
	//			public boolean equalTo(String other) { return true; }
	//			public boolean equalTo(Object other) { return true; }
	//		}
	//		interface Equivalent<T> { boolean equalTo(T other); }
	//		interface EqualityComparable { boolean equalTo(Object other); }

	//		class A<T extends Number> { void m(T t) {} }
	//		class B<S extends Integer> extends A<S> { void m(S t) {}}
	//		class D extends B<Integer> { void m(Number t) {}    void m(Integer t) {} }

	//		inheritedMethods does not include I.test since A has a valid implementation
	//		interface I<E extends Comparable<E>> { void test(E element); }
	//		class A implements I<Integer> { public void test(Integer i) {} }
	//		class B extends A { public void test(Comparable i) {} }

	if (inheritedMethod.isStatic() || currentMethod.isStatic()) {
		MethodBinding original = inheritedMethod.original(); // can be the same as inherited
		if (this.type.scope.compilerOptions().complianceLevel >= ClassFileConstants.JDK1_7 && currentMethod.areParameterErasuresEqual(original)) {
			problemReporter(currentMethod).methodNameClashHidden(currentMethod, inheritedMethod.declaringClass.isRawType() ? inheritedMethod : original);
		}
		return; // no chance of bridge method's clashing
	}

	if (!detectNameClash(currentMethod, inheritedMethod, false)) { // check up the hierarchy for skipped inherited methods
		TypeBinding[] currentParams = currentMethod.parameters;
		TypeBinding[] inheritedParams = inheritedMethod.parameters;
		int length = currentParams.length;
		if (length != inheritedParams.length) return; // no match

		for (int i = 0; i < length; i++)
			if (currentParams[i] != inheritedParams[i])
				if (currentParams[i].isBaseType() != inheritedParams[i].isBaseType() || !inheritedParams[i].isCompatibleWith(currentParams[i]))
					return; // no chance that another inherited method's bridge method can collide

		ReferenceBinding[] interfacesToVisit = null;
		int nextPosition = 0;
		ReferenceBinding superType = inheritedMethod.declaringClass;
		ReferenceBinding[] itsInterfaces = superType.superInterfaces();
		if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
			nextPosition = itsInterfaces.length;
			interfacesToVisit = itsInterfaces;
		}
		superType = superType.superclass(); // now start with its superclass
		while (superType != null && superType.isValidBinding()) {
			MethodBinding[] methods = superType.getMethods(currentMethod.selector);
			for (int m = 0, n = methods.length; m < n; m++) {
				MethodBinding substitute = computeSubstituteMethod(methods[m], currentMethod);
				if (substitute != null && !isSubstituteParameterSubsignature(currentMethod, substitute) && detectNameClash(currentMethod, substitute, true))
					return;
			}
			if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
				if (interfacesToVisit == null) {
					interfacesToVisit = itsInterfaces;
					nextPosition = interfacesToVisit.length;
				} else {
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
			superType = superType.superclass();
		}

		for (int i = 0; i < nextPosition; i++) {
			superType = interfacesToVisit[i];
			if (superType.isValidBinding()) {
				MethodBinding[] methods = superType.getMethods(currentMethod.selector);
				for (int m = 0, n = methods.length; m < n; m++){
					MethodBinding substitute = computeSubstituteMethod(methods[m], currentMethod);
					if (substitute != null && !isSubstituteParameterSubsignature(currentMethod, substitute) && detectNameClash(currentMethod, substitute, true))
						return;
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
	}
}
void checkInheritedMethods(MethodBinding inheritedMethod, MethodBinding otherInheritedMethod) {

	// the 2 inherited methods clash because of a parameterized type overrides a raw type
	//		interface I { void foo(A a); }
	//		class Y { void foo(A<String> a) {} }
	//		abstract class X extends Y implements I { }
	//		class A<T> {}
	// in this case the 2 inherited methods clash because of type variables
	//		interface I { <T, S> void foo(T t); }
	//		class Y { <T> void foo(T t) {} }
	//		abstract class X extends Y implements I {}

	if (inheritedMethod.isStatic()) return;
	if (this.environment.globalOptions.complianceLevel < ClassFileConstants.JDK1_7 && inheritedMethod.declaringClass.isInterface())
		return;  // JDK7 checks for name clashes in interface inheritance, while JDK6 and below don't. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=354229

	detectInheritedNameClash(inheritedMethod.original(), otherInheritedMethod.original());
}
// 8.4.8.4
void checkInheritedMethods(MethodBinding[] methods, int length) {
	boolean continueInvestigation = true;
	MethodBinding concreteMethod = null;
	for (int i = 0; i < length; i++) {
		if (!methods[i].isAbstract()) {
			if (concreteMethod != null) {
				problemReporter().duplicateInheritedMethods(this.type, concreteMethod, methods[i]);
				continueInvestigation = false;
			}
			concreteMethod = methods[i];
		}
	}
	if (continueInvestigation) {
		super.checkInheritedMethods(methods, length);
	}
}
boolean checkInheritedReturnTypes(MethodBinding method, MethodBinding otherMethod) {
	if (areReturnTypesCompatible(method, otherMethod)) return true;

	/* We used to have some checks here to see if we would have already blamed the super type and if so avoid blaming
	   the current type again. I have gotten rid of them as they in fact short circuit error reporting in cases where
	   they should not. This means that occasionally we would report the error twice - the diagnostics is valid however,
	   albeit arguably redundant. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=334313. For an example of a test
	   where we do this extra reporting see org.eclipse.jdt.core.tests.compiler.regression.MethodVerifyTest.test159()
	 */
	// check to see if this is just a warning, if so report it & skip to next method
	if (isUnsafeReturnTypeOverride(method, otherMethod)) {
		if (!method.declaringClass.implementsInterface(otherMethod.declaringClass, false))
			problemReporter(method).unsafeReturnTypeOverride(method, otherMethod, this.type);
		return true;
	}

	return false;
}
void checkAgainstInheritedMethods(MethodBinding currentMethod, MethodBinding[] methods, int length, MethodBinding[] allInheritedMethods)
{
	super.checkAgainstInheritedMethods(currentMethod, methods, length, allInheritedMethods);
	if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
		for (int i = length; --i >= 0;)
			if (!currentMethod.isStatic() && !methods[i].isStatic())
				checkNullSpecInheritance(currentMethod, methods[i]);
	}
}

void checkNullSpecInheritance(MethodBinding currentMethod, MethodBinding inheritedMethod) {
	// precondition: caller has checked whether annotation-based null analysis is enabled.
	long inheritedBits = inheritedMethod.tagBits;
	long currentBits = currentMethod.tagBits;
	AbstractMethodDeclaration srcMethod = null;
	if (this.type.equals(currentMethod.declaringClass)) // is currentMethod from the current type?
		srcMethod = currentMethod.sourceMethod();

	// return type:
	if ((inheritedBits & TagBits.AnnotationNonNull) != 0) {
		long currentNullBits = currentBits & (TagBits.AnnotationNonNull|TagBits.AnnotationNullable);
		if (currentNullBits != TagBits.AnnotationNonNull) {
			if (srcMethod != null) {
				this.type.scope.problemReporter().illegalReturnRedefinition(srcMethod, inheritedMethod,
															this.environment.getNonNullAnnotationName());
			} else {
				this.type.scope.problemReporter().cannotImplementIncompatibleNullness(currentMethod, inheritedMethod);
				return;
			}
		}
	}

	// parameters:
	Argument[] currentArguments = srcMethod == null ? null : srcMethod.arguments;
	if (inheritedMethod.parameterNonNullness != null) {
		// inherited method has null-annotations, check compatibility:

		int length = inheritedMethod.parameterNonNullness.length;
		for (int i = 0; i < length; i++) {
			Argument currentArgument = currentArguments == null ? null : currentArguments[i];

			Boolean inheritedNonNullNess = inheritedMethod.parameterNonNullness[i];
			Boolean currentNonNullNess = (currentMethod.parameterNonNullness == null)
										? null : currentMethod.parameterNonNullness[i];
			if (inheritedNonNullNess != null) {				// super has a null annotation
				if (currentNonNullNess == null) {			// current parameter lacks null annotation
					boolean needNonNull = false;
					char[][] annotationName;
					if (inheritedNonNullNess == Boolean.TRUE) {
						needNonNull = true;
						annotationName = this.environment.getNonNullAnnotationName();
					} else {
						annotationName = this.environment.getNullableAnnotationName();
					}
					if (currentArgument != null) {
						this.type.scope.problemReporter().parameterLackingNullAnnotation(
								currentArgument,
								inheritedMethod.declaringClass,
								needNonNull,
								annotationName);
						continue;
					} else {
						this.type.scope.problemReporter().cannotImplementIncompatibleNullness(currentMethod, inheritedMethod);
						break;
					}
				}
			}
			if (inheritedNonNullNess != Boolean.TRUE) {		// super parameter is not restricted to @NonNull
				if (currentNonNullNess == Boolean.TRUE) { 	// current parameter is restricted to @NonNull
					if (currentArgument != null)
						this.type.scope.problemReporter().illegalRedefinitionToNonNullParameter(
														currentArgument,
														inheritedMethod.declaringClass,
														inheritedNonNullNess == null
														? null
														: this.environment.getNullableAnnotationName());
					else
						this.type.scope.problemReporter().cannotImplementIncompatibleNullness(currentMethod, inheritedMethod);
				}
			}
		}
	} else if (currentMethod.parameterNonNullness != null) {
		// super method has no annotations but current has
		for (int i = 0; i < currentMethod.parameterNonNullness.length; i++) {
			if (currentMethod.parameterNonNullness[i] == Boolean.TRUE) { // tightening from unconstrained to @NonNull
				if (currentArguments != null) {
					this.type.scope.problemReporter().illegalRedefinitionToNonNullParameter(
																	currentArguments[i],
																	inheritedMethod.declaringClass,
																	null);
				} else {
					this.type.scope.problemReporter().cannotImplementIncompatibleNullness(currentMethod, inheritedMethod);
					break;
				}
			}
		}
	}
}

void reportRawReferences() {
	CompilerOptions compilerOptions = this.type.scope.compilerOptions();
	if (compilerOptions.sourceLevel < ClassFileConstants.JDK1_5 // shouldn't whine at all
			|| compilerOptions.reportUnavoidableGenericTypeProblems) { // must have already whined 
		return;
	}
	/* Code below is only for a method that does not override/implement a super type method. If it were to,
	   it would have been handled in checkAgainstInheritedMethods.
	*/
	Object [] methodArray = this.currentMethods.valueTable;
	for (int s = methodArray.length; --s >= 0;) {
		if (methodArray[s] == null) continue;
		MethodBinding[] current = (MethodBinding[]) methodArray[s];
		for (int i = 0, length = current.length; i < length; i++) {
			MethodBinding currentMethod = current[i];
			if ((currentMethod.modifiers & (ExtraCompilerModifiers.AccImplementing | ExtraCompilerModifiers.AccOverriding)) == 0) {
				AbstractMethodDeclaration methodDecl = currentMethod.sourceMethod();
				if (methodDecl == null) return;
				TypeBinding [] parameterTypes = currentMethod.parameters;
				Argument[] arguments = methodDecl.arguments;
				for (int j = 0, size = currentMethod.parameters.length; j < size; j++) {
					TypeBinding parameterType = parameterTypes[j];
					Argument arg = arguments[j];
					if (parameterType.leafComponentType().isRawType()
						&& compilerOptions.getSeverity(CompilerOptions.RawTypeReference) != ProblemSeverities.Ignore
			      		&& (arg.type.bits & ASTNode.IgnoreRawTypeCheck) == 0) {
						methodDecl.scope.problemReporter().rawTypeReference(arg.type, parameterType);
			    	}
				}
				if (!methodDecl.isConstructor() && methodDecl instanceof MethodDeclaration) {
					TypeReference returnType = ((MethodDeclaration) methodDecl).returnType;
					TypeBinding methodType = currentMethod.returnType;
					if (returnType != null) {
						if (methodType.leafComponentType().isRawType()
								&& compilerOptions.getSeverity(CompilerOptions.RawTypeReference) != ProblemSeverities.Ignore
								&& (returnType.bits & ASTNode.IgnoreRawTypeCheck) == 0) {
							methodDecl.scope.problemReporter().rawTypeReference(returnType, methodType);
						}
					}
				}
			}
		}
	}
}
public void reportRawReferences(MethodBinding currentMethod, MethodBinding inheritedMethod) {
	CompilerOptions compilerOptions = this.type.scope.compilerOptions();
	if (compilerOptions.sourceLevel < ClassFileConstants.JDK1_5 // shouldn't whine at all
			|| compilerOptions.reportUnavoidableGenericTypeProblems) { // must have already whined 
		return;
	}
	AbstractMethodDeclaration methodDecl = currentMethod.sourceMethod();
	if (methodDecl == null) return;
	TypeBinding [] parameterTypes = currentMethod.parameters;
	TypeBinding [] inheritedParameterTypes = inheritedMethod.parameters;
	Argument[] arguments = methodDecl.arguments;
	for (int j = 0, size = currentMethod.parameters.length; j < size; j++) {
		TypeBinding parameterType = parameterTypes[j];
		TypeBinding inheritedParameterType = inheritedParameterTypes[j];
		Argument arg = arguments[j];
		if (parameterType.leafComponentType().isRawType()) {
			if (inheritedParameterType.leafComponentType().isRawType()) {
				arg.binding.tagBits |= TagBits.ForcedToBeRawType;
			} else {
				if (compilerOptions.getSeverity(CompilerOptions.RawTypeReference) != ProblemSeverities.Ignore
						&& (arg.type.bits & ASTNode.IgnoreRawTypeCheck) == 0) {
					methodDecl.scope.problemReporter().rawTypeReference(arg.type, parameterType);
				}
			}
    	}
    }
	TypeReference returnType = null;
	if (!methodDecl.isConstructor() && methodDecl instanceof MethodDeclaration && (returnType = ((MethodDeclaration) methodDecl).returnType) != null) {
		final TypeBinding inheritedMethodType = inheritedMethod.returnType;
		final TypeBinding methodType = currentMethod.returnType;
		if (methodType.leafComponentType().isRawType()) {
			if (inheritedMethodType.leafComponentType().isRawType()) {
				// 
			} else {
				if ((returnType.bits & ASTNode.IgnoreRawTypeCheck) == 0
						&& compilerOptions.getSeverity(CompilerOptions.RawTypeReference) != ProblemSeverities.Ignore) {
					methodDecl.scope.problemReporter().rawTypeReference(returnType, methodType);
				}
			}
		}
	}
 }

void checkMethods() {
	boolean mustImplementAbstractMethods = mustImplementAbstractMethods();
	boolean skipInheritedMethods = mustImplementAbstractMethods && canSkipInheritedMethods(); // have a single concrete superclass so only check overridden methods
	boolean isOrEnclosedByPrivateType = this.type.isOrEnclosedByPrivateType();
	char[][] methodSelectors = this.inheritedMethods.keyTable;
	nextSelector : for (int s = methodSelectors.length; --s >= 0;) {
		if (methodSelectors[s] == null) continue nextSelector;

		MethodBinding[] current = (MethodBinding[]) this.currentMethods.get(methodSelectors[s]);
		MethodBinding[] inherited = (MethodBinding[]) this.inheritedMethods.valueTable[s];
		
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=296660, if current type is exposed,
		// inherited methods of super classes are too. current != null case handled below.
		if (current == null && !isOrEnclosedByPrivateType) {
			int length = inherited.length;
			for (int i = 0; i < length; i++){
				inherited[i].original().modifiers |= ExtraCompilerModifiers.AccLocallyUsed;
			}
		}
		if (current == null && this.type.isPublic()) {
			int length = inherited.length;
			for (int i = 0; i < length; i++) {
				MethodBinding inheritedMethod = inherited[i];
				if (inheritedMethod.isPublic() && !inheritedMethod.declaringClass.isPublic())
					this.type.addSyntheticBridgeMethod(inheritedMethod.original());
			}
		}

		if (current == null && skipInheritedMethods)
			continue nextSelector;

		if (inherited.length == 1 && current == null) { // handle the common case
			if (mustImplementAbstractMethods && inherited[0].isAbstract())
				checkAbstractMethod(inherited[0]);
			continue nextSelector;
		}

		int index = -1;
		int inheritedLength = inherited.length;
		MethodBinding[] matchingInherited = new MethodBinding[inheritedLength];
		MethodBinding[] foundMatch = new MethodBinding[inheritedLength]; // null is no match, otherwise value is matching currentMethod
		if (current != null) {
			for (int i = 0, length1 = current.length; i < length1; i++) {
				MethodBinding currentMethod = current[i];
				MethodBinding[] nonMatchingInherited = null;
				for (int j = 0; j < inheritedLength; j++) {
					MethodBinding inheritedMethod = computeSubstituteMethod(inherited[j], currentMethod);
					if (inheritedMethod != null) {
						if (foundMatch[j] == null && isSubstituteParameterSubsignature(currentMethod, inheritedMethod)) {
							matchingInherited[++index] = inheritedMethod;
							foundMatch[j] = currentMethod;
						} else {
							// best place to check each currentMethod against each non-matching inheritedMethod
							checkForNameClash(currentMethod, inheritedMethod);
							if (inheritedLength > 1) {
								if (nonMatchingInherited == null)
									nonMatchingInherited = new MethodBinding[inheritedLength];
								nonMatchingInherited[j] = inheritedMethod;
							}
						}
					}
				}
				if (index >= 0) {
					// see addtional comments in https://bugs.eclipse.org/bugs/show_bug.cgi?id=122881
					// if (index > 0 && currentMethod.declaringClass.isInterface()) // only check when inherited methods are from interfaces
					//	checkInheritedReturnTypes(matchingInherited, index + 1);
					checkAgainstInheritedMethods(currentMethod, matchingInherited, index + 1, nonMatchingInherited); // pass in the length of matching
					while (index >= 0) matchingInherited[index--] = null; // clear the contents of the matching methods
				}
			}
		}

		// skip tracks which inherited methods have matched other inherited methods
		// either because they match the same currentMethod or match each other
		boolean[] skip = new boolean[inheritedLength];
		for (int i = 0; i < inheritedLength; i++) {
			MethodBinding matchMethod = foundMatch[i];
			if (matchMethod == null && current != null && this.type.isPublic()) { // current == null case handled already.
				MethodBinding inheritedMethod = inherited[i];
				if (inheritedMethod.isPublic() && !inheritedMethod.declaringClass.isPublic()) {
					this.type.addSyntheticBridgeMethod(inheritedMethod.original());
				}
			}
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=296660, if current type is exposed,
			// inherited methods of super classes are too. current == null case handled already.
			if (!isOrEnclosedByPrivateType && matchMethod == null && current != null) {
				inherited[i].original().modifiers |= ExtraCompilerModifiers.AccLocallyUsed;	
			}
			if (skip[i]) continue;
			MethodBinding inheritedMethod = inherited[i];
			if (matchMethod == null)
				matchingInherited[++index] = inheritedMethod;
			for (int j = i + 1; j < inheritedLength; j++) {
				MethodBinding otherInheritedMethod = inherited[j];
				if (matchMethod == foundMatch[j] && matchMethod != null)
					continue; // both inherited methods matched the same currentMethod
				if (canSkipInheritedMethods(inheritedMethod, otherInheritedMethod))
					continue;
				// Skip the otherInheritedMethod if it is completely replaced by inheritedMethod
				// This elimination used to happen rather eagerly in computeInheritedMethods step
				// itself earlier. (https://bugs.eclipse.org/bugs/show_bug.cgi?id=302358)
				if (inheritedMethod.declaringClass != otherInheritedMethod.declaringClass) {
					if (otherInheritedMethod.declaringClass.isInterface()) {
						if (isInterfaceMethodImplemented(otherInheritedMethod, inheritedMethod, otherInheritedMethod.declaringClass)) {
							skip[j] = true;
							continue;
						}
					} else if (areMethodsCompatible(inheritedMethod, otherInheritedMethod)) {
						skip[j] = true;
						continue;
					}
				}
				otherInheritedMethod = computeSubstituteMethod(otherInheritedMethod, inheritedMethod);
				if (otherInheritedMethod != null) {
					if (isSubstituteParameterSubsignature(inheritedMethod, otherInheritedMethod)) {
							if (index == -1)
								matchingInherited[++index] = inheritedMethod;
							if (foundMatch[j] == null)
								matchingInherited[++index] = otherInheritedMethod;
							skip[j] = true;
					} else if (matchMethod == null && foundMatch[j] == null) {
						checkInheritedMethods(inheritedMethod, otherInheritedMethod);
					}
				}
			}
			if (index == -1) continue;

			if (index > 0)
				checkInheritedMethods(matchingInherited, index + 1); // pass in the length of matching
			else if (mustImplementAbstractMethods && matchingInherited[0].isAbstract() && matchMethod == null)
				checkAbstractMethod(matchingInherited[0]);
			while (index >= 0) matchingInherited[index--] = null; // clear the previous contents of the matching methods
		}
	}
}
void checkTypeVariableMethods(TypeParameter typeParameter) {
	char[][] methodSelectors = this.inheritedMethods.keyTable;
	nextSelector : for (int s = methodSelectors.length; --s >= 0;) {
		if (methodSelectors[s] == null) continue nextSelector;
		MethodBinding[] inherited = (MethodBinding[]) this.inheritedMethods.valueTable[s];
		if (inherited.length == 1) continue nextSelector;

		int index = -1;
		MethodBinding[] matchingInherited = new MethodBinding[inherited.length];
		for (int i = 0, length = inherited.length; i < length; i++) {
			while (index >= 0) matchingInherited[index--] = null; // clear the previous contents of the matching methods
			MethodBinding inheritedMethod = inherited[i];
			if (inheritedMethod != null) {
				matchingInherited[++index] = inheritedMethod;
				for (int j = i + 1; j < length; j++) {
					MethodBinding otherInheritedMethod = inherited[j];
					if (canSkipInheritedMethods(inheritedMethod, otherInheritedMethod))
						continue;
					otherInheritedMethod = computeSubstituteMethod(otherInheritedMethod, inheritedMethod);
					if (otherInheritedMethod != null && isSubstituteParameterSubsignature(inheritedMethod, otherInheritedMethod)) {
						matchingInherited[++index] = otherInheritedMethod;
						inherited[j] = null; // do not want to find it again
					}
				}
			}
			if (index > 0) {
				MethodBinding first = matchingInherited[0];
				int count = index + 1;
				while (--count > 0) {
					MethodBinding match = matchingInherited[count];
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=314556
					MethodBinding interfaceMethod = null, implementation = null;
					if (first.declaringClass.isInterface()) {
						interfaceMethod = first;
					} else if (first.declaringClass.isClass()) {
						implementation = first;
					}
					if (match.declaringClass.isInterface()) {
						interfaceMethod = match;
					} else if (match.declaringClass.isClass()) {
						implementation = match;
					}
					if (interfaceMethod != null && implementation != null && !isAsVisible(implementation, interfaceMethod))
						problemReporter().inheritedMethodReducesVisibility(typeParameter, implementation, new MethodBinding [] {interfaceMethod});
					
					if (areReturnTypesCompatible(first, match)) continue;
					// unrelated interfaces - check to see if return types are compatible
					if (first.declaringClass.isInterface() && match.declaringClass.isInterface() && areReturnTypesCompatible(match, first))
						continue;
					break;
				}
				if (count > 0) {  // All inherited methods do NOT have the same vmSignature
					problemReporter().inheritedMethodsHaveIncompatibleReturnTypes(typeParameter, matchingInherited, index + 1);
					continue nextSelector;
				}
			}
		}
	}
}
MethodBinding computeSubstituteMethod(MethodBinding inheritedMethod, MethodBinding currentMethod) {
	if (inheritedMethod == null) return null;
	if (currentMethod.parameters.length != inheritedMethod.parameters.length) return null; // no match

	// due to hierarchy & compatibility checks, we need to ensure these 2 methods are resolved
	if (currentMethod.declaringClass instanceof BinaryTypeBinding)
		((BinaryTypeBinding) currentMethod.declaringClass).resolveTypesFor(currentMethod);
	if (inheritedMethod.declaringClass instanceof BinaryTypeBinding)
		((BinaryTypeBinding) inheritedMethod.declaringClass).resolveTypesFor(inheritedMethod);

	TypeVariableBinding[] inheritedTypeVariables = inheritedMethod.typeVariables;
	int inheritedLength = inheritedTypeVariables.length;
	if (inheritedLength == 0) return inheritedMethod; // no substitution needed
	TypeVariableBinding[] typeVariables = currentMethod.typeVariables;
	int length = typeVariables.length;
	if (length == 0)
		return inheritedMethod.asRawMethod(this.environment);
	if (length != inheritedLength)
		return inheritedMethod; // no match JLS 8.4.2

	// interface I { <T> void foo(T t); }
	// class X implements I { public <T extends I> void foo(T t) {} }
	// for the above case, we do not want to answer the substitute method since its not a match
	TypeBinding[] arguments = new TypeBinding[length];
	System.arraycopy(typeVariables, 0, arguments, 0, length);
	ParameterizedGenericMethodBinding substitute =
		this.environment.createParameterizedGenericMethod(inheritedMethod, arguments);
	for (int i = 0; i < inheritedLength; i++) {
		TypeVariableBinding inheritedTypeVariable = inheritedTypeVariables[i];
		TypeBinding argument = arguments[i];
		if (argument instanceof TypeVariableBinding) {
			TypeVariableBinding typeVariable = (TypeVariableBinding) argument;
			if (typeVariable.firstBound == inheritedTypeVariable.firstBound) {
				if (typeVariable.firstBound == null)
					continue; // both are null
			} else if (typeVariable.firstBound != null && inheritedTypeVariable.firstBound != null) {
				if (typeVariable.firstBound.isClass() != inheritedTypeVariable.firstBound.isClass())
					return inheritedMethod; // not a match
			}
			if (Scope.substitute(substitute, inheritedTypeVariable.superclass) != typeVariable.superclass)
				return inheritedMethod; // not a match
			int interfaceLength = inheritedTypeVariable.superInterfaces.length;
			ReferenceBinding[] interfaces = typeVariable.superInterfaces;
			if (interfaceLength != interfaces.length)
				return inheritedMethod; // not a match
			// TODO (kent) another place where we expect the superinterfaces to be in the exact same order
			next : for (int j = 0; j < interfaceLength; j++) {
				TypeBinding superType = Scope.substitute(substitute, inheritedTypeVariable.superInterfaces[j]);
				for (int k = 0; k < interfaceLength; k++)
					if (superType == interfaces[k])
						continue next;
				return inheritedMethod; // not a match
			}
		} else if (inheritedTypeVariable.boundCheck(substitute, argument) != TypeConstants.OK) {
	    	return inheritedMethod;
		}
	}
   return substitute;
}
boolean detectInheritedNameClash(MethodBinding inherited, MethodBinding otherInherited) {
	if (!inherited.areParameterErasuresEqual(otherInherited))
		return false;
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=322001
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=323693
	// When reporting a name clash between two inherited methods, we should not look for a
	// signature clash, but instead should be looking for method descriptor clash. 
	if (inherited.returnType.erasure() != otherInherited.returnType.erasure())
		return false;
	// skip it if otherInherited is defined by a subtype of inherited's declaringClass or vice versa.
	// avoid being order sensitive and check with the roles reversed also.
	if (inherited.declaringClass.erasure() != otherInherited.declaringClass.erasure()) {
		if (inherited.declaringClass.findSuperTypeOriginatingFrom(otherInherited.declaringClass) != null)
			return false;
		if (otherInherited.declaringClass.findSuperTypeOriginatingFrom(inherited.declaringClass) != null)
			return false;
	}

	problemReporter().inheritedMethodsHaveNameClash(this.type, inherited, otherInherited);
	return true;
}
boolean detectNameClash(MethodBinding current, MethodBinding inherited, boolean treatAsSynthetic) {
	MethodBinding methodToCheck = inherited;
	MethodBinding original = methodToCheck.original(); // can be the same as inherited
	if (!current.areParameterErasuresEqual(original))
		return false;
	int severity = ProblemSeverities.Error;
	if (this.environment.globalOptions.complianceLevel == ClassFileConstants.JDK1_6) {
		// for 1.6 return types also need to be checked
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=317719
		if (current.returnType.erasure() != original.returnType.erasure())
			severity = ProblemSeverities.Warning;
	}
	if (!treatAsSynthetic) {
		// For a user method, see if current class overrides the inherited method. If it does,
		// then any grievance we may have ought to be against the current class's method and
		// NOT against any super implementations. https://bugs.eclipse.org/bugs/show_bug.cgi?id=293615
		
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=315978 : we now defer this rather expensive
		// check to just before reporting (the incorrect) name clash. In the event there is no name
		// clash to report to begin with (the common case), no penalty needs to be paid.  
		MethodBinding[] currentNamesakes = (MethodBinding[]) this.currentMethods.get(inherited.selector);
		if (currentNamesakes.length > 1) { // we know it ought to at least one and that current is NOT the override
			for (int i = 0, length = currentNamesakes.length; i < length; i++) {
				MethodBinding currentMethod = currentNamesakes[i];
				if (currentMethod != current && doesMethodOverride(currentMethod, inherited)) {
					methodToCheck = currentMethod;
					break;
				}
			}
		}
	}
	original = methodToCheck.original(); // can be the same as inherited
	if (!current.areParameterErasuresEqual(original))
		return false;
	original = inherited.original();  // For error reporting use, inherited.original()
	problemReporter(current).methodNameClash(current, inherited.declaringClass.isRawType() ? inherited : original, severity);
	if (severity == ProblemSeverities.Warning) return false;
	return true;
}
public boolean doesMethodOverride(MethodBinding method, MethodBinding inheritedMethod) {
	return couldMethodOverride(method, inheritedMethod) && areMethodsCompatible(method, inheritedMethod);
}
boolean doTypeVariablesClash(MethodBinding one, MethodBinding substituteTwo) {
	// one has type variables and substituteTwo did not pass bounds check in computeSubstituteMethod()
	return one.typeVariables != Binding.NO_TYPE_VARIABLES && !(substituteTwo instanceof ParameterizedGenericMethodBinding);
}
SimpleSet findSuperinterfaceCollisions(ReferenceBinding superclass, ReferenceBinding[] superInterfaces) {
	ReferenceBinding[] interfacesToVisit = null;
	int nextPosition = 0;
	ReferenceBinding[] itsInterfaces = superInterfaces;
	if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
		nextPosition = itsInterfaces.length;
		interfacesToVisit = itsInterfaces;
	}

	boolean isInconsistent = this.type.isHierarchyInconsistent();
	ReferenceBinding superType = superclass;
	while (superType != null && superType.isValidBinding()) {
		isInconsistent |= superType.isHierarchyInconsistent();
		if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
			if (interfacesToVisit == null) {
				interfacesToVisit = itsInterfaces;
				nextPosition = interfacesToVisit.length;
			} else {
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
		superType = superType.superclass();
	}

	for (int i = 0; i < nextPosition; i++) {
		superType = interfacesToVisit[i];
		if (superType.isValidBinding()) {
			isInconsistent |= superType.isHierarchyInconsistent();
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

	if (!isInconsistent) return null; // hierarchy is consistent so no collisions are possible
	SimpleSet copy = null;
	for (int i = 0; i < nextPosition; i++) {
		ReferenceBinding current = interfacesToVisit[i];
		if (current.isValidBinding()) {
			TypeBinding erasure = current.erasure();
			for (int j = i + 1; j < nextPosition; j++) {
				ReferenceBinding next = interfacesToVisit[j];
				if (next.isValidBinding() && next.erasure() == erasure) {
					if (copy == null)
						copy = new SimpleSet(nextPosition);
					copy.add(interfacesToVisit[i]);
					copy.add(interfacesToVisit[j]);
				}
			}
		}
	}
	return copy;
}
boolean hasGenericParameter(MethodBinding method) {
	if (method.genericSignature() == null) return false;

	// may be only the return type that is generic, need to check parameters
	TypeBinding[] params = method.parameters;
	for (int i = 0, l = params.length; i < l; i++) {
		TypeBinding param = params[i].leafComponentType();
		if (param instanceof ReferenceBinding) {
			int modifiers = ((ReferenceBinding) param).modifiers;
			if ((modifiers & ExtraCompilerModifiers.AccGenericSignature) != 0)
				return true;
		}
	}
	return false;
}
boolean isAcceptableReturnTypeOverride(MethodBinding currentMethod, MethodBinding inheritedMethod) {
	// called when currentMethod's return type is compatible with inheritedMethod's return type

	if (inheritedMethod.declaringClass.isRawType())
		return true; // since the inheritedMethod comes from a raw type, the return type is always acceptable

	MethodBinding originalInherited = inheritedMethod.original();
	TypeBinding originalInheritedReturnType = originalInherited.returnType.leafComponentType();
	if (originalInheritedReturnType.isParameterizedTypeWithActualArguments())
		return !currentMethod.returnType.leafComponentType().isRawType(); // raw types issue a warning if inherited is parameterized

	TypeBinding currentReturnType = currentMethod.returnType.leafComponentType();
	switch (currentReturnType.kind()) {
	   	case Binding.TYPE_PARAMETER :
	   		if (currentReturnType == inheritedMethod.returnType.leafComponentType())
	   			return true;
	   		//$FALL-THROUGH$
		default :
			if (originalInheritedReturnType.isTypeVariable())
				if (((TypeVariableBinding) originalInheritedReturnType).declaringElement == originalInherited)
					return false;
			return true;
	}
}
// caveat: returns false if a method is implemented that needs a bridge method
boolean isInterfaceMethodImplemented(MethodBinding inheritedMethod, MethodBinding existingMethod, ReferenceBinding superType) {
	if (inheritedMethod.original() != inheritedMethod && existingMethod.declaringClass.isInterface())
		return false; // must hold onto ParameterizedMethod to see if a bridge method is necessary

	inheritedMethod = computeSubstituteMethod(inheritedMethod, existingMethod);
	return inheritedMethod != null
		&& inheritedMethod.returnType == existingMethod.returnType // keep around to produce bridge methods
		&& doesMethodOverride(existingMethod, inheritedMethod);
}
public boolean isMethodSubsignature(MethodBinding method, MethodBinding inheritedMethod) {
	if (!org.eclipse.jdt.core.compiler.CharOperation.equals(method.selector, inheritedMethod.selector))
		return false;

	// need to switch back to the original if the method is from a ParameterizedType
	if (method.declaringClass.isParameterizedType())
		method = method.original();

	MethodBinding inheritedOriginal = method.findOriginalInheritedMethod(inheritedMethod);
	return isParameterSubsignature(method, inheritedOriginal == null ? inheritedMethod : inheritedOriginal);
}
boolean isParameterSubsignature(MethodBinding method, MethodBinding inheritedMethod) {
	MethodBinding substitute = computeSubstituteMethod(inheritedMethod, method);
	return substitute != null && isSubstituteParameterSubsignature(method, substitute);
}
// if method "overrides" substituteMethod then we can skip over substituteMethod while resolving a message send
// if it does not then a name clash error is likely
boolean isSubstituteParameterSubsignature(MethodBinding method, MethodBinding substituteMethod) {
	if (!areParametersEqual(method, substituteMethod)) {
		// method can still override substituteMethod in cases like :
		// <U extends Number> void c(U u) {}
		// @Override void c(Number n) {}
		// but method cannot have a "generic-enabled" parameter type
		if (substituteMethod.hasSubstitutedParameters() && method.areParameterErasuresEqual(substituteMethod))
			return method.typeVariables == Binding.NO_TYPE_VARIABLES && !hasGenericParameter(method);

		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=279836
		if (method.declaringClass.isRawType() && substituteMethod.declaringClass.isRawType())
			if (method.hasSubstitutedParameters() && substituteMethod.hasSubstitutedParameters())
				return areMethodsCompatible(method, substituteMethod);

		return false;
	}

	if (substituteMethod instanceof ParameterizedGenericMethodBinding) {
		if (method.typeVariables != Binding.NO_TYPE_VARIABLES)
			return !((ParameterizedGenericMethodBinding) substituteMethod).isRaw;
		// since substituteMethod has substituted type variables, method cannot have a generic signature AND no variables -> its a name clash if it does
		return !hasGenericParameter(method);
	}

	// if method has its own variables, then substituteMethod failed bounds check in computeSubstituteMethod()
	return method.typeVariables == Binding.NO_TYPE_VARIABLES;
}
boolean isUnsafeReturnTypeOverride(MethodBinding currentMethod, MethodBinding inheritedMethod) {
	// called when currentMethod's return type is NOT compatible with inheritedMethod's return type

	// JLS 3 �8.4.5: more are accepted, with an unchecked conversion
	if (currentMethod.returnType == inheritedMethod.returnType.erasure()) {
		TypeBinding[] currentParams = currentMethod.parameters;
		TypeBinding[] inheritedParams = inheritedMethod.parameters;
		for (int i = 0, l = currentParams.length; i < l; i++)
			if (!areTypesEqual(currentParams[i], inheritedParams[i]))
				return true;
	}
	if (currentMethod.typeVariables == Binding.NO_TYPE_VARIABLES
		&& inheritedMethod.original().typeVariables != Binding.NO_TYPE_VARIABLES
		&& currentMethod.returnType.erasure().findSuperTypeOriginatingFrom(inheritedMethod.returnType.erasure()) != null) {
			return true;
	}
	return false;
}
boolean reportIncompatibleReturnTypeError(MethodBinding currentMethod, MethodBinding inheritedMethod) {
	if (isUnsafeReturnTypeOverride(currentMethod, inheritedMethod)) {
		problemReporter(currentMethod).unsafeReturnTypeOverride(currentMethod, inheritedMethod, this.type);
		return false;
	}
	return super.reportIncompatibleReturnTypeError(currentMethod, inheritedMethod);
}
void verify() {
	if (this.type.isAnnotationType())
		this.type.detectAnnotationCycle();

	super.verify();
	
	reportRawReferences();

	for (int i = this.type.typeVariables.length; --i >= 0;) {
		TypeVariableBinding var = this.type.typeVariables[i];
		// must verify bounds if the variable has more than 1
		if (var.superInterfaces == Binding.NO_SUPERINTERFACES) continue;
		if (var.superInterfaces.length == 1 && var.superclass.id == TypeIds.T_JavaLangObject) continue;

		this.currentMethods = new HashtableOfObject(0);
		ReferenceBinding superclass = var.superclass();
		if (superclass.kind() == Binding.TYPE_PARAMETER)
			superclass = (ReferenceBinding) superclass.erasure();
		ReferenceBinding[] itsInterfaces = var.superInterfaces();
		ReferenceBinding[] superInterfaces = new ReferenceBinding[itsInterfaces.length];
		for (int j = itsInterfaces.length; --j >= 0;) {
			superInterfaces[j] = itsInterfaces[j].kind() == Binding.TYPE_PARAMETER
				? (ReferenceBinding) itsInterfaces[j].erasure()
				: itsInterfaces[j];
		}
		computeInheritedMethods(superclass, superInterfaces);
		checkTypeVariableMethods(this.type.scope.referenceContext.typeParameters[i]);
	}
}
}
