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
package com.google.gwt.lang;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Utility class for defining class prototyes to setup an equivalent to the Java class hierarchy in
 * JavaScript.
 */
public class JavaClassHierarchySetupUtil {
  /*
  * Holds a map from typeIds to prototype objects.
  */
  private static JavaScriptObject prototypesByTypeId = JavaScriptObject.createObject();

  /**
   * If not already created it creates the prototype for the class and stores it in
   * {@code prototypesByTypeId}. If superTypeId is null, it means that the class being defined
   * is the topmost class (i.e. java.lang.Object) and creates an empty prototype for it.
   * Otherwise it creates the prototype for the class by calling {@code createSubclassPrototype()}.
   * It also assigns the castable type map and sets the constructors prototype field to the
   * current prototype.
   * Finally adds the class literal if it was created before the call to {@code defineClass}.
   * Class literals might be created before the call to {@code defineClass} if they are in separate
   * code-split fragments. In that case Class.createFor* methods will have created a placeholder and
   * stored in {@code prototypesByTypeId} the class literal.<p></p>
   *
   * As a prerequisite if superSeed is not null, it is assumed that defineClass for the supertype
   * has already been called.
   */
  public static native JavaScriptObject defineClass(JavaScriptObject typeId,
      JavaScriptObject superTypeId, JavaScriptObject castableTypeMap) /*-{
    // Setup aliases for (horribly long) JSNI references.
    var prototypesByTypeId = @com.google.gwt.lang.JavaClassHierarchySetupUtil::prototypesByTypeId;
    var createSubclassPrototype =
        @com.google.gwt.lang.JavaClassHierarchySetupUtil::createSubclassPrototype(*)
    var maybeGetClassLiteralFromPlaceHolder =  @com.google.gwt.lang.JavaClassHierarchySetupUtil::
        maybeGetClassLiteralFromPlaceHolder(*);
    // end of alias definitions.

    var prototype = prototypesByTypeId[typeId];
    var clazz = maybeGetClassLiteralFromPlaceHolder(prototype);
    if (prototype && !clazz) {
      // not a placeholder entry setup by Class.setClassLiteral
      _ = prototype;
    } else {
      _ = prototypesByTypeId[typeId]  = (!superTypeId) ? {} : createSubclassPrototype(superTypeId);
      // Make polymorphic dispatch work in v8 for overridden methods.
      // TODO(dankurka): remove this once we have new style cast maps.
      _['__$objectId_' + typeId] = 1;
      _.@java.lang.Object::castableTypeMap = castableTypeMap;
      _.constructor = _;
      if (!superTypeId) {
        // Set the typeMarker on java.lang.Object's prototype, implicitly setting it for all
        // Java subclasses (String and Arrays have special handling in Cast and Array respectively).
        _.@java.lang.Object::typeMarker =
            @JavaClassHierarchySetupUtil::typeMarkerFn(*);
      }
    }
    for (var i = 3; i < arguments.length; ++i) {
      // Assign the type prototype to each constructor.
      arguments[i].prototype = _;
    }
    if (clazz) {
      _.@java.lang.Object::___clazz = clazz;
    }
  }-*/;

  /**
   * Like defineClass() but second parameter is a native JS prototype reference.
   */
  public static native JavaScriptObject defineClassWithPrototype(int typeId, JavaScriptObject jsSuperClass,
                                                    JavaScriptObject castableTypeMap) /*-{
      // Setup aliases for (horribly long) JSNI references.
      var prototypesByTypeId = @com.google.gwt.lang.JavaClassHierarchySetupUtil::prototypesByTypeId;

      var maybeGetClassLiteralFromPlaceHolder =  @com.google.gwt.lang.JavaClassHierarchySetupUtil::
          maybeGetClassLiteralFromPlaceHolder(Lcom/google/gwt/core/client/JavaScriptObject;);
      // end of alias definitions.

      var prototype = prototypesByTypeId[typeId];
      var clazz = maybeGetClassLiteralFromPlaceHolder(prototype);

      if (prototype && !clazz) {
          // not a placeholder entry setup by Class.setClassLiteral
          _ = prototype;
      } else {
          var superPrototype = jsSuperClass && jsSuperClass.prototype || {};
          _ = prototypesByTypeId[typeId] =  @com.google.gwt.lang.JavaClassHierarchySetupUtil::
              portableObjCreate(Lcom/google/gwt/core/client/JavaScriptObject;)(superPrototype);
          // Make polymorphic dispatch work in v8 for overridden methods.
          // TODO(dankurka): remove this once we have new style cast maps.
          _['__$objectId_' + typeId] = 1;
          _.@java.lang.Object::castableTypeMap = castableTypeMap;
      }
      for (var i = 3; i < arguments.length; ++i) {
          // Assign the type prototype to each constructor.
          arguments[i].prototype = _;
      }
      if (clazz) {
          _.@java.lang.Object::___clazz = clazz;
      }
  }-*/;

