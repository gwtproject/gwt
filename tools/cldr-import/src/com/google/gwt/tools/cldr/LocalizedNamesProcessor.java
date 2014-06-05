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

import com.google.gwt.codegen.server.CodeGenUtils;
import com.google.gwt.codegen.server.JavaSourceWriterBuilder;
import com.google.gwt.codegen.server.SourceWriter;
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.tools.cldr.RegionLanguageData.RegionPopulation;

import com.ibm.icu.text.MessageFormat;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Extract localized names from CLDR data.
 */
public class LocalizedNamesProcessor extends Processor {

  private static class IndexedName implements Comparable<IndexedName> {

    private final int index;
    private final CollationKey key;

    public IndexedName(Collator collator, int index, String value) {
      this.index = index;
      this.key = collator.getCollationKey(value);
    }

    @Override
    public int compareTo(IndexedName o) {
      return key.compareTo(o.key);
    }

    /**
     * @return index of this name.
     */
    public int getIndex() {
      return index;
    }
  }

  private static final String CATEGORY_LANGUAGE = "language";
  private static final String CATEGORY_LOCALE_DISPLAY_NAMES = "localeDisplayNames";
  private static final String CATEGORY_LOCALE_PATTERN = "localePattern";
  private static final String CATEGORY_LOCALE_SEPARATOR = "localeSeparator";
  private static final String CATEGORY_SCRIPT = "script";
  private static final String CATEGORY_TERRITORY = "territory";
  private static final String CATEGORY_VARIANT = "variant";

  private static final String KEY_LIKELYORDER = "!likelyorder";
  private static final String KEY_SORTORDER = "!sortorder";

  /**
   * Split a list of region codes into an array.
   * 
   * @param regionList comma-separated list of region codes
   * @return array of region codes, null if none
   */
  private static String[] getRegionOrder(String regionList) {
    String[] split = null;
    if (regionList != null && regionList.length() > 0) {
      split = regionList.split(",");
    }
    return split;
  }

  public LocalizedNamesProcessor(Processors processors) {
    super(processors);
    addEntries(CATEGORY_LOCALE_PATTERN, "//ldml/localeDisplayNames/localeDisplayPattern",
        "localePattern", null);
    addEntries(CATEGORY_LOCALE_SEPARATOR, "//ldml/localeDisplayNames/localeDisplayPattern",
        "localeSeparator", null);
    addEntries(CATEGORY_TERRITORY, "//ldml/localeDisplayNames/territories", "territory", "type");
    addEntries(CATEGORY_LANGUAGE, "//ldml/localeDisplayNames/languages", "language", "type");
    addEntries(CATEGORY_SCRIPT, "//ldml/localeDisplayNames/scripts", "script", "type");
    addEntries(CATEGORY_VARIANT, "//ldml/localeDisplayNames/variants", "variant", "type");

  }

