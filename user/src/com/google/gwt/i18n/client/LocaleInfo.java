/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.i18n.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.constants.DateTimeConstants;
import com.google.gwt.i18n.client.constants.NumberConstants;
import com.google.gwt.i18n.shared.Locales;

/**
 * Provides access to the currently-active locale and the list of available
 * locales.
 */
@SuppressWarnings("deprecation")
public class LocaleInfo {

  /**
   * Delegate to shared implementation.
   */
  private static class DateTimeFormatInfoDelegator implements DateTimeFormatInfo {

    private com.google.gwt.i18n.shared.DateTimeFormatInfo dtfi;
 
    public DateTimeFormatInfoDelegator(com.google.gwt.i18n.shared.DateTimeFormatInfo dtfi) {
      this.dtfi = dtfi;
    }

    @Override
    public String[] ampms() {
      return dtfi.ampms();
    }

    @Override
    public String dateFormat() {
      return dtfi.dateFormat();
    }

    @Override
    public String dateFormatFull() {
      return dtfi.dateFormatFull();
    }

    @Override
    public String dateFormatLong() {
      return dtfi.dateFormatLong();
    }

    @Override
    public String dateFormatMedium() {
      return dtfi.dateFormatMedium();
    }

    @Override
    public String dateFormatShort() {
      return dtfi.dateFormatShort();
    }

    @Override
    public String dateTime(String timePattern, String datePattern) {
      return dtfi.dateTime(timePattern, datePattern);
    }

    @Override
    public String dateTimeFull(String timePattern, String datePattern) {
      return dtfi.dateTimeFull(timePattern, datePattern);
    }

    @Override
    public String dateTimeLong(String timePattern, String datePattern) {
      return dtfi.dateTimeLong(timePattern, datePattern);
    }

    @Override
    public String dateTimeMedium(String timePattern, String datePattern) {
      return dtfi.dateTimeMedium(timePattern, datePattern);
    }

    @Override
    public String dateTimeShort(String timePattern, String datePattern) {
      return dtfi.dateTimeShort(timePattern, datePattern);
    }

    @Override
    public String[] erasFull() {
      return dtfi.erasFull();
    }

    @Override
    public String[] erasShort() {
      return dtfi.erasShort();
    }

    @Override
    public int firstDayOfTheWeek() {
      return dtfi.firstDayOfTheWeek();
    }

    @Override
    public String formatDay() {
      return dtfi.formatDay();
    }

    @Override
    public String formatHour12Minute() {
      return dtfi.formatHour12Minute();
    }

    @Override
    public String formatHour12MinuteSecond() {
      return dtfi.formatHour12MinuteSecond();
    }

    @Override
    public String formatHour24Minute() {
      return dtfi.formatHour24Minute();
    }

    @Override
    public String formatHour24MinuteSecond() {
      return dtfi.formatHour24MinuteSecond();
    }

    @Override
    public String formatMinuteSecond() {
      return dtfi.formatMinuteSecond();
    }

    @Override
    public String formatMonthAbbrev() {
      return dtfi.formatMonthAbbrev();
    }

    @Override
    public String formatMonthAbbrevDay() {
      return dtfi.formatMonthAbbrevDay();
    }

    @Override
    public String formatMonthFull() {
      return dtfi.formatMonthFull();
    }

    @Override
    public String formatMonthFullDay() {
      return dtfi.formatMonthFullDay();
    }

    @Override
    public String formatMonthFullWeekdayDay() {
      return dtfi.formatMonthFullWeekdayDay();
    }

    @Override
    public String formatMonthNumDay() {
      return dtfi.formatMonthNumDay();
    }

    @Override
    public String formatYear() {
      return dtfi.formatYear();
    }

    @Override
    public String formatYearMonthAbbrev() {
      return dtfi.formatYearMonthAbbrev();
    }

    @Override
    public String formatYearMonthAbbrevDay() {
      return dtfi.formatYearMonthAbbrevDay();
    }

    @Override
    public String formatYearMonthFull() {
      return dtfi.formatYearMonthFull();
    }

    @Override
    public String formatYearMonthFullDay() {
      return dtfi.formatYearMonthFullDay();
    }

    @Override
    public String formatYearMonthNum() {
      return dtfi.formatYearMonthNum();
    }

    @Override
    public String formatYearMonthNumDay() {
      return dtfi.formatYearMonthNumDay();
    }

    @Override
    public String formatYearMonthWeekdayDay() {
      return dtfi.formatYearMonthWeekdayDay();
    }

    @Override
    public String formatYearQuarterFull() {
      return dtfi.formatYearQuarterFull();
    }

    @Override
    public String formatYearQuarterShort() {
      return dtfi.formatYearQuarterShort();
    }

    @Override
    public String[] monthsFull() {
      return dtfi.monthsFull();
    }

    @Override
    public String[] monthsFullStandalone() {
      return dtfi.monthsFullStandalone();
    }

    @Override
    public String[] monthsNarrow() {
      return dtfi.monthsNarrow();
    }

