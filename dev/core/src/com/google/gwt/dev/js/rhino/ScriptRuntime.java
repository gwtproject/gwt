/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * The contents of this file are subject to the Netscape Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/NPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Rhino code, released
 * May 6, 1999.
 *
 * The Initial Developer of the Original Code is Netscape
 * Communications Corporation.  Portions created by Netscape are
 * Copyright (C) 1997-2000 Netscape Communications Corporation. All
 * Rights Reserved.
 *
 * Contributor(s):
 * Patrick Beard
 * Norris Boyd
 * Igor Bukanov
 * Roger Lawrence
 * Frank Mitchell
 * Andrew Wason
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the GNU Public License (the "GPL"), in which case the
 * provisions of the GPL are applicable instead of those above.
 * If you wish to allow use of your version of this file only
 * under the terms of the GPL and not to allow others to use your
 * version of this file under the NPL, indicate your decision by
 * deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL.  If you do not delete
 * the provisions above, a recipient may use your version of this
 * file under either the NPL or the GPL.
 */
// Modified by Google

package com.google.gwt.dev.js.rhino;

/**
 * This is the class that implements the runtime.
 *
 * @author Norris Boyd
 */

public class ScriptRuntime {

    public static double NaN = 0.0d / 0.0;

    public static String numberToString(double d, int base) {
        if (Double.isNaN(d))
            return "NaN";
        if (d == Double.POSITIVE_INFINITY)
            return "Infinity";
        if (d == Double.NEGATIVE_INFINITY)
            return "-Infinity";
        if (d == 0.0)
            return "0";

        if ((base < 2) || (base > 36)) {
            throw new Error(Context.getMessage1("msg.bad.radix",
                                                Integer.toString(base)));
        }

        if (base != 10) {
            return DToA.JS_dtobasestr(base, d);
        } else {
            StringBuffer result = new StringBuffer();
            DToA.JS_dtostr(result, DToA.DTOSTR_STANDARD, 0, d);
            return result.toString();
        }

    }

    /*
     * Helper function for toNumber, parseInt, and TokenStream.getToken.
     */
    static double stringToNumber(String s, int start, int radix) {
        char digitMax = '9';
        char lowerCaseBound = 'a';
        char upperCaseBound = 'A';
        int len = s.length();
        if (radix < 10) {
            digitMax = (char) ('0' + radix - 1);
        }
        if (radix > 10) {
            lowerCaseBound = (char) ('a' + radix - 10);
            upperCaseBound = (char) ('A' + radix - 10);
        }
        int end;
        double sum = 0.0;
        for (end=start; end < len; end++) {
            char c = s.charAt(end);
            int newDigit;
            if ('0' <= c && c <= digitMax)
                newDigit = c - '0';
            else if ('a' <= c && c < lowerCaseBound)
                newDigit = c - 'a' + 10;
            else if ('A' <= c && c < upperCaseBound)
                newDigit = c - 'A' + 10;
            else
                break;
            sum = sum*radix + newDigit;
        }
        if (start == end) {
            return NaN;
        }
        if (sum >= 9007199254740992.0) {
            if (radix == 10) {
                /* If we're accumulating a decimal number and the number
                 * is >= 2^53, then the result from the repeated multiply-add
                 * above may be inaccurate.  Call Java to get the correct
                 * answer.
                 */
                try {
                    return Double.valueOf(s.substring(start, end)).doubleValue();
                } catch (NumberFormatException nfe) {
                    return NaN;
                }
            } else if (radix == 2 || radix == 4 || radix == 8 ||
                       radix == 16 || radix == 32)
            {
                /* The number may also be inaccurate for one of these bases.
                 * This happens if the addition in value*radix + digit causes
                 * a round-down to an even least significant mantissa bit
                 * when the first dropped bit is a one.  If any of the
                 * following digits in the number (which haven't been added
                 * in yet) are nonzero then the correct action would have
                 * been to round up instead of down.  An example of this
                 * occurs when reading the number 0x1000000000000081, which
                 * rounds to 0x1000000000000000 instead of 0x1000000000000100.
                 */
                BinaryDigitReader bdr = new BinaryDigitReader(radix, s, start, end);
                int bit;
                sum = 0.0;

                /* Skip leading zeros. */
                do {
                    bit = bdr.getNextBinaryDigit();
                } while (bit == 0);

                if (bit == 1) {
                    /* Gather the 53 significant bits (including the leading 1) */
                    sum = 1.0;
                    for (int j = 52; j != 0; j--) {
                        bit = bdr.getNextBinaryDigit();
                        if (bit < 0)
                            return sum;
                        sum = sum*2 + bit;
                    }
                    /* bit54 is the 54th bit (the first dropped from the mantissa) */
                    int bit54 = bdr.getNextBinaryDigit();
                    if (bit54 >= 0) {
                        double factor = 2.0;
                        int sticky = 0;  /* sticky is 1 if any bit beyond the 54th is 1 */
                        int bit3;

                        while ((bit3 = bdr.getNextBinaryDigit()) >= 0) {
                            sticky |= bit3;
                            factor *= 2;
                        }
                        sum += bit54 & (bit | sticky);
                        sum *= factor;
                    }
                }
            }
            /* We don't worry about inaccurate numbers for any other base. */
        }
        return sum;
    }

    /**
     * For escaping strings printed by object and array literals; not quite
     * the same as 'escape.'
     */
    public static String escapeString(String s) {

        StringBuffer sb = null;

        for(int i = 0, L = s.length(); i != L; ++i) {
            int c = s.charAt(i);

            if (' ' <= c && c <= '~' && c != '"' && c != '\\') {
                // an ordinary print character (like C isprint()) and not "
                // or \ . Note single quote ' is not escaped
                if (sb != null) {
                    sb.append((char)c);
                }
                continue;
            }
            if (sb == null) {
                sb = new StringBuffer(L + 3);
                sb.append(s);
                sb.setLength(i);
            }

            int escape = -1;
            switch (c) {
                case '\b':  escape = 'b';  break;
                case '\f':  escape = 'f';  break;
                case '\n':  escape = 'n';  break;
                case '\r':  escape = 'r';  break;
                case '\t':  escape = 't';  break;
                case 0xb:   escape = 'v';  break; // Java lacks \v.
                case '"':   escape = '"';  break;
                case ' ':   escape = ' ';  break;
                case '\\':  escape = '\\'; break;
            }
            if (escape >= 0) {
                // an \escaped sort of character
                sb.append('\\');
                sb.append((char)escape);
            } else {
                int hexSize;
                if (c < 256) {
                    // 2-digit hex
                    sb.append("\\x");
                    hexSize = 2;
                } else {
                    // Unicode.
                    sb.append("\\u");
                    hexSize = 4;
                }
                // append hexadecimal form of c left-padded with 0
                for (int shift = (hexSize - 1) * 4; shift >= 0; shift -= 4) {
                    int digit = 0xf & (c >> shift);
                    int hc = (digit < 10) ? '0' + digit : 'a' - 10 + digit;
                    sb.append((char)hc);
                }
            }
        }

        return (sb == null) ? s : sb.toString();
    }

}
