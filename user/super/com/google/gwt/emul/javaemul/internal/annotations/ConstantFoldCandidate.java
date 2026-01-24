/*
 * Copyright 2026 GWT Project Authors
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
package javaemul.internal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * An annotation to mark a method as a candidate for constant folding by the compiler. This method
 * should not have its contents inlined until after the optimization loop has halted, then it can be
 * inlined. During the loop, the compiler may replaces calls to this method if all arguments are
 * literal constants, and if the instance is a constant as well, and if the method has an analogous
 * implementation in the JDK. The method is assumed to be free from side effects.
 * <p>
 * Only Strings and boxed primitives are valid instances for the purposes of this optimization
 * though static methods in other classes may be considered as well if they only take literal
 * parameters at a callsite.
 * <p>
 * It is generally unnecessary to annotate methods that just perform standard math operations,
 * since the compiler can inline and execute those operations on constants already.
 * <p>
 *   TODO what about methods that throw exceptions?
 *   TODO what about enums?
 * <p>
 * Internal SDK use only, might change or disappear at any time.
 */
@CompilerHint
@Target(ElementType.METHOD)
public @interface ConstantFoldCandidate {
}