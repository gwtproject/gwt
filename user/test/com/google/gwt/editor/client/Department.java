/*
 * Copyright 2012 Google Inc.
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
package com.google.gwt.editor.client;

/**
 * Simple class used in
 * {@link SimpleBeanEditorTest#testEditorWithGenericSubEditors()}.
 */
public class Department {
  Manager manager = new Manager();
  Intern intern = new Intern();

  public Manager getManager() {
    return manager;
  }

  public Intern getIntern() {
    return intern;
  }

  public void setManager(Manager manager) {
    this.manager = manager;
  }

  public void setIntern(Intern intern) {
    this.intern = intern;
  }
}