  @Override
  public void cleanupData() {
    localeData.copyLocaleData("en", "default", CATEGORY_TERRITORY, "languages", "scripts",
        "variants");
    copyNativeDisplayNames();
    // Generate a sort order before removing duplicates
    for (GwtLocale locale : localeData.getNonEmptyLocales(CATEGORY_TERRITORY)) {
      // TODO(jat): deal with language population data that has a script
      Map<String, String> map = localeData.getEntries(CATEGORY_TERRITORY, locale);
      List<String> countryCodes = new ArrayList<String>();
      for (String regionCode : map.keySet()) {
        // only include real country codes
        if (!"ZZ".equals(regionCode) && regionCode.length() == 2) {
          countryCodes.add(regionCode);
        }
      }
      Locale javaLocale =
          new Locale(locale.getLanguageNotNull(), locale.getRegionNotNull(), locale
              .getVariantNotNull());
      Collator collator = Collator.getInstance(javaLocale);
      IndexedName[] names = new IndexedName[countryCodes.size()];
      for (int i = 0; i < names.length; ++i) {
        names[i] = new IndexedName(collator, i, map.get(countryCodes.get(i)));
      }
      Arrays.sort(names);
      StringBuilder buf = new StringBuilder();
      boolean first = true;
      for (int i = 0; i < names.length; ++i) {
        if (first) {
          first = false;
        } else {
          buf.append(',');
        }
        buf.append(countryCodes.get(names[i].getIndex()));
      }
      localeData.addEntry(CATEGORY_TERRITORY, locale, KEY_SORTORDER, buf.toString());
    }
    for (GwtLocale locale : localeData.getAllLocales()) {
      Set<RegionPopulation> regions = getRegionsForLocale(locale);
      StringBuilder buf = new StringBuilder();
      if (!locale.isDefault()) {
        int count = 0;
        for (RegionPopulation region : regions) {
          // only keep the first 10, and stop if there aren't many speakers
          if (++count > 10 || region.getLiteratePopulation() < 3000000) {
            break;
          }
          if (count > 1) {
            buf.append(',');
          }
          buf.append(region.getRegion());
        }
      }
      localeData.addEntry(CATEGORY_TERRITORY, locale, KEY_LIKELYORDER, buf.toString());

      if (!locale.isDefault()) {
        // generate display names for each locale
        for (GwtLocale nameLocale : localeData.getAllLocales()) {
          String displayName = getDisplayNameForLocale(locale, nameLocale);
          if (displayName != null) {
            localeData.addEntry(CATEGORY_LOCALE_DISPLAY_NAMES, locale, nameLocale.toString(), displayName);
          }
        }
      }
    }
    localeData.copyLocaleData("en", "default", CATEGORY_LOCALE_DISPLAY_NAMES);
    /*
     * We must write the properties files used for server-side before we remove
     * duplicates, because those files need to be complete.
     */
    writeLocalizedNamesProperties();

    localeData.removeDuplicates(CATEGORY_TERRITORY);
    localeData.removeDuplicates(CATEGORY_LANGUAGE);
    localeData.removeDuplicates(CATEGORY_SCRIPT);
    localeData.removeDuplicates(CATEGORY_VARIANT);
    localeData.removeDuplicates(CATEGORY_LOCALE_DISPLAY_NAMES);
  }

  @Override
  public void writeOutputFiles() throws IOException {
    writeLocalizedNames();
    writeLocaleDisplayNames();
  }

  /**
   * Copy each locale's native display name to sharedLocaleData.
   */
  private void copyNativeDisplayNames() {
    for (GwtLocale locale : localeData.getAllLocales()) {
      if (locale.isDefault()) {
        continue;
      }
      String result = getDisplayNameForLocale(locale, locale);
      sharedLocaleData.addEntry(CATEGORY_LOCALE_NATIVE_DISPLAY_NAME, locale, locale.toString(), result);
    }
  }

  /**
   * @param locale
   * @param likelyOrder
   * @param sortOrder
   * @param regionCodesWithNames
   * @param namesMap
   */
  private void generateClientLocale(GwtLocale locale, Map<String, String> namesMap,
      List<String> regionCodesWithNames, String[] sortOrder, String[] likelyOrder) {
    ProcessorCodeGenContext codeGen = new ProcessorCodeGenContext("user/src/");
    String pkg = "com.google.gwt.i18n.client.impl.cldr";
    String myClass = "LocalizedNamesImpl" + localeSuffix(locale);
    JavaSourceWriterBuilder jswb = codeGen.addClass(pkg, myClass);
    sharedLocaleData.addEntry(CATEGORY_GENCLASSES_CLIENT, locale, "LocalizedNames", pkg + "."
        + myClass);
    jswb.setCallbacks(new PrintVersionCallback(locale));
    if (!regionCodesWithNames.isEmpty()) {
      jswb.addImport("com.google.gwt.core.client.JavaScriptObject");
    }
    if (locale.isDefault()) {
      jswb.addImport("com.google.gwt.i18n.client.impl.LocalizedNamesImplBase");
    }
    jswb.setJavaDocCommentForClass("Localized names for the \"" + locale + "\" locale.");
    if (locale.isDefault()) {
      jswb.setSuperclass("LocalizedNamesImplBase");
    } else {
      jswb.setSuperclass("LocalizedNamesImpl"
          + localeSuffix(localeData.inheritsFrom(CATEGORY_TERRITORY, locale)));
    }
    SourceWriter pw = jswb.createSourceWriter();
    if (likelyOrder != null) {
      writeStringListMethod(pw, "loadLikelyRegionCodes", likelyOrder);
    }
    if (!regionCodesWithNames.isEmpty()) {
      pw.println();
      pw.println("@Override");
      pw.println("protected JavaScriptObject loadNameMap() {");
      pw.indentln("return overrideMap(super.loadNameMap(), nameMapOverrides());");
      pw.println("}");
    }
    if (sortOrder != null) {
      writeStringListMethod(pw, "loadSortedRegionCodes", sortOrder);
    }
    if (!regionCodesWithNames.isEmpty()) {
      pw.println();
      pw.println("private native JavaScriptObject nameMapOverrides() /*-{");
      generateNativeMap(pw, regionCodesWithNames, namesMap);
      pw.println("}-*/;");
    }
    pw.close();
  }

