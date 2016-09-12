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

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Represents information about the user's position as reported by the browser.
 */
@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public interface Position {

  /**
   * Returns information about the coordinates reported by the browser.
   */
  @JsProperty(name = "coords")
  Coordinates getCoordinates();

  /**
   * Returns the time this position was reported by the browser.
   */
  @JsProperty
  double getTimestamp();

  /**
   * Represents position reported by the browser.
   */
  @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
  public interface Coordinates {

    /**
     * Returns the estimated accuracy reported by the browser, in meters.
     */
    @JsProperty
    double getAccuracy();

    /**
     * Returns the altitude reported by the browser, in meters, above the <a
     * href="http://en.wikipedia.org/wiki/Reference_ellipsoid">reference
     * ellipsoid</a>, or <code>null</code> if the browser did not report an
     * altitude.
     */
    @JsProperty
    double getAltitude();

    /**
     * Returns the estimated accuracy of the altitude reported by the browser,
     * in meters, or <code>null</code> if the browser did not report an
     * accuracy.
     */
    @JsProperty
    Double getAltitudeAccuracy();

    /**
     * Returns the heading, in degrees from due north, reported by the browser,
     * based on previous calls to get the user's position, or <code>null</code>
     * if the browser did not report a heading.
     */
    @JsProperty
    Double getHeading();

    /**
     * Returns the decimal latitude reported by the browser.
     */
    @JsProperty
    double getLatitude();

    /**
     * Returns the decimal longitude reported by the browser.
     */
    @JsProperty
    double getLongitude();

    /**
     * Returns the speed, in meters/second, reported by the browser, based on
     * previous calls to get the user's position, or <code>null</code> if the
     * browser did not report a speed.
     */
    @JsProperty
    Double getSpeed();
  }
}
