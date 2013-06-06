package com.google.gwt.user.client.ui;

import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.logical.shared.HasHighlightHandlers;
import com.google.gwt.event.logical.shared.HasShowRangeHandlers;

import java.util.Date;

public interface IsDatePicker extends IsWidget2, HasHighlightHandlers<Date>,
    HasShowRangeHandlers<Date>, HasValue<Date>, IsEditor<LeafValueEditor<Date>> {

}
