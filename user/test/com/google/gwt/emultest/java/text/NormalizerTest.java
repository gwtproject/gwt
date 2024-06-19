/*
 * Copyright 2024 GWT Project Authors
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
package com.google.gwt.emultest.java.text;

import java.text.Normalizer;
import java.text.Normalizer.Form;

public class NormalizerTest extends EmulTestBase {

    private String hangul = "\uD55C\uAE00";
    private String hangulDecomposed = "\u1112\u1161\u11AB\u1100\u1173\u11AF";
    private String ligatureFF = "\uFB00";

    public void testNormalizeHangul() {
        assertEquals(hangul, Normalizer.normalize(hangul, Form.NFC));
        assertEquals(hangulDecomposed, Normalizer.normalize(hangul, Form.NFD));
        assertEquals(hangul, Normalizer.normalize(hangul, Form.NFKC));
        assertEquals(hangulDecomposed, Normalizer.normalize(hangul, Form.NFKD));
        assertEquals(hangul, Normalizer.normalize(hangulDecomposed, Form.NFC));
    }

    public void testNormalizeLigature() {
        assertEquals(ligatureFF, Normalizer.normalize(ligatureFF, Form.NFC));
        assertEquals(ligatureFF, Normalizer.normalize(ligatureFF, Form.NFD));
        assertEquals("ff", Normalizer.normalize(ligatureFF, Form.NFKC));
        assertEquals("ff", Normalizer.normalize(ligatureFF, Form.NFKD))
    }

    public void testIsNormalizedHangul() {
        assertTrue(Normalizer.isNormalized(hangul, Form.NFC));
        assertFalse(Normalizer.isNormalized(hangul, Form.NFD));
        assertTrue(Normalizer.isNormalized(hangul, Form.NFKC));
        assertFalse(Normalizer.normalize(hangul, Form.NFKD));
        assertFalse(Normalizer.isNormalized(hangulDecomposed, Form.NFC));
    }

    public void testNormalizeLigature() {
        assertTrue(Normalizer.isNormalized(ligatureFF, Form.NFC));
        assertTrue(Normalizer.isNormalized(ligatureFF, Form.NFD));
        assertFalse(Normalizer.isNormalized(ligatureFF, Form.NFKC));
        assertFalse(Normalizer.isNormalized(ligatureFF, Form.NFKD))
    }
}
