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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JDeclaredType;
import com.google.gwt.dev.jjs.ast.JInterfaceType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JType;
import com.google.gwt.dev.util.StringInterner;
import com.google.gwt.thirdparty.guava.common.base.Predicate;
import com.google.gwt.thirdparty.guava.common.base.Predicates;
import com.google.gwt.thirdparty.guava.common.collect.FluentIterable;
import com.google.gwt.thirdparty.guava.common.collect.Iterables;
import com.google.gwt.thirdparty.guava.common.collect.LinkedHashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides stubs or forwarding methods to implement default interface methods, and to account for
 * accidental overrides.
 */
public class ComputeOverridesAndImplementDefaultMethods {
  // Maps signatures to methods at each type, package private methods have the package prepended
  // to the signature to represent classes that have multiple package private methods with the
  // same signatures declared at classes in different packages.
  Map<JType, Map<String, JMethod>> polymorphicMethodsByExtendedSignatureByType =
      Maps.newLinkedHashMap();
  Map<JMethod, JMethod> defaultMethodsByForwardingMethod = Maps.newHashMap();

  /**
   * Returns the methods created by this pass. These methods are created due to default
   * declarations or due to accidental overrides.
   */
  public List<JMethod> exec(JProgram program) {
    List<JMethod> newlyCreateMethods = Lists.newArrayList();
    for (JDeclaredType type : program.getDeclaredTypes()) {
      if (!(type instanceof JClassType)) {
        continue;
      }
      computeOverrides(type, newlyCreateMethods);
    }
    return newlyCreateMethods;
  }

  /**
   * Compute all overrides and accumulate newly created methods.
   * <p>
   * Every method that is dispatchable at type {@code type} will be recorded in
   * {@code polymorphicMethodsByExtendedSignatureByType}. Package private method will have the
   * package qualified name prepended to the signature.
   * <p>
   * NOTE: {@code polymorphicMethodsByExtendedSignatureByType} is Map and not a multimap to
   * distinguish between the absence of a type and a type with no methods.
   * The absence of a type means that the type has not been processed yet.
   */
  private void computeOverrides(JDeclaredType type, List<JMethod> newlyCreatedMethods) {
    if (type == null || polymorphicMethodsByExtendedSignatureByType.containsKey(type)) {
      // Already computed.
      return;
    }

    // Compute overrides of all superclasses recursively.
    JClassType superClass = type.getSuperClass();
    computeOverrides(superClass, newlyCreatedMethods);
    for (JInterfaceType implementedInterface : type.getImplements()) {
      computeOverrides(implementedInterface, newlyCreatedMethods);
    }

    // At this point we can assume that the override computation for superclasses and
    // superinterfaces is correct and all their synthetic virtual override forwarding stubs are
    // in place

    // Initialize the entries for the current types with its super and declared polymorphic
    // methods.
    Map<String, JMethod> polymorphicMethodsByExtendedSignature = Maps.newLinkedHashMap();
    if (polymorphicMethodsByExtendedSignatureByType.containsKey(type.getSuperClass())) {
      polymorphicMethodsByExtendedSignature
          .putAll(polymorphicMethodsByExtendedSignatureByType.get(type.getSuperClass()));
    }

    // Compute the override relation concerning only classes.
    for (JMethod method : type.getMethods()) {
      String extendedSignature = getExtendedSignature(method);
      if (extendedSignature != null) {
        JMethod overridenMethod = polymorphicMethodsByExtendedSignature.get(extendedSignature);
        if (overridenMethod == null && !method.isPackagePrivate()) {
          // if the method is not package private, check whether it overrides a package private
          // method.
          String packagePrivateSignature = getPackagePrivateSignature(method);
          JMethod packagePrivateOverridenMethod =
              polymorphicMethodsByExtendedSignature.get(packagePrivateSignature);
          if (packagePrivateOverridenMethod != null) {
            // Overrides a package private method and makes it public.
            addOverridingMethod(packagePrivateOverridenMethod, method);
            polymorphicMethodsByExtendedSignature.put(packagePrivateSignature, method);
          }
        } else if (overridenMethod != null) {
          addOverridingMethod(overridenMethod, method);
        }
        polymorphicMethodsByExtendedSignature.put(extendedSignature, method);
      }
    }
    polymorphicMethodsByExtendedSignatureByType.put(type, polymorphicMethodsByExtendedSignature);

    // Find all interface methods, if there is a default implementations it will be first.

    Multimap<String, JMethod> interfaceMethodsBySignature
        = collectLeafSuperInterfaceMethodsBySignature(type);

    // Compute interface overrides, fix accidental overrides and implement default methods.
    for (String signature : interfaceMethodsBySignature.keySet()) {
      Collection<JMethod> interfaceMethods = interfaceMethodsBySignature.get(signature);
      JMethod baseImplementingMethod = polymorphicMethodsByExtendedSignature.get(signature);
      JMethod implementingMethod = baseImplementingMethod;
      if (implementingMethod == null || implementingMethod.getEnclosingType() != type) {
        implementingMethod = maybeAddSynteticOverride(type, implementingMethod, interfaceMethods);
        if (implementingMethod == null) {
          assert type instanceof JInterfaceType;
          assert interfaceMethods.size() == 1;
          polymorphicMethodsByExtendedSignature.put(signature, interfaceMethods.iterator().next());
          continue;
        }
        newlyCreatedMethods.add(implementingMethod);
      }
      if (implementingMethod.getEnclosingType() == type) {
        for (JMethod interfaceMethod : interfaceMethods) {
          addOverridingMethod(interfaceMethod, implementingMethod);
        }
      }
    }
  }

