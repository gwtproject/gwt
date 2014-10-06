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

package com.google.gwt.resources.client.gss;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.Import;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.gss.ScopeResource.SharedParent;

public interface TestResources extends ClientBundle {
  public interface ImageResources extends ClientBundle {
        @Source("someImageResource.png")
        ImageResource someResource();
  }

  interface SomeGssResource extends CssResource {
    String someClass();
  }

  interface SpriteGssResource extends CssResource {
    String someClassWithSprite();

    String embeddedSprite();

    // define a style class having the same name than another resource in the ClientBundle
    // test possible conflict
    String someImageResource();
  }

  interface ExternalClasses extends CssResource {
    String obfuscatedClass();

    String externalClass();

    String externalClass2();

    String unobfuscated();

    String unobfuscated2();
  }

  interface EmptyClass extends CssResource {
    String empty();
  }

  interface WithConstant extends CssResource {
    String constantOne();

    String classOne();
  }

  interface ClassNameAnnotation extends CssResource {
    @ClassName("renamed-class")
    String renamedClass();

    String nonRenamedClass();
  }

  interface TestImportCss extends CssResource {
    String other();
  }

  // used to test shared annotation between clientBundle
  interface SharedChild3 extends SharedParent {
    String nonSharedClassName();
  }

  interface CssWithConstant extends CssResource {
    String constantOne();
    int constantTwo();
    String CONSTANT_THREE();

    String className1();
    String conflictConstantClass();
  }

  interface RuntimeConditional extends CssResource {
    String foo();
  }

  interface NonStandardAtRules extends CssResource {
    String foo();
  }

  interface NonStandardFunctions extends CssResource {
    String foo();
  }

  ClassNameAnnotation classNameAnnotation();

  SomeGssResource mixin();

  SomeGssResource add();

  SomeGssResource eval();

  SomeGssResource resourceUrl();

  SpriteGssResource sprite();

  ExternalClasses externalClasses();

  EmptyClass emptyClass();

  WithConstant withConstant();

  ImageResource someImageResource();

  @Source("bananaguitar.ani")
  DataResource someDataResource();

  @Import({ImportResource.ImportWithPrefixCss.class, ImportResource.ImportCss.class})
  TestImportCss testImportCss();

  SharedChild3 sharedChild3();

  CssWithConstant cssWithConstant();

  @NotStrict
  SomeGssResource notstrict();

  RuntimeConditional runtimeConditional();

  ImageResources embeddedImageResources();

  NonStandardAtRules nonStandardAtRules();

  NonStandardFunctions nonStandardFunctions();
}
