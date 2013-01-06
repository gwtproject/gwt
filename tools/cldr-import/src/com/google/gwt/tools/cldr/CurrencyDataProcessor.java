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
import com.google.gwt.i18n.rebind.CurrencyInfo;
import com.google.gwt.i18n.shared.GwtLocale;

import org.apache.tapestry.util.text.LocalizedProperties;
import org.unicode.cldr.util.CLDRFile;
import org.unicode.cldr.util.Factory;
import org.unicode.cldr.util.XPathParts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Loads data needed to produce DateTimeFormatInfo implementations.
 */
public class CurrencyDataProcessor extends Processor {

  private static final String CATEGORY_DEFAULT_CURRENCY = "defCurrency";
  private static final String KEY_DEFAULT = "!default";
  private static final String CATEGORY_CURRENCY = "currency";
  private Map<String, Integer> currencyFractions = new HashMap<String, Integer>();
  private int defaultCurrencyFraction;
  private Map<String, Integer> rounding = new HashMap<String, Integer>();

  private Set<String> stillInUse = new HashSet<String>();
  private LocalizedProperties currencyExtra;

  public CurrencyDataProcessor(File outputDir, Factory cldrFactory, LocaleData localeData,
      LocaleData sharedLocaleData) {
    super(outputDir, cldrFactory, localeData, sharedLocaleData);
  }

  @Override
  protected void cleanupData() {
    localeData.removeDuplicates(CATEGORY_CURRENCY);
    localeData.removeDuplicates(CATEGORY_DEFAULT_CURRENCY);
  }

  @Override
  protected void loadData() throws IOException {
    System.out.println("Loading data for currencies");
    localeData.addVersions(cldrFactory);
    loadLocaleIndependentCurrencyData();
    localeData.addCurrencyEntries(CATEGORY_CURRENCY, cldrFactory, currencyFractions,
        defaultCurrencyFraction, stillInUse, rounding);
    loadCurrencyExtra();
  }

  @Override
  protected void printPropertiesHeader(PrintWriter pw) {
    super.printPropertiesHeader(pw);
    pw.println();
    pw.println("#");
    pw.println("# The key is an ISO4217 currency code, and the value is of the " + "form:");
    pw.println("#   display name|symbol|decimal digits|not-used-flag|rounding");
    pw.println("# If a symbol is not supplied, the currency code will be used");
    pw.println("# If # of decimal digits is omitted, 2 is used");
    pw.println("# If a currency is not generally used, not-used-flag=1");
    pw.println("# If a currency should be rounded to a multiple of of the least significant");
    pw.println("#   digit, rounding will be present");
    pw.println("# Trailing empty fields can be omitted");
    pw.println();
  }

  @Override
  protected void writeOutputFiles() throws IOException {
    for (GwtLocale locale : localeData.getNonEmptyLocales()) {
      String defCurrencyCode = localeData.getEntry(CATEGORY_CURRENCY, locale, KEY_DEFAULT);
      Map<String, String> currencyMap = localeData.getEntries(CATEGORY_CURRENCY, locale);
      Map<String, CurrencyInfo> currencies = new HashMap<String, CurrencyInfo>();
      for (Map.Entry<String, String> entry : currencyMap.entrySet()) {
        String currencyCode = entry.getKey();
        String extra = currencyExtra.getProperty(currencyCode);
        currencies.put(currencyCode, new CurrencyInfo(currencyCode, entry.getValue(), extra));
      }
      CurrencyInfo defCurrency = currencies.get(defCurrencyCode);
      if (defCurrency == null && defCurrencyCode != null) {
        // Handle the case where the default currency is removed because it
        // inherits from the parent locale, but is a different default.
        defCurrency = new CurrencyInfo(defCurrencyCode, localeData.getInheritedEntry(CATEGORY_CURRENCY,
            locale, defCurrencyCode), currencyExtra.getProperty(defCurrencyCode));        
      }
      if (defCurrency != null || !currencies.isEmpty()) {
        GwtLocale parent = localeData.inheritsFrom(locale);
        if (parent == null) {
          parent = localeData.getGwtLocale("");
        }
        writeJavaOutputFile(locale, parent, currencies, defCurrency);
        writeJsOutputFile(locale, parent, currencies, defCurrency);
      }
    }
  }

