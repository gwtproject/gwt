/*
 * Copyright 2011 Google Inc.
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
package com.google.gwt.editor.client.testing;

import com.google.gwt.editor.client.CompositeEditor;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorContext;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.EditorVisitor;
import com.google.gwt.editor.client.HasEditorDelegate;
import com.google.gwt.editor.client.HasEditorErrors;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.ValueAwareEditor;

/**
 * A no-op implementation of EditorContext for testing.
 * 
 * @param <T> the type of data not being edited
 */
public class FakeEditorContext<T> implements EditorContext<T> {

  /**
   * Returns {@code null}.
   */
  @Override
  public CompositeEditor<T, ?, ?> asCompositeEditor() {
    return null;
  }

  /**
   * Returns {@code null}.
   */
  @Override
  public HasEditorDelegate<T> asHasEditorDelegate() {
    return null;
  }

  /**
   * Returns {@code null}.
   */
  @Override
  public HasEditorErrors<T> asHasEditorErrors() {
    return null;
  }

  /**
   * Returns {@code null}.
   */
  @Override
  public LeafValueEditor<T> asLeafValueEditor() {
    return null;
  }

  /**
   * Returns {@code null}.
   */
  @Override
  public ValueAwareEditor<T> asValueAwareEditor() {
    return null;
  }

  /**
   * Returns {@code false}.
   */
  @Override
  public boolean canSetInModel() {
    return false;
  }

  /**
   * Returns {@code value} via an unchecked generic cast.
   */
  @Override
  @SuppressWarnings("unchecked")
  public T checkAssignment(Object value) {
    return (T) value;
  }

  /**
   * Returns {@link EditorContext#ROOT_PATH}.
   */
  @Override
  public String getAbsolutePath() {
    return ROOT_PATH;
  }

  /**
   * Returns {@code null}.
   */
  @Override
  public Class<T> getEditedType() {
    return null;
  }

  /**
   * Returns {@code null}.
   */
  @Override
  public Editor<T> getEditor() {
    return null;
  }

  /**
   * Returns {@code null}.
   */
  @Override
  public EditorDelegate<T> getEditorDelegate() {
    return null;
  }

  /**
   * Returns {@code null}.
   */
  @Override
  public T getFromModel() {
    return null;
  }

  /**
   * A no-op.
   */
  @Override
  public void setInModel(T data) {
  }

  /**
   * No-op.
   */
  @Override
  public void traverseSyntheticCompositeEditor(EditorVisitor visitor) {
  }
}
