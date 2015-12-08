package com.google.gwt.user.client.rpc.core.java.util;

import com.google.gwt.user.client.rpc.CustomFieldSerializer;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

import java.util.Optional;

public class Optional_CustomFieldSerializer extends
        CustomFieldSerializer<Optional> {

    public static Optional instantiate(SerializationStreamReader streamReader)
            throws SerializationException {
        return Optional.ofNullable(streamReader.readObject());
    }

    public static void deserialize(SerializationStreamReader streamReader,
                                   Optional instance) throws SerializationException {
        //do nothing
    }

    public static void serialize(SerializationStreamWriter streamWriter,
                                 Optional instance) throws SerializationException {
        streamWriter.writeObject(instance.orElse(null));
    }


    @Override
    public void serializeInstance(SerializationStreamWriter streamWriter,
                                  Optional instance) throws SerializationException {
        serialize(streamWriter, instance);
    }

    @Override
    public void deserializeInstance(SerializationStreamReader streamReader, Optional instance) throws SerializationException {
        //no-op, instantiate did it
    }

    @Override
    public boolean hasCustomInstantiateInstance() {
        return true;
    }

    @Override
    public Optional instantiateInstance(SerializationStreamReader streamReader) throws SerializationException {
        return instantiate(streamReader);
    }
}