  private void generateLocaleDisplayNames(GwtLocale locale, Map<String, String> namesMap) {
    String myClass = "LocaleDisplayNamesImpl" + localeSuffix(locale);
    String pkg = "com.google.gwt.i18n.shared.impl.cldr";
    ProcessorCodeGenContext codeGen = new ProcessorCodeGenContext("user/src/");
    JavaSourceWriterBuilder jswb = codeGen.addClass(pkg, myClass);
    jswb.setCallbacks(new PrintVersionCallback(locale));
    sharedLocaleData.addEntry(CATEGORY_GENCLASSES, locale, "LocaleDisplayNames", pkg + "."
        + myClass);
    String superClass;
    if (locale.isDefault()) {
      superClass = "LocaleDisplayNamesImplBase";
      jswb.addImport("com.google.gwt.i18n.shared.impl.LocaleDisplayNamesImplBase");
    } else {
      GwtLocale parent = localeData.inheritsFrom(CATEGORY_LOCALE_DISPLAY_NAMES, locale);
      superClass = "LocaleDisplayNamesImpl" + localeSuffix(parent);
    }
    jswb.setSuperclass(superClass);
    jswb.addImport("java.util.Map");
    jswb.setJavaDocCommentForClass("Locale display names for the \"" + locale + "\" locale.\n");
    SourceWriter pw = jswb.createSourceWriter();
    pw.println();
    pw.println("@Override");
    pw.println("protected Map<String, String> loadDisplayNames() {");
    pw.indent();
    pw.println("Map<String, String> result = super.loadDisplayNames();");
    Set<String> keySet = namesMap.keySet();
    String[] keys = keySet.toArray(new String[keySet.size()]);
    Arrays.sort(keys);
    for (String key : keys) {
      pw.println("result.put(" + CodeGenUtils.asStringLiteral(key) + ", "
          + CodeGenUtils.asStringLiteral(namesMap.get(key)) + ");");
    }
    pw.println("return result;");
    pw.outdent();
    pw.println("}");
    pw.close();
  }

  /**
   * @param pw
   * @param regionCodesWithNames
   * @param namesMap
   */
  private void generateNativeMap(SourceWriter pw, List<String> regionCodesWithNames,
      Map<String, String> namesMap) {
    pw.println("return {");
    pw.indent();
    boolean firstLine = true;
    for (String code : regionCodesWithNames) {
      String name = namesMap.get(code);
      if (name != null && !name.equals(code)) {
        if (firstLine) {
          firstLine = false;
        } else {
          pw.println(",");
        }
        pw.print("\"" + quote(code) + "\": \"" + quote(name) + "\"");
      }
    }
    pw.println();
    pw.outdent();
    pw.println("};");
  }

