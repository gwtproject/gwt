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
package java.util;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A factory to create ES6 Map instances.
 */
class ES6MapFactory {

  private static JavaScriptObject jsMapCstr = getJsMapConstructor();

  // Provided as instance method so the class needs to be instantiated. This prevents clinit to be
  // called twice from AbstractHashMap#reset.
  public native <V> ES6Map<V> newJsMap() /*-{
    return new @ES6MapFactory::jsMapCstr;
  }-*/;

  private static native JavaScriptObject getJsMapConstructor() /*-{
    if (typeof Map === 'function' && Map.prototype.entries) {
      return Map;
    } else {
      return @ES6MapFactory::getJsMapPolyFill()();
    }
  }-*/;

  /**
   * Returns polyfill for Map that can handle String keys.
   * <p>Implementation notes:
   * <p>String keys are mapped to their values via a JS associative map. String keys could collide
   * with intrinsic properties (like watch, constructor). To avoid that;
   * {@link InternalJsStringMap}) uses {@code Object.create(null)} so it doesn't inherit any
   * properties.
   * <p>For legacy browsers where {@code Object.create} is not available or handling of
   * {@code __proto__} is broken, the polyfill is patched to prepend each key with a ':' while
   * storing.
   */
  private static native JavaScriptObject getJsMapPolyFill() /*-{
    function stringmap() {
      this.size = 0;
      this.obj = this.createObject();
    };

    stringmap.prototype.createObject = function(key) {
      return Object.create(null);
    }

    stringmap.prototype.get = function(key) {
      return this.obj[key];
    };

    stringmap.prototype.set = function(key, value) {
      if (this.obj[key] === undefined) {
        this.size++;
      }
      this.obj[key] = value;
    };

    stringmap.prototype['delete'] = function(key) {
      if (this.obj[key] !== undefined) {
        delete this.obj[key];
        this.size--;
      }
    };

    stringmap.prototype.keys = function() {
      return Object.getOwnPropertyNames(this.obj);
    };

    stringmap.prototype.entries = function() {
      var keys = this.keys();
      var map = this;
      var nextIndex = 0;
      return {
        next: function() {
          if (nextIndex >= keys.length) return {done: true};
          var key = keys[nextIndex++];
          return {value: [key, map.get(key)], done: false};
        }
      };
    };

    if (!@ES6MapFactory::canHandleObjectCreateAndProto()()) {
      @ES6MapFactory::patchPolyfillForLegacy(*)(stringmap);
    }

    return stringmap;
  }-*/;

  /**
   * Return {@code true} if the browser is modern enough to handle Object.create and also properly
   * handles '__proto__' field with Object.create(null). (Safari 5, Android, Firefox)
   */
  private static native boolean canHandleObjectCreateAndProto() /*-{
    if (!Object.create || !Object.getOwnPropertyNames) {
      return false;
    }

    var protoField = "__proto__";

    var map = Object.create(null);
    if (map[protoField] !== undefined) {
      return false;
    }

    var keys = Object.getOwnPropertyNames(map);
    if (keys.length != 0) {
      return false;
    }

    map[protoField] = 42;
    if (map[protoField] !== 42) {
      return false;
    }

    // For old Firefox version who doesn't have native Map. See the Firefox bug:
    // https://bugzilla.mozilla.org/show_bug.cgi?id=837630
    if (Object.getOwnPropertyNames(map).length == 0) {
      return false;
    }

    // Looks like the browser has a workable handling of proto field.
    return true;
  }-*/;

  /**
   * Patches the polyfill to drop Object.create(null) and prefix each key with ':" so it will not
   * interfere with intrinsic fields.
   */
  private static native void patchPolyfillForLegacy(JavaScriptObject stringmap) /*-{
    function keyNormalizer(fn) {
      return function() {
        arguments[0] = ':' + arguments[0];
        return fn.apply(this, arguments);
      };
    }

    stringmap.prototype.createObject = function() { return {}; };

    stringmap.prototype.get = keyNormalizer(stringmap.prototype.get);

    stringmap.prototype.set = keyNormalizer(stringmap.prototype.set);

    stringmap.prototype['delete'] = keyNormalizer(stringmap.prototype['delete']);

    stringmap.prototype.keys = function() {
      var result = [];
      for (var key in this.obj) {
        if (key.charCodeAt(0) == 58) {
          result.push(key.substring(1));
        }
      }
      return result;
    };
  }-*/;

  static class ES6Iterator<V> extends JavaScriptObject {
    protected ES6Iterator() { }
    public final native ES6IteratorEntry<V> next() /*-{ return this.next(); }-*/;
  }

  static class ES6IteratorEntry<V> extends JavaScriptObject {
    protected ES6IteratorEntry() { }
    public final native boolean done() /*-{ return this.done; }-*/;
    public final native String getKey() /*-{ return this.value[0]; }-*/;
    public final native V getValue() /*-{ return this.value[1]; }-*/;
  }

  static class ES6Map<V> extends JavaScriptObject {
    protected ES6Map() { }
    public final native V get(String key) /*-{ return this.get(key); }-*/;
    public final native void set(int key, V value) /*-{ this.set(key, value); }-*/;
    public final native void set(String key, V value) /*-{ this.set(key, value); }-*/;
    // Calls delete via brackets to be workable with polyfills
    public final native void delete(int key) /*-{ this['delete'](key); }-*/;
    public final native void delete(String key) /*-{ this['delete'](key); }-*/;
    public final native int size() /*-{ return this.size; }-*/;
    public final native ES6Iterator<V> entries() /*-{ return this.entries(); }-*/;
  }
}
