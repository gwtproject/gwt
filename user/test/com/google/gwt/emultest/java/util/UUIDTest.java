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
package com.google.gwt.emultest.java.util;

import com.google.gwt.junit.client.GWTTestCase;

import java.util.UUID;

/**
 * Tests for GWT's emulation of the JRE UUID class.
 */
public class UUIDTest extends GWTTestCase {

  /**
   * Sets module name so that javascript compiler can operate.
   */
  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.EmulSuite";
  }

  public void testFromAndToString() {
    String uuid = "21f1f6fd-0011-49e8-aabd-02bc7c801a4e";
    String uuid2 = "c86f295f-1184-4211-b254-600f8a43efdd";
    UUID test = UUID.fromString(uuid);
    UUID test2 = UUID.fromString(uuid2);
    assertFalse(test.toString().equals(test2.toString()));
    assertFalse(test == test2);
    assertEquals(test.toString(), uuid);
    assertEquals(test2.toString(), uuid2);
  }

  public void testFromLongs() {
    long msb = 0x0000000000004000L;
    long lsb = 0x8000000000000000L;
    UUID test = new UUID(msb, lsb);
    assertEquals(test.getMostSignificantBits(), msb);
    assertEquals(test.getLeastSignificantBits(), lsb);
    assertEquals(test.toString(), "00000000-0000-4000-8000-000000000000");
  }

  public void testVariant() {
    UUID test0 = new UUID(0x0000000000004000L, 0x0000000000000000L);
    UUID test1 = new UUID(0x0000000000004000L, 0x1000000000000000L);
    UUID test2 = new UUID(0x0000000000004000L, 0x2000000000000000L);
    UUID test3 = new UUID(0x0000000000004000L, 0x3000000000000000L);
    UUID test4 = new UUID(0x0000000000004000L, 0x4000000000000000L);
    UUID test5 = new UUID(0x0000000000004000L, 0x5000000000000000L);
    UUID test6 = new UUID(0x0000000000004000L, 0x6000000000000000L);
    UUID test7 = new UUID(0x0000000000004000L, 0x7000000000000000L);
    UUID test8 = new UUID(0x0000000000004000L, 0x8000000000000000L);
    UUID test9 = new UUID(0x0000000000004000L, 0x9000000000000000L);
    UUID testa = new UUID(0x0000000000004000L, 0xa000000000000000L);
    UUID testb = new UUID(0x0000000000004000L, 0xb000000000000000L);
    UUID testc = new UUID(0x0000000000004000L, 0xc000000000000000L);
    UUID testd = new UUID(0x0000000000004000L, 0xd000000000000000L);
    UUID teste = new UUID(0x0000000000004000L, 0xe000000000000000L);
    UUID testf = new UUID(0x0000000000004000L, 0xf000000000000000L);

    assertEquals(test0.variant(), 0);
    assertEquals(test1.variant(), 0);
    assertEquals(test2.variant(), 0);
    assertEquals(test3.variant(), 0);
    assertEquals(test4.variant(), 0);
    assertEquals(test5.variant(), 0);
    assertEquals(test6.variant(), 0);
    assertEquals(test7.variant(), 0);
    assertEquals(test8.variant(), 2);
    assertEquals(test9.variant(), 2);
    assertEquals(testa.variant(), 2);
    assertEquals(testb.variant(), 2);
    assertEquals(testc.variant(), 6);
    assertEquals(testd.variant(), 6);
    assertEquals(teste.variant(), 7);
    assertEquals(testf.variant(), 7);
  }

  public void testVersion() {
    UUID test0 = new UUID(0x0000000000000000L, 0x8000000000000000L);
    UUID test1 = new UUID(0x0000000000001000L, 0x8000000000000000L);
    UUID test2 = new UUID(0x0000000000002000L, 0x8000000000000000L);
    UUID test3 = new UUID(0x0000000000003000L, 0x8000000000000000L);
    UUID test4 = new UUID(0x0000000000004000L, 0x8000000000000000L);
    UUID test5 = new UUID(0x0000000000005000L, 0x8000000000000000L);
    UUID test6 = new UUID(0x0000000000006000L, 0x8000000000000000L);
    UUID test7 = new UUID(0x0000000000007000L, 0x8000000000000000L);
    UUID test8 = new UUID(0x0000000000008000L, 0x8000000000000000L);
    UUID test9 = new UUID(0x0000000000009000L, 0x8000000000000000L);
    UUID testa = new UUID(0x000000000000a000L, 0x8000000000000000L);
    UUID testb = new UUID(0x000000000000b000L, 0x8000000000000000L);
    UUID testc = new UUID(0x000000000000c000L, 0x8000000000000000L);
    UUID testd = new UUID(0x000000000000d000L, 0x8000000000000000L);
    UUID teste = new UUID(0x000000000000e000L, 0x8000000000000000L);
    UUID testf = new UUID(0x000000000000f000L, 0x8000000000000000L);

    assertEquals(test0.version(), 0x0);
    assertEquals(test1.version(), 0x1);
    assertEquals(test2.version(), 0x2);
    assertEquals(test3.version(), 0x3);
    assertEquals(test4.version(), 0x4);
    assertEquals(test5.version(), 0x5);
    assertEquals(test6.version(), 0x6);
    assertEquals(test7.version(), 0x7);
    assertEquals(test8.version(), 0x8);
    assertEquals(test9.version(), 0x9);
    assertEquals(testa.version(), 0xa);
    assertEquals(testb.version(), 0xb);
    assertEquals(testc.version(), 0xc);
    assertEquals(testd.version(), 0xd);
    assertEquals(teste.version(), 0xe);
    assertEquals(testf.version(), 0xf);
  }

  public void testCompareTo() {
    UUID testHighMsb = new UUID(0x0000000000064000L, 0x8500000000000000L);
    UUID testHighLsb = new UUID(0x0000000000054000L, 0x8600000000000000L);
    UUID testMid = new UUID(0x0000000000054000L, 0x8500000000000000L);
    UUID testLowMsb = new UUID(0x0000000000044000L, 0x8500000000000000L);
    UUID testLowLsb = new UUID(0x0000000000054000L, 0x8400000000000000L);

    assertEquals(testMid.compareTo(testHighMsb), -1);
    assertEquals(testMid.compareTo(testHighLsb), -1);
    assertEquals(testMid.compareTo(testMid), 0);
    assertEquals(testMid.compareTo(testLowMsb), 1);
    assertEquals(testMid.compareTo(testLowLsb), 1);
  }

  public void testEquals() {
    UUID test1 = new UUID(0x0000000000004000L, 0x8000000000000000L);
    UUID test2 = UUID.fromString("00000000-0000-4000-8000-000000000000");

    assertEquals(test1, test2);
  }

  public void testNode() {
    // Test against known value
    UUID test = UUID.fromString("547fa190-b209-11d8-bc4e-95ef8f69921e");
    long val = 164856135782942L;
    assertEquals(val, test.node());
  }

  public void testTimestamp() {
    // Test against known value
    UUID test = UUID.fromString("f8636b90-b207-11d8-b231-e33c9df047ca");
    long val = 133051936309210000L;
    assertEquals(val, test.timestamp());
  }

  public void testClockSequence() {
    // Test against known value
    UUID test = UUID.fromString("c079ef59-f5b1-1801-a348-c38429e61be7");
    int val = 9032;
    assertEquals(val, test.clockSequence());
  }

  @SuppressWarnings("unused")
  public void testInvalidFromString() {
    try {
      UUID.fromString("invalid");
      fail("Should have thrown InvalidArgumentException");
    } catch (IllegalArgumentException e) {
    }
  }

}
