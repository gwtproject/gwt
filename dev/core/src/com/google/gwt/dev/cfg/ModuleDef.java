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
package com.google.gwt.dev.cfg;

import com.google.gwt.core.ext.Linker;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.javac.CompilationProblemReporter;
import com.google.gwt.dev.javac.CompilationState;
import com.google.gwt.dev.javac.CompilationStateBuilder;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.resource.ResourceOracle;
import com.google.gwt.dev.resource.impl.DefaultFilters;
import com.google.gwt.dev.resource.impl.PathPrefix;
import com.google.gwt.dev.resource.impl.PathPrefixSet;
import com.google.gwt.dev.resource.impl.ResourceFilter;
import com.google.gwt.dev.resource.impl.ResourceOracleImpl;
import com.google.gwt.dev.util.Empty;
import com.google.gwt.dev.util.Util;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Represents a module specification. In principle, this could be built without
 * XML for unit tests.
 */
public class ModuleDef {

  private static final ResourceFilter NON_JAVA_RESOURCES = new ResourceFilter() {
    @Override
    public boolean allows(String path) {
      return !path.endsWith(".java") && !path.endsWith(".class");
    }
  };

  private static final Comparator<Map.Entry<String, ?>> REV_NAME_CMP =
      new Comparator<Map.Entry<String, ?>>() {
        @Override
        public int compare(Map.Entry<String, ?> entry1, Map.Entry<String, ?> entry2) {
          String key1 = entry1.getKey();
          String key2 = entry2.getKey();
          // intentionally reversed
          return key2.compareTo(key1);
        }
      };

  public static boolean isValidModuleName(String moduleName) {
    // Check for an empty string between two periods.
    if (moduleName.contains("..")) {
      return false;
    }
    // Insure the package name components are a valid Java ident.
    String[] parts = moduleName.split("\\.");
    for (int i = 0; i < parts.length - 1; i++) {
      String part = parts[i];
      if (!Util.isValidJavaIdent(part)) {
        return false;
      }
    }
    return true;
  }

  private final Set<String> activeLinkers = new LinkedHashSet<String>();

  private String activePrimaryLinker;

  /**
   * A set of URLs for <module>.gwtar files found on the classpath that correspond
   * to <module>.gwt.xml files loaded as a part of this module's nested load.
   * 
   * @see com.google.gwt.dev.CompileModule
   */
  private final List<URL> archiveURLs = new ArrayList<URL>();

  private boolean collapseAllProperties;

  private final DefaultFilters defaultFilters;

  private final List<String> entryPointTypeNames = new ArrayList<String>();

  private final Set<File> gwtXmlFiles = new HashSet<File>();

  private final Set<String> inheritedModules = new HashSet<String>();

  /**
   * All resources found on the public path, specified by <public> directives in
   * modules (or the implicit ./public directory). Marked 'lazy' because it does not
   * start searching for resources until a query is made.
   */
  private ResourceOracleImpl lazyPublicOracle;

  /**
   * A subset of lazySourceOracle, contains files other than .java and .class files.
   */
  private ResourceOracleImpl lazyResourcesOracle;

  /**
   * Contains all files from the source path, specified by <source> and <super>
   * directives in modules (or the implicit ./client directory).
   */
  private ResourceOracleImpl lazySourceOracle;

  private final Map<String, Class<? extends Linker>> linkerTypesByName =
      new LinkedHashMap<String, Class<? extends Linker>>();

  private final long moduleDefCreationTime = System.currentTimeMillis();

  private final String name;
  private final ResourceLoader resources;

  /**
   * Must use a separate field to track override, because setNameOverride() will
   * get called every time a module is inherited, but only the last one matters.
   */
  private String nameOverride;

  private boolean needsRefresh;

  private final Properties properties = new Properties();

  private PathPrefixSet publicPrefixSet = new PathPrefixSet();

  private final Rules rules = new Rules();

  private final Scripts scripts = new Scripts();

  private final Map<String, String> servletClassNamesByPath = new HashMap<String, String>();

  private PathPrefixSet sourcePrefixSet = new PathPrefixSet();

  private final Styles styles = new Styles();

  public ModuleDef(String name) {
    this(name, ResourceLoaders.forClassLoader(Thread.currentThread()));
  }

  public ModuleDef(String name, ResourceLoader resources) {
    this.name = name;
    this.resources = resources;
    defaultFilters = new DefaultFilters();
  }

  public synchronized void addEntryPointTypeName(String typeName) {
    entryPointTypeNames.add(typeName);
  }

  public void addGwtXmlFile(File xmlFile) {
    gwtXmlFiles.add(xmlFile);
  }

