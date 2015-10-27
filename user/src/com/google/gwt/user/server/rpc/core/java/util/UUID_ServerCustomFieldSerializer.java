package com.google.gwt.user.server.rpc.core.java.util;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Comparator;
import java.util.UUID;
import java.util.UUID;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.core.java.util.Collection_CustomFieldSerializerBase;
import com.google.gwt.user.client.rpc.core.java.util.UUID_CustomFieldSerializer;
import com.google.gwt.user.server.rpc.ServerCustomFieldSerializer;
import com.google.gwt.user.server.rpc.impl.DequeMap;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamReader;

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
