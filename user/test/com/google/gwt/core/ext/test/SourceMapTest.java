/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.core.ext.test;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.linker.CastableTypeMap;
import com.google.gwt.core.ext.linker.SymbolData;
import com.google.gwt.core.ext.soyc.SourceMapRecorder;
import com.google.gwt.core.ext.soyc.coderef.ClassDescriptor;
import com.google.gwt.core.ext.soyc.coderef.EntityDescriptor;
import com.google.gwt.core.ext.soyc.coderef.EntityDescriptor.Fragment;
import com.google.gwt.core.ext.soyc.coderef.EntityDescriptorJsonTranslator;
import com.google.gwt.core.ext.soyc.coderef.MethodDescriptor;
import com.google.gwt.dev.CompilerOptionsImpl;
import com.google.gwt.dev.util.Util;
import com.google.gwt.dev.util.log.PrintWriterTreeLogger;
import com.google.gwt.thirdparty.debugging.sourcemap.FilePosition;
import com.google.gwt.thirdparty.debugging.sourcemap.SourceMapConsumerV3;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Sets;
import com.google.gwt.thirdparty.guava.common.primitives.Ints;
import com.google.gwt.util.tools.Utility;

import junit.framework.TestCase;

import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Basic tests for Source maps and (new) soyc reports.
 *
 * @author ocallau@google.com (Oscar Callau)
 */
public class SourceMapTest extends TestCase {

  static String stringContent(File filePath) throws IOException {
    FileReader reader = new FileReader(filePath);
    char[] content = new char[(int) filePath.length()];
    reader.read(content);
    reader.close();
    return new String(content);
  }

