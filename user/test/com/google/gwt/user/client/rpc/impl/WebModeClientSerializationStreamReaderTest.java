package com.google.gwt.user.client.rpc.impl;

import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.user.client.rpc.RpcTestBase;
import com.google.gwt.user.client.rpc.SerializationException;

/**
 * Tests the super sourced version for {@link ClientSerializationStreamReader}. For devmode version
 * see {@link ClientSerializationStreamReaderTest}
 */
@DoNotRunWith(Platform.Devel)
public class WebModeClientSerializationStreamReaderTest extends RpcTestBase {

  public void testParsingVersion8() throws SerializationException {

    ClientSerializationStreamReader reader = new ClientSerializationStreamReader(null);

    String encoded = "["
        + "\"NaN\"," // a stringified double
        + "\"Infinity\"," // a stringified double
        + "\"-Infinity\"," // a stringified double
        + "0," // flags
        + "8" // version
        + "]";

    assertEquals(8, readVersion(encoded));

    reader.prepareToRead(encoded);

    assertEquals(8, reader.getVersion());

    assertTrue(Double.isInfinite(reader.readDouble()));
    assertTrue(Double.isInfinite(reader.readDouble()));
    assertTrue(Double.isNaN(reader.readDouble()));
  }

  public void testParsingVersion7() throws SerializationException {

    ClientSerializationStreamReader reader = new ClientSerializationStreamReader(null);

    String encoded = "["
        + "NaN," // a double
        + "Infinity," // a double
        + "-Infinity," // a double
        + "0," // flags
        + "7" // version
        + "]";

    assertEquals(7, readVersion(encoded));

    reader.prepareToRead(encoded);

    assertEquals(7, reader.getVersion());

    assertTrue(Double.isInfinite(reader.readDouble()));
    assertTrue(Double.isInfinite(reader.readDouble()));
    assertTrue(Double.isNaN(reader.readDouble()));
  }

  private native int readVersion(String encoded)/*-{
    return @com.google.gwt.user.client.rpc.impl.ClientSerializationStreamReader::readVersion(Ljava/lang/String;)(encoded);
  }-*/;
}
