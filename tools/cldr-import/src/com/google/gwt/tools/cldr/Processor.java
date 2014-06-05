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

import com.google.gwt.codegen.server.AbortablePrintWriter;
import com.google.gwt.codegen.server.JavaSourceWriterBuilder;
import com.google.gwt.codegen.server.JavaSourceWriterBuilder.CallbackHooks;
import com.google.gwt.codegen.server.LoggingCodeGenContext;
import com.google.gwt.codegen.server.SourceWriter;
import com.google.gwt.i18n.shared.GwtLocale;

import org.unicode.cldr.util.CLDRFile;
import org.unicode.cldr.util.XPathParts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Base class for CLDR processors that generate GWT i18n resources.
 */
public abstract class Processor {

  protected class AttributeProcessor implements QueryMatchCallback {

    private final String category;
    private final String tag;
    private final String key;
    private final String attribute;

    public AttributeProcessor(String category, String path, String tag, String key,
        String attribute) {
      this.category = category;
      this.tag = tag;
      this.key = key;
      this.attribute = attribute;
      cldrData.registerLocale(new XPathQuery(path), this);
    }

    @Override
    public void process(GwtLocale locale, XPathParts parts, String value) {
      Map<String, String> attr = parts.findAttributes(tag);
      if (attr == null) {
        return;
      }
      String v = attr.get(attribute);
      if (v != null) {
        localeData.addEntry(category, locale, key, v);
      }
    }
  }
  protected class EntryProcessor implements QueryMatchCallback {

    private final String category;
    private final String matchTag;
    private final String matchAttr;
    private final String matchVal;
    private final String tag;
    private final String keyAttribute;
    private final String mapAttribute;

    public EntryProcessor(String category, String prefix, String matchTag, String matchAttr,
        String matchVal, String tag, String keyAttribute, String mapAttribute) {
      this.category = category;
      this.matchTag = matchTag;
      this.matchAttr = matchAttr;
      this.matchVal = matchVal;
      this.tag = tag;
      this.keyAttribute = keyAttribute;
      this.mapAttribute = mapAttribute;
      cldrData.registerLocale(new XPathQuery(prefix), this);
    }

    @Override
    public void process(GwtLocale locale, XPathParts parts, String value) {
      if (parts.containsAttribute("alt")) {
        // ignore alternate strings
        return;
      }
      if (matchTag != null) {
        Map<String, String> attr = parts.findAttributes(matchTag);
        String val = attr.get(matchAttr);
        if (!Objects.equals(matchVal, val)) {
          return;
        }
      }
      Map<String, String> attr = parts.findAttributes(tag);
      if (attr == null) {
        return;
      }
      boolean draft = parts.containsAttribute("draft");
      String key = keyAttribute != null ? attr.get(keyAttribute) : mapAttribute != null
          ? mapAttribute : parts.getElement(-1);
      localeData.addEntry(category, locale, key, value, draft);
    }
  }

  /**
   * Callback implementation that prints the CLDR version information.
   */
  protected class PrintCldrVersionCallback extends CallbackHooks {
    @Override
    public void afterImports(AbortablePrintWriter pw) {
      pw.println();
      pw.println("// Generated from CLDR " + CLDRFile.GEN_VERSION);
    }
  }

  /**
   * Callback implementation that prints the CLDR version information.
   */
  protected class PrintVersionCallback extends CallbackHooks {

    private final GwtLocale locale;

    protected PrintVersionCallback(GwtLocale locale) {
      this.locale = locale;
    }

    @Override
    public void afterImports(AbortablePrintWriter pw) {
      pw.println();
      printVersion(pw, locale, "// ");
    }
  }

  /**
   * A CodeGenContext implementation that logs to j.u.logging and creates
   * output files using {@link Processor#createOutputFile(String, String)}.
   */
  protected class ProcessorCodeGenContext extends LoggingCodeGenContext {

    private final String pathPrefix;

    public ProcessorCodeGenContext() {
      this("");
    }

    public ProcessorCodeGenContext(String pathPrefix) {
      this.pathPrefix = pathPrefix;
    }

