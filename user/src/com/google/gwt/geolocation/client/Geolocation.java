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

package com.google.gwt.geolocation.client;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.internal.Entry;
import com.google.gwt.dom.client.PartialSupport;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Implements the HTML5 Geolocation interface.
 * 
 * <p>
 * You can obtain a user's position by first calling
 * <code>Geolocation.getIfSupported()</code>
 * </p>
 * 
 * <p>
 * Once you have a <code>Geolocation</code>, you can request the user's current
 * position by calling {@link #getCurrentPosition(Callback)} or
 * {@link #watchPosition(Callback)}.
 * </p>
 * 
 * <p>
 * The first time an application requests the user's position, the browser will
 * prompt the user for permission. If the user grants permission, the browser
 * will locate the user and report it back to your application. If the user
 * declines permission, the callback's {@link Callback#onFailure(Object)} method
 * will be called with a {@link PositionError} with its code set to
 * {@link PositionError#PERMISSION_DENIED}.
 * </p>
 * 
 * <p>
 * <span style="color:red;">Experimental API: This API is still under
 * development and is subject to change.</span>
 * 
 * <p>
 * This may not be supported on all browsers.
 * </p>
 * 
 * @see <a href="http://www.w3.org/TR/geolocation-API/">W3C Geolocation API</a>
 * @see <a href="http://diveintohtml5.info/geolocation.html">Dive Into HTML5 -
 *      Geolocation</a>
 */
@PartialSupport
public final class Geolocation {
  
  @JsProperty(name = "geolocation", namespace = "window.navigator")
  private static native NativeGeolocation getGeoLocation();
  
  @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
  private static class NativeGeolocation {
    native void getCurrentPosition(
        NativeCallback<Position> success,
        NativeCallback<NativeError> failure,
        PositionOptions opt);
    native int watchPosition(
        NativeCallback<Position> success,
        NativeCallback<NativeError> failure,
        PositionOptions opt);

    native void clearWatch(int watchId);
  }

  private static Geolocation impl;

  /**
   * Additional options for receiving the user's location.
   */
  @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
  public static class PositionOptions {
    
    private boolean enableHighAccuracy;
    private int timeout;
    private int maximumAge;
    
    @JsProperty
    private native Object getEnableHighAccuracy();
    @JsProperty
    private native Object getTimeout();
    @JsProperty
    private native Object getMaximumAge();
    
    @JsOverlay
    private final PositionOptions ensureDefaults() {
      if (getEnableHighAccuracy() == null) {
        enableHighAccuracy = false;
      }
      if (getTimeout() == null) {
        timeout = -1;
      }
      
      if (getMaximumAge() == null) {
        maximumAge = 0;
      }
      return this;
    }

    /**
     * Sets whether or not the application will request a more accurate position
     * from the browser.
     *
     * <p>
     * If the browser supports this option, the user will be prompted to grant
     * permission to this application, even if permission to get the user's
     * (less accurate) position has already been granted.</p>
     *
     * <p>
     * Requesting high accuracy may be slower, or not supported at all,
     * depending on the browser.
     * </p>
     *
     * <p>
     * By default this is <code>false</code>
     * </p>
     */
    @JsOverlay
    public final PositionOptions setHighAccuracyEnabled(boolean enabled) {
      this.enableHighAccuracy = enabled;
      return this;
    }

    /**
     * Allows the browser to return a position immediately with a cached
     * position. The maximum age is then the oldest acceptable cached
     * position. If no acceptable cached position is found, the browser will
     * locate the user and cache and return the position.
     *
     * <p>
     * By default this is 0, which means that the position cache will not be
     * used.
     * </p>
     */
    @JsOverlay
    public final PositionOptions setMaximumAge(int maximumAge) {
      this.maximumAge = maximumAge;
      return this;
    }

    /**
     * Sets the amount of time (in milliseconds) that the application is willing
     * to wait before getting the user's position. If a request for position
     * takes more than this amount of time, an error will result.
     *
     * <p>
     * By default this is -1, which means there is no application-specified
     * timeout.
     * </p>
     */
    @JsOverlay
    public final PositionOptions setTimeout(int timeout) {
      this.timeout = timeout;
      return this;
    }
  }

  /**
   * Returns a {@link Geolocation} if the browser supports this feature, and
   * <code>null</code> otherwise.
   */
  public static Geolocation getIfSupported() {
    if (!isSupported()) {
      return null;
    } else {
      if (impl == null) {
        impl = new Geolocation();
      }
      return impl;
    }
  }
  
  /**
   * Returns <code>true</code> if the browser supports geolocation.
   */
  public static boolean isSupported() {
    String userAgent = System.getProperty("user.agent", "safari");
    if (userAgent.equals("ie8")) {
      return false;
    }
    return getGeoLocation() != null;
  }

  private static void handleFailure(Callback<Position, PositionError> callback, int code,
      String msg) {
    callback.onFailure(new PositionError(code, msg));
  }

  private static void handleSuccess(Callback<Position, PositionError> callback, Position pos) {
    callback.onSuccess(pos);
  }

  /**
   * Should be instantiated by {@link #getIfSupported()}.
   */
  protected Geolocation() {
  }

  /**
   * Stops watching the user's position.
   *
   * @param watchId the ID of a position watch as returned by a previous call to
   *        {@link #watchPosition(Callback)}.
   */
  public void clearWatch(int watchId) {
    getGeoLocation().clearWatch(watchId);
  }

  /**
   * Calls the callback with the user's current position.
   */
  public void getCurrentPosition(Callback<Position, PositionError> callback) {
    getCurrentPosition(callback, null);
  }

  @JsFunction
  private interface NativeCallback<T> {
    void onEvent(T t);
  }
  
  @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
  private static class NativeError {
    int code;
    String message;
  }
  
  /**
   * Calls the callback with the user's current position, with additional options.
   */
  public void getCurrentPosition(
      final Callback<Position, PositionError> callback, PositionOptions options) {
    if (options == null) {
      options = new PositionOptions();
    }

    NativeCallback<Position> success =
        new NativeCallback<Position>() {
          @Override
          public void onEvent(Position pos) {
            handleSuccess(callback, pos);
          }
        };

    NativeCallback<NativeError> failure =
        new NativeCallback<NativeError>() {

          @Override
          public void onEvent(NativeError t) {
            handleFailure(callback, t.code, t.message);
          }
        };

    getGeoLocation()
        .getCurrentPosition(
            Entry.wrapEntry(success), Entry.wrapEntry(failure), options.ensureDefaults());
  }

  /**
   * Repeatedly calls the given callback with the user's position, as it
   * changes.
   *
   * <p>
   * The frequency of these updates is entirely up to the browser. There is no
   * guarantee that updates will be received at any set interval, but are
   * instead designed to be sent when the user's position changes. This method
   * should be used instead of polling the user's current position.
   * </p>
   *
   * @return the ID of this watch, which can be passed to
   *         {@link #clearWatch(int)} to stop watching the user's position.
   */
  public int watchPosition(Callback<Position, PositionError> callback) {
    return watchPosition(callback, null);
  }

  /**
   * Repeatedly calls the given callback with the user's position, as it changes, with additional
   * options.
   *
   * <p>The frequency of these updates is entirely up to the browser. There is no guarantee that
   * updates will be received at any set interval, but are instead designed to be sent when the
   * user's position changes. This method should be used instead of polling the user's current
   * position.
   *
   * <p>If the browser does not support geolocation, this method will do nothing, and will return
   * -1.
   *
   * @return the ID of this watch, which can be passed to {@link #clearWatch(int)} to stop watching
   *     the user's position.
   */
  public int watchPosition(
      final Callback<Position, PositionError> callback, PositionOptions options) {
    if (options == null) {
      options = new PositionOptions();
    }

    NativeCallback<Position> success =
        new NativeCallback<Position>() {

          @Override
          public void onEvent(Position t) {
            handleSuccess(callback, t);
          }
        };

    NativeCallback<NativeError> failure =
        new NativeCallback<NativeError>() {

          @Override
          public void onEvent(NativeError t) {
            handleFailure(callback, t.code, t.message);
          }
        };

    int id = -1;
    if (isSupported()) {
      id =
          getGeoLocation()
              .watchPosition(
                  Entry.wrapEntry(success), Entry.wrapEntry(failure), options.ensureDefaults());
    }
    return id;
  }
}
