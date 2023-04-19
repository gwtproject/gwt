/*
 * Copyright 2022 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.user.server.rpc;

/**
 * A serialization-compatible mock for {@link java.util.EnumMap} that uses an equal
 * {@link #serialVersionUID} and a compatible set of fields. When de-serializing an
 * {@link java.util.EnumMap} instance into an instance of this class, e.g., by overriding
 * {@link ObjectInputStream}'s {@link ObjectInputStream#resolveClass} method such that it delivers
 * this class instead of {@link java.util.EnumMap}, the fields are made accessible through getters;
 * in particular, {@link #getKeyType()} reveals the original {@link java.util.EnumMap}'s key type,
 * even if the map is empty.
 * <p>
 * 
 * The {@link EnumMap#getEnumMapKeyType(java.util.EnumMap)} method can be used to determine the key
 * type of any {@link java.util.EnumMap}, even if it's empty.
 */
class EnumMap<K extends Enum<K>, V> extends java.util.EnumMap<K, V> {
  private final Class<K> keyType;
  private transient K[] keyUniverse;
  private transient Object[] vals;
  private transient int size = 0;
  private static final long serialVersionUID = 458661240069192865L;

  EnumMap(Class<K> c) {
    super(c);
    keyType = null;
  }

  public K[] getKeyUniverse() {
    return keyUniverse;
  }

  public Object[] getVals() {
    return vals;
  }

  public int getSize() {
    return size;
  }

  public Class<K> getKeyType() {
    return keyType;
  }

  /**
   * Reconstitute the <tt>EnumMap</tt> instance from a stream (i.e., deserialize it).
   */
  private void readObject(java.io.ObjectInputStream s) throws java.io.IOException,
      ClassNotFoundException {
    // Read in the key type and any hidden stuff
    s.defaultReadObject();
    // Read in size (number of Mappings)
    int numberOfMappings = s.readInt();
    // Read the keys and values, and put the mappings in the HashMap
    for (int i = 0; i < numberOfMappings; i++) {
      s.readObject(); // key
      s.readObject(); // value
    }
  }
}
