/*
 * Copyright 2008 Google Inc.
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
package com.google.gwt.dev.javac;

import com.google.gwt.core.ext.TreeLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class to invalidate units in a set based on errors or references to
 * other invalidate units.
 */
public class CompilationUnitInvalidator {

  public static void retainValidUnits(TreeLogger logger,
      Collection<CompilationUnit> units, Map<String, CompiledClass> validClasses) {
    logger = logger.branch(TreeLogger.TRACE, "Removing invalidated units");

    // Build a map of api-refs (that don't refer to non-error validClasses) -> dependent units
    Map<String, List<CompilationUnit>> depsNeeded = new HashMap<String, List<CompilationUnit>>();

    // This is what deps the new units can provide
    Set<String> depsProvided = new HashSet<String>();

    // These are all of the invalid units
    Set<CompilationUnit> allBrokenUnits = new HashSet<CompilationUnit>();

    // This find units that depend on broken units from validClasses and units
    // that depend on other units.  It builds depsNeeded and depsProvided for
    // later.
    for (Iterator<CompilationUnit> unitIt = units.iterator(); unitIt.hasNext(); ) {
      CompilationUnit unit = unitIt.next();

      if (unit.isError()) {
        // It is bad and can be removed immediately
        allBrokenUnits.add(unit);
      } else {
        // Update set of dependencies the unit provides
        for (CompiledClass cc : unit.getCompiledClasses()) {
          depsProvided.add(cc.getSourceName());
        }

        // Update map of dependencies that the unit needs
        for (String ref : unit.getDependencies().getApiRefs()) {
          // Check validClasses
          CompiledClass compiledClass = validClasses.get(ref);
          if ((compiledClass == null) || compiledClass.getUnit().isError()) {
            // we'll put this into the double-check pot
            appendAtKey(depsNeeded, ref, unit);
          }
        }
      }
    }

    // At this point, validClasses have been fully utilized and are just checking
    // within `units` using depsNeeded and depsProvided.
    boolean changed;
    do {
      // Find the missing deps for this pass
      Map<String, List<CompilationUnit>> missing =
          new HashMap<String, List<CompilationUnit>>(depsNeeded);
      missing.keySet().removeAll(depsProvided);

      // Process the units with missing deps
      for (Map.Entry<String, List<CompilationUnit>> brokenEntry : missing.entrySet()) {
        List<CompilationUnit> brokenUnits = brokenEntry.getValue();
        allBrokenUnits.addAll(brokenUnits);

        // Remove the broken units from the provides set
        for (CompilationUnit brokenUnit : brokenUnits) {
          for (CompiledClass cc : brokenUnit.getCompiledClasses()) {
            depsProvided.remove(cc.getSourceName());
          }
        }

        // Log it to maintain some logging compatibility with prior versions
        // of this class.
        for (CompilationUnit compilationUnit : brokenUnits) {
          if (!allBrokenUnits.contains(compilationUnit)) {
            TreeLogger branch = logger.branch(TreeLogger.DEBUG,
                "Compilation unit '" + compilationUnit
                + "' is removed due to invalid reference(s):");
            branch.log(TreeLogger.DEBUG, brokenEntry.getKey());
          }
        }
      }

      // Having found and removed some units with missing deps, remove their
      // needs.
      depsNeeded.keySet().removeAll(missing.keySet());

      changed = !missing.isEmpty();
    } while (changed);

    units.removeAll(allBrokenUnits);
  }

  /**
   * Appends the value to a list found in the map at the given key.  Creates and
   * adds a new list to the map if there is no entry at key.
   */
  private static <K, V> void appendAtKey(Map<K, List<V>> map, K key, V value) {
    List<V> list = map.get(key);
    if (list == null) {
      list = new ArrayList<V>();
      map.put(key, list);
    }
    list.add(value);
  }
}
