package com.google.gwt.user.tools;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.ArgProcessorBase;
import com.google.gwt.dev.cfg.ModuleDefLoader;
import com.google.gwt.dev.resource.impl.PathPrefix;
import com.google.gwt.dev.resource.impl.PathPrefixSet;
import com.google.gwt.dev.resource.impl.ResourceFilter;
import com.google.gwt.dev.resource.impl.ResourceOracleImpl;
import com.google.gwt.dev.util.arg.ArgHandlerLogLevel;
import com.google.gwt.dev.util.arg.ArgHandlerModuleName;
import com.google.gwt.dev.util.arg.OptionLogLevel;
import com.google.gwt.dev.util.arg.OptionModuleName;
import com.google.gwt.dev.util.log.PrintWriterTreeLogger;
import com.google.gwt.thirdparty.guava.common.base.Charsets;
import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableSet;
import com.google.gwt.thirdparty.guava.common.collect.SetMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Sets;
import com.google.gwt.thirdparty.guava.common.io.Files;
import com.google.gwt.util.tools.ArgHandlerFile;
import com.google.gwt.util.tools.ArgHandlerFlag;
import com.google.gwt.util.tools.Utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class GraphDependencies {

  private static final ResourceFilter MODULE_RESOURCES = new ResourceFilter() {
    @Override
    public boolean allows(String path) {
      return path.endsWith(ModuleDefLoader.GWT_MODULE_XML_SUFFIX);
    }
  };

  static class ArgProcessor extends ArgProcessorBase {
    public ArgProcessor(GraphDependenciesOptions options) {
      registerHandler(new ArgHandlerLogLevel(options));
      registerHandler(new ArgHandlerAllModules(options));
      registerHandler(new ArgHandlerOutFile(options));
      registerHandler(new ArgHandlerModuleName(options) {
        @Override
        public boolean isRequired() {
          return false;
        }
      });
    }

    @Override
    protected String getName() {
      return GraphDependencies.class.getName();
    }
  }

  private static class ArgHandlerAllModules extends ArgHandlerFlag {
    private final GraphDependenciesOptions options;

    private ArgHandlerAllModules(GraphDependenciesOptions options) {
      this.options = options;
    }

    @Override
    public String getTag() {
      return "-all";
    }

    @Override
    public String getPurpose() {
      return "Process all modules on the classpath.";
    }

    @Override
    public boolean setFlag() {
      options.setAllModules(true);
      return true;
    }
  }

  private static class ArgHandlerOutFile extends ArgHandlerFile {
    private final GraphDependenciesOptions options;

    public ArgHandlerOutFile(GraphDependenciesOptions options) {
      this.options = options;
    }

    @Override
    public String getTag() {
      return "-out";
    }

    @Override
    public String getPurpose() {
      return "Output file";
    }

    @Override
    public void setFile(File file) {
      options.setOutFile(file);
    }
  }

  public interface GraphDependenciesOptions extends OptionModuleName, OptionLogLevel {

    boolean getAllModules();

    void setAllModules(boolean allModules);

    File getOutFile();

    void setOutFile(File file);
  }

  static class GraphDependenciesOptionsImpl implements GraphDependenciesOptions {
    private TreeLogger.Type logLevel;
    private boolean allModules;
    private File outFile;
    private List<String> moduleNames = new ArrayList<String>();

    public GraphDependenciesOptionsImpl() {
    }

    public GraphDependenciesOptionsImpl(GraphDependenciesOptions other) {
      copyFrom(other);
    }

    public void copyFrom(GraphDependenciesOptions other) {
      setLogLevel(other.getLogLevel());
      setAllModules(other.getAllModules());
      setOutFile(other.getOutFile());
      setModuleNames(other.getModuleNames());
    }

    @Override
    public List<String> getModuleNames() {
      return Collections.unmodifiableList(moduleNames);
    }

    @Override
    public void addModuleName(String moduleName) {
      moduleNames.add(moduleName);
    }

    @Override
    public void setModuleNames(List<String> moduleNames) {
      this.moduleNames = new ArrayList<String>(moduleNames);
    }

    @Override
    public boolean getAllModules() {
      return allModules;
    }

    @Override
    public void setAllModules(boolean allModules) {
      this.allModules = allModules;
    }

    @Override
    public File getOutFile() {
      return outFile;
    }

    public void setOutFile(File file) {
      this.outFile = file;
    };

    @Override
    public Type getLogLevel() {
      return logLevel;
    }

    @Override
    public void setLogLevel(Type logLevel) {
      this.logLevel = logLevel;
    }
  }

  public static void main(String[] args) {
    GraphDependenciesOptions options = new GraphDependenciesOptionsImpl();

    if (new ArgProcessor(options).processArgs(args)) {
      PrintWriterTreeLogger logger = new PrintWriterTreeLogger();
      logger.setMaxDetail(options.getLogLevel());

      if (!new GraphDependencies(options).run(logger)) {
        System.exit(0);
      }
    }

    System.exit(1);
  }

  private final GraphDependenciesOptions options;
  private final XMLInputFactory factory;

  public GraphDependencies(GraphDependenciesOptions options) {
    this.options = new GraphDependenciesOptionsImpl(options);

    factory = XMLInputFactory.newFactory();
    factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
    factory.setProperty(XMLInputFactory.IS_VALIDATING, false);
    factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
  }

  private boolean run(TreeLogger logger) {
    HashMultimap<String, String> modules = HashMultimap.create();

    try {
      if (options.getAllModules()) {
        logger.log(TreeLogger.INFO, "Scanning the classpath for modules");

        PathPrefixSet pathPrefixSet = new PathPrefixSet();
        pathPrefixSet.add(new PathPrefix("", MODULE_RESOURCES));
        ResourceOracleImpl oracle = new ResourceOracleImpl(logger);
        oracle.setPathPrefixes(pathPrefixSet);

        ResourceOracleImpl.refresh(logger, oracle);

        for (String modulePath : oracle.getPathNames()) {
          String moduleName =
              modulePath.substring(0, modulePath.length()
                  - ModuleDefLoader.GWT_MODULE_XML_SUFFIX.length());
          moduleName = moduleName.replace('/', '.');
          processModule(logger, moduleName, modulePath, modules);
        }
      } else {
        for (String moduleName : options.getModuleNames()) {
          processModule(logger, moduleName, modules);
        }
      }
    } catch (UnableToCompleteException e) {
      return false;
    }

    TreeLogger branch = logger.branch(TreeLogger.INFO, "Writing Dot file");

    PrintWriter writer = null;
    try {
      Files.createParentDirs(options.getOutFile());
      writer = new PrintWriter(Files.newWriter(options.getOutFile(), Charsets.UTF_8));

      writer.println("digraph {");

      // All modules (including those without dependencies)
      for (String module : modules.keySet()) {
        writer.println(String.format("\"%s\" ;", module));

        for (String dependency : modules.get(module)) {
          writer.println(String.format("\"%s\" -> \"%s\" ;", module, dependency));
        }

        writer.println();
      }

      writer.println("// Modules without dependencies");
      for (String module : Sets.difference(ImmutableSet.copyOf(modules.values()), modules.keySet())) {
        writer.println(String.format("\"%s\" ;", module));
      }

      writer.println("}");

    } catch (IOException e) {
      branch.log(TreeLogger.ERROR, e.getMessage(), e);
      return false;
    } finally {
      Utility.close(writer);
    }

    return true;
  }

  private void processModule(TreeLogger logger, String moduleName,
      SetMultimap<String, String> modules) throws UnableToCompleteException {
    String modulePath = moduleName.replace('.', '/') + ModuleDefLoader.GWT_MODULE_XML_SUFFIX;
    processModule(logger, moduleName, modulePath, modules);
  }

  private void processModule(TreeLogger logger, String moduleName, String modulePath,
      SetMultimap<String, String> modules) throws UnableToCompleteException {
    TreeLogger branch = logger.branch(TreeLogger.INFO, "Processing module " + moduleName);

    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(modulePath);
    if (is == null) {
      branch.log(TreeLogger.ERROR, "Resource not found: " + modulePath);
      return;
    }

    XMLStreamReader reader = null;
    try {
      reader = factory.createXMLStreamReader(is);

      reader.nextTag();
      reader.require(XMLStreamReader.START_ELEMENT, null, "module");

      while (reader.hasNext()) {
        reader.nextTag();

        if (reader.isStartElement()) {
          if ("inherits".equals(reader.getLocalName())) {
            String inheritedModule = reader.getAttributeValue(null, "name");
            boolean added = modules.put(moduleName, inheritedModule);
            if (added) {
              processModule(branch, inheritedModule, modules);
            } else {
              branch.log(TreeLogger.INFO, "Skipping " + inheritedModule + " (already processed)");
            }
          }
          skip(reader);
        } else if (reader.isEndElement()) {
          reader.require(XMLStreamReader.END_ELEMENT, null, "module");
          break;
        }
      }
    } catch (XMLStreamException e) {
      branch.log(TreeLogger.ERROR, e.getMessage(), e);
      throw new UnableToCompleteException();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (XMLStreamException e) {
          branch.log(TreeLogger.ERROR, "Error closing XML reader", e);
        }
      }
      Utility.close(is);
    }
  }

  private void skip(XMLStreamReader reader) throws XMLStreamException {
    reader.require(XMLStreamReader.START_ELEMENT, null, null);

    String localName = reader.getLocalName();
    int depth = 1; // start at 1: we "enter" the element

    while (depth > 0) {
      int eventType = reader.next();
      if (eventType == XMLStreamReader.START_ELEMENT) {
        depth++;
      } else if (eventType == XMLStreamReader.END_ELEMENT) {
        depth--;
      }
    }

    reader.require(XMLStreamReader.END_ELEMENT, null, localName);
  }
}
