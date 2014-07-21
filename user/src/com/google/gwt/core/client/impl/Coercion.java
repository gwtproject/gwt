package com.google.gwt.core.client.impl;

/**
 * Private implementation class for GWT. This API should not be
 * considered public or stable.
 */
public final class Coercion {

  /**
   * Coerce to 32 bits.
   * Trick related to JS and lack of integer rollover.
   */
  public static native int coerce(int value) /*-{
    return value | 0;
  }-*/;

  /**
   * Coerce to primitive string.
   */
  public static native int coerce(String s) /*-{
    return String(s);
  }-*/;

  private Coercion() { }
}
