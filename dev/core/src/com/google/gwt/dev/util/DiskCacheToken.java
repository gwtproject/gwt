/*
 * Copyright 2011 Google Inc.
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
package com.google.gwt.dev.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Wrapper around a {@link DiskCache} token that allows easy serialization.
 */
public class DiskCacheToken implements Externalizable {

  private transient DiskCache diskCache;
  private transient long token;

  /**
   * Create a wrapper for a token associated with {@link DiskCache#INSTANCE}.
   */
  public DiskCacheToken(long token) {
    this(DiskCache.INSTANCE, token);
  }

  /**
   * Create a wrapper for a token associated with the given diskCache.
   */
  DiskCacheToken(DiskCache diskCache, long token) {
    assert token >= 0;
    this.diskCache = diskCache;
    this.token = token;
  }

  /**
   * Retrieve the underlying bytes.
   * 
   * @return the bytes that were written
   */
  public synchronized byte[] readByteArray() {
    return diskCache.readByteArray(token);
  }

  /**
   * Deserialize the underlying bytes as an object.
   * 
   * @param <T> the type of the object to deserialize
   * @param type the type of the object to deserialize
   * @return the deserialized object
   */
  public <T> T readObject(Class<T> type) {
    return diskCache.readObject(token, type);
  }

  /**
   * Read the underlying bytes as a String.
   * 
   * @return the String that was written
   */
  public String readString() {
    return diskCache.readString(token);
  }

  public DiskCacheToken() {
    diskCache = DiskCache.INSTANCE;
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    diskCache.transferToObjectStream(token, out);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    token = diskCache.transferFromObjectStream(in);
  }
}
