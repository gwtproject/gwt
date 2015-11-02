/*
 * Copyright 2015 Google Inc.
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
package com.google.gwt.user.server.rpc.core.java.util;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.core.java.util.UUID_CustomFieldSerializer;
import com.google.gwt.user.server.rpc.ServerCustomFieldSerializer;
import com.google.gwt.user.server.rpc.impl.DequeMap;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamReader;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.UUID;

/**
 * Custom field serializer for {@link java.util.UUID}.
 */
@SuppressWarnings({"unused"})
public class UUID_ServerCustomFieldSerializer extends ServerCustomFieldSerializer<UUID> {

  public static void deserialize(ServerSerializationStreamReader streamReader, UUID instance,
      Type[] expectedParameterTypes, DequeMap<TypeVariable<?>, Type> resolvedTypes) throws
      SerializationException {
 // No fields
  }

  public static UUID instantiate(ServerSerializationStreamReader streamReader,
      Type[] expectedParameterTypes, DequeMap<TypeVariable<?>, Type> resolvedTypes) throws
      SerializationException {
    return UUID_CustomFieldSerializer.instantiate(streamReader);
  }

  @Override
  public void deserializeInstance(SerializationStreamReader streamReader, UUID instance)
      throws SerializationException {
 // No fields
  }

  @Override
  public void deserializeInstance(ServerSerializationStreamReader streamReader, UUID instance,
      Type[] expectedParameterTypes, DequeMap<TypeVariable<?>, Type> resolvedTypes) throws
      SerializationException {
    deserialize(streamReader, instance, expectedParameterTypes, resolvedTypes);
  }

  @Override
  public boolean hasCustomInstantiateInstance() {
    return true;
  }

  @Override
  public UUID instantiateInstance(SerializationStreamReader streamReader)
      throws SerializationException {
    return UUID_CustomFieldSerializer.instantiate(streamReader);
  }

  @Override
  public UUID instantiateInstance(ServerSerializationStreamReader streamReader,
      Type[] expectedParameterTypes, DequeMap<TypeVariable<?>, Type> resolvedTypes) throws
      SerializationException {
    return instantiate(streamReader, expectedParameterTypes, resolvedTypes);
  }

  @Override
  public void serializeInstance(SerializationStreamWriter streamWriter, UUID instance)
      throws SerializationException {
    UUID_CustomFieldSerializer.serialize(streamWriter, instance);
  }

}
