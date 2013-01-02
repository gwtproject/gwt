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
package com.google.gwt.tools.cldr;

import com.google.gwt.i18n.shared.GwtLocale;

import org.apache.tapestry.util.text.LocalizedProperties;
import org.unicode.cldr.util.Factory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Loads data needed to produce NumberConstants implementations.
 */
public class NumberConstantsProcessor extends Processor {

  public NumberConstantsProcessor(File outputDir, Factory cldrFactory, LocaleData localeData,
      LocaleData sharedLocaleData) {
    super(outputDir, cldrFactory, localeData, sharedLocaleData);
  }

  @Override
  protected void cleanupData() {
    localeData.removeDuplicates("numbers");
  }

  @Override
  protected void loadData() throws IOException {
    System.out.println("Loading data for number constants");
    localeData.addVersions(cldrFactory);
    // Temporary hack -- convert existing property files instead of reading CLDR data
    for (GwtLocale locale : localeData.getAllLocales()) {
      loadPropertyFile(locale);
    }
//    localeData.addEntries("numbers", cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "decimal", null, "decimal");
//    localeData.addEntries("numbers", cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "group", null, "group");
//    localeData.addEntries("numbers", cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "list", null, "list");
//    localeData.addEntries("numbers", cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "percentSign", null, "percentSign");
//    localeData.addEntries("numbers", cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "plusSign", null, "plusSign");
//    localeData.addEntries("numbers", cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "minusSign", null, "minusSign");
//    localeData.addEntries("numbers", cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "exponential", null, "exponential");
//    localeData.addEntries("numbers", cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "perMille", null, "perMille");
//    localeData.addEntries("numbers", cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "infinity", null, "infinity");
//    localeData.addEntries("numbers", cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "nan", null, "nan");
  }

  @Override
  protected void writeOutputFiles() throws IOException {
    setOverrides(true);
    for (GwtLocale locale : localeData.getNonEmptyLocales("numbers")) {
      GwtLocale parent = localeData.inheritsFrom(locale);
      if (parent == null) {
        parent = localeData.getGwtLocale("");
      }
      writeJavaOutputFile(locale, parent);
    }
  }

  private void loadPropertyFile(GwtLocale locale) {
    InputStream str = null;
    try {
      // Load NumberConstants from the system classpath, so gwt-user/core/src must be there
      str = ClassLoader.getSystemResourceAsStream(
          "com/google/gwt/i18n/client/constants/NumberConstantsImpl" + localeSuffix(locale)
          + ".properties");
      if (str != null) {
        LocalizedProperties props = new LocalizedProperties();
        props.load(str, "UTF-8");
        @SuppressWarnings("unchecked")
        Map<String, String> map = props.getPropertyMap();
        for (Map.Entry<String, String> entry : map.entrySet()) {
          localeData.addEntry("numbers", locale, entry.getKey(), entry.getValue());
        }
      }
      return;
    } catch (UnsupportedEncodingException notPossible) {
      // UTF-8 should always be defined
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.err.println("Unable to load NumberConstants.properties for " + locale + ", continuing");
  }

  private void writeJavaOutputFile(GwtLocale locale, GwtLocale parent) throws IOException,
      FileNotFoundException {
    String pkg = "com.google.gwt.i18n.shared.cldr";
    String myClass = "NumberConstantsImpl" + localeSuffix(locale);
    String superClass = "NumberConstantsImpl" + localeSuffix(parent);
    sharedLocaleData.addEntry("genClasses", locale, "NumberConstants", pkg + "." + myClass);
    PrintWriter pw = createOutputFile("user/src/" + pkg.replace('.', '/') +"/", myClass + ".java");
    printJavaHeader(pw);
    pw.println("package " + pkg + ";");
    if (locale.isDefault()) {
      pw.println();
      pw.println("import com.google.gwt.i18n.shared.NumberConstants;");
    }
    pw.println();
    printVersion(pw, locale, "// ");
    pw.println();
    pw.println("/**");
    pw.println(" * Implementation of NumberConstants for locale \"" + locale.toString() + "\".");
    pw.println(" */");
    pw.println("public class " + myClass + (locale.isDefault() ? " implements NumberConstants"
        : " extends " + superClass) + " {");
    Map<String, String> entries = localeData.getEntries("numbers", locale);
    Set<String> keySet = entries.keySet();
    String[] keys = keySet.toArray(new String[keySet.size()]);
    Arrays.sort(keys);
    for (String key : keys) {
      generateStringValue(pw, key, entries.get(key));
    }
    pw.println("}");
    pw.close();
  }
}
