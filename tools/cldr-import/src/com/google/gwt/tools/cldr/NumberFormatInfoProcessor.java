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
package com.google.gwt.tools.cldr;

import com.google.gwt.codegen.server.JavaSourceWriterBuilder;
import com.google.gwt.codegen.server.SourceWriter;
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.i18n.shared.impl.NumberFormatInfoImplBase;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableMap;

import org.unicode.cldr.util.XPathParts;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads data needed to produce NumberConstants implementations.
 */
public class NumberFormatInfoProcessor extends Processor {

  public static class LocaleNumberSymbols {
    public String defaultNumberSystem;
    public Map<String, Map<String, String>> symbols = new HashMap<String, Map<String, String>>();
    public Map<String, Map<String, String>> extra = new HashMap<String, Map<String,String>>();

    public Map<String, String> getSymbolsMap(String numberSystem) {
      Map<String, String> map = symbols.get(numberSystem);
      if (map == null) {
        map = new HashMap<String, String>();
        symbols.put(numberSystem, map);
      }
      return map;
    }

    public Map<String, String> getExtrasMap(String numberSystem) {
      Map<String, String> map = extra.get(numberSystem);
      if (map == null) {
        map = new HashMap<String, String>();
        extra.put(numberSystem, map);
      }
      return map;
    }
  }

  private static final String PACKAGE = "com.google.gwt.i18n.shared.impl.cldr";
  private static final String CATEGORY_NUMBERSYSTEMS = "numbersystems";

  protected Map<String, String> digitsMap = new HashMap<String, String>();
  protected Map<GwtLocale, LocaleNumberSymbols> numberSymbols
     = new HashMap<GwtLocale, LocaleNumberSymbols>();

  protected LocaleNumberSymbols getNumberSymbols(GwtLocale locale) {
    LocaleNumberSymbols lns = numberSymbols.get(locale);
    if (lns == null) {
      lns = new LocaleNumberSymbols();
      numberSymbols.put(locale, lns);
    }
    return lns;
  }

  public NumberFormatInfoProcessor(Processors processors) {
    super(processors);
    new NumberingSystems(); 
    new LoadLocaleNumbers();
  }

  private class LoadLocaleNumbers implements QueryMatchCallback {

    private final Map<String, String> keymap;
    private final Pattern formatPattern;

    public LoadLocaleNumbers() {
      cldrData.registerLocale(new XPathQuery("//ldml/numbers"), this);
      keymap = ImmutableMap.<String, String>builder()
          .put("decimal", "decimalSeparator")
          .put("group", "groupingSeparator")
          .put("list", "listSeparator")
          .put("percentSign", "percent")
          .put("plusSign", "plusSign")
          .put("minusSign", "minusSign")
          .put("exponential", "exponentialSymbol")
          .put("perMille", "perMille")
          .put("infinity", "infinity")
          .put("nan", "notANumber")
          .build();
      formatPattern = Pattern.compile("^(currency|decimal|percent|scientific)Formats");
    }

    @Override
    public void process(GwtLocale locale, XPathParts parts, String value) {
      LocaleNumberSymbols lns = getNumberSymbols(locale);
      String tag = parts.getElement(-1);
      String topTag = parts.getElement(2);
      String numberSystem = parts.getAttributeValue(2, "numberSystem");
      if (numberSystem == null) {
        if ("defaultNumberingSystem".equals(tag)) {
          lns.defaultNumberSystem = value;
        }
        return;
      }
      if ("symbols".equals(topTag)) {
        Map<String, String> attr = parts.findAttributes("symbols");
        if (attr != null && attr.containsKey("type")) {
          return;
        }
        String key = keymap.get(tag);
        if (key == null) {
          // anything extra goes into extra
          lns.getExtrasMap(numberSystem).put(tag, value);
        } else {
          lns.getSymbolsMap(numberSystem).put(key, value);
        }
        return;
      }
      Matcher m = formatPattern.matcher(topTag);
      if (m.matches()) {
        // looking for //ldml/numbers/fooFormats/fooFormatLength/fooFormat/pattern
        String formatType = m.group(1);
        if (!(formatType + "FormatLength").equals(parts.getElement(3))
            || !(formatType + "Format").equals(parts.getElement(4))
            || !"pattern".equals(parts.getElement(5))) {
          return;
        }
        String type = parts.getAttributeValue(3, "type");
        if (type != null) {
          // if *FormatLength has a type attribute, it is a plural-like pattern
          return;
        }
        type = parts.getAttributeValue(4, "type");
        if (type != null && !"standard".equals(type)) {
          // FUTURE(jat): handle other pattern types, like accounting?
          return;
        }
        type = parts.getAttributeValue(5, "type");
        if (type != null && !"standard".equals(type)) {
          // TODO(jat): support compact formats, like 128k
          return;
        }
        lns.getSymbolsMap(numberSystem).put(formatType + "Pattern", value);
        return;
      }
    }
  }