  /**
   * @param locale
   * @param likelyOrder
   * @param sortOrder
   * @param regionCodesWithNames
   * @param namesMap
   */
  private void generateSharedJavaLocale(GwtLocale locale, Map<String, String> namesMap,
      List<String> regionCodesWithNames, String[] sortOrder, String[] likelyOrder) {
    GwtLocale parent = localeData.inheritsFrom(CATEGORY_TERRITORY, locale);
    String myClass = "LocalizedNamesImpl" + localeSuffix(locale);
    String pkg = "com.google.gwt.i18n.shared.impl.cldr";
    ProcessorCodeGenContext codeGen = new ProcessorCodeGenContext("user/src/");
    JavaSourceWriterBuilder jswb = codeGen.addClass(pkg, myClass);
    jswb.setCallbacks(new PrintVersionCallback(locale));
    String superClass;
    if (locale.isDefault()) {
      superClass = "LocalizedNamesImplBase";
    } else {
      superClass = "LocalizedNamesImpl" + localeSuffix(parent);
    }
    jswb.setSuperclass(superClass);
    sharedLocaleData.addEntry(CATEGORY_GENCLASSES, locale, "LocalizedNames", pkg + "." + myClass);
    if (locale.isDefault()) {
      jswb.addImport("com.google.gwt.i18n.shared.impl.LocalizedNamesImplBase");
    }
    jswb.setJavaDocCommentForClass("Localized names for the \"" + locale + "\" locale.\n");
    SourceWriter pw = jswb.createSourceWriter();
    if (likelyOrder != null) {
      writeStringListMethod(pw, "getLikelyRegionCodes", likelyOrder);
    }
    if (!regionCodesWithNames.isEmpty()) {
      pw.println();
      pw.println("@Override");
      pw.println("protected void ensureNameMap() {");
      pw.indent();
      pw.println("super.ensureNameMap();");
      for (String code : regionCodesWithNames) {
        String name = namesMap.get(code);
        if (name != null && !name.equals(code)) {
          pw.println("nameMap.put(\"" + quote(code) + "\", \"" + quote(name) + "\");");
        }
      }
      pw.outdent();
      pw.println("}");
    }
    if (sortOrder != null) {
      writeStringListMethod(pw, "loadSortedRegionCodes", sortOrder, false);
    }
    pw.close();
  }

  /**
   * Get the display name for {@code locale}, as displayed in {@code targetLocale}.
   *
   * @param targetLocale
   * @param locale
   * @return
   */
  private String getDisplayNameForLocale(GwtLocale targetLocale, GwtLocale locale) {
    if (locale.isDefault()) {
      // Should there be a better name for this?
      return "Default of last resort";
    }
    String localePattern = localeData.getEntry(CATEGORY_LOCALE_PATTERN, targetLocale, "value");
    String localeSeparator = localeData.getEntry(CATEGORY_LOCALE_SEPARATOR, targetLocale, "value");
    String language = locale.getLanguage();
    String region = locale.getRegion();
    String baseName = localeData.getInheritedEntry(CATEGORY_LANGUAGE, targetLocale, language);
    if (baseName == null) {
      baseName = language;
    }
    // Collect the additional descriptions
    StringBuilder buf = new StringBuilder();

    // If the region is part of a special name for the language, use it.  Otherwise, add it to the
    // list of additional descriptions.
    if (region != null) {
      String val = localeData.getInheritedEntry(CATEGORY_LANGUAGE, targetLocale, language + "_" + region);
      if (val != null) {
        baseName = val;
      } else {
        val = localeData.getInheritedEntry(CATEGORY_TERRITORY, targetLocale, region);
        if (val != null) {
          buf.append(val);
        }
      }
    }
    // Add in the script if present, but only if it isn't the default script
    String script = locale.getScript();
    GwtLocale localeDefScript = localeData.getLocaleFactory().fromComponents(locale.getLanguage(),
        null, locale.getRegion(), locale.getVariant());
    if (script != null && !locale.usesSameScript(localeDefScript)) {
      String val = localeData.getInheritedEntry(CATEGORY_SCRIPT, targetLocale, script);
      if (val != null) {
        if (buf.length() > 0) {
          buf.append(localeSeparator);
        }
        buf.append(val);
      }
    }
    // Add in the variant if present
    String variant = locale.getVariant();
    if (variant != null) {
      String val = localeData.getInheritedEntry(CATEGORY_VARIANT, targetLocale, variant);
      if (val != null) {
        if (buf.length() > 0) {
          buf.append(localeSeparator);
        }
        buf.append(val);
      }
    }
    if (buf.length() > 0) {
      return MessageFormat.format(localePattern, baseName, buf.toString());
    } else {
      return baseName;
    }
  }

