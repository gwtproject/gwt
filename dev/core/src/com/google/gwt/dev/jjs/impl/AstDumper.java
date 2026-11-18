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

package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.HasSourceInfo;
import com.google.gwt.dev.jjs.ast.Context;
import com.google.gwt.dev.jjs.ast.JClassType;
import com.google.gwt.dev.jjs.ast.JInterfaceType;
import com.google.gwt.dev.jjs.ast.JMethod;
import com.google.gwt.dev.jjs.ast.JNode;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.jjs.ast.JVisitor;
import com.google.gwt.dev.js.JsSourceGenerationVisitor;
import com.google.gwt.dev.js.ast.JsBlock;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsNode;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsSuperVisitor;
import com.google.gwt.dev.js.ast.JsVisitor;
import com.google.gwt.dev.util.PrintWriterTextOutput;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A simple utility to dump a JProgram or JsProgram to a temp file, which can be called
 * sequentially during a compilation/optimization run, so intermediate steps can be compared.
 * <p>
 * Uses the system property {@code gwt.jjs.dumpAst} to determine the name (or prefix) of the file
 * to dump the AST to. Output can be filtered to specific source files with the system property
 * {@code gwt.jjs.dumpAst.filter}, which is a comma-separated list of source file names, optionally
 * suffixed with {@code :{startLine}-{endLine}} to only include lines in that range. Those lines
 * will then be printed, regardless of which method/type they are migrated to during the course of
 * compilation.
 *
 * <p>
 * TODO(jbrosenberg): Add proper logging and/or exception handling for the
 * potential IOException that might occur when writing the file.
 */
public class AstDumper {

  private static int autoVersionNumber = 0;

  /**
   * Appends a new version of the Java AST at the end of the file, each time its called.
   */
  public static void maybeDumpAST(JProgram jprogram) {
    maybeDumpAST(jprogram, null, true);
  }

  /**
   * Appends a new version of the JS AST to the end of the file each time its called.
   */
  public static void maybeDumpAST(JsProgram jsProgram) {
    maybeDumpAST(jsProgram, null, true);
  }

  /**
   * Writes the AST to the file with a versioned extension, using an
   * auto-incrementing version number (starting from 1), each time it's called.
   * Any previous contents of the file written to will be overwritten.
   */
  public static void maybeDumpAST(JProgram jprogram, boolean autoIncrementVersion) {
    if (!autoIncrementVersion) {
      maybeDumpAST(jprogram);
    } else {
      maybeDumpAST(jprogram, autoVersionNumber++);
    }
  }

  /**
   * Writes the AST to the file with the provided version number extension. Any
   * previous contents of the file written to will be overwritten.
   */
  public static void maybeDumpAST(JProgram jprogram, int versionNumber) {
    String fileExtension = "." + versionNumber;
    maybeDumpAST(jprogram, fileExtension, false);
  }

  /**
   * Writes the AST to the file with the provided version string extension. Any
   * previous contents of the file written to will be overwritten.
   */
  public static void maybeDumpAST(JProgram jprogram, String versionString) {
    String fileExtension = "." + versionString;
    maybeDumpAST(jprogram, fileExtension, false);
  }

  private static void maybeDumpAST(JProgram jprogram, String fileExtension, boolean append) {
    String dumpFile = System.getProperty("gwt.jjs.dumpAst");
    if (dumpFile == null) {
      return;
    }
    final Set<FilterRange> files = getFilter();
    try (PrintWriterTextOutput out = createWriter(dumpFile, fileExtension, append)) {
      JVisitor v = new SourceGenerationVisitor(out);
      if (files != null) {
        v = new JFilteredAstVisitor(v, files);
      }

      v.accept(jprogram);
    } catch (Exception e) {
      System.out.println("Could not dump AST");
      e.printStackTrace();
    }
  }

  private static void maybeDumpAST(JsProgram jsProgram, String fileExtension, boolean append) {
    String dumpFile = System.getProperty("gwt.jjs.dumpAst");
    if (dumpFile == null) {
      return;
    }
    final Set<FilterRange> files = getFilter();
    try (PrintWriterTextOutput out = createWriter(dumpFile, fileExtension, append)) {
      JsVisitor v = new JsSourceGenerationVisitor(out);
      if (files != null) {
        v = new JsFilteredAstVisitor(v, files);
      }

      v.accept(jsProgram);
    } catch (Exception e) {
      System.out.println("Could not dump AST");
      e.printStackTrace();
    }
  }

  private static Set<FilterRange> getFilter() {
    String dumpFilter = System.getProperty("gwt.jjs.dumpAst.filter");
    final Set<FilterRange> files;
    if (dumpFilter != null) {
      files = Arrays.stream(dumpFilter.split(","))
          .map(String::trim)
          .map(FilterRange::new)
          .collect(Collectors.toSet());
    } else {
      files = null;
    }
    return files;
  }

  private static PrintWriterTextOutput createWriter(String dumpFile, String fileExtension,
                                                    boolean append) throws IOException {
    if (fileExtension != null) {
      dumpFile += fileExtension;
    }
    final FileOutputStream os = new FileOutputStream(dumpFile, append);
    final PrintWriter pw = new PrintWriter(os);
    return new PrintWriterTextOutput(pw, false);
  }

  private static class FilterRange {
    private final String fileName;
    private final int startLine;
    private final int endLine;

