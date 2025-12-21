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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.tools.Diagnostic;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

/**
 * A doclet for listing the specified classes and
 * their methods and constructors.
 */
public class JavaEmulSummaryDoclet implements Doclet {

  public static final String OPT_OUTFILE = "-outfile";
  // should be aligned with the latest version that's tested by CI
  private static final int MAX_JRE_VERSION = 22;
  private static final int CURRENT_JRE_VERSION = Runtime.version().feature();
  private static final String JAVADOC_URL = "https://docs.oracle.com/en/java/javase/"
      + CURRENT_JRE_VERSION + "/docs/api/";
  private static final List<String> DEPRECATED = List.of(
      "java.lang.Character#isJavaLetter(char)",
      "java.lang.Character#isJavaLetterOrDigit(char)",
      "java.lang.String#getBytes(int, int, byte[], int)"
  );
  private enum Status {
    OPEN("\u23F3", "Planned to be implemented, patches or reviews welcome"),
    EVALUATING("\uD83E\uDD14", "Evaluating feasibility"),
    EXTERNAL("\uD83E\uDDE9", "Workaround available via external library"),
    WONTFIX("\u274C", "Won't be implemented");
    final String title;
    final String icon;
    Status(String icon, String title) {
      this.icon = icon;
      this.title = title;
    }
  }

  private Reporter reporter;
  private String outputFile;

  private final Map<String, String> issueToSignatures = new HashMap<>();
  private final Map<String, String> issueToTitle = new HashMap<>();
  private final Map<String, String> issueStatus = new HashMap<>();
  private final List<String> triage = new ArrayList<>();

