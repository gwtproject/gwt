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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;

/**
 * A utility to extract the key type of any {@link java.util.EnumMap}, even if it is empty. Some
 * frameworks have tried this by using reflection on the map's private field {@code keyType}, but
 * that approach breaks with Java 17 where reflection on private elements of any {@code java.*}
 * class is forbidden unless the {@code --add-opens} VM argument is used which is a fairly unsafe
 * thing to do.
 * <p>
 * 
 * Use like this: <pre>
 *      private static enum MyBoolean {
 *          TRUE, FALSE
 *      };
 *      EnumMap<MyBoolean, Integer> enumMap = new EnumMap<>(MyBoolean.class);
 *      Class<?> c = EnumMapUtil.getKeyType(enumMap);
 * </pre>
 * <p>
 * 
 * Implementation note: If the map passed to {@link #getKeyType(java.util.EnumMap)} is not empty,
 * the first key is obtained from the {@link EnumMap#keySet() key set} and its type is determined
 * and returned. If the map is empty, it is serialized into an {@link ObjectOutputStream} writing to
 * a {@link ByteArrayOutputStream} from which it is read again with a specialized
 * {@link ObjectInputStream}where the {@code resolveClass} method is overridden. Upon reading a
 * class descriptor for class {@link java.util.EnumMap}, instead of regularly resolving the class
 * descriptor a package-protected class {@link com.google.gwt.user.server.rpc.EnumMap} is returned
 * which has a serial version UID equal to that of {@link java.util.EnumMap} and the same set of
 * fields. By having the same simple name (despite living in a different package) the
 * {@link ObjectInputStream} considers this class to be compatible to {@link java.util.EnumMap} and
 * de-serializes the stream contents into an instance of
 * {@link com.google.gwt.user.server.rpc.EnumMap} which in turn offers a public getter
 * {@link com.google.gwt.user.server.rpc.EnumMap#getKeyType()} for the key type. Through this getter
 * the key type is then determined and returned.
 *
 */
public class EnumMapUtil {
  private static class MyObjectInputStream extends ObjectInputStream {
    public MyObjectInputStream(InputStream in) throws IOException {
      super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,
        ClassNotFoundException {
      Class<?> result = null;
      if (desc.getName().equals("java.util.EnumMap")) {
        result = EnumMap.class;
      } else {
        try {
          result = super.resolveClass(desc);
        } catch (ClassNotFoundException e) {
          result = Class.forName(desc.getName(), false, Thread.currentThread()
              .getContextClassLoader());
        }
      }
      return result;
    }
  }

  public static <K extends Enum<K>, V> Class<K> getKeyType(java.util.EnumMap<K, V> enumMap)
      throws IOException, ClassNotFoundException {
    final Class<K> result;
    if (enumMap.isEmpty()) {
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      final ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(enumMap);
      oos.close();
      final ObjectInputStream ois = new MyObjectInputStream(new ByteArrayInputStream(bos
          .toByteArray()));
      @SuppressWarnings("unchecked")
      final EnumMap<K, V> readMap = (EnumMap<K, V>) ois.readObject();
      final Class<K> keyType = readMap.getKeyType();
      result = keyType;
    } else {
      result = enumMap.keySet().iterator().next().getDeclaringClass();
    }
    return result;
  }
}
