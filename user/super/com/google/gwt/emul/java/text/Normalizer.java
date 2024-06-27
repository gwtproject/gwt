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
package java.text;

import javaemul.internal.JsUtils;
import jsinterop.annotations.JsType;

/**
 * Emulation of <a href="https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/Normalizer.html">java.text.Normalizer</a>.
 */
public final class Normalizer {

  public enum Form {
    /** Canonical decomposition. */
    NFD,
    /** Canonical decomposition followed by composition. */
    NFC,
    /** Compatibility decomposition. */
    NFKD,
    /** Compatibility decomposition followed by composition. */
    NFKC
  }

  public static String normalize(CharSequence input, Form form) {
    return JsUtils.<NativeString>uncheckedCast(input.toString()).normalize(form.name());
  }

  public static boolean isNormalized(CharSequence input, Form form) {
    String str = input.toString();
    return str.equals(normalize(str, form));
  }

  @JsType(isNative = true, name = "String", namespace = "<window>")
  private static class NativeString {
    public native String normalize(String form);
  }
}

