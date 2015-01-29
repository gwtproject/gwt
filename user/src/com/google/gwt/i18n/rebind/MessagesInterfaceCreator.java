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

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.server.MessageFormatUtils;
import com.google.gwt.i18n.server.MessageFormatUtils.ArgumentChunk;
import com.google.gwt.i18n.server.MessageFormatUtils.TemplateChunk;

import org.apache.tapestry.util.text.LocalizedProperties;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a MessagesInterface from a Resource file.
 */
public class MessagesInterfaceCreator extends
    AbstractLocalizableInterfaceCreator {

  /**
   * Searches for MessageFormat-style args in the template string and returns
   * a map of  of argument indices seen.
   * 
   * @param template template to parse
   * @return set of argument indices seen
   * @throws ParseException if the template is incorrect.
   */
  private static Map<Integer,ArgumentChunk> getMessageArgs(String template) throws ParseException {
	  HashMap<Integer,ArgumentChunk> args = new HashMap<>();
	  for (TemplateChunk chunk : MessageFormatUtils.MessageStyle.MESSAGE_FORMAT.parse(template)) {
		  if (chunk instanceof ArgumentChunk) {
			  args.put(((ArgumentChunk) chunk).getArgumentNumber(),(ArgumentChunk) chunk);
		  }
	  }
	  return args;
  }
  
  /**
   * Constructor for <code>MessagesInterfaceCreator</code>.
   * 
   * @param className class name
   * @param packageName package name
   * @param resourceBundle resource bundle
   * @param targetLocation target location
   * @throws IOException
   */
  public MessagesInterfaceCreator(String className, String packageName,
      File resourceBundle, File targetLocation) throws IOException {
    super(className, packageName, resourceBundle, targetLocation,
      Messages.class);
  }
  
  @Override
  void generateMethods(LocalizedProperties properties, String[] keys) {
    for (int i = 0; i < keys.length; i++) {
      String key = keys[i];
      String value = properties.getProperty(key);
      Map<String,String> plurals = new HashMap<>();
      while (i + 1 < keys.length && isNextPlural(key,keys[i + 1])) {
        i++;
        plurals.put(keys[i],properties.getProperty(keys[i]));
      }
      genMethodDecl(value, key, plurals);
    }
  }
  
  @Override
  protected void genMethodArgs(String defaultValue) {
  }
  
  private boolean isNextPlural(String key, String nextKey) {
    return nextKey.matches(".*\\[.*\\]$") && nextKey.startsWith(key);
  }
  private void genMethodArgs(Map<Integer,ArgumentChunk> args) {
    for (int i = 0; i <= Collections.max(args.keySet()); i++) {
      if (i > 0) {
        composer.print(",  ");
      }
      if (!args.containsKey(i)) {
        composer.print("@Optional String arg"+ i);
        continue;
      }
      String format = (format = args.get(i).getFormat())  != null ? format : "string";
      String subFormat = (subFormat = args.get(i).getSubFormat()) != null ? subFormat : "";
      if (args.get(i).isList()) {
        composer.print("java.util.List<");
      }
      switch(format) {
        case "number" :
          determineNumberType(subFormat);
          break;
        case "date" :
        case "time" :
        case "localdatetime" :
          composer.print("java.util.Date");
          break;
        case "safehtml" :
          composer.print("com.google.gwt.safehtml.shared.SafeHtml");
          break;
        default :
          composer.print("String");
      }
      if(args.get(i).isList()) {
        composer.print(">");
      }
      composer.print(" arg" + i);
    }
  }
  
  @Override
  protected void genValueAnnotation(String defaultValue) {
    composer.println("@DefaultMessage(" + makeJavaString(defaultValue) + ")");
  }

  @Override
  protected String javaDocComment(String path) {
    return "Interface to represent the messages contained in resource bundle:\n\t"
      + path + "'.";
  }
  
  private void determineNumberType(String subFormat) {
    switch (subFormat) {
      case "integer" :
        composer.print("Integer");
        break;
      case "currency" :
      case "percent" :
      default :
        composer.print("Double");
    }
  }
  
  private String determineReturnType(Map<Integer,ArgumentChunk> args) {
    for (ArgumentChunk arg : args.values()) {  
      if ("safehtml".equals(arg.getFormat()))
        return "com.google.gwt.safehtml.shared.SafeHtml";
    }
    return "String";
  }
  
  private void genPluralsAnnotation(Map<String,String> plurals) { 
    composer.print("@AlternateMessage({");
    String[] keys = plurals.keySet().toArray(new String[]{});
    if (keys.length > 1) {
      composer.println("");
      composer.indent();
    }
    for (int i = 0; i < keys.length; i++) {
      String key = keys[i];
      if (i > 0) {
        composer.println(",");
      }
      composer.print("\"" + key.substring(key.indexOf('[') + 1, key.length() - 1) + "\", ");
      composer.print("\"" + plurals.get(key) + "\"");
    }
    if (keys.length > 1) {
      composer.println("");
      composer.outdent();
    }
    composer.println("})");
  }
  
  private void genMethodDecl(String defaultValue, String key, Map<String,String> plurals) {
    try {
      Map<Integer,ArgumentChunk> args = getMessageArgs(defaultValue);
      genMethodJavaDoc(defaultValue,args);
      genValueAnnotation(defaultValue);
      if (!plurals.isEmpty()) {
        genPluralsAnnotation(plurals);
      }
      composer.println("@Key(" + makeJavaString(key) + ")");
      String methodName = formatKey(key);
      String type = determineReturnType(args);
      composer.print(type + " " + methodName);
      composer.print("(");
      if (!plurals.isEmpty()) {
        composer.print("@PluralCount ");
      }
      if (!args.isEmpty()) {
        genMethodArgs(args);
      }
      composer.print(");\n");
    } catch (ParseException e) {
      throw new RuntimeException(defaultValue
          + " could not be parsed as a MessageFormat string.", e);
    }
  }
  
  private void genMethodJavaDoc(String defaultValue, Map<Integer,ArgumentChunk> args) {
    composer.beginJavaDocComment();
    String escaped = makeJavaString(defaultValue);
    composer.println("Translated " + escaped + ".\n");
    if (!args.isEmpty()) {
      for (int i = 0; i <= Collections.max(args.keySet()); i++) {
        composer.print("@param arg" + i);
        if (args.containsKey(i)) {
          composer.println(" " + makeJavaString(args.get(i).getAsMessageFormatString()));
        } else {
          composer.println(" optional");
        }
      }
    }
    composer.println("@return translated " + escaped);
    composer.endJavaDocComment();
  }
 
}
