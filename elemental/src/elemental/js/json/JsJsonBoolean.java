/*
 * Copyright 2010 Google Inc.
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
package elemental.js.json;

import elemental.json.JsonBoolean;

/**
 * Client-side 'low overhead' JSO implementation using extension method
 * technique.
 */
final public class JsJsonBoolean extends JsJsonValue
    implements JsonBoolean {

  public static JsonBoolean create(boolean bool) {
    return createProd(bool);
  }

  /*
   * MAGIC: primitive boolean cast to object interface.
   */
  private static native JsJsonBoolean createProd(boolean bool) /*-{
    return Object(bool);
  }-*/;

  protected JsJsonBoolean() {
  }

  public boolean getBoolean() {
    return valueProd();
  }

  private native boolean valueProd() /*-{
    return this && this.valueOf();
  }-*/;
}
