package com.google.doctool.custom;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.util.DocTreeScanner;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Taglet;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public abstract class AbstractTaglet implements Taglet {
    protected DocletEnvironment env;

    @Override
    public void init(DocletEnvironment env, Doclet doclet) {
        this.env = env;
    }

    protected String getText(DocTree tag) {
        return tag.accept(new DocTreeScanner<String, Void>() {
            @Override
            public String visitText(TextTree node, Void unused) {
                return node.getBody();
            }
        }, null);
    }

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
