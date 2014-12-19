/*
 * Copyright 2014 Google Inc.
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
 * A widget that implements this interface can display a user message to
 * indicate whether this element is required to fill out or not.
 */
public interface HasRequired {
  /**
   * required property name.
   */
  String REQUIRED_PROPERTY_NAME = "required";

  	/**
	 * Determines whether or not this object is required.
	 * 
	 * <p>
	 * This option will make it mandatory filling a field and block form
	 * validation if one of the fields (concerned by this attribute) has not
	 * been informed.
	 * 
	 * @return <code>true</code> if the object is required.
	 */
  boolean isRequired();

  	/**
	 * Sets the HTML5 required value.
	 * 
	 * <p>
	 * When present, it specifies that an input field must be filled out before
	 * submitting the form.
	 * 
	 * <p>
	 * Note: The required attribute works with the following input types: text,
	 * search, url, tel, email, password, date pickers, number, checkbox, radio,
	 * and file.
	 * 
	 * <p>
	 * <strong>The exact behavior of the placeholder might differ between
	 * browsers.</strong>
	 * 
	 * @param required
	 *            <code>true</code> if the listBox or text field must be filled.
	 */
  void setRequired(boolean required);
}