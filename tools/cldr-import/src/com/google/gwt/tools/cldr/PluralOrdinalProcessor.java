/*
 * Copyright 2012 Google Inc.
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
import com.google.gwt.tools.cldr.PluralRuleParser.BinaryExpr;
import com.google.gwt.tools.cldr.PluralRuleParser.Range;
import com.google.gwt.tools.cldr.PluralRuleParser.Relation;
import com.google.gwt.tools.cldr.PluralRuleParser.Token;
import com.google.gwt.tools.cldr.PluralRuleParser.Tree;
import com.google.gwt.tools.cldr.PluralRuleParser.TreeVisitor;
import com.google.gwt.tools.cldr.PluralRuleParser.Variable;

import org.unicode.cldr.util.CLDRFile;
import org.unicode.cldr.util.Factory;
import org.unicode.cldr.util.XPathParts;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Process CLDR data for plurals/ordinals.
 */
public class PluralOrdinalProcessor extends Processor {

  private Map<String, Map<String, String>> pluralMaps;
  private Map<String, String> pluralLocales;
  private Map<String, Map<String, String>> ordinalMaps;
  private Map<String, String> ordinalLocales;

  public PluralOrdinalProcessor(File outputDir, Factory cldrFactory, LocaleData localeData,
      LocaleData sharedLocaleData) {
    super(outputDir, cldrFactory, localeData, sharedLocaleData);
    pluralMaps = new HashMap<String, Map<String, String>>();
    pluralLocales = new HashMap<String, String>();
    ordinalMaps = new HashMap<String, Map<String, String>>();
    ordinalLocales = new HashMap<String, String>();
  }

  private CLDRFile getSupplementalData(String fileName) {
    try {
      return cldrFactory.make(fileName, false);
    } catch (RuntimeException e) {
      return Factory.make(cldrFactory.getSupplementalDirectory().getPath(), ".*").make(fileName,
          false);
    }
  }

  @Override
  protected void loadData() throws IOException {
    System.out.println("Loading data for plurals/ordinals");
    loadPluralRules("plurals", "PluralRule", "", pluralMaps, pluralLocales);
    loadPluralRules("ordinals", "OrdinalRule", "[@type=\"ordinal\"]", ordinalMaps, ordinalLocales);
  }

  private void loadPluralRules(String fileName, String category, String pluralType,
      Map<String, Map<String, String>> implMap, Map<String, String> localesMap) {
    Map<String, Map<String, String>> rules = new HashMap<String, Map<String, String>>();
    CLDRFile supp = getSupplementalData(fileName);
    XPathParts parts = new XPathParts();
    Iterator<String> iterator = supp.iterator("//supplementalData/plurals" + pluralType
    		+ "/pluralRules");
    while (iterator.hasNext()) {
      String path = iterator.next();
      parts.set(supp.getFullXPath(path));
      String locales = parts.findAttributeValue("pluralRules", "locales");
      if (locales == null) {
        continue;
      }
      String count = parts.findAttributeValue("pluralRule", "count");
      if (count == null) {
        continue;
      }
      String ruleText = supp.getStringValue(path);
      Map<String, String> ruleMap = rules.get(locales);
      if (ruleMap == null) {
        ruleMap = new HashMap<String, String>();
        rules.put(locales, ruleMap);
      }
      ruleMap.put(count, ruleText);
    }
    for (Map.Entry<String, Map<String, String>> rulesEntry : rules.entrySet()) {
      String[] locales = rulesEntry.getKey().split(" ");
      Map<String, String> ruleMap = rulesEntry.getValue();
      String pkg = "com.google.gwt.i18n.shared.cldr";
      String className = category + "Impl_";
      if (locales.length < 3) {
        className += join("_", Arrays.asList(locales));
      } else {
        String hexString = Integer.toHexString(ruleMap.hashCode());
        className += "00000000".substring(hexString.length()) + hexString;
      }
      while (implMap.containsKey(className)) {
        className += "_";
      }
      localesMap.put(className, rulesEntry.getKey());
      implMap.put(className, ruleMap);
      for (String localeName : locales) {
        GwtLocale locale = localeData.getGwtLocale(localeName);
        sharedLocaleData.addEntry("genClasses", locale, category, pkg + "." + className);
      }
    }
  }

  @Override
  protected void writeOutputFiles() throws IOException {
    System.out.println("Writing plural/ordinal implementations");
    writeImplementations(pluralMaps, pluralLocales);
    writeImplementations(ordinalMaps, ordinalLocales);
  }

