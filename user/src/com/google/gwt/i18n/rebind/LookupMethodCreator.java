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
package com.google.gwt.i18n.rebind;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.i18n.rebind.AbstractResource.ResourceList;
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.user.rebind.AbstractGeneratorClassCreator;
import com.google.gwt.user.rebind.AbstractMethodCreator;
import com.google.gwt.user.rebind.AbstractSourceCreator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Method creator to call the correct Map for the given Dictionary.
 */
class LookupMethodCreator extends AbstractMethodCreator {

  /**
   * Used partition size if no one is specified.
   * 
   * Used in constructor without a partition size.
   */
  private static final int DEFAULT_PARTITIONS_SIZE = 500;

  private final JType returnType;

  private final int partitionsSize;

  /**
   * Constructor for <code>LookupMethodCreator</code>. The default partition size of
   * {@value #DEFAULT_PARTITIONS_SIZE} is used.
   *
   * @param classCreator parent class creator
   * @param returnType associated return type
   * 
   * @see LookupMethodCreator#DEFAULT_PARTITIONS_SIZE
   */
  public LookupMethodCreator(AbstractGeneratorClassCreator classCreator, JType returnType) {
    this(classCreator, returnType, DEFAULT_PARTITIONS_SIZE);
  }

  /**
   * Constructor for <code>LookupMethodCreator</code>.
   *
   * @param classCreator parent class creator
   * @param returnType associated return type
   * @param partitionsSize max numbers of lookups per method.
   */
  public LookupMethodCreator(AbstractGeneratorClassCreator classCreator, JType returnType,
      int partitionsSize) {
    super(classCreator);
    this.returnType = returnType;
    this.partitionsSize = partitionsSize;
  }

  @Override
  public void createMethodFor(TreeLogger logger, JMethod targetMethod, String key,
      ResourceList resourceList, GwtLocale locale) {
    createMethodFor(targetMethod);
  }

  /**
   * Returns a {@code String} containing the return type name.
   */
  protected String getReturnTypeName() {
    String type;
    JPrimitiveType s = returnType.isPrimitive();
    if (s != null) {
      type = AbstractSourceCreator.getJavaObjectTypeFor(s);
    } else {
      type = returnType.getParameterizedQualifiedSourceName();
    }
    return type;
  }

  void createMethodFor(JMethod targetMethod) {
    String template = "{0} target = ({0}) cache.get(arg0);";
    String returnTypeName = getReturnTypeName();
    String lookup = MessageFormat.format(template, new Object[] {returnTypeName});
    println(lookup);
    println("if (target != null) {");
    indent();
    printReturnTarget();
    outdent();
    println("}");

    List<JMethod> allMethodsToCreate = findMethodsToCreate(targetMethod, returnType);
    List<List<JMethod>> methodPartitions = Lists.partition(allMethodsToCreate, partitionsSize);
    Iterator<List<JMethod>> methodsIterator = methodPartitions.iterator();

    int partitionIndex = 0;
    while (methodsIterator.hasNext()) {
      List<JMethod> methodsToCreate = methodsIterator.next();
      createMethodLookups(methodsToCreate);
      if (methodsIterator.hasNext()) {
        String partitionMethodName = createPartitionMethodName(targetMethod, partitionIndex++);
        printFound(partitionMethodName);
        outdent();
        println("}");
        createPartitionLookup(partitionMethodName, targetMethod);
      }
    }

    String format = "throw new java.util.MissingResourceException(\"Cannot find constant ''\" +"
        + "{0} + \"''; expecting a method name\", \"{1}\", {0});";
    String result = MessageFormat.format(format, "arg0", 
        this.currentCreator.getTarget().getQualifiedSourceName());
    println(result);
  }

  void createMethodLookups(List<JMethod> methodsToCreate) {
    for (JMethod methodToCreate : methodsToCreate) {
      String methodName = methodToCreate.getName();
      String body = "if(arg0.equals(" + wrap(methodName) + ")) {";
      println(body);
      indent();
      printFound(methodName);
      outdent();
      println("}");
    }
  }

  void createPartitionLookup(String partitionMethodName, JMethod targetMethod) {
    println("");
    final String templatePartitionMethodName = "private {0} {1}({2} arg0) '{";
    final String argument0Type = targetMethod.getParameterTypes()[0].getQualifiedSourceName();
    String partitionMethodSignature = MessageFormat.format(templatePartitionMethodName,
        new Object[] {getReturnTypeName(), partitionMethodName, argument0Type});
    println(partitionMethodSignature);
    indent();
  }

  String createPartitionMethodName(JMethod targetMethod, int partitionIndex) {
    final String templatePartitionMethodName = "{1}FromPartition{2}";
    final String argument0Type = targetMethod.getParameterTypes()[0].getQualifiedSourceName();
    return MessageFormat.format(templatePartitionMethodName, new Object[] {
        getReturnTypeName(), targetMethod.getName(), partitionIndex, argument0Type});
  }

  List<JMethod> findMethodsToCreate(JMethod targetMethod, JType methodReturnType) {
    JMethod[] allMethods = ((ConstantsWithLookupImplCreator) currentCreator).allInterfaceMethods;
    JType erasedType = methodReturnType.getErasedType();
    List<JMethod> methodsToCreate = new ArrayList<>();
    for (JMethod methodToCheck : allMethods) {
      if (methodToCheck.getReturnType().getErasedType().equals(erasedType)
          && methodToCheck != targetMethod) {
        methodsToCreate.add(methodToCheck);
      }
    }
    return methodsToCreate;
  }

  void printFound(String methodName) {
    println(MessageFormat.format(returnTemplate(), new Object[] {methodName}));
  }

  void printLookup(String methodName) {
    String body = "if(arg0.equals(" + wrap(methodName) + ")) {";
    println(body);
    indent();
    printFound(methodName);
    outdent();
    println("}");
  }

  void printReturnTarget() {
    println("return target;");
  }

  String returnTemplate() {
    return "return {0}();";
  }
}
