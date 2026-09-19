/*
 * Copyright 2026 GWT Project Authors
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

package com.google.gwt.dev.jjs;

import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.jjs.impl.FullCompileTestBase;
import com.google.gwt.dev.js.JsSymbolResolver;

import java.util.List;

public class JavaToJavaScriptCompilerTest extends FullCompileTestBase {

    public void testJsOptimization() throws UnableToCompleteException, InterruptedException {
        String code = """
                package test;
                import com.google.gwt.core.client.GWT;
                import com.google.gwt.core.client.RunAsyncCallback;
                public class EntryPoint {
                  public static final int FOO = 1337;
                  public static void functionA() { onModuleLoad(); }
                  public static native void log(double number) /*-{
                    console.log(number);
                  }-*/;
                  public static void onModuleLoad() {
                   log(FOO * 1.0);
                  }
                }""";

        compileSnippetToJS(code.toString());
        JsSymbolResolver.exec(jsProgram);
        JavaToJavaScriptCompiler.optimizeJsLoop(jsProgram, List.of(), 2);
        assertTrue(jsProgram.toSource().contains("1337"));
    }

    @Override
    protected void optimizeJava() {
        // only JS optimization
    }
}
