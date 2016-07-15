/*
 * Copyright 2008 Google Inc.
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
import com.google.gwt.dev.util.collect.HashMap;
import com.google.gwt.dev.util.collect.Lists;
import com.google.gwt.dev.util.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class that initializes the different members of a
 * {@link JDelegatingClassType} from its corresponding base type on demand.
 */
class DelegateMembers extends AbstractMembers {
  /**
   * Implementation note: it is critical that everything in this class is
   * computed as lazily as possible. Many, many more parameterized types, raw
   * types, type bindings, or wilcard types can be created than real classes,
   * and computing anything up front would add runtime overhead and memory.
   */

  private final JClassType baseType;
  private Map<String, JField> fieldMap;
  private JField[] fields;
  private List<JConstructor> lazyConstructors;
  private Map<String, Object> methodMap;
  private Map<String, Object> allMethodMap;
  private JMethod[] methods;
  private JMethod[] allMethods;
  private final Substitution substitution;

  /**
   */
  public DelegateMembers(JDelegatingClassType enclosingType,
      JClassType baseType, Substitution substitution) {
    super(enclosingType);
    this.baseType = baseType;
    this.substitution = substitution;
  }

  @Override
  public JField findField(String name) {
    initFields();
    return fieldMap.get(name);
  }

  @Override
  public JField[] getFields() {
    initFields();
    return fields.length == 0 ? fields : fields.clone();
  }

  @Override
  public JMethod[] getMethods() {
    initMethods();
    if (methods == null) {
      methods = withoutJava8InterfaceMethods(allMethods);
    }
    return methods.length == 0 ? methods : methods.clone();
  }

  @Override
  public JMethod[] getAllMethods() {
    initMethods();
    return allMethods.length == 0 ? allMethods : allMethods.clone();
  }

  @Override
  public JMethod[] getOverloads(String name) {
    initMethods();
    // Compute methodMap lazily based on allMethodMap
    if (methodMap == null) {
      methodMap = Maps.create();
    }
    Object object = methodMap.get(name);
    if (object == null) {
      object = allMethodMap.get(name);
      if (object == null) {
        return TypeOracle.NO_JMETHODS;
      } else {
        methodMap = Maps.put(methodMap, name, object);
      }
    }
    if (object instanceof JMethod) {
      return new JMethod[]{(JMethod) object};
    } else {
      return ((JMethod[]) object).clone();
    }
  }

  @Override
  public JMethod[] getAllOverloads(String name) {
    initMethods();
    Object object = allMethodMap.get(name);
    if (object == null) {
      return TypeOracle.NO_JMETHODS;
    } else if (object instanceof JMethod) {
      return new JMethod[]{(JMethod) object};
    } else {
      return ((JMethod[]) object).clone();
    }
  }

  @Override
  protected void addConstructor(JConstructor ctor) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void addField(JField field) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void addMethod(JMethod method) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected List<JConstructor> doGetConstructors() {
    if (lazyConstructors != null) {
      /*
       * Return if the constructors are being initialized or have been
       * initialized.
       */
      return lazyConstructors;
    }
    lazyConstructors = new ArrayList<JConstructor>();

    JConstructor[] baseCtors = baseType.getConstructors();
    for (JConstructor baseCtor : baseCtors) {
      JConstructor newCtor = new JConstructor(getParentType(), baseCtor);
      initializeParams(baseCtor, newCtor);
      lazyConstructors.add(newCtor);
    }

    return lazyConstructors = Lists.normalize(lazyConstructors);
  }

  @Override
  protected Map<String, JClassType> doGetNestedTypes() {
    // TODO: is this correct?
    return Maps.create();
  }

  private void initFields() {
    if (fields != null) {
      return;
    }
    // Transitively sorted.
    fields = baseType.getFields();
    fieldMap = new HashMap<String, JField>();
    for (int i = 0; i < fields.length; ++i) {
      JField baseField = fields[i];
      JField newField = new JField(getParentType(), baseField);
      newField.setType(substitute(baseField.getType()));
      fields[i] = newField;
      fieldMap.put(newField.getName(), newField);
    }
    fieldMap = Maps.normalize(fieldMap);
  }

  private void initializeExceptions(JAbstractMethod srcMethod,
      JAbstractMethod newMethod) {
    for (JClassType thrown : srcMethod.getThrows()) {
      // exceptions cannot be parameterized; just copy them over
      newMethod.addThrows(thrown);
    }
  }

  private void initializeParams(JAbstractMethod srcMethod,
      JAbstractMethod newMethod) {
    for (JParameter srcParam : srcMethod.getParameters()) {
      JParameter newParam = new JParameter(newMethod, srcParam);
      newParam.setType(substitute(srcParam.getType()));
      newMethod.addParameter(newParam);
    }
  }

  @SuppressWarnings("unchecked")
  private void initMethods() {
    if (allMethods != null) {
      return;
    }
    // Transitively sorted.
    allMethods = baseType.getAllMethods();
    allMethodMap = new HashMap<String, Object>();
    for (int i = 0; i < allMethods.length; ++i) {
      JMethod baseMethod = allMethods[i];
      JMethod newMethod = new JMethod(getParentType(), baseMethod);
      initializeParams(baseMethod, newMethod);
      newMethod.setReturnType(substitute(baseMethod.getReturnType()));
      initializeExceptions(baseMethod, newMethod);
      allMethods[i] = newMethod;

      String methodName = newMethod.getName();
      Object object = allMethodMap.get(methodName);
      if (object == null) {
        allMethodMap.put(methodName, newMethod);
      } else if (object instanceof JMethod) {
        List<JMethod> list = new ArrayList<JMethod>(2);
        list.add((JMethod) object);
        list.add(newMethod);
        allMethodMap.put(methodName, list);
      } else {
        List<JMethod> list = (List<JMethod>) object;
        list.add(newMethod);
      }
    }

    // Replace the ArrayLists with plain arrays.
    for (String methodName : allMethodMap.keySet()) {
      Object object = allMethodMap.get(methodName);
      if (object instanceof List) {
        List<JMethod> list = (List<JMethod>) object;
        allMethodMap.put(methodName, list.toArray(TypeOracle.NO_JMETHODS));
      }
    }
    allMethodMap = Maps.normalize(allMethodMap);
  }

  private JType substitute(JType type) {
    if (type instanceof JClassType) {
      return substitution.getSubstitution((JClassType) type);
    }
    return type;
  }
}
