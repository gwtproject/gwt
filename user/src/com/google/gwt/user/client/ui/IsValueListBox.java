package com.google.gwt.user.client.ui;

import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.adapters.TakesValueEditor;

public interface IsValueListBox<T> extends IsWidget2, Focusable, HasConstrainedValue<T>,
    IsEditor<TakesValueEditor<T>> {

}
