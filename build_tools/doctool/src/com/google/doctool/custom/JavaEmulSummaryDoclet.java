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

import java.io.IOException;
import java.io.OutputStream;
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

  public static final String OPT_OUT_FILE = "-outFile";
  public static final String OPT_MISSING_FILE = "-missingFile";
  public static final String OPT_TRIAGE_FILE = "-triageFile";
  public static final String OPT_MISSING_PROPERTIES_DIR = "-missingProperties";
  // should be aligned with the latest version that's tested by CI
  private static final int MAX_JRE_VERSION = 22;
  // Lowest version for which missing method detection is reliable.
  // The tool can run with older versions, but will ignore missing methods.
  private static final int MIN_JRE_VERSION = 17;
  private static final int CURRENT_JRE_VERSION = Runtime.version().feature();
  private static final String JAVADOC_URL = "https://docs.oracle.com/en/java/javase/"
      + CURRENT_JRE_VERSION + "/docs/api/";
  private static final List<String> DEPRECATED = List.of(
      "java.lang.Character#isJavaLetter(char)",
      "java.lang.Character#isJavaLetterOrDigit(char)",
      "java.lang.String#getBytes(int, int, byte[], int)",
      "java.lang.Class#isUnnamedClass()", // exists in 21 as a preview, removed
      "java.lang.Enum#finalize()" // finalizers are ignored by GWT and to be removed from JVM
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
  private String missingFile;
  private String triageFile;
  private String missingPropertiesDir;

  // the key in the following maps is the full URL of an issue
  private final Map<String, Meta> issueDescriptions = new HashMap<>();
  private final List<String> triage = new ArrayList<>();

  private static class Meta {
    public final String title;
    public final String status;
    public final String members;

      private Meta(String title, String status, String members) {
          this.title = title;
          this.status = status;
          this.members = members;
      }
  }

  @Override
  public boolean run(DocletEnvironment env) {
    loadMissingMemberLists();
    try (PrintWriter pwPresent = createPrintWriter(outputFile);
         PrintWriter pwMissing = createPrintWriter(missingFile)) {
      pwPresent.println("<ol class=\"toc\" id=\"pageToc\">");
      getSpecifiedPackages(env)
          .forEach(pack ->
              pwPresent.format("  <li><a href=\"#Package_%s\">%s</a></li>\n",
                  pack.getQualifiedName().toString().replace('.', '_'),
                  pack.getQualifiedName().toString()));

      pwPresent.println("</ol>\n");
      Set<String> allClasses = getSpecifiedPackages(env)
        .flatMap(pack -> pack.getEnclosedElements().stream()
        .flatMap(clazz -> withInnerClasses(clazz, pack, null)))
        .collect(Collectors.toSet());
      getSpecifiedPackages(env).forEach(pack -> {
        Optional<Module> matchingModuleName = ModuleLayer.boot().modules().stream()
            .filter(m -> m.getPackages().contains(pack.getQualifiedName().toString()))
            .findFirst();

        pwPresent.format("<h2 id=\"Package_%s\">Package %s</h2>%n<dl>%n",
            pack.getQualifiedName().toString().replace('.', '_'),
            pack.getQualifiedName().toString());
        pwMissing.format("<h2 id=\"Package_%s\">Package %s</h2>%n<dl>%n",
            pack.getQualifiedName().toString().replace('.', '_'),
            pack.getQualifiedName().toString());

        String packURL = JAVADOC_URL
            + matchingModuleName.map(m -> m.getName() + "/").orElse("")
            + pack.getQualifiedName().toString().replace(".", "/") + "/";

        pack.getEnclosedElements()
            .stream()
            .filter(element -> env.isSelected(element) && env.isIncluded(element))
            .filter(JavaEmulSummaryDoclet::isMemberVisible)
            .sorted(Comparator.comparing((Element o) -> o.getSimpleName()
                .toString()))
            .forEach(cls -> emitClassDocs(env, pwPresent, pwMissing,
                packURL, cls, pack.getQualifiedName().toString() + ".", allClasses));

        pwPresent.println("</dl>\n");
        pwMissing.println("</dl>\n");
      });
      if (!triage.isEmpty() && CURRENT_JRE_VERSION >= MIN_JRE_VERSION) {
        Files.writeString(Path.of(triageFile),
            "members=" + String.join("\\\n", triage));
        throw new IllegalStateException("Missing methods found, check triage.properties " +
            "and split it into gh*.properties files");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return true;
  }

  private PrintWriter createPrintWriter(String filePath) throws IOException {
    Path path = Path.of(filePath);
    Files.createDirectories(path.getParent());
    OutputStream fwPresent = Files.newOutputStream(path);
    return new PrintWriter(fwPresent, true, StandardCharsets.UTF_8);
  }

  private void loadMissingMemberLists() {
    final Properties properties = new Properties();
    try (Stream<Path> str = Files.list(Path.of(missingPropertiesDir))) {
      str.filter(file -> !file.getFileName().toString().startsWith("triage")).forEach(file -> {
        try {
          properties.clear();
          properties.load(Files.newInputStream(file));
          String issue = (String) properties.computeIfAbsent(
              "link", ignore -> extractIssueLink(file.getFileName().toString()));
          issueDescriptions.put(issue, new Meta(
              (String) properties.get("title"),
              (String) properties.getOrDefault("status", "open"),
              (String) properties.get("members")));
        } catch (IOException ex) {
          throw new UncheckedIOException(ex);
        }
      });
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  private String extractIssueLink(String filename) {
    return "https://github.com/gwtproject/gwt/issues/" + filename.replaceAll("gh|\\.properties", "");
  }

  private Stream<String> withInnerClasses(Element clazz, PackageElement pack, String parentName) {
    return Stream.concat(
      Stream.of(pack.getQualifiedName() + "." + getFullName(clazz, parentName)),
      clazz.getEnclosedElements().stream()
          .filter(JavaEmulSummaryDoclet::isType)
          .flatMap(inner -> withInnerClasses(inner, pack,
              getFullName(clazz, parentName))));
  }

  private static boolean isType(Element element) {
    return element.getKind().isClass() || element.getKind().isInterface();
  }

  private String getFullName(Element clazz, String parentName) {
    return (parentName == null ? "" : parentName + "$") + clazz.getSimpleName();
  }

  private void emitClassDocs(DocletEnvironment env, PrintWriter pwPresent,
                 PrintWriter pwMissing, String packURL, Element cls,
                 String pack, Set<String> allClasses) {
    pwPresent.format("%n  <dt><a href=\"%s%s.html\">%s</a></dt>%n", packURL,
        qualifiedSimpleName(cls), qualifiedSimpleName(cls));
    if ("JsException".equals(cls.getSimpleName().toString())) {
      return;
    }
    // Print out all fields
    List<String> fields = cls.getEnclosedElements()
        .stream()
        .filter(element -> element.getKind().isField())
        .filter(JavaEmulSummaryDoclet::isMemberVisible)
        .map(field -> field.getSimpleName().toString())
        .collect(Collectors.toList());

    if (!fields.isEmpty()) {
      pwPresent.format("  <dd style='margin-bottom: 0.5em;'><strong>Fields:</strong> %s</dd>\n",
          String.join(", ", fields));
    }

    List<String> constructors = cls.getEnclosedElements()
        .stream()
        .filter(element -> ElementKind.CONSTRUCTOR == element.getKind())
        .filter(JavaEmulSummaryDoclet::isMemberVisible)
        .map(member -> (ExecutableElement) member)
        .map(executableElement ->
            flatSignature(t -> simpleParamName(env, t), cls, executableElement))
        .collect(Collectors.toList());

    List<String> methods = getMethodNames(cls, t -> simpleParamName(env, t));

    List<String> erasedMethods = getMethodNames(cls, t -> erasedParamName(env, t));

    if (!constructors.isEmpty()) {
      pwPresent.format("  <dd><strong>Constructors:</strong> %s</dd>\n",
        createMemberList(constructors));
    }
    // Print out all constructors and methods
    if (!methods.isEmpty()) {
      pwPresent.format("  <dd><strong>Methods:</strong> %s</dd>\n",
        createMemberList(methods));
    }
    String fullName = pack + cls.getSimpleName();
    List<String> missingMembers = new ArrayList<>();
    Class<?> c;
    try {
      c = Class.forName(fullName);
    } catch (ClassNotFoundException e) {
      c = null;
    }
    if (c == null) {
      if (CURRENT_JRE_VERSION >= MAX_JRE_VERSION) {
        throw new RuntimeException("Class does not exist in JRE: " + fullName);
      } else {
        System.out.format("Class %s not supported in Java %s%n", fullName, CURRENT_JRE_VERSION);
        return;
      }
    }
    Class<?> superclass = c.getSuperclass() != null ? c.getSuperclass() : Object.class;
    List<Method> superMethods = new ArrayList<>(Arrays.asList(superclass.getMethods()));

    for (Class<?> parentInterface : c.getInterfaces()) {
      if (!allClasses.contains(parentInterface.getTypeName())) {
        System.out.format("Missing interface for %s: %s%n", c, parentInterface.getTypeName());
      }
      superMethods.addAll(Arrays.asList(parentInterface.getMethods()));
    }
    for (Method method: c.getDeclaredMethods()) {
      String reflectionSignature = getReflectionSignature(method);
      if (isModifierVisible(method.getModifiers())
          && !erasedMethods.contains(reflectionSignature)
          && !DEPRECATED.contains(fullName + "#" + reflectionSignature)
          && !reflectionSignature.matches("of\\(Enum(, Enum)+\\)")
          && superMethods.stream().noneMatch(m ->
              nameAndParamCount(m).equals(nameAndParamCount(method)))) {
        missingMembers.add(reflectionSignature);
      } else if (!"".equals(getStatus(fullName + "#" + reflectionSignature))) {
        System.out.format("No longer missing: %s%n", reflectionSignature);
      }
    }
    for (Field field: c.getFields()) {
      if (isModifierVisible(field.getModifiers())
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
      for (Map.Entry<String, List<String>> entry : missingMemberGroups.entrySet()) {
        pwMissing.format("  <dd><strong><a href=\"%s\">%s</a>:</strong> %s</dd>%n",
          entry.getKey(),
          getIssueTitle(entry.getKey()),
          createMemberList(entry.getValue()));
      }
    }
    for (Class<?> inner: c.getClasses()) {
      if (c.getSuperclass() != null && List.of(c.getSuperclass().getClasses()).contains(inner)) {
        continue;
      }
      if (isModifierVisible(inner.getModifiers())) {
        if (cls.getEnclosedElements().stream().noneMatch(
            el -> el.getSimpleName().toString().equals(inner.getSimpleName()))) {
          String fullInnerName = qualifiedSimpleName(cls) + "." + inner.getSimpleName();
          addMissingClass(fullInnerName, pack, packURL, pwMissing);
        }
      }
    }
   cls.getEnclosedElements()
        .stream()
        .filter(JavaEmulSummaryDoclet::isType)
        .filter(JavaEmulSummaryDoclet::isMemberVisible)
        .sorted(Comparator.comparing((Element o) -> o.getSimpleName().toString()))
        .forEach(innerCls ->
            emitClassDocs(env, pwPresent, pwMissing, packURL, innerCls,
           pack + cls.getSimpleName() + "$", allClasses));
  }

  private void addMissingClass(String fullInnerName, String pack, String packURL,
      PrintWriter pwMissing) {
    pwMissing.format("%n  <dt><a href=\"%s%s.html\">%s</a></dt>%n",
        packURL, fullInnerName, fullInnerName);
    String status = getStatus(fullInnerName);
    pwMissing.format(
        "  <dd><strong><a href=\"%s\">%s</a>:</strong> Inner class missing</dd>%n",
        status, getIssueTitle(status));
    if (status.isEmpty()) {
      triage.add(pack + fullInnerName);
    }
  }

  private boolean isModifierVisible(int modifiers) {
    return java.lang.reflect.Modifier.isPublic(modifiers)
        || java.lang.reflect.Modifier.isProtected(modifiers);
  }

  private Object getIssueTitle(String key) {
    if (key.isEmpty()) {
      return "Needs triage";
    }
    Meta meta = issueDescriptions.get(key);
    String title = meta.title;
    Status status = Status.valueOf(meta.status.toUpperCase(Locale.ROOT));
    return "<span class=\"issueStatus\" aria-label=\"" + status.title + "\">" + status.icon
        + "</span>#" + key.replaceFirst("^.*/(\\d+)$", "$1")
        + (title == null ? "" : " (" + title + ")");
  }

  private boolean isFieldFromSuper(Field field, Class<?> c) {
    for (Class<?> parentInterface : c.getInterfaces()) {
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
    for (String category: issueDescriptions.keySet()) {
      if (issueDescriptions.get(category).members.contains(methodRef)) {
        return category;
      }
    }
    return "";
  }

  private List<String> getMethodNames(Element cls, Function<TypeMirror, String> typeNamer) {
    return cls.getEnclosedElements()
      .stream()
      .filter(element -> ElementKind.METHOD == element.getKind())
      .filter(JavaEmulSummaryDoclet::isMemberVisible)
      .map(member -> (ExecutableElement) member)
      .map(executableElement -> flatSignature(typeNamer, cls, executableElement))
      .collect(Collectors.toList());
  }

  private static boolean isMemberVisible(Element member) {
    return member.getModifiers().contains(Modifier.PUBLIC)
        || member.getModifiers().contains(Modifier.PROTECTED);
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
    return String.join(", ", members);
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
            return List.of(OPT_OUT_FILE, OPT_MISSING_FILE, OPT_TRIAGE_FILE,
                OPT_MISSING_PROPERTIES_DIR);
          }

          @Override
          public String getParameters() {
            return "file";
          }

          @Override
          public boolean process(String opt, List<String> arguments) {
            if (arguments.isEmpty()) {
              reporter.print(Diagnostic.Kind.ERROR,
                  "Argument must be a path: " + opt);
              return false;
            }
            String value = arguments.get(0);
            reporter.print(Diagnostic.Kind.NOTE,
              "JRE emulation summary Doclet Option " + opt + ":" + value);
            switch (opt) {
              case OPT_OUT_FILE:
                outputFile = value;
                break;
              case OPT_MISSING_FILE:
                missingFile = value;
                break;
              case OPT_TRIAGE_FILE:
                triageFile = value;
                break;
              case OPT_MISSING_PROPERTIES_DIR:
                missingPropertiesDir = value;
                break;
              default:
                throw new IllegalArgumentException("Invalid option " + opt);
            }
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