  private static native JavaScriptObject portableObjCreate(JavaScriptObject obj) /*-{
    function F() {};
    F.prototype = obj || {};
    return new F();
  }-*/;

  /**
   * Create a subclass prototype.
   */
  public static native JavaScriptObject createSubclassPrototype(JavaScriptObject superTypeId) /*-{
    // Setup aliases for (horribly long) JSNI references.
    var prototypesByTypeId = @com.google.gwt.lang.JavaClassHierarchySetupUtil::prototypesByTypeId;
    // end of alias definitions.
    return @com.google.gwt.lang.JavaClassHierarchySetupUtil::
        portableObjCreate(Lcom/google/gwt/core/client/JavaScriptObject;)(prototypesByTypeId[superTypeId]);
  }-*/;

  /**
   * Retrieves the class literal if stored in a place holder, {@code null} otherwise.
   */
  private static native JavaScriptObject maybeGetClassLiteralFromPlaceHolder(
      JavaScriptObject entry) /*-{
    // TODO(rluble): Relies on Class.createFor*() storing the class literal wrapped as an array
    // to distinguish it from an actual prototype.
    return (entry instanceof Array) ? entry[0] : null;
  }-*/;

  /**
   * Creates a JS namespace to attach exported classes to.
   * @param namespace a dotted js namespace string
   * @return a nested object literal representing the namespace
   */
  public static native JavaScriptObject provide(JavaScriptObject namespace) /*-{
      var cur = this; // global this
      // TODO: remove and switch default assume via compile-time config/flag
      if (namespace == '$wnd') {
          return $wnd;
      } else if (namespace === '') {
          return cur;
      }

      // if namespace begins with $wnd, then we root the namespace there
      if (namespace.substring(0, 5) == '$wnd.') {
          cur = $wnd;
          namespace = namespace.substring(5);
      }
      // borrowed from Closure's base.js
      var parts = namespace.split('.');

      // Internet Explorer exhibits strange behavior when throwing errors from
      // methods externed in this manner.  See the testExportSymbolExceptions in
      // base_test.html for an example.
      if (!(parts[0] in cur) && cur.execScript) {
          cur.execScript('var ' + parts[0]);
      }

      // Certain browsers cannot parse code in the form for((a in b); c;);
      // This pattern is produced by the JSCompiler when it collapses the
      // statement above into the conditional loop below. To prevent this from
      // happening, use a for-loop and reserve the init logic as below.

      // Parentheses added to eliminate strict JS warning in Firefox.
      for (var part; parts.length && (part = parts.shift());) {
          if (cur[part]) {
              cur = cur[part];
          } else {
              cur = cur[part] = {};
          }
      }
      return cur;
  }-*/;

  /**
   * Create a function that invokes the specified method reference.
   */
  public static native JavaScriptObject makeBridgeMethod(JavaScriptObject methodRef) /*-{
    return function() {
      return methodRef.apply(this, arguments);
    };
  }-*/;

  /**
   * Do polyfills for all methods expected in a modern browser.
   */
  public static native void modernizeBrowser() /*-{
    // Patch up Array.isArray for browsers that don't support the fast native check.
    // This is only needed for IE8
    if (!Array.isArray) {
        Array.isArray = function (vArg) {
          return Object.prototype.toString.call(vArg) === "[object Array]";
        };
    }

    // Implemented similar to:
    // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/keys
    // This is only needed for IE8
    if (!Object.keys) {
      var hasDontEnumBug = !({toString: null}).propertyIsEnumerable('toString'),
      dontEnums = ['constructor', 'hasOwnProperty', 'isPrototypeOf', 'propertyIsEnumerable',
          'toLocaleString', 'toString', 'valueOf'];

      Object.keys = function(obj) {
        if (obj === null || (typeof obj !== 'object' && typeof obj !== 'function')) {
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
    }
  }-*/;

  /**
   * Retrieves the prototype for a type if it exists, null otherwise.
   */
  public static native JavaScriptObject getClassPrototype(JavaScriptObject typeId) /*-{
    var prototypeForTypeId =
        @com.google.gwt.lang.JavaClassHierarchySetupUtil::prototypesByTypeId[typeId];
    return prototypeForTypeId;
  }-*/;

  /**
   * Marker function. All Java Objects (except Strings) have a typeMarker field pointing to
   * this function.
   */
  static native void typeMarkerFn() /*-{
  }-*/;

  /**
   * A global noop function. Replaces clinits after execution.
   */
  static native void emptyMethod() /*-{
  }-*/;
}
