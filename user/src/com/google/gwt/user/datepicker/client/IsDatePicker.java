package com.google.gwt.user.datepicker.client;

import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.logical.shared.HasHighlightHandlers;
import com.google.gwt.event.logical.shared.HasShowRangeHandlers;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget2;

import java.util.Date;

public interface IsDatePicker extends IsWidget2, HasHighlightHandlers<Date>,
    HasShowRangeHandlers<Date>, HasValue<Date>, IsEditor<LeafValueEditor<Date>> {

}