  /**
   * Adds overridden/overriding information to the corresponding JMethods.
   */
  private void addOverridingMethod(JMethod overriddenMethod, JMethod overridingMethod) {
    assert overriddenMethod != overridingMethod : overriddenMethod + " can not override itself";
    overridingMethod.addOverriddenMethod(overriddenMethod);
    overriddenMethod.addOverridingMethod(overridingMethod);
    for (JMethod transitivelyOverriddenMethod : overriddenMethod.getOverriddenMethods()) {

      overridingMethod.addOverriddenMethod(transitivelyOverriddenMethod);
      transitivelyOverriddenMethod.addOverridingMethod(overridingMethod);
    }
  }

  /**
   * Returns a unique extended signature for the method. An extended signature is the method
   * signature if the method is public; otherwise if the method is package private the extended
   * signature is the method signature prepended the package.
   * <p>
   * Allows to represent package private dispatch when unrelated package private methods have the
   * same signature.
   */
  private String getExtendedSignature(JMethod method) {
    if (!method.canBePolymorphic()) {
      return null;
    }
    if (method.isPackagePrivate()) {
      return getPackagePrivateSignature(method);
    }
    return method.getSignature();
  }

  /**
   * Returns the signature of {@code method} as if {@code method} was package private.
   */
  private String getPackagePrivateSignature(JMethod method) {
    String packageName = method.getEnclosingType().getPackageName();
    return StringInterner.get().intern(packageName + "." + method.getSignature());
  }
  /**
   * Adds a synthetic override if needed.
   * <p>
   * This is used for two main reasons:
   * <ul>
   *   <li>1. to add a concrete implementation for a default method</li>
   *   <li>2. to add a virtual override to account more precisely for accidental overrides</li>
   * </ul>
   */
  private JMethod maybeAddSynteticOverride(
      JDeclaredType type, JMethod baseImplementingMethod, Collection<JMethod> interfaceMethods) {

    // If there is a default implementation it will be first.
    JMethod interfaceMethod = interfaceMethods.iterator().next();
    assert !interfaceMethod.isStatic();

    JMethod implementingMethod = baseImplementingMethod;

    // Only populate classes with stubs, forwarding methods or default implementations.
    if (type instanceof JClassType &&
        interfaceMethod.isDefaultMethod() && (baseImplementingMethod == null ||
        baseImplementingMethod.isDefaultMethod() &&
            defaultMethodsByForwardingMethod.keySet().contains(baseImplementingMethod) &&
            defaultMethodsByForwardingMethod.get(baseImplementingMethod) != interfaceMethod)) {

      assert FluentIterable.from(interfaceMethods).filter(new Predicate<JMethod>() {
        @Override
        public boolean apply(JMethod jMethod) {
          return jMethod.isDefaultMethod();
        }
      }).size() == 1 : "Ambiguous default method resolution for class " + type.getName() +
          " conflicting methods " +
          Iterables.toString(FluentIterable.from(interfaceMethods).filter(
              new Predicate<JMethod>() {
                @Override
                public boolean apply(JMethod jMethod) {
                  return jMethod.isDefaultMethod();
                }
              }));

      implementingMethod = JjsUtils.createForwardingMethod(type, interfaceMethod);

      defaultMethodsByForwardingMethod.put(implementingMethod, interfaceMethod);
    } else if (baseImplementingMethod == null && interfaceMethod.isAbstract() &&
        (type instanceof JClassType || interfaceMethods.size() > 1)) {
      // It is an abstract stub
      implementingMethod = JjsUtils.createSyntheticAbstractStub(type, interfaceMethod);
    } else if (type instanceof JClassType && baseImplementingMethod.getEnclosingType() != type &&
        !FluentIterable.from(interfaceMethods)
            .allMatch(Predicates.in(baseImplementingMethod.getOverriddenMethods()))) {
        // the implementing method does not override all interface declared methods with the same
        // signature.
      if (baseImplementingMethod.isAbstract()) {
        implementingMethod = JjsUtils.createSyntheticAbstractStub(type, interfaceMethod);
      } else {
        implementingMethod = JjsUtils.createForwardingMethod(type, baseImplementingMethod);
        if (baseImplementingMethod.isFinal()) {
          // To keep consistency we reset the final mark
          baseImplementingMethod.setFinal(false);
        }
      }
    }

    if (implementingMethod != null) {
      polymorphicMethodsByExtendedSignatureByType.get(type)
          .put(implementingMethod.getSignature(), implementingMethod);

      if (baseImplementingMethod != null && baseImplementingMethod != implementingMethod) {
        addOverridingMethod(baseImplementingMethod, implementingMethod);
      }
    }

    return implementingMethod;
  }