  /**
   * Collects numbering system data.
   */
  private class NumberingSystems implements QueryMatchCallback {
    //supplementalData/numberingSystems/numberingSystem id="arab" type="numeric" digits="٠١٢٣٤٥٦٧٨٩"/>

    public NumberingSystems() {
      cldrData.registerSupplemental("numberingSystems",
          new XPathQuery("//supplementalData/numberingSystems/numberingSystem"), this);
    }

    @Override
    public void process(GwtLocale locale, XPathParts parts, String value) {
      Map<String, String> attr = parts.findAttributes("numberingSystem");
      if (attr == null || !"numeric".equals(attr.get("type"))) {
        // FUTURE(jat): can we support algorithmic number systems?
        return;
      }
      String id = attr.get("id");
      String digits = attr.get("digits");
      digitsMap.put(id, digits);
    }
  }

  @Override
  public void cleanupData() {
    generateLocaleEntries();
    localeData.removeDuplicates(CATEGORY_NUMBERSYSTEMS);
    localeData.removeDuplicates(LocaleData.CATEGORY_NUMBERS);
    localeData.removeDuplicates(LocaleData.CATEGORY_NUMBERS_EXTRA);

    // *AFTER* we have removed inherited duplicates, remove entries from base locales which
    // duplicate the number-system-specific entries
    LocaleNumberSymbols lns = numberSymbols.get(localeData.getGwtLocale(""));
    for (GwtLocale locale : localeData.getNonEmptyLocales(LocaleData.CATEGORY_NUMBERS)) {
      if (locale.isDefault()) {
        continue;
      }
      GwtLocale parent = localeData.inheritsFrom(LocaleData.CATEGORY_NUMBERS, locale);
      if (parent != null && !parent.isDefault()) {
        continue;
      }
      String numberSystem = localeData.getEntry(CATEGORY_NUMBERSYSTEMS, locale, "!default");
      if (numberSystem == null) {
        numberSystem = "latn";
      }
      Map<String, String> baseMap = lns.symbols.get(numberSystem);
      if (baseMap != null) {
        localeData.removeDuplicates(LocaleData.CATEGORY_NUMBERS, locale, baseMap);
        if (localeData.getEntries(LocaleData.CATEGORY_NUMBERS, locale).isEmpty()) {
          sharedLocaleData.addEntry(CATEGORY_GENCLASSES, locale, "NumberFormatInfo",
              PACKAGE + "." + getClassName("_" + numberSystem));
        }
      }
    }
  }

  private void generateLocaleEntries() {
    LocaleNumberSymbols base = numberSymbols.get(localeData.getGwtLocale(""));
    if (base.defaultNumberSystem == null) {
      base.defaultNumberSystem = "latn";
    }
    for (Map.Entry<GwtLocale, LocaleNumberSymbols> entry : numberSymbols.entrySet()) {
      GwtLocale locale = entry.getKey();
      LocaleNumberSymbols lns = entry.getValue();
      localeData.addEntry(CATEGORY_NUMBERSYSTEMS, locale, "!default", lns.defaultNumberSystem);
      localeData.addEntries(LocaleData.CATEGORY_NUMBERS, locale,
          base.symbols.get(lns.defaultNumberSystem));
      localeData.addEntries(LocaleData.CATEGORY_NUMBERS, locale,
          lns.symbols.get(lns.defaultNumberSystem));
      localeData.addEntries(LocaleData.CATEGORY_NUMBERS_EXTRA, locale,
          lns.extra.get(lns.defaultNumberSystem));
    }
  }

