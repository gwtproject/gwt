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
package com.google.gwt.i18n.client.impl.cldr;
// DO NOT EDIT - GENERATED FROM CLDR AND ICU DATA

/**
 * Implementation of DateTimeFormatInfo for the "nds" locale.
 */
public class DateTimeFormatInfoImpl_nds extends DateTimeFormatInfoImpl {

  @Override
  public String[] ampms() {
    return new String[] {
        "vm",
        "nm"
    };
  }

  @Override
  public String dateFormatFull() {
    return "EEEE, 'de' d. MMMM y";
  }

  @Override
  public String dateFormatLong() {
    return "d. MMMM y";
  }

  @Override
  public String dateFormatMedium() {
    return "d. MMM y";
  }

  @Override
  public String dateFormatShort() {
    return "d.MM.yy";
  }

  @Override
  public String dateTimeFull(String timePattern, String datePattern) {
    return datePattern + ", " + timePattern;
  }

  @Override
  public String dateTimeLong(String timePattern, String datePattern) {
    return datePattern + ", " + timePattern;
  }

  @Override
  public String dateTimeMedium(String timePattern, String datePattern) {
    return datePattern + ", " + timePattern;
  }

  @Override
  public String dateTimeShort(String timePattern, String datePattern) {
    return datePattern + ", " + timePattern;
  }

  @Override
  public String[] erasFull() {
    return new String[] {
        "vör Christus",
        "na Christus"
    };
  }

  @Override
  public String[] erasShort() {
    return new String[] {
        "v.Chr.",
        "n.Chr."
    };
  }

  @Override
  public String[] monthsFull() {
    return new String[] {
        "Januaar",
        "Februaar",
        "März",
        "April",
        "Mai",
        "Juni",
        "Juli",
        "August",
        "September",
        "Oktover",
        "November",
        "Dezember"
    };
  }

  @Override
  public String[] monthsShort() {
    return new String[] {
        "Jan.",
        "Feb.",
        "März",
        "Apr.",
        "Mai",
        "Juni",
        "Juli",
        "Aug.",
        "Sep.",
        "Okt.",
        "Nov.",
        "Dez."
    };
  }

  @Override
  public String[] quartersFull() {
    return new String[] {
        "1. Quartaal",
        "2. Quartaal",
        "3. Quartaal",
        "4. Quartaal"
    };
  }

  @Override
  public String[] quartersShort() {
    return new String[] {
        "Q.1",
        "Q.2",
        "Q.3",
        "Q.4"
    };
  }

  @Override
  public String timeFormatFull() {
    return "'Klock' H.mm:ss (zzzz)";
  }

  @Override
  public String timeFormatLong() {
    return "'Klock' H.mm:ss (z)";
  }

  @Override
  public String timeFormatMedium() {
    return "'Klock' H.mm:ss";
  }

  @Override
  public String timeFormatShort() {
    return "'Kl'. H.mm";
  }

  @Override
  public String[] weekdaysFull() {
    return new String[] {
        "Sünndag",
        "Maandag",
        "Dingsdag",
        "Middeweken",
        "Dunnersdag",
        "Freedag",
        "Sünnavend"
    };
  }

  @Override
  public String[] weekdaysNarrow() {
    return new String[] {
        "S",
        "M",
        "D",
        "M",
        "D",
        "F",
        "S"
    };
  }

  @Override
  public String[] weekdaysShort() {
    return new String[] {
        "Sü.",
        "Ma.",
        "Di.",
        "Mi.",
        "Du.",
        "Fr.",
        "Sa."
    };
  }
}
