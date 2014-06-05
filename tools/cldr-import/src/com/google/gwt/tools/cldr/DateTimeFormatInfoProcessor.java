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
import com.google.gwt.i18n.rebind.DateTimePatternGenerator;
import com.google.gwt.i18n.server.MessageFormatUtils.ArgumentChunk;
import com.google.gwt.i18n.server.MessageFormatUtils.DefaultTemplateChunkVisitor;
import com.google.gwt.i18n.server.MessageFormatUtils.MessageStyle;
import com.google.gwt.i18n.server.MessageFormatUtils.StringChunk;
import com.google.gwt.i18n.server.MessageFormatUtils.TemplateChunk;
import com.google.gwt.i18n.server.MessageFormatUtils.VisitorAbortException;
import com.google.gwt.i18n.shared.DateTimeFormatInfo;
import com.google.gwt.i18n.shared.GwtLocale;

import org.unicode.cldr.util.XPathParts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Process data about date formatting; generates implementations of
 * {@link DateTimeFormatInfo}.
 */
public class DateTimeFormatInfoProcessor extends Processor {

  private static final String CATEGORY_DATE = "date";
  private static final String CATEGORY_DATE_TIME = "dateTime";
  private static final String CATEGORY_DAY_PERIOD_ABBREV = "dayPeriod-abbrev";
  private static final String CATEGORY_ERA_ABBREV = "era-abbrev";
  private static final String CATEGORY_ERA_WIDE = "era-wide";
  private static final String CATEGORY_PREDEF = "predef";
  private static final String CATEGORY_QUARTER_ABBREV = "quarter-abbrev";
  private static final String CATEGORY_QUARTER_WIDE = "quarter-wide";
  private static final String CATEGORY_TIME = "time";
  private static final String CATEGORY_WEEKDATA = "weekdata";

  private static final String[] DAYS = new String[] {
      "sun", "mon", "tue", "wed", "thu", "fri", "sat"};

  /**
   * Index of the formats, ordered by the method name.
   */
  private static final SortedMap<String, String> FORMAT_BY_METHOD;

  /**
   * Map of skeleton format patterns and the method name suffix that uses them.
   */
  private static final Map<String, String> FORMATS;

  private static final String KEY_FIRST_DAY = "firstDay";
  private static final String KEY_FULL = "full";
  private static final String KEY_LONG = "long";
  private static final String KEY_MEDIUM = "medium";
  private static final String KEY_MIN_DAYS = "minDays";
  private static final String KEY_REDIRECT = "redirect";
  private static final String KEY_SHORT = "short";
  private static final String KEY_WEEKEND_END = "weekendEnd";
  private static final String KEY_WEEKEND_START = "weekendStart";

  private static final String PERIOD_DAY = "day";
  private static final String PERIOD_ERA = "era";
  private static final String PERIOD_MONTH = "month";
  private static final String PERIOD_QUARTER = "quarter";
  private static final String[] ALL_DATE_CATEGORIES = new String[] {
    CATEGORY_DATE, CATEGORY_TIME,
    CATEGORY_PREDEF, CATEGORY_QUARTER_WIDE, CATEGORY_QUARTER_ABBREV,
    LocaleData.CATEGORY_PERIOD_SA_WIDE(PERIOD_DAY),
    LocaleData.CATEGORY_PERIOD_NARROW(PERIOD_DAY),
    LocaleData.CATEGORY_PERIOD_SA_NARROW(PERIOD_DAY),
    LocaleData.CATEGORY_PERIOD_ABBREV(PERIOD_DAY),
    LocaleData.CATEGORY_PERIOD_SA_ABBREV(PERIOD_DAY),
    LocaleData.CATEGORY_PERIOD_WIDE(PERIOD_MONTH),
    LocaleData.CATEGORY_PERIOD_SA_WIDE(PERIOD_MONTH),
    LocaleData.CATEGORY_PERIOD_NARROW(PERIOD_MONTH),
    LocaleData.CATEGORY_PERIOD_SA_NARROW(PERIOD_MONTH),
    LocaleData.CATEGORY_PERIOD_ABBREV(PERIOD_MONTH),
    LocaleData.CATEGORY_PERIOD_SA_ABBREV(PERIOD_MONTH)
  };

