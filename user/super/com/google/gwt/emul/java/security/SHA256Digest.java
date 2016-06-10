/*
 * Copyright 2016 Google Inc.
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

/* 
 * Portions of this code are from Bouncy Castle Inc.
 * 
 * Copyright (c) 2000 - 2015 The Legion of the Bouncy Castle Inc. (http://www.bouncycastle.org)
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
 * and associated documentation files (the "Software"), to deal in the Software without 
 * restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies 
 * or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR 
 * OTHER DEALINGS IN THE SOFTWARE.
 * 
 * Modified by Google:
 *   1. Changed class interface to be consistent with current class hierachy.
 *   2. Enfored int overflow behavior in GWT.
 */
package java.security;

class SHA256Digest extends MessageDigest {

  private static final int WORD_LENGTH = 32;
  private static final int BYTE_MASK = 0xff;
  private static final int[] K = {
      0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4,
      0xab1c5ed5, 0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe,
      0x9bdc06a7, 0xc19bf174, 0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f,
      0x4a7484aa, 0x5cb0a9dc, 0x76f988da, 0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
      0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967, 0x27b70a85, 0x2e1b2138, 0x4d2c6dfc,
      0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85, 0xa2bfe8a1, 0xa81a664b,
      0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070, 0x19a4c116,
      0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
      0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7,
      0xc67178f2
  };

  private int h1, h2, h3, h4, h5, h6, h7, h8;
  private byte[] xBuf = new byte[4];
  private int xBuffOff;
  private int[] x = new int[64];
  private int xOff;
  private long byteCount;

  public SHA256Digest() {
    super("SHA-256");
    engineReset();
  }

  @Override
  protected void engineUpdate(byte input) {
    xBuf[xBuffOff++] = input;
    if (xBuffOff == xBuf.length) {
      processWord(xBuf, 0);
      xBuffOff = 0;
    }
    byteCount++;
  }

  @Override
  protected void engineUpdate(byte[] in, int inOff, int len) {

    // Fill the current uncomplete word.
    while (xBuffOff != 0 && len > 0) {
      update(in[inOff]);
      inOff++;
      len--;
    }
    
    // Fill word by word.
    while (len > xBuf.length) {
      processWord(in, inOff);
      inOff += xBuf.length;
      len -= xBuf.length;
      byteCount += xBuf.length;
    }
    
    // Fill the remainder.
    while (len > 0) {
      update(in[inOff]);
      inOff++;
      len--;
    }
  }

  @Override
  protected byte[] engineDigest() {
    finish();
    byte[] digest = getDigest();
    engineReset();
    return digest;
  }

  @Override
  protected void engineReset() {
    h1 = 0x6a09e667;
    h2 = 0xbb67ae85;
    h3 = 0x3c6ef372;
    h4 = 0xa54ff53a;
    h5 = 0x510e527f;
    h6 = 0x9b05688c;
    h7 = 0x1f83d9ab;
    h8 = 0x5be0cd19;
    
    byteCount = 0;
    xBuffOff = 0;
    for (int i = 0; i < xBuf.length; i++) {
      xBuf[i] = 0;
    }
    xOff = 0;
    for (int i = 0; i < x.length; i++) {
      x[i] = 0;
    } 
  }
  
  private byte[] getDigest() {
    byte[] digest = new byte[32];
    int offset = 0;
    for (int h : new int[] {h1, h2, h3, h4, h5, h6, h7, h8}) {
      digest[offset++] = (byte) ((h >>> 24) & BYTE_MASK);
      digest[offset++] = (byte) ((h >>> 16) & BYTE_MASK);
      digest[offset++] = (byte) ((h >>> 8) & BYTE_MASK);
      digest[offset++] = (byte) (h & BYTE_MASK);
    }
    return digest;
  }
  
  private void finish() {
    long bitLength = byteCount << 3;
    update((byte) 128);
    while (xBuffOff != 0) {
      update((byte) 0);
    }
    processLength(bitLength);
    processBlock();
  }
  
  private void processLength(long bitLength) {
    if (xOff > 14) {
      processBlock();
    }
    
    x[14] = (int) (bitLength >>> 32);
    x[15] = (int) (bitLength & 0xffffffff);
  }
  
