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
import com.google.gwt.i18n.shared.CurrencyList;
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.i18n.shared.impl.CurrencyDataHelper;
import com.google.gwt.i18n.shared.impl.CurrencyDataImpl;

import org.unicode.cldr.util.XPathParts;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Process data about currencies; generates implementations of {@link CurrencyList}. 
 */
public class CurrencyDataProcessor extends Processor {

  /**
   * Represents data about a single currency in a particular locale from CLDR.
   */
  public static class Currency {

    private static boolean equalsNullCheck(Object a, Object b) {
      if (a == null) {
        return b == null;
      }
      return a.equals(b);
    }

    private static int hashCodeNullCheck(Object obj) {
      return obj == null ? 0 : obj.hashCode();
    }

    private final String code;

    private int decimalDigits;

    private String decimalSeparator;

    private String groupingSeparator;

    private boolean inUse;

    private String pattern;

    private int rounding;

    private String symbol;

    public Currency(String code) {
      this.code = code;
    }

    /**
     * Encode the currency data as needed by CurrencyListGenerator.
     * 
     * Changes here must be reflected in {@link CurrencyInfo#fromData(String, String)}.
     * 
     * @param currency
     * @return a string containing the property file entry for the specified
     *         currency
     */
    public String encodeCurrencyData() {
      StringBuilder buf = new StringBuilder();
      appendBlankForNull(buf, symbol);
      buf.append('|').append(decimalDigits);
      buf.append('|').append(inUse);
      buf.append('|').append(rounding);
      buf.append('|');
      appendBlankForNull(buf, decimalSeparator);
      buf.append('|');
      appendBlankForNull(buf, groupingSeparator);
      buf.append('|');
      appendBlankForNull(buf, pattern);
      return buf.toString();
    }