  static {
    FORMATS = new HashMap<String, String>();
    FORMATS.put("d", "Day");
    FORMATS.put("hmm", "Hour12Minute");
    FORMATS.put("hmmss", "Hour12MinuteSecond");
    FORMATS.put("Hmm", "Hour24Minute");
    FORMATS.put("Hmmss", "Hour24MinuteSecond");
    FORMATS.put("mss", "MinuteSecond");
    FORMATS.put("MMM", "MonthAbbrev");
    FORMATS.put("MMMd", "MonthAbbrevDay");
    FORMATS.put("MMMM", "MonthFull");
    FORMATS.put("MMMMd", "MonthFullDay");
    FORMATS.put("MMMMEEEEd", "MonthFullWeekdayDay");
    FORMATS.put("Md", "MonthNumDay");
    FORMATS.put("y", "Year");
    FORMATS.put("yMMM", "YearMonthAbbrev");
    FORMATS.put("yMMMd", "YearMonthAbbrevDay");
    FORMATS.put("yMMMM", "YearMonthFull");
    FORMATS.put("yMMMMd", "YearMonthFullDay");
    FORMATS.put("yM", "YearMonthNum");
    FORMATS.put("yMd", "YearMonthNumDay");
    FORMATS.put("yMMMEEEd", "YearMonthWeekdayDay");
    FORMATS.put("yQQQQ", "YearQuarterFull");
    FORMATS.put("yQ", "YearQuarterShort");

    FORMAT_BY_METHOD = new TreeMap<String, String>();
    for (Map.Entry<String, String> entry : FORMATS.entrySet()) {
      FORMAT_BY_METHOD.put(entry.getValue(), entry.getKey());
    }
  }

  /**
   * Convert the unlocalized name of a day ("sun".."sat") into a day number of
   * the week, ie 0-6.
   * 
   * @param day abbreviated, unlocalized name of the day ("sun".."sat")
   * @return the day number, 0-6
   * @throws IllegalArgumentException if the day name is not found
   */
  private static int getDayNumber(String day) {
    for (int i = 0; i < DAYS.length; ++i) {
      if (DAYS[i].equals(day)) {
        return i;
      }
    }
    throw new IllegalArgumentException();
  }

