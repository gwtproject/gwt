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
package com.google.gwt.i18n.rebind;

import com.google.gwt.codegen.server.CodeGenUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.core.server.CldrInstantiator;
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.i18n.shared.LocaleInfo;
import com.google.gwt.i18n.shared.Locales;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import org.apache.tapestry.util.text.LocalizedProperties;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generator used to generate an implementation of the LocaleInfoImpl class,
 * which is used by the LocaleInfo class.
 */
public class LocaleInfoGenerator extends Generator {

  /**
   * Properties file containing machine-generated locale display names, in their
   * native locales (if possible).
   */
  private static final String GENERATED_LOCALE_NATIVE_DISPLAY_NAMES = "com/google/gwt/i18n/shared/cldr/LocaleNativeDisplayNames-generated.properties";

  /**
   * Properties file containing hand-made corrections to the machine-generated
   * locale display names above.
   */
  private static final String MANUAL_LOCALE_NATIVE_DISPLAY_NAMES = "com/google/gwt/i18n/shared/cldr/LocaleNativeDisplayNames-manual.properties";

  /**
   * Properties file containing hand-made overrides of locale display names, in
   * their native locales (if possible).
   */
  private static final String OVERRIDE_LOCALE_NATIVE_DISPLAY_NAMES = "com/google/gwt/i18n/shared/cldr/LocaleNativeDisplayNames-override.properties";

  /**
   * Generate an implementation for the given type.
   * 
   * @param logger error logger
   * @param context generator context
   * @param typeName target type name
   * @return generated class name
   * @throws UnableToCompleteException
   */
  @Override
  public final String generate(TreeLogger logger, final GeneratorContext context,
      String typeName) throws UnableToCompleteException {
    TypeOracle typeOracle = context.getTypeOracle();
    // Get the current locale and interface type.
    PropertyOracle propertyOracle = context.getPropertyOracle();
    LocaleUtils localeUtils = LocaleUtils.getInstance(logger, propertyOracle,
        context);

    JClassType targetClass;
    try {
      targetClass = typeOracle.getType(typeName);
    } catch (NotFoundException e) {
      logger.log(TreeLogger.ERROR, "No such type " + typeName, e);
      throw new UnableToCompleteException();
    }
    if (Locales.class.getName().equals(targetClass.getQualifiedSourceName())) {
      return generateLocalesImpl(logger, context, targetClass, typeOracle, propertyOracle,
          localeUtils);
    }
    logger.log(TreeLogger.ERROR, "LocaleInfoGenerator asked to create " + typeName);
    throw new UnableToCompleteException();
  }

  private boolean generateAvailableLocaleNames(SourceWriter writer, GwtLocale[] allLocales,
      Map<GwtLocale, LocaleInfo> localeInfoImpls) {
    writer.println();
    writer.println("@Override");
    writer.println("public String[] getAvailableLocaleNames() {");
    writer.println("  return new String[] {");
    boolean hasAnyRtl = false;
    for (GwtLocale possibleLocale : allLocales) {
      writer.println("    " + CodeGenUtils.asStringLiteral(possibleLocale.toString()) + ",");
      if (!hasAnyRtl) {
        LocaleInfo localeInfo = localeInfoImpls.get(possibleLocale);
        if (localeInfo != null && localeInfo.isRTL()) {
          hasAnyRtl = true;
        }
      }
    }
    writer.println("  };");
    writer.println("}");
    return hasAnyRtl;
  }

  private void generateHasAnyRtl(SourceWriter writer, boolean hasAnyRtl) {
    writer.println();
    writer.println("@Override");
    writer.println("public boolean hasAnyRtl() {");
    writer.println("  return " + hasAnyRtl + ";");
    writer.println("}");
  }

  private void generateLocaleCookie(SourceWriter writer, LocaleUtils localeUtils) {
    String cookie = localeUtils.getCookie();
    writer.println();
    writer.println("@Override");
    writer.println("public String getLocaleCookieName() {");
    writer.println("  return " + CodeGenUtils.asStringLiteral(cookie) + ";");
    writer.println("}");
  }

  private void generateLocaleQueryParam(SourceWriter writer, LocaleUtils localeUtils) {
    String queryParam = localeUtils.getQueryParam();
    writer.println();
    writer.println("@Override");
    writer.println("public String getLocaleQueryParam() {");
    writer.println("  return " + CodeGenUtils.asStringLiteral(queryParam) + ";");
    writer.println("}");
  }

