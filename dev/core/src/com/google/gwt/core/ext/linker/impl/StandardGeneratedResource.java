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
package com.google.gwt.core.ext.linker.impl;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.GeneratedResource;
import com.google.gwt.dev.util.DiskCache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * The standard implementation of {@link GeneratedResource}.
 */
public class StandardGeneratedResource extends GeneratedResource {
  private static final DiskCache diskCache = DiskCache.INSTANCE;

  private final long lastModified = System.currentTimeMillis();

  private transient long token;

  public StandardGeneratedResource(Class<? extends Generator> generatorType,
      String partialPath, byte[] data) {
    super(StandardLinkerContext.class, generatorType, partialPath);
    this.token = diskCache.writeByteArray(data);
  }

  @Override
  public InputStream getContents(TreeLogger logger)
      throws UnableToCompleteException {
    return new ByteArrayInputStream(diskCache.readByteArray(token));
  }

  public long getLastModified() {
    return lastModified;
  }

  @Override
  public void writeTo(TreeLogger logger, OutputStream out)
      throws UnableToCompleteException {
    try {
      diskCache.transferToStream(token, out);
    } catch (IOException e) {
      logger.log(TreeLogger.ERROR, "Unable to copy artifact: " + getPartialPath(), e);
      throw new UnableToCompleteException();
    }
  }

  private void readObject(ObjectInputStream stream) throws IOException,
      ClassNotFoundException {
    stream.defaultReadObject();
    token = diskCache.transferFromStream(stream);
  }

  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    diskCache.transferToStream(token, stream);
  }
}
