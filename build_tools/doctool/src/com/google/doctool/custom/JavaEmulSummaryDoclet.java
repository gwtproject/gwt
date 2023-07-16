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

package com.google.doctool.custom;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A doclet for listing the specified classes and
 * their methods and constructors.
 */
public class JavaEmulSummaryDoclet implements Doclet {

    public static final String OPT_OUTFILE = "-outfile";
    private static final String JAVADOC_URL = "https://docs.oracle.com/en/java/javase/11/docs/api/";

    private Reporter reporter;
    private String outputFile;

    @Override
    public boolean run(DocletEnvironment env) {
        try {

            File outFile = new File(outputFile);
            outFile.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(outFile);
                 PrintWriter pw = new PrintWriter(fw, true)) {

                pw.println("<ol class=\"toc\" id=\"pageToc\">");
                getSpecifiedPackages(env)
                        .forEach(pack -> {
                            pw.format("  <li><a href=\"#Package_%s\">%s</a></li>\n",
                                    pack.getQualifiedName()
                                            .toString().replace('.', '_'),
                                    pack.getQualifiedName().toString());
                        });

                pw.println("</ol>\n");

                getSpecifiedPackages(env).forEach(pack -> {
                    pw.format("<h2 id=\"Package_%s\">Package %s</h2>\n",
                            pack.getQualifiedName().toString().replace('.', '_'),
                            pack.getQualifiedName().toString());
                    pw.println("<dl>");

                    String packURL = JAVADOC_URL + pack.getQualifiedName().toString()
                            .replace(".", "/") + "/";

                    Iterator<? extends Element> classesIterator = pack.getEnclosedElements()
                            .stream()
                            .filter(element -> env.isSelected(element) && env.isIncluded(element))
                            .filter(element -> element.getModifiers().contains(Modifier.PUBLIC))
                            .sorted(Comparator.comparing((Element o) -> o.getSimpleName()
                                    .toString()))
                            .iterator();

                    while (classesIterator.hasNext()) {
                        Element cls = classesIterator.next();
                        // Each class links to Oracle's main JavaDoc
                        emitClassDocs(env, pw, packURL, cls);
                        if (classesIterator.hasNext()) {
                            pw.print("\n");
                        }
                    }

                    pw.println("</dl>\n");
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private void emitClassDocs(DocletEnvironment env, PrintWriter pw, String packURL, Element cls) {
        pw.format("  <dt><a href=\"%s%s.html\">%s</a></dt>\n", packURL,
                qualifiedSimpleName(cls), qualifiedSimpleName(cls));

        // Print out all fields
        String fields = cls.getEnclosedElements()
                .stream()
                .filter(element -> element.getKind().isField())
                .filter(field -> field.getModifiers().contains(Modifier.PUBLIC))
                .map(field -> field.getSimpleName().toString())
                .collect(Collectors.joining(", "));

        if (!fields.isEmpty()) {
            pw.format("  <dd style='margin-bottom: 0.5em;'>%s</dd>\n", fields);
        }

        List<String> constructors = cls.getEnclosedElements()
                .stream()
                .filter(element -> ElementKind.CONSTRUCTOR == element.getKind())
                .filter(member -> member.getModifiers().contains(Modifier.PUBLIC))
                .map(member -> (ExecutableElement) member)
                .map(executableElement -> flatSignature(env, cls, executableElement))
                .collect(Collectors.toList());

        List<String> methods = cls.getEnclosedElements()
                .stream()
                .filter(element -> ElementKind.METHOD == element.getKind())
                .filter(member -> member.getModifiers().contains(Modifier.PUBLIC))
                .map(member -> (ExecutableElement) member)
                .map(executableElement -> flatSignature(env, cls, executableElement))
                .collect(Collectors.toList());

        List<String> members = new ArrayList<>(constructors);
        members.addAll(methods);

        // Print out all constructors and methods
        if (!members.isEmpty()) {
            pw.format("  <dd>%s</dd>\n", createMemberList(members));
        }

        Iterator<? extends Element> classesIterator = cls.getEnclosedElements()
                .stream()
                .filter(element -> element.getKind().isClass()
                        || element.getKind().isInterface()
                        || ElementKind.ENUM == element.getKind())
                .filter(element -> element.getModifiers().contains(Modifier.PUBLIC))
                .sorted(Comparator.comparing((Element o) -> o.getSimpleName().toString()))
                .iterator();
        if (classesIterator.hasNext()) {
            pw.print("\n");
        }
        while (classesIterator.hasNext()) {
            Element innerCls = classesIterator.next();
            // Each class links to Sun's main JavaDoc
            emitClassDocs(env, pw, packURL, innerCls);
            if (classesIterator.hasNext()) {
                pw.print("\n");
            }
        }
    }

    private String createMemberList(Collection<String> members) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = members.iterator();
        while (iter.hasNext()) {
            String member = iter.next();
            sb.append(member);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private String qualifiedSimpleName(Element element) {
        String elementName = element.getSimpleName().toString();
        if (ElementKind.PACKAGE != element.getEnclosingElement().getKind()) {
            return qualifiedSimpleName(element.getEnclosingElement()) + "." + elementName;
        }
        return elementName;
    }

    private String flatSignature(DocletEnvironment env, Element parent, ExecutableElement member) {
        return (ElementKind.CONSTRUCTOR == member.getKind()
                ? parent.getSimpleName().toString()
                : member.getSimpleName().toString()) +
                "(" + member.getParameters()
                .stream()
                .map(Element::asType)
                .map(t -> simpleParamName(env, t))
                .collect(Collectors.joining(", ")) + ")";
    }

    private String simpleParamName(DocletEnvironment env, TypeMirror type) {
        if (type.getKind().isPrimitive() || TypeKind.TYPEVAR == type.getKind()) {
            return String.valueOf(type);
        } else if (TypeKind.ARRAY == type.getKind()) {
            return simpleParamName(env, ((ArrayType) type).getComponentType()) + "[]";
        } else {
            return qualifiedSimpleName(env.getTypeUtils().asElement(type));
        }
    }

    @Override
    public void init(Locale locale, Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public String getName() {
        return "JreEmulationSummaryDoclet";
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        Option[] options = {
                new Option() {

                    @Override
                    public int getArgumentCount() {
                        return 1;
                    }

                    @Override
                    public String getDescription() {
                        return "JRE emulation summary Doc location";
                    }

                    @Override
                    public Kind getKind() {
                        return Kind.STANDARD;
                    }

                    @Override
                    public List<String> getNames() {
                        return List.of(OPT_OUTFILE);
                    }

                    @Override
                    public String getParameters() {
                        return "file";
                    }

                    @Override
                    public boolean process(String opt, List<String> arguments) {
                        if (arguments.isEmpty()) {
                            reporter.print(Diagnostic.Kind.ERROR,
                                    "You must specify an output filepath with "
                                            + OPT_OUTFILE);
                            return false;
                        }
                        reporter.print(Diagnostic.Kind.NOTE,
                                "JRE emulation summary Doclet Option : "
                                + arguments.get(0));
                        outputFile = arguments.get(0);
                        return true;
                    }
                }
        };
        return new HashSet<>(Arrays.asList(options));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private Stream<PackageElement> getSpecifiedPackages(DocletEnvironment root) {
        return root.getSpecifiedElements()
                .stream()
                .filter(element -> ElementKind.PACKAGE == element.getKind())
                .map(element -> (PackageElement) element);
    }
}
