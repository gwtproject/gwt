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
import com.google.gwt.codegen.server.StringGenerator;
import com.google.gwt.i18n.server.MessageFormatUtils.ArgumentChunk;
import com.google.gwt.i18n.server.MessageFormatUtils.DefaultTemplateChunkVisitor;
import com.google.gwt.i18n.server.MessageFormatUtils.MessageStyle;
import com.google.gwt.i18n.server.MessageFormatUtils.StringChunk;
import com.google.gwt.i18n.server.MessageFormatUtils.VisitorAbortException;
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.i18n.shared.ListPatterns;
import com.google.gwt.safehtml.shared.SafeHtml;

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

  private static final String CATEGORY_LIST = "list";

  public ListFormattingProcessor(File outputDir, Factory cldrFactory, LocaleData localeData,
      LocaleData sharedLocaleData) {
    super(outputDir, cldrFactory, localeData, sharedLocaleData);
  }

  @Override
  protected void cleanupData() {
    localeData.removeCompleteDuplicates(CATEGORY_LIST);
  }

  @Override
  protected void loadData() throws IOException {
    System.out.println("Loading data for list formatting");
    localeData.addVersions(cldrFactory);
    localeData.addEntries(CATEGORY_LIST, cldrFactory, "//ldml/listPatterns/listPattern",
        "listPatternPart", "type");
  }

  @Override
  protected void writeOutputFiles() throws IOException {
    for (GwtLocale locale : localeData.getNonEmptyLocales(CATEGORY_LIST)) {
      Map<String, String> map = localeData.getEntries(CATEGORY_LIST, locale);
      writePropertiesFile(locale, map);
      writeJavaFile(locale, map);
    }
  }

  private void writeJavaFile(GwtLocale locale, Map<String, String> map) {
    String pkg = "com.google.gwt.i18n.shared.cldr";
    String myClass = "ListPatternsImpl" + localeSuffix(locale);
    sharedLocaleData.addEntry(CATEGORY_GENCLASSES, locale, "ListPatterns", pkg + "." + myClass);
    ProcessorCodeGenContext codeGen = new ProcessorCodeGenContext("user/src/");
    JavaSourceWriterBuilder jswb = codeGen.addClass(pkg, myClass);
    jswb.setCallbacks(new PrintVersionCallback(locale));
    jswb.addImport(ListPatterns.class);
    jswb.addImport(SafeHtml.class);
    jswb.addImplementedInterface("ListPatterns");
    jswb.setJavaDocCommentForClass("List formatting for the \"" + locale + "\" locale.");
    SourceWriter pw = jswb.createSourceWriter();
    generateFormatEntryMethod(pw, map, false);
    generateFormatEntryMethod(pw, map, true);
    pw.close();
  }

  private void generateFormatEntryMethod(SourceWriter pw, Map<String, String> map,
      boolean useSafeHtml) {
    String type = useSafeHtml ? "SafeHtml" : "String";
    pw.println();
    pw.println("@Override");
    pw.println("public " + type + " formatEntry(int index, int count, " + type + " left, "
        + type + " formattedTail) {");
    pw.indent();
    // TODO(jat): make this more general if CLDR ever includes counts besides 2
    String pattern = map.get("2");
    if (pattern != null) {
      pw.println("switch (count) {");
      pw.indent();
      pw.println("case 2:");
      pw.indentln("return " + generateReturn(pattern, useSafeHtml) + ";");
      pw.outdent();
      pw.println("}");
    }
    pattern = map.get("start");
    if (pattern != null) {
      pw.println("if (index == 0) {");
      pw.indentln("return " + generateReturn(pattern, useSafeHtml) + ";");
      pw.println("}");
    }
    pattern = map.get("end");
    if (pattern != null) {
      pw.println("if (index >= count - 2) {");
      pw.indentln("return " + generateReturn(pattern, useSafeHtml) + ";");
      pw.println("}");
    }
    pattern = map.get("middle");
    pw.println("return " + generateReturn(pattern, useSafeHtml) + ";");
    pw.outdent();
    pw.println("}");
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
