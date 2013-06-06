package com.google.gwt.user.client.ui;

import java.util.Date;

import com.google.gwt.event.logical.shared.HasHighlightHandlers;
import com.google.gwt.event.logical.shared.HasShowRangeHandlers;
import com.google.gwt.user.client.ui.HasValue;

public interface IsDatePicker extends IsWidget2, HasHighlightHandlers<Date>, HasShowRangeHandlers<Date>, HasValue<Date> {
}
