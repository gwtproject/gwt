package com.google.gwt.user.client.ui;

import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.i18n.shared.HasDirectionEstimator;

public interface IsCheckBox extends IsButtonBase, HasName, HasValue<Boolean>, HasWordWrap,
    HasDirectionalSafeHtml, HasDirectionEstimator, IsEditor<LeafValueEditor<Boolean>> {

}
