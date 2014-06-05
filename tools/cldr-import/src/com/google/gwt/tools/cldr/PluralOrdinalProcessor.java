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

import com.google.gwt.codegen.server.AbortablePrintWriter;
import com.google.gwt.codegen.server.CodeGenUtils;
import com.google.gwt.codegen.server.JavaSourceWriterBuilder;
import com.google.gwt.codegen.server.JavaSourceWriterBuilder.CallbackHooks;
import com.google.gwt.codegen.server.SourceWriter;
import com.google.gwt.dev.util.collect.Maps;
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.i18n.shared.impl.VariantSelectorBase;
import com.google.gwt.tools.cldr.PluralRuleParser.BinaryExpr;
import com.google.gwt.tools.cldr.PluralRuleParser.Range;
import com.google.gwt.tools.cldr.PluralRuleParser.Relation;
import com.google.gwt.tools.cldr.PluralRuleParser.Token;
import com.google.gwt.tools.cldr.PluralRuleParser.Tree;
import com.google.gwt.tools.cldr.PluralRuleParser.TreeVisitor;
import com.google.gwt.tools.cldr.PluralRuleParser.Variable;
import com.google.gwt.tools.cldr.PluralRuleParser.VariableMod;

import org.unicode.cldr.util.CLDRFile;
import org.unicode.cldr.util.XPathParts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Process CLDR data for plurals/ordinals.
 */
public class PluralOrdinalProcessor extends Processor {

  private static final Pattern AT_SEPARATED = Pattern.compile("\\s*\\Q\\E@\\s*");

  private Map<String, Map<String, String>> pluralMaps;
  private Map<String, String> pluralLocales;
  private Map<String, Map<String, String>> ordinalMaps;
  private Map<String, String> ordinalLocales;

  public PluralOrdinalProcessor(Processors processors) {
    super(processors);
    pluralMaps = new HashMap<String, Map<String, String>>();
    pluralLocales = new HashMap<String, String>();
    ordinalMaps = new HashMap<String, Map<String, String>>();
    ordinalLocales = new HashMap<String, String>();
    loadPluralRules("plurals", "PluralRule", "[@type=\"cardinal\"]", pluralMaps, pluralLocales);
    loadPluralRules("ordinals", "OrdinalRule", "[@type=\"ordinal\"]", ordinalMaps, ordinalLocales);
  }

  private void loadPluralRules(String fileName, String category, String pluralType,
      Map<String, Map<String, String>> implMap, Map<String, String> localesMap) {
    cldrData.registerSupplemental(fileName, new XPathQuery("//supplementalData/plurals" + pluralType
        + "/pluralRules"), new PluralRulesCallback(category, implMap, localesMap));
  }

  private class PluralRulesCallback implements QueryMatchCallback {

    private final String category;
    private final Map<String, Map<String, String>> rules;
    private final Map<String, Map<String, String>> implMap;
    private final Map<String, String> localesMap;

    public PluralRulesCallback(String category, Map<String, Map<String, String>> implMap,
        Map<String, String> localesMap) {
      this.category = category;
      this.implMap = implMap;
      this.localesMap = localesMap;
      rules = new HashMap<String, Map<String, String>>();
      rules.put("default", Maps.create("OTHER", "@ all values"));
      cldrData.addPostProcessor(new Runnable() {
        @Override
        public void run() {
          summarize();
        }
      });
    }

    @Override
    public void process(GwtLocale locale, XPathParts parts, String ruleText) {
      String locales = parts.findAttributeValue("pluralRules", "locales");
      if (locales == null) {
        return;
      }
      String count = parts.findAttributeValue("pluralRule", "count");
      if (count == null) {
        return;
      }
      count = count.toUpperCase(Locale.ENGLISH);
      Map<String, String> ruleMap = rules.get(locales);
      if (ruleMap == null) {
        ruleMap = new HashMap<String, String>();
        rules.put(locales, ruleMap);
      }
      ruleMap.put(count, ruleText);
    }

    private void summarize() {
      for (Map.Entry<String, Map<String, String>> rulesEntry : rules.entrySet()) {
        String[] locales = rulesEntry.getKey().split(" ");
        Arrays.sort(locales);
        Map<String, String> ruleMap = rulesEntry.getValue();
        String pkg = "com.google.gwt.i18n.shared.impl.cldr";
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
        Set<GwtLocale> allLocales = localeData.getAllLocales();
        for (String localeName : locales) {
          GwtLocale locale = localeData.getGwtLocale(localeName);
          if (allLocales.contains(locale)) {
            sharedLocaleData.addEntry(CATEGORY_GENCLASSES, locale, category, pkg + "." + className);
          }
        }
      }
    }
    }

  @Override
  public void writeOutputFiles() throws IOException {
    System.out.println("Writing plural/ordinal implementations");
    writeImplementations(pluralMaps, pluralLocales, "Plural");
    writeImplementations(ordinalMaps, ordinalLocales, "Ordinal");
  }

