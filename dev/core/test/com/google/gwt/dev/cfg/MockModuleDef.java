/*
 * Copyright 2007 Google Inc.
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
package com.google.gwt.dev.cfg;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.javac.testing.impl.MockResourceOracle;
import com.google.gwt.dev.resource.Resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Mock for {@link ModuleDef}.
 */
public class MockModuleDef extends ModuleDef {

  private Resource publicResource = new Resource() {

    @Override
    public long getLastModified() {
      return 0;
    }

    @Override
    public String getLocation() {
      return "/mock/onPublicPath.txt";
    }

    @Override
    public String getPath() {
      return "onPublicPath.txt";
    }

    @Override
    public InputStream openContents() {
      return new ByteArrayInputStream("w00t!".getBytes(UTF_8));
    }

    @Override
    public boolean wasRerooted() {
      return false;
    }
  };

  private Properties properties = new Properties();

  public MockModuleDef() {
    super("mock");
    normalize(TreeLogger.NULL);
    lazyPublicOracle = new MockResourceOracle(publicResource);
  }

  @Override
  public Properties getProperties() {
    return properties;
  }
}
