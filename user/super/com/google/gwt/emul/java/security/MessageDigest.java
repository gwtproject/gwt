/*
 * Copyright 2010 Google Inc.
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
package java.security;

import static javaemul.internal.Coercions.ensureInt;

/**
 * Message Digest algorithm - <a href=
 * "http://java.sun.com/j2se/1.4.2/docs/api/java/security/MessageDigest.html"
 * >[Sun's docs]</a>.
 */
public abstract class MessageDigest extends MessageDigestSpi {

  private static class Md5Digest extends MessageDigest {

    // 16 * 4 bytes
    static byte padding[] = {
        (byte) 0x80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    /**
     * Converts a long to a 8-byte array using low order first.
     *
     * @param n A long.
     * @return A byte[].
     */
    public static byte[] toBytes(long n) {
      byte[] b = new byte[8];

      b[0] = (byte) (n);
      n >>>= 8;
      b[1] = (byte) (n);
      n >>>= 8;
      b[2] = (byte) (n);
      n >>>= 8;
      b[3] = (byte) (n);
      n >>>= 8;
      b[4] = (byte) (n);
      n >>>= 8;
      b[5] = (byte) (n);
      n >>>= 8;
      b[6] = (byte) (n);
      n >>>= 8;
      b[7] = (byte) (n);

      return b;
    }

    /**
     * Converts a 64-byte array into a 16-int array.
     *
     * @param in A byte[].
     * @param out An int[].
     */
    private static void byte2int(byte[] in, int[] out) {
      for (int inpos = 0, outpos = 0; outpos < 16; outpos++) {
        out[outpos] = ((in[inpos++] & 0xff) | ((in[inpos++] & 0xff) << 8)
            | ((in[inpos++] & 0xff) << 16) | ((in[inpos++] & 0xff) << 24));
      }
    }

    /*
     * Method F.
     *
     * @param x An int.
     * @param y An int.
     * @param z An int.
     * @return An int.
     */
    private static int f(int x, int y, int z) {
      return (z ^ (x & (y ^ z)));
    }

    /*
     * Method FF.
     *
     * @param a An int.
     * @param b An int.
     * @param c An int.
     * @param d An int.
     * @param x An int.
     * @param s An int.
     * @param ac An int.
     * @return An int.
     */
    private static int ff(int a, int b, int c, int d, int x, int s, int ac) {
      a += x + ac + f(b, c, d);
      a = (a << s | a >>> -s);
      return a + b;
    }

    /*
     * Method G.
     *
     * @param x An int.
     * @param y An int.
     * @param z An int.
     * @return An int.
     */
    private static int g(int x, int y, int z) {
      return (y ^ (z & (x ^ y)));
    }

    /*
     * Method GG.
     *
     * @param a An int.
     * @param b An int.
     * @param c An int.
     * @param d An int.
     * @param x An int.
     * @param s An int.
     * @param ac An int.
     * @return An int.
     */
    private static int gg(int a, int b, int c, int d, int x, int s, int ac) {
      a += x + ac + g(b, c, d);
      a = (a << s | a >>> -s);
      return a + b;
    }

    /*
     * Method H.
     *
     * @param x An int.
     * @param y An int.
     * @param z An int.
     * @return An int.
     */
    private static int h(int x, int y, int z) {
      return (x ^ y ^ z);
    }

    /*
     * Method HH.
     *
     * @param a An int.
     * @param b An int.
     * @param c An int.
     * @param d An int.
     * @param x An int.
     * @param s An int.
     * @param ac An int.
     * @return An int.
     */
    private static int hh(int a, int b, int c, int d, int x, int s, int ac) {
      a += x + ac + h(b, c, d);
      a = (a << s | a >>> -s);
      return a + b;
    }

    /*
     * Method I.
     *
     * @param x An int.
     * @param y An int.
     * @param z An int.
     * @return An int.
     */
    private static int i(int x, int y, int z) {
      return (y ^ (x | ~z));
    }

    /*
     * Method II.
     *
     * @param a An int.
     * @param b An int.
     * @param c An int.
     * @param d An int.
     * @param x An int.
     * @param s An int.
     * @param ac An int.
     * @return An int.
     */
    private static int ii(int a, int b, int c, int d, int x, int s, int ac) {
      a += x + ac + i(b, c, d);
      a = (a << s | a >>> -s);
      return a + b;
    }

    /**
     * Converts a 4-int array into a 16-byte array.
     *
     * @param in An int[].
     * @param out A byte[].
     */
    private static void int2byte(int[] in, byte[] out) {
      for (int inpos = 0, outpos = 0; inpos < 4; inpos++) {
        out[outpos++] = (byte) (in[inpos] & 0xff);
        out[outpos++] = (byte) ((in[inpos] >>> 8) & 0xff);
        out[outpos++] = (byte) ((in[inpos] >>> 16) & 0xff);
        out[outpos++] = (byte) ((in[inpos] >>> 24) & 0xff);
      }
    }

    private byte buffer[];

    // TODO(jat): consider doing away with long math
    private long counter;

    private final byte[] oneByte = new byte[1];

    private int remainder;

    private int state[];

    private int x[];

    public Md5Digest() {
      super("MD5");
      engineReset();
    }

    @Override
    protected byte[] engineDigest() {
      byte[] bits = toBytes(counter << 3);
      byte[] digest = new byte[16];

      if (remainder > 8) {
        engineUpdate(padding, 0, remainder - 8);
      } else {
        engineUpdate(padding, 0, 64 + (remainder - 8));
      }

      engineUpdate(bits, 0, 8);

      int2byte(state, digest);

      this.reset();
      return digest;
    }

    @Override
    protected int engineGetDigestLength() {
      return 16;
    }

    @Override
    protected void engineReset() {
      buffer = new byte[64];
      state = new int[4];
      x = new int[16];

      state[0] = 0x67452301;
      state[1] = 0xefcdab89;
      state[2] = 0x98badcfe;
      state[3] = 0x10325476;

      counter = 0;
      remainder = 64;
    }

    @Override
    protected void engineUpdate(byte input) {
      // TODO(jat): better implementation
      oneByte [0] = input;
      engineUpdate(oneByte, 0, 1);
    }

    @Override
    protected void engineUpdate(byte[] input, int offset, int len) {
      while (true) {
        if (len >= remainder) {
          System.arraycopy(input, offset, buffer, (int) (counter & 63L),
              remainder);
          transform(buffer);
          counter += remainder;
          offset += remainder;
          len -= remainder;
          remainder = 64;
        } else {
          System.arraycopy(input, offset, buffer, (int) (counter & 63L), len);
          counter += len;
          remainder -= len;
          break;
        }
      }
    }

    /*
     * TODO: Document.
     *
     * @param buffer A byte[].
     */
    private void transform(byte[] buffer) {
      int a, b, c, d;

      byte2int(buffer, x);

      a = state[0];
      b = state[1];
      c = state[2];
      d = state[3];

      a = ff(a, b, c, d, x[0], 7, 0xd76aa478);
      d = ff(d, a, b, c, x[1], 12, 0xe8c7b756);
      c = ff(c, d, a, b, x[2], 17, 0x242070db);
      b = ff(b, c, d, a, x[3], 22, 0xc1bdceee);
      a = ff(a, b, c, d, x[4], 7, 0xf57c0faf);
      d = ff(d, a, b, c, x[5], 12, 0x4787c62a);
      c = ff(c, d, a, b, x[6], 17, 0xa8304613);
      b = ff(b, c, d, a, x[7], 22, 0xfd469501);
      a = ff(a, b, c, d, x[8], 7, 0x698098d8);
      d = ff(d, a, b, c, x[9], 12, 0x8b44f7af);
      c = ff(c, d, a, b, x[10], 17, 0xffff5bb1);
      b = ff(b, c, d, a, x[11], 22, 0x895cd7be);
      a = ff(a, b, c, d, x[12], 7, 0x6b901122);
      d = ff(d, a, b, c, x[13], 12, 0xfd987193);
      c = ff(c, d, a, b, x[14], 17, 0xa679438e);
      b = ff(b, c, d, a, x[15], 22, 0x49b40821);

      a = gg(a, b, c, d, x[1], 5, 0xf61e2562);
      d = gg(d, a, b, c, x[6], 9, 0xc040b340);
      c = gg(c, d, a, b, x[11], 14, 0x265e5a51);
      b = gg(b, c, d, a, x[0], 20, 0xe9b6c7aa);
      a = gg(a, b, c, d, x[5], 5, 0xd62f105d);
      d = gg(d, a, b, c, x[10], 9, 0x2441453);
      c = gg(c, d, a, b, x[15], 14, 0xd8a1e681);
      b = gg(b, c, d, a, x[4], 20, 0xe7d3fbc8);
      a = gg(a, b, c, d, x[9], 5, 0x21e1cde6);
      d = gg(d, a, b, c, x[14], 9, 0xc33707d6);
      c = gg(c, d, a, b, x[3], 14, 0xf4d50d87);
      b = gg(b, c, d, a, x[8], 20, 0x455a14ed);
      a = gg(a, b, c, d, x[13], 5, 0xa9e3e905);
      d = gg(d, a, b, c, x[2], 9, 0xfcefa3f8);
      c = gg(c, d, a, b, x[7], 14, 0x676f02d9);
      b = gg(b, c, d, a, x[12], 20, 0x8d2a4c8a);

      a = hh(a, b, c, d, x[5], 4, 0xfffa3942);
      d = hh(d, a, b, c, x[8], 11, 0x8771f681);
      c = hh(c, d, a, b, x[11], 16, 0x6d9d6122);
      b = hh(b, c, d, a, x[14], 23, 0xfde5380c);
      a = hh(a, b, c, d, x[1], 4, 0xa4beea44);
      d = hh(d, a, b, c, x[4], 11, 0x4bdecfa9);
      c = hh(c, d, a, b, x[7], 16, 0xf6bb4b60);
      b = hh(b, c, d, a, x[10], 23, 0xbebfbc70);
      a = hh(a, b, c, d, x[13], 4, 0x289b7ec6);
      d = hh(d, a, b, c, x[0], 11, 0xeaa127fa);
      c = hh(c, d, a, b, x[3], 16, 0xd4ef3085);
      b = hh(b, c, d, a, x[6], 23, 0x4881d05);
      a = hh(a, b, c, d, x[9], 4, 0xd9d4d039);
      d = hh(d, a, b, c, x[12], 11, 0xe6db99e5);
      c = hh(c, d, a, b, x[15], 16, 0x1fa27cf8);
      b = hh(b, c, d, a, x[2], 23, 0xc4ac5665);

      a = ii(a, b, c, d, x[0], 6, 0xf4292244);
      d = ii(d, a, b, c, x[7], 10, 0x432aff97);
      c = ii(c, d, a, b, x[14], 15, 0xab9423a7);
      b = ii(b, c, d, a, x[5], 21, 0xfc93a039);
      a = ii(a, b, c, d, x[12], 6, 0x655b59c3);
      d = ii(d, a, b, c, x[3], 10, 0x8f0ccc92);
      c = ii(c, d, a, b, x[10], 15, 0xffeff47d);
      b = ii(b, c, d, a, x[1], 21, 0x85845dd1);
      a = ii(a, b, c, d, x[8], 6, 0x6fa87e4f);
      d = ii(d, a, b, c, x[15], 10, 0xfe2ce6e0);
      c = ii(c, d, a, b, x[6], 15, 0xa3014314);
      b = ii(b, c, d, a, x[13], 21, 0x4e0811a1);
      a = ii(a, b, c, d, x[4], 6, 0xf7537e82);
      d = ii(d, a, b, c, x[11], 10, 0xbd3af235);
      c = ii(c, d, a, b, x[2], 15, 0x2ad7d2bb);
      b = ii(b, c, d, a, x[9], 21, 0xeb86d391);

      // Coerce to 32-bits to compute the expression correctly in JavaScript.
      state[0] = ensureInt(state[0] + a);
      state[1] = ensureInt(state[1] + b);
      state[2] = ensureInt(state[2] + c);
      state[3] = ensureInt(state[3] + d);
    }
  }