    @Override
    public String[] monthsNarrowStandalone() {
      return dtfi.monthsNarrowStandalone();
    }

    @Override
    public String[] monthsShort() {
      return dtfi.monthsShort();
    }

    @Override
    public String[] monthsShortStandalone() {
      return dtfi.monthsShortStandalone();
    }

    @Override
    public String[] quartersFull() {
      return dtfi.quartersFull();
    }

    @Override
    public String[] quartersShort() {
      return dtfi.quartersShort();
    }

    @Override
    public String timeFormat() {
      return dtfi.timeFormat();
    }

    @Override
    public String timeFormatFull() {
      return dtfi.timeFormatFull();
    }

    @Override
    public String timeFormatLong() {
      return dtfi.timeFormatLong();
    }

    @Override
    public String timeFormatMedium() {
      return dtfi.timeFormatMedium();
    }

    @Override
    public String timeFormatShort() {
      return dtfi.timeFormatShort();
    }

    @Override
    public String[] weekdaysFull() {
      return dtfi.weekdaysFull();
    }

    @Override
    public String[] weekdaysFullStandalone() {
      return dtfi.weekdaysFullStandalone();
    }

    @Override
    public String[] weekdaysNarrow() {
      return dtfi.weekdaysNarrow();
    }

    @Override
    public String[] weekdaysNarrowStandalone() {
      return dtfi.weekdaysNarrowStandalone();
    }

    @Override
    public String[] weekdaysShort() {
      return dtfi.weekdaysShort();
    }

    @Override
    public String[] weekdaysShortStandalone() {
      return dtfi.weekdaysShortStandalone();
    }

    @Override
    public int weekendEnd() {
      return dtfi.weekendEnd();
    }

    @Override
    public int weekendStart() {
      return dtfi.weekendStart();
    }
  }

  private static class LocalesInstance {
    public static Locales instance = GWT.create(Locales.class);
  }

  /**
   * Delegate to shared implementation.
   */
  private static class LocalizedNamesDelegator implements LocalizedNames {
    private final com.google.gwt.i18n.shared.LocalizedNames ln;
    
    public LocalizedNamesDelegator(com.google.gwt.i18n.shared.LocalizedNames ln) {
      this.ln = ln;
    }

    @Override
    public String[] getLikelyRegionCodes() {
      return ln.getLikelyRegionCodes();
    }

    @Override
    public String getRegionName(String regionCode) {
      return ln.getRegionName(regionCode);
    }

    @Override
    public String[] getSortedRegionCodes() {
      return ln.getSortedRegionCodes();
    }
  }

  /**
   * Delegate to shared implementation.
   */
  private static class NumberConstantsDelegator implements NumberConstants {
    private final com.google.gwt.i18n.shared.NumberConstants nc;

    public NumberConstantsDelegator(com.google.gwt.i18n.shared.NumberConstants nc) {
      this.nc = nc;
    }

    @Override
    public String currencyPattern() {
      return nc.currencyPattern();
    }

    @Override
    public String decimalPattern() {
      return nc.decimalPattern();
    }

    @Override
    public String decimalSeparator() {
      return nc.decimalSeparator();
    }

    @Override
    public String defCurrencyCode() {
      return nc.defCurrencyCode();
    }

    @Override
    public String exponentialSymbol() {
      return nc.exponentialSymbol();
    }

    @Override
    public String globalCurrencyPattern() {
      return nc.globalCurrencyPattern();
    }

    @Override
    public String groupingSeparator() {
      return nc.groupingSeparator();
    }

    @Override
    public String infinity() {
      return nc.infinity();
    }

    @Override
    public String minusSign() {
      return nc.minusSign();
    }

    @Override
    public String monetaryGroupingSeparator() {
      return nc.monetaryGroupingSeparator();
    }

    @Override
    public String monetarySeparator() {
      return nc.monetarySeparator();
    }

    @Override
    public String notANumber() {
      return nc.notANumber();
    }

    @Override
    public String percent() {
      return nc.percent();
    }

    @Override
    public String percentPattern() {
      return nc.percentPattern();
    }

    @Override
    public String perMill() {
      return nc.perMill();
    }

    @Override
    public String plusSign() {
      return nc.plusSign();
    }

    @Override
    public String scientificPattern() {
      return nc.scientificPattern();
    }

    @Override
    public String simpleCurrencyPattern() {
      return nc.simpleCurrencyPattern();
    }

    @Override
    public String zeroDigit() {
      return nc.zeroDigit();
    }
  }

  /**
   * Currently we only support getting the currently running locale, so this
   * is a static.  In the future, we would need a hash map from locale names
   * to LocaleInfo instances.
   */
  private static LocaleInfo instance
      = new LocaleInfo(GWT.<com.google.gwt.i18n.shared.LocaleInfo>create(com.google.gwt.i18n.shared.LocaleInfo.class));

