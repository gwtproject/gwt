/*
 * Copyright 2010 Google Inc.
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
package com.google.gwt.core.ext.typeinfo;

import static com.google.gwt.core.ext.typeinfo.JniConstants.DESC_BOOLEAN;
import static com.google.gwt.core.ext.typeinfo.JniConstants.DESC_BYTE;
import static com.google.gwt.core.ext.typeinfo.JniConstants.DESC_CHAR;
import static com.google.gwt.core.ext.typeinfo.JniConstants.DESC_DOUBLE;
import static com.google.gwt.core.ext.typeinfo.JniConstants.DESC_FLOAT;
import static com.google.gwt.core.ext.typeinfo.JniConstants.DESC_INT;
import static com.google.gwt.core.ext.typeinfo.JniConstants.DESC_LONG;
import static com.google.gwt.core.ext.typeinfo.JniConstants.DESC_SHORT;
import static com.google.gwt.core.ext.typeinfo.JniConstants.DESC_VOID;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a primitive type in a declaration.
 */
public enum JPrimitiveType implements JType {
  BOOLEAN("boolean", "Boolean", DESC_BOOLEAN, "false"), //
  BYTE("byte", "Byte", DESC_BYTE, "0"), //
  CHAR("char", "Character", DESC_CHAR, "0"), //
  DOUBLE("double", "Double", DESC_DOUBLE, "0d"), //
  FLOAT("float", "Float", DESC_FLOAT, "0f"), //
  INT("int", "Integer", DESC_INT, "0"), //
  LONG("long", "Long", DESC_LONG, "0L"), //
  SHORT("short", "Short", DESC_SHORT, "0"), //
  VOID("void", "Void", DESC_VOID, "null");

  /**
   * Lazy-initialized map of Java identifier name to enum values.
   */
  private static class NameMap {
    static final Map<String, JPrimitiveType> map = new HashMap<String, JPrimitiveType>();
    static {
      for (JPrimitiveType type : JPrimitiveType.values()) {
        map.put(type.getSimpleSourceName(), type);
      }
    }
  }

  public static JPrimitiveType parse(String name) {
    return NameMap.map.get(name);
  }

  private final String boxedName;

  private final String defaultValue;

  private final String jni;

  private final String name;

  private JPrimitiveType(String name, String boxedName, char jni,
      String defaultValue) {
    this.name = name;
    this.boxedName = boxedName;
    this.jni = String.valueOf(jni);
    this.defaultValue = defaultValue;
  }

  @Override
  public JType getErasedType() {
    return this;
  }

  @Override
  public String getJNISignature() {
    return jni;
  }

  @Override
  public JType getLeafType() {
    return this;
  }

  @Override
  public String getParameterizedQualifiedSourceName() {
    return name;
  }

  @Override
  public String getQualifiedBinaryName() {
    return name;
  }

  public String getQualifiedBoxedSourceName() {
    return "java.lang." + boxedName;
  }

  @Override
  public String getQualifiedSourceName() {
    return name;
  }

  @Override
  public String getSimpleSourceName() {
    return name;
  }

  public String getUninitializedFieldExpression() {
    return defaultValue;
  }

  @Override
  public JAnnotationType isAnnotation() {
    return null;
  }

  @Override
  public JArrayType isArray() {
    return null;
  }

  @Override
  public JClassType isClass() {
    return null;
  }

  @Override
  public JClassType isClassOrInterface() {
    return null;
  }

  @Override
  public JEnumType isEnum() {
    return null;
  }

  @Override
  public JGenericType isGenericType() {
    return null;
  }

  @Override
  public JClassType isInterface() {
    return null;
  }

  @Override
  public JParameterizedType isParameterized() {
    return null;
  }

  @Override
  public JPrimitiveType isPrimitive() {
    return this;
  }

  @Override
  public JRawType isRawType() {
    return null;
  }

  @Override
  public JTypeParameter isTypeParameter() {
    return null;
  }

  @Override
  public JWildcardType isWildcard() {
    return null;
  }

  @Override
  public String toString() {
    return name;
  }
}