    @Override
    public JavaSourceWriterBuilder addClass(String superPkg, String pkgName, String className) {
      String pkgPath = superPkg == null ? pkgName : superPkg + '/' + pkgName;
      if (pkgPath.length() > 0) {
        pkgPath = pkgPath.replace('.', '/') + '/';
      }
      String classPath = className.replace('.', '_');
      String fileName = pkgPath + classPath + ".java";
      try {
        PrintWriter pw = createOutputFile(pathPrefix, fileName);
        AbortablePrintWriter apw = new AbortablePrintWriter(pw);
        printHeader(apw);
        return new JavaSourceWriterBuilder(apw, pkgName, className);
      } catch (FileNotFoundException e) {
        error("Unable to create " + fileName, e);
        return null;
      } catch (IOException e) {
        error("Unable to create " + fileName, e);
        return null;
      }
    }
  }

  protected class TerritoryEntries implements QueryMatchCallback {

    private final String category;
    private final String tag;
    private final String keyAttribute;
    private final Map<String, String> map;

    public TerritoryEntries(String category, String prefix, String tag, String keyAttribute) {
      this.category = category;
      this.tag = tag;
      this.keyAttribute = keyAttribute;
      map = new HashMap<String, String>();
      cldrData.registerSupplemental("supplementalData", new XPathQuery(prefix), this);
    }

    @Override
    public void process(GwtLocale locale, XPathParts parts, String value) {
      Map<String, String> attr = parts.findAttributes(tag);
      if (attr == null || attr.get("alt") != null) {
        return;
      }
      String key = attr.get(keyAttribute);
      String territories = attr.get("territories");
      String draft = attr.get("draft");
      for (String territory : territories.split(" ")) {
        if (draft == null || !map.containsKey(territory)) {
          map.put(territory, key);
        }
      }
    }

    public void summarize() {
      if (cldrData.getRegionLanguageData() != null) {
        // find the choice used by most literate speakers of each language
        // based on region-based preferences.
        localeData.summarizeTerritoryEntries(category, cldrData.getRegionLanguageData(), tag, map);
      }
    }
  }

  protected static final String CATEGORY_LOCALE_NATIVE_DISPLAY_NAME = "localeNativeDisplayName";

  protected static final String CATEGORY_GENCLASSES = "genClasses";
  protected static final String CATEGORY_GENCLASSES_CLIENT = "genClasses-client";

  protected static final String I18N_PACKAGE_PATH = "user/src/com/google/gwt/i18n/";

  protected static <T> String join(String joiner, Iterable<T> objects) {
    StringBuilder buf = new StringBuilder();
    for (Object obj : objects) {
      if (buf.length() > 0) {
        buf.append(joiner);
      }
      buf.append(obj.toString());
    }
    return buf.toString();
  }


  protected static String localeSuffix(GwtLocale locale) {
    return (locale.isDefault() ? "" : "_") + locale.getAsString();
  }

  /**
   * @param value
   * @return value with all quotes escaped
   */
  protected static String quote(String value) {
    return value.replace("\"", "\\\"");
  }

  /**
   * @param value
   * @return value as a string literal; all quotes escaped or null
   */
  protected static String quotedOrNull(String value) {
    if (value == null) {
      return "null";
    }
    return "\"" + value.replace("\"", "\\\"") + "\"";
  }

  protected final CldrData cldrData;
  protected final LocaleData localeData;
  protected final File outputDir;
  protected final Processors processors;
  protected final LocaleData sharedLocaleData;

  /**
   * Initialize the shared portion of a Processor.  All subclasses must have a public constructor
   * taking the same arguments which is called reflectively.
   * 
   * @param processors
   */
  protected Processor(Processors processors) {
    this.processors = processors;
    this.outputDir = processors.getOutputDir();
    this.cldrData = processors.getCldrData();
    this.localeData = cldrData.getLocaleData();
    this.sharedLocaleData = localeData;
  }

  public void addAttributeEntries(String category, String path, String tag, String key,
      String attribute, String defaultValue) {
    new AttributeProcessor(category, path, tag, key, attribute);
    // add the default in the root locale
    localeData.addEntry(category, localeData.getGwtLocale(""), key, defaultValue);
  }

