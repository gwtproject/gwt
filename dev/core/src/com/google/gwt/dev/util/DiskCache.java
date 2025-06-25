/*
 * Copyright 2009 Google Inc.
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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * A nifty class that lets you squirrel away data on the file system. Write
 * once, read many times. Instance of this are thread-safe by way of internal
 * synchronization.
 *
 * Note that in the current implementation, the backing temp file will get
 * arbitrarily large as you continue adding things to it. There is no internal
 * GC or compaction.
 */
public class DiskCache {
  /**
   * For future thought: if we used Object tokens instead of longs, we could
   * actually track references and do GC/compaction on the underlying file.
   *
   * I considered using memory mapping, but I didn't see any obvious way to make
   * the map larger after the fact, which kind of defeats the infinite-append
   * design. At any rate, I measured the current performance of this design to
   * be so fast relative to what I'm using it for, I didn't pursue this further.
   */

  /**
   * The size of a {@link #threadLocalBuf}, which should be large enough for
   * efficient data transfer but small enough to fit easily into the L2 cache of
   * most modern processors.
   */
  private static final int THREAD_LOCAL_BUF_SIZE = 16 * 1024;

  /**
   * Stores reusable thread local buffers for efficient data transfer.
   */
  private static final ThreadLocal<byte[]> threadLocalBuf = ThreadLocal.withInitial(() -> new byte[THREAD_LOCAL_BUF_SIZE]);

  /**
   * A global shared Disk cache.
   */
  public static DiskCache INSTANCE = new DiskCache();

  private boolean atEnd = true;
  private final RandomAccessFile file;

  private DiskCache() {
    try {
      File temp = File.createTempFile("gwt", "byte-cache");
      temp.deleteOnExit();
      file = new RandomAccessFile(temp, "rw");
      file.setLength(0);
      registerShutdownHook();
    } catch (IOException e) {
      throw new RuntimeException("Unable to initialize byte cache", e);
    }
  }

  /**
   * Retrieve the underlying bytes.
   *
   * @param token a previously returned token
   * @return the bytes that were written
   */
  public synchronized byte[] readByteArray(long token) {
    try {
      atEnd = false;
      file.seek(token);
      int length = file.readInt();
      byte[] result = new byte[length];
      file.readFully(result);
      return result;
    } catch (IOException e) {
      throw new RuntimeException("Unable to read from byte cache", e);
    }
  }

  /**
   * Deserialize the underlying bytes as an object.
   *
   * @param <T> the type of the object to deserialize
   * @param token a previously returned token
   * @param type the type of the object to deserialize
   * @return the deserialized object
   */
  public <T> T readObject(long token, Class<T> type) {
    try {
      byte[] bytes = readByteArray(token);
      ByteArrayInputStream in = new ByteArrayInputStream(bytes);
      ObjectInputStream objectInputStream = new StringInterningObjectInputStream(in);
      return type.cast(objectInputStream.readObject());
    } catch (ClassNotFoundException | IOException e) {
      throw new RuntimeException("Unexpected exception deserializing from disk cache", e);
    }
  }

  /**
   * Read the underlying bytes as a String.
   *
   * @param token a previously returned token
   * @return the String that was written
   */
  public String readString(long token) {
    return new String(readByteArray(token), UTF_8);
  }

  /**
   * Write the rest of the data in an input stream to disk. Note: this method
   * does not close the InputStream.
   *
   * @param in open stream containing the data to write to the disk cache.
   *
   * @return a token to retrieve the data later
   */
  public synchronized long transferFromStream(InputStream in) throws IOException {
    assert in != null;
    byte[] buf = takeThreadLocalBuf();
    try {
      long position = moveToEndPosition();

      // Placeholder, we don't know the length yet.
      file.writeInt(-1);

      // Transfer all the bytes.
      int length = 0;
      int bytesRead;
      while ((bytesRead = in.read(buf)) != -1) {
        file.write(buf, 0, bytesRead);
        length += bytesRead;
      }

      // Now go back and fill in the length.
      file.seek(position);
      file.writeInt(length);
      // Don't eagerly seek the end, the next operation might be a read.
      atEnd = false;
      return position;
    } finally {
      releaseThreadLocalBuf(buf);
    }
  }

  /**
   * Returns a thread-local buffer for efficient data transfer. Usages should be non-reentrant,
   * resulting in a max of one buffer per thread.
   */
  private static byte[] takeThreadLocalBuf() {
    byte[] buf = threadLocalBuf.get();
    if (buf == null) {
      throw new IllegalStateException("Reentrant usage, or failed to return!");
    }
    threadLocalBuf.set(null);
    return buf;
  }

  private static void releaseThreadLocalBuf(byte[] buf) {
    threadLocalBuf.set(buf);
  }

  /**
   * Writes the underlying bytes into the specified output stream.
   *
   * @param token a previously returned token
   * @param out the stream to write into
   */
  public synchronized void transferToStream(long token, OutputStream out) throws IOException {
    byte[] buf = takeThreadLocalBuf();
    try {
      atEnd = false;
      file.seek(token);
      int length = file.readInt();
      int bufLen = buf.length;
      while (length > bufLen) {
        int read = file.read(buf, 0, bufLen);
        length -= read;
        out.write(buf, 0, read);
      }
      while (length > 0) {
        int read = file.read(buf, 0, length);
        length -= read;
        out.write(buf, 0, read);
      }
    } finally {
      releaseThreadLocalBuf(buf);
    }
  }

  /**
   * Write a byte array to disk.
   *
   * @return a token to retrieve the data later
   */
  public synchronized long writeByteArray(byte[] bytes) {
    try {
      long position = moveToEndPosition();
      file.writeInt(bytes.length);
      file.write(bytes);
      return position;
    } catch (IOException e) {
      throw new RuntimeException("Unable to write to byte cache", e);
    }
  }

  /**
   * Serialize an Object to disk.
   *
   * @return a token to retrieve the data later
   */
  public long writeObject(Object object) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (ObjectOutputStream objectStream = new ObjectOutputStream(out)) {
      objectStream.writeObject(object);
    } catch (IOException e) {
      throw new RuntimeException("Unexpected IOException on in-memory stream", e);
    }
    return writeByteArray(out.toByteArray());
  }

  /**
   * Write a String to disk as bytes.
   *
   * @return a token to retrieve the data later
   */
  public long writeString(String str) {
    return writeByteArray(str.getBytes(UTF_8));
  }

  /**
   * Moves to the end of the file if necessary and returns the offset position.
   * Caller must synchronize.
   *
   * @return the offset position of the end of the file
   * @throws IOException
   */
  private long moveToEndPosition() throws IOException {
    // Get an end pointer.
    if (atEnd) {
      return file.getFilePointer();
    } else {
      long position = file.length();
      file.seek(position);
      atEnd = true;
      return position;
    }
  }

  /**
   * Register a shutdown hook to close the RandomAccessFile associated with the temp file.<br>
   * There is a known <a href="https://bugs.java.com/bugdatabase/view_bug.do?bug_id=4171239">bug</a>
   * in Windows that prevents the 'temp' file from being deleted by 'deleteOnExit'
   * (see {@link DiskCache#DiskCache()}) because it is still open by the RandomAccessFile.<br>
   * This hook forces the RandomAccessFile to be closed at shutdown to allow the correct
   * 'temp' file removal.
   */
  private void registerShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          file.close();
        } catch (IOException e) {
          // No exception handling in a shutdown hook
        }
      }
    }));
  }
}