  public void addLinker(String name) {
    Class<? extends Linker> clazz = getLinker(name);
    assert clazz != null;

    LinkerOrder order = clazz.getAnnotation(LinkerOrder.class);
    if (order.value() == Order.PRIMARY) {
      if (activePrimaryLinker != null) {
        activeLinkers.remove(activePrimaryLinker);
      }
      activePrimaryLinker = name;
    }

    activeLinkers.add(name);
  }

  public synchronized void addPublicPackage(String publicPackage, String[] includeList,
      String[] excludeList, String[] skipList, boolean defaultExcludes, boolean caseSensitive) {

    if (lazyPublicOracle != null) {
      throw new IllegalStateException("Already normalized");
    }
    publicPrefixSet.add(new PathPrefix(publicPackage, defaultFilters.customResourceFilter(
        includeList, excludeList, skipList, defaultExcludes, caseSensitive), true, excludeList));
  }

  public void addSourcePackage(String sourcePackage, String[] includeList, String[] excludeList,
      String[] skipList, boolean defaultExcludes, boolean caseSensitive) {
    addSourcePackageImpl(sourcePackage, includeList, excludeList, skipList, defaultExcludes,
        caseSensitive, false);
  }

  public void addSourcePackageImpl(String sourcePackage, String[] includeList,
      String[] excludeList, String[] skipList, boolean defaultExcludes, boolean caseSensitive,
      boolean isSuperSource) {
    if (lazySourceOracle != null) {
      throw new IllegalStateException("Already normalized");
    }
    PathPrefix pathPrefix =
        new PathPrefix(sourcePackage, defaultFilters.customJavaFilter(includeList, excludeList,
            skipList, defaultExcludes, caseSensitive), isSuperSource, excludeList);
    sourcePrefixSet.add(pathPrefix);
  }

  public void addSuperSourcePackage(String superSourcePackage, String[] includeList,
      String[] excludeList, String[] skipList, boolean defaultExcludes, boolean caseSensitive) {
    addSourcePackageImpl(superSourcePackage, includeList, excludeList, skipList, defaultExcludes,
        caseSensitive, true);
  }

  /**
   * Free up memory no longer needed in later compile stages. After calling this
   * method, the ResourceOracle will be empty and unusable. Calling
   * {@link #refresh()} will restore them.
   */
  public synchronized void clear() {
    if (lazySourceOracle != null) {
      lazySourceOracle.clear();
    }
    rules.dispose();
  }

  public void clearEntryPoints() {
    entryPointTypeNames.clear();
  }

  /**
   * Associate a Linker class with a symbolic name. If the name had been
   * previously assigned, this method will redefine the name. If the redefined
   * linker had been previously added to the set of active linkers, the old
   * active linker will be replaced with the new linker.
   */
  public void defineLinker(TreeLogger logger, String name, Class<? extends Linker> linker)
      throws UnableToCompleteException {
    Class<? extends Linker> old = getLinker(name);
    if (old != null) {
      // Redefining an existing name
      if (activePrimaryLinker.equals(name)) {
        // Make sure the new one is also a primary linker
        if (!linker.getAnnotation(LinkerOrder.class).value().equals(Order.PRIMARY)) {
          logger.log(TreeLogger.ERROR, "Redefining primary linker " + name
              + " with non-primary implementation " + linker.getName());
          throw new UnableToCompleteException();
        }

      } else if (activeLinkers.contains(name)) {
        // Make sure it's a not a primary linker
        if (linker.getAnnotation(LinkerOrder.class).value().equals(Order.PRIMARY)) {
          logger.log(TreeLogger.ERROR, "Redefining non-primary linker " + name
              + " with primary implementation " + linker.getName());
          throw new UnableToCompleteException();
        }
      }
    }
    linkerTypesByName.put(name, linker);
  }

  public synchronized Resource findPublicFile(String partialPath) {
    doRefresh();
    return lazyPublicOracle.getResourceMap().get(partialPath);
  }

  public synchronized String findServletForPath(String actual) {
    // Walk in backwards sorted order to find the longest path match first.
    Set<Entry<String, String>> entrySet = servletClassNamesByPath.entrySet();
    Entry<String, String>[] entries = Util.toArray(Entry.class, entrySet);
    Arrays.sort(entries, REV_NAME_CMP);
    for (int i = 0, n = entries.length; i < n; ++i) {
      String mapping = entries[i].getKey();
      /*
       * Ensure that URLs that match the servlet mapping, including those that
       * have additional path_info, get routed to the correct servlet.
       * 
       * See "Inside Servlets", Second Edition, pg. 208
       */
      if (actual.equals(mapping) || actual.startsWith(mapping + "/")) {
        return entries[i].getValue();
      }
    }
    return null;
  }

