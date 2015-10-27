package com.google.gwt.user.client.rpc.core.java.util;

import java.util.UUID;

import com.google.gwt.user.client.rpc.CustomFieldSerializer;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

public class UUID_CustomFieldSerializer extends CustomFieldSerializer<UUID> {

  @SuppressWarnings("unused")
  public static void deserialize(SerializationStreamReader streamReader,
      UUID instance) {
    // No fields
  }

  public static UUID instantiate(SerializationStreamReader streamReader)
      throws SerializationException {
    return UUID.fromString(streamReader.readString());
  }

  public static void serialize(SerializationStreamWriter streamWriter,
      UUID instance) throws SerializationException {
    streamWriter.writeString(instance.toString());
  }

  @Override
  public void deserializeInstance(SerializationStreamReader streamReader,
      UUID instance) throws SerializationException {
    deserialize(streamReader, instance);
  }

  @Override
  public boolean hasCustomInstantiateInstance() {
    return true;
  }

  @Override
  public UUID instantiateInstance(SerializationStreamReader streamReader)
      throws SerializationException {
    return instantiate(streamReader);
  }

  @Override
  public void serializeInstance(SerializationStreamWriter streamWriter,
      UUID instance) throws SerializationException {
    serialize(streamWriter, instance);
  }

}