package com.google.gwt.resources.client.impl;

import com.google.gwt.resources.client.CustomImageResource;
import com.google.gwt.safehtml.shared.SafeUri;

public class CustomImageResourcePrototype extends ImageResourcePrototype implements
    CustomImageResource {

  public CustomImageResourcePrototype(String name, SafeUri url) {
    super(name, url, 0, 0, 0, 0, false, false);
  }
}
