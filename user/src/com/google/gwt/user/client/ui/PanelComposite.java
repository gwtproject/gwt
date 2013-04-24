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
 * A {@link Composite} that wraps {@link HasWidgets} widgets.
 * <p>
 * This class is especially useful with {@code UIBinder} as it lets this
 * composite to be used as parent in the templates.
 *
 * @param <T> type of the widget wrapped
 */
public class PanelComposite<T extends Widget & HasWidgets> extends TypedComposite<T>
    implements HasWidgets.ForIsWidget {

  @Override
  public void add(Widget w) {
    getCheckedWidget().add(w);
  }

  @Override
  public void add(IsWidget w) {
    this.add(asWidgetOrNull(w));
  }

  @Override
  public boolean remove(Widget w) {
    return getCheckedWidget().remove(w);
  }

  @Override
  public boolean remove(IsWidget w) {
    return this.remove(asWidgetOrNull(w));
  }

  @Override
  public void clear() {
    getCheckedWidget().clear();
  }

  @Override
  public Iterator<Widget> iterator() {
    return getCheckedWidget().iterator();
  }
}
