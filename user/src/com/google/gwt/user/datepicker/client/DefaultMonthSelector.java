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
 * A simple {@link MonthSelector} used for the default date picker. It allows to select months and
 * years but we don't change the name for backward compatibility. Not extensible as we wish to
 * evolve it freely over time.
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

    setDate(getModel().getCurrentMonth());
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
        int beforeYear = getNbrYearTodisplayBeforeCurrent();
        int delta = yearListBox.getSelectedIndex() - beforeYear;
        addMonths(delta * CalendarModel.MONTHS_IN_YEAR);
      }
    });

    return yearListBox;
  }


  private boolean isDatePickerConfigChanged() {
    boolean isMonthCurrentlySelectable = monthSelect.getParent() != null;
    boolean isYearNavigationCurrentlyEnabled = yearBackwards.getParent() != null;

    return getDatePicker().isYearAndMonthDropdownVisible() != isMonthCurrentlySelectable ||
        getDatePicker().isYearArrowsVisible() != isYearNavigationCurrentlyEnabled;
  }

  private void setDate(Date date) {
    if (getDatePicker().isYearAndMonthDropdownVisible()) {
      // setup months dropdown
      int month = date.getMonth();
      monthSelect.setSelectedIndex(month);

      // setup years dropdown
      yearSelect.clear();

      int year = date.getYear();
      Date newDate = new Date();
      int beforeYear = getNbrYearTodisplayBeforeCurrent();
      int afterYear = getNbrYearTodisplayAfterCurrent();

      for (int i = year - beforeYear; i <= year + afterYear; i++) {
        newDate.setYear(i);
        yearSelect.addItem(getModel().getYearFormatter().format(newDate));
      }

      yearSelect.setSelectedIndex(beforeYear);
    } else {
      grid.setText(0, monthColumn, getModel().formatCurrentMonthAndYear());
    }
  }

  private int getNbrYearTodisplayBeforeCurrent() {
    return (getDatePicker().getVisibleYearCount() -  1) / 2;
  }

  private int getNbrYearTodisplayAfterCurrent() {
    return getDatePicker().getVisibleYearCount() / 2;
  }

  private void setupGrid() {
    grid.clear();
    FlexCellFormatter formatter = (FlexCellFormatter) grid.getCellFormatter();
    boolean yearAndMonthSelectable = getDatePicker().isYearAndMonthDropdownVisible();
    boolean yearNavigationEnabled = getDatePicker().isYearArrowsVisible();
    int column = 0;

    if (yearNavigationEnabled) {
      grid.setWidget(0, column, yearBackwards);
      formatter.setWidth(0, column++, "1");
    }

    grid.setWidget(0, column, monthBackwards);
    formatter.setWidth(0, column++, "1");

    if (!yearAndMonthSelectable) {
      // for backward compatibility
      monthColumn = column;
      formatter.setStyleName(0, column, css().month());
      formatter.setWidth(0, column++, "100%");
    } else if (getModel().isMonthBeforeYear()) {
      setupMonth(formatter, column++);
      setupYear(formatter, column++);
    } else {
      setupYear(formatter, column++);
      setupMonth(formatter, column++);
    }

    grid.setWidget(0, column, monthForwards);
    formatter.setWidth(0, column++, "1");

    if (yearNavigationEnabled) {
      grid.setWidget(0, column, yearForwards);
      formatter.setWidth(0, column, "1");
    }
  }

  private void setupMonth(FlexCellFormatter formatter, int column) {
    grid.setWidget(0, column, monthSelect);
    formatter.setStyleName(0, column, css().month());
    formatter.setWidth(0, column, "50%");
    monthColumn = column;
  }

  private void setupYear(FlexCellFormatter formatter, int column) {
    grid.setWidget(0, column, yearSelect);
    formatter.setStyleName(0, column, css().year());
    formatter.setWidth(0, column, "50%");
    yearColumn = column;
  }
}