  /**
   * Add any dependencies by calling {@link Processors#requireProcessor(Class)}.
   */
  public void addDependencies() {
    // does nothing by default
  }

  public void addEntries(String category, String prefix, String tag, String keyAttribute) {
    addEntries(category, prefix, tag, keyAttribute, "value");
  }

  public void addEntries(String category, String prefix, String tag, String keyAttribute,
      String mapAttribute) {
    addEntries(category, prefix, null, null, null, tag, keyAttribute, mapAttribute);
  }

  public void addEntries(String category, String prefix, String matchTag,
      String matchAttr, String matchVal, String tag, String keyAttribute) {
    addEntries(category, prefix, matchTag, matchAttr, matchVal, tag, keyAttribute, "value");
  }

  public void addEntries(String category, String prefix, String matchTag, String matchAttr,
      String matchVal, String tag, String keyAttribute, String mapAttribute) {
    new EntryProcessor(category, prefix, matchTag, matchAttr, matchVal, tag, keyAttribute,
        mapAttribute);
  }

  /**
   * Add entries from territory-oriented CLDR data.
   * 
   * @param category category to store resulting data under
   * @param cldrFactory
   * @param regionLanguageData
   * @param prefix the XPath prefix to iterate through
   * @param tag the tag to load
   * @param keyAttribute the attribute in the tag to use as the key
   */
  public void addTerritoryEntries(String category, String prefix, String tag, String keyAttribute) {
    final TerritoryEntries terr = new TerritoryEntries(category, prefix, tag, keyAttribute);
    cldrData.addPostProcessor(new Runnable() {
      @Override
      public void run() {
        terr.summarize();
      }
    });
  }

  /**
   * Override hook for subclasses to implement any processing done after {@link #cleanupData()}
   * has been executed on all processors.
   */
  public void afterCleanup() {
    // do nothing by default
  }

  /**
   * Override hook for subclasses to implement any cleanup needed, such as
   * removing values which duplicate those from ancestors.
   */
  public void cleanupData() {
    // do nothing by default
  }

  public void loadExternalData(GwtLocale locale) {
    // do nothing by default
  }

  /**
   * Write output files produced by this processor.
   * 
   * @throws IOException
   */
  public abstract void writeOutputFiles() throws IOException;

  /**
   * Create an output file including any parent directories.
   * 
   * @param name name of file, which will be prefixed by
   *          user/src/com/google/gwt/i18n/client/impl/cldr
   * @param ext extension for file
   * @param locale locale name or null if not localized
   * @return a PrintWriter instance
   * @throws IOException
   */
  protected PrintWriter createFile(String name, String ext, String locale) throws IOException {
    if (locale == null || locale.length() == 0) {
      locale = "";
    } else {
      locale = "_" + locale;
    }
    return createOutputFile("client/impl/cldr/" + name + locale + "." + ext);
  }

  protected PrintWriter createOutputFile(String suffix) throws IOException, FileNotFoundException {
    return createOutputFile(I18N_PACKAGE_PATH, suffix);
  }

