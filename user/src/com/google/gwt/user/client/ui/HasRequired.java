package com.google.gwt.user.client.ui;

/**
 * A widget that implements this interface can display a user message to
 * indicate whether this element is required to fill out or not.
 */
public interface HasRequired {
  /**
   * required property name.
   */
  static final String REQUIRED_PROPERTY_NAME = "required";
  
  /**
   * Determines whether or not this object is required.
   *
   * <p>This option will make it mandatory filling a field and block form validation
   * if one of the fields (concerned by this attribute) has not been informed.</p>
   *
   * @return <code>true</code> if the object is required.
   */
  boolean isRequired();

  /**
   * Sets the HTML5 required value.
   *
   * <p>When present, it specifies that an input field must be filled out before submitting the form.</p>
   *
   * <p>Note: The required attribute works with the following input types: text, search, url, tel, email,
   * password, date pickers, number, checkbox, radio, and file.</p>
   *
   * <p><strong>The exact behavior of the placeholder might differ between browsers.</strong></p>
   *
   * @param required <code>true</code> if the listBox or text field must be filled.
   */
  void setRequired(boolean required);
}