  private void writeImplementations(Map<String, Map<String, String>> implMap,
      Map<String, String> localeMap) throws IOException {
    for (Map.Entry<String, Map<String, String>> entry : implMap.entrySet()) {
      String className = entry.getKey();
      Map<String, String> ruleMap = entry.getValue();
      Set<String> keySet = ruleMap.keySet();
      String[] keys = keySet.toArray(new String[keySet.size()]);
      Arrays.sort(keys);
      String pkg = "com.google.gwt.i18n.shared.cldr";
      String path = "shared/cldr/" + className + ".java";
      PrintWriter pw = createOutputFile(path);
      printJavaHeader(pw);
      pw.println("package " + pkg + ";");
      pw.println();
      pw.println("import com.google.gwt.i18n.shared.impl.VariantSelectorBase;");
      pw.println();
      pw.println("// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA");
      String locales = localeMap.get(className);
      if (locales != null) {
        pw.println("//  " + locales);
      }
      pw.println("public class " + className + " extends VariantSelectorBase {");
      pw.println();
      pw.println("  public " + className + "() {");
      pw.println("    super(new VariantForm[] {");
      for (String form : keys) {
        pw.println("      VariantForm." + form.toUpperCase(Locale.ENGLISH) + ",");
      }
      pw.println("    });");
      pw.println("  };");
      pw.println();
      pw.println("  @Override");
      pw.println("  public String getFormDescription(VariantForm form) {");
      pw.println("    switch (form) {");
      for (String form : keys) {
        pw.println("      case " + form.toUpperCase(Locale.ENGLISH) + ":");
        pw.println("        return " + CodeGenUtils.asStringLiteral(ruleMap.get(form)) + ";");
      }
      pw.println("      default:");
      pw.println("        return \"anything else\";");
      pw.println("    }");
      pw.println("  }");
      pw.println();
      pw.println("  @Override");
      pw.println("  public VariantForm select(double n) {");
      Set<Integer> seenMod = new HashSet<Integer>();
      Map<String, String> javaExpr = new HashMap<String, String>();
      for (String form : keys) {
        String rule = ruleMap.get(form);
        String translatedRule = translateCldrToJava(rule, seenMod);
        javaExpr.put(form,  translatedRule);
      }
      ArrayList<Integer> mods = new ArrayList<Integer>(seenMod);
      Collections.sort(mods);
      for (int mod : mods) {
        pw.println("    double n" + mod + " = n % " + mod + ";");
      }
      for (String form : keys) {
        String rule = javaExpr.get(form);
        writeOneRule(pw, rule, form);
      }
      pw.println("    return VariantForm.OTHER;");
      pw.println("  }");
      pw.println("}");
      pw.close();
    }
  }

  private class JavaCodeGenerator extends TreeVisitor {

    private final Set<Integer> seenMod;
    private final Stack<String> exprStack;

    public JavaCodeGenerator(Set<Integer> seenMod) {
      this.seenMod = seenMod;
      exprStack = new Stack<String>();
    }

    @Override
    public void visit(BinaryExpr binExpr) {
      switch (binExpr.op){
        case AND:
        case OR:
          String right = exprStack.pop();
          String left = exprStack.pop();
          exprStack.push("(" + left + ") " + (binExpr.op == Token.Kind.AND ? "&&" : "||") + " ("
              + right + ")");
          break;
        default:
          throw new RuntimeException(); 
      }
    }

    @Override
    public void visit(Range range) {
      // we will actually process this in the parent node, since we may need
      // to clone the left side
      exprStack.push("<unused>");
    }

    @Override
    public void visit(Variable var) {
      String name = "n";
      if (var.mod > 0) {
        seenMod.add(var.mod);
        name += String.valueOf(var.mod);
      }
      exprStack.push(name);
    }

    @Override
    public void visit(Relation rel) {
      boolean intOnly = true;
      switch (rel.op){
        case WITHIN:
          intOnly = false;
          /* FALL-THROUGH */
        case IN:
        case IS:
          // ignore the expression for the right side
          exprStack.pop();
          String var = exprStack.pop();
          StringBuilder buf = new StringBuilder();
          int[] bounds = rel.right.rangeValues;
          int n = bounds.length;
          if (intOnly && (n > 2 || bounds[0] != bounds[1])) {
            if (rel.negate) {
              buf.append("(n - (long) n != 0.0) || (");
            } else {
              buf.append("(n - (long) n == 0.0) && (");
            }
          }
          for (int i = 0; i < n; i += 2) {
            if (n > 2) {
              if (i > 0) {
                buf.append(rel.negate ? " && " : " || ");
              }
              buf.append('(');
            }
            if (rel.negate) {
              if (bounds[i] != bounds[i + 1]) {
                buf.append("").append(var).append(" < ").append(bounds[i]).append(" || ");
                buf.append(var).append(" > ").append(bounds[i + 1]).append("");
              } else {
                buf.append(var).append(" != ").append(bounds[i]);
              }
            } else {
              if (bounds[i] != bounds[i + 1]) {
                buf.append("").append(var).append(" >= ").append(bounds[i]).append(" && ");
                buf.append(var).append(" <= ").append(bounds[i + 1]).append("");
              } else {
                buf.append(var).append(" == ").append(bounds[i]);
              }
            }
            if (n > 2) {
              buf.append(')');
            }
          }
          if (intOnly && (n > 2 || bounds[0] != bounds[1])) {
            buf.append(')');
          }
          exprStack.push(buf.toString());
          break;
        default:
          throw new RuntimeException(); 
      }
    }

    public String getResult() {
      return exprStack.pop();
    }
  }

  private String translateCldrToJava(String rule, Set<Integer> seenMod) {
    Tree parseTree = new PluralRuleParser().parse(rule);
    JavaCodeGenerator codeGen = new JavaCodeGenerator(seenMod);
    parseTree.accept(codeGen);
    return codeGen.getResult();
  }

  private void writeOneRule(PrintWriter pw, String expr, String form) {
    pw.println("    if (" + expr + ") {");
    pw.println("      return VariantForm." + form.toUpperCase(Locale.ENGLISH) + ";");
    pw.println("    }");
  }
}
