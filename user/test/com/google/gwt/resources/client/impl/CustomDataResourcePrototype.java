package com.google.gwt.resources.client.impl;

import com.google.gwt.resources.client.CustomDataResource;
import com.google.gwt.safehtml.shared.SafeUri;

public class CustomDataResourcePrototype extends DataResourcePrototype implements CustomDataResource {
  public CustomDataResourcePrototype(String name, SafeUri uri) {
    super(name, uri);
  }
}