  private void writeImplementations(Map<String, Map<String, String>> implMap,
      final Map<String, String> localeMap, String type) {
    for (Map.Entry<String, Map<String, String>> entry : implMap.entrySet()) {
      final String className = entry.getKey();
      Map<String, String> ruleMap = entry.getValue();
      Set<String> keySet = ruleMap.keySet();
      String[] keys = keySet.toArray(new String[keySet.size()]);
      Arrays.sort(keys);
      String pkg = "com.google.gwt.i18n.shared.impl.cldr";
      ProcessorCodeGenContext codeGen = new ProcessorCodeGenContext("user/src/");
      JavaSourceWriterBuilder jswb = codeGen.addClass(pkg, className);
      jswb.setCallbacks(new CallbackHooks() {
        @Override
        public void afterImports(AbortablePrintWriter pw) {
          pw.println("// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA");
          pw.println("//  cldrVersion=" + CLDRFile.GEN_VERSION);
        }
      });
      jswb.addImport(VariantSelectorBase.class);
      jswb.setSuperclass("VariantSelectorBase");
      String locales = localeMap.get(className);
      jswb.setJavaDocCommentForClass(type + " rule implementation"
          + (locales != null ? " for locales:\n  " + locales : ""));
      SourceWriter pw = jswb.createSourceWriter();
      pw.println();
      pw.println("public " + className + "() {");
      pw.indent();
      pw.println("super(new VariantForm[] {");
      pw.indent();
      for (String form : keys) {
        pw.println("// " + ruleMap.get(form));
        pw.println("VariantForm." + form + ",");
      }
      pw.outdent();
      pw.println("});");
      pw.outdent();
      pw.println("};");
      pw.println();
      pw.println("@Override");
      pw.println("public String getFormDescription(VariantForm form) {");
      pw.indent();
      pw.println("switch (form) {");
      pw.indent();
      for (String form : keys) {
        if ("OTHER".equals(form)) {
          continue;
        }
        pw.println("case " + form + ":");
        pw.indentln("return " + CodeGenUtils.asStringLiteral(getRuleSamples(ruleMap.get(form))) + ";");
      }
      pw.println("default:");
      String otherDesc = "anything else";
      if (ruleMap.containsKey("OTHER")) {
        otherDesc = getRuleSamples(ruleMap.get("OTHER"));
      }
      pw.indentln("return " + CodeGenUtils.asStringLiteral(otherDesc) + ";");
      pw.outdent();
      pw.println("}");
      pw.outdent();
      pw.println("}");
      pw.println();
      pw.println("@Override");
      pw.println("public VariantForm select(double n, int v) {");
      pw.indent();
      Set<String> seenName = new HashSet<String>();
      Set<VariableMod> seenMod = new HashSet<VariableMod>();
      Map<String, String> javaExpr = new HashMap<String, String>();
      for (String form : keys) {
        if ("OTHER".equals(form)) {
          continue;
        }
        String rule = ruleMap.get(form);
        try {
          String translatedRule = translateCldrToJava(rule, seenName, seenMod);
          javaExpr.put(form,  translatedRule);
        } catch (RuntimeException e) {
          System.err.println("Skipping bad plural rule '" + rule + "' from locales " + locales);
          e.printStackTrace();
        }
      }
      /*
       * From http://cldr.unicode.org/index/downloads/cldr-24#TOC-Plural-rule-changes
       *
       * Symbol  Value
       *   i    integer digits of n.
       *   v    number of visible fraction digits in n, with trailing zeros.
       *   w    number of visible fraction digits in n, without trailing zeros.
       *   f    visible fractional digits in n, with trailing zeros.
       *   t    visible fractional digits in n, without trailing zeros.
       */
      // n is supplied as a parameter
      if (seenName.contains("i")) {
        pw.println("long i = (long) n;");
      }
      // v is supplied as a parameter
      if (seenName.contains("w")) {
        pw.println("int w = getNonzeroFractionDigitsCount(n, v);");
      }
      if (seenName.contains("f")) {
        pw.println("double f = getFractionDigits(n, v, false);");
      }
      if (seenName.contains("t")) {
        pw.println("double t = getFractionDigits(n, v, true);");
      }
      ArrayList<VariableMod> mods = new ArrayList<VariableMod>(seenMod);
      Collections.sort(mods);
      for (VariableMod mod : mods) {
        pw.println("double " + mod.name + mod.mod + " = " + mod.name + " % " + mod.mod + ";");
      }
      for (String form : keys) {
        if (!"OTHER".equals(form)) {
          String rule = javaExpr.get(form);
          writeOneRule(pw, rule, form);
        }
      }
      pw.println("return VariantForm.OTHER;");
      pw.outdent();
      pw.println("}");
      pw.close();
    }
  }

  private static String getRuleSamples(String rule) {
    if (rule == null) {
      return "";
    }
    String[] split = AT_SEPARATED.split(rule);
    if (split.length < 2) {
      return rule;
    }
    List<String> samples = Arrays.asList(split);
    return join("; ", samples.subList(1, samples.size()));
  }

  private class JavaCodeGenerator extends TreeVisitor {

    private final Set<String> seenName;
    private final Set<VariableMod> seenMod;
    private final Stack<String> exprStack;

    public JavaCodeGenerator(Set<String> seenName, Set<VariableMod> seenMod) {
      this.seenName = seenName;
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
      String name = var.name;
      seenName.add(name);
      if (var.mod > 0) {
        seenMod.add(new VariableMod(var.name, var.mod));
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
        case EQUALS:
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

  private String translateCldrToJava(String rule, Set<String> seenName, Set<VariableMod> seenMod) {
    String[] split = AT_SEPARATED.split(rule);
    Tree parseTree = new PluralRuleParser().parse(split[0]);
    JavaCodeGenerator codeGen = new JavaCodeGenerator(seenName, seenMod);
    parseTree.accept(codeGen);
    return codeGen.getResult();
  }

  private void writeOneRule(SourceWriter pw, String expr, String form) {
    pw.println("if (" + expr + ") {");
    pw.indentln("return VariantForm." + form.toUpperCase(Locale.ENGLISH) + ";");
    pw.println("}");
  }
}