  /**
   * Returns the Resource for a source file if it is found; <code>null</code>
   * otherwise.
   * 
   * @param partialPath the partial path of the source file
   * @return the resource for the requested source file
   */
  public synchronized Resource findSourceFile(String partialPath) {
    doRefresh();
    return lazySourceOracle.getResourceMap().get(partialPath);
  }

  public Set<String> getActiveLinkerNames() {
    return new LinkedHashSet<String>(activeLinkers);
  }

  public Set<Class<? extends Linker>> getActiveLinkers() {
    Set<Class<? extends Linker>> toReturn = new LinkedHashSet<Class<? extends Linker>>();
    for (String linker : activeLinkers) {
      assert linkerTypesByName.containsKey(linker) : linker;
      toReturn.add(linkerTypesByName.get(linker));
    }
    return toReturn;
  }

  public Class<? extends Linker> getActivePrimaryLinker() {
    assert linkerTypesByName.containsKey(activePrimaryLinker) : activePrimaryLinker;
    return linkerTypesByName.get(activePrimaryLinker);
  }

  /**
   * Returns URLs to fetch archives of precompiled compilation units.
   * 
   * @see com.google.gwt.dev.CompileModule
   */
  public Collection<URL> getAllCompilationUnitArchiveURLs() {
    return Collections.unmodifiableCollection(archiveURLs);
  }

  public String[] getAllPublicFiles() {
    doRefresh();
    return lazyPublicOracle.getPathNames().toArray(Empty.STRINGS);
  }

  /**
   * Strictly for statistics gathering. There is no guarantee that the source
   * oracle has been initialized.
   */
  public String[] getAllSourceFiles() {
    doRefresh();
    return lazySourceOracle.getPathNames().toArray(Empty.STRINGS);
  }

  /**
   * Returns the physical name for the module by which it can be found in the
   * classpath.
   */
  public String getCanonicalName() {
    return name;
  }

  public CompilationState getCompilationState(TreeLogger logger) throws UnableToCompleteException {
    return getCompilationState(logger, false);
  }

  public synchronized CompilationState getCompilationState(TreeLogger logger, boolean suppressErrors)
      throws UnableToCompleteException {
    doRefresh();
    CompilationState compilationState =
        CompilationStateBuilder.buildFrom(logger, lazySourceOracle.getResources(), null,
            suppressErrors);
    checkForSeedTypes(logger, compilationState);
    return compilationState;
  }

  public synchronized String[] getEntryPointTypeNames() {
    final int n = entryPointTypeNames.size();
    return entryPointTypeNames.toArray(new String[n]);
  }

  public synchronized String getFunctionName() {
    return getName().replace('.', '_');
  }

  public Class<? extends Linker> getLinker(String name) {
    return linkerTypesByName.get(name);
  }

  public Map<String, Class<? extends Linker>> getLinkers() {
    return linkerTypesByName;
  }

  public synchronized String getName() {
    return nameOverride != null ? nameOverride : name;
  }

  /**
   * The properties that have been defined.
   */
  public synchronized Properties getProperties() {
    return properties;
  }

  public synchronized ResourceOracle getResourcesOracle() {
    if (lazyResourcesOracle == null) {
      lazyResourcesOracle = new ResourceOracleImpl(TreeLogger.NULL, resources);
      PathPrefixSet pathPrefixes = lazySourceOracle.getPathPrefixes();
      PathPrefixSet newPathPrefixes = new PathPrefixSet();
      for (PathPrefix pathPrefix : pathPrefixes.values()) {
        newPathPrefixes.add(new PathPrefix(pathPrefix.getPrefix(), NON_JAVA_RESOURCES, pathPrefix
            .shouldReroot()));
      }
      lazyResourcesOracle.setPathPrefixes(newPathPrefixes);
      ResourceOracleImpl.refresh(TreeLogger.NULL, lazyResourcesOracle);
    } else {
      doRefresh();
    }
    return lazyResourcesOracle;
  }

  /**
   * Gets a reference to the internal rules for this module def.
   */
  public synchronized Rules getRules() {
    return rules;
  }

  /**
   * Gets a reference to the internal scripts list for this module def.
   */
  public Scripts getScripts() {
    return scripts;
  }

  public synchronized String[] getServletPaths() {
    return servletClassNamesByPath.keySet().toArray(Empty.STRINGS);
  }

  /**
   * Gets a reference to the internal styles list for this module def.
   */
  public Styles getStyles() {
    return styles;
  }

  public boolean isGwtXmlFileStale() {
    return lastModified() > moduleDefCreationTime;
  }

  public boolean isInherited(String moduleName) {
    return inheritedModules.contains(moduleName);
  }

