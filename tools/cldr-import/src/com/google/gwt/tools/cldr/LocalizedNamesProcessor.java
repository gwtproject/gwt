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
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.tools.cldr.RegionLanguageData.RegionPopulation;

import com.ibm.icu.text.MessageFormat;

import org.unicode.cldr.util.Factory;

import java.io.File;
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

  private final RegionLanguageData regionLanguageData;

  public LocalizedNamesProcessor(File outputDir, Factory cldrFactory, LocaleData localeData,
      LocaleData sharedLocaleData) {
    super(outputDir, cldrFactory, localeData, sharedLocaleData);
    regionLanguageData = new RegionLanguageData(cldrFactory);
  }

  @Override
  protected void cleanupData() {
    localeData.copyLocaleData("en", "default", "territory", "languages", "scripts", "variants");
    copyNativeDisplayNames();
    // Generate a sort order before removing duplicates
    for (GwtLocale locale : localeData.getNonEmptyLocales("territory")) {
      // TODO(jat): deal with language population data that has a script
      Map<String, String> map = localeData.getEntries("territory", locale);
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
      localeData.addEntry("territory", locale, "!sortorder", buf.toString());
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
      localeData.addEntry("territory", locale, "!likelyorder", buf.toString());

      if (!locale.isDefault()) {
        // generate display names for each locale
        for (GwtLocale nameLocale : localeData.getAllLocales()) {
          String displayName = getDisplayNameForLocale(locale, nameLocale);
          if (displayName != null) {
            localeData.addEntry("localeDisplayNames", locale, nameLocale.toString(), displayName);
          }
        }
      }
    }
    localeData.copyLocaleData("en", "default", "localeDisplayNames");
    /*
     * We must write the properties files used for server-side before we remove
     * duplicates, because those files need to be complete.
     */
    writeLocalizedNamesProperties();

    localeData.removeDuplicates("territory");
    localeData.removeDuplicates("language");
    localeData.removeDuplicates("script");
    localeData.removeDuplicates("variant");
    localeData.removeDuplicates("localeDisplayNames");
  }

  @Override
  protected void loadData() throws IOException {
    System.out.println("Loading data for localized names");
    localeData.addVersions(cldrFactory);
    localeData.addEntries("localePattern", cldrFactory,
        "//ldml/localeDisplayNames/localeDisplayPattern", "localePattern", null);
    localeData.addEntries("localeSeparator", cldrFactory,
        "//ldml/localeDisplayNames/localeDisplayPattern", "localeSeparator", null);
    localeData.addEntries("territory", cldrFactory, "//ldml/localeDisplayNames/territories",
        "territory", "type");
    localeData.addEntries("language", cldrFactory, "//ldml/localeDisplayNames/languages",
        "language", "type");
    localeData.addEntries("script", cldrFactory, "//ldml/localeDisplayNames/scripts", "script",
        "type");
    localeData.addEntries("variant", cldrFactory, "//ldml/localeDisplayNames/variants", "variant",
        "type");
  }

  @Override
  protected void writeOutputFiles() throws IOException {
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
      sharedLocaleData.addEntry("localeNativeDisplayName", locale, locale.toString(), result);
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
      List<String> regionCodesWithNames, String[] sortOrder, String[] likelyOrder)
      throws IOException {
    PrintWriter pw = null;
    try {
      pw = createOutputFile("client/cldr/LocalizedNamesImpl" + localeSuffix(locale) + ".java");
      String pkg = "com.google.gwt.i18n.client.cldr";
      printHeader(pw);
      pw.println("package " + pkg + ";");
      pw.println();
      boolean needBlank = false;
      if (!regionCodesWithNames.isEmpty()) {
        pw.println("import com.google.gwt.core.client.JavaScriptObject;");
        needBlank = true;
      }
      if (locale.isDefault()) {
        pw.println("import com.google.gwt.i18n.client.impl.LocalizedNamesImplBase;");
        needBlank = true;
      }
      if (needBlank) {
        pw.println();
      }
      printVersion(pw, locale, "// ");
      pw.println();
      pw.println("/**");
      pw.println(" * Localized names for the \"" + locale + "\" locale.");
      pw.println(" */");
      pw.print("public class LocalizedNamesImpl" + localeSuffix(locale) + " extends ");
      if (locale.isDefault()) {
        pw.print("LocalizedNamesImplBase");
      } else {
        pw.print("LocalizedNamesImpl" + localeSuffix(localeData.inheritsFrom("territory", locale)));
      }
      pw.println(" {");
      if (likelyOrder != null) {
        writeStringListMethod(pw, "loadLikelyRegionCodes", likelyOrder);
      }
      if (!regionCodesWithNames.isEmpty()) {
        pw.println();
        pw.println("  @Override");
        pw.println("  protected JavaScriptObject loadNameMap() {");
        pw.println("    return overrideMap(super.loadNameMap(), nameMapOverrides());");
        pw.println("  }");
      }
      if (sortOrder != null) {
        writeStringListMethod(pw, "loadSortedRegionCodes", sortOrder);
      }
      if (!regionCodesWithNames.isEmpty()) {
        pw.println();
        pw.println("  private native JavaScriptObject nameMapOverrides() /*-{");
        generateNativeMap(pw, regionCodesWithNames, namesMap);
        pw.println("  }-*/;");
      }
      pw.println("}");
    } finally {
      if (pw != null) {
        pw.close();
      }
    }
  }

  /**
   * @param locale
   * @param namesMap
   * @param regionCodesWithNames
   * @param sortOrder
   * @param likelyOrder
   */
  @SuppressWarnings("unused")
  private void generateDefaultLocale(GwtLocale locale, Map<String, String> namesMap,
      List<String> regionCodesWithNames, String[] sortOrder, String[] likelyOrder)
      throws IOException {
    PrintWriter pw = null;
    try {
      pw = createOutputFile("shared/DefaultLocalizedNames.java");
      String pkg = "com.google.gwt.i18n.shared";
      sharedLocaleData.addEntry("genClasses", locale, "LocalizedNames", pkg + ".DefaultLocalizedNames");
      printHeader(pw);
      pw.println("package com.google.gwt.i18n.shared;");
      pw.println();
      printVersion(pw, locale, "// ");
      pw.println();
      pw.println("/**");
      pw.println(" * Default LocalizedNames implementation.");
      pw.println(" */");
      pw.print("public class DefaultLocalizedNames extends " + "DefaultLocalizedNamesBase {");
      if (likelyOrder != null) {
        writeStringListMethod(pw, "loadLikelyRegionCodes", likelyOrder);
      }
      pw.println();
      pw.println("  @Override");
      pw.println("  protected void loadNameMap() {");
      pw.println("    super.loadNameMap();");
      for (String code : regionCodesWithNames) {
        String name = namesMap.get(code);
        if (name != null) {
          pw.println("    namesMap.put(\"" + quote(code) + "\", \"" + quote(name) + "\");");
        }
      }
      pw.println("  }");
      if (sortOrder != null) {
        writeStringListMethod(pw, "loadSortedRegionCodes", sortOrder);
      }
      pw.println("}");
    } finally {
      if (pw != null) {
        pw.close();
      }
    }
  }

  private void generateLocaleDisplayNames(GwtLocale locale, Map<String, String> namesMap)
      throws IOException {
    PrintWriter pw = null;
    try {
      String myClass = "LocaleDisplayNamesImpl" + localeSuffix(locale);
      pw = createOutputFile("shared/cldr/" + myClass + ".java");
      String pkg = "com.google.gwt.i18n.shared.cldr";
      sharedLocaleData.addEntry("genClasses", locale, "LocaleDisplayNames", pkg + "." + myClass);
      printHeader(pw);
      pw.println("package " + pkg + ";");
      String superClass;
      if (locale.isDefault()) {
        superClass = "LocaleDisplayNamesImplBase";
        pw.println();
        pw.println("import com.google.gwt.i18n.shared.impl.LocaleDisplayNamesImplBase;");
      } else {
        GwtLocale parent = localeData.inheritsFrom("localeDisplayNames", locale);
        superClass = "LocaleDisplayNamesImpl" + localeSuffix(parent);
      }
      pw.println();
      pw.println("import java.util.Map;");
      pw.println();
      printVersion(pw, locale, "// ");
      pw.print("public class " + myClass + " extends " + superClass + " {");
      pw.println();
      pw.println("  @Override");
      pw.println("  protected Map<String, String> loadDisplayNames() {");
      pw.println("    Map<String, String> result = super.loadDisplayNames();");
      Set<String> keySet = namesMap.keySet();
      String[] keys = keySet.toArray(new String[keySet.size()]);
      Arrays.sort(keys);
      for (String key : keys) {
        pw.println("    result.put(" + CodeGenUtils.asStringLiteral(key) + ", "
            + CodeGenUtils.asStringLiteral(namesMap.get(key)) + ");");
      }
      pw.println("    return result;");
      pw.println("  }");
      pw.println("}");
    } finally {
      if (pw != null) {
        pw.close();
      }
    }
  }

  /**
   * @param regionCodesWithNames
   * @param namesMap
   */
  private void generateNativeMap(PrintWriter pw, List<String> regionCodesWithNames,
      Map<String, String> namesMap) {
    pw.println("    return {");
    boolean firstLine = true;
    for (String code : regionCodesWithNames) {
      String name = namesMap.get(code);
      if (name != null && !name.equals(code)) {
        if (firstLine) {
          firstLine = false;
        } else {
          pw.println(",");
        }
        pw.print("        \"" + quote(code) + "\": \"" + quote(name) + "\"");
      }
    }
    pw.println();
    pw.println("    };");
  }

  /**
   * @param locale
   * @param likelyOrder
   * @param sortOrder
   * @param regionCodesWithNames
   * @param namesMap
   */
  private void generateSharedJavaLocale(GwtLocale locale, Map<String, String> namesMap,
      List<String> regionCodesWithNames, String[] sortOrder, String[] likelyOrder)
      throws IOException {
    GwtLocale parent = localeData.inheritsFrom("territory", locale);
    PrintWriter pw = null;
    try {
      String myClass = "LocalizedNamesImpl" + localeSuffix(locale);
      String superClass;
      if (locale.isDefault()) {
        superClass = "LocalizedNamesImplBase";
      } else {
        superClass = "LocalizedNamesImpl" + localeSuffix(parent);
      }
      String pkg = "com.google.gwt.i18n.shared.cldr";
      sharedLocaleData.addEntry("genClasses", locale, "LocalizedNames", pkg + "." + myClass);
      pw = createOutputFile("user/src/" + pkg.replace('.',  '/') + "/",
          myClass + ".java");
      printHeader(pw);
      pw.println("package " + pkg + ";");
      if (locale.isDefault()) {
        pw.println();
        pw.println("import com.google.gwt.i18n.shared.impl.LocalizedNamesImplBase;");
      }
      pw.println();
      printVersion(pw, locale, "// ");
      pw.println("/**");
      pw.println(" * Localized names for the \"" + locale + "\" locale.");
      pw.println(" */");
      pw.print("public class " + myClass + " extends " + superClass + " {");
      if (likelyOrder != null) {
        writeStringListMethod(pw, "getLikelyRegionCodes", likelyOrder);
      }
      if (!regionCodesWithNames.isEmpty()) {
        pw.println();
        pw.println("  @Override");
        pw.println("  protected void ensureNameMap() {");
        pw.println("    super.ensureNameMap();");
        for (String code : regionCodesWithNames) {
          String name = namesMap.get(code);
          if (name != null && !name.equals(code)) {
            pw.println("    nameMap.put(\"" + quote(code) + "\", \"" + quote(name) + "\");");
          }
        }
        pw.println("  }");
      }
      if (sortOrder != null) {
        writeStringListMethod(pw, "loadSortedRegionCodes", sortOrder, false);
      }
      pw.println("}");
    } finally {
      if (pw != null) {
        pw.close();
      }
    }
  }

  /**
   * @param locale
   * @param likelyOrder
   * @param sortOrder
   * @param regionCodesWithNames
   * @param namesMap
   */
  @SuppressWarnings("unused")
  private void generateSharedJsLocale(GwtLocale locale, Map<String, String> namesMap,
      List<String> regionCodesWithNames, String[] sortOrder, String[] likelyOrder)
      throws IOException {
    GwtLocale parent = localeData.inheritsFrom(locale);
    PrintWriter pw = null;
    try {
      String myClass = "LocalizedNamesImpl" + localeSuffix(locale);
      String superClass;
      if (locale.isDefault()) {
        superClass = "LocalizedNamesImplBase";
      } else {
        superClass = "LocalizedNamesImpl" + localeSuffix(parent);
      }
      String pkg = "com.google.gwt.i18n.shared.cldr";
      pw = createOutputFile("user/super/com/google/gwt/i18n/super/" + pkg.replace('.',  '/') + "/",
          myClass + ".java");
      localeData.addEntry("genClasses", locale, "LocalizedNames", pkg + ".LocalizedNamesImpl"
          + localeSuffix(locale));
      printHeader(pw);
      pw.println("package " + pkg + ";");
      boolean needBlank = true;
      if (!regionCodesWithNames.isEmpty()) {
        pw.println();
        needBlank = false;
        pw.println("import com.google.gwt.core.client.JavaScriptObject;");
      }
      if (locale.isDefault()) {
        if (needBlank) {
          pw.println();
        }
        pw.println("import com.google.gwt.i18n.shared.impl.LocalizedNamesImplBase;");
      }
      pw.println();
      printVersion(pw, locale, "// ");
      pw.println("/**");
      pw.println(" * Localized names for the \"" + locale + "\" locale.");
      pw.println(" */");
      pw.println("public class LocalizedNamesImpl" + localeSuffix(locale) + " extends "
          + superClass + " {");
      if (likelyOrder != null) {
        writeStringListMethod(pw, "getLikelyRegionCodes", likelyOrder);
      }
      if (!regionCodesWithNames.isEmpty()) {
        pw.println();
        pw.println("  @Override");
        pw.println("  protected JavaScriptObject loadNameMap() {");
        pw.println("    return overrideMap(super.loadNameMap(), nameMapOverrides());");
        pw.println("  }");
      }
      if (sortOrder != null) {
        writeStringListMethod(pw, "loadSortedRegionCodes", sortOrder, false);
      }
      if (!regionCodesWithNames.isEmpty()) {
        pw.println();
        pw.println("  private native JavaScriptObject nameMapOverrides() /*-{");
        generateNativeMap(pw, regionCodesWithNames, namesMap);
        pw.println("  }-*/;");
      }
      pw.println("}");
    } finally {
      if (pw != null) {
        pw.close();
      }
    }
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
    String localePattern = localeData.getEntry("localePattern", targetLocale, "value");
    String localeSeparator = localeData.getEntry("localeSeparator", targetLocale, "value");
    String language = locale.getLanguage();
    String region = locale.getRegion();
    String baseName = localeData.getInheritedEntry("language", targetLocale, language);
    if (baseName == null) {
      baseName = language;
    }
    // Collect the additional descriptions
    StringBuilder buf = new StringBuilder();
    
    // If the region is part of a special name for the language, use it.  Otherwise, add it to the
    // list of additional descriptions.
    if (region != null) {
      String val = localeData.getInheritedEntry("language", targetLocale, language + "_" + region);
      if (val != null) {
        baseName = val;
      } else {
        val = localeData.getInheritedEntry("territory", targetLocale, region);
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
      String val = localeData.getInheritedEntry("script", targetLocale, script);
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
      String val = localeData.getInheritedEntry("variant", targetLocale, variant);
      if (val != null) {
        if (buf.length() > 0) {
          buf.append(localeSeparator);
        }
        buf.append(val);
      }
    }
    String result = baseName;
    if (buf.length() > 0) {
      result = MessageFormat.format(localePattern, baseName, buf.toString());
    }
    return result;
  }

  /**
   * @param locale
   * @return region populations speaking this language
   */
  private Set<RegionPopulation> getRegionsForLocale(GwtLocale locale) {
    Set<RegionPopulation> retVal =
        regionLanguageData
            .getRegions(locale.getLanguageNotNull() + "_" + locale.getScriptNotNull());
    if (retVal.isEmpty()) {
      retVal = regionLanguageData.getRegions(locale.getLanguageNotNull());
    }
    return retVal;
  }

  private void writeLocaleDisplayNames() throws IOException {
    for (GwtLocale locale : localeData.getNonEmptyLocales("localeDisplayNames")) {
      Map<String, String> namesMap = localeData.getEntries("localeDisplayNames", locale);
      generateLocaleDisplayNames(locale, namesMap);
    }
  }

  private void writeLocalizedNames() throws IOException {
    for (GwtLocale locale : localeData.getNonEmptyLocales("territory")) {
      Map<String, String> namesMap = localeData.getEntries("territory", locale);
      List<String> regionCodesWithNames = new ArrayList<String>();
      for (String regionCode : namesMap.keySet()) {
        if (!regionCode.startsWith("!")) {
          // skip entries which aren't actually region codes
          regionCodesWithNames.add(regionCode);
        }
      }
      String[] sortOrder = getRegionOrder(namesMap.get("!sortorder"));
      String[] likelyOrder = getRegionOrder(namesMap.get("!likelyorder"));
      if (regionCodesWithNames.isEmpty() && sortOrder == null && likelyOrder == null) {
        // nothing to do
        return;
      }
      // sort for deterministic output
      Collections.sort(regionCodesWithNames);
//      if (locale.isDefault()) {
//        generateDefaultLocale(locale, namesMap, regionCodesWithNames, sortOrder, likelyOrder);
//      }
      generateClientLocale(locale, namesMap, regionCodesWithNames, sortOrder, likelyOrder);
      generateSharedJavaLocale(locale, namesMap, regionCodesWithNames, sortOrder, likelyOrder);
      // generateSharedJsLocale(locale, namesMap, regionCodesWithNames, sortOrder, likelyOrder);
    }
  }

  private void writeLocalizedNamesProperties() {
    for (GwtLocale locale : localeData.getAllLocales()) {
      PrintWriter pw = null;
      try {
        pw = createOutputFile("shared/cldr/LocaleDisplayNames" + localeSuffix(locale)
            + ".properties");
        printPropertiesHeader(pw);
        pw.println();
        printVersion(pw, locale, "# ");
        Map<String, String> map = localeData.getEntries("localeDisplayNames", locale);
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
   * @param pw PrintWriter to write on
   * @param methodName the name of the method to create
   * @param values the list of string values to return.
   */
  private void writeStringListMethod(PrintWriter pw, String methodName, String[] values) {
    writeStringListMethod(pw, methodName, values, true);
  }

  /**
   * Generate a method which returns an array of string constants.
   * 
   * @param pw PrintWriter to write on
   * @param methodName the name of the method to create
   * @param values the list of string values to return.
   */
  private void writeStringListMethod(PrintWriter pw, String methodName, String[] values, boolean isPublic) {
    pw.println();
    pw.println("  @Override");
    pw.println("  " + (isPublic ? "public" : "protected") + " String[] " + methodName + "() {");
    pw.println("    return new String[] {");
    for (String code : values) {
      pw.println("        \"" + Processor.quote(code) + "\",");
    }
    pw.println("    };");
    pw.println("  }");
  }
}
