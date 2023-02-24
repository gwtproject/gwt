/*
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
package java.time;

import java.io.Serializable;
import java.util.Date;

/**
 * Skeletal implemention to be able to use in DTOs.
 */
public class LocalDate implements Serializable {

  /**
   * Serialization version.
   */
  private static final long serialVersionUID = 2942565459149668126L;

  private final int year;
  private final int month;
  private final int day;
  
  private LocalDate(int year, int month, int day) {
    this.year = year;
    this.month = month;
    this.day = day;
  }
  
  @Override
  public boolean equals(Object obj) {
    boolean eq = obj == this;
    if (eq == false && obj instanceof LocalDate) {
      LocalDate d = (LocalDate) obj;
      eq = this.year == d.year && this.month == d.month && this.day == d.day;
    }
    return eq;
  }

  @Override
  public int hashCode() {
    return day + month + year;
  }

  public static LocalDate parse(String value) {
    return null;
  }
  
  public static LocalDate of(int year, int month, int dayOfMonth) {
    return new LocalDate(year, month, dayOfMonth);
  }

  public static LocalDate now() {
    Date date = new Date();
    LocalDate ld = new LocalDate(date.getYear() + 1900
      , date.getMonth() + 1
      , date.getDate());
    return ld;
  }

  public LocalDate minusDays(long days) {
    Date d = toDate();
    d = new Date(d.getTime() - days * 86400000);
    return fromDate(d);
  }
  
  public static LocalDate fromDate(Date date) {
    LocalDate ld = new LocalDate(date.getYear() + 1900
      , date.getMonth() + 1
      , date.getDate());
    return ld;
  }
  
  public Date toDate() {
    Date d = new Date(year - 1900, month - 1, day, 0, 0, 0);
    return d;
  }
  
  public int getYear() {
    return year;
  }

  public int getMonth() {
    return month;
  }

  public int getDayOfMonth() {
    return day;
  }
}
