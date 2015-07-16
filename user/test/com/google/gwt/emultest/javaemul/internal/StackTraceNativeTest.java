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
package com.google.gwt.emultest.javaemul.internal;

import static com.google.gwt.emultest.javaemul.internal.StackTraceExamples.TYPE_ERROR;

import com.google.gwt.core.client.impl.Impl;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;

import javaemul.internal.StackTraceCreator;
import javaemul.internal.StackTraceCreator.CollectorLegacy;
import javaemul.internal.StackTraceCreator.CollectorModern;

/**
 * Tests {@link StackTraceCreator} in the native mode.
 */
@DoNotRunWith(Platform.Devel)
public class StackTraceNativeTest extends StackTraceTestBase {

  @Override
  public String getModuleName() {
    return "com.google.gwt.emultest.StackTraceNative";
  }

  @Override
  protected String[] getTraceJava() {
    return new String[] {
        Impl.getNameOf("@java.lang.Throwable::fillInStackTrace()"),
        Impl.getNameOf("@java.lang.Throwable::new(Ljava/lang/String;)"),
        Impl.getNameOf("@java.lang.Exception::new(Ljava/lang/String;)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::throwException2(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::throwException1(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::getLiveException(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceTestBase::testTraceJava()"),
    };
  }

  @Override
  protected String[] getTraceRecursion() {
    final String[] expectedModern = {
        Impl.getNameOf("@java.lang.Throwable::fillInStackTrace()"),
        Impl.getNameOf("@java.lang.Throwable::new(Ljava/lang/String;)"),
        Impl.getNameOf("@java.lang.Exception::new(Ljava/lang/String;)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::throwException2(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::throwException1(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::throwRecursive(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::throwRecursive(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::throwRecursive(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::throwException2(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::throwException1(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::getLiveException(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceTestBase::testTraceRecursion()"),
    };

    final String[] expectedLegacy = {
        Impl.getNameOf("@java.lang.Throwable::fillInStackTrace()"),
        Impl.getNameOf("@java.lang.Throwable::new(Ljava/lang/String;)"),
        Impl.getNameOf("@java.lang.Exception::new(Ljava/lang/String;)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::throwException2(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::throwException1(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::throwRecursive(*)"),
    };

    return isLegacyCollector() ? expectedLegacy : expectedModern;
  }

  @Override
  protected String[] getTraceJse(Object thrown) {
    String[] nativeMethodNames = StackTraceExamples.getNativeMethodNames();
    final String[] full = {
        nativeMethodNames[0],
        nativeMethodNames[1],
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::throwJse(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::throwException2(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::throwException1(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::getLiveException(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceTestBase::assertJse(*)"),
    };

    final String[] limited = {
        Impl.getNameOf("@com.google.gwt.lang.Exceptions::wrap(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceExamples::getLiveException(*)"),
        Impl.getNameOf("@com.google.gwt.emultest.javaemul.internal.StackTraceTestBase::assertJse(*)"),
    };

    // For legacy browsers and non-error javascript exceptions (e.g. throw "string"), we can only
    // construct stack trace from the catch block and below.

    return (isLegacyCollector() || thrown != TYPE_ERROR) ? limited : full;
  }

  // TODO(goktug): new Error().stack is broken for htmlunit:
  // https://sourceforge.net/p/htmlunit/bugs/1606/
  @DoNotRunWith(Platform.HtmlUnitBug)
  public void testCollectorType() {
    if (isIE8() || isSafari5()) {
      assertTrue(isLegacyCollector());
    } else {
      assertTrue(isModernCollector());
    }
  }

  private static boolean isLegacyCollector() {
    return StackTraceCreator.collector instanceof CollectorLegacy;
  }

  private static boolean isModernCollector() {
    return StackTraceCreator.collector instanceof CollectorModern;
  }

  private static native boolean isIE8() /*-{
    return navigator.userAgent.toLowerCase().indexOf('msie') != -1 && $doc.documentMode == 8;
  }-*/;

  private static native boolean isSafari5() /*-{
    return navigator.userAgent.match(' Safari/') && !navigator.userAgent.match(' Chrom')
        && !!navigator.userAgent.match(' Version/5.');
  }-*/;
}
