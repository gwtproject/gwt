/*
 * Copyright 2014 Google Inc.
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
package com.google.gwt.dev.jjs.impl;

import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.ast.JBooleanLiteral;
import com.google.gwt.dev.jjs.ast.JCharLiteral;
import com.google.gwt.dev.jjs.ast.JDoubleLiteral;
import com.google.gwt.dev.jjs.ast.JExpression;
import com.google.gwt.dev.jjs.ast.JFloatLiteral;
import com.google.gwt.dev.jjs.ast.JIntLiteral;
import com.google.gwt.dev.jjs.ast.JLiteral;
import com.google.gwt.dev.jjs.ast.JLongLiteral;
import com.google.gwt.dev.jjs.ast.JNullLiteral;
import com.google.gwt.dev.jjs.ast.JStringLiteral;
import com.google.gwt.dev.js.ast.JsBooleanLiteral;
import com.google.gwt.dev.js.ast.JsExpression;
import com.google.gwt.dev.js.ast.JsLiteral;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsNullLiteral;
import com.google.gwt.dev.js.ast.JsNumberLiteral;
import com.google.gwt.dev.js.ast.JsObjectLiteral;
import com.google.gwt.dev.js.ast.JsPropertyInitializer;
import com.google.gwt.dev.js.ast.JsStringLiteral;
import com.google.gwt.lang.LongLib;
import com.google.gwt.thirdparty.guava.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

/**
 * Translates Java literals into JavaScript literals.
 */
public class JjsUtils {

  public static JsLiteral translateLiteral(JLiteral literal) {
    return translatorByLiteralClass.get(literal.getClass()).translate(literal);
  }

  private static Map<Class<? extends JLiteral>, LiteralTranslators> translatorByLiteralClass =
      new ImmutableMap.Builder()
          .put(JBooleanLiteral.class, LiteralTranslators.BOOLEAN_LITERAL_TRANSLATOR)
          .put(JCharLiteral.class, LiteralTranslators.CHAR_LITERAL_TRANSLATOR)
          .put(JFloatLiteral.class, LiteralTranslators.FLOAT_LITERAL_TRANSLATOR)
          .put(JDoubleLiteral.class, LiteralTranslators.DOUBLE_LITERAL_TRANSLATOR)
          .put(JIntLiteral.class, LiteralTranslators.INT_LITERAL_TRANSLATOR)
          .put(JLongLiteral.class, LiteralTranslators.LONG_LITERAL_TRANSLATOR)
          .put(JNullLiteral.class, LiteralTranslators.NULL_LITERAL_TRANSLATOR)
          .put(JStringLiteral.class, LiteralTranslators.STRING_LITERAL_TRANSLATOR)
          .build();

  private enum LiteralTranslators {
    BOOLEAN_LITERAL_TRANSLATOR() {
      @Override
      JsLiteral translate(JExpression literal) {
        return JsBooleanLiteral.get(((JBooleanLiteral) literal).getValue());
      }
    },
    CHAR_LITERAL_TRANSLATOR() {
      @Override
      JsLiteral translate(JExpression literal) {
        return new JsNumberLiteral(literal.getSourceInfo(), ((JCharLiteral) literal).getValue());
      }
    },
    FLOAT_LITERAL_TRANSLATOR() {
      @Override
      JsLiteral translate(JExpression literal) {
        return new JsNumberLiteral(literal.getSourceInfo(), ((JFloatLiteral) literal).getValue());
      }
    },
    DOUBLE_LITERAL_TRANSLATOR() {
      @Override
      JsLiteral translate(JExpression literal) {
        return new JsNumberLiteral(literal.getSourceInfo(), ((JDoubleLiteral) literal).getValue());
      }
    },
    INT_LITERAL_TRANSLATOR() {
      @Override
      JsLiteral translate(JExpression literal) {
        return new JsNumberLiteral(literal.getSourceInfo(), ((JIntLiteral) literal).getValue());
      }
    },
    LONG_LITERAL_TRANSLATOR() {
      @Override
      JsLiteral translate(JExpression literal) {
        SourceInfo sourceInfo = literal.getSourceInfo();
        int[] intArray = LongLib.getAsIntArray(((JLongLiteral) literal).getValue());
        JsObjectLiteral objectLit = new JsObjectLiteral(sourceInfo);
        List<JsPropertyInitializer> inits = objectLit.getPropertyInitializers();

        JsExpression label0 = lName.makeRef(sourceInfo);
        JsExpression label1 = mName.makeRef(sourceInfo);
        JsExpression label2 = hName.makeRef(sourceInfo);
        JsExpression value0 = new JsNumberLiteral(sourceInfo, intArray[0]);
        JsExpression value1 = new JsNumberLiteral(sourceInfo, intArray[1]);
        JsExpression value2 = new JsNumberLiteral(sourceInfo, intArray[2]);
        inits.add(new JsPropertyInitializer(sourceInfo, label0, value0));
        inits.add(new JsPropertyInitializer(sourceInfo, label1, value1));
        inits.add(new JsPropertyInitializer(sourceInfo, label2, value2));
        objectLit.setInternable();
        return objectLit;
      }
    },
    STRING_LITERAL_TRANSLATOR() {
      @Override
      JsLiteral translate(JExpression literal) {
        return new JsStringLiteral(literal.getSourceInfo(), ((JStringLiteral) literal).getValue());
      }
    },
    NULL_LITERAL_TRANSLATOR() {
      @Override
      JsLiteral translate(JExpression literal) {
        return JsNullLiteral.INSTANCE;
      }
    };

    private static final JsName lName;
    private static final JsName hName;
    private static final JsName mName;


    static {
      lName = new JsName(null, "l", "l");
      lName.setObfuscatable(false);
      hName = new JsName(null, "h", "h");
      hName.setObfuscatable(false);
      mName = new JsName(null, "m", "m");
      mName.setObfuscatable(false);
    }

    abstract JsLiteral translate(JExpression literal);
  }

  private JjsUtils() {
  }
}