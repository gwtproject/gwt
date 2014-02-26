package com.google.gwt.apt.jsinterface;

import com.google.auto.service.AutoService;
import com.google.gwt.core.client.js.JsInterface;
import com.google.gwt.thirdparty.guava.common.base.Strings;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 * Annotation processor for @JsInterface to automatically create Type_Prototype stub classes needed for GWT
 * JS interop layer.
 */
@AutoService(Processor.class)
public class JsInterfaceProcessor extends AbstractProcessor {
  /*
   * Most of the code in this class is borrowed and modified from Guava's excellent @AutoValue processor.
   */
  private static final boolean SILENT = false;

  public JsInterfaceProcessor() {}


  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Collections.singleton(JsInterface.class.getName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private void note(String msg) {
    if (!SILENT) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
    }
  }

  @SuppressWarnings("serial")
  // CHECKSTYLE:OFF:WhitespaceAround
  private static class CompileException extends Exception {}
  // CHECKSTYLE:ON

  /**
   * Issue a compilation error. This method does not throw an exception, since we want to
   * continue processing and perhaps report other errors. It is a good idea to introduce a
   * test case in CompilationErrorsTest for any new call to reportError(...) to ensure that we
   * continue correctly after an error.
   */
  private void reportError(String msg, Element e) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
  }

  /**
   * Issue a compilation error and abandon the processing of this class. This does not prevent
   * the processing of other classes.
   */
  private void abortWithError(String msg, Element e) throws CompileException {
    reportError(msg, e);
    throw new CompileException();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    boolean claimed = (annotations.size() == 1
        && annotations.iterator().next().getQualifiedName().toString().equals(
        JsInterface.class.getName()));
    if (claimed) {
      process(roundEnv);
      return true;
    } else {
      return false;
    }
  }

  private void process(RoundEnvironment roundEnv) {
    Collection<? extends Element> annotatedElements =
        roundEnv.getElementsAnnotatedWith(JsInterface.class);
    Collection<? extends TypeElement> types = ElementFilter.typesIn(annotatedElements);
    for (TypeElement type : types) {
      try {
        processType(type);
      } catch (CompileException e) {
        // We abandoned this type, but continue with the next.
      } catch (RuntimeException e) {
        // Don't propagate this exception, which will confusingly crash the compiler.
        reportError("@JsInterface processor threw an exception: " + e, type);
      }
    }
  }

  private static AnnotationMirror getAnnotationMirror(TypeElement typeElement, Class<?> clazz) {
    String clazzName = clazz.getName();
    for(AnnotationMirror m : typeElement.getAnnotationMirrors()) {
      if(m.getAnnotationType().toString().equals(clazzName)) {
        return m;
      }
    }
    return null;
  }

  private static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
    for(Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet() ) {
      if(entry.getKey().getSimpleName().toString().equals(key)) {
        return entry.getValue();
      }
    }
    return null;
  }

  private void processType(TypeElement type) throws CompileException {
    try {
      JsInterface jsInterface = type.getAnnotation(JsInterface.class);

      if (jsInterface == null) {
        // This shouldn't happen unless the compilation environment is buggy,
        // but it has happened in the past and can crash the compiler.
        abortWithError("annotation processor for @JsInterface was invoked with a type that "
            + "does not have that annotation; this is probably a compiler bug", type);
      } else {
        if (Strings.isNullOrEmpty(jsInterface.prototype())) {
          return;
        }
      }

      if (type.getKind() != ElementKind.INTERFACE) {
        abortWithError("@" + JsInterface.class.getName() + " only applies to interfaces", type);
      }


      StringWriter sw = new StringWriter();
      JavaWriter javaWriter = new JavaWriter(sw);
      try {
        List<ExecutableElement> methods = new ArrayList<ExecutableElement>();
        TypeElement superProto = (TypeElement) findLocalAndInheritedMethods(type, methods);
        Set<TypeMirror> types = new HashSet<TypeMirror>();
        types.addAll(returnTypesOf(methods));
        String pkg = TypeSimplifier.packageNameOf(type);
        TypeSimplifier typeSimplifier = new TypeSimplifier(processingEnv.getTypeUtils(), pkg, types);
        String simpleName = simpleNameOf(generatedSubclassName(type));
        javaWriter.emitSingleLineComment("Automatically generated for " + classNameOf(type) + " DO NOT EDIT!");
        javaWriter.emitPackage(TypeSimplifier.packageNameOf(type))
            .emitAnnotation("javax.annotation.Generated(\"com.google.gwt.apt.jsinterface.JsInterfaceProcessor\")")
            .beginType(simpleName + formalTypeString(type), "class", EnumSet.of(Modifier.PUBLIC),
                superProto != null ? javaWriter.compressType(generatedSubclassName(superProto)) : null,
                javaWriter.compressType(classNameOf(type)));

        Types typeUtils = processingEnv.getTypeUtils();

        AnnotationMirror jsInterfaceMirror = getAnnotationMirror(type, JsInterface.class);
        AnnotationValue constructorParams = getAnnotationValue(jsInterfaceMirror, "constructor");

        if (constructorParams != null && constructorParams.getValue() instanceof List) {

          List<String> ctorParams = new ArrayList<String>();
          int cparamCount = 0;
          for (AnnotationValue ctorParamType : ((List<AnnotationValue>) constructorParams.getValue())) {
            ctorParams.add(javaWriter.compressType(ctorParamType.getValue().toString()));
            ctorParams.add("p" + (cparamCount++));
          }
          javaWriter.beginConstructor(EnumSet.of(Modifier.PUBLIC), ctorParams, null);
          javaWriter.emitEmptyLine();
          javaWriter.endConstructor();
        }

        for (ExecutableElement method : methods) {
          List<String> params = new ArrayList<String>();
          int count = 0;
          for (VariableElement param : method.getParameters()) {
            params.add(typeSimplifier.simplify(param.asType()));
            params.add("p" + (count++));
          }
          javaWriter.beginMethod(typeSimplifier.simplify(method.getReturnType()), method.getSimpleName().toString(),
              EnumSet.of(Modifier.PUBLIC), params, Collections.EMPTY_LIST);
          switch (method.getReturnType().getKind()) {
            case VOID:
              javaWriter.emitEmptyLine();
              break;
            case BOOLEAN:
              javaWriter.emitStatement("return false");
              break;
            case BYTE:
              case CHAR:
                case DOUBLE:
            case FLOAT:
            case INT:
            case SHORT:
            case LONG:
              javaWriter.emitStatement("return 0");
              break;
            default:
              // prevents pruning to nullMethod of stubs
              javaWriter.emitStatement("return (" +
                  javaWriter.compressType(classNameOf(
                      (TypeElement) typeUtils.asElement(method.getReturnType()))) +") new Object()");
          }
          javaWriter.endMethod();
        }
        javaWriter.endType();
      } catch (IOException e) {
        abortWithError("IOException generating @JsInterface: " + e.getMessage(), type);
      }
      writeSourceFile(generatedSubclassName(type), sw.toString(), type);
    } catch (Exception e) {
      note(e.getMessage());
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      note(sw.toString());
      e.printStackTrace();
      throw e;
    }
  }

  // Why does TypeParameterElement.toString() not return this? Grrr.
  private static String typeParameterString(TypeParameterElement type) {
    String s = type.getSimpleName().toString();
    List<? extends TypeMirror> bounds = type.getBounds();
    if (bounds.isEmpty()) {
      return s;
    } else {
      s += " extends ";
      String sep = "";
      for (TypeMirror bound : bounds) {
        s += sep + bound;
        sep = " & ";
      }
      return s;
    }
  }

  private static String formalTypeString(TypeElement type) {
    List<? extends TypeParameterElement> typeParameters = type.getTypeParameters();
    if (typeParameters.isEmpty()) {
      return "";
    } else {
      String s = "<";
      String sep = "";
      for (TypeParameterElement typeParameter : typeParameters) {
        s += sep + typeParameterString(typeParameter);
        sep = ", ";
      }
      return s + ">";
    }
  }

  private static String actualTypeString(TypeElement type) {
    List<? extends TypeParameterElement> typeParameters = type.getTypeParameters();
    if (typeParameters.isEmpty()) {
      return "";
    } else {
      String s = "<";
      String sep = "";
      for (TypeParameterElement typeParameter : typeParameters) {
        s += sep + typeParameter.getSimpleName();
        sep = ", ";
      }
      return s + ">";
    }
  }

  // The @AutoValue type, with a ? for every type.
  private static String wildcardTypeString(TypeElement type) {
    List<? extends TypeParameterElement> typeParameters = type.getTypeParameters();
    if (typeParameters.isEmpty()) {
      return "";
    } else {
      String s = "<";
      String sep = "";
      for (int i = 0; i < typeParameters.size(); i++) {
        s += sep + "?";
        sep = ", ";
      }
      return s + ">";
    }
  }

  private Set<TypeMirror> returnTypesOf(List<ExecutableElement> methods) {
    HashSet<TypeMirror> returnTypes = new HashSet<TypeMirror>();
    for (ExecutableElement method : methods) {
      returnTypes.add(method.getReturnType());
    }
    return returnTypes;
  }

  private String generatedClassName(TypeElement type, String suffix) {
    String name = type.getSimpleName().toString();
    while (type.getEnclosingElement() instanceof TypeElement) {
      type = (TypeElement) type.getEnclosingElement();
      name = type.getSimpleName() + "_" + name;
    }
    String pkg = TypeSimplifier.packageNameOf(type);
    String dot = pkg.isEmpty() ? "" : ".";
    return pkg + dot + name + suffix;
  }

  private String generatedSubclassName(TypeElement type) {
    return generatedClassName(type, "_Prototype");
  }

  private static String simpleNameOf(String s) {
    if (s.contains(".")) {
      return s.substring(s.lastIndexOf('.') + 1);
    } else {
      return s;
    }
  }

  // Return the name of the class, including any enclosing classes but not the package.
  private static String classNameOf(TypeElement type) {
    String name = type.getQualifiedName().toString();
    String pkgName = TypeSimplifier.packageNameOf(type);
    if (!pkgName.isEmpty()) {
      return name.substring(pkgName.length() + 1);
    } else {
      return name;
    }
  }

  private Element findLocalAndInheritedMethods(TypeElement type, List<ExecutableElement> methods) {
    note("Looking at methods in " + type);
    Types typeUtils = processingEnv.getTypeUtils();
    Elements elementUtils = processingEnv.getElementUtils();
    TypeMirror superProtoIntf = null;
    for (TypeMirror superInterface : type.getInterfaces()) {
      JsInterface jsi = typeUtils.asElement(superInterface).getAnnotation(JsInterface.class);
      if (jsi != null && jsi.prototype() != null) {
        superProtoIntf = superInterface;
      }
    }

    for (TypeMirror superInterface : type.getInterfaces()) {
      if (superInterface != superProtoIntf) {
        findLocalAndInheritedMethods((TypeElement) typeUtils.asElement(superInterface), methods);
      }
    }

    // Add each method of this class, and in so doing remove any inherited method it overrides.
    // This algorithm is quadratic in the number of methods but it's hard to see how to improve
    // that while still using Elements.overrides.
    List<ExecutableElement> theseMethods = ElementFilter.methodsIn(type.getEnclosedElements());
    for (ExecutableElement method : theseMethods) {
        boolean alreadySeen = false;
        for (Iterator<ExecutableElement> methodIter = methods.iterator(); methodIter.hasNext();) {
          ExecutableElement otherMethod = methodIter.next();
          if (elementUtils.overrides(method, otherMethod, type)) {
            methodIter.remove();
          } else if (method.getSimpleName().equals(otherMethod.getSimpleName())
              && method.getParameters().equals(otherMethod.getParameters())) {
            // If we inherit this method on more than one path, we don't want to add it twice.
            alreadySeen = true;
          }
        }
        if (!alreadySeen) {
          methods.add(method);
        }
    }

    if (superProtoIntf != null) {
      return typeUtils.asElement(superProtoIntf);
    } else {
      return null;
    }
  }

  private void writeSourceFile(String className, String text, TypeElement originatingType) {
    try {
      note(text);
      JavaFileObject sourceFile =
          processingEnv.getFiler().createSourceFile(className, originatingType);
      Writer writer = sourceFile.openWriter();
      try {
        writer.write(text);
      } finally {
        writer.close();
      }
    } catch (IOException e) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
          "Could not write generated class " + className + ": " + e);
    }
  }

  private TypeMirror getTypeMirror(Class<?> c) {
    return processingEnv.getElementUtils().getTypeElement(c.getName()).asType();
  }
}
