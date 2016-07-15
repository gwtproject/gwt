/*
 * Copyright 2007 Google Inc.
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
package com.google.gwt.dev.javac.typemodel;

import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.thirdparty.guava.common.base.Predicate;
import com.google.gwt.thirdparty.guava.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nullable;

abstract class AbstractMembers {

  protected final JClassType classType;
  private JMethod[] cachedInheritableMethods;
  private JMethod[] cachedAllInheritableMethods;
  private JMethod[] cachedOverridableMethods;
  private JMethod[] cachedAllOverridableMethods;

  public AbstractMembers(JClassType classType) {
    this.classType = classType;
  }

  public JConstructor findConstructor(JType[] paramTypes) {
    JConstructor[] ctors = getConstructors();
    for (JConstructor candidate : ctors) {
      if (candidate.hasParamTypes(paramTypes)) {
        return candidate;
      }
    }
    return null;
  }

  public abstract JField findField(String name);

  public JMethod findMethod(String name, JType[] paramTypes) {
    JMethod[] overloads = getAllOverloads(name);
    for (JMethod candidate : overloads) {
      if (candidate.hasParamTypes(paramTypes)) {
        return candidate;
      }
    }
    return null;
  }

  public JClassType findNestedType(String typeName) {
    String[] parts = typeName.split("\\.");
    return findNestedTypeImpl(parts, 0);
  }

  public JConstructor getConstructor(JType[] paramTypes)
      throws NotFoundException {
    JConstructor result = findConstructor(paramTypes);
    if (result == null) {
      throw new NotFoundException();
    }
    return result;
  }

  public JConstructor[] getConstructors() {
    return doGetConstructors().toArray(TypeOracle.NO_JCTORS);
  }

  public JField getField(String name) {
    JField field = findField(name);
    assert (field != null);
    return field;
  }

  public abstract JField[] getFields();

  public JMethod[] getInheritableMethods() {
    if (cachedInheritableMethods == null) {
      cachedInheritableMethods = withoutJava8InterfaceMethods(getAllInheritableMethods());
    }
    return cachedInheritableMethods;
  }

  public JMethod[] getAllInheritableMethods() {
    if (cachedAllInheritableMethods == null) {
      Map<String, JMethod> methodsBySignature = new TreeMap<String, JMethod>();
      getInheritableMethodsOnSuperinterfacesAndMaybeThisInterface(methodsBySignature);
      if (classType.isClass() != null) {
        getInheritableMethodsOnSuperclassesAndThisClass(methodsBySignature);
      }
      int size = methodsBySignature.size();
      if (size == 0) {
        cachedAllInheritableMethods = TypeOracle.NO_JMETHODS;
      } else {
        Collection<JMethod> leafMethods = methodsBySignature.values();
        cachedAllInheritableMethods = leafMethods.toArray(new JMethod[size]);
      }
    }
    return cachedAllInheritableMethods;
  }

  public JMethod getMethod(String name, JType[] paramTypes)
      throws NotFoundException {
    JMethod result = findMethod(name, paramTypes);
    if (result == null) {
      throw new NotFoundException();
    }
    return result;
  }

  public abstract JMethod[] getMethods();

  public abstract JMethod[] getAllMethods();

  public JClassType getNestedType(String typeName) throws NotFoundException {
    JClassType result = findNestedType(typeName);
    if (result == null) {
      throw new NotFoundException();
    }
    return result;
  }

  public JClassType[] getNestedTypes() {
    return doGetNestedTypes().values().toArray(TypeOracle.NO_JCLASSES);
  }

  public abstract JMethod[] getOverloads(String name);

  public abstract JMethod[] getAllOverloads(String name);

  public JMethod[] getOverridableMethods() {
    if (cachedOverridableMethods == null) {
      cachedOverridableMethods = withoutJava8InterfaceMethods(getAllOverridableMethods());
    }
    return cachedOverridableMethods;
  }

  public JMethod[] getAllOverridableMethods() {
    if (cachedAllOverridableMethods == null) {
      JMethod[] inheritableMethods = getAllInheritableMethods();
      ArrayList<JMethod> methods = new ArrayList<JMethod>(
          inheritableMethods.length);
      for (JMethod method : inheritableMethods) {
        if (!method.isFinal()) {
          methods.add(method);
        }
      }
      int size = methods.size();
      if (size == 0) {
        cachedAllOverridableMethods = TypeOracle.NO_JMETHODS;
      } else {
        cachedAllOverridableMethods = methods.toArray(new JMethod[size]);
      }
    }
    return cachedAllOverridableMethods;
  }

  protected abstract void addConstructor(JConstructor ctor);

  protected abstract void addField(JField field);

  protected abstract void addMethod(JMethod method);

  protected abstract List<JConstructor> doGetConstructors();

  protected abstract Map<String, JClassType> doGetNestedTypes();

  protected JClassType findNestedTypeImpl(String[] typeName, int index) {
    JClassType found = doGetNestedTypes().get(typeName[index]);
    if (found == null) {
      return null;
    } else if (index < typeName.length - 1) {
      return found.findNestedTypeImpl(typeName, index + 1);
    } else {
      return found;
    }
  }

  protected void getInheritableMethodsOnSuperclassesAndThisClass(
      Map<String, JMethod> methodsBySignature) {
    assert (classType.isClass() != null);

    // Recurse first so that more derived methods will clobber less derived
    // methods.
    JClassType superClass = classType.getSuperclass();
    if (superClass != null) {
      superClass.getInheritableMethodsOnSuperclassesAndThisClass(methodsBySignature);
    }

    JMethod[] declaredMethods = getAllMethods();
    for (JMethod method : declaredMethods) {
      // Ensure that this method is inheritable.
      if (method.isPrivate() || method.isStatic()) {
        // We cannot inherit this method, so skip it.
        continue;
      }

      // We can override this method, so record it.
      String sig = computeInternalSignature(method);
      methodsBySignature.put(sig, method);
    }
  }

  /**
   * Gets the methods declared in interfaces that this type extends. If this
   * type is a class, its own methods are not added. If this type is an
   * interface, its own methods are added. Used internally by
   * {@link #getAllOverridableMethods()}.
   *
   * @param methodsBySignature
   */
  protected void getInheritableMethodsOnSuperinterfacesAndMaybeThisInterface(
      Map<String, JMethod> methodsBySignature) {
    // Recurse first so that more derived methods will clobber less derived
    // methods.
    JClassType[] superIntfs = classType.getImplementedInterfaces();
    for (JClassType superIntf : superIntfs) {
      superIntf.getInheritableMethodsOnSuperinterfacesAndMaybeThisInterface(methodsBySignature);
    }

    if (classType.isInterface() == null) {
      // This is not an interface, so we're done after having visited its
      // implemented interfaces.
      return;
    }

    JMethod[] declaredMethods = getAllMethods();
    for (JMethod method : declaredMethods) {
      String sig = computeInternalSignature(method);
      JMethod existing = methodsBySignature.get(sig);
      if (existing != null) {
        JClassType existingType = existing.getEnclosingType();
        JClassType thisType = method.getEnclosingType();
        if (thisType.isAssignableFrom(existingType)) {
          // The existing method is in a more-derived type, so don't replace it.
          continue;
        }
      }
      methodsBySignature.put(sig, method);
    }
  }

  protected JClassType getParentType() {
    return classType;
  }

  protected boolean isJava8InterfaceMethod(JMethod method) {
    assert classType.isInterface() != null
        : "isJava8InterfaceMethod helper should not be called for non-interface classes";
    return !method.isAbstract();
  }

  // Tries to minimize allocations
  protected JMethod[] withoutJava8InterfaceMethods(JMethod[] methods) {
    if (classType.isInterface() == null) {
      return methods;
    }

    int java8InterfaceMethods = 0;
    for (JMethod method : methods) {
      if (isJava8InterfaceMethod(method)) {
        java8InterfaceMethods++;
      }
    }
    if (java8InterfaceMethods == 0) {
      return methods;
    }
    if (java8InterfaceMethods == methods.length) {
      return TypeOracle.NO_JMETHODS;
    }

    JMethod[] result = new JMethod[methods.length - java8InterfaceMethods];
    int i = 0;
    for (JMethod method : methods) {
      if (!isJava8InterfaceMethod(method)) {
        result[i++] = method;
      }
    }
    return result;
  }

  /**
   * Returns a filtered view of methods without the Java 8 interface methods.
   */
  protected Collection<JMethod> withoutJava8InterfaceMethods(Collection<JMethod> methods) {
    if (classType.isInterface() == null) {
      return methods;
    }
    return Collections2.filter(methods, new Predicate<JMethod>() {
      @Override
      public boolean apply(@Nullable JMethod method) {
        return isJava8InterfaceMethod(method);
      }
    });
  }

  private String computeInternalSignature(JMethod method) {
    StringBuilder sb = new StringBuilder();
    sb.setLength(0);
    sb.append(method.getName());
    JParameter[] params = method.getParameters();
    for (JParameter param : params) {
      sb.append("/");
      sb.append(param.getType().getErasedType().getQualifiedSourceName());
    }
    return sb.toString();
  }
}
