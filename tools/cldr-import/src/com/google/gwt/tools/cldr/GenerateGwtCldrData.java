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
package com.google.gwt.tools.cldr;

import com.google.gwt.i18n.shared.GwtLocale;

import com.ibm.icu.dev.tool.UOption;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Generate a country list for each locale, taking into account the literate
 * population of each country speaking the language.
 */
public class GenerateGwtCldrData {

  // LocaleInfoProcessor pulls in all others, so the default is to generate everything
  private static final String DEFAULT_PROCESSORS = "LocaleInfoProcessor";

  public static void main(String[] args) throws IOException, SecurityException,
      IllegalArgumentException {
    System.out.println("Starting to generate from CLDR data");
    UOption[] options = {
        UOption.HELP_H(), UOption.HELP_QUESTION_MARK(),
        // v24
        // UOption.SOURCEDIR().setDefault(CldrUtility.MAIN_DIRECTORY),
        // v25
        UOption.SOURCEDIR(),
        outputDir().setDefault("./"),
        restrictLocales(),
        processors().setDefault(DEFAULT_PROCESSORS),
    };
    UOption.parseArgs(args, options);
    String sourceDir = options[2].value; // SOURCEDIR
    String targetDir = options[3].value; // outputDir
    String restrictLocales = options[4].value; // --restrictLocales
    String procNames = options[5].value; // processors

    List<Class<? extends Processor>> processorClasses = new ArrayList<Class<? extends Processor>>();
    for (String procName : procNames.split(",")) {
      if (!procName.contains(".")) {
        procName = Processor.class.getPackage().getName() + "." + procName;
      }
      Throwable thrown = null;
      try {
        Class<?> clazz = Class.forName(procName);
        processorClasses.add(clazz.asSubclass(Processor.class));
      } catch (ClassNotFoundException e) {
        thrown = e;
      } catch (ClassCastException e) {
        thrown = e;
      }
      if (thrown != null) {
        System.err.println("Ignoring " + procName + " (" + thrown + ")");
      }
    }
    CldrData cldrData = new CldrData(sourceDir, restrictLocales);
    File outputDir = new File(targetDir);
    Processors processors = new Processors(cldrData, outputDir);
    for (Class<? extends Processor> processorClass : processorClasses) {
      processors.requireProcessor(processorClass);
    }
    System.out.println("Loading external data");
    for (GwtLocale locale : cldrData.allLocales()) {
      System.out.println("  " + locale.toString());
      for (Processor processor : processors) {
        processor.loadExternalData(locale);
      }
    }
    System.out.println("Loading CLDR data");
    cldrData.processData();
    System.out.println("Cleaning up data");
    for (Processor processor : processors) {
      processor.cleanupData();
    }
    System.out.println("Performing post-processing");
    for (Processor processor : processors) {
      processor.afterCleanup();
    }
    System.out.println("Writing output files");
    for (Processor processor : processors) {
      processor.writeOutputFiles();
    }
    System.out.println("Finished.");
  }

  private static UOption outputDir() {
    return UOption.create("outdir", 'o', UOption.REQUIRES_ARG);
  }

  private static UOption processors() {
    return UOption.create("processors", 'p', UOption.REQUIRES_ARG);
  }

  private static UOption restrictLocales() {
    return UOption.create("restrictLocales", 'r', UOption.REQUIRES_ARG);
  }
}
