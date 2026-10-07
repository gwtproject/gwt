/*
 * Copyright 2011 Google Inc.
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
package com.google.gwt.user.client.impl;

/**
 * Mozilla implementation of {@link com.google.gwt.user.client.impl.WindowImpl}.
 * @deprecated This class is only used to make tests work with HtmlUnit 2.55.0, and will
 * be removed in a future release after HtmlUnit is updated.
 */
@Deprecated
public class WindowImplMozilla extends WindowImpl {

  /**
   * For old Firefox versions, reading from $wnd.location.hash decodes the fragment.
   * The <a href="https://bugzilla.mozilla.org/show_bug.cgi?id=483304">issue</a>
   * still exists in the HtmlUnit version currently used for testing.
   */
  @Override
  public native String getHash() /*-{
    var href = $wnd.location.href;
    var hashLoc = href.indexOf("#");
    return (hashLoc > 0) ? href.substring(hashLoc) : "";
  }-*/;

}
