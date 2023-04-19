/*
 * Copyright 2006 Google Inc.
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
package com.google.doctool.custom;

import com.sun.source.doctree.DocTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.DocTreePath;
import com.sun.source.util.DocTrees;

import java.io.IOException;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * A taglet for slurping examples into javadoc output.
 */
public class ExampleTaglet extends AbstractTaglet {

  @Override
  public String getName() {
    return "example";
  }

  @Override
  public String toString(List<? extends DocTree> list, Element element) {
    StringBuilder results = new StringBuilder();
    DocTrees trees = env.getDocTrees();
    for (DocTree tag : list) {
      String linkText = getHtmlContent(tag);

      // Using the linktext and the current element as context, find the referenced Element if any
      final Element targetElement = trees.getElement(
              new DocTreePath(
                      new DocTreePath(
                              trees.getPath(element),
                              trees.getDocCommentTree(element)
                      ),
                      trees.getDocTreeFactory().newReferenceTree(linkText)
              )
      );
      if (targetElement == null) {
        String message = "Unable to resolve " + linkText + " found in javadoc for " + element;
        printMessage(Diagnostic.Kind.ERROR, message, element, tag);
        // return empty so the docs continue, fail
        return "";
      }

      // having found the specified element, get its position in the source file
      Tree tree = trees.getTree(targetElement);

      // find the compilation unit that was contains this
      CompilationUnitTree cu = trees.getPath(targetElement).getCompilationUnit();
      try (Reader reader = cu.getSourceFile().openReader(false)) {
        long startPosition = trees.getSourcePositions().getStartPosition(cu, tree);

        String slurpSource = slurpSource(reader, startPosition);
        // The <pre> tag still requires '<' and '>' characters to be escaped
        slurpSource = slurpSource.replace("<", "&lt;");
        slurpSource = slurpSource.replace(">", "&gt;");
        results.append("<blockquote><pre>").append(slurpSource).append("</pre></blockquote>");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return results.toString();
  }

  @Override
  public Set<Location> getAllowedLocations() {
    return EnumSet.allOf(Location.class);
  }

  @Override
  public boolean isInlineTag() {
    return true;
  }

  /**
   * Read the content of the reader line by line until the start character is found in a line.
   * Remove leading whitespace on each line to match the first, and stop when closing curly
   * brackets match opening brackets.
   *
   * @param reader the reader to get bytes from
   * @param startChar the first character of the file that we're interested in
   * @return a newline-joined, html-escaped string
   */
  private static String slurpSource(Reader reader, long startChar) throws IOException {
    // We want to skip until the line that contains character startChar - can't use BufferedReader
    // for this as it won't count characters (and the newlines could be two chars etc)

    // style guide says 100, actual max is around 190
    CharBuffer line = CharBuffer.allocate(250);
    // position starts at 0, since we incr at the same time as we read
    int position = -1;
    // accumulate finished lines to return
    StringBuilder lines = new StringBuilder();
    // indent starts at -1 meaning "we don't know the indentation level yet"
    int indent = -1;

    // braceDepth lets us tell when we finish the target element, but we have to be sure we saw at
    // least one open/close set, or semicolon.
    int braceDepth = 0;
    boolean seenSemiColonOrBrace = false;

    // read each character - if \r (check for a \n to follow) or \n end the line and record the
    // position of the end of the line.
    int ch = reader.read();
    position++;
    while (true) {
      if (ch == '\n' || ch == '\r' || ch == -1) {
        if (ch == '\r') {
          ch = reader.read();
          position++;
          if (ch == '\n') {
            ch = reader.read();
            position++;
          }
        } else if (ch == '\n') {
          ch = reader.read();
          position++;
        } // else EOF, nothing to do

        // We've read a full line (and are ready to start the next one).
        // If position < startChar, we haven't yet reached our content, continue
        if (position <= startChar) {
          line.clear();
          continue;
        }
        // make the written chars available to be read
        line.flip();
        if (indent == -1) {
          // this is our first line, read the indentation level
          for (indent = 0; Character.isWhitespace(line.charAt(indent)); ++indent) {
            // just accumulate
          }
        }
        // remove the indent chars from this line, and append it
        String lineStr;
        if (line.length() >= indent) {
          lineStr = line.subSequence(indent, line.length()).toString();
        } else {
          lineStr = line.toString();
        }

        lines.append(lineStr).append('\n');
        for (int i = 0, n = line.length(); i < n; ++i) {
          char c = line.charAt(i);
          if (c == '{') {
            seenSemiColonOrBrace = true;
            ++braceDepth;
          } else if (c == '}') {
            --braceDepth;
          } else if (c == ';') {
            seenSemiColonOrBrace = true;
          }
        }

        // now that we've written the line, check if it was our final line
        if ((braceDepth <= 0 && seenSemiColonOrBrace) || ch == -1) {
          break;
        }

        // free up the buffer to have new chars written to it
        line.clear();
      } else {
        // we're in the middle of a line, record the character and continue
        line.put((char) ch);

        ch = reader.read();
        position++;
      }
    }

    return lines.toString();
  }

}