  @SuppressWarnings("unused")
  private String generateLocalesImpl(TreeLogger logger, GeneratorContext context,
      JClassType targetClass, TypeOracle typeOracle, PropertyOracle propertyOracle,
      LocaleUtils localeUtils) throws UnableToCompleteException {
    assert (Locales.class.getName().equals(targetClass.getQualifiedSourceName()));

    String packageName = targetClass.getPackage().getName();
    String className = targetClass.getName().replace('.', '_') + "Impl";
    Set<GwtLocale> localeSet = localeUtils.getAllLocales();
    GwtLocale[] allLocales = localeSet.toArray(
        new GwtLocale[localeSet.size()]);
    // sort for deterministic output
    Arrays.sort(allLocales);
    PrintWriter pw = context.tryCreate(logger, packageName, className);
    if (pw != null) {
      LocalizedProperties displayNames = new LocalizedProperties();
      LocalizedProperties displayNamesManual = new LocalizedProperties();
      LocalizedProperties displayNamesOverride = new LocalizedProperties();
      ClassLoader classLoader = getClass().getClassLoader();
      try {
        InputStream str = classLoader.getResourceAsStream(GENERATED_LOCALE_NATIVE_DISPLAY_NAMES);
        if (str != null) {
          displayNames.load(str, "UTF-8");
        }
        str = classLoader.getResourceAsStream(MANUAL_LOCALE_NATIVE_DISPLAY_NAMES);
        if (str != null) {
          displayNamesManual.load(str, "UTF-8");
        }
        str = classLoader.getResourceAsStream(OVERRIDE_LOCALE_NATIVE_DISPLAY_NAMES);
        if (str != null) {
          displayNamesOverride.load(str, "UTF-8");
        }
      } catch (UnsupportedEncodingException e) {
        // UTF-8 should always be defined
        logger.log(TreeLogger.ERROR, "UTF-8 encoding is not defined", e);
        throw new UnableToCompleteException();
      } catch (IOException e) {
        logger.log(TreeLogger.ERROR, "Exception reading locale display names",
            e);
        throw new UnableToCompleteException();
      }

      ClassSourceFileComposerFactory factory = new ClassSourceFileComposerFactory(
          packageName, className);
      factory.addImplementedInterface(targetClass.getQualifiedSourceName());
      factory.addImport(GWT.class.getCanonicalName());
      factory.addImport(JavaScriptObject.class.getCanonicalName());
      factory.addImport(HashMap.class.getCanonicalName());
      SourceWriter writer = factory.createSourceWriter(context, pw);
      Map<GwtLocale, LocaleInfo> localeInfoImpls = loadLocaleInfoImpls(allLocales);
      writer.println("private static native String getLocaleNativeDisplayName(");
      writer.println("    JavaScriptObject nativeDisplayNamesNative,String localeName) /*-{");
      writer.println("  return nativeDisplayNamesNative[localeName];");
      writer.println("}-*/;");
      writer.println();
      writer.println("HashMap<String,String> nativeDisplayNamesJava;");
      writer.println("private JavaScriptObject nativeDisplayNamesNative;");
      boolean hasAnyRtl = generateAvailableLocaleNames(writer, allLocales, localeInfoImpls);
      writer.println();
      writer.println("@Override");
      // writer.println("public native LocaleInfo getLocale(String localeName) /*-{");
      writer.println("public LocaleInfo getLocale(String localeName) {");
      writer.indent();
      Map<Class<? extends LocaleInfo>, List<GwtLocale>> localeMap = new HashMap<
              Class<? extends LocaleInfo>, List<GwtLocale>>();
      Class<? extends LocaleInfo> defaultClass = null;
      for (GwtLocale possibleLocale : allLocales) {
        LocaleInfo localeInfo = localeInfoImpls.get(possibleLocale);
        Class<? extends LocaleInfo> clazz = localeInfo.getClass();
        if (!localeMap.containsKey(clazz)) {
          localeMap.put(clazz, new ArrayList<GwtLocale>());
        }
        localeMap.get(clazz).add(possibleLocale);
        if (possibleLocale.isDefault()) {
          defaultClass = clazz;
        }
      }
      localeMap.remove(defaultClass);
      if (false) {
        // TODO(jat): figure out why JS switch didn't work
        if (!localeMap.isEmpty()) {
          writer.println("switch (localeName) {");
          writer.indent();
          Set<Class<? extends LocaleInfo>> keySet = localeMap.keySet();
          Class<?>[] keys = keySet.toArray(new Class[keySet.size()]);
          Arrays.sort(keys);
          for (Class<?> key : keys) {
            List<GwtLocale> locales = localeMap.get(key);
            for (GwtLocale possibleLocale : locales) {
              writer.println("case " + CodeGenUtils.asStringLiteral(possibleLocale.toString())
                  + ":");
            }
            writer.println("  return @" + key.getCanonicalName() + "::new()();");
          }
          writer.outdent();
          writer.println("}");
        }
        writer.println("return @" + defaultClass.getCanonicalName() + "::new()();");
      } else {
        Set<Class<? extends LocaleInfo>> keySet = localeMap.keySet();
        Class<?>[] keys = keySet.toArray(new Class[keySet.size()]);
        Arrays.sort(keys, new Comparator<Class<?>>() {
          @Override
          public int compare(Class<?> a, Class<?> b) {
            return a.getCanonicalName().compareTo(b.getCanonicalName());
          }
        });
        for (Class<?> key : keys) {
          String prefix = "if (";
          List<GwtLocale> locales = localeMap.get(key);
          for (GwtLocale possibleLocale : locales) {
            writer.print(prefix + CodeGenUtils.asStringLiteral(possibleLocale.toString())
                + ".equals(localeName)");
            prefix = "\n    || ";
          }
          writer.println(") {");
          writer.println("  return new " + key.getCanonicalName() + "();");
          writer.println("}");
        }
        writer.println("return new " + defaultClass.getCanonicalName() + "();");
      }
      writer.outdent();
      // writer.println("}-*/;");
      writer.println("}");
      generateLocaleCookie(writer, localeUtils);
      writer.println();
      writer.println("@Override");
      writer.println("public String getLocaleNativeDisplayName(String localeName) {");
      writer.println("  if (GWT.isScript()) {");
      writer.println("    if (nativeDisplayNamesNative == null) {");
      writer.println("      nativeDisplayNamesNative = loadNativeDisplayNamesNative();");
      writer.println("    }");
      writer.println("    return getLocaleNativeDisplayName(nativeDisplayNamesNative, localeName);");
      writer.println("  } else {");
      writer.println("    if (nativeDisplayNamesJava == null) {");
      writer.println("      nativeDisplayNamesJava = new HashMap<String, String>();");
      {
        for (GwtLocale possibleLocale : allLocales) {
          String localeName = possibleLocale.toString();
          String displayName = displayNamesOverride.getProperty(localeName);
          if (displayName == null) {
            displayName = displayNamesManual.getProperty(localeName);
          }
          if (displayName == null) {
            displayName = displayNames.getProperty(localeName);
          }
          if (displayName != null && displayName.length() != 0) {
            writer.println("      nativeDisplayNamesJava.put("
                + CodeGenUtils.asStringLiteral(localeName) + ", "
                + CodeGenUtils.asStringLiteral(displayName) + ");");
          }
        }
      }

      writer.println("    }");
      writer.println("    return nativeDisplayNamesJava.get(localeName);");
      writer.println("  }");
      writer.println("}");
      generateLocaleQueryParam(writer, localeUtils);
      generateHasAnyRtl(writer, hasAnyRtl);
      writer.println();
      writer.println("private native JavaScriptObject loadNativeDisplayNamesNative() /*-{");
      writer.println("  return {");
      {
        boolean needComma = false;
        for (GwtLocale possibleLocale : allLocales) {
          String localeName = possibleLocale.toString();
          String displayName = displayNamesOverride.getProperty(localeName);
          if (displayName == null) {
            displayName = displayNamesManual.getProperty(localeName);
          }
          if (displayName == null) {
            displayName = displayNames.getProperty(localeName);
          }
          if (displayName != null && displayName.length() != 0) {
            if (needComma) {
              writer.println(",");
            }
            writer.print("    " + CodeGenUtils.asStringLiteral(localeName) + ": "
                + CodeGenUtils.asStringLiteral(displayName));
            needComma = true;
          }
        }
        if (needComma) {
          writer.println();
        }
      }
      writer.println("  };");
      writer.println("}-*/;");
      writer.commit(logger);
    }
    return packageName + "." + className;
  }

  private Map<GwtLocale, LocaleInfo> loadLocaleInfoImpls(GwtLocale[] allLocales) {
    Map<GwtLocale, LocaleInfo> result = new HashMap<GwtLocale, LocaleInfo>();
    for (GwtLocale locale : allLocales) {
      result.put(locale, CldrInstantiator.<LocaleInfo>createInstance(LocaleInfo.class,
          locale));
    }
    return result;
  }
}
