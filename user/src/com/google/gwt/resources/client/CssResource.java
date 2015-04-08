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
package com.google.gwt.resources.client;

import com.google.gwt.resources.ext.DefaultExtensions;
import com.google.gwt.resources.ext.ResourceGeneratorType;
import com.google.gwt.resources.rg.CssResourceGenerator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Aggregates and minifies CSS stylesheets. A CssResource represents a regular
 * GSS (Google cloure stylesheet) file.
 * <p>
 * Currently-supported accessor functions:
 * 
 * <ul>
 * <li>{@code String someClassName();} will allow the css class
 * <code>.someClassName</code> to be obfuscated at runtime. The function will
 * return the obfuscated class name.</li>
 * <li>{@code <primitive numeric type or String> someDefName();} will allow
 * access to the values defined by {@literal @def} rules within the CSS file.
 * The defined value must be a raw number, a CSS length, or a percentage value
 * if it is to be returned as a numeric type.
 * </ul>
 *
 * <p>
 * Any class selectors that do not correspond with a String accessor method in
 * the return type will trigger a compilation error. This ensures that the
 * CssResource does not contribute any unobfuscated class selectors into the
 * global CSS namespace. Strict mode can be disabled by annotating the
 * ClientBundle method declaration with {@link NotStrict}, however this is only
 * recommended for interacting with legacy CSS.
 * 
 * <p>
 * Given these interfaces:
 * 
 * <pre>
 * interface MyCss extends CssResource {
 *   String someClass();
 * }
 * 
 * interface MyBundle extends ClientBundle {
 *  {@literal @Source("my.gss")}
 *   MyCss css();
 * }
 * </pre>
 * 
 * the source CSS will fail to compile if it does not contain exactly the one
 * class selector defined in the MyCss type.
 *
 * <p>
 * The {@code @external} at-rule can be used in strict mode to indicate that
 * certain class selectors are exempt from the strict semantics. Class selectors
 * marked as external will not be obfuscated and are not required to have string
 * accessor functions. Consider the following example in conjunction with the
 * above <code>MyCss</code> interface:
 * 
 * <pre>
   * {@literal @external} foo, bar;
   * .foo .someClass .bar { .... }
   * </pre>
 * 
 * The resulting CSS would look like:
 * 
 * <pre>
   * .foo .A1234 .bar { .... }
   * </pre>
 * 
 * If a <code>String foo()</code> method were defined in <code>MyCss</code>, it
 * would return the string value "<code>foo</code>".
 * <p>
 * The utility tool <code>com.google.gwt.resources.css.InterfaceGenerator</code>
 * can be used to automatically generate a Java interface from a GSS file.
 *
 * <p>
 * For more information about GSS syntax please refer to
 * <a href="http://www.gwtproject.org/doc/latest/DevGuideGssVsCss.html"> the documentation</a>
 *
 */
@DefaultExtensions(value = {".css", ".gss"})
@ResourceGeneratorType(CssResourceGenerator.class)
public interface CssResource extends CssResourceBase {
  /**
   * The original CSS class name specified in the resource. This allows CSS
   * classes that do not correspond to Java identifiers to be mapped onto
   * obfuscated class accessors.
   * 
   * <pre>
   * .some-non-java-ident { background: blue; }
   * 
   * interface MyCssResource extends CssResource {
   *   {@literal @}ClassName("some-non-java-ident")
   *   String classAccessor();
   * }
   * </pre>
   */
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface ClassName {
    String value();
  }

  /**
   * Makes class selectors from other CssResource types available in the raw
   * source of a CssResource. String accessor methods can be referred to using
   * the value of the imported type's {@link ImportedWithPrefix} value.
   * <p>
   * This is an example of creating a descendant selector with two unrelated
   * types:
   * 
   * <pre>
   *{@literal @ImportedWithPrefix}("some-prefix")
   * interface ToImport extends CssResource {
   *   String widget();
   * }
   * 
   *{@literal @ImportedWithPrefix}("other-import")
   * interface OtherImport extends CssResource {
   *   String widget();
   * }
   * 
   * interface Resources extends ClientBundle {
   *  {@literal @Import}(value = {ToImport.class, OtherImport.class})
   *  {@literal @Source}("my.gss")
   *   CssResource usesImports();
   * }
   * 
   * my.gss:
   * // Now I can refer to these classes defined elsewhere with no 
   * // fear of name collisions
   * .some-prefix-widget .other-import-widget {...}
   * </pre>
   * 
   * If the imported CssResource type is lacking an {@link ImportedWithPrefix}
   * annotation, the simple name of the type will be used instead. In the above
   * example, without the annotation on <code>ToImport</code>, the class
   * selector would have been <code>.ToImport-widget</code>. Notice also that
   * both interfaces defined a method called <code>widget()</code>, which would
   * prevent meaningful composition of the original interfaces.
   * <p>
   * It is an error to import multiple classes with the same prefix into one
   * CssResource.
   */
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface Import {
    Class<? extends CssResource>[] value();
  }

  /**
   * Specifies the string prefix to use when one CssResource is imported into
   * the scope of another CssResource.
   * 
   * @see Import
   */
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface ImportedWithPrefix {
    String value();
  }

  /**
   * The presence of this annotation on a CssResource accessor method indicates
   * that any class selectors that do not correspond with a String accessor
   * method in the return type or an {@code @external} declaration should not
   * trigger a compilation error. This annotation is not recommended for new
   * code.
   * 
   * <pre>
   * interface Resources extends ClientBundle {
   *  {@literal @NotStrict}
   *  {@literal @Source}("legacy.gss")
   *   CssResource css();
   * }
   * </pre>
   */
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface NotStrict {
  }

  /**
   * Indicates that the String accessor methods defined in a CssResource will
   * return the same values across all implementations of that type.
   * <p>
   * This is an example of "stateful" class selectors being used:
   * 
   * <pre>
   *{@literal @Shared}
   * interface FocusCss extends CssResource {
   *   String focused();
   *   String unfocused();
   * }
   * 
   * interface PanelCss extends CssResource, FocusCss {
   *   String widget();
   * }
   * 
   * interface InputCss extends CssResource, FocusCss {
   *   String widget();
   * }
   * 
   * input.gss:
   * *.focused .widget {border: thin solid blue;}
   * 
   * Application.java:
   * myPanel.add(myInputWidget);
   * myPanel.addStyleName(instanceOfPanelCss.focused());
   * </pre>
   * 
   * Because the <code>FocusCss</code> interface is tagged with {@code @Shared},
   * the <code>focused()</code> method on the instance of <code>PanelCss</code>
   * will match the <code>.focused</code> parent selector in
   * <code>input.gss</code>.
   * <p>
   * The effect of inheriting an {@code Shared} interface can be replicated by
   * use use of the {@link Import} annotation (e.g. {@code .FocusCss-focused
   * .widget}), however the use of state-bearing descendant selectors is common
   * enough to warrant an easier use-case.
   */
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface Shared {
  }

  /**
   * This annotation is a no-op.
   * 
   * @deprecated Strict mode is now the default behavior for CssResource
   */
  @Deprecated
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface Strict {
  }

  /**
   * Calls
   * {@link com.google.gwt.dom.client.StyleInjector#injectStylesheet(String)} to
   * inject the contents of the CssResource into the DOM. Repeated calls to this
   * method on an instance of a CssResources will have no effect.
   * 
   * @return <code>true</code> if this method mutated the DOM.
   */
  boolean ensureInjected();

  /**
   * Provides the contents of the CssResource.
   */
  String getText();
}
