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
package java.util;

import java.io.Serializable;

/**
 * UUID reimplementation for use on client side GWT.
 */
public class UUID implements Serializable, Comparable<UUID> {

  private static final char[] hexDigits = "0123456789abcdef".toCharArray();

  private long msb;
  private long lsb;
  private int hashCode = -1;

  /**
   * Serializable classes must have a zero arg constructor for GWT
   */
  @SuppressWarnings("unused")
  private UUID() {
  }

  public UUID(long msb, long lsb) {
    this.msb = msb;
    this.lsb = lsb;
  }

  /**
   * Returns the most significant 64 bits of the UUID
   * 
   * @return Returns the most significant 64 bits of the UUID
   */
  public long getMostSignificantBits() {
    return msb;
  }

  /**
   * Returns the least significant 64 bits of the UUID
   * 
   * @return Returns the least significant 64 bits of the UUID
   */
  public long getLeastSignificantBits() {
    return lsb;
  }

  /**
   * Constructs a new UUID from a string representation.
   *
   * @param String representation of the UUID (must be in proper format)
   *
   * @return UUID object of String passed in
   */
  public static UUID fromString(String uuidString) {
    long[] words = new long[2];
    int b = 0;
    for (int i = 0; i < 36; i++) {
      int c = uuidString.charAt(i) | 0x20; // to lowercase (will lose some error checking)
      if (i == 8 || i == 13 || i == 23) {
        if (c != '-') {
          throw new IllegalArgumentException(String.valueOf(i));
        }
      } else if (i == 18) {
        if (c != '-') {
          throw new IllegalArgumentException(String.valueOf(i));
        }
        b = 1;
      } else {
        byte h = (byte) (c & 0x0f);
        if (c >= 'a' && c <= 'f') {
          h += 9;
        } else if (c < '0' || c > '9') {
          throw new IllegalArgumentException();
        }
        words[b] = words[b] << 4 | h;
      }
    }
    return new UUID(words[0], words[1]);
  }

  /**
   * The variant field determines the layout of the UUID. That is, the interpretation of all other
   * bits in the UUID depends on the setting of the bits in the variant field. As such, it could
   * more accurately be called a type field; we retain the original term for compatibility. The
   * variant field consists of a variable number of the most significant bits of octet 8 of the
   * UUID.
   *
   * The following table lists the contents of the variant field, where the letter "x" indicates a
   * "don't-care" value.
   *
   * Msb0 Msb1 Msb2 Description
   *
   * 0 x x Reserved, NCS backward compatibility.
   *
   * 1 0 x The variant specified in this document.
   *
   * 1 1 0 Reserved, Microsoft Corporation backward compatibility
   *
   * 1 1 1 Reserved for future definition.
   *
   * @return int value of the variant, either 0, 2, 6 or 7
   */
  public int variant() {
    int variant = (int) (lsb >>> 61);
    return variant > 5 ? variant : (variant & 0x04) == 0 ? 0 : 2;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(UUID u) {
    if (msb > u.msb) {
      return 1;
    } else if (msb == u.msb) {
      if (lsb == u.lsb) {
        return 0;
      }
      return lsb < u.lsb ? 1 : -1;
    }
    return msb > u.msb ? 1 : -1;
  }

  /**
   * The version number is in the most significant 4 bits of the time stamp (bits 4 through 7 of the
   * time_hi_and_version field).
   *
   * The following table lists the currently-defined versions for this UUID variant.
   *
   * Msb0 Msb1 Msb2 Msb3 Version Description
   *
   * 0 0 0 1 1 The time-based version specified in this document.
   *
   * 0 0 1 0 2 DCE Security version, with embedded POSIX UIDs.
   *
   * 0 0 1 1 3 The name-based version specified in this document that uses MD5 hashing.
   *
   * 0 1 0 0 4 The randomly or pseudo- randomly generated version specified in this document.
   *
   * 0 1 0 1 5 The name-based version specified in this document that uses SHA-1 hashing.
   *
   * @return int version of the UUID
   */
  public int version() {
    return (int) (msb & 0xf000) >> 12;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    if (hashCode == -1) {
      hashCode = (int) ((msb >>> 32) ^ msb ^ (lsb >>> 32) ^ lsb);
    }
    return hashCode;
  }

  /**
   * In v1 type UUIDs returns the 60-bit timestamp of when the UUID was generated. This is
   * represented by Coordinated Universal Time (UTC) as a count of 100-nanosecond intervals since
   * 00:00:00.00, 15 October 1582 (the date of Gregorian reform to the Christian calendar).
   * 
   * @return long representing the timestamp for this UUID
   */
  public long timestamp() {
    if (version() != 1) {
      throw new UnsupportedOperationException("Not a time-based UUID");
    }
    long timelow = msb >>> 32, timemid = (msb >> 16) & 0x0ffff, timehigh = msb & 0x0fff;
    return (timehigh << 48) | (timemid << 32) | timelow;
  }

  /**
   * In v1 type UUIDs returns the generated clock sequence. The clock sequence is used to help avoid
   * duplicates that could arise when the clock is set backwards in time or if the node ID changes.
   * 
   * @return 14-bits representing the clock sequence for this UUID
   */
  public int clockSequence() {
    if (version() != 1) {
      throw new UnsupportedOperationException("Not a time-based UUID");
    }
    return (int) ((lsb >>> 48) & 0x3fff);
  }

  /**
   * For v1 type UUIDs return the node (the last 48 bits of the uuid)
   * 
   * @return long representing the node that generated the UUID
   */
  public long node() {
    if (version() != 1) {
      throw new UnsupportedOperationException("Not a time-based UUID");
    }
    return lsb & 0xffffffffffffL;
  }

  /**
   * Compares to another object. Only returns true if the other object is a UUID type and the
   * underlying bytes of the UUIDs are equal.
   * 
   * @param obj The object to be compared
   *
   * @return true if they are equal, false in all other cases
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    return (msb == ((UUID) obj).msb) && (lsb == ((UUID) obj).lsb);
  }

  /**
   * Builds and returns a string representing this UUID. The formal definition is:
   * 
   * UUID = time-low "-" time-mid "-" time-high-and-version "-" clock-seq-and-reserved clock-seq-low
   * "-" node
   * 
   * time-low = 4hexOctet time-mid = 2hexOctet time-high-and-version = 2hexOctet
   * clock-seq-and-reserved = hexOctet clock-seq-low = hexOctet node = 6hexOctet hexOctet = hexDigit
   * hexDigit hexDigit = "0" / "1" / "2" / "3" / "4" / "5" / "6" / "7" / "8" / "9" / "a" / "b" / "c"
   * / "d" / "e" / "f" / "A" / "B" / "C" / "D" / "E" / "F"
   * 
   * @return String representation of this UUID
   */
  @Override
  public String toString() {
    char[] chars = new char[36];
    for (int i = 60, j = 0; i >= 0; i -= 4) {
      chars[j++] = hexDigits[(int) (msb >> i) & 0x0f];
      if (j == 8 || j == 13 || j == 18) {
        chars[j++] = '-';
      }
    }
    for (int i = 60, j = 19; i >= 0; i -= 4) {
      chars[j++] = hexDigits[(int) (lsb >> i) & 0x0f];
      if (j == 23) {
        chars[j++] = '-';
      }
    }
    return new String(chars);
  }

}
