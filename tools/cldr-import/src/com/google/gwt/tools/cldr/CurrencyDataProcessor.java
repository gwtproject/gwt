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

import com.google.gwt.i18n.rebind.CurrencyInfo;
import com.google.gwt.i18n.shared.GwtLocale;

import org.apache.tapestry.util.text.LocalizedProperties;
import org.unicode.cldr.util.CLDRFile;
import org.unicode.cldr.util.Factory;
import org.unicode.cldr.util.XPathParts;

import java.io.File;
import java.io.FileNotFoundException;
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
    localeData.removeDuplicates("currency");
    localeData.removeDuplicates("defCurrency");
  }

  @Override
  protected void loadData() throws IOException {
    System.out.println("Loading data for currencies");
    localeData.addVersions(cldrFactory);
    loadLocaleIndependentCurrencyData();
    localeData.addCurrencyEntries("currency", cldrFactory, currencyFractions,
        defaultCurrencyFraction, stillInUse, rounding);
    loadCurrencyExtra();
  }

  @Override
  protected void printHeader(PrintWriter pw) {
    printPropertiesHeader(pw);
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
      // generateClientProperties(locale);

      String defCurrencyCode = localeData.getEntry("currency", locale, "!default");
      Map<String, String> currencyMap = localeData.getEntries("currency", locale);
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
        defCurrency = new CurrencyInfo(defCurrencyCode, localeData.getInheritedEntry("currency",
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

  @SuppressWarnings("unused")
  private void generateClientProperties(GwtLocale locale) throws IOException, FileNotFoundException {
    String path = "client/impl/cldr/CurrencyData";
    PrintWriter pw = createOutputFile(path + Processor.localeSuffix(locale) + ".properties");
    printHeader(pw);
    pw.println();
    printVersion(pw, locale, "# ");
    Map<String, String> map = localeData.getEntries("currency", locale);
    String[] keys = new String[map.size()];
    map.keySet().toArray(keys);
    Arrays.sort(keys);

    for (String key : keys) {
      if ("!default".equals(key)) {
        // skip the default currency when writing the properties file
        continue;
      }
      pw.print(key);
      pw.print(" = ");
      pw.println(map.get(key));
    }
    pw.close();
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
      Map<String, String> attr = parts.findAttributes("currency");
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
    
    localeData.summarizeTerritoryEntries("currency", new RegionLanguageData(cldrFactory),
        "!default", regionCurrencies);
    GwtLocale defaultLocale = localeData.getGwtLocale("");
    if (localeData.getEntry("currency", defaultLocale, "!default") == null) {
      // The Euro is the closest we have to a global currency, so use it as
      // the ultimate default.
      localeData.addEntry("currency", defaultLocale, "!default", "EUR");
    }
  }

  private void writeJavaOutputFile(GwtLocale locale, GwtLocale parent,
      Map<String, CurrencyInfo> currencies, CurrencyInfo defCurrency)
      throws IOException, FileNotFoundException {
    String myClass = "CurrencyListImpl" + localeSuffix(locale);
    String pkg = "com.google.gwt.i18n.shared.cldr";
    sharedLocaleData.addEntry("genClasses", locale, "CurrencyList", pkg + "." + myClass);
    String path = "shared/cldr/" + myClass + ".java";
    String superClass = "CurrencyListImpl" + (locale.isDefault() ? "Base" : localeSuffix(parent));
    PrintWriter pw = createOutputFile(path);
    printJavaHeader(pw);
    pw.println("package " + pkg + ";");
    pw.println();
    pw.println("import com.google.gwt.i18n.shared.CurrencyData;");
    pw.println("import com.google.gwt.i18n.shared.impl.CurrencyDataImpl;");
    if (locale.isDefault()) {
      pw.println("import com.google.gwt.i18n.shared.impl.CurrencyListImplBase;");
    }
    if (!currencies.isEmpty()) {
      pw.println();
      pw.println("import java.util.Map;");
    }
    pw.println();
    pw.println("// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA");
    Map<String, String> map = localeData.getEntries("version", locale);
    for (Map.Entry<String, String> entry : map.entrySet()) {
      pw.println("//  " + entry.getKey() + "=" + entry.getValue());
    }
    pw.println();
    pw.println("/**");
    pw.println(" * Pure Java implementation of CurrencyList for locale \"" + locale.toString()
        + "\".");
    pw.println(" */");
    pw.println("public class " + myClass + " extends " + superClass
        + " {");
    if (defCurrency != null) {
      pw.println();
      pw.println("  @Override");
      pw.println("  public CurrencyData getDefault() {");
      pw.println("    return " + defCurrency.getJava() + ";");
      pw.println("  }");
    }
    if (!currencies.isEmpty()) {
      String[] keys = currencies.keySet().toArray(new String[currencies.size()]);
      Arrays.sort(keys);
      pw.println();
      pw.println("  @Override");
      pw.println("  protected Map<String, CurrencyData> loadCurrencies() {");
      pw.println("    Map<String, CurrencyData> result = super.loadCurrencies();");
      for (String curCode : keys) {
        if ("!default".equals(curCode)) {
          continue;
        }
        pw.println("    result.put(\"" + curCode + "\", " + currencies.get(curCode).getJava()
            + ");");
      }
      pw.println("    return result;");
      pw.println("  }");
      pw.println();
      pw.println("  @Override");
      pw.println("  protected Map<String, String> loadCurrencyNames() {");
      pw.println("    Map<String, String> result = super.loadCurrencyNames();");
      for (String curCode : keys) {
        if ("!default".equals(curCode)) {
          continue;
        }
        pw.println("    result.put(\"" + curCode + "\", \""
            + quote(currencies.get(curCode).getDisplayName()) + "\");");
      }
      pw.println("    return result;");
      pw.println("  }");
    }
    pw.println("}");
    pw.close();
  }

  private void writeJsOutputFile(GwtLocale locale, GwtLocale parent,
      Map<String, CurrencyInfo> currencies, CurrencyInfo defCurrency)
      throws IOException, FileNotFoundException {
    String myClass = "CurrencyListImpl" + localeSuffix(locale);
    String path = "com/google/gwt/i18n/client/cldr/" + myClass + ".java";
    String superClass = "CurrencyListImpl" + (locale.isDefault() ? "Base" : localeSuffix(parent));
    PrintWriter pw = createOutputFile("user/src/",
        path);
    printJavaHeader(pw);
    pw.println("package com.google.gwt.i18n.client.cldr;");
    pw.println();
    if (!currencies.isEmpty()) {
      pw.println("import com.google.gwt.core.client.JavaScriptObject;");
    }
    if (defCurrency != null) {
      pw.println("import com.google.gwt.i18n.shared.CurrencyData;");
    }
    if (locale.isDefault()) {
      pw.println("import com.google.gwt.i18n.client.impl.CurrencyListImplBase;");
    }
    pw.println();
    pw.println("// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA");
    Map<String, String> map = localeData.getEntries("version", locale);
    for (Map.Entry<String, String> entry : map.entrySet()) {
      pw.println("//  " + entry.getKey() + "=" + entry.getValue());
    }
    pw.println();
    pw.println("/**");
    pw.println(" * JS implementation of CurrencyList for locale \"" + locale.toString()
        + "\".");
    pw.println(" */");
    pw.println("public class " + myClass + " extends " + superClass
        + " {");
    if (defCurrency != null) {
      pw.println();
      pw.println("  @Override");
      pw.println("  public native CurrencyData getDefault() /*-{");
      pw.println("    return " + defCurrency.getJson() + ";");
      pw.println("  }-*/;");
    }
    if (!currencies.isEmpty()) {
      String[] keys = currencies.keySet().toArray(new String[currencies.size()]);
      Arrays.sort(keys);
      pw.println();
      pw.println("  @Override");
      pw.println("  protected JavaScriptObject loadCurrencies() {");
      pw.println("    return overrideMap(super.loadCurrencies(), loadCurrenciesOverride());");
      pw.println("  }");
      pw.println();
      pw.println("  @Override");
      pw.println("  protected JavaScriptObject loadCurrencyNames() {");
      pw.println("    return overrideMap(super.loadCurrencyNames(), loadCurrencyNamesOverride());");
      pw.println("  }");
      pw.println();
      pw.println("  private native JavaScriptObject loadCurrenciesOverride() /*-{");
      pw.println("    return {");
      for (String curCode : keys) {
        if ("!default".equals(curCode)) {
          continue;
        }
        pw.println("      \"" + curCode + "\": " + currencies.get(curCode).getJson() + ",");
      }
      pw.println("    };");
      pw.println("  }-*/;");
      pw.println();
      pw.println("  private native JavaScriptObject loadCurrencyNamesOverride() /*-{");
      pw.println("    return {");
      for (String curCode : keys) {
        if ("!default".equals(curCode)) {
          continue;
        }
        pw.println("      \"" + curCode + "\": \""
            + quote(currencies.get(curCode).getDisplayName()) + "\",");
      }
      pw.println("    };");
      pw.println("  }-*/;");
    }
    pw.println("}");
    pw.close();
  }
}
