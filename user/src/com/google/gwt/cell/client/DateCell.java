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
package com.google.gwt.cell.client;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

import java.util.Date;

/**
 * A {@link Cell} used to render {@link Date}s.
 */
public class DateCell extends AbstractCell<Date> {

  private final DateTimeFormat format;

  private final SafeHtmlRenderer<String> renderer;

  private final TimeZone timeZone;

  private final com.google.gwt.i18n.shared.DateTimeFormat sharedFormat;

  private final com.google.gwt.i18n.shared.TimeZone timeZoneShared;

  /**
   * Construct a new {@link DateCell} using the format
   * {@link PredefinedFormat#DATE_FULL} and a {@link SimpleSafeHtmlRenderer}.
   */
  public DateCell() {
    this(com.google.gwt.i18n.shared.DateTimeFormat.getFormat(
        com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat.DATE_FULL),
        SimpleSafeHtmlRenderer.getInstance(), null);
  }

  /**
   * Construct a new {@link DateCell} using the format
   * {@link PredefinedFormat#DATE_FULL} and a {@link SimpleSafeHtmlRenderer}.
   *
   * @param renderer a non-null {@link SafeHtmlRenderer} used to render the
   *          formatted date as HTML
   */
  public DateCell(SafeHtmlRenderer<String> renderer) {
    this(com.google.gwt.i18n.shared.DateTimeFormat.getFormat(
      com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat.DATE_FULL),
      renderer, null);
  }

  /**
   * Construct a new {@link DateCell} using the specified format and a
   * {@link SimpleSafeHtmlRenderer}.
   *
   * @param format the {@link DateTimeFormat} used to render the date
   *
   * @deprecated use
   * {@link DateCell#DateCell(com.google.gwt.i18n.shared.DateTimeFormat)}
   */
  @Deprecated
  public DateCell(DateTimeFormat format) {
    this(format, SimpleSafeHtmlRenderer.getInstance(), null);
  }

  /**
   * Construct a new {@link DateCell} using the specified format and the given
   * {@link SafeHtmlRenderer}.
   *
   * @param format the {@link DateTimeFormat} used to render the date
   * @param renderer a non-null {@link SafeHtmlRenderer} used to render the
   *          formatted date
   *
   * @deprecated use
   * {@link DateCell#DateCell(com.google.gwt.i18n.shared.DateTimeFormat, SafeHtmlRenderer)}
   */
  @Deprecated
  public DateCell(DateTimeFormat format, SafeHtmlRenderer<String> renderer) {
    this(format, renderer, null);
  }

  /**
   * Construct a new {@link DateCell} using the specified format and time zone.
   *
   * @param format the {@link DateTimeFormat} used to render the date
   * @param timeZone the {@link TimeZone} used to render the date, or null to
   *          use the default behavior for the local time zone and the rendered
   *          date. See {@link DateTimeFormat#format(Date)} and
   *          {@link Date#getTimezoneOffset()}
   *
   * @deprecated use
   * {@link DateCell#DateCell(com.google.gwt.i18n.shared.DateTimeFormat, com.google.gwt.i18n.shared.TimeZone)}
   */
  @Deprecated
  public DateCell(DateTimeFormat format, TimeZone timeZone) {
    this(format, SimpleSafeHtmlRenderer.getInstance(), timeZone);
  }

  /**
   * Construct a new {@link DateCell} using the specified format, the given
   * {@link SafeHtmlRenderer}, and the specified time zone.
   *
   * @param format the {@link DateTimeFormat} used to render the date
   * @param renderer a non-null {@link SafeHtmlRenderer} used to render the
   *          formatted date
   * @param timeZone the {@link TimeZone} used to render the date, or null to
   *          use the default behavior for the local time zone and the rendered
   *          date. See {@link DateTimeFormat#format(Date)} and
   *          {@link Date#getTimezoneOffset()}
   *
   * @deprecated use
   * {@link DateCell#DateCell(com.google.gwt.i18n.shared.DateTimeFormat, SafeHtmlRenderer, com.google.gwt.i18n.shared.TimeZone)}
   */
  @Deprecated
  public DateCell(DateTimeFormat format, SafeHtmlRenderer<String> renderer,
      TimeZone timeZone) {
    if (format == null) {
      throw new IllegalArgumentException("format == null");
    }
    if (renderer == null) {
      throw new IllegalArgumentException("renderer == null");
    }
    this.format = format;
    this.sharedFormat = null;
    this.renderer = renderer;
    this.timeZone = timeZone;
    this.timeZoneShared = null;
  }

  /**
   * Construct a new {@link DateCell} using the specified format and a
   * {@link SimpleSafeHtmlRenderer}.
   *
   * @param format the {@link com.google.gwt.i18n.shared.DateTimeFormat}
   *          used to render the date
   */
  public DateCell(com.google.gwt.i18n.shared.DateTimeFormat format) {
    this(format, SimpleSafeHtmlRenderer.getInstance(), null);
  }

  /**
   * Construct a new {@link DateCell} using the specified format and the given
   * {@link SafeHtmlRenderer}.
   *
   * @param format the {@link com.google.gwt.i18n.shared.DateTimeFormat}
   *          used to render the date
   * @param renderer a non-null {@link SafeHtmlRenderer} used to render the
   *          formatted date
   */
  public DateCell(com.google.gwt.i18n.shared.DateTimeFormat format, SafeHtmlRenderer<String> renderer) {
    this(format, renderer, null);
  }

  /**
   * Construct a new {@link DateCell} using the specified format and time zone.
   *
   * @param format the {@link com.google.gwt.i18n.shared.DateTimeFormat}
   *          used to render the date
   * @param timeZone the {@link com.google.gwt.i18n.shared.TimeZone}
   *          used to render the date, or null to
   *          use the default behavior for the local time zone and the rendered
   *          date. See {@link DateTimeFormat#format(Date)} and
   *          {@link Date#getTimezoneOffset()}
   */
  public DateCell(com.google.gwt.i18n.shared.DateTimeFormat format,
      com.google.gwt.i18n.shared.TimeZone timeZone) {
    this(format, SimpleSafeHtmlRenderer.getInstance(), timeZone);
  }

  /**
   * Construct a new {@link DateCell} using the specified format, the given
   * {@link SafeHtmlRenderer}, and the specified time zone.
   *
   * @param format the {@link com.google.gwt.i18n.shared.DateTimeFormat}
   *          used to render the date
   * @param renderer a non-null {@link SafeHtmlRenderer} used to render the
   *          formatted date
   * @param timeZone the {@link com.google.gwt.i18n.shared.TimeZone}
   *          used to render the date, or null to
   *          use the default behavior for the local time zone and the rendered
   *          date. See {@link DateTimeFormat#format(Date)} and
   *          {@link Date#getTimezoneOffset()}
   */
  public DateCell(com.google.gwt.i18n.shared.DateTimeFormat format, SafeHtmlRenderer<String> renderer,
      com.google.gwt.i18n.shared.TimeZone timeZone) {
    if (format == null) {
      throw new IllegalArgumentException("format == null");
    }
    if (renderer == null) {
      throw new IllegalArgumentException("renderer == null");
    }
    this.format = null;
    this.sharedFormat = format;
    this.renderer = renderer;
    this.timeZone = null;
    this.timeZoneShared = timeZone;
  }

  @Override
  public void render(Context context, Date value, SafeHtmlBuilder sb) {
    if (value != null) {
      if (sharedFormat != null) {
        sb.append(renderer.render(sharedFormat.format(value, timeZoneShared)));
      } else {
        sb.append(renderer.render(format.format(value, timeZone)));
      }
    }
  }
}
