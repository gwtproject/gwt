/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.user.client.ui;

import java.util.Iterator;

/**
 * A {@link Composite} that wraps a {@link HasWidgets} widget.
 * <p>
 * This class is especially useful with {@code UIBinder} as it lets this
 * composite to be used as parent in the templates.
 *
 * @param <T> type of the widget wrapped
 */
public class PanelComposite<T extends Widget & HasWidgets> extends Composite<T>
    implements HasWidgets.ForIsWidget {

  @Override
  public void add(Widget w) {
    getDelegate().add(w);
  }

  @Override
  public void add(IsWidget w) {
    T widget = getDelegate();
    if (widget instanceof HasWidgets.ForIsWidget) {
      // Do not alter the behavior if add(IsWidget) implemented differently:
      ((HasWidgets.ForIsWidget) widget).add(w);
    } else {
      widget.add(asWidgetOrNull(w));
    }
  }

  @Override
  public boolean remove(Widget w) {
    return getDelegate().remove(w);
  }

  @Override
  public boolean remove(IsWidget w) {
    T widget = getDelegate();
    if (widget instanceof HasWidgets.ForIsWidget) {
      // Do not alter the behavior if remove(IsWidget) implemented differently:
      return ((HasWidgets.ForIsWidget) widget).remove(w);
    } else {
      return widget.remove(asWidgetOrNull(w));
    }
  }

  @Override
  public void clear() {
    getDelegate().clear();
  }

  @Override
  public Iterator<Widget> iterator() {
    return getDelegate().iterator();
  }
}