  @Override
  public void writeOutputFiles() throws IOException {
    writeBaseNumberFiles();
    for (GwtLocale locale : localeData.getNonEmptyLocales(LocaleData.CATEGORY_NUMBERS)) {
      GwtLocale parent = localeData.inheritsFrom(LocaleData.CATEGORY_NUMBERS, locale);
      if (parent == null) {
        parent = localeData.getGwtLocale("");
      }
      Map<String, String> symbols = localeData.getEntries(LocaleData.CATEGORY_NUMBERS, locale);
      String defaultNumberSystem = localeData.getInheritedEntry(CATEGORY_NUMBERSYSTEMS, locale,
          "!default");
      writeJavaOutputFile(locale, parent, defaultNumberSystem, symbols);
    }
  }

  private void writeBaseNumberFiles() {
    GwtLocale root = localeData.getGwtLocale("");
    LocaleNumberSymbols lns = getNumberSymbols(root);
    for (Map.Entry<String, Map<String, String>> entry : lns.symbols.entrySet()) {
      String numberSystem = entry.getKey();
      writeNumberFormatInfo("_" + numberSystem, entry.getValue(), digitsMap.get(numberSystem), null);
    }
  }

  private String writeNumberFormatInfo(String suffix, Map<String, String> values, String digits,
      String superSuffix) {
    String className = getClassName(suffix);
    String myClass = className;
    ProcessorCodeGenContext codeGen = new ProcessorCodeGenContext("user/src/");
    JavaSourceWriterBuilder jswb = codeGen.addClass(PACKAGE, myClass);
    jswb.setCallbacks(new PrintCldrVersionCallback());
    if (superSuffix == null) {
      jswb.addImport(NumberFormatInfoImplBase.class);
      jswb.setSuperclass("NumberFormatInfoImplBase");
    } else {
      jswb.setSuperclass(getClassName(superSuffix));
    }
    jswb.setJavaDocCommentForClass("Implementation of NumberFormatInfo for \""
        + suffix + "\".");
    SourceWriter pw = jswb.createSourceWriter();
    Map<String, String> entries = values;
    Set<String> keySet = entries.keySet();
    String[] keys = keySet.toArray(new String[keySet.size()]);
    Arrays.sort(keys);
    for (String key : keys) {
      generateStringValue(pw, key, entries.get(key));
    }
    if (digits != null && !digits.isEmpty()) {
      // TODO(jat): is the zero digit sufficient, or should we just store all the digits?
      generateCharValue(pw, "zeroDigit", digits.charAt(0));
    }
    pw.close();
    return jswb.getFullyQualifiedClassName();
  }

  private void writeJavaOutputFile(GwtLocale locale, GwtLocale parent, String numberSystem,
      Map<String, String> symbols) {
    if (locale.isDefault()) {
      // root locale has no changes over base number system files, just record the right one to use
      sharedLocaleData.addEntry(CATEGORY_GENCLASSES, locale, "NumberFormatInfo",
          PACKAGE + "." + getClassName("_" + numberSystem));
      return;
    }
    // base class will be numbersystem implementation for top-level locales
    String parentSuffix = parent.isDefault() ? "_" + numberSystem : parent.toString();
    String digits = null;
    String myNumberSystem = localeData.getEntry(CATEGORY_NUMBERSYSTEMS, locale, "!default");
    if (myNumberSystem != null) {
      digits = digitsMap.get(locale);
    }
    sharedLocaleData.addEntry(CATEGORY_GENCLASSES, locale, "NumberFormatInfo",
        writeNumberFormatInfo(locale.toString(), symbols, digits, parentSuffix));
  }

  private static String getClassName(String numberSystem) {
    return "NumberFormatInfoImpl_" + numberSystem;
  }
}