  /**
   * Collects all interface methods at by signature so that (1) methods in the final set do not
   * have overrides in the set, and (2) if there is a default implementation for a signature, it
   * appears first.
   * <p>
   * NOTE: There should not be any ambiguity (e.g. to conflicting defaults), those cases should
   * have been a compilation error in JDT.
   */
  private Multimap<String, JMethod> collectLeafSuperInterfaceMethodsBySignature(
      JDeclaredType type) {

    Multimap<String, JMethod> interfaceMethodsBySignature = LinkedHashMultimap.create();
    collectAllSuperInterfaceMethodsBySignature(type, interfaceMethodsBySignature);

    List<String> signatures = Lists.newArrayList(interfaceMethodsBySignature.keySet());
    for (String signature : signatures) {
      Collection<JMethod> allMethods = interfaceMethodsBySignature.get(signature);
      Set<JMethod> notOverriddenMethods  = Sets.newLinkedHashSet(allMethods);
      for (JMethod method : allMethods) {
        notOverriddenMethods =
            Sets.difference(notOverriddenMethods, method.getOverriddenMethods());
      }
      Set<JMethod> defaultMethods = FluentIterable.from(notOverriddenMethods).filter(
          new Predicate<JMethod>() {
            @Override
            public boolean apply(JMethod method) {
              return method.isDefaultMethod();
            }
          }).toSet();
      Set<JMethod> leafMethods = Sets.newLinkedHashSet(defaultMethods);
      leafMethods.addAll(notOverriddenMethods);
      interfaceMethodsBySignature.replaceValues(signature, leafMethods);
    }

    return interfaceMethodsBySignature;
  }

  private void collectAllSuperInterfaceMethodsBySignature(JDeclaredType type,
      Multimap<String, JMethod> methodsBySignature) {

    for (JDeclaredType superType: getAllImmediateSupers(type)) {
      if (!(superType instanceof JInterfaceType)) {
        continue;
      }
      for (JMethod method : polymorphicMethodsByExtendedSignatureByType.get(superType).values()) {
        if (method.canBePolymorphic()) {
          methodsBySignature.put(getExtendedSignature(method), method);
        }
      }
    }
  }

  private Iterable<JDeclaredType> getAllImmediateSupers(JDeclaredType type) {
    if (type == null) {
      return FluentIterable.from(Collections.<JDeclaredType>emptyList());
    }
    return FluentIterable.from(Collections.<JDeclaredType>singleton(type.getSuperClass()))
        .append(type.getImplements())
        .filter(Predicates.notNull());
  }
}