  protected PrintWriter createOutputFile(String prefix, String suffix) throws IOException,
      FileNotFoundException {
    PrintWriter pw;
    File f = new File(outputDir, prefix + suffix);
    File parent = f.getParentFile();
    if (parent != null) {
      parent.mkdirs();
    }
    f.createNewFile();
    pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),
        "UTF-8")), false);
    return pw;
  }

  protected void generateCharValue(SourceWriter pw, String method, char value) {
    pw.println();
    pw.println("@Override");
    pw.println("public char " + method + "() {");
    pw.indentln("return '" + value + "';");
    pw.println("}");
  }

  protected void generateIntMethod(SourceWriter pw, String category, GwtLocale locale, String key,
      String method) {
    String value = localeData.getEntry(category, locale, key);
    if (value != null) {
      pw.println();
      pw.println("@Override");
      pw.println("public int " + method + "() {");
      pw.indentln("return " + value + ";");
      pw.println("}");
    }
  }

  protected void generateStringMethod(SourceWriter pw, String category, GwtLocale locale,
      String key, String method) {
    String value = localeData.getEntry(category, locale, key);
    generateStringValue(pw, method, value);
  }

  protected void generateStringValue(SourceWriter pw, String method, String value) {
    if (value != null) {
      pw.println();
      pw.println("@Override");
      pw.println("public String " + method + "() {");
      pw.indentln("return \"" + quote(value) + "\";");
      pw.println("}");
    }
  }

  protected void printHeader(PrintWriter pw) {
    printJavaHeader(pw);
  }

  protected void printJavaHeader(PrintWriter pw) {
    int year = Calendar.getInstance().get(Calendar.YEAR);
    pw.println("/*");
    pw.println(" * Copyright " + year + " Google Inc.");
    pw.println(" * ");
    pw.println(" * Licensed under the Apache License, Version 2.0 (the "
        + "\"License\"); you may not");
    pw.println(" * use this file except in compliance with the License. You "
        + "may obtain a copy of");
    pw.println(" * the License at");
    pw.println(" * ");
    pw.println(" * http://www.apache.org/licenses/LICENSE-2.0");
    pw.println(" * ");
    pw.println(" * Unless required by applicable law or agreed to in writing, " + "software");
    pw.println(" * distributed under the License is distributed on an \"AS "
        + "IS\" BASIS, WITHOUT");
    pw.println(" * WARRANTIES OR CONDITIONS OF ANY KIND, either express or " + "implied. See the");
    pw.println(" * License for the specific language governing permissions and "
        + "limitations under");
    pw.println(" * the License.");
    pw.println(" */");
  }

  protected void printPropertiesHeader(PrintWriter pw) {
    int year = Calendar.getInstance().get(Calendar.YEAR);
    pw.println("# Copyright " + year + " Google Inc.");
    pw.println("# ");
    pw.println("# Licensed under the Apache License, Version 2.0 (the "
        + "\"License\"); you may not");
    pw.println("# use this file except in compliance with the License. You "
        + "may obtain a copy of");
    pw.println("# the License at");
    pw.println("# ");
    pw.println("# http://www.apache.org/licenses/LICENSE-2.0");
    pw.println("# ");
    pw.println("# Unless required by applicable law or agreed to in writing, " + "software");
    pw.println("# distributed under the License is distributed on an \"AS "
        + "IS\" BASIS, WITHOUT");
    pw.println("# WARRANTIES OR CONDITIONS OF ANY KIND, either express or " + "implied. See the");
    pw.println("# License for the specific language governing permissions and "
        + "limitations under");
    pw.println("# the License.");
  }

  protected void printVersion(PrintWriter pw, GwtLocale locale, String prefix) {
    pw.println(prefix + "DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA:");
    Map<String, String> map = localeData.getEntries("version", locale);
    Set<String> keySet = map.keySet();
    String[] keys = keySet.toArray(new String[keySet.size()]);
    Arrays.sort(keys);
    for (String key : keys) {
      pw.println(prefix + " " + key + "=" + map.get(key));
    }
  }

  protected void printXmlHeader(PrintWriter pw) {
    int year = Calendar.getInstance().get(Calendar.YEAR);
    pw.println("<!--");
    pw.println("   - Copyright " + year + " Google Inc.");
    pw.println("   - ");
    pw.println("   - Licensed under the Apache License, Version 2.0 (the "
        + "\"License\"); you may not");
    pw.println("   - use this file except in compliance with the License. You "
        + "may obtain a copy of");
    pw.println("   - the License at");
    pw.println("   - ");
    pw.println("   - http://www.apache.org/licenses/LICENSE-2.0");
    pw.println("   - ");
    pw.println("   - Unless required by applicable law or agreed to in writing, " + "software");
    pw.println("   - distributed under the License is distributed on an \"AS "
        + "IS\" BASIS, WITHOUT");
    pw.println("   - WARRANTIES OR CONDITIONS OF ANY KIND, either express or " + "implied. See the");
    pw.println("   - License for the specific language governing permissions and "
        + "limitations under");
    pw.println("   - the License.");
    pw.println("-->");
  }

  protected void registerLocale(XPathQuery query, QueryMatchCallback callback) {
    cldrData.registerLocale(query, callback);
  }

  protected void registerSupplemental(String supp, XPathQuery query, QueryMatchCallback callback) {
    cldrData.registerSupplemental(supp, query, callback);
  }
}
