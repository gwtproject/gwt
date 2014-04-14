/*
 * Copyright 2009 Google Inc.
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
package com.google.gwt.core.ext.linker;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation, when placed on a {@link com.google.gwt.core.ext.Linker}
 * class, indicates that the linker supports the shardable version of the Linker
 * API. Specifically, it implements
 * {@link com.google.gwt.core.ext.Linker#link(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.LinkerContext, ArtifactSet, boolean)}
 * rather than
 * {@link com.google.gwt.core.ext.Linker#link(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.LinkerContext, ArtifactSet)}
 * .
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Shardable {
}
