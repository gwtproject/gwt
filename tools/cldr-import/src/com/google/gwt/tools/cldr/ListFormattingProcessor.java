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

import com.google.gwt.codegen.server.StringGenerator;
import com.google.gwt.i18n.server.MessageFormatUtils.ArgumentChunk;
import com.google.gwt.i18n.server.MessageFormatUtils.DefaultTemplateChunkVisitor;
import com.google.gwt.i18n.server.MessageFormatUtils.MessageStyle;
import com.google.gwt.i18n.server.MessageFormatUtils.StringChunk;
import com.google.gwt.i18n.server.MessageFormatUtils.VisitorAbortException;
import com.google.gwt.i18n.shared.GwtLocale;

import org.unicode.cldr.util.Factory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Map;

/**
 * Extract list formatting information from CLDR data.
 */
public class ListFormattingProcessor extends Processor {

  public ListFormattingProcessor(File outputDir, Factory cldrFactory, LocaleData localeData,
      LocaleData sharedLocaleData) {
    super(outputDir, cldrFactory, localeData, sharedLocaleData);
  }

  @Override
  protected void cleanupData() {
    localeData.removeCompleteDuplicates("list");
  }

  @Override
  protected void loadData() throws IOException {
    System.out.println("Loading data for list formatting");
    localeData.addVersions(cldrFactory);
    localeData.addEntries("list", cldrFactory, "//ldml/listPatterns/listPattern",
        "listPatternPart", "type");
  }

  @Override
  protected void writeOutputFiles() throws IOException {
    for (GwtLocale locale : localeData.getNonEmptyLocales("list")) {
      Map<String, String> map = localeData.getEntries("list", locale);
      writePropertiesFile(locale, map);
      writeJavaFile(locale, map);
    }
  }

  private void writeJavaFile(GwtLocale locale, Map<String, String> map) throws IOException {
    String pkg = "com.google.gwt.i18n.shared.cldr";
    sharedLocaleData.addEntry("genClasses", locale, "ListPatterns",
        pkg + ".ListPatternsImpl" + localeSuffix(locale));
    PrintWriter pw = createOutputFile("user/src/" + pkg.replace('.', '/') + "/",
        "ListPatternsImpl" + localeSuffix(locale) + ".java");
    printJavaHeader(pw);
    pw.println("package " + pkg + ";");
    pw.println();
    pw.println("import com.google.gwt.i18n.shared.ListPatterns;");
    pw.println("import com.google.gwt.safehtml.shared.SafeHtml;");
    pw.println();
    printVersion(pw, locale, "// ");
    pw.println("public class ListPatternsImpl" + localeSuffix(locale)
        + " implements ListPatterns {");
    generateFormatEntryMethod(pw, map, false);
    generateFormatEntryMethod(pw, map, true);
    pw.println("}");
    pw.close();
  }

  private void generateFormatEntryMethod(PrintWriter pw, Map<String, String> map,
      boolean useSafeHtml) {
    String type = useSafeHtml ? "SafeHtml" : "String";
    pw.println();
    pw.println("  @Override");
    pw.println("  public " + type + " formatEntry(int index, int count, " + type + " left, "
    		+ type + " formattedTail) {");
    // TODO(jat): make this more general if CLDR ever includes counts besides 2
    String pattern = map.get("2");
    if (pattern != null) {
      pw.println("    switch (count) {");
      pw.println("      case 2:");
      pw.println("        return " + generateReturn(pattern, useSafeHtml) + ";");
      pw.println("    }");
    }
    pattern = map.get("start");
    if (pattern != null) {
      pw.println("    if (index == 0) {");
      pw.println("      return " + generateReturn(pattern, useSafeHtml) + ";");
      pw.println("    }");
    }
    pattern = map.get("end");
    if (pattern != null) {
      pw.println("    if (index >= count - 2) {");
      pw.println("      return " + generateReturn(pattern, useSafeHtml) + ";");
      pw.println("    }");
    }
    pattern = map.get("middle");
    pw.println("    return " + generateReturn(pattern, useSafeHtml) + ";");
    pw.println("  }");
  }

  private CharSequence generateReturn(String pattern, final boolean useSafeHtml) {
    StringBuilder buf = new StringBuilder();
    final StringGenerator gen = StringGenerator.create(buf, useSafeHtml);
    try {
      MessageStyle.MESSAGE_FORMAT.parseAccept(pattern, new DefaultTemplateChunkVisitor() {
        @Override
        public void visit(ArgumentChunk argChunk) throws VisitorAbortException {
          gen.appendExpression(argChunk.getArgumentNumber() == 0 ? "left" : "formattedTail",
              useSafeHtml, false, false);
        }

        @Override
        public void visit(StringChunk stringChunk) throws VisitorAbortException {
          gen.appendStringLiteral(stringChunk.getString());
        }
      });
    } catch (ParseException e) {
      throw new RuntimeException(e);
    } catch (VisitorAbortException e) {
      throw new RuntimeException(e);
    }
    gen.completeString();
    return buf;
  }

  private void writePropertiesFile(GwtLocale locale, Map<String, String> map) throws IOException {
    PrintWriter pw = createOutputFile("rebind/cldr/ListPatterns_" + locale.getAsString()
        + ".properties");
    printPropertiesHeader(pw);
    pw.println();
    printVersion(pw, locale, "# ");
    for (Map.Entry<String, String> entry : map.entrySet()) {
      pw.println(entry.getKey() + "=" + entry.getValue());
    }
    pw.close();
  }
}
