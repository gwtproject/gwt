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

/**
 * A {@link TypedComposite} implementation that implements {@link RequiresResize} and
 * automatically delegates that interface's methods to its wrapped widget, which
 * must itself implement {@link RequiresResize}.
 * 
 * @param <T> type of the widget wrapped
 */
public abstract class TypedResizeComposite<T extends Widget & RequiresResize>
    extends TypedComposite<T> implements RequiresResize {

  @Override
  public void onResize() {
    getCheckedWidget().onResize();
  }
}
