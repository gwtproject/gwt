/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dev.cfg;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

import java.io.PrintWriter;
import java.util.Map;

/**
 * Generator used to generate a class unique to the current module which has a register() function
 * which when invoked will register all currently known runtime rebind rule implementations with a
 * global registry.<br />
 *
 * The resulting class is expected to be invoked at runtime as part of per module bootstrapping and
 * before anything that might depend on GWT.create() rebinding (including onModuleLoad() or
 * gwtOnLoad() methods).
 */
public class RuntimeRebindRegistratorGenerator extends Generator {

  /**
   * The extension for all generated runtime rebind registrator classes. Is exposed publicly so
   * others can filter using the extension.
   */
  public static final String RUNTIME_REBIND_REGISTRATOR_TYPE_NAME = "RuntimeRebindRegistrator";

  @Override
  public String generate(TreeLogger logger, GeneratorContext context, String moduleName)
      throws UnableToCompleteException {
    Map<String, String> runtimeRebindRuleSourcesByName =
        RuntimeRebindRuleGenerator.RUNTIME_REBIND_RULE_SOURCES_BY_NAME;

    // Creates a new class definition unique per module to avoid collision.
    String typeName =
        moduleName.replace(".", "_").replace("-", "_") + "_" + RUNTIME_REBIND_REGISTRATOR_TYPE_NAME;
    PrintWriter out = context.tryCreate(logger, "com.google.gwt.core.client", typeName);

    if (out != null) {
      out.println("package com.google.gwt.core.client;");
      out.println("public class " + typeName + " {");

      // Drop all of the runtime rebind rule class implementations in.
      for (String runtimeRebindRuleSource : runtimeRebindRuleSourcesByName.values()) {
        out.println(runtimeRebindRuleSource);
      }

      out.println("  public static void register() {");
      if (!runtimeRebindRuleSourcesByName.isEmpty()) {
        // Instantiates and registers each one.
        for (String runtimeRebindRuleName : runtimeRebindRuleSourcesByName.keySet()) {
          out.println(String.format(
              "    RuntimeRebinder.registerRuntimeRebindRule(new %s());", runtimeRebindRuleName));
        }
      } else {
        out.println("    // There are no runtime rebind rules for this module.");
      }
      out.println("  }");
      out.println("}");

      context.commit(logger, out);
    } else {
      // Must have been a cache hit.
    }

    return "com.google.gwt.core.client." + typeName;
  }
}
