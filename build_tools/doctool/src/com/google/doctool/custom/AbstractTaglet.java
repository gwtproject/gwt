/*
 * Copyright 2022 Google Inc.
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
import com.sun.source.doctree.EndElementTree;
import com.sun.source.doctree.EntityTree;
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.util.DocTreeScanner;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Taglet;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Abstract taglet baseclass to facilitate some basic operations.
 */
public abstract class AbstractTaglet implements Taglet {
    protected DocletEnvironment env;

    @Override
    public void init(DocletEnvironment env, Doclet doclet) {
        this.env = env;
    }

    /**
     * Given a tag, returns the html within that tag.
     *
     * @param tag the tag to read the text from
     * @return the tags, entities, and text within that tag, as an html string
     */
    protected String getHtmlContent(DocTree tag) {
        StringBuilder sb = new StringBuilder();
        tag.accept(new DocTreeScanner<Void, Void>() {
            @Override
            public Void visitStartElement(StartElementTree node, Void unused) {
                sb.append("<").append(node.getName());
                if (!node.getAttributes().isEmpty()) {
                    sb.append(" ");
                    // super only scans attributes
                    super.visitStartElement(node, unused);
                }
                if (node.isSelfClosing()) {
                    sb.append("/>");
                } else {
                    sb.append(">");
                }

                return null;
            }

            @Override
            public Void visitEndElement(EndElementTree node, Void unused) {
                sb.append("</").append(node.getName()).append(">");
                return null;
            }

            @Override
            public Void visitText(TextTree node, Void unused) {
                // Only includes non-entity text
                sb.append(node.getBody());
                return null;
            }

            @Override
            public Void visitEntity(EntityTree node, Void unused) {
                // Entities need to be wrapped in &/;, but we aren't worrying about illegal
                // entities here, assuming they are handled elsewhere
                sb.append("&").append(node.getName()).append(";");
                return null;
            }
        }, null);
        return sb.toString();
    }

    /**
     * Helper method to log a diagnostic message to the user about a particular element+doctree.
     *
     * @param kind the kind of message to log
     * @param message the message text to write
     * @param element the element that the message applies to
     * @param docTree the doctree node that the message applies to
     */
    protected void printMessage(Diagnostic.Kind kind, String message, Element element,
                                DocTree docTree) {
        env.getDocTrees().printMessage(
                kind,
                message,
                docTree,
                env.getDocTrees().getDocCommentTree(element),
                env.getDocTrees().getPath(element).getCompilationUnit()
        );
    }
}
