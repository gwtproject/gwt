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

  @Override
  protected void refresh() {
    if (datePickerConfigChanged()) {
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
    setupMonthSelect();
    setupYearSelect();

    // Set up grid.
    grid = new FlexTable();
    grid.setStyleName(css().monthSelector());

    setupGrid();

    initWidget(grid);
  }

  private PushButton createNavigationButton(String label, final int nbrOfMonth, String cssClass) {
    PushButton button = new PushButton();

    button.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        addMonths(nbrOfMonth);
      }
    });

    button.getUpFace().setHTML(label);
    button.setStyleName(cssClass);

    return button;
  }

  private boolean datePickerConfigChanged() {
    boolean isMonthCurrentlySelectable = monthSelect.getParent() != null;
    boolean isYearCurrentlySelectable = yearSelect.getParent() != null;
    boolean isYearNavigationCurrentlyEnabled = yearBackwards.getParent() != null;

    return getDatePicker().isMonthSelectable() != isMonthCurrentlySelectable ||
        getDatePicker().isYearSelectable() != isYearCurrentlySelectable ||
        getDatePicker().isYearNavigationEnabled() != isYearNavigationCurrentlyEnabled;
  }

  private void setMonth(Date date) {
    int monthColumn = getDatePicker().isYearNavigationEnabled() ? 2 : 1;

    if (getDatePicker().isMonthSelectable()) {
      int month = date.getMonth();
      monthSelect.setSelectedIndex(month);
    } else if (getDatePicker().isYearSelectable()) {
      grid.setText(0, monthColumn, getModel().formatCurrentMonth());
    } else {
      grid.setText(0, monthColumn, getModel().formatCurrentMonthAndYear());
    }
  }

  private void setupGrid() {
    FlexCellFormatter formatter = (FlexCellFormatter) grid.getCellFormatter();
    boolean monthSelectable = getDatePicker().isMonthSelectable();
    boolean yearSelectable = getDatePicker().isYearSelectable();
    boolean yearNavigationEnabled = getDatePicker().isYearNavigationEnabled();
    int column = 0;

    if (yearNavigationEnabled) {
      grid.setWidget(0, column, yearBackwards);
      formatter.setWidth(0, column++, "1");
    }

    grid.setWidget(0, column, monthBackwards);
    formatter.setWidth(0, column++, "1");

    if (monthSelectable) {
      grid.setWidget(0, column, monthSelect);
      formatter.setStyleName(0, column, css().month());
      formatter.setWidth(0, column++, "50%");
    } else if (yearSelectable) {
      formatter.setStyleName(0, column, css().month());
      formatter.setWidth(0, column++, "50%");
    }

    if (yearSelectable) {
      grid.setWidget(0, column, yearSelect);
      formatter.setStyleName(0, column, css().year());
      formatter.setWidth(0, column++, "50%");
    } else if (monthSelectable) {
      formatter.setStyleName(0, column, css().year());
      formatter.setWidth(0, column++, "50%");
    }

    // for backward compatibility
    if (!monthSelectable && !yearSelectable) {
      formatter.setStyleName(0, column, css().month());
      formatter.setWidth(0, column++, "100%");
    }

    grid.setWidget(0, column, monthForwards);
    formatter.setWidth(0, column++, "1");

    if (yearNavigationEnabled) {
      grid.setWidget(0, column, yearForwards);
      formatter.setWidth(0, column, "1");
    }
  }

  private void setupMonthSelect() {
    monthSelect = new ListBox();

    for (int i = 0; i < 12; i++) {
      monthSelect.addItem(getModel().formatMonth(i));
    }

    monthSelect.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(ChangeEvent event) {
        int previousMonth = getModel().getCurrentMonth().getMonth();
        int newMonth = monthSelect.getSelectedIndex();
        int delta = newMonth - previousMonth;

        addMonths(delta);
      }
    });
  }

  private void setupYearSelect() {
    yearSelect = new ListBox();

    yearSelect.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(ChangeEvent event) {
        int lowerBound = Math.max(getDatePicker().getNumberOfMonthToDisplayBeforeCurrentYear(), 0);
        int delta = yearSelect.getSelectedIndex() - lowerBound;
        addMonths(delta * 12);
      }
    });
  }

  private void setYear(Date date) {
    if (getDatePicker().isYearSelectable()) {
      yearSelect.clear();

      int year = date.getYear();

      Date d = new Date();

      int lowerBound = Math.max(getDatePicker().getNumberOfMonthToDisplayBeforeCurrentYear(), 0);
      int upperBound = Math.max(getDatePicker().getNumberOfMonthToDisplayAfterCurrentYear(), 0);

      for (int i = year - lowerBound; i <= year + upperBound; i++) {
        d.setYear(i);
        yearSelect.addItem(getModel().getYearFormatter().format(d));
      }

      yearSelect.setSelectedIndex(lowerBound);

    } else if (getDatePicker().isMonthSelectable()) {
      int yearColumn = getDatePicker().isYearNavigationEnabled() ? 3 : 2;
      grid.setText(0, yearColumn, getModel().formatCurrentYear());
    }
  }
}
