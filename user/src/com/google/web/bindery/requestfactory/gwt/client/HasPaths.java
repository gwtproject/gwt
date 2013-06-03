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
package com.google.web.bindery.requestfactory.gwt.client;

import com.google.gwt.editor.client.Editor;

/**
 * Editors used with {@link RequestFactoryEditorDriver} that implement this interface will provide
 * paths to {@link RequestFactoryEditorDriver#getPaths()} in addition to those collected from their
 * sub-editors.
 * 
 * @param <T> the type of data being edited
 */
public interface HasPaths<T> extends Editor<T> {

  /**
   * Called by {@link RequestFactoryEditorDriver} when collecting paths for
   * {@link RequestFactoryEditorDriver#getPaths()}.
   */
  String[] getPaths();
}