  static File[] filterByName(File dir, String regexPattern) {
    final String regex = regexPattern;
    return dir.listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        return pathname.getName().matches(regex);
      }
    });
  }

  /**
   * This class represents each row in a generated SymbolMap file.  Because not all fields are
   * serialized, such as CastableTypeMap, some methods are not implemented.
   *
   */
  static final class SimpleSymbolData implements SymbolData {

    static Map<String, SimpleSymbolData> readSymbolMap(File filePath) throws IOException {
      Map<String, SimpleSymbolData> sdata = Maps.newLinkedHashMap();

      BufferedReader reader = new BufferedReader(new FileReader(filePath));
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("#")) {
          // reading a comment
          continue;
        }

        SimpleSymbolData symbolData = new SimpleSymbolData(line);
        String key = symbolData.getJsniIdent();
        if (sdata.containsKey(key)) {
          throw new IOException("Duplicate jsni: " + key);
        }
        sdata.put(key,symbolData);
      }

      return sdata;
    }

    private static final String NOT_IMPLEMENTED_MESSAGE =
        "Data not available on current serialized SymbolMap";

    private String jsName;
    private String jsniIdent;
    private String className;
    private String memberName;
    private String sourceUri;
    private int sourceLine;
    private int fragmentNumber;
    // counting how many times it was found in the symbol map table
    private int counter = 0;


    public SimpleSymbolData(String line) {
      this.parseFromLine(line);
    }

    public int getCounter() {
      return counter;
    }

    @Override
    public CastableTypeMap getCastableTypeMap() {
      return null;
    }

    @Override
    public String getClassName() {
      return this.className;
    }

    @Override
    public int getFragmentNumber() {
      return this.fragmentNumber;
    }

    @Override
    public String getJsniIdent() {
      return this.jsniIdent;
    }

    @Override
    public String getMemberName() {
      return this.memberName;
    }

    @Override
    public int getQueryId() {
      throw new ShouldNotImplement(NOT_IMPLEMENTED_MESSAGE);
    }

    @Override
    public int getSeedId() {
      throw new ShouldNotImplement(NOT_IMPLEMENTED_MESSAGE);
    }

    @Override
    public int getSourceLine() {
      return this.sourceLine;
    }

    @Override
    public String getSourceUri() {
      return this.sourceUri;
    }

    @Override
    public String getSymbolName() {
      return this.jsName;
    }

    @Override
    public boolean isClass() {
      return this.memberName == null || this.memberName.isEmpty();
    }

    @Override
    public boolean isField() {
      return !this.isClass() && jsniIdent.indexOf("(") < 0;
    }

    @Override
    public boolean isMethod() {
      return !this.isClass() && jsniIdent.indexOf("(") >= 0;
    }

    public int incCounter() {
      return ++counter;
    }

    public String toString() {
      return jsniIdent + " -> " + jsName;
    }

    private void parseFromLine(String line) {
      String[] fields = line.split(",");

      this.jsName = fields[0];
      this.jsniIdent = fields[1].isEmpty() ? fields[2] : fields[1];
      this.className = fields[2];
      this.memberName = fields[3]; // may be empty
      this.sourceUri = fields[4];
      this.sourceLine = Integer.parseInt(fields[5]);
      this.fragmentNumber = Integer.parseInt(fields[6]);
    }
  }

  private final CompilerOptionsImpl options = new CompilerOptionsImpl();
  // maps permutationId to symbolMap content
  private Map<Integer, Map<String, SimpleSymbolData>> mapping =
      Maps.newHashMap();

  private void checkSourceMap(File symbolMap, List<File> sourceMapFiles)
      throws Exception {
    final Map<String, SimpleSymbolData> symbolTable = SimpleSymbolData.readSymbolMap(symbolMap);
    boolean firstIteration = true;
    // final Set<String> ls = new HashSet<String>();
    // final Set<String> ms = new HashSet<String>();
    for (File sourceMapFile : sourceMapFiles) {
      SourceMapConsumerV3 sourceMap = new SourceMapConsumerV3();
      sourceMap.parse(stringContent(sourceMapFile));
      if (firstIteration) {
        mapping.put((Integer) sourceMap.getExtensions().get(SourceMapRecorder.PERMUTATION_EXT),
            symbolTable);
        firstIteration = false;
      }
      sourceMap.visitMappings(new  SourceMapConsumerV3.EntryVisitor() {
        @Override
        public void visit(String sourceName, String symbolName,
            FilePosition srcStartPos, FilePosition startPosition,FilePosition endPosition) {
          if (symbolName == null || symbolName.isEmpty()) {
            return;
          }
          SimpleSymbolData symbolData = symbolTable.get(symbolName);
          if (symbolData == null) {
            // ls.add(symbolName); //in:sourcemap   not-in:symbolmap
            return;
          }
          symbolData.incCounter();
          // field declarations will work, but field accesses wont
          if (!symbolData.isField()) {
            assertEquals(symbolData.getSourceUri(), sourceName);
            if (symbolData.isClass()) {
              if (symbolData.getFragmentNumber() >= 0) {
                assertEquals(symbolData.getSourceLine() - 1, srcStartPos.getLine());
              } // Some classes on fragment -1 (interfaces) wont work.
            } else {
              if (symbolData.getCounter() <= 1) {
                assertTrue(symbolData.getSourceLine() - srcStartPos.getLine() < 2);
              }
            }
            // Some methods wont work on source line. They were generated from the
            // SourceInfo with JsName axis
          }
        }
      });
    }

    /* TODO (ocallau) to be removed, useful only for debugging as well as some commented code above
    ArrayList<SimpleSymbolData> ss =new ArrayList<SimpleSymbolData>();
    for ( SimpleSymbolData b : symbolTable.values()){
      if (b.getCounter() == 0) ss.add(b); //not-in: sourcemap       in:symbolmap
    }

    String[] sorted_ls = new String[ls.size()];
    ls.toArray(sorted_ls);
    Arrays.sort(sorted_ls);*/
  }

  private void testSymbolMapsCorrespondence(File root) throws Exception {
    // Testing SourceMaps as SymbolMap replacement
    // make sure the files have been produced
    assertTrue(root.exists());

    File[] symbolMapFiles = filterByName(root, "(.*)\\.symbolMap")
        ,  sourceMapFiles = filterByName(root, "(.*)_sourceMap(\\d+)\\.json");
    // At least there is a source map file for each symbol map file
    assertTrue(symbolMapFiles.length <= sourceMapFiles.length);

    List<List<File>> sourceMapSets = Lists.newArrayList();
    for (int i = 0; i < symbolMapFiles.length; i++) {
      String name = symbolMapFiles[i].getName().split("\\.")[0];
      List<File> set =  Lists.newArrayList();
      for (File sourceMap : sourceMapFiles) {
        if (sourceMap.getName().startsWith(name)) {
          set.add(sourceMap);
        }
      }
      assertTrue(set.size() >= 1);
      sourceMapSets.add(set);
    }
    for (int i = 0; i < symbolMapFiles.length; i++) {
      checkSourceMap(symbolMapFiles[i], sourceMapSets.get(i));
    }
  }

  private void testSoycCorrespondence(File root) throws Exception {
    // Testing SourceMap as Soyc reports replacements
    assertTrue(root.exists());

    for (Integer permutation : mapping.keySet()) {

      checkSplitPloints(
          new File(root.getPath() + "/splitPoints" + permutation + ".xml.gz"),
          new File(root.getPath() + "/" + SourceMapRecorder.SPLIT_POINTS_NAME + permutation
              + ".json"));
      checkEntities(
          new File(root.getPath() + "/stories" + permutation + ".xml.gz"),
          new File(root.getPath() + "/dependencies" + permutation + ".xml.gz"),
          mapping.get(permutation),
          new File(root.getPath() + "/" + EntityDescriptorJsonTranslator.ARTIFACT_NAME + permutation
              + ".json"));
    }
  }

  private void checkEntities(File sizeMap, File dependency,
      Map<String, SimpleSymbolData> symbolTable, File entitiesFile)
      throws Exception {
    Map<String, ClassDescriptor> clsMap =
          EntityDescriptorJsonTranslator.readJson(new JSONObject(stringContent(entitiesFile)))
              .flatClasses();
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

    parser.reset();
    parser.parse(new GZIPInputStream(new FileInputStream(sizeMap)), checkStories(clsMap));

    parser.reset();
    parser.parse(new GZIPInputStream(new FileInputStream(dependency)), checkDependencies(clsMap));

    checkSymbols(symbolTable, clsMap);
  }

  private void checkSymbols(Map<String, SimpleSymbolData> symbolTable,
      Map<String, ClassDescriptor> clsMap) {
    // List<SimpleSymbolData> ls = Lists.newArrayList();
    for (SimpleSymbolData symbol : symbolTable.values()) {
      if (symbol.getClassName().endsWith("[]")) {
        // Arrays aren't stored, because they are not entities, ie definable piece of code
        continue;
      }
      ClassDescriptor cls = clsMap.get(symbol.getClassName());
      // assertNotNull(cls);
      if (cls == null) {
        /* TODO(ocallau) Needs to investigate why, may be checking in savedSource
        ls.add(symbol);*/
        continue;
      }
      if (symbol.isClass()) {
        assertEquals(symbol.getSymbolName(), cls.getObfuscatedName());
      } else if (symbol.isField()){
        assertEquals(symbol.getSymbolName(),
            cls.getField(symbol.getMemberName()).getObfuscatedName());

      } else {
        // method
        MethodDescriptor mth = cls.getMethod(
            unSynthMethodSignature(symbol.getJsniIdent().split("::")[1]));
        assertTrue(mth.getAllObfuscatedNames().contains(symbol.getSymbolName()));
        // symbol.getSymbolName();
      }
    }
    /* TODO (ocallau) to be removed, useful only for debugging as well as some commented code above
    ls.size();*/
  }

  private String unSynthMethodSignature(String mthSignature) {
    if (mthSignature.startsWith("$") &&
        !mthSignature.startsWith("$init()") &&
        !mthSignature.startsWith("$clinit()")) {
      return mthSignature.replaceFirst("L[^;\\(]*;","").substring(1);
    }
    return mthSignature;
  }

  private DefaultHandler checkDependencies(final Map<String, ClassDescriptor> clsMap) {
    return new DefaultHandler() {
      Set<Integer> currentDependencies = Sets.newHashSet();
      // mName is just the method name, not a complete signature
      String methodName(String mName){
        if (mName.startsWith("$") && !mName.equals("$init") && !mName.equals("$clinit")) {
          return mName.substring(1);
        }
        return mName;
      }

      boolean compareMethodNames(String strictName, String relaxName) {
        if (strictName.equals(relaxName)) {
          return true;
        }
        // only $init and $clinit will match the below if
        if (relaxName.startsWith("$")) {
          return strictName.equals(relaxName.substring(1));
        }
        return false;
      }

      @Override
      public void startElement(String uri, String localName, String qName, Attributes attributes)
          throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        // "name"/"by" attributes wont include the signature, just the method name
        if (qName.equals("method")) {
          currentDependencies.clear();
          String[] fullName = attributes.getValue("name").split("::");
          for (MethodDescriptor method : clsMap.get(fullName[0]).getMethods()) {
            if (compareMethodNames(method.getName(), methodName(fullName[1]))) {
              currentDependencies.addAll(Ints.asList(method.getDependantPointers()));
            }
          }
          assertTrue(currentDependencies.size() > 0);
        } else if (qName.equals("called")) {
          assertTrue(currentDependencies.size() > 0);

          String[] fullName = attributes.getValue("by").split("::");
          boolean present = false;
          for (MethodDescriptor method : clsMap.get(fullName[0]).getMethods()) {
            if (compareMethodNames(method.getName(), methodName(fullName[1]))) {
              if (currentDependencies.contains(method.getUniqueId())) {
                present = true;
                break;
              }
            }
          }
          // We cannot do much, because of the orig dependencies.xml format impressions
          assertTrue(present);
        }
      }
    };
  }

  private DefaultHandler checkStories(final Map<String, ClassDescriptor> clsMap) {
    return new DefaultHandler() {
      int fragment = -1;
      @Override
      public void startElement(String uri, String localName, String qName, Attributes attributes)
          throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (qName.equals("sizemap")){
          fragment = Integer.parseInt(attributes.getValue("fragment"));
        } else if (qName.equals("size")) {
          assertTrue(fragment > -1);
          // <size type="type" ref="com.google.gwt.core.client.JavaScriptException" size="25"/>
          // eg. com.google.gwt.core.client.JavaScriptException::$clinit()V
          // type := type | method | field | string | var
          String kind = attributes.getValue("type");
          int size = Integer.parseInt(attributes.getValue("size"));
          String ref = attributes.getValue("ref");
          if (kind.equals("type")) {
            checkInFragments(size, clsMap.get(ref).getFragments());
          } else if (kind.equals("method")) {
            String[] fullName = ref.split("::");
            checkInFragments(size,
                clsMap.get(fullName[0]).
                    getMethod(unSynthMethodSignature(fullName[1])).getFragments());
          } else if (kind.equals("field")) {
            String[] fullName = ref.split("::");
            checkInFragments(size,
                clsMap.get(fullName[0]).getField(fullName[1]).getFragments());
          }
          // var and string are not recorded in entities
        }
      }

      // Checks that current fragment and size are in the list
      private void checkInFragments(int size, List<Fragment> fragments) {
        for (EntityDescriptor.Fragment frag : fragments) {
          if (frag.id == fragment &&
              frag.size == size) {
            return;
          }
        }
        fail("Fragment <" + fragment + "> and size <" + size  + "> don't match");
      }
    };
  }

  private void checkSplitPloints(File origSplitPoints, File splitPointsFile)
      throws Exception {
    JSONObject jsPoints = new JSONObject(stringContent(splitPointsFile));
    final JSONArray initSeq = (JSONArray) jsPoints.get("initialSequence");
    final JSONArray spoints = (JSONArray) jsPoints.get("splitPoints");
    // ocallau: Considering stable order. May be this is too strict, in that case, we need to
    // store the elements in a list and provide a search method
    SAXParserFactory.newInstance().newSAXParser().parse(
        new GZIPInputStream(new FileInputStream(origSplitPoints)),
        new DefaultHandler() {
      int spIdx = 0;
      int isIdx = 0;
      @Override
      public void startElement(String uri, String localName, String qName, Attributes attributes)
          throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        try {
          if (localName.equals("splipoint")) {
            JSONObject splitPoint = spoints.getJSONObject(spIdx++);
            assertEquals(Integer.parseInt(attributes.getValue("id")), splitPoint.getInt("id"));
            assertEquals(attributes.getValue("location"), splitPoint.getString("location"));
          } else if (localName.equals("splitpointref")) {
            assertEquals(Integer.parseInt(attributes.getValue("id")), initSeq.getInt(isIdx++));
          }
        } catch (JSONException ex) {
          fail(ex.getMessage());
        }
      }
    });
  }

  public void testSourceMapT() throws Exception {
    String benchmark = "hello";
    String module = "com.google.gwt.sample.hello.Hello";

    File work = Utility.makeTemporaryDirectory(null, benchmark + "work");
    try {
      options.setSoycEnabled(true);
      options.addModuleName(module);
      options.setWarDir(new File(work, "war"));
      options.setExtraDir(new File(work, "extra"));
      PrintWriterTreeLogger logger = new PrintWriterTreeLogger();
      logger.setMaxDetail(TreeLogger.ERROR);
      new com.google.gwt.dev.Compiler(options).run(logger);
      // Change parentDir for cached/pre-built reports
      String parentDir = options.getExtraDir() + "/" + benchmark;
      testSymbolMapsCorrespondence(new File(parentDir + "/symbolMaps/"));
      testSoycCorrespondence(new File(parentDir + "/soycReport/"));

    } finally {
      Util.recursiveDelete(work, false);
    }
  }
}