  /**
   * @param locale
   * @return region populations speaking this language
   */
  private Set<RegionPopulation> getRegionsForLocale(GwtLocale locale) {
    RegionLanguageData regionLanguageData = cldrData.getRegionLanguageData();
    Set<RegionPopulation> retVal = regionLanguageData.getRegions(locale.getLanguageNotNull() + "_"
        + locale.getScriptNotNull());
    if (retVal.isEmpty()) {
      retVal = regionLanguageData.getRegions(locale.getLanguageNotNull());
    }
    return retVal;
  }

  private void writeLocaleDisplayNames() {
    for (GwtLocale locale : localeData.getNonEmptyLocales(CATEGORY_LOCALE_DISPLAY_NAMES)) {
      Map<String, String> namesMap = localeData.getEntries(CATEGORY_LOCALE_DISPLAY_NAMES, locale);
      generateLocaleDisplayNames(locale, namesMap);
    }
  }

  private void writeLocalizedNames() {
    for (GwtLocale locale : localeData.getNonEmptyLocales(CATEGORY_TERRITORY)) {
      Map<String, String> namesMap = localeData.getEntries(CATEGORY_TERRITORY, locale);
      List<String> regionCodesWithNames = new ArrayList<String>();
      for (String regionCode : namesMap.keySet()) {
        if (!regionCode.startsWith("!")) {
          // skip entries which aren't actually region codes
          regionCodesWithNames.add(regionCode);
        }
      }
      String[] sortOrder = getRegionOrder(namesMap.get(KEY_SORTORDER));
      String[] likelyOrder = getRegionOrder(namesMap.get(KEY_LIKELYORDER));
      if (regionCodesWithNames.isEmpty() && sortOrder == null && likelyOrder == null) {
        // nothing to do
        return;
      }
      // sort for deterministic output
      Collections.sort(regionCodesWithNames);
      generateClientLocale(locale, namesMap, regionCodesWithNames, sortOrder, likelyOrder);
      generateSharedJavaLocale(locale, namesMap, regionCodesWithNames, sortOrder, likelyOrder);
    }
  }

  private void writeLocalizedNamesProperties() {
    for (GwtLocale locale : localeData.getAllLocales()) {
      PrintWriter pw = null;
      try {
        pw = createOutputFile("client/cldr/LocaleDisplayNames" + localeSuffix(locale)
            + ".properties");
        printPropertiesHeader(pw);
        pw.println();
        printVersion(pw, locale, "# ");
        Map<String, String> map = localeData.getEntries(CATEGORY_LOCALE_DISPLAY_NAMES, locale);
        Set<String> keySet = map.keySet();
        String[] keys = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keys);
        for (String localeName : keys) {
          pw.println(localeName + "=" + map.get(localeName));
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      } finally {
        if (pw != null) {
          pw.close();
        }
      }
    }
  }


  /**
   * Generate a method which returns an array of string constants.
   * 
   * @param pw SourceWriter to write on
   * @param methodName the name of the method to create
   * @param values the list of string values to return.
   */
  private void writeStringListMethod(SourceWriter pw, String methodName, String[] values) {
    writeStringListMethod(pw, methodName, values, true);
  }

  /**
   * Generate a method which returns an array of string constants.
   * 
   * @param pw SourceWriter to write on
   * @param methodName the name of the method to create
   * @param values the list of string values to return.
   */
  private void writeStringListMethod(SourceWriter pw, String methodName, String[] values, boolean isPublic) {
    pw.println();
    pw.println("@Override");
    pw.println((isPublic ? "public" : "protected") + " String[] " + methodName + "() {");
    pw.indent();
    pw.println("return new String[] {");
    pw.indent();
    pw.indent();
    for (String code : values) {
      pw.println("\"" + Processor.quote(code) + "\",");
    }
    pw.outdent();
    pw.outdent();
    pw.println("};");
    pw.outdent();
    pw.println("}");
  }
}