  public static MessageDigest getInstance(String algorithm)
      throws NoSuchAlgorithmException {
    if ("MD5".equals(algorithm)) {
      return new Md5Digest();
    }
    throw new NoSuchAlgorithmException(algorithm + " not supported");
  }

  public static boolean isEqual(byte[] digestA, byte[] digestB) {
    int n = digestA.length;
    if (n != digestB.length) {
      return false;
    }
    for (int i = 0; i < n; ++i) {
      if (digestA[i] != digestB[i]) {
        return false;
      }
    }
    return true;
  }

  private final String algorithm;

  protected MessageDigest(String algorithm) {
    this.algorithm = algorithm;
  }

  public byte[] digest() {
    return engineDigest();
  }

  public byte[] digest(byte[] input) {
    update(input);
    return digest();
  }

  public int digest(byte[] buf, int offset, int len) throws DigestException {
    return engineDigest(buf, offset, len);
  }

  public final String getAlgorithm() {
    return algorithm;
  }

  public final int getDigestLength() {
    return engineGetDigestLength();
  }

  public void reset() {
    engineReset();
  }

  public void update(byte input) {
    engineUpdate(input);
  }

  public void update(byte[] input) {
    engineUpdate(input, 0, input.length);
  }

  public void update(byte[] input, int offset, int len) {
    engineUpdate(input, offset, len);
  }
}
