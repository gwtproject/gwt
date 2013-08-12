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

package com.google.gwt.user.datepicker.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;

import java.util.Date;

/**
 * A simple {@link MonthSelector} used for the default date picker. Not
 * extensible as we wish to evolve it freely over time.
 */

public final class DefaultMonthSelector extends MonthSelector {

  private PushButton monthBackwards;
  private PushButton monthForwards;
  private FlexTable grid;
  private PushButton yearBackwards;
  private PushButton yearForwards;
  private ListBox monthSelect;
  private ListBox yearSelect;
  private int monthColumn;
  private int yearColumn;

  /**
   * Constructor.
   */
  public DefaultMonthSelector() {
  }

  /**
   * Returns the button for moving to the previous month.
   */
  public Element getBackwardButtonElement() {
    return monthBackwards.getElement();
  }

  /**
   * Returns the button for moving to the next month.
   */
  public Element getForwardButtonElement() {
    return monthForwards.getElement();
  }

  /**
   * Returns the button for moving to the previous year.
   */
  public Element getYearBackwardButtonElement() {
    return yearBackwards.getElement();
  }

  /**
   * Returns the button for moving to the next year.
   */
  public Element getYearForwardButtonElement() {
    return yearForwards.getElement();
  }

  /**
   * Returns the ListBox for selecting the month
   */
  public ListBox getMonthSelectListBox() {
    return monthSelect;
  }

  /**
   * Returns the ListBox for selecting the year
   */
  public ListBox getYearSelectListBox() {
    return yearSelect;
  }

  @Override
  protected void refresh() {
    if (isDatePickerConfigChanged()) {
      // if the config has changed since the last refresh, rebuild the grid
      setupGrid();
    }

    Date current = getModel().getCurrentMonth();

    setMonth(current);

    setYear(current);
  }

  @Override
  protected void setup() {
    // previous, next buttons
    monthBackwards = createNavigationButton("&lsaquo;", -1, css().previousButton());
    monthForwards = createNavigationButton("&rsaquo;", 1, css().nextButton());
    yearBackwards = createNavigationButton("&laquo;", -12, css().previousYearButton());
    yearForwards = createNavigationButton("&raquo;", 12, css().nextYearButton());

    // month and year selector
    monthSelect = createMonthSelect();
    yearSelect = createYearSelect();

    // Set up grid.
    grid = new FlexTable();
    grid.setStyleName(css().monthSelector());

    setupGrid();

    initWidget(grid);
  }

  private PushButton createNavigationButton(String label, final int noOfMonth, String styleName) {
    PushButton button = new PushButton();

    button.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        addMonths(noOfMonth);
      }
    });

    button.getUpFace().setHTML(label);
    button.setStyleName(styleName);

    return button;
  }

  private ListBox createMonthSelect() {
    final ListBox monthListBox = new ListBox();

    for (int i = 0; i < CalendarModel.MONTHS_IN_YEAR; i++) {
      monthListBox.addItem(getModel().formatMonth(i));
    }

    monthListBox.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(ChangeEvent event) {
        int previousMonth = getModel().getCurrentMonth().getMonth();
        int newMonth = monthListBox.getSelectedIndex();
        int delta = newMonth - previousMonth;

        addMonths(delta);
      }
    });

    return monthListBox;
  }

  private ListBox createYearSelect() {
    final ListBox yearListBox = new ListBox();

    yearListBox.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(ChangeEvent event) {
        int lowerBound = Math.max(getDatePicker().getYearsBeforeCurrentInDropdown(), 0);
        int delta = yearListBox.getSelectedIndex() - lowerBound;
        addMonths(delta * CalendarModel.MONTHS_IN_YEAR);
      }
    });

    return yearListBox;
  }


  private boolean isDatePickerConfigChanged() {
    boolean isMonthCurrentlySelectable = monthSelect.getParent() != null;
    boolean isYearCurrentlySelectable = yearSelect.getParent() != null;
    boolean isYearNavigationCurrentlyEnabled = yearBackwards.getParent() != null;

    return getDatePicker().isMonthDropdownVisible() != isMonthCurrentlySelectable ||
        getDatePicker().isYearDropdownVisible() != isYearCurrentlySelectable ||
        getDatePicker().isYearArrowsVisible() != isYearNavigationCurrentlyEnabled;
  }

  private void setMonth(Date date) {
    if (getDatePicker().isMonthDropdownVisible()) {
      int month = date.getMonth();
      monthSelect.setSelectedIndex(month);
    } else if ( getDatePicker().isYearDropdownVisible()) {
      grid.setText(0, monthColumn, getModel().formatCurrentMonth());
    } else {
      grid.setText(0, monthColumn, getModel().formatCurrentMonthAndYear());
    }
  }

  private void setYear(Date date) {
    if (getDatePicker().isYearDropdownVisible()) {
      yearSelect.clear();

      int year = date.getYear();

      Date newDate = new Date();

      int lowerBound = Math.max(getDatePicker().getYearsBeforeCurrentInDropdown(), 0);
      int upperBound = Math.max(getDatePicker().getYearsAfterCurrentInDropdown(), 0);

      for (int i = year - lowerBound; i <= year + upperBound; i++) {
        newDate.setYear(i);
        yearSelect.addItem(getModel().getYearFormatter().format(newDate));
      }

      yearSelect.setSelectedIndex(lowerBound);

    } else if (getDatePicker().isMonthDropdownVisible()) {
      grid.setText(0, yearColumn, getModel().formatCurrentYear());
    }
  }

  private void setupGrid() {
    grid.clear();
    FlexCellFormatter formatter = (FlexCellFormatter) grid.getCellFormatter();
    boolean monthSelectable = getDatePicker().isMonthDropdownVisible();
    boolean yearSelectable = getDatePicker().isYearDropdownVisible();
    boolean yearNavigationEnabled = getDatePicker().isYearArrowsVisible();
    int column = 0;

    if (yearNavigationEnabled) {
      grid.setWidget(0, column, yearBackwards);
      formatter.setWidth(0, column++, "1");
    }

    grid.setWidget(0, column, monthBackwards);
    formatter.setWidth(0, column++, "1");

    if (!monthSelectable && !yearSelectable) {
      // for backward compatibility
      monthColumn = column;
      formatter.setStyleName(0, column, css().month());
      formatter.setWidth(0, column++, "100%");
    } else if (getModel().isMonthBeforeYear()) {
      setupMonth(formatter, column++, monthSelectable);
      setupYear(formatter, column++, yearSelectable);
    } else {
      setupYear(formatter, column++, yearSelectable);
      setupMonth(formatter, column++, monthSelectable);
    }

    grid.setWidget(0, column, monthForwards);
    formatter.setWidth(0, column++, "1");

    if (yearNavigationEnabled) {
      grid.setWidget(0, column, yearForwards);
      formatter.setWidth(0, column, "1");
    }
  }

  private void setupMonth(FlexCellFormatter formatter, int column, boolean monthSelectable) {
    if (monthSelectable) {
      grid.setWidget(0, column, monthSelect);
    }
    formatter.setStyleName(0, column, css().month());
    formatter.setWidth(0, column, "50%");
    monthColumn = column;
  }

  private void setupYear(FlexCellFormatter formatter, int column, boolean yearSelectable) {
    if (yearSelectable) {
      grid.setWidget(0, column, yearSelect);
    }
    formatter.setStyleName(0, column, css().year());
    formatter.setWidth(0, column, "50%");
    yearColumn = column;
  }
}