  /**
   * Returns an array of available locale names.
   */
  public static final String[] getAvailableLocaleNames() {
    /*
     * The set of all locales is constant across all permutations, so this
     * is static.  Ideally, the set of available locales would be generated
     * by a different GWT.create but that would slow the compilation process
     * unnecessarily.
     *
     * This is static, and accesses infoImpl this way, with an eye towards
     * when we implement static LocaleInfo getLocale(String localeName) as
     * you might want to get the list of available locales in order to create
     * instances of each of them.
     */
    return LocalesInstance.instance.getAvailableLocaleNames();
  }

  /**
   * Returns a LocaleInfo instance for the current locale.
   */
  public static final LocaleInfo getCurrentLocale() {
    /*
     * In the future, we could make additional static methods which returned a
     * LocaleInfo instance for a specific locale (from the set of those the app
     * was compiled with), accessed via a method like:
     *    public static LocaleInfo getLocale(String localeName)
     */
    return instance;
  }

  /**
   * Returns the name of the name of the cookie holding the locale to use,
   * which is defined in the config property {@code locale.cookie}.
   * 
   * @return locale cookie name, or null if none
   */
  public static final String getLocaleCookieName() {
    return LocalesInstance.instance.getLocaleCookieName();
  }

  /**
   * Returns the display name of the requested locale in its native locale, if
   * possible. If no native localization is available, the English name will
   * be returned, or as a last resort just the locale name will be returned.  If
   * the locale name is unknown (including an user overrides) or is not a valid
   * locale property value, null is returned.
   *
   * If the I18N module has not been imported, this will always return null.
   *
   * @param localeName the name of the locale to lookup.
   * @return the name of the locale in its native locale
   */
  public static String getLocaleNativeDisplayName(String localeName) {
    /*
     * See the comment from getAvailableLocaleNames() above.
     */
    return LocalesInstance.instance.getLocaleNativeDisplayName(localeName);
  }

  /**
   * Returns the name of the query parameter holding the locale to use, which is
   * defined in the config property {@code locale.queryparam}.
   * 
   * @return locale URL query parameter name, or null if none
   */
  public static String getLocaleQueryParam() {
    return LocalesInstance.instance.getLocaleQueryParam();
  }

  /**
   * Returns the runtime locale (note that this requires the i18n locale property
   * provider's assistance).
   */
  public static native String getRuntimeLocale() /*-{
    return $wnd['__gwt_Locale'];
  }-*/;

  /**
   * Returns true if any locale supported by this build of the app is RTL.
   */
  public static boolean hasAnyRTL() {
    return LocalesInstance.instance.hasAnyRtl();
  }

  private com.google.gwt.i18n.shared.LocaleInfo localeInfo;

  private DateTimeConstants dateTimeConstants;

  private DateTimeFormatInfo dateTimeFormatInfo;

  private NumberConstants numberConstants;

  /**
   * Constructor to be used by subclasses, such as mock classes for testing.
   * Any such subclass should override all methods.
   */
  protected LocaleInfo() {
    localeInfo = null;
  }

  /**
   * Create a LocaleInfo instance, passing in the implementation classes.
   *
   * @param localeInfo LocaleInfoImpl instance to use
   */
  private LocaleInfo(com.google.gwt.i18n.shared.LocaleInfo localeInfo) {
    this.localeInfo = localeInfo;
  }

  /**
   * Returns a DateTimeConstants instance for this locale.
   */
  public final DateTimeConstants getDateTimeConstants() {
    ensureDateTimeConstants();
    return dateTimeConstants;
  }

  /**
   * Returns a DateTimeConstants instance for this locale.
   */
  public final DateTimeFormatInfo getDateTimeFormatInfo() {
    ensureDateTimeFormatInfo();
    return dateTimeFormatInfo;
  }

  /**
   * Returns the name of this locale, such as "default, "en_US", etc.
   */
  public final String getLocaleName() {
    return localeInfo.getLocaleName();
  }

  /**
   * @return an implementation of {@link LocalizedNames} for this locale.
   */
  public final LocalizedNames getLocalizedNames() {
    return new LocalizedNamesDelegator(localeInfo.getLocalizedNames());
  }

  /**
   * Returns a NumberConstants instance for this locale.
   */
  public final NumberConstants getNumberConstants() {
    ensureNumberConstants();
    return numberConstants;
  }

  /**
   * Returns true if this locale is right-to-left instead of left-to-right.
   */
  public final boolean isRTL() {
    return localeInfo.isRTL();
  }

  private void ensureDateTimeConstants() {
    if (dateTimeConstants == null) {
      ensureDateTimeFormatInfo();
      dateTimeConstants = new DateTimeConstantsAdapter(dateTimeFormatInfo);
    }
  }

  private void ensureDateTimeFormatInfo() {
    if (dateTimeFormatInfo == null) {
      dateTimeFormatInfo = new DateTimeFormatInfoDelegator(localeInfo.getDateTimeFormatInfo());
    }
  }

  private void ensureNumberConstants() {
    if (numberConstants == null) {
      numberConstants = new NumberConstantsDelegator(localeInfo.getNumberConstants());
    }
  }
}