    private void appendBlankForNull(StringBuilder buf, String str) {
      if (str != null) {
        buf.append(str);
      }
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof Currency)) {
        return false;
      }
      Currency other = (Currency) obj;
      return code.equals(other.code) && equalsNullCheck(symbol, other.symbol)
          && equalsNullCheck(pattern, other.pattern)
          && equalsNullCheck(decimalSeparator, other.decimalSeparator)
          && equalsNullCheck(groupingSeparator, other.groupingSeparator)
          && decimalDigits == other.decimalDigits && inUse == other.inUse
          && rounding == other.rounding;
    }

    public String getCode() {
      return code;
    }

    /**
     * @return the number of decimal digits this currency is commonly displayed
     *         with.
     */
    public int getDecimalDigits() {
      return decimalDigits;
    }

    public String getDecimalSeparator() {
      return decimalSeparator;
    }

    public String getGroupingSeparator() {
      return groupingSeparator;
    }

    public String getPattern() {
      return pattern;
    }

    public int getRounding() {
      return rounding;
    }

    public String getSymbol() {
      return symbol;
    }

    @Override
    public int hashCode() {
      return code.hashCode() + 19 * hashCodeNullCheck(symbol) + 23 * hashCodeNullCheck(pattern)
          + 29 * hashCodeNullCheck(decimalSeparator) + 31 * hashCodeNullCheck(groupingSeparator)
          + 37 * decimalDigits + (inUse ? 41 : 0) + 43 * rounding;
    }

    /**
     * @return true if this currency is still in regular use.
     */
    public boolean isInUse() {
      return inUse;
    }

    public void setDecimalDigits(int decimalDigits) {
      this.decimalDigits = decimalDigits;
    }

    public void setDecimalSeparator(String decimalSeparator) {
      this.decimalSeparator = decimalSeparator;
    }

    public void setGroupingSeparator(String groupingSeparator) {
      this.groupingSeparator = groupingSeparator;
    }

    public void setInUse(boolean inUse) {
      this.inUse = inUse;
    }

    public void setPattern(String pattern) {
      this.pattern = pattern;
    }

    public void setRounding(int rounding) {
      this.rounding = rounding;
    }

    public void setSymbol(String symbol) {
      this.symbol = symbol;
    }

    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(code);
      if (symbol != null) {
        buf.append(" (");
        buf.append(symbol);
        buf.append(")");
      }
      return buf.toString();
    }
  }

  private static class CurrencyInfo extends CurrencyDataImpl {

    private int rounding;

    public CurrencyInfo(String currencyCode, String currencySymbol, int flagsAndPrecision,
        String currencyPattern, String decimalSeparator, String groupingSeparator, int rounding) {
      super(currencyCode, currencySymbol, flagsAndPrecision, currencyPattern, decimalSeparator,
          groupingSeparator, rounding);
    }

    public String getJava() {
      StringBuilder buf = new StringBuilder();
      buf.append("new CurrencyDataImpl(").append(quotedOrNull(currencyCode)).append(", ");
      buf.append(quotedOrNull(currencySymbol)).append(", ");
      buf.append(flagsAndPrecision).append(", ");
      buf.append(quotedOrNull(currencyPattern)).append(", ");
      buf.append(quotedOrNull(decimalSeparator)).append(", ");
      buf.append(quotedOrNull(groupingSeparator)).append(", ");
      buf.append(rounding);
      return buf.append(')').toString();
    }

    public String getJson() {
      StringBuilder buf = new StringBuilder();
      buf.append("[ ").append(quotedOrNull(currencyCode)).append(", ");
      buf.append(quotedOrNull(currencySymbol)).append(", ");
      buf.append(flagsAndPrecision).append(", ");
      buf.append(quotedOrNull(currencyPattern)).append(", ");
      buf.append(quotedOrNull(decimalSeparator)).append(", ");
      buf.append(quotedOrNull(groupingSeparator)).append(", ");
      buf.append(rounding).append(' ');
      return buf.append(']').toString();
    }

    /**
     * Decode currency data from a string-serialized form.  This must be able to process the
     * output of {@link Currency#encodeCurrencyData()}.
     * 
     * @param currencyCode
     * @param combined encoded string, fields separated with "|".
     * @return
     */
    public static CurrencyInfo fromData(String currencyCode, String combined) {
      String[] fields = combined.split("\\|");
      String symbol = currencyCode;
      int digits = 2;
      boolean inUse = true;
      int rounding = 0;
      boolean suffixPosition = false;
      boolean fixedPosition = false;
      boolean spaceForced = false;
      boolean spacingFixed = false;
      String currencyPattern = null;
      String decimalSeparator = null;
      String groupingSeparator = null;
      switch (fields.length) {
        default:
          // excess fields?
        case 7:
          if (!fields[6].isEmpty()) currencyPattern = fields[6];
        case 6:
          if (!fields[5].isEmpty()) groupingSeparator = fields[5];
        case 5:
          if (!fields[4].isEmpty()) decimalSeparator = fields[4];
        case 4:
          if (!fields[3].isEmpty()) rounding = Integer.parseInt(fields[3], 10);
        case 3:
          if (!fields[2].isEmpty()) inUse = Boolean.parseBoolean(fields[2]);
        case 2:
          if (!fields[1].isEmpty()) digits = Integer.parseInt(fields[1], 10);
        case 1:
          if (!fields[0].isEmpty()) symbol = fields[0];
        case 0:
          // no data
      }
      return new CurrencyInfo(currencyCode, symbol, CurrencyDataHelper.encode(digits,
          !inUse, suffixPosition, fixedPosition, spaceForced, spacingFixed), currencyPattern,
          decimalSeparator, groupingSeparator, rounding);
    }
  }

  private static final String CATEGORY_DEFAULT_CURRENCY = "defCurrency";
  private static final String KEY_DEFAULT = "!default";
  private static final String CATEGORY_CURRENCY = "currency";
  private static final String CATEGORY_CURRENCY_NAMES = "currency-names";

  private Map<String, Integer> currencyFractions = new HashMap<String, Integer>();
  private int defaultCurrencyFraction;
  private Map<String, Integer> rounding = new HashMap<String, Integer>();
  private Set<String> stillInUse = new HashSet<String>();
  private Map<GwtLocale, Map<String, Currency>> currencyMap = new HashMap<GwtLocale, Map<String, Currency>>();

  public CurrencyDataProcessor(Processors processors) {
    super(processors);
    addCurrencyEntries(CATEGORY_CURRENCY, currencyFractions, defaultCurrencyFraction, stillInUse,
        rounding);
  }

  @Override
  public void addDependencies() {
    // NUMBERS_EXTRA is required
    processors.requireProcessor(NumberFormatInfoProcessor.class);
  }

  @Override
  public void cleanupData() {
    for (Map.Entry<GwtLocale, Map<String, Currency>> entry : currencyMap.entrySet()) {
      GwtLocale locale = entry.getKey();
      Map<String, String> extra = localeData.getEntries(LocaleData.CATEGORY_NUMBERS_EXTRA, locale);
      Map<String, Currency> map = entry.getValue();
      for (Currency currency : map.values()) {
        String code = currency.getCode();
        if (currency.getDecimalSeparator() == null && extra.containsKey("currencyDecimal")) {
          currency.setDecimalSeparator(extra.get("currencyDecimal"));
        }
        localeData.addEntry(CATEGORY_CURRENCY, locale, code, currency.encodeCurrencyData());
      }
    }

    // copy any currencies that are only mentioned elsewhere to the default locale, but leave
    // out locale-specific things like name, symbol, and separators
    GwtLocale defaultLocale = localeData.getGwtLocale("");
    for (Map.Entry<GwtLocale, Map<String, Currency>> entry : currencyMap.entrySet()) {
      for (Currency currency : entry.getValue().values()) {
        String code = currency.getCode();
        if (localeData.getEntry(CATEGORY_CURRENCY, defaultLocale, code) == null) {
          // ok to trash currency here as we don't need it any more
          currency.setSymbol(null);
          currency.setDecimalSeparator(null);
          currency.setGroupingSeparator(null);
          localeData.addEntry(CATEGORY_CURRENCY, defaultLocale, code, currency.encodeCurrencyData());
          localeData.addEntryIfMissing(CATEGORY_CURRENCY_NAMES, defaultLocale, code, code);
        }
      }
    }

    localeData.removeDuplicates(CATEGORY_CURRENCY);
    localeData.removeDuplicates(CATEGORY_CURRENCY_NAMES);
    localeData.removeDuplicates(CATEGORY_DEFAULT_CURRENCY);
  }

  /**
   * Add currency entries for all locales.
   * 
   * @param category
   * @param cldrFactory
   * @param currencyFractions map of currency fraction data extracted from
   *          locale-independent data
   * @param defaultCurrencyFraction
   * @param stillInUse
   * @param rounding 
   */
  public void addCurrencyEntries(String category, Map<String, Integer> currencyFractions,
      int defaultCurrencyFraction, Set<String> stillInUse, Map<String, Integer> rounding) {
    new CurrencyProcessor();

  }

  private class CurrencyProcessor {

    public CurrencyProcessor() {
      new CurrencyEntries();
      new CurrencyFractions();
      new CurrencyRegions();
    }

    private class CurrencyEntries implements QueryMatchCallback {

      public CurrencyEntries() {
        cldrData.registerLocale(new XPathQuery("//ldml/numbers/currencies"), this);
      }

      @Override
      public void process(GwtLocale locale, XPathParts parts, String value) {
        Map<String, String> attr = parts.findAttributes("currency");
        if (attr == null) {
          return;
        }
        String currencyCode = attr.get("type");
        Map<String, Currency> map = currencyMap.get(locale);
        if (map == null) {
          map = new HashMap<String, Currency>();
          currencyMap.put(locale, map);
        }
        Currency currency = map.get(currencyCode);
        if (currency == null) {
          currency = new Currency(currencyCode);
          if (currencyFractions.containsKey(currencyCode)) {
            currency.setDecimalDigits(currencyFractions.get(currencyCode));
          } else {
            currency.setDecimalDigits(defaultCurrencyFraction);
          }
          currency.setInUse(stillInUse.contains(currencyCode));
          Integer roundingMult = rounding.get(currencyCode);
          if (roundingMult != null) {
            currency.setRounding(roundingMult);
          }
          map.put(currencyCode, currency);
        }
        String field = parts.getElement(4);
        attr = parts.findAttributes(field);
        if (attr == null) {
          attr = Collections.emptyMap();
        }
        String draft = attr.get("draft");
        if ("symbol".equalsIgnoreCase(field)) {
          currency.setSymbol(value);
        } else if ("displayName".equalsIgnoreCase(field)) {
          if (attr.get("count") != null) {
            // We don't care about currency "count" names
            return;
          }
          localeData.addEntry(CATEGORY_CURRENCY_NAMES, locale, currencyCode, value, draft != null);
        } else if ("pattern".equalsIgnoreCase(field)) {
          currency.setPattern(value);
        } else if ("decimal".equalsIgnoreCase(field)) {
          currency.setDecimalSeparator(value);
        } else if ("group".equalsIgnoreCase(field)) {
          currency.setGroupingSeparator(value);
        } else {
          System.err.println("Ignoring unknown field \"" + field + "\" on currency data for \""
              + currencyCode + "\"");
        }
      }
    }
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
  public void writeOutputFiles() throws IOException {
    for (GwtLocale locale : localeData.getNonEmptyLocales(CATEGORY_CURRENCY,
        CATEGORY_CURRENCY_NAMES)) {
      String defCurrencyCode = localeData.getEntry(CATEGORY_CURRENCY, locale, KEY_DEFAULT);
      Map<String, String> currencyMap = localeData.getEntries(CATEGORY_CURRENCY, locale);
      Map<String, CurrencyInfo> currencies = new HashMap<String, CurrencyInfo>();
      for (Map.Entry<String, String> entry : currencyMap.entrySet()) {
        String currencyCode = entry.getKey();
        currencies.put(currencyCode, CurrencyInfo.fromData(currencyCode, entry.getValue()));
      }
      CurrencyInfo defCurrency = currencies.get(defCurrencyCode);
      if (defCurrency == null && defCurrencyCode != null) {
        // Handle the case where the default currency is removed because it
        // inherits from the parent locale, but is a different default.
        defCurrency = CurrencyInfo.fromData(defCurrencyCode, localeData.getInheritedEntry(CATEGORY_CURRENCY,
            locale, defCurrencyCode));
      }
      if (defCurrency != null || !currencies.isEmpty()) {
        GwtLocale parent = localeData.inheritsFrom(CATEGORY_CURRENCY, locale);
        if (parent == null) {
          parent = localeData.getGwtLocale("");
        }
        writeJavaOutputFile(locale, parent, currencies, defCurrency);
        writeJsOutputFile(locale, parent, currencies, defCurrency);
      }
    }
  }

  private class CurrencyFractions implements QueryMatchCallback {

    public CurrencyFractions() {
      defaultCurrencyFraction = 0;
      cldrData.registerSupplemental("supplementalData",
          new XPathQuery("//supplementalData/currencyData/fractions/info"), this);
    }

    @Override
    public void process(GwtLocale locale, XPathParts parts, String value) {
      Map<String, String> attr = parts.findAttributes("info");
      if (attr == null) {
        return;
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
  }

  private class CurrencyRegions implements QueryMatchCallback {

    private Map<String, String> regionCurrencies = new HashMap<String, String>();

    public CurrencyRegions() {
      cldrData.registerSupplemental("supplementalData",
          new XPathQuery("//supplementalData/currencyData/region"), this);
      // default to Euro
      localeData.addEntry(CATEGORY_CURRENCY, localeData.getGwtLocale(""), KEY_DEFAULT, "EUR");
      cldrData.addPostProcessor(new Runnable() {
        @Override
        public void run() {
          summarize();
        }
      });

    }

    protected void summarize() {
      localeData.summarizeTerritoryEntries(CATEGORY_CURRENCY, cldrData.getRegionLanguageData(),
          KEY_DEFAULT, regionCurrencies);
    }

    @Override
    public void process(GwtLocale locale, XPathParts parts, String value) {
      Map<String, String> attr = parts.findAttributes(CATEGORY_CURRENCY);
      if (attr == null) {
        return;
      }
      String region = parts.findAttributeValue("region", "iso3166");
      String curCode = attr.get("iso4217");
      if ("ZZ".equals(region) || "false".equals(attr.get("tender")) || "XXX".equals(curCode)) {
        // ZZ is an undefined region, "XXX is an unknown currency code (and needs
        // to be special-cased because it is listed as used in Anartica!)
        return;
      }
      String to = attr.get("to");
      if (to == null) {
        stillInUse.add(curCode);
        regionCurrencies.put(region, curCode);
      }
    }
  }

  private void writeJavaOutputFile(GwtLocale locale, GwtLocale parent,
      Map<String, CurrencyInfo> currencies, CurrencyInfo defCurrency) {
    String myClass = "CurrencyListImpl" + localeSuffix(locale);
    String pkg = "com.google.gwt.i18n.shared.impl.cldr";
    ProcessorCodeGenContext codeGen = new ProcessorCodeGenContext("user/src/");
    JavaSourceWriterBuilder jswb = codeGen.addClass(pkg, myClass);
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
    if (!currencies.isEmpty() || (currencies.containsKey(KEY_DEFAULT) && currencies.size() == 1)) {
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
    }
    Map<String, String> nameMap = localeData.getEntries(CATEGORY_CURRENCY_NAMES, locale);
    if (!nameMap.isEmpty()) {
      String[] keys = nameMap.keySet().toArray(new String[nameMap.size()]);
      Arrays.sort(keys);
      pw.println();
      pw.println("@Override");
      pw.println("protected Map<String, String> loadCurrencyNames() {");
      pw.indent();
      pw.println("Map<String, String> result = super.loadCurrencyNames();");
      for (String curCode : keys) {
        String name = nameMap.get(curCode);
        if (KEY_DEFAULT.equals(curCode) || name == null) {
          continue;
        }
        
        pw.println("result.put(\"" + curCode + "\", \"" + quote(name) + "\");");
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
    String pkg = "com.google.gwt.i18n.client.impl.cldr";
    String superClass = "CurrencyListImpl" + (locale.isDefault() ? "Base" : localeSuffix(parent));
    ProcessorCodeGenContext codeGen = new ProcessorCodeGenContext("user/src/");
    JavaSourceWriterBuilder jswb = codeGen.addClass(pkg, myClass);
    jswb.setCallbacks(new PrintVersionCallback(locale));
    sharedLocaleData.addEntry(CATEGORY_GENCLASSES_CLIENT, locale, "CurrencyList", pkg + "."
        + myClass);
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
    boolean hasCurrenciesOverride = !currencies.isEmpty() || (currencies.containsKey(KEY_DEFAULT)
        && currencies.size() == 1);
    String[] keys = currencies.keySet().toArray(new String[currencies.size()]);
    Arrays.sort(keys);
    if (hasCurrenciesOverride) {
      pw.println();
      pw.println("@Override");
      pw.println("protected JavaScriptObject loadCurrencies() {");
      pw.indentln("return overrideMap(super.loadCurrencies(), loadCurrenciesOverride());");
      pw.println("}");
    }
    Map<String, String> nameMap = localeData.getEntries(CATEGORY_CURRENCY_NAMES, locale);
    if (!nameMap.isEmpty()) {
      pw.println();
      pw.println("@Override");
      pw.println("protected JavaScriptObject loadCurrencyNames() {");
      pw.indentln("return overrideMap(super.loadCurrencyNames(), loadCurrencyNamesOverride());");
      pw.println("}");
    }
    if (hasCurrenciesOverride) {
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
    }
    if (!nameMap.isEmpty()) {
      String[] nameKeys = nameMap.keySet().toArray(new String[nameMap.size()]);
      Arrays.sort(nameKeys);
      pw.println();
      pw.println("private native JavaScriptObject loadCurrencyNamesOverride() /*-{");
      pw.indent();
      pw.println("return {");
      pw.indent();
      for (String curCode : nameKeys) {
        if (KEY_DEFAULT.equals(curCode)) {
          continue;
        }
        String name = nameMap.get(curCode);
        pw.println("\"" + curCode + "\": \"" + quote(name) + "\",");
      }
      pw.outdent();
      pw.println("};");
      pw.outdent();
      pw.println("}-*/;");
    }
    pw.close();
  }
}
