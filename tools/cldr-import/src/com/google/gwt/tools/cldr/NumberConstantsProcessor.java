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

import com.google.gwt.codegen.server.JavaSourceWriterBuilder;
import com.google.gwt.codegen.server.SourceWriter;
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.i18n.shared.NumberConstants;

import org.apache.tapestry.util.text.LocalizedProperties;
import org.unicode.cldr.util.Factory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Loads data needed to produce NumberConstants implementations.
 */
public class NumberConstantsProcessor extends Processor {

  private static final String CATEGORY_NUMBERS = "numbers";

  public NumberConstantsProcessor(File outputDir, Factory cldrFactory, LocaleData localeData,
      LocaleData sharedLocaleData) {
    super(outputDir, cldrFactory, localeData, sharedLocaleData);
  }

  @Override
  protected void cleanupData() {
    localeData.removeDuplicates(CATEGORY_NUMBERS);
  }

  @Override
  protected void loadData() throws IOException {
    System.out.println("Loading data for number constants");
    localeData.addVersions(cldrFactory);
    // Temporary hack -- convert existing property files instead of reading CLDR data
    for (GwtLocale locale : localeData.getAllLocales()) {
      loadPropertyFile(locale);
    }
//    localeData.addEntries(CATEGORY_NUMBERS, cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "decimal", null, "decimal");
//    localeData.addEntries(CATEGORY_NUMBERS, cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "group", null, "group");
//    localeData.addEntries(CATEGORY_NUMBERS, cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "list", null, "list");
//    localeData.addEntries(CATEGORY_NUMBERS, cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "percentSign", null, "percentSign");
//    localeData.addEntries(CATEGORY_NUMBERS, cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "plusSign", null, "plusSign");
//    localeData.addEntries(CATEGORY_NUMBERS, cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "minusSign", null, "minusSign");
//    localeData.addEntries(CATEGORY_NUMBERS, cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "exponential", null, "exponential");
//    localeData.addEntries(CATEGORY_NUMBERS, cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "perMille", null, "perMille");
//    localeData.addEntries(CATEGORY_NUMBERS, cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "infinity", null, "infinity");
//    localeData.addEntries(CATEGORY_NUMBERS, cldrFactory, "//ldml/numbers/symbols[@numberSystem=\"latn\"]",
//        "nan", null, "nan");
  }

  @Override
  protected void writeOutputFiles() throws IOException {
    for (GwtLocale locale : localeData.getNonEmptyLocales(CATEGORY_NUMBERS)) {
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
          localeData.addEntry(CATEGORY_NUMBERS, locale, entry.getKey(), entry.getValue());
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

  private void writeJavaOutputFile(GwtLocale locale, GwtLocale parent) {
    String pkg = "com.google.gwt.i18n.shared.cldr";
    String myClass = "NumberConstantsImpl" + localeSuffix(locale);
    ProcessorCodeGenContext codeGen = new ProcessorCodeGenContext("user/src/");
    JavaSourceWriterBuilder jswb = codeGen.addClass("com.google.gwt.i18n.shared.cldr", myClass);
    jswb.setCallbacks(new PrintVersionCallback(locale));
    sharedLocaleData.addEntry(CATEGORY_GENCLASSES, locale, "NumberConstants", pkg + "." + myClass);
    if (locale.isDefault()) {
      jswb.addImport(NumberConstants.class);
      jswb.addImplementedInterface("NumberConstants");
    } else {
      String superClass = "NumberConstantsImpl" + localeSuffix(parent);
      jswb.setSuperclass(superClass);
    }
    jswb.setJavaDocCommentForClass("Implementation of NumberConstants for locale \""
        + locale.toString() + "\".");
    SourceWriter pw = jswb.createSourceWriter();
    Map<String, String> entries = localeData.getEntries(CATEGORY_NUMBERS, locale);
    Set<String> keySet = entries.keySet();
    String[] keys = keySet.toArray(new String[keySet.size()]);
    Arrays.sort(keys);
    for (String key : keys) {
      generateStringValue(pw, key, entries.get(key));
    }
    pw.close();
  }
}
