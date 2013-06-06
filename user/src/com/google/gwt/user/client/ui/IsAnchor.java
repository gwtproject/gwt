package com.google.gwt.user.client.ui;


import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.shared.HasDirectionEstimator;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasWordWrap;

public interface IsAnchor extends IsFocusWidget, HasHorizontalAlignment, HasName, HasHTML, HasWordWrap, HasDirection, HasDirectionEstimator, HasDirectionalSafeHtml {

  String getHref();

  void setHref(String href);

  String getTarget();

  void setTarget(String target);

}
