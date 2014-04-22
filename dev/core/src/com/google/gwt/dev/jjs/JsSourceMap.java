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
package com.google.gwt.dev.jjs;

import com.google.gwt.core.ext.soyc.Range;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * An unmodifiable container of mappings from one JavaScript file to the
 * Java code it came from.
 */
public class JsSourceMap extends AbstractMap<Range, SourceInfo> {
  private final Map<Range, SourceInfo> delegate;

  public JsSourceMap(Map<Range, SourceInfo> data) {
    this.delegate = Collections.unmodifiableMap(data);
  }

  @Override
  public Set<Entry<Range, SourceInfo>> entrySet() {
    return delegate.entrySet();
  }
}