  @Override
  public boolean run(DocletEnvironment env) {
    final Properties properties = new Properties();
    Path missingDir = Path.of("build_tools/doctool/src/com/google/doctool/custom/missing");
    try (Stream<Path> str = Files.list(missingDir)) {
      str.filter(file -> !file.getFileName().toString().startsWith("triage")).forEach(file -> {
        try {
          properties.clear();
          properties.load(Files.newInputStream(file));
          String issue = file.getFileName().toString().replaceAll("gh|\\.properties", "");
          issueToSignatures.put(issue, (String) properties.get("members"));
          issueToTitle.put(issue, (String) properties.get("title"));
          issueStatus.put(issue, (String) properties.getOrDefault("status", "open"));
        } catch (IOException ex) {
          throw new UncheckedIOException(ex);
        }
      });
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
    File outFilePresent = new File(outputFile);
    File outFileMissing = new File(outputFile.replace(".html", "-missing.html"));
    outFilePresent.getParentFile().mkdirs();
    try (FileWriter fwPresent = new FileWriter(outFilePresent, StandardCharsets.UTF_8);
      PrintWriter pwPresent = new PrintWriter(fwPresent, true);
      FileWriter fwMissing = new FileWriter(outFileMissing, StandardCharsets.UTF_8);
      PrintWriter pwMissing = new PrintWriter(fwMissing, true)) {

      pwPresent.println("<ol class=\"toc\" id=\"pageToc\">");
      getSpecifiedPackages(env)
          .forEach(pack ->
              pwPresent.format("  <li><a href=\"#Package_%s\">%s</a></li>\n",
                  pack.getQualifiedName().toString().replace('.', '_'),
                  pack.getQualifiedName().toString()));

      pwPresent.println("</ol>\n");
      Set<String> allClasses = getSpecifiedPackages(env)
        .flatMap(pack -> pack.getEnclosedElements().stream()
        .flatMap(clazz -> withInnerClasses(clazz, pack)))
        .collect(Collectors.toSet());
      getSpecifiedPackages(env).forEach(pack -> {
        Optional<Module> matchingModuleName = ModuleLayer.boot().modules().stream()
            .filter(m -> m.getPackages().contains(pack.getQualifiedName().toString()))
            .findFirst();

        pwPresent.format("<h2 id=\"Package_%s\">Package %s</h2>\n",
            pack.getQualifiedName().toString().replace('.', '_'),
            pack.getQualifiedName().toString());
        pwMissing.format("<h2 id=\"Package_%s\">Package %s</h2>\n",
          pack.getQualifiedName().toString().replace('.', '_'),
          pack.getQualifiedName().toString());
        pwPresent.println("<dl>");

        String packURL = JAVADOC_URL
            + matchingModuleName.map(m -> m.getName() + "/").orElse("")
            + pack.getQualifiedName().toString().replace(".", "/") + "/";

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
          emitClassDocs(env, pwPresent, pwMissing, packURL, cls,
              pack.getQualifiedName().toString() + ".", allClasses);
          if (classesIterator.hasNext()) {
            pwPresent.print("\n");
          }
        }

        pwPresent.println("</dl>\n");
      });
      if (!triage.isEmpty()) {
        System.out.println("Missing methods found, check triage.properties " +
            "and split it into gh*.properties files");
        Files.writeString(missingDir.resolve("triage.properties"),
            "members=" + String.join("\\\n", triage));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return true;
  }

  private Stream<String> withInnerClasses(Element clazz, PackageElement pack) {
    return Stream.concat(
      Stream.of(pack.getQualifiedName() + "." + clazz.getSimpleName().toString()),
      clazz.getEnclosedElements().stream()
        .map(inner -> pack.getQualifiedName()
          + "." + clazz.getSimpleName() + "$" + inner.getSimpleName()));
  }

  private void emitClassDocs(DocletEnvironment env, PrintWriter pwpPresent,
                 PrintWriter pwMissing, String packURL, Element cls,
                 String pack, Set<String> allClasses) {
    pwpPresent.format("  <dt><a href=\"%s%s.html\">%s</a></dt>\n", packURL,
        qualifiedSimpleName(cls), qualifiedSimpleName(cls));
    if ("JsException".equals(cls.getSimpleName().toString())) {
      return;
    }
    // Print out all fields
    List<String> fields = cls.getEnclosedElements()
        .stream()
        .filter(element -> element.getKind().isField())
        .filter(field -> field.getModifiers().contains(Modifier.PUBLIC))
        .map(field -> field.getSimpleName().toString())
        .collect(Collectors.toList());

    if (!fields.isEmpty()) {
      pwpPresent.format("  <dd style='margin-bottom: 0.5em;'><strong>Fields:</strong> %s</dd>\n",
          String.join(", ", fields));
    }

    List<String> constructors = cls.getEnclosedElements()
        .stream()
        .filter(element -> ElementKind.CONSTRUCTOR == element.getKind())
        .filter(member -> member.getModifiers().contains(Modifier.PUBLIC))
        .map(member -> (ExecutableElement) member)
        .map(executableElement ->
            flatSignature(t -> simpleParamName(env, t), cls, executableElement))
        .collect(Collectors.toList());

    List<String> methods = getMethodNames(cls, t -> simpleParamName(env, t));

    List<String> erasedMethods = getMethodNames(cls, t -> erasedParamName(env, t));

    if (!constructors.isEmpty()) {
      pwpPresent.format("  <dd><strong>Constructors:</strong> %s</dd>\n",
        createMemberList(constructors));
    }
    // Print out all constructors and methods
    if (!methods.isEmpty()) {
      pwpPresent.format("  <dd><strong>Methods:</strong> %s</dd>\n",
        createMemberList(methods));
    }
    String[] parts = (pack + cls.getSimpleName()).split("\\$");
    List<String> missingMembers = new ArrayList<>();
    Class<?> c;
    try {
      c = Class.forName(parts[0]);
    } catch (ClassNotFoundException e) {
      c = null;
    }
    if (c != null && parts.length > 1) {
      c = Arrays.stream(c.getDeclaredClasses())
        .filter(inner -> inner.getSimpleName().equals(cls.getSimpleName().toString()))
        .findFirst().orElse(null);
    }
    if (c == null) {
      if (CURRENT_JRE_VERSION >= MAX_JRE_VERSION) {
        throw new RuntimeException("Class does not exist in JRE: " + parts[0]);
      } else {
        System.out.format("Class %s not supported in Java %s%n", parts[0], CURRENT_JRE_VERSION);
        return;
      }
    }
    Class<?> superclass = c.getSuperclass() != null ? c.getSuperclass() : Object.class;
    List<Method> superMethods = new ArrayList<>(Arrays.asList(superclass.getMethods()));

    for (Class<?> parentInterface: c.getInterfaces()) {
      if (!allClasses.contains(parentInterface.getTypeName())) {
        System.out.println("Missing interface for " + c + ": " + parentInterface.getTypeName());
      }
      superMethods.addAll(Arrays.asList(parentInterface.getMethods()));
    }
    for (Method method: c.getDeclaredMethods()) {
      String reflectionSignature = getReflectionSignature(method);
      if (java.lang.reflect.Modifier.isPublic(method.getModifiers())
          && !erasedMethods.contains(reflectionSignature)
          && !DEPRECATED.contains(pack + cls.getSimpleName() + "#" + reflectionSignature)
          && !reflectionSignature.matches("of\\(Enum(, Enum)+\\)")
          && superMethods.stream().noneMatch(m ->
              nameAndParamCount(m).equals(nameAndParamCount(method)))) {
        missingMembers.add(reflectionSignature);
      } else if (!"".equals(getStatus(pack + cls.getSimpleName() + "#" + reflectionSignature))) {
        System.out.println("No longer missing: " + reflectionSignature);
      }
    }
    for (Field field: c.getFields()) {
      if (java.lang.reflect.Modifier.isPublic(field.getModifiers())
          && !fields.contains(field.getName())
          && !isFieldFromSuper(field, c)) {
        missingMembers.add(field.getName());
      }
    }

    if (!missingMembers.isEmpty()) {
      Map<String, List<String>> missingMemberGroups = missingMembers.stream()
          .collect(Collectors.groupingBy(signature ->
              getStatus(pack + cls.getSimpleName() + "#" + signature)));
      if (missingMemberGroups.containsKey("")) {
        missingMemberGroups.get("").stream()
            .map(s -> pack + cls.getSimpleName() + "#" + s)
            .forEach(triage::add);
      }
      pwMissing.format("%n  <dt><a href=\"%s%s.html\">%s</a></dt>%n", packURL,
        qualifiedSimpleName(cls), qualifiedSimpleName(cls));
      for (Map.Entry<String, List<String>> entry: missingMemberGroups.entrySet()) {
        pwMissing.format("  <dd><strong><a href=\"https://github.com/gwtproject/gwt/issues/%s\">%s</a>:</strong> %s</dd>%n",
          entry.getKey(),
          getIssueTitle(entry.getKey()),
          createMemberList(entry.getValue()));
      }
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
      pwpPresent.print("\n");
    }
    while (classesIterator.hasNext()) {
      Element innerCls = classesIterator.next();
      // Each class links to Sun's main JavaDoc
      emitClassDocs(env, pwpPresent, pwMissing, packURL, innerCls,
        pack + cls.getSimpleName() + "$", allClasses);
      if (classesIterator.hasNext()) {
        pwpPresent.print("\n");
      }
    }
  }
  private Object getIssueTitle(String key) {
    if (key.isEmpty()) {
      return "Needs triage";
    }
    String title = issueToTitle.get(key);
    Status status = Status.valueOf(issueStatus.get(key).toUpperCase(Locale.ROOT));
    return "<span title=\"" + status.title + "\">" + status.icon
        + "</span>#" + key + (title == null ? "" : " (" + title + ")");
  }

  private boolean isFieldFromSuper(Field field, Class<?> c) {
    for (Class<?> parentInterface: c.getInterfaces()) {
      for (Field parent : parentInterface.getFields()) {
        if (parent.equals(field)) {
          return true;
        }
      }
    }
    Class<?> superClass = c.getSuperclass();
    while (superClass != null) {
      for (Field parent : superClass.getFields()) {
        if (parent.equals(field)) {
          return true;
        }
      }
      superClass = superClass.getSuperclass();
    }
    return false;
  }

  private String getStatus(String methodRef) {
    for (String category: issueToSignatures.keySet()) {
      if (issueToSignatures.get(category).contains(methodRef)) {
        return category;
      }
    }
    return "";
  }

  private List<String> getMethodNames(Element cls, Function<TypeMirror, String> typeNamer) {
    return cls.getEnclosedElements()
      .stream()
      .filter(element -> ElementKind.METHOD == element.getKind())
      .filter(member -> member.getModifiers().contains(Modifier.PUBLIC))
      .map(member -> (ExecutableElement) member)
      .map(executableElement -> flatSignature(typeNamer, cls, executableElement))
      .collect(Collectors.toList());
  }

  private String nameAndParamCount(Method m) {
    return m.getName() + ":" + m.getParameterCount();
  }

  private String getReflectionSignature(Method method) {
    return method.getName() + "(" + Arrays.stream(method.getParameters())
      .map(param -> param.getType().getSimpleName())
      .collect(Collectors.joining(", ")) + ")";
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

  private String flatSignature(Function<TypeMirror, String> namer,
       Element parent, ExecutableElement member) {
    return (ElementKind.CONSTRUCTOR == member.getKind()
        ? parent.getSimpleName().toString()
        : member.getSimpleName().toString()) +
        "(" + member.getParameters()
        .stream()
        .map(Element::asType)
        .map(namer)
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

  private String erasedParamName(DocletEnvironment env, TypeMirror type) {
    if (TypeKind.TYPEVAR == type.getKind()) {
      TypeMirror upperBound = ((TypeVariable) type).getUpperBound();
      return erasedParamName(env, upperBound);
    } else if (type.getKind().isPrimitive()) {
      return String.valueOf(type);
    } else if (TypeKind.ARRAY == type.getKind()) {
      return erasedParamName(env, ((ArrayType) type).getComponentType()) + "[]";
    } else {
      return env.getTypeUtils().asElement(type).getSimpleName().toString();
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
