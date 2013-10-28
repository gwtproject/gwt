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
package com.google.gwt.core.ext.soyc.coderef;

import com.google.gwt.thirdparty.guava.common.collect.Lists;

import java.util.List;

/**
 * The abstraction of any possible entity in the code that is register by soyc: classes, methods
 * and fields. It includes the recorded fragment and size with the assumption that it only appears
 * in a single fragment.
 *
 * @author ocallau@google.com (Oscar Callau)
 */
public abstract class EntityDescriptor {

  protected String name;
  protected String obfuscatedName;
  // Some entities can be in several fragments
  protected List<Fragment> fragments = Lists.newArrayList();

  /**
   * Stores the size contribution to each fragment for this entity.
   * Fragments are 0-based, and -1 means in no fragment
   */
  public class Fragment {
    public int id = -1;
    public int size;
  }

  public void addFragment(Fragment frg) {
    fragments.add(frg);
  }

  public List<Fragment> getFragments() {
    return fragments;
  }

  public abstract String getName();

  public String getObfuscatedName() {
    return obfuscatedName;
  }

  public void setObfuscatedName(String obfuscatedName) {
    this.obfuscatedName = obfuscatedName;
  }
}
