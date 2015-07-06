/*
 * Copyright 2014 Google Inc.
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

/**
 * A very simple emulation of Locale for shared-code patterns like
 * {@code String.toUpperCase(Locale.US)}.
 * <p>
 * Note: Any changes to this class should put into account the assumption that was made in rest of
 * the JRE emulation.
 */
public class Locale {

  public static final Locale ROOT = new Locale() {
    @Override
    public String toString() {
      return "";
    }
  };

  public static final Locale ENGLISH = new Locale() {
    @Override
    public String toString() {
      return "en";
    }
  };

  public static final Locale FRENCH = new Locale() {
    @Override
    public String toString() {
      return "fr";
    }
  };

  public static final Locale GERMAN = new Locale() {
    @Override
    public String toString() {
      return "de";
    }
  };

  public static final Locale ITALIAN = new Locale() {
    @Override
    public String toString() {
      return "it";
    }
  };

  public static final Locale JAPANESE = new Locale() {
    @Override
    public String toString() {
      return "ja";
    }
  };

  public static final Locale KOREAN = new Locale() {
    @Override
    public String toString() {
      return "ko";
    }
  };

  public static final Locale CHINESE = new Locale() {
    @Override
    public String toString() {
      return "zh";
    }
  };

  public static final Locale SIMPLIFIED_CHINESE = new Locale() {
    @Override
    public String toString() {
      return "zh_CN";
    }
  };

  public static final Locale TRADITIONAL_CHINESE = new Locale() {
    @Override
    public String toString() {
      return "zh_TW";
    }
  };

  public static final Locale FRANCE = new Locale() {
    @Override
    public String toString() {
      return "fr_FR";
    }
  };

  public static final Locale GERMANY = new Locale() {
    @Override
    public String toString() {
      return "de_DE";
    }
  };

  public static final Locale GERMANY = new Locale() {
    @Override
    public String toString() {
      return "it_IT";
    }
  };

  public static final Locale JAPAN = new Locale() {
    @Override
    public String toString() {
      return "ja_JP";
    }
  };

  public static final Locale KOREA = new Locale() {
    @Override
    public String toString() {
      return "ko_KR";
    }
  };

  public static final Locale CHINA = SIMPLIFIED_CHINESE;

  public static final Locale PRC = SIMPLIFIED_CHINESE;

  public static final Locale TAIWAN = TRADITIONAL_CHINESE;

  public static final Locale UK = new Locale() {
    @Override
    public String toString() {
      return "en_GB";
    }
  };

  public static final Locale US = new Locale() {
    @Override
    public String toString() {
      return "en_US";
    }
  };

  public static final Locale CANADA = new Locale() {
    @Override
    public String toString() {
      return "en_CA";
    }
  };

  public static final Locale CANADA_FRENCH = new Locale() {
    @Override
    public String toString() {
      return "fr_CA";
    }
  };


  private static Locale defaultLocale = new Locale() {
    @Override
    public String toString() {
      return "unknown";
    }
  };

  /**
   * Returns an instance that represents the browser's default locale (not necessarily the one
   * defined by 'gwt.locale').
   */
  public static Locale getDefault() {
    return defaultLocale;
  }

  // Hidden as we don't support manual creation of Locales.
  private Locale() { }
}