  private void loadCurrencyExtra() {
    currencyExtra = new LocalizedProperties();
    InputStream str = null;
    try {
      // Load CurrencyExtra from the system classpath, so gwt-user/core/src must be there
      str = getClass().getClassLoader().getResourceAsStream(
          "com/google/gwt/tools/cldr/CurrencyExtra.properties");
      if (str != null) {
        currencyExtra.load(str, "UTF-8");
        return;
      }
    } catch (UnsupportedEncodingException notPossible) {
      // UTF-8 should always be defined
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.err.println("Unable to load CurrencyExtra.properties, continuing");
  }


  private void loadLocaleIndependentCurrencyData() {
    CLDRFile supp = cldrFactory.getSupplementalData();

    // load the table of default # of decimal places and rounding for each currency
    defaultCurrencyFraction = 0;
    Map<String, String> regionCurrencies = new HashMap<String, String>();
    XPathParts parts = new XPathParts();
    Iterator<String> iterator = supp.iterator("//supplementalData/currencyData/fractions/info");
    while (iterator.hasNext()) {
      String path = iterator.next();
      parts.set(supp.getFullXPath(path));
      Map<String, String> attr = parts.findAttributes("info");
      if (attr == null) {
        continue;
      }
      String curCode = attr.get("iso4217");
      int digits = Integer.valueOf(attr.get("digits"));
      if ("DEFAULT".equalsIgnoreCase(curCode)) {
        defaultCurrencyFraction = digits;
      } else {
        currencyFractions.put(curCode, digits);
      }
      int roundingDigits = Integer.valueOf(attr.get("rounding"));
      if (roundingDigits != 0) {
        rounding.put(curCode, roundingDigits);
      }
    }

    // find which currencies are still in use in some region, everything else
    // should be marked as deprecated
    iterator = supp.iterator("//supplementalData/currencyData/region");
    while (iterator.hasNext()) {
      String path = iterator.next();
      parts.set(supp.getFullXPath(path));
      Map<String, String> attr = parts.findAttributes(CATEGORY_CURRENCY);
      if (attr == null) {
        continue;
      }
      String region = parts.findAttributeValue("region", "iso3166");
      String curCode = attr.get("iso4217");
      if ("ZZ".equals(region) || "false".equals(attr.get("tender")) || "XXX".equals(curCode)) {
        // ZZ is an undefined region, XXX is an unknown currency code (and needs
        // to be special-cased because it is listed as used in Anartica!)
        continue;
      }
      String to = attr.get("to");
      if (to == null) {
        stillInUse.add(curCode);
        regionCurrencies.put(region, curCode);
      }
    }

    localeData.summarizeTerritoryEntries(CATEGORY_CURRENCY, new RegionLanguageData(cldrFactory),
        KEY_DEFAULT, regionCurrencies);
    GwtLocale defaultLocale = localeData.getGwtLocale("");
    if (localeData.getEntry(CATEGORY_CURRENCY, defaultLocale, KEY_DEFAULT) == null) {
      // The Euro is the closest we have to a global currency, so use it as
      // the ultimate default.
      localeData.addEntry(CATEGORY_CURRENCY, defaultLocale, KEY_DEFAULT, "EUR");
    }
  }

  private void writeJavaOutputFile(GwtLocale locale, GwtLocale parent,
      Map<String, CurrencyInfo> currencies, CurrencyInfo defCurrency) {
    String myClass = "CurrencyListImpl" + localeSuffix(locale);
    String pkg = "com.google.gwt.i18n.shared.cldr";
    ProcessorCodeGenContext codeGen = new ProcessorCodeGenContext("user/src/");
    JavaSourceWriterBuilder jswb = codeGen.addClass("com.google.gwt.i18n.shared.cldr", myClass);
    jswb.setCallbacks(new PrintVersionCallback(locale));
    sharedLocaleData.addEntry(CATEGORY_GENCLASSES, locale, "CurrencyList", pkg + "." + myClass);
    String superClass = "CurrencyListImpl" + (locale.isDefault() ? "Base" : localeSuffix(parent));
    jswb.setSuperclass(superClass);
    jswb.addImport("com.google.gwt.i18n.shared.CurrencyData");
    jswb.addImport("com.google.gwt.i18n.shared.impl.CurrencyDataImpl");
    if (locale.isDefault()) {
      jswb.addImport("com.google.gwt.i18n.shared.impl.CurrencyListImplBase");
    }
    if (!currencies.isEmpty()) {
      jswb.addImport("java.util.Map;");
    }
    jswb.setJavaDocCommentForClass(" * Pure Java implementation of CurrencyList for locale \""
        + locale.toString() + "\".");
    SourceWriter pw = jswb.createSourceWriter();
    if (defCurrency != null) {
      pw.println();
      pw.println("@Override");
      pw.println("public CurrencyData getDefault() {");
      pw.indentln("return " + defCurrency.getJava() + ";");
      pw.println("}");
    }
    if (!currencies.isEmpty()) {
      String[] keys = currencies.keySet().toArray(new String[currencies.size()]);
      Arrays.sort(keys);
      pw.println();
      pw.println("@Override");
      pw.println("protected Map<String, CurrencyData> loadCurrencies() {");
      pw.indent();
      pw.println("Map<String, CurrencyData> result = super.loadCurrencies();");
      for (String curCode : keys) {
        if (KEY_DEFAULT.equals(curCode)) {
          continue;
        }
        pw.println("result.put(\"" + curCode + "\", " + currencies.get(curCode).getJava()
            + ");");
      }
      pw.println("return result;");
      pw.outdent();
      pw.println("}");
      pw.println();
      pw.println("@Override");
      pw.println("protected Map<String, String> loadCurrencyNames() {");
      pw.indent();
      pw.println("Map<String, String> result = super.loadCurrencyNames();");
      for (String curCode : keys) {
        if (KEY_DEFAULT.equals(curCode)) {
          continue;
        }
        pw.println("result.put(\"" + curCode + "\", \""
            + quote(currencies.get(curCode).getDisplayName()) + "\");");
      }
      pw.println("return result;");
      pw.outdent();
      pw.println("}");
    }
    pw.close();
  }

  private void writeJsOutputFile(GwtLocale locale, GwtLocale parent,
      Map<String, CurrencyInfo> currencies, CurrencyInfo defCurrency) {
    String myClass = "CurrencyListImpl" + localeSuffix(locale);
    String superClass = "CurrencyListImpl" + (locale.isDefault() ? "Base" : localeSuffix(parent));
    ProcessorCodeGenContext codeGen = new ProcessorCodeGenContext("user/src/");
    JavaSourceWriterBuilder jswb = codeGen.addClass("com.google.gwt.i18n.client.cldr", myClass);
    jswb.setCallbacks(new PrintVersionCallback(locale));
    jswb.setSuperclass(superClass);
    if (!currencies.isEmpty()) {
      jswb.addImport("com.google.gwt.core.client.JavaScriptObject");
    }
    if (defCurrency != null) {
      jswb.addImport("com.google.gwt.i18n.shared.CurrencyData");
    }
    if (locale.isDefault()) {
      jswb.addImport("com.google.gwt.i18n.client.impl.CurrencyListImplBase");
    }
    jswb.setJavaDocCommentForClass("JS implementation of CurrencyList for locale \""
        + locale.toString() + "\".");
    SourceWriter pw = jswb.createSourceWriter();
    if (defCurrency != null) {
      pw.println();
      pw.println("@Override");
      pw.println("public native CurrencyData getDefault() /*-{");
      pw.indentln("return " + defCurrency.getJson() + ";");
      pw.println("}-*/;");
    }
    if (!currencies.isEmpty()) {
      String[] keys = currencies.keySet().toArray(new String[currencies.size()]);
      Arrays.sort(keys);
      pw.println();
      pw.println("@Override");
      pw.println("protected JavaScriptObject loadCurrencies() {");
      pw.indentln("return overrideMap(super.loadCurrencies(), loadCurrenciesOverride());");
      pw.println("}");
      pw.println();
      pw.println("@Override");
      pw.println("protected JavaScriptObject loadCurrencyNames() {");
      pw.indentln("return overrideMap(super.loadCurrencyNames(), loadCurrencyNamesOverride());");
      pw.println("}");
      pw.println();
      pw.println("private native JavaScriptObject loadCurrenciesOverride() /*-{");
      pw.indent();
      pw.println("return {");
      pw.indent();
      for (String curCode : keys) {
        if (KEY_DEFAULT.equals(curCode)) {
          continue;
        }
        pw.println("\"" + curCode + "\": " + currencies.get(curCode).getJson() + ",");
      }
      pw.outdent();
      pw.println("};");
      pw.outdent();
      pw.println("}-*/;");
      pw.println();
      pw.println("private native JavaScriptObject loadCurrencyNamesOverride() /*-{");
      pw.indent();
      pw.println("return {");
      pw.indent();
      for (String curCode : keys) {
        if (KEY_DEFAULT.equals(curCode)) {
          continue;
        }
        pw.println("\"" + curCode + "\": \"" + quote(currencies.get(curCode).getDisplayName())
            + "\",");
      }
      pw.outdent();
      pw.println("};");
      pw.outdent();
      pw.println("}-*/;");
    }
    pw.close();
  }
}
