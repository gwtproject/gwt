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
package com.google.gwt.core.client;

/**
 * A simple wrapper around Object methods in JavaScript providing polyfills if necessary.
 */
public class JsObjects {
  // used by rebinding
  @SuppressWarnings("unused")
  private static class JsObjectsImplNative extends JsObjectsImpl {
    @Override
    public native JsArrayString getKeys(JavaScriptObject object) /*-{
      return Object.keys(object);
    }-*/;
  }

  private static class JsObjectsImpl {
    private static JsObjectsImpl instance =
        supportsObjectsKeys() ? new JsObjectsImplNative() : new JsObjectsImplPolyFill();

    private static native boolean supportsObjectsKeys() /*-{
      return !!Object.keys;
    }-*/;

    public JsArrayString getKeys(JavaScriptObject object) {
      return instance.getKeys(object);
    }
  }

  private static class JsObjectsImplPolyFill extends JsObjectsImpl {

    private static JavaScriptObject objectKeysPolyfill = getObjectKeysPolyfill();

    @Override
    public JsArrayString getKeys(JavaScriptObject object) {
      return executeObjectKeysPolyfill(objectKeysPolyfill, object);
    }

    private native JsArrayString executeObjectKeysPolyfill(JavaScriptObject func,
        JavaScriptObject param) /*-{
      return func(param);
    }-*/;

    private static native JavaScriptObject getObjectKeysPolyfill() /*-{
      var hasDontEnumBug = !({toString: null}).propertyIsEnumerable('toString'),
      dontEnums = ['constructor', 'hasOwnProperty', 'isPrototypeOf', 'propertyIsEnumerable',
          'toString', 'toLocaleString', 'valueOf'];

      return function (obj) {
        if (typeof obj !== 'object' && (typeof obj !== 'function' || obj === null)) {
          throw new TypeError('Object.keys called on non-object');
        }

        var result = [], prop, i;

        for (prop in obj) {
          if (Object.prototype.hasOwnProperty.call(obj, prop)) {
            result.push(prop);
          }
        }

        if (hasDontEnumBug) {
          for (i = 0; i < dontEnums.length; i++) {
            if (Object.prototype.hasOwnProperty.call(obj, dontEnums[i])) {
              result.push(dontEnums[i]);
            }
          }
        }
        return result;
      };
    }-*/;
  }

  private static final JsObjectsImpl impl = GWT.create(JsObjectsImpl.class);

  /**
   * This method returns an array with all the own (not in the prototype chain)
   * enumerable properties names ("keys") of a given object.
   */
  public static JsArrayString getKeys(JavaScriptObject object) {
    return impl.getKeys(object);
  }

  private JsObjects() {
  }
}
