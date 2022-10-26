package com.google.doctool.custom;

import com.sun.source.doctree.DocTree;
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
     * Given a tag, returns the text within that tag.
     *
     * @param tag the tag to read the text from
     * @return the body of the textnode within that tag
     */
    protected String getText(DocTree tag) {
        return tag.accept(new DocTreeScanner<String, Void>() {
            @Override
            public String visitText(TextTree node, Void unused) {
                return node.getBody();
            }
        }, null);
    }

    /**
     * Helper method to log a diagnostic message to the user about a particular element+doctree.
     *
     * @param kind the kind of message to log
     * @param message the message text to write
     * @param element the element that the message applies to
     * @param docTree the doctree node that the message applies to
     */
    protected void printMessage(Diagnostic.Kind kind, String message, Element element, DocTree docTree) {
        env.getDocTrees().printMessage(
                kind,
                message,
                docTree,
                env.getDocTrees().getDocCommentTree(element),
                env.getDocTrees().getPath(element).getCompilationUnit()
        );
    }
}