  public DateTimeFormatInfoProcessor(Processors processors) {
    super(processors);
//    addEntries(CATEGORY_PREDEF,
//        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/dateTimeFormats/"
//            + "availableFormats", "dateFormatItem", "id");
    addNameEntries(PERIOD_MONTH);
    addNameEntries(PERIOD_DAY);
    addNameEntries(PERIOD_QUARTER);

    // only add the entries we will use to avoid overriding a parent for
    // differences that don't matter.
    addEntries(CATEGORY_DAY_PERIOD_ABBREV,
        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/dayPeriods/"
            + "dayPeriodContext[@type=\"format\"]/"
            + "dayPeriodWidth[@type=\"abbreviated\"]/dayPeriod[@type=\"am\"]", "dayPeriod", "type");
    addEntries(CATEGORY_DAY_PERIOD_ABBREV,
        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/dayPeriods/"
            + "dayPeriodContext[@type=\"format\"]/"
            + "dayPeriodWidth[@type=\"abbreviated\"]/dayPeriod[@type=\"pm\"]", "dayPeriod", "type");

    addEntries(CATEGORY_ERA_ABBREV,
        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/eras/eraAbbr", PERIOD_ERA, "type");
    addEntries(CATEGORY_ERA_WIDE,
        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/eras/eraNames", PERIOD_ERA, "type");
    addDateTimeFormatEntries(CATEGORY_DATE);
    addDateTimeFormatEntries(CATEGORY_TIME);
    addDateTimeFormatEntries(CATEGORY_DATE_TIME);
    addEntries(CATEGORY_PREDEF, "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/"
        + "dateTimeFormats/availableFormats", "dateFormatItem", "id");
    addTerritoryEntries(CATEGORY_WEEKDATA,
        "//supplementalData/weekData/firstDay", KEY_FIRST_DAY, PERIOD_DAY);
    addTerritoryEntries(CATEGORY_WEEKDATA,
        "//supplementalData/weekData/weekendStart", KEY_WEEKEND_START, PERIOD_DAY);
    addTerritoryEntries(CATEGORY_WEEKDATA,
        "//supplementalData/weekData/weekendEnd", KEY_WEEKEND_END, PERIOD_DAY);
    addTerritoryEntries(CATEGORY_WEEKDATA,
        "//supplementalData/weekData/minDays", KEY_MIN_DAYS, "count");
  }

  /**
   * @param period "month", "day", "quarter", "dayPeriod",
   * @param cldrFactory
   */
  public void addNameEntries(String period) {
    addEntries(period + "-abbrev",
        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/" + period + "s/" + period
            + "Context[@type=\"format\"]/" + period + "Width[@type=\"abbreviated\"]", period,
        "type");
    addEntries(period + "-narrow",
        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/" + period + "s/" + period
            + "Context[@type=\"format\"]/" + period + "Width[@type=\"narrow\"]", period, "type");
    addEntries(period + "-wide",
        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/" + period + "s/" + period
            + "Context[@type=\"format\"]/" + period + "Width[@type=\"wide\"]", period, "type");
    addEntries(period + "-sa-abbrev",
        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/" + period + "s/" + period
            + "Context[@type=\"stand-alone\"]/" + period + "Width[@type=\"abbreviated\"]", period,
        "type");
    addEntries(period + "-sa-narrow",
        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/" + period + "s/" + period
            + "Context[@type=\"stand-alone\"]/" + period + "Width[@type=\"narrow\"]", period,
        "type");
    addEntries(period + "-sa-wide",
        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/" + period + "s/" + period
            + "Context[@type=\"stand-alone\"]/" + period + "Width[@type=\"wide\"]", period, "type");
  }

  public void addDateTimeFormatEntries(String group) {
    addAttributeEntries(group, "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/"
        + group + "Formats/default", "default", "default", "choice", "medium");
    addDateTimeFormatEntries(group, "full");
    addDateTimeFormatEntries(group, "long");
    addDateTimeFormatEntries(group, "medium");
    addDateTimeFormatEntries(group, "short");
  }

  private void addDateTimeFormatEntries(String group, String length) {
    addEntries(group, "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/" + group
        + "Formats/" + group + "FormatLength" + "[@type=\"" + length + "\"]/" + group
        + "Format[@type=\"standard\"]" + "/pattern[@type=\"standard\"]", group + "FormatLength",
        "type");
  }

  @Override
  public void cleanupData() {
    System.out.println("Removing duplicates from date/time formats");
    localeData.copyLocaleData("en", "default", CATEGORY_ERA_WIDE, CATEGORY_ERA_ABBREV, CATEGORY_QUARTER_WIDE,
        CATEGORY_QUARTER_ABBREV, LocaleData.CATEGORY_PERIOD_WIDE(PERIOD_DAY),
        LocaleData.CATEGORY_PERIOD_SA_WIDE(PERIOD_DAY),
        LocaleData.CATEGORY_PERIOD_NARROW(PERIOD_DAY),
        LocaleData.CATEGORY_PERIOD_SA_NARROW(PERIOD_DAY),
        LocaleData.CATEGORY_PERIOD_ABBREV(PERIOD_DAY),
        LocaleData.CATEGORY_PERIOD_SA_ABBREV(PERIOD_DAY),
        LocaleData.CATEGORY_PERIOD_WIDE(PERIOD_MONTH),
        LocaleData.CATEGORY_PERIOD_SA_WIDE(PERIOD_MONTH),
        LocaleData.CATEGORY_PERIOD_NARROW(PERIOD_MONTH),
        LocaleData.CATEGORY_PERIOD_SA_NARROW(PERIOD_MONTH),
        LocaleData.CATEGORY_PERIOD_ABBREV(PERIOD_MONTH),
        LocaleData.CATEGORY_PERIOD_SA_ABBREV(PERIOD_MONTH));
    removeUnusedFormats();
    localeData.removeDuplicates(CATEGORY_PREDEF);
    localeData.removeDuplicates(CATEGORY_WEEKDATA);
    localeData.removeDuplicates(CATEGORY_DATE);
    localeData.removeDuplicates(CATEGORY_TIME);
    localeData.removeDuplicates(CATEGORY_DATE_TIME);
    localeData.removeCompleteDuplicates(CATEGORY_DAY_PERIOD_ABBREV);
    computePeriodRedirects(PERIOD_DAY);
    computePeriodRedirects(PERIOD_MONTH);
    computePeriodRedirects(PERIOD_QUARTER);
    computePeriodRedirects(PERIOD_ERA);
    removePeriodDuplicates(PERIOD_DAY);
    removePeriodDuplicates(PERIOD_MONTH);
    removePeriodDuplicates(PERIOD_QUARTER);
    removePeriodDuplicates(PERIOD_ERA);
    /*
     * Check ICU patterns
     *
     * String cldrPattern = localeData.getEntry(CATEGORY_PREDEF, locale, skeleton);
     * if (cldrPattern != null && !cldrPattern.equals(pattern)) {
     *   System.err.println("Mismatch on skeleton pattern in locale " + locale + " for skeleton '"
     *       + skeleton + "': icu='" + pattern + "', cldr='" + cldrPattern + "'");
     * }
    */
  }

  /**
   * Generate an override for a method which takes String arguments, which
   * simply redirect to another method based on a default value.
   * 
   * @param pw
   * @param category
   * @param locale
   * @param method
   * @param args
   */
  protected void generateArgMethod(SourceWriter pw, String category, GwtLocale locale,
      String method, String... args) {
    String value = localeData.getEntry(category, locale, "default");
    if (value != null && value.length() > 0) {
      pw.println();
      pw.println("@Override");
      pw.print("public String " + method + "(");
      String prefix = "";
      for (String arg : args) {
        pw.print(prefix + "String " + arg);
        prefix = ", ";
      }
      pw.println(") {");
      pw.indent();
      pw.print("return " + method + Character.toTitleCase(value.charAt(0)) + value.substring(1)
          + "(");
      prefix = "";
      for (String arg : args) {
        pw.print(prefix + arg);
        prefix = ", ";
      }
      pw.println(");");
      pw.outdent();
      pw.println("}");
    }
  }

  /**
   * Generate an override for a method which takes String arguments.
   * 
   * @param pw
   * @param category
   * @param locale
   * @param key
   * @param method
   * @param args
   */
  protected void generateArgMethodRedirect(SourceWriter pw, String category, GwtLocale locale,
      String key, String method, final String... args) {
    String value = localeData.getEntry(category, locale, key);
    if (value != null) {
      pw.println();
      pw.println("@Override");
      pw.print("public String " + method + "(");
      String prefix = "";
      for (String arg : args) {
        pw.print(prefix + "String " + arg);
        prefix = ", ";
      }
      pw.println(") {");
      final StringBuilder buf = new StringBuilder();
      final StringGenerator gen = StringGenerator.create(buf, false, true);
      try {
        List<TemplateChunk> chunks = MessageStyle.MESSAGE_FORMAT.parse(value);
        for (TemplateChunk chunk : chunks) {
          chunk.accept(new DefaultTemplateChunkVisitor() {
            @Override
            public void visit(ArgumentChunk argChunk) {
              gen.appendStringValuedExpression(args[argChunk.getArgumentNumber()]);
            }

            @Override
            public void visit(StringChunk stringChunk) {
              gen.appendStringLiteral(stringChunk.getString());
            }
          });
        }
      } catch (ParseException e) {
        throw new RuntimeException("Unable to parse pattern '" + value + "' for locale " + locale
            + " key " + category + "/" + key, e);
      } catch (VisitorAbortException e) {
        throw new RuntimeException("Unable to parse pattern '" + value + "' for locale " + locale
            + " key " + category + "/" + key, e);
      }
      gen.completeString();
      pw.indentln("return " + buf.toString() + ";");
      pw.println("}");
    }
  }

  /**
   * Generate a method which returns a day number as an integer.
   * 
   * @param pw
   * @param locale
   * @param key
   * @param method
   */
  protected void generateDayNumber(SourceWriter pw, GwtLocale locale, String key, String method) {
    String day = localeData.getEntry(CATEGORY_WEEKDATA, locale, key);
    if (day != null) {
      int value = getDayNumber(day);
      pw.println();
      pw.println("@Override");
      pw.println("public int " + method + "() {");
      pw.indentln("return " + value + ";");
      pw.println("}");
    }
  }

  /**
   * Generate a method which returns a format string for a given predefined
   * skeleton pattern.
   * 
   * @param locale
   * @param pw
   * @param skeleton
   * @param methodSuffix
   */
  protected void generateFormat(GwtLocale locale, SourceWriter pw, String skeleton,
      String methodSuffix) {
    String pattern = localeData.getEntry(CATEGORY_PREDEF, locale, skeleton);
    generateStringValue(pw, "format" + methodSuffix, pattern);
  }

  /**
   * Generate a series of methods which returns names in wide, narrow, and
   * abbreviated lengths plus their standalone versions.
   * 
   * @param pw
   * @param group
   * @param locale
   * @param methodPrefix
   * @param keys
   */
  protected void generateFullStringList(SourceWriter pw, String group, GwtLocale locale,
      String methodPrefix, String... keys) {
    generateStringListPair(pw, group, locale, methodPrefix, "Full", LocaleData.WIDTH_WIDE, keys);
    generateStringListPair(pw, group, locale, methodPrefix, "Narrow", LocaleData.WIDTH_NARROW,
        keys);
    generateStringListPair(pw, group, locale, methodPrefix, "Short", LocaleData.WIDTH_ABBREV,
        keys);
  }

  /**
   * Generate an override of a standalone names list that simply redirects to
   * the non-standalone version.
   * 
   * @param pw
   * @param methodPrefix
   */
  protected void generateStandaloneRedirect(SourceWriter pw, String methodPrefix) {
    pw.println();
    pw.println("@Override");
    pw.println("public String[] " + methodPrefix + "Standalone" + "() {");
    pw.indentln("return " + methodPrefix + "();");
    pw.println("}");
  }

  /**
   * Generate a method which returns a list of strings.
   * 
   * @param pw
   * @param category
   * @param fallbackCategory
   * @param locale
   * @param method
   * @param keys
   * @return true if the method was skipped as identical to its ancestor
   */
  protected boolean generateStringList(SourceWriter pw, String category, String fallbackCategory,
      GwtLocale locale, String method, String... keys) {
    Map<String, String> map = localeData.getEntries(category, locale);
    Map<String, String> fallback =
        fallbackCategory == null ? Collections.<String, String> emptyMap() : localeData.getEntries(
            fallbackCategory, locale);
    if (map == null || map.isEmpty() && fallback != null && !fallback.isEmpty()) {
      return true;
    }
    if (map != null && !map.isEmpty()) {
      if (fallbackCategory != null) {
        // see if the entry is the same as the fallback
        boolean different = false;
        for (String key : keys) {
          String value = map.get(key);
          if (value != null && !value.equals(fallback.get(key))) {
            different = true;
            break;
          }
        }
        if (!different) {
          return true;
        }
      }
      pw.println();
      pw.println("@Override");
      pw.println("public String[] " + method + "() {");
      pw.indent();
      pw.print("return new String[] {");
      pw.indent();
      pw.indent();
      boolean first = true;
      for (String key : keys) {
        String value = map.get(key);
        if (value == null) {
          value = fallback.get(key);
        }
        if (value == null) {
          System.err.println("Missing \"" + key + "\" in " + locale + "/" + category);
          value = "";
        }
        if (first) {
          first = false;
        } else {
          pw.print(",");
        }
        pw.println();
        pw.print("\"" + value.replace("\"", "\\\"") + "\"");
      }
      pw.println();
      pw.outdent();
      pw.outdent();
      pw.println("};");
      pw.outdent();
      pw.println("}");
    }
    return false;
  }

  protected void generateStringListPair(SourceWriter pw, String group, GwtLocale locale,
      String methodPrefix, String width, String categorySuffix, String... keys) {
    generateStringList(pw, LocaleData.CATEGORY_PERIOD_WIDTH(group, categorySuffix, false), null,
        locale, methodPrefix + width, keys);
    String redirect = localeData.getEntry(LocaleData.CATEGORY_PERIOD_WIDTH(group, categorySuffix,
        true, true), locale, KEY_REDIRECT);
    if ("yes".equals(redirect)) {
      generateStandaloneRedirect(pw, methodPrefix + width);
    } else {
      generateStringList(pw, LocaleData.CATEGORY_PERIOD_WIDTH(group, categorySuffix, true),
          LocaleData.CATEGORY_PERIOD_WIDTH(group, categorySuffix, false), locale,
          methodPrefix + width + "Standalone", keys);
    }
  }

//  @Override
//  protected void loadData() throws IOException {
//    System.out.println("Loading data for date/time formats");
//    localeData.addVersions(cldrFactory);
//    localeData.addEntries(CATEGORY_PREDEF, cldrFactory,
//        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/dateTimeFormats/"
//            + "availableFormats", "dateFormatItem", "id");
//    localeData.addNameEntries(PERIOD_MONTH, cldrFactory);
//    localeData.addNameEntries(PERIOD_DAY, cldrFactory);
//    localeData.addNameEntries(PERIOD_QUARTER, cldrFactory);
//
//    // only add the entries we will use to avoid overriding a parent for
//    // differences that don't matter.
//    localeData.addEntries(CATEGORY_DAY_PERIOD_ABBREV, cldrFactory,
//        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/dayPeriods/"
//            + "dayPeriodContext[@type=\"format\"]/"
//            + "dayPeriodWidth[@type=\"abbreviated\"]/dayPeriod[@type=\"am\"]", "dayPeriod", "type");
//    localeData.addEntries(CATEGORY_DAY_PERIOD_ABBREV, cldrFactory,
//        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/dayPeriods/"
//            + "dayPeriodContext[@type=\"format\"]/"
//            + "dayPeriodWidth[@type=\"abbreviated\"]/dayPeriod[@type=\"pm\"]", "dayPeriod", "type");
//
//    localeData.addEntries(CATEGORY_ERA_ABBREV, cldrFactory,
//        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/eras/eraAbbr", PERIOD_ERA, "type");
//    localeData.addEntries(CATEGORY_ERA_WIDE, cldrFactory,
//        "//ldml/dates/calendars/calendar[@type=\"gregorian\"]/eras/eraNames", PERIOD_ERA, "type");
//    localeData.addDateTimeFormatEntries(CATEGORY_DATE, cldrFactory);
//    localeData.addDateTimeFormatEntries(CATEGORY_TIME, cldrFactory);
//    localeData.addDateTimeFormatEntries(CATEGORY_DATE_TIME, cldrFactory);
//    loadWeekData();
//    loadFormatPatterns();
//  }

  /**
   * Write an output file.
   * 
   * @param locale
   * @throws IOException
   * @throws FileNotFoundException
   */
  protected void writeOneOutputFile(GwtLocale locale) throws IOException,
      FileNotFoundException {
    // TODO(jat): make uz_UZ inherit from uz_Cyrl rather than uz, for example
    sharedLocaleData.addEntry(CATEGORY_GENCLASSES, locale, "DateTimeFormatInfo",
        "com.google.gwt.i18n.shared.impl.cldr.DateTimeFormatInfoImpl" + localeSuffix(locale));
    String myClass = "DateTimeFormatInfoImpl" + localeSuffix(locale);
    GwtLocale parent = localeData.inheritsFrom(locale, ALL_DATE_CATEGORIES);
    ProcessorCodeGenContext codeGen = new ProcessorCodeGenContext("user/src/");
    JavaSourceWriterBuilder jswb = codeGen.addClass("com.google.gwt.i18n.shared.impl.cldr", myClass);
    jswb.setCallbacks(new PrintVersionCallback(locale));
    if (locale.isDefault()) {
      jswb.addImport("com.google.gwt.i18n.shared.DateTimeFormatInfo");
    }
    jswb.setJavaDocCommentForClass("Implementation of DateTimeFormatInfo for the \"" + locale
        + "\" locale.");
    if (locale.isDefault()) {
      jswb.addImplementedInterface(DateTimeFormatInfo.class.getSimpleName());
    } else {
      jswb.setSuperclass("DateTimeFormatInfoImpl" + localeSuffix(parent));
    }
    SourceWriter pw = jswb.createSourceWriter();

    // write AM/PM names
    generateStringList(pw, CATEGORY_DAY_PERIOD_ABBREV, null, locale, "ampms", "am", "pm");

    // write standard date formats
    generateArgMethod(pw, CATEGORY_DATE, locale, "dateFormat");
    generateStringMethod(pw, CATEGORY_DATE, locale, KEY_FULL, "dateFormatFull");
    generateStringMethod(pw, CATEGORY_DATE, locale, KEY_LONG, "dateFormatLong");
    generateStringMethod(pw, CATEGORY_DATE, locale, KEY_MEDIUM, "dateFormatMedium");
    generateStringMethod(pw, CATEGORY_DATE, locale, KEY_SHORT, "dateFormatShort");

    // write methods for assembling date/time formats
    generateArgMethod(pw, CATEGORY_DATE_TIME, locale, CATEGORY_DATE_TIME, "timePattern", "datePattern");
    generateArgMethodRedirect(pw, CATEGORY_DATE_TIME, locale, KEY_FULL, "dateTimeFull", "timePattern",
        "datePattern");
    generateArgMethodRedirect(pw, CATEGORY_DATE_TIME, locale, KEY_LONG, "dateTimeLong", "timePattern",
        "datePattern");
    generateArgMethodRedirect(pw, CATEGORY_DATE_TIME, locale, KEY_MEDIUM, "dateTimeMedium", "timePattern",
        "datePattern");
    generateArgMethodRedirect(pw, CATEGORY_DATE_TIME, locale, KEY_SHORT, "dateTimeShort", "timePattern",
        "datePattern");

    // write era names
    generateStringList(pw, CATEGORY_ERA_WIDE, null, locale, "erasFull", "0", "1");
    generateStringList(pw, CATEGORY_ERA_ABBREV, null, locale, "erasShort", "0", "1");

    // write firstDayOfTheWeek
    generateDayNumber(pw, locale, KEY_FIRST_DAY, "firstDayOfTheWeek");

    // write predefined date/time formats
    for (Map.Entry<String, String> entry : FORMAT_BY_METHOD.entrySet()) {
      generateFormat(locale, pw, entry.getValue(), entry.getKey());
    }

    // write month names
    generateFullStringList(pw, PERIOD_MONTH, locale, "months", "1", "2", "3", "4", "5", "6", "7", "8",
        "9", "10", "11", "12");

    // write quarter names
    generateStringList(pw, CATEGORY_QUARTER_WIDE, null, locale, "quartersFull", "1", "2", "3", "4");
    generateStringList(pw, CATEGORY_QUARTER_ABBREV, null, locale, "quartersShort", "1", "2", "3", "4");

    // write standard time formats
    generateArgMethod(pw, CATEGORY_TIME, locale, "timeFormat");
    generateStringMethod(pw, CATEGORY_TIME, locale, KEY_FULL, "timeFormatFull");
    generateStringMethod(pw, CATEGORY_TIME, locale, KEY_LONG, "timeFormatLong");
    generateStringMethod(pw, CATEGORY_TIME, locale, KEY_MEDIUM, "timeFormatMedium");
    generateStringMethod(pw, CATEGORY_TIME, locale, KEY_SHORT, "timeFormatShort");

    // write weekday names
    generateFullStringList(pw, PERIOD_DAY, locale, "weekdays", DAYS);

    // write weekend boundaries
    generateDayNumber(pw, locale, KEY_WEEKEND_END, KEY_WEEKEND_END);
    generateDayNumber(pw, locale, KEY_WEEKEND_START, KEY_WEEKEND_START);

    pw.close();
  }

  @Override
  public void writeOutputFiles() throws IOException {
    System.out.println("Writing output for date/time formats");
    for (GwtLocale locale : localeData.getNonEmptyLocales(ALL_DATE_CATEGORIES)) {
      writeOneOutputFile(locale);
    }
  }

  private void computePeriodRedirects(String period) {
    computePeriodRedirects(period, LocaleData.WIDTH_ABBREV);
    computePeriodRedirects(period, LocaleData.WIDTH_NARROW);
    computePeriodRedirects(period, LocaleData.WIDTH_WIDE);
  }

  private void computePeriodRedirects(String period, String width) {
    localeData.computeRedirects(LocaleData.CATEGORY_PERIOD_WIDTH(period, width, false),
        LocaleData.CATEGORY_PERIOD_WIDTH(period, width, true));
  }

  public class LoadFormatPatterns implements QueryMatchCallback {

    public LoadFormatPatterns() {
      cldrData.registerLocale(new XPathQuery("//ldml/dates/calendars/calendar[@type=\"gregorian\"]"
          + "/dateTimeFormats/availableFormats"), this);
    }

    @Override
    public void process(GwtLocale locale, XPathParts parts, String value) {
      // MUSTFIX(jat): implement
    }
  }

  @Override
  public void loadExternalData(GwtLocale locale) {
    DateTimePatternGenerator dtpg = new DateTimePatternGenerator(locale);
    for (Map.Entry<String, String> entry : FORMATS.entrySet()) {
      String skeleton = entry.getKey();
      String pattern = dtpg.getBestPattern(skeleton);
      localeData.addEntry(CATEGORY_PREDEF, locale, skeleton, pattern);
    }
  }

  /**
   * Remove duplicates from period names.
   * 
   * @param group
   */
  private void removePeriodDuplicates(String group) {
    removePeriodWidthDuplicates(group, LocaleData.WIDTH_WIDE);
    removePeriodWidthDuplicates(group, LocaleData.WIDTH_ABBREV);
    removePeriodWidthDuplicates(group, LocaleData.WIDTH_NARROW);
  }

  private void removePeriodWidthDuplicates(String group, String width) {
    localeData.removeCompleteDuplicates(LocaleData.CATEGORY_PERIOD_WIDTH(group, width, false));
    localeData.removeCompleteDuplicates(LocaleData.CATEGORY_PERIOD_WIDTH(group, width, true));
    localeData.removeCompleteDuplicates(LocaleData.CATEGORY_PERIOD_WIDTH(group, width, true, true));
  }

  private void removeUnusedFormats() {
    for (GwtLocale locale : localeData.getAllLocales()) {
      Set<String> toRemove = new HashSet<String>();
      Map<String, String> map = localeData.getEntries(CATEGORY_PREDEF, locale);
      for (Entry<String, String> entry : map.entrySet()) {
        if (!FORMATS.containsKey(entry.getKey())) {
          toRemove.add(entry.getKey());
        }
      }
      localeData.removeEntries(CATEGORY_PREDEF, locale, toRemove);
    }
  }
}