    private FilterRange(String directive) {
      if (directive.contains(":")) {
        String[] parts = directive.split(":");
        this.fileName = parts[0];
        String[] lineParts = parts[1].split("-");
        this.startLine = Integer.parseInt(lineParts[0]);
        if (lineParts.length > 1) {
          this.endLine = Integer.parseInt(lineParts[1]);
        } else {
          this.endLine = this.startLine;
        }
      } else {
        this.fileName = directive;
        this.startLine = 0;
        this.endLine = Integer.MAX_VALUE;
      }
    }

    public boolean matches(HasSourceInfo node) {
      String nodeFile = node.getSourceInfo().getFileName();
      int nodeLine = node.getSourceInfo().getStartLine();
      return nodeFile.equals(fileName) && nodeLine >= startLine && nodeLine <= endLine;
    }
  }

  /**
   * Filters the AST by type, method to determine what the delegate can see, by checking if any
   * code is present from the requested files. Any class/interface that needs to be written will be
   * visited directly (only via visit/endVisit, not accept/traverse), and only after at least one
   * member is found that needs to be visited, and members will be then be handled by accept() to
   * limit the type to only those members which match the specified filter.
   */
  private static class JFilteredAstVisitor extends JVisitor {
    private final JVisitor delegate;
    private final Set<FilterRange> sourceFiles;

    private JClassType currentClass = null;
    private JInterfaceType currentInterface = null;
    private boolean visitedCurrentType = false;
    private boolean shouldVisitCurrentMethod = false;

    private JFilteredAstVisitor(JVisitor delegate, Set<FilterRange> sourceFiles) {
      this.delegate = delegate;
      this.sourceFiles = sourceFiles;
    }

    @Override
    public boolean visit(JNode x, Context ctx) {
      shouldVisitCurrentMethod |= sourceFiles.stream().anyMatch(s -> s.matches(x));

      // If we're already looking at this method, no need to keep checking
      return !shouldVisitCurrentMethod;
    }

    @Override
    public boolean visit(JInterfaceType x, Context ctx) {
      currentInterface = x;
      visitedCurrentType = false;
      return true;
    }

    @Override
    public void endVisit(JInterfaceType x, Context ctx) {
      if (visitedCurrentType) {
        delegate.endVisit(x, ctx);
      }
      currentInterface = null;
    }

    @Override
    public boolean visit(JClassType x, Context ctx) {
      currentClass = x;
      visitedCurrentType = false;
      return true;
    }

    @Override
    public void endVisit(JClassType x, Context ctx) {
      if (visitedCurrentType) {
        delegate.endVisit(x, ctx);
      }
      currentClass = null;
    }

    @Override
    public boolean visit(JMethod x, Context ctx) {
      // Reset for each method
      shouldVisitCurrentMethod = sourceFiles.stream().anyMatch(s -> s.matches(x));

      // If we're already looking at this method, no need to keep checking
      return !shouldVisitCurrentMethod;
    }

    @Override
    public void endVisit(JMethod x, Context ctx) {
      if (shouldVisitCurrentMethod) {
        if (!visitedCurrentType) {
          if (currentClass != null) {
            delegate.visit(currentClass, ctx);
          } else {
            assert currentInterface != null;
            delegate.visit(currentInterface, ctx);
          }
          visitedCurrentType = true;
        }

        delegate.accept(x);
        shouldVisitCurrentMethod = false;
      }
    }

    @Override
    public boolean visit(JProgram x, Context ctx) {
      // As SourceGenerationVisitor visits all types (not just ones that are reference-only),
      // we need to as well.
      for (int i = 0; i < x.getDeclaredTypes().size(); i++) {
        accept(x.getDeclaredTypes().get(i));
      }
      return false;
    }
  }

  /**
   * Filters the AST by top-level node to determine what the delegate can see, by checking if any
   * code is present from the requested files. Top level nodes are usually functions are class
   * declaration code.
   */
  private static class JsFilteredAstVisitor extends JsSuperVisitor {
    private final JsVisitor delegate;
    private final Set<FilterRange> sourceFiles;

    private boolean shouldVisitCurrentTopLevelStatement = false;

    private JsFilteredAstVisitor(JsVisitor delegate, Set<FilterRange> sourceFiles) {
      this.delegate = delegate;
      this.sourceFiles = sourceFiles;
    }

    @Override
    public boolean visit(JsNode x, JsContext ctx) {
      return test(x);
    }

    /**
     * Tests if we should print the node and its surrounding top-level statement. Returns true
     * if we should continue visiting children, false to skip them (because we already know we
     * will print contents).
     */
    private boolean test(JsNode node) {
      shouldVisitCurrentTopLevelStatement |= sourceFiles.stream().anyMatch(s -> s.matches(node));

      return !shouldVisitCurrentTopLevelStatement;
    }

    @Override
    public boolean visit(JsBlock x, JsContext ctx) {
      if (x.isGlobalBlock()) {
        // Iterate children directly, so we can track what is top-level or not
        for (JsNode child : x.getStatements()) {
          accept(child);

          // If any part of that node should be visited, pass to the delegate and reset
          if (shouldVisitCurrentTopLevelStatement) {
            delegate.accept(child);

            // Reset after each top-level statement
            shouldVisitCurrentTopLevelStatement = false;
          }
        }
        return false;
      }
      return test(x);
    }
  }
}