  private void processWord(byte[] in, int inOff) {
    int byteMask = 0xff;
    // Concatenate four bytes to one int.
    x[xOff++] = ((in[inOff] & byteMask) << 24) | ((in[inOff + 1] & byteMask) << 16)
        | ((in[inOff + 2] & byteMask) << 8) | (in[inOff + 3] & byteMask);
    if (xOff == 16) {
      processBlock();
    }
  }
  
  private void processBlock() {
    // Prepare message schedule.
    for (int t = 16; t < 64; t++) {
      x[t] = enforceOverflow(sigma1(x[t - 2]) + x[t - 7] + sigma0(x[t - 15]) + x[t - 16]);
    }
    
    // Initialize eight working variables.
    int a = h1;
    int b = h2;
    int c = h3;
    int d = h4;
    int e = h5;
    int f = h6;
    int g = h7;
    int h = h8;
    
    int t = 0;
    for (int i = 0;  i < 8; i++) {
      // t = 8 * i
      h += sum1(e) + ch(e, f, g) + K[t] + x[t++];
      d = enforceOverflow(h + d);
      h = enforceOverflow(h + sum0(a) + maj(a, b, c));

      // t = 8 * i + 1
      g += sum1(d) + ch(d, e, f) + K[t] + x[t++];
      c = enforceOverflow(c + g);
      g = enforceOverflow(g + sum0(h) + maj(h, a, b));
      
      // t = 8 * i + 2
      f += sum1(c) + ch(c, d, e) + K[t] + x[t++];
      b = enforceOverflow(b + f);
      f = enforceOverflow(f + sum0(g) + maj(g, h, a));
      
      // t = 8 * i + 3
      e += sum1(b) + ch(b, c, d) + K[t] + x[t++];
      a = enforceOverflow(a + e);
      e = enforceOverflow(e + sum0(f) + maj(f, g, h));
      
      // t = 8 * i + 4
      d += sum1(a) + ch(a, b, c) + K[t] + x[t++];
      h = enforceOverflow(h + d);
      d = enforceOverflow(d + sum0(e) + maj(e, f, g));
      
      // t = 8 * i + 5
      c += sum1(h) + ch(h, a, b) + K[t] + x[t++];
      g = enforceOverflow(g + c);
      c = enforceOverflow(c + sum0(d) + maj(d, e, f));
      
      // t = 8 * i + 6
      b += sum1(g) + ch(g, h, a) + K[t] + x[t++];
      f = enforceOverflow(f + b);
      b = enforceOverflow(b + sum0(c) + maj(c, d, e));
      
      // t = 8 * i + 7
      a += sum1(f) + ch(f, g, h) + K[t] + x[t++];
      e = enforceOverflow(e + a);
      a = enforceOverflow(a + sum0(b) + maj(b, c, d));
    }
    
    h1 = enforceOverflow(h1 + a);
    h2 = enforceOverflow(h2 + b);
    h3 = enforceOverflow(h3 + c);
    h4 = enforceOverflow(h4 + d);
    h5 = enforceOverflow(h5 + e);
    h6 = enforceOverflow(h6 + f);
    h7 = enforceOverflow(h7 + g);
    h8 = enforceOverflow(h8 + h);
    
    // Reset offset and buffer.
    xOff = 0;
    for (int i = 0; i < x.length; i++) {
      x[i] = 0;
    }
  }
  
  private int sum0(int x) {
    return rightRotate(x, 2) ^ rightRotate(x, 13) ^ rightRotate(x, 22);
  }
  
  private int sum1(int x) {
    return rightRotate(x, 6) ^ rightRotate(x, 11) ^ rightRotate(x, 25);
  }
  
  private int sigma0(int x) {
    return rightRotate(x, 7) ^ rightRotate(x, 18) ^ (x >>> 3);
  }
  
  private int sigma1(int x) {
    return rightRotate(x, 17) ^ rightRotate(x, 19) ^ (x >>> 10);
  }
  
  private int rightRotate(int x, int n) {
    return (x >>> n) | enforceOverflow(x << (WORD_LENGTH - n));
  }
  
  private int ch(int x, int y, int z) {
    return enforceOverflow(((x & y) ^ ((~x) & z)));
  }
  
  private int maj(int x, int y, int z) {
    return (x & y) ^ (x & z) ^ (y & z);
  }
  
  // GWT emulate integer with double and doesn't overflow. Enfore it by a integer mask.
  private int enforceOverflow(int input) {
    int intMask = 0xffffffff;
    return input & intMask;
  }
}
