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
package com.google.gwt.core.ext.linker;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.Linker;
import com.google.gwt.dev.util.Util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * A resource created by a {@link Generator} invoking
 * {@link com.google.gwt.core.ext.GeneratorContext#tryCreateResource(com.google.gwt.core.ext.TreeLogger, String)}
 * during the compilation process.
 */
public abstract class GeneratedResource extends EmittedArtifact {
  private String generatorTypeName;
  private transient Class<? extends Generator> generatorType;

  protected GeneratedResource(Class<? extends Linker> linkerType,
      Class<? extends Generator> generatorType, String partialPath) {
    super(linkerType, partialPath);
    this.generatorTypeName = generatorType.getName();
    this.generatorType = generatorType;
  }

  /**
   * The type of Generator that created the resource.
   */
  public final Class<? extends Generator> getGenerator() {
    // generatorType is null when deserialized.
    if (generatorType == null) {
      try {
        Class<?> clazz = Class.forName(generatorTypeName, false,
            Thread.currentThread().getContextClassLoader());
        generatorType = clazz.asSubclass(Generator.class);
      } catch (ClassNotFoundException e) {
        // The class may not be available.
        generatorType = Generator.class;
      }
    }
    return generatorType;
  }

  /**
   * Empty constructor for externalization.
   */
  public GeneratedResource() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    Util.serializeString(generatorTypeName, out);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    generatorTypeName = Util.deserializeString(in);
  }
}