  public long lastModified() {
    long lastModified = 0;
    for (File xmlFile : gwtXmlFiles) {
      if (xmlFile.exists()) {
        lastModified = Math.max(lastModified, xmlFile.lastModified());
      }
    }
    return lastModified > 0 ? lastModified : moduleDefCreationTime;
  }

  /**
   * For convenience in unit tests, servlets can be automatically loaded and
   * mapped in the embedded web server. If a servlet is already mapped to the
   * specified path, it is replaced.
   * 
   * @param path the url path at which the servlet resides
   * @param servletClassName the name of the servlet to publish
   */
  public synchronized void mapServlet(String path, String servletClassName) {
    servletClassNamesByPath.put(path, servletClassName);
  }

  public synchronized void refresh() {
    needsRefresh = true;
  }

  /**
   * Mainly for testing and decreasing compile times.
   */
  public void setCollapseAllProperties(boolean collapse) {
    collapseAllProperties = collapse;
  }

  /**
   * Override the module's apparent name. Setting this value to
   * <code>null<code> will disable the name override.
   */
  public synchronized void setNameOverride(String nameOverride) {
    this.nameOverride = nameOverride;
  }

  void addCompilationUnitArchiveURL(URL url) {
    archiveURLs.add(url);
  }

  void addInteritedModule(String moduleName) {
    inheritedModules.add(moduleName);
  }

  /**
   * The final method to call when everything is setup. Before calling this
   * method, several of the getter methods may not be called. After calling this
   * method, the add methods may not be called.
   * 
   * @param logger Logs the activity.
   */
  synchronized void normalize(TreeLogger logger) {
    Event moduleDefNormalize =
        SpeedTracerLogger.start(CompilerEventType.MODULE_DEF, "phase", "normalize");
    // Normalize property providers.
    //
    for (Property current : getProperties()) {
      if (current instanceof BindingProperty) {
        BindingProperty prop = (BindingProperty) current;

        if (collapseAllProperties) {
          prop.addCollapsedValues("*");
        }

        prop.normalizeCollapsedValues();

        /*
         * Create a default property provider for any properties with more than
         * one possible value and no existing provider.
         */
        if (prop.getProvider() == null && prop.getConstrainedValue() == null) {
          String src = "{";
          src += "return __gwt_getMetaProperty(\"";
          src += prop.getName();
          src += "\"); }";
          prop.setProvider(new PropertyProvider(src));
        }
      }
    }

    // Create the public path.
    TreeLogger branch = Messages.PUBLIC_PATH_LOCATIONS.branch(logger, null);
    lazyPublicOracle = new ResourceOracleImpl(branch, resources);
    lazyPublicOracle.setPathPrefixes(publicPrefixSet);

    // Create the source path.
    branch = Messages.SOURCE_PATH_LOCATIONS.branch(logger, null);
    lazySourceOracle = new ResourceOracleImpl(branch, resources);
    lazySourceOracle.setPathPrefixes(sourcePrefixSet);

    needsRefresh = true;
    moduleDefNormalize.end();
  }

  private void checkForSeedTypes(TreeLogger logger, CompilationState compilationState)
      throws UnableToCompleteException {
    // Sanity check the seed types and don't even start it they're missing.
    boolean seedTypesMissing = false;
    TypeOracle typeOracle = compilationState.getTypeOracle();
    if (typeOracle.findType("java.lang.Object") == null) {
      CompilationProblemReporter.logMissingTypeErrorWithHints(logger, "java.lang.Object",
          compilationState);
      seedTypesMissing = true;
    } else {
      TreeLogger branch = logger.branch(TreeLogger.TRACE, "Finding entry point classes", null);
      String[] typeNames = getEntryPointTypeNames();
      for (int i = 0; i < typeNames.length; i++) {
        String typeName = typeNames[i];
        if (typeOracle.findType(typeName) == null) {
          CompilationProblemReporter.logMissingTypeErrorWithHints(branch, typeName,
              compilationState);
          seedTypesMissing = true;
        }
      }
    }

    if (seedTypesMissing) {
      throw new UnableToCompleteException();
    }
  }

  private synchronized void doRefresh() {
    if (!needsRefresh) {
      return;
    }
    Event moduleDefEvent =
        SpeedTracerLogger.start(CompilerEventType.MODULE_DEF, "phase", "refresh", "module",
            getName());
    // Refresh resource oracles.
    if (lazyResourcesOracle == null) {
      ResourceOracleImpl.refresh(TreeLogger.NULL, lazyPublicOracle, lazySourceOracle);
    } else {
      ResourceOracleImpl.refresh(TreeLogger.NULL, lazyPublicOracle, lazySourceOracle,
          lazyResourcesOracle);
    }
    moduleDefEvent.end();
    needsRefresh = false;
  }
}
