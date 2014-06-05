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
import com.google.gwt.i18n.server.MessageFormatUtils.TemplateChunk;
import com.google.gwt.i18n.server.MessageFormatUtils.VisitorAbortException;
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.i18n.shared.ListPatterns;

import org.unicode.cldr.util.CLDRFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Process data about currencies; generates implementations of {@link ListPatterns}, and
 * properties file needed by the messages generator. 
 */
public class ListFormattingProcessor extends Processor {

  private static final String CATEGORY_LIST = "list";

  public ListFormattingProcessor(Processors processors) {
    super(processors);
    addEntries(CATEGORY_LIST, "//ldml/listPatterns/listPattern", "listPattern", "type", null,
        "listPatternPart", "type");
  }

  @Override
  public void cleanupData() {
    localeData.removeCompleteDuplicates(CATEGORY_LIST);
  }

  @Override
  public void writeOutputFiles() throws IOException {
    for (GwtLocale locale : localeData.getNonEmptyLocales(CATEGORY_LIST)) {
      writeJavaOutput(locale);
      writePropertiesOutput(locale);
    }
  }

  private void writePropertiesOutput(GwtLocale locale) throws IOException {
    PrintWriter pw = null;
    for (Map.Entry<String, String> entry : localeData.getEntries(CATEGORY_LIST, locale).entrySet()) {
      if (pw == null) {
        pw = createOutputFile("rebind/cldr/ListPatterns_" + locale.getAsString() + ".properties");
        printPropertiesHeader(pw);
        pw.println();
        printVersion(pw, locale, "# ");
      }
      pw.println(entry.getKey() + "=" + entry.getValue());
    }
    if (pw != null) {
      pw.close();
    }
  }

  private void writeJavaOutput(GwtLocale locale) throws IOException {
    GwtLocale parent = localeData.inheritsFrom(CATEGORY_LIST, locale);
    String className = "ListPatternsImpl_" + locale.getAsString();
    PrintWriter pw = createOutputFile("shared/impl/cldr/" + className + ".java");
    printHeader(pw);
    String pkg = "com.google.gwt.i18n.shared.impl.cldr";
    pw.println("package " + pkg + ";");
    sharedLocaleData.addEntry(CATEGORY_GENCLASSES, locale, "ListPatterns", pkg + "." + className);
    // GWT now requires JDK 1.6, so we always generate @Overrides
    pw.println();
    if (locale.isDefault()) {
      pw.println("import com.google.gwt.i18n.shared.ListPatterns;");
    }
    pw.println("import com.google.gwt.safehtml.shared.SafeHtml;");
    pw.println();
    pw.println("// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA");
    pw.println("//  cldrVersion=" + CLDRFile.GEN_VERSION);
    Map<String, String> map = localeData.getEntries("version", locale);
    for (Map.Entry<String, String> entry : map.entrySet()) {
      pw.println("//  " + entry.getKey() + "=" + entry.getValue());
    }
    pw.println();
    pw.println("/**");
    pw.println(" * Implementation of ListPatterns for the \"" + locale + "\" locale.");
    pw.println(" */");
    pw.print("public class " + className);
    if (locale.isDefault()) {
      pw.print(" implements " + ListPatterns.class.getSimpleName());
    } else {
      pw.print(" extends ListPatternsImpl_" + parent.getAsString());
    }
    pw.println(" {");
    Map<String, String> patterns = localeData.getEntries(CATEGORY_LIST, locale);
    pw.println();
    pw.println("  @Override");
    pw.println("  public String formatEntry(int index, int count, String arg0, String arg1) {");
    generateFormatEntry(locale, pw, patterns, false);
    pw.println("  }");
    pw.println();
    pw.println("  @Override");
    pw.println("  public SafeHtml formatEntry(int index, int count, SafeHtml arg0, SafeHtml arg1) {");
    generateFormatEntry(locale, pw, patterns, true);
    pw.println("  }");
    pw.println("}");
    pw.close();
  }

  private void generateFormatEntry(GwtLocale locale, PrintWriter pw, Map<String, String> patterns,
      boolean isSafeHtml) {
    // TODO(jat): support specific counts besides 2 if they are ever added to CLDR
    String pair = patterns.get("2");
    if (pair != null) {
      pw.println("    switch (count) {");
      pw.println("      case 2:");
      pw.println("        // " + pair);
      pw.println("        return " + generateExpression(pair, isSafeHtml) + ";");
      pw.println("    }");
    }
    String end = patterns.get("end");
    if (end != null) {
      pw.println("    if (index == count - 2) {");
      pw.println("      // " + end);
      pw.println("      return " + generateExpression(end, isSafeHtml) + ";");
      pw.println("    }");
    }
    String start = patterns.get("start");
    if (end != null) {
      pw.println("    if (index == 0) {");
      pw.println("      // " + start);
      pw.println("      return " + generateExpression(start, isSafeHtml) + ";");
      pw.println("    }");
    }
    String middle = patterns.get("middle");
    if (middle == null) {
      System.out.println("Warning - no middle listpattern for " + locale);
      middle = "{0}, {1}";
    }
    pw.println("    return " + generateExpression(middle, isSafeHtml) + ";");
  }

  private String generateExpression(String pattern, final boolean isSafeHtml) {
    StringBuilder buf = new StringBuilder();
    final StringGenerator gen = StringGenerator.create(buf, isSafeHtml);
    try {
      List<TemplateChunk> chunks = MessageStyle.MESSAGE_FORMAT.parse(pattern);
      for (TemplateChunk chunk : chunks) {
        chunk.accept(new DefaultTemplateChunkVisitor() {
          @Override
          public void visit(ArgumentChunk argChunk) {
            String param = "arg" + argChunk.getArgumentNumber();
            gen.appendExpression(param, isSafeHtml, false, false);
          }

          @Override
          public void visit(StringChunk stringChunk) {
            gen.appendStringLiteral(stringChunk.getString());
          }
        });
      }
    } catch (ParseException e) {
      throw new RuntimeException("Unable to parse pattern '" + pattern + "'", e);
    } catch (VisitorAbortException e) {
      throw new RuntimeException("Unable to parse pattern '" + pattern + "'", e);
    }
    gen.completeString();
    return buf.toString();
